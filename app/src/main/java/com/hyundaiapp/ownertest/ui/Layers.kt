package com.hyundaiapp.ownertest.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundaiapp.ownertest.game.FlashReply
import com.hyundaiapp.ownertest.game.MonsterHint
import com.hyundaiapp.ownertest.signals.TurnSignal

/** 매 프레임 갱신되는 시계(깜빡임·펄스용). 화면에 떠 있는 동안만 돈다 */
@Composable
fun rememberFrameClock(): State<Long> {
    val t = remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { withFrameMillis { t.longValue = System.currentTimeMillis() } } }
    return t
}

// ---------- 괴물 ----------

/** 접근 정도 0(없음)~1(코앞). 브레이크를 밟는 동안 물러난다 */
fun monsterApproach(active: Boolean, elapsedMs: Long, brakeHeldMs: Long): Float =
    if (!active) 0f else (elapsedMs / 25_000f - (brakeHeldMs / 8_000f) * 0.85f).coerceIn(0.05f, 1f)

/** 정면의 작은 룸미러 안 그림자 + 화면 가장자리 어두워짐 (룸미러를 보라는 신호) */
@Composable
fun MonsterHintInFront(mirror: Rect, approach: Float, modifier: Modifier = Modifier) {
    if (approach <= 0f) return
    Canvas(modifier.fillMaxSize()) {
        val r = Rect(mirror.left * size.width, mirror.top * size.height, mirror.right * size.width, mirror.bottom * size.height)
        val h = r.height * (0.35f + 0.6f * approach)
        drawRoundRect(Color(0xFF05070A), Offset(r.center.x - h * 0.17f, r.bottom - h * 1.05f), Size(h * 0.34f, h), CornerRadius(h * 0.1f))
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f * approach)), center = center, radius = size.maxDimension * 0.7f))
    }
}

// ---------- 왼쪽 미러: 손전등 ----------

/** 신호가 오는 동안 10초마다 세 번 깜빡. 응답 뒤엔 길게 한 번(맞음) / 짧게 두 번(아님) */
fun flashlightOn(signalActive: Boolean, elapsedMs: Long, reply: FlashReply, replyAgeMs: Long): Boolean {
    if (replyAgeMs in 0..3000) return when (reply) {
        FlashReply.LONG_OK -> replyAgeMs < 2500
        FlashReply.TWO_SHORT_NO -> replyAgeMs < 220 || replyAgeMs in 450..670
        FlashReply.NONE -> false
    }
    if (!signalActive) return false
    val c = elapsedMs % 10_000
    return c < 300 || c in 600..900 || c in 1200..1500
}

/** 어느 시점에 있든 신호가 올 때 왼쪽 가장자리가 두 번 희게 */
@Composable
fun EdgeFlickerLayer(signalActive: Boolean, elapsedMs: Long, modifier: Modifier = Modifier) {
    val c = elapsedMs % 10_000
    val on = signalActive && (c < 150 || c in 300..450)
    if (!on) return
    Canvas(modifier.fillMaxSize()) {
        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.55f), Color.Transparent), startX = 0f, endX = size.width * 0.18f))
    }
}

// ---------- 레버·계기판 ----------

/** 방향지시등 표시(계기판 자리). `left`/`right`는 화면 비율 좌표 */
@Composable
fun TurnArrowsLayer(left: Offset, right: Offset, signal: TurnSignal?, modifier: Modifier = Modifier) {
    if (signal == null || signal == TurnSignal.OFF) return
    val clock by rememberFrameClock()
    val blink = (clock % 800) < 400
    Canvas(modifier.fillMaxSize()) {
        val s = size.width * 0.011f
        fun arrow(c: Offset, toLeft: Boolean, lit: Boolean) {
            if (!lit) return
            val p = Offset(c.x * size.width, c.y * size.height)
            val path = Path().apply {
                if (toLeft) { moveTo(p.x - s, p.y); lineTo(p.x + s, p.y - s); lineTo(p.x + s, p.y + s) } else { moveTo(p.x + s, p.y); lineTo(p.x - s, p.y - s); lineTo(p.x - s, p.y + s) }
                close()
            }
            drawCircle(Brush.radialGradient(listOf(Color(0xFF55FF66).copy(alpha = 0.35f), Color.Transparent), center = p, radius = s * 4), s * 4, p)
            drawPath(path, Color(0xFF55FF66))
        }
        arrow(left, true, signal == TurnSignal.LEFT && blink)
        arrow(right, false, signal == TurnSignal.RIGHT && blink)
    }
}

/** 신호가 오래 안 풀릴 때 왼쪽 레버가 은은히 빛난다. `at`는 화면 비율 좌표 */
@Composable
fun StalkGlow(at: Offset, modifier: Modifier = Modifier) {
    val clock by rememberFrameClock()
    val pulse = 0.55f + 0.45f * ((clock % 1600) / 1600f).let { if (it < 0.5f) it * 2 else (1 - it) * 2 }
    Canvas(modifier.fillMaxSize()) {
        val p = Offset(at.x * size.width, at.y * size.height)
        val r = size.width * 0.07f
        drawCircle(Brush.radialGradient(listOf(Color(0xFF9DFF8A).copy(alpha = 0.35f * pulse), Color.Transparent), center = p, radius = r), r, p)
    }
}

// ---------- 글: 메모·힌트·자막 ----------

/** 정면 구석 메모: 읽은 기둥 글씨(괴물 힌트 때 깜빡), 두 번 놓치면 ◀ ◀ ▶ */
@Composable
fun MemoCorner(pillarSeen: Boolean, monsterHint: MonsterHint, showArrows: Boolean, modifier: Modifier = Modifier) {
    val clock by rememberFrameClock()
    val flash = monsterHint != MonsterHint.NONE && (clock % 1000) < 500
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopEnd) {
        Column(horizontalAlignment = Alignment.End) {
            if (pillarSeen) Text("그것은 빨간 불빛을 무서워해", color = if (flash) Color(0xFFFF6A5E) else Color(0xFFBFB8A8).copy(alpha = 0.7f), fontSize = 20.sp,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 10f)))
            if (monsterHint == MonsterHint.RED_IS_BEHIND) Text("차에서 빨간 불이 나는 곳은 뒤다.", color = Color(0xFFFF6A5E), fontSize = 20.sp,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 10f)))
            if (showArrows) Text("◀ ◀ ▶", color = Color(0xFF9DFF8A).copy(alpha = 0.8f), fontSize = 24.sp)
        }
    }
}

/** 아래 가운데 한 줄(조사 힌트·사물 설명·자막) */
@Composable
fun BottomLine(text: String?, modifier: Modifier = Modifier, color: Color = Color(0xFFD8D2C4)) {
    if (text == null) return
    Box(modifier.fillMaxSize().padding(bottom = 36.dp), contentAlignment = Alignment.BottomCenter) {
        Text(text, color = color, fontSize = 27.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
            style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 12f)))
    }
}
