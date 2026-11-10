package com.hyundaiapp.ownertest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundaiapp.ownertest.AppContainer
import com.hyundaiapp.ownertest.game.GameSnapshot
import com.hyundaiapp.ownertest.game.Phase
import com.hyundaiapp.ownertest.signals.SourceStatus
import kotlinx.coroutines.delay

/** 결말의 첫 목소리 — 핵심만. (문장, 다음 문장까지 ms) */
val ENDING_LINES = listOf(
    "문은 내가 잠갔어." to 3500L,
    "오늘 밤은 연습이었어. 밖은 진짜야." to 4500L,
    "살아남아." to 3000L,
    "문 열어도 돼." to 0L,
)

/** 단계별 화면 + 결말 내레이션 + 디버그 오버레이 */
@Composable
fun GameScreen(container: AppContainer) {
    val engine by container.engine.collectAsState()
    val snap by engine.snapshot.collectAsState()
    val status by container.signals.status.collectAsState()
    val error by container.signals.lastError.collectAsState()
    val debug by container.debugVisible.collectAsState()

    var subtitle by remember { mutableStateOf<String?>(null) }
    var dawn by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(snap.phase, engine) {
        when (snap.phase) {
            Phase.CLEARED -> {
                delay(5000) // 손전등이 꺼지고 잠시
                ENDING_LINES.forEachIndexed { i, (line, wait) ->
                    subtitle = line
                    container.voice.speak(line)
                    dawn = (i + 1).toFloat() / ENDING_LINES.size
                    if (wait > 0) delay(wait)
                }
            }
            Phase.EARLY_EXIT -> container.voice.speak("아직이야.")
            Phase.ENDED -> { subtitle = null; container.voice.speak("행운을.") }
            Phase.TITLE -> { subtitle = null; dawn = 0f }
            else -> subtitle = null
        }
    }

    val now = { System.currentTimeMillis() }
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        when (snap.phase) {
            Phase.TITLE -> TitleScreen(status)
            Phase.PLAYING, Phase.CLEARED -> SceneScreen(snap, dawn, subtitle, onView = { engine.onView(it, now()) })
            Phase.PAUSED_GEAR -> PausedGearScreen()
            Phase.GAME_OVER -> GameOverScreen(onRetry = { engine.retry(now()) })
            Phase.EARLY_EXIT -> EarlyExitScreen(snap.car.anyDoorOpen, onReboard = { engine.reboard(now()) }, onQuit = { container.restart() })
            Phase.ENDED -> ResultScreen(snap, onRestart = { container.restart() })
        }
        if (debug) DebugOverlay(snap, status, error)
    }
}

@Composable
private fun DebugOverlay(s: GameSnapshot, status: SourceStatus, error: String?) {
    val car = s.car
    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.TopStart) {
        Column(Modifier.background(Color.Black.copy(alpha = 0.6f)).padding(8.dp)) {
            listOf(
                "소스 $status ${error ?: ""}",
                "단계 ${s.phase} t=${s.gameTimeMs / 1000}s 시점 ${s.view}",
                "실내등 ${car.cabinLightOn} 하향 ${car.lowBeamOn} 상향 ${car.highBeamOn} 제상 ${car.defrostFrontOn} 열선 ${car.steeringHeat}",
                "깜빡이 ${car.turnSignal} 브레이크 ${car.brakePressed} 잠금 ${car.doorsLocked} 문 ${car.doorOpen.filterValues { it }.keys} 기어 ${car.gear}",
                "열감응 ${"%.0f".format(s.thermalRevealProgress * 100)}%/${s.thermalSeen} 야광 ${s.glowChargeMs / 1000}s/${s.glowVisible}/${s.glowSeen} 김 ${"%.0f".format(s.fogClearProgress * 100)}% 기둥 ${s.pillarVisible}/${s.pillarSeen}",
                "괴물 ${if (s.monster.active) "${s.monster.elapsedMs / 1000}s 브 ${s.monster.brakeHeldMs / 1000}s ${s.monster.hint}" else "-"} 격퇴 ${s.repelledCount}  신호 ${if (s.signal.active) "${s.signal.elapsedMs / 1000}s ${s.signal.presses}" else "-"} 자격 ${s.signalEligible} 응답 ${s.signal.lastReply}",
                "다시 ${s.retryCount} 힌트 ${s.hintCount} ${s.investigationHint ?: ""}",
            ).forEach { Text(it, color = Color(0xFFB0FFB0), fontSize = 12.sp) }
        }
    }
}
