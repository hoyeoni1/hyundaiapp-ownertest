package com.hyundaiapp.sawcar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hyundaiapp.sawcar.game.View
import com.hyundaiapp.sawcar.ui.GameScreen

/**
 * adb로 조작(시연·실측 스크립트용):
 *   am broadcast -a com.hyundaiapp.sawcar.DEBUG --es view STEERING
 *   am broadcast -a com.hyundaiapp.sawcar.DEBUG --es action retry|reboard|restart|fast|speak|debug
 */
class MainActivity : ComponentActivity() {
    private val debugReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val c = context.container
            val now = System.currentTimeMillis()
            intent.getStringExtra("view")?.let { name ->
                View.entries.firstOrNull { it.name == name }?.let { c.engine.value.onView(it, now) }
            }
            when (intent.getStringExtra("action")) {
                "retry" -> c.engine.value.retry(now)
                "reboard" -> c.engine.value.reboard(now)
                "restart" -> c.restart()
                "fast" -> c.restart(AppContainer.FAST)
                "shots" -> c.restart(AppContainer.SHOTS)
                "speak" -> c.voice.speak(intent.getStringExtra("text") ?: "문은 내가 잠갔어.")
                "debug" -> c.debugVisible.value = !c.debugVisible.value
            }
            Log.i(AppContainer.TAG, "debug intent ${intent.extras?.keySet()?.joinToString { "$it=${intent.extras?.get(it)}" }}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container.start()
        ContextCompat.registerReceiver(this, debugReceiver, IntentFilter(DEBUG_ACTION), ContextCompat.RECEIVER_EXPORTED)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) { GameScreen(container) }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(debugReceiver)
        super.onDestroy()
    }

    private companion object { const val DEBUG_ACTION = "com.hyundaiapp.sawcar.DEBUG" }
}
