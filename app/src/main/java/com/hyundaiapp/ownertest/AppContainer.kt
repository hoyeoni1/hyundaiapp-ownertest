package com.hyundaiapp.ownertest

import android.app.Application
import android.content.Context
import android.util.Log
import com.hyundaiapp.ownertest.game.GameConfig
import com.hyundaiapp.ownertest.game.GameEngine
import com.hyundaiapp.ownertest.game.GameSnapshot
import com.hyundaiapp.ownertest.signals.CarSignals
import com.hyundaiapp.ownertest.signals.PleosCarSignals
import com.hyundaiapp.ownertest.voice.CarVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** 객체 모음(수동 DI). 신호 → 엔진 연결과 250ms 틱을 여기서 돈다 */
class AppContainer(context: Context) {
    val signals: CarSignals = PleosCarSignals(context)
    private val _engine = MutableStateFlow(GameEngine())
    /** 지금 엔진. [restart]로 바뀐다(시연용 짧은 타이머 등) */
    val engine: StateFlow<GameEngine> = _engine
    val voice: CarVoice by lazy { CarVoice(context) }
    /** 화면 위 디버그 패널(adb `--es action debug`로 토글) */
    val debugVisible = MutableStateFlow(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    fun start() {
        if (job != null) return
        signals.start()
        voice // 결말 전에 준비돼 있도록 미리 연결한다
        job = scope.launch {
            launch { signals.state.collect { engine.value.onCarState(it, System.currentTimeMillis()) } }
            launch { while (isActive) { engine.value.tick(System.currentTimeMillis()); delay(TICK_MS) } }
            launch { _engine.collectLatest { e -> var last: String? = null; e.snapshot.collect { s -> val line = summary(s); if (line != last) { last = line; Log.i(TAG, line) } } } }
        }
    }

    /** 처음부터. [config]로 시연용 짧은 타이머를 줄 수 있다 */
    fun restart(config: GameConfig = GameConfig()) {
        _engine.value = GameEngine(config = config)
        engine.value.onCarState(signals.state.value, System.currentTimeMillis())
    }

    fun stop() {
        job?.cancel(); job = null
        signals.stop()
    }

    private fun summary(s: GameSnapshot): String =
        "phase=${s.phase} view=${s.view} thermal=${s.thermalRevealed}/${s.thermalSeen} glow=${s.glowVisible}/${s.glowSeen} " +
            "pillar=${s.pillarVisible}/${s.pillarSeen} monster=${s.monster.active} hint=${s.monster.hint} repelled=${s.repelledCount} " +
            "signal=${s.signal.active} presses=${s.signal.presses} reply=${s.signal.lastReply} eligible=${s.signalEligible} " +
            "retry=${s.retryCount} invHint=${s.investigationHint}"

    companion object {
        const val TAG = "OwnerTestGame"
        private const val TICK_MS = 250L
        /** 시연·실측용: 괴물 15초, 신호 10초 */
        val FAST = GameConfig(
            firstMonsterMs = 15_000, monsterJitterMs = 5_000, monsterRepeatMinMs = 20_000, monsterRepeatMaxMs = 30_000,
            signalFirstMinMs = 10_000, signalFirstMaxMs = 15_000, signalRetryMinMs = 10_000, signalRetryMaxMs = 15_000,
        )
        /** 화면 촬영용: 조사할 시간을 주고(괴물 70초), 괴물은 천천히(60초), 신호는 빨리 */
        val SHOTS = GameConfig(
            firstMonsterMs = 70_000, monsterJitterMs = 0, monsterTimeoutMs = 60_000, monsterRepeatMinMs = 60_000, monsterRepeatMaxMs = 80_000,
            signalFirstMinMs = 8_000, signalFirstMaxMs = 10_000, signalRetryMinMs = 8_000, signalRetryMaxMs = 10_000,
        )
    }
}

class OwnerTestApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

val Context.container: AppContainer get() = (applicationContext as OwnerTestApp).container
