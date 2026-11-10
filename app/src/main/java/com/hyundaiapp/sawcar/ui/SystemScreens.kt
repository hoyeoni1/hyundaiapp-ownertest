package com.hyundaiapp.sawcar.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundaiapp.sawcar.game.GameSnapshot
import com.hyundaiapp.sawcar.signals.SourceStatus
import kotlinx.coroutines.delay

private val Ink = Color(0xFFD8D2C4)
private val Dim = Color(0xFF8A857A)

/** S1. 문을 잠그면 시작한다 */
@Composable
fun TitleScreen(status: SourceStatus) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) { ImageCache.prefetch(context, listOf(Img.TITLE) + Img.FRONT_ALL) }
    Box(Modifier.fillMaxSize().background(Color(0xFF050607))) {
        CoverImage(Img.TITLE)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
        Column(Modifier.fillMaxSize().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("문을 잠그면 시작합니다.", color = Ink, fontSize = 44.sp, style = Shadowed)
            Spacer(Modifier.height(28.dp))
            Text("앱은 문을 잠그지 않습니다. 언제든 나갈 수 있습니다.", color = Dim, fontSize = 20.sp, style = Shadowed)
            Spacer(Modifier.height(36.dp))
            Text(
                when (status) { SourceStatus.READY -> "차량 연결됨"; SourceStatus.FAILED -> "차량 연결 실패 — 앱을 다시 열어 주세요"; else -> "차량 연결 중…" },
                color = if (status == SourceStatus.READY) Color(0xFF6E8F6E) else Color(0xFF9A7A50), fontSize = 16.sp, style = Shadowed,
            )
        }
    }
}

private val Shadowed = androidx.compose.ui.text.TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 14f))

/** 기어가 P를 벗어났다 — 이야기 밖의 회색 화면 */
@Composable
fun PausedGearScreen() {
    Column(Modifier.fillMaxSize().background(Color(0xFF3A3D40)).padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("차가 움직이면 훈련은 멈춥니다.", color = Color(0xFFE8E8E8), fontSize = 34.sp)
        Spacer(Modifier.height(12.dp))
        Text("P로 두면 이어집니다.", color = Color(0xFFBDBDBD), fontSize = 22.sp)
    }
}

/** 괴물에게 25초 안에 붉은빛을 못 보여 줬다 */
@Composable
fun GameOverScreen(onRetry: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(1200); step = 1; delay(2500); step = 2 }
    val red by animateFloatAsState(if (step == 0) 0.85f else 0f, tween(1200), label = "red")
    Column(
        Modifier.fillMaxSize().background(Color(0xFF000000)).background(Color(0xFF7A0E0E).copy(alpha = red)).padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        when (step) {
            0 -> Unit
            1 -> Text("…", color = Ink, fontSize = 48.sp)
            else -> {
                Text("실패", color = Ink, fontSize = 44.sp)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2F33), contentColor = Ink)) { Text("다시", fontSize = 22.sp) }
            }
        }
    }
}

/** 결말 전에 문을 열었다 */
@Composable
fun EarlyExitScreen(doorOpen: Boolean, onReboard: () -> Unit, onQuit: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF07080A)).padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("아직이야.", color = Ink, fontSize = 40.sp)
        Spacer(Modifier.height(36.dp))
        Row {
            Button(onClick = onReboard, enabled = !doorOpen, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2F33), contentColor = Ink)) {
                Text(if (doorOpen) "문을 닫으면 다시 탈 수 있다" else "다시 타기", fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = onQuit, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1D20), contentColor = Dim)) { Text("끝내기", fontSize = 20.sp) }
        }
    }
}

/** 성공 */
@Composable
fun ResultScreen(snap: GameSnapshot, onRestart: () -> Unit) {
    val minutes = snap.gameTimeMs / 60_000
    val seconds = (snap.gameTimeMs / 1_000) % 60
    Box(Modifier.fillMaxSize().background(Color(0xFF0B0E12))) {
        CoverImage(Img.END_DOOR)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        Column(Modifier.fillMaxSize().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("성공", color = Ink, fontSize = 44.sp, style = Shadowed)
            Spacer(Modifier.height(20.dp))
            Text("${minutes}분 ${seconds}초 · 실패 ${snap.retryCount}번 · 힌트 ${snap.hintCount}번", color = Dim, fontSize = 20.sp, style = Shadowed)
            Spacer(Modifier.height(28.dp))
            listOf("실내등", "핸들 열선", "앞유리 제상", "전조등", "브레이크", "방향지시등")
                .forEach { Text(it, color = Ink, fontSize = 20.sp, textAlign = TextAlign.Center, style = Shadowed, modifier = Modifier.padding(vertical = 3.dp)) }
            Spacer(Modifier.height(28.dp))
            Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2F33), contentColor = Ink)) { Text("처음부터", fontSize = 20.sp) }
        }
    }
}
