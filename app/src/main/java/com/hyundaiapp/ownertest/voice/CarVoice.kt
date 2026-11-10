package com.hyundaiapp.ownertest.voice

import ai.pleos.playground.tts.TextToSpeech
import ai.pleos.playground.tts.constant.ErrorType
import ai.pleos.playground.tts.constant.Mode
import ai.pleos.playground.tts.listener.EventListener
import ai.pleos.playground.tts.listener.OnInitializeListener
import android.content.Context
import android.media.AudioAttributes
import android.util.Log
import java.util.Locale

/**
 * 결말·조기 탈출의 차 목소리. Pleos TTS SDK 기기 내 모드.
 * 크루즈브레이커 RiskSpeaker에서 확인한 것: Locale은 `Locale.KOREA`(ko_KR)여야 하고, 해제된 객체에 speak하면 실제로 읽으므로
 * 해제 뒤에는 SDK를 부르지 않는다.
 */
class CarVoice(context: Context) {
    private val lock = Any()
    @Volatile private var ready = false
    @Volatile private var released = false
    /** 준비 전에 들어온 문장. 준비되면 바로 읽는다(11-09: 첫 speak가 onSuccess보다 3ms 빨라 건너뛰었음) */
    private var pending: String? = null
    private val tts = TextToSpeech(context.applicationContext, Mode.ON_DEVICE)

    init {
        tts.initialize(object : OnInitializeListener {
            override fun onSuccess() {
                synchronized(lock) {
                    if (released) return
                    ready = true
                    tts.setLocale(Locale.KOREA)
                    tts.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    )
                    Log.i(TAG, "tts ready (sdk ${TextToSpeech.version})")
                    pending?.let { tts.speak(it); Log.i(TAG, "speak (queued) $it") }
                    pending = null
                }
            }
            override fun onFailure(error: ErrorType) { Log.w(TAG, "tts init failed: $error") }
        })
        tts.addEventListener(object : EventListener {
            override fun onStart() { Log.i(TAG, "tts start") }
            override fun onReady() = Unit
            override fun onDone() { Log.i(TAG, "tts done") }
            override fun onError(error: ErrorType) { Log.w(TAG, "tts error: $error") }
            override fun onStop() = Unit
            override fun onUpdatedRms(rms: Double) = Unit
        })
    }

    /** 읽었으면 true. 준비 전이면 준비되는 대로 읽는다(마지막 문장만). 해제 뒤면 false(자막만) */
    fun speak(text: String): Boolean = synchronized(lock) {
        if (released) { Log.i(TAG, "skip speech (released)"); return false }
        if (!ready) { pending = text; Log.i(TAG, "queue speech $text"); return false }
        tts.speak(text)
        Log.i(TAG, "speak $text")
        true
    }

    fun release() = synchronized(lock) {
        if (released) return
        released = true
        ready = false
        tts.release()
    }

    private companion object { const val TAG = "OwnerTestVoice" }
}
