package com.hyundaiapp.ownertest.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.hyundaiapp.ownertest.game.GameSnapshot
import com.hyundaiapp.ownertest.game.View

/** 1536×1024 이미지 안의 자리들 — 이미지 비율 좌표(0~1). 탭 판정과 표시에 쓴다 */
object Spots {
    // 정면 (f_*)
    val frontSteering = Rect(0.16f, 0.48f, 0.47f, 0.96f)
    val frontMirror = Rect(0.40f, 0.13f, 0.67f, 0.28f)
    val frontWindshield = Rect(0.12f, 0.08f, 0.88f, 0.56f)
    val frontTurnLeft = Offset(0.30f, 0.60f)
    val frontTurnRight = Offset(0.375f, 0.60f)

    // 왼쪽 (l_*) / 오른쪽 (r_*) 의 사이드미러
    val leftMirror = Rect(0.44f, 0.30f, 0.68f, 0.54f)
    val rightMirror = Rect(0.34f, 0.35f, 0.51f, 0.52f)

    // 핸들 확대 (s_*)
    val thermalPaper = Rect(0.16f, 0.14f, 0.36f, 0.43f)
    val glowNote = Rect(0.37f, 0.47f, 0.65f, 0.78f)
    val leftStalk = Offset(0.225f, 0.545f)
    val steerTurnLeft = Offset(0.405f, 0.40f)
    val steerTurnRight = Offset(0.605f, 0.40f)
}

/** 엔진 상태 → 겹쳐 그릴 이미지들(아래부터, 알파). 전부 순수 함수 */
object Frames {

    /** 정면: 실내등 × 김 진행률 × 전조등, 결말이면 새벽 */
    fun front(s: GameSnapshot, dawn: Float): List<Pair<Int, Float>> {
        val lit = s.car.cabinLightOn == true
        val p = s.fogClearProgress.coerceIn(0f, 1f)
        val layers = ArrayList<Pair<Int, Float>>()
        layers += seq(if (lit) Img.LIT_FOG else Img.DARK_FOG, p)
        if (s.car.headlightOn) {
            val beamFogged = if (lit) Img.F_LIT_BEAM_FOG100 else Img.F_DARK_BEAM_FOG100
            val beamClear = if (lit) Img.F_LIT_BEAM_FOG0 else Img.F_DARK_BEAM_FOG0
            if (p < 0.995f) layers += beamFogged to (1f - p)
            if (p > 0.005f) layers += beamClear to p
        }
        if (dawn > 0f) {
            val stage = dawn.coerceIn(0f, 1f) * 3f
            layers += Img.F_DAWN1 to stage.coerceIn(0f, 1f)
            if (stage > 1f) layers += Img.F_DAWN2 to (stage - 1f).coerceIn(0f, 1f)
            if (stage > 2f) layers += Img.F_DAWN3 to (stage - 2f).coerceIn(0f, 1f)
        }
        return layers
    }

    fun side(left: Boolean, lit: Boolean): List<Pair<Int, Float>> =
        listOf((if (left) (if (lit) Img.L_LIT else Img.L_DARK) else (if (lit) Img.R_LIT else Img.R_DARK)) to 1f)

    /** 핸들 확대: 밝으면 열 감응지 진행률, 어두우면 야광(켜진 지 22초 뒤부터 흐려짐) */
    fun steering(s: GameSnapshot, glowAgeMs: Long): List<Pair<Int, Float>> {
        if (s.car.cabinLightOn == true) return seq(Img.THERMAL, s.thermalRevealProgress)
        val layers = arrayListOf(Img.S_DARK to 1f)
        if (s.glowVisible) layers += (if (glowAgeMs < 22_000) Img.S_DARK_GLOW else Img.S_DARK_GLOW_FADE) to 1f
        return layers
    }

    /** 룸미러 확대: 접근 정도로 괴물 1~5, 브레이크면 붉은 판 */
    fun rearview(s: GameSnapshot): List<Pair<Int, Float>> {
        val a = monsterApproach(s.monster.active, s.monster.elapsedMs, s.monster.brakeHeldMs)
        return seq(if (s.car.brakePressed == true) Img.REAR_RED else Img.REAR, a)
    }

    fun leftMirror(flashOn: Boolean): List<Pair<Int, Float>> = listOf((if (flashOn) Img.LM_FLASH else Img.LM_DARK) to 1f)

    fun rightMirror(): List<Pair<Int, Float>> = listOf(Img.RM_DARK to 1f)

    /** 시점에 들어갈 때 미리 읽어 둘 장들 */
    fun prefetchFor(view: View): List<Int> = when (view) {
        View.FRONT -> Img.FRONT_ALL
        View.LEFT -> listOf(Img.L_DARK, Img.L_LIT, Img.LM_DARK, Img.LM_FLASH)
        View.RIGHT -> listOf(Img.R_DARK, Img.R_LIT, Img.RM_DARK)
        View.STEERING -> Img.STEERING_ALL
        View.REARVIEW -> Img.REAR_ALL
        View.LEFT_MIRROR -> listOf(Img.LM_DARK, Img.LM_FLASH)
        View.RIGHT_MIRROR -> listOf(Img.RM_DARK)
    }
}
