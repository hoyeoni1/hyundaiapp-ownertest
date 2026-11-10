package com.hyundaiapp.ownertest.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundaiapp.ownertest.game.FlashReply
import com.hyundaiapp.ownertest.game.GameSnapshot
import com.hyundaiapp.ownertest.game.View
import kotlinx.coroutines.delay

/** 시점의 상위(뒤로 가기) */
fun View.parent(): View = when (this) {
    View.FRONT -> View.FRONT
    View.LEFT, View.RIGHT, View.STEERING, View.REARVIEW -> View.FRONT
    View.LEFT_MIRROR -> View.LEFT
    View.RIGHT_MIRROR -> View.RIGHT
}

/**
 * 1인칭 장면. 엔진 상태에 맞는 생성 이미지들을 [Frames]가 고르고 [ImageStack]이 겹쳐 그린다(상태가 바뀌면 크로스페이드).
 * 탭: 정면 양끝 → 좌/우, 핸들 → 핸들 확대, 룸미러 → 룸미러 확대, 좌/우의 사이드미러 → 미러 확대, 확대 화면 아무 곳 → 이전 시점.
 */
@Composable
fun SceneScreen(snap: GameSnapshot, dawnProgress: Float, subtitle: String?, onView: (View) -> Unit) {
    val view = snap.view
    val context = LocalContext.current
    val clock by rememberFrameClock()

    var caption by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(caption) { if (caption != null) { delay(3000); caption = null } }

    // 손전등 응답 연출 시각
    var replyAt by remember { mutableLongStateOf(0L) }
    LaunchedEffect(snap.signal.lastReply) { if (snap.signal.lastReply != FlashReply.NONE) replyAt = System.currentTimeMillis() }

    // 야광이 켜진 지 얼마나 됐나(흐려지는 연출)
    var glowSince by remember { mutableLongStateOf(0L) }
    LaunchedEffect(snap.glowVisible) { glowSince = if (snap.glowVisible) System.currentTimeMillis() else 0L }
    val glowAge = if (glowSince == 0L) 0L else clock - glowSince

    LaunchedEffect(view) { ImageCache.prefetch(context, Frames.prefetchFor(view)) }

    BackHandler(enabled = view != View.FRONT) { onView(view.parent()) }

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black)) {
        val canvas = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val map = Mapping(coverSrcRect(IMG_SIZE, canvas), IMG_SIZE)
        val approach = monsterApproach(snap.monster.active, snap.monster.elapsedMs, snap.monster.brakeHeldMs)
        val lit = snap.car.cabinLightOn == true
        val flashOn = flashlightOn(snap.signal.active, snap.signal.elapsedMs, snap.signal.lastReply, if (replyAt == 0L) -1 else clock - replyAt)

        val layers = when (view) {
            View.FRONT -> Frames.front(snap, dawnProgress)
            View.LEFT -> Frames.side(left = true, lit = lit)
            View.RIGHT -> Frames.side(left = false, lit = lit)
            View.STEERING -> Frames.steering(snap, glowAge)
            View.REARVIEW -> Frames.rearview(snap)
            View.LEFT_MIRROR -> Frames.leftMirror(flashOn)
            View.RIGHT_MIRROR -> Frames.rightMirror()
        }
        ImageStack(layers, fadeOutMs = if (view == View.LEFT_MIRROR) 60 else 450)

        fun pt(o: Offset) = Offset(map.x(o.x), map.y(o.y))
        when (view) {
            View.FRONT -> {
                TurnArrowsLayer(pt(Spots.frontTurnLeft), pt(Spots.frontTurnRight), snap.car.turnSignal)
                MonsterHintInFront(map.rect(Spots.frontMirror), approach)
                EdgeArrow(left = true); EdgeArrow(left = false)
            }
            View.LEFT -> EdgeArrow(left = false)
            View.RIGHT -> EdgeArrow(left = true)
            View.STEERING -> {
                TurnArrowsLayer(pt(Spots.steerTurnLeft), pt(Spots.steerTurnRight), snap.car.turnSignal)
                if (snap.signal.leverGlow) StalkGlow(pt(Spots.leftStalk))
                BackHint()
            }
            View.REARVIEW, View.LEFT_MIRROR, View.RIGHT_MIRROR -> BackHint()
        }

        EdgeFlickerLayer(snap.signal.active, snap.signal.elapsedMs)
        MemoCorner(snap.pillarSeen, snap.monster.hint, snap.signal.showArrowHint)
        BottomLine(subtitle ?: caption ?: snap.investigationHint, color = if (subtitle != null) Color(0xFFEDE6D6) else Color(0xFFD8D2C4))

        // 탭
        Box(Modifier.fillMaxSize().pointerInput(view, snap.steeringReadable, snap.thermalRevealed, snap.fogClearProgress, snap.car.headlightOn, snap.glowVisible) {
            detectTapGestures { pos ->
                val fx = pos.x / size.width
                val fy = pos.y / size.height
                val sx = map.srcX(fx)
                val sy = map.srcY(fy)
                fun inside(r: Rect) = sx in r.left..r.right && sy in r.top..r.bottom
                when (view) {
                    View.FRONT -> when {
                        fx < 0.08f -> onView(View.LEFT)
                        fx > 0.92f -> onView(View.RIGHT)
                        inside(Spots.frontSteering) -> onView(View.STEERING)
                        inside(Spots.frontMirror) -> onView(View.REARVIEW)
                        inside(Spots.frontWindshield) -> caption = when {
                            snap.fogClearProgress < 1f && !snap.car.headlightOn -> "뿌옇다. 밖이 안 보인다."
                            snap.fogClearProgress < 1f -> "빛이 김에 번진다."
                            !snap.car.headlightOn -> "걷혔지만 어둡다."
                            else -> null
                        }
                    }
                    View.LEFT -> when {
                        fx > 0.92f -> onView(View.FRONT)
                        inside(Spots.leftMirror) -> onView(View.LEFT_MIRROR)
                    }
                    View.RIGHT -> when {
                        fx < 0.08f -> onView(View.FRONT)
                        inside(Spots.rightMirror) -> onView(View.RIGHT_MIRROR)
                    }
                    View.STEERING -> when {
                        inside(Spots.thermalPaper) -> caption = when {
                            !snap.steeringReadable -> "어두워서 보이지 않는다."
                            !snap.thermalRevealed -> "감열지. 구석에 열 표시 하나. 글씨는 없다."
                            else -> null
                        }
                        inside(Spots.glowNote) -> caption = when {
                            !snap.steeringReadable -> if (snap.glowVisible) null else "어두워서 보이지 않는다."
                            else -> "태양은 진하고 달은 흐리다. 아래는 비어 있다."
                        }
                        else -> onView(View.FRONT)
                    }
                    View.REARVIEW, View.LEFT_MIRROR, View.RIGHT_MIRROR -> onView(view.parent())
                }
            }
        })
    }
}

@Composable
private fun EdgeArrow(left: Boolean) {
    Box(Modifier.fillMaxSize().padding(horizontal = 18.dp), contentAlignment = if (left) Alignment.CenterStart else Alignment.CenterEnd) {
        Text(if (left) "‹" else "›", color = Color.White.copy(alpha = 0.45f), fontSize = 64.sp)
    }
}

@Composable
private fun BackHint() {
    Box(Modifier.fillMaxSize().padding(bottom = 10.dp), contentAlignment = Alignment.BottomCenter) {
        Text("⌄", color = Color.White.copy(alpha = 0.35f), fontSize = 40.sp)
    }
}
