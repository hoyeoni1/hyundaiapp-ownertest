package com.hyundaiapp.ownertest.game

import com.hyundaiapp.ownertest.signals.CarState
import com.hyundaiapp.ownertest.signals.GearState
import com.hyundaiapp.ownertest.signals.TurnSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

/** 플레이어가 보고 있는 곳. 화면이 알려 준다 */
enum class View { FRONT, STEERING, REARVIEW, LEFT, LEFT_MIRROR, RIGHT, RIGHT_MIRROR }

enum class Phase {
    /** S1. 문을 잠그면 시작 */
    TITLE,
    PLAYING,
    /** 기어가 P를 벗어남(이야기 밖 일시정지) */
    PAUSED_GEAR,
    /** 결말 전에 문을 열었다 */
    EARLY_EXIT,
    /** 괴물에게 25초 안에 붉은빛을 못 보여 줬다 */
    GAME_OVER,
    /** 신호에 올바르게 답했다 → 새벽·첫 목소리 */
    CLEARED,
    /** 결말 뒤 문을 열었다 */
    ENDED,
}

enum class MonsterHint { NONE, PILLAR_FLASH, RED_IS_BEHIND }
enum class FlashReply { NONE, LONG_OK, TWO_SHORT_NO }

data class MonsterStatus(
    val active: Boolean = false,
    val elapsedMs: Long = 0,
    /** 브레이크를 연속으로 밟고 있는 시간(괴물이 물러서는 중) */
    val brakeHeldMs: Long = 0,
    val hint: MonsterHint = MonsterHint.NONE,
)

data class SignalStatus(
    val active: Boolean = false,
    val elapsedMs: Long = 0,
    /** 지금까지 켠 방향(최대 3) */
    val presses: List<TurnSignal> = emptyList(),
    val showArrowHint: Boolean = false,
    val leverGlow: Boolean = false,
    val lastReply: FlashReply = FlashReply.NONE,
)

data class GameSnapshot(
    val phase: Phase = Phase.TITLE,
    val gameTimeMs: Long = 0,
    val view: View = View.FRONT,
    val car: CarState = CarState(),
    /** 실내등이 켜져 핸들 확대의 글씨가 읽힌다 */
    val steeringReadable: Boolean = false,
    val thermalRevealProgress: Float = 0f,
    val thermalRevealed: Boolean = false,
    val thermalSeen: Boolean = false,
    val glowChargeMs: Long = 0,
    val glowVisible: Boolean = false,
    val glowSeen: Boolean = false,
    val fogClearProgress: Float = 0f,
    val pillarVisible: Boolean = false,
    val pillarSeen: Boolean = false,
    val monster: MonsterStatus = MonsterStatus(),
    val signal: SignalStatus = SignalStatus(),
    val repelledCount: Int = 0,
    val retryCount: Int = 0,
    val hintCount: Int = 0,
    /** 조사 구간에서 진전이 없을 때 정면 아래에 뜨는 한 줄 */
    val investigationHint: String? = null,
    val signalEligible: Boolean = false,
)

data class GameConfig(
    val firstMonsterMs: Long = 90_000,
    val monsterJitterMs: Long = 60_000,
    val monsterRepeatMinMs: Long = 60_000,
    val monsterRepeatMaxMs: Long = 150_000,
    val monsterRepelHoldMs: Long = 8_000,
    val monsterTimeoutMs: Long = 25_000,
    val monsterHint1Ms: Long = 10_000,
    val monsterHint2Ms: Long = 18_000,
    val signalFirstMinMs: Long = 20_000,
    val signalFirstMaxMs: Long = 60_000,
    val signalRetryMinMs: Long = 30_000,
    val signalRetryMaxMs: Long = 90_000,
    val signalWindowMs: Long = 40_000,
    val glowChargeMs: Long = 10_000,
    val glowVisibleMs: Long = 30_000,
    val thermalRevealMs: Long = 10_000,
    val fogClearMs: Long = 10_000,
    val answer: List<TurnSignal> = listOf(TurnSignal.LEFT, TurnSignal.LEFT, TurnSignal.RIGHT),
)

/**
 * 시나리오 v1.2의 상태기계. Android에 의존하지 않는다.
 *
 * 시간은 호출자가 넣는 벽시계(ms)로 받고, 안에서는 일시정지를 뺀 "게임 시간"으로 센다.
 * 차량 상태는 [onCarState], 시점은 [onView], 시간 경과는 [tick]으로 넣는다. 결과는 [snapshot].
 */
class GameEngine(
    private val random: Random = Random.Default,
    private val config: GameConfig = GameConfig(),
) {
    private val _snapshot = MutableStateFlow(GameSnapshot())
    val snapshot: StateFlow<GameSnapshot> = _snapshot

    private var car = CarState()
    private var view = View.FRONT
    private var phase = Phase.TITLE

    // 시계
    private var wallStartedAt = 0L
    private var pausedTotal = 0L
    private var pausedAt: Long? = null
    private var t = 0L

    // 시작
    private var sawUnlocked = false

    // 핸들의 쪽지
    private var cabinOnSince: Long? = null
    private var cabinEverOn = false
    private var viewedSteeringLit = false
    private var glowVisibleUntil: Long? = null
    private var heatOnAt: Long? = null
    private var thermalSeen = false
    private var glowSeen = false

    // 기둥
    private var defrostOnSince: Long? = null
    private var pillarSeen = false

    // 괴물
    private var monsterNextAt: Long? = null
    private var monsterStartedAt: Long? = null
    private var brakeHeldSince: Long? = null
    private var repelled = 0
    private var retries = 0
    private var afterGameOverHint = false

    // 신호
    private var signalNextAt: Long? = null
    private var signalStartedAt: Long? = null
    private val presses = mutableListOf<TurnSignal>()
    private var missCount = 0
    private var lastReply = FlashReply.NONE

    // 힌트
    private var lastProgressAt = 0L
    private var shownHintKey: String? = null
    private var hints = 0

    fun onView(v: View, wall: Long) {
        view = v
        tick(wall)
    }

    fun onCarState(next: CarState, wall: Long) {
        val prev = car
        car = next
        when (phase) {
            Phase.TITLE -> {
                if (next.doorsLocked == false) sawUnlocked = true
                if (next.doorsLocked == true && sawUnlocked) startGame(wall)
            }
            Phase.PLAYING -> {
                updateClock(wall)
                if (next.gear != null && next.gear != GearState.PARK) { pause(wall, Phase.PAUSED_GEAR); tick(wall); return }
                if (next.anyDoorOpen) { pause(wall, Phase.EARLY_EXIT); tick(wall); return }
                onCabinLight(prev.cabinLightOn, next.cabinLightOn)
                if (next.steeringHeat != null && next.steeringHeat >= 1 && heatOnAt == null) heatOnAt = t
                if (next.defrostFrontOn == true && defrostOnSince == null) defrostOnSince = t
                if (next.defrostFrontOn != true) defrostOnSince = null
                if (next.brakePressed == true && brakeHeldSince == null) brakeHeldSince = t
                if (next.brakePressed != true) brakeHeldSince = null
                onTurnSignal(prev.turnSignal, next.turnSignal)
            }
            Phase.PAUSED_GEAR -> if (next.gear == GearState.PARK) resume(wall)
            Phase.CLEARED -> if (next.anyDoorOpen) phase = Phase.ENDED
            Phase.EARLY_EXIT, Phase.GAME_OVER, Phase.ENDED -> Unit
        }
        tick(wall)
    }

    /** 게임 오버 뒤 [다시]. 본 것은 그대로, 괴물만 사라진다 */
    fun retry(wall: Long) {
        if (phase != Phase.GAME_OVER) return
        retries++
        monsterStartedAt = null
        brakeHeldSince = if (car.brakePressed == true) t else null
        afterGameOverHint = !pillarSeen
        resume(wall)
        monsterNextAt = t + config.firstMonsterMs + random.nextLong(config.monsterJitterMs + 1)
        tick(wall)
    }

    /** 조기 탈출 뒤 [다시 타기]. 문이 닫혀 있어야 한다 */
    fun reboard(wall: Long) {
        if (phase != Phase.EARLY_EXIT || car.anyDoorOpen) return
        resume(wall)
        tick(wall)
    }

    fun tick(wall: Long) {
        if (phase == Phase.PLAYING) {
            updateClock(wall)
            tickInvestigation()
            tickMonster()
            tickSignal()
        }
        publish()
    }

    // ---- 시작·시계 ----

    private fun startGame(wall: Long) {
        phase = Phase.PLAYING
        wallStartedAt = wall
        pausedTotal = 0
        pausedAt = null
        t = 0
        lastProgressAt = 0
        if (car.cabinLightOn == true) { cabinOnSince = 0; cabinEverOn = true }
        if (car.steeringHeat != null && car.steeringHeat!! >= 1) heatOnAt = 0
        if (car.defrostFrontOn == true) defrostOnSince = 0
        if (car.brakePressed == true) brakeHeldSince = 0
        monsterNextAt = config.firstMonsterMs + random.nextLong(config.monsterJitterMs + 1)
    }

    private fun updateClock(wall: Long) {
        t = wall - wallStartedAt - pausedTotal
    }

    private fun pause(wall: Long, into: Phase) {
        updateClock(wall)
        pausedAt = wall
        phase = into
    }

    private fun resume(wall: Long) {
        pausedAt?.let { pausedTotal += wall - it }
        pausedAt = null
        phase = Phase.PLAYING
        updateClock(wall)
    }

    // ---- 핸들의 쪽지 ----

    private fun onCabinLight(prev: Boolean?, next: Boolean?) {
        if (next == true && prev != true) { cabinOnSince = t; cabinEverOn = true }
        if (next != true && prev == true) {
            val since = cabinOnSince
            if (since != null && t - since >= config.glowChargeMs) glowVisibleUntil = t + config.glowVisibleMs
            cabinOnSince = null
        }
    }

    private val steeringReadable get() = car.cabinLightOn == true
    private val thermalRevealed get() = heatOnAt?.let { t - it >= config.thermalRevealMs } == true
    private val glowVisible get() = glowVisibleUntil?.let { t < it } == true && car.cabinLightOn != true
    private val fogCleared get() = defrostOnSince?.let { t - it >= config.fogClearMs } == true
    private val pillarVisible get() = fogCleared && car.headlightOn

    private fun tickInvestigation() {
        var progressed = false
        if (view == View.STEERING && steeringReadable && !viewedSteeringLit) { viewedSteeringLit = true; progressed = true }
        if (view == View.STEERING && steeringReadable && thermalRevealed && !thermalSeen) { thermalSeen = true; progressed = true }
        if (view == View.STEERING && glowVisible && !glowSeen) { glowSeen = true; progressed = true }
        if (view == View.FRONT && pillarVisible && !pillarSeen) { pillarSeen = true; progressed = true }
        if (cabinEverOn && lastProgressAt == 0L && t > 0) progressed = true
        if (progressed) lastProgressAt = t
    }

    private fun investigationHint(): String? {
        if (afterGameOverHint) return HINT_LOOK_AHEAD
        val idle = t - lastProgressAt
        fun at(ms: Long, text: String): String? = if (idle >= ms) text else null
        if (!cabinEverOn) return at(120_000, "머리 위.") ?: at(60_000, "어둡다. 손 닿는 곳에 불이 있을 것이다.")
        if (!viewedSteeringLit) return at(60_000, "핸들에 뭔가 붙어 있다.")
        if (!thermalRevealed) at(180_000, "핸들을 데우는 스위치가 있다.")?.let { return it }
        if (!thermalRevealed) at(90_000, "가장자리가 그을렸다. 열이 닿았던 자리다.")?.let { return it }
        if (!glowSeen) at(180_000, "실내등을 켜 두었다가 끈다.")?.let { return it }
        if (!glowSeen) at(90_000, "태양은 진하고 달은 흐리다. 빛을 쬔 뒤의 어둠.")?.let { return it }
        if (!pillarSeen) {
            val defrost = car.defrostFrontOn == true
            val beam = car.headlightOn
            if (defrost && !beam) return at(60_000, "걷혔지만 어둡다.")
            if (!defrost && beam) return at(60_000, "빛이 김에 번진다.")
            if (!defrost && !beam) return at(90_000, "뿌옇고, 어둡다. 둘 다 해결해야 보인다.")
        }
        return null
    }

    // ---- 괴물 ----

    private val monsterActive get() = monsterStartedAt != null
    private val signalActive get() = signalStartedAt != null

    private fun tickMonster() {
        val next = monsterNextAt
        if (!monsterActive && next != null && t >= next) {
            if (signalActive) {
                monsterNextAt = t + 5_000 // 신호가 끝난 뒤에 온다
            } else {
                monsterStartedAt = t
                monsterNextAt = null
            }
        }
        val started = monsterStartedAt ?: return
        val held = brakeHeldSince?.let { t - it } ?: 0
        if (car.brakePressed == true && held >= config.monsterRepelHoldMs) {
            monsterStartedAt = null
            repelled++
            lastProgressAt = t
            monsterNextAt = t + config.monsterRepeatMinMs + random.nextLong(config.monsterRepeatMaxMs - config.monsterRepeatMinMs + 1)
            return
        }
        if (t - started >= config.monsterTimeoutMs) {
            pausedAt = wallStartedAt + pausedTotal + t // 게임 시간 기준으로 멈춘다
            phase = Phase.GAME_OVER
        }
    }

    private fun monsterStatus(): MonsterStatus {
        val started = monsterStartedAt ?: return MonsterStatus()
        val elapsed = t - started
        val held = if (car.brakePressed == true) brakeHeldSince?.let { t - it } ?: 0 else 0
        val hint = when {
            !pillarSeen -> MonsterHint.NONE
            elapsed >= config.monsterHint2Ms -> MonsterHint.RED_IS_BEHIND
            elapsed >= config.monsterHint1Ms -> MonsterHint.PILLAR_FLASH
            else -> MonsterHint.NONE
        }
        return MonsterStatus(active = true, elapsedMs = elapsed, brakeHeldMs = held, hint = hint)
    }

    // ---- 신호 ----

    private val signalEligible get() = thermalSeen && glowSeen && pillarSeen && repelled >= 1

    private fun tickSignal() {
        if (signalEligible && signalNextAt == null && !signalActive) {
            signalNextAt = t + config.signalFirstMinMs + random.nextLong(config.signalFirstMaxMs - config.signalFirstMinMs + 1)
        }
        val next = signalNextAt
        if (!signalActive && next != null && t >= next && !monsterActive) {
            signalStartedAt = t
            signalNextAt = null
            presses.clear()
            lastReply = FlashReply.NONE
        }
        val started = signalStartedAt ?: return
        if (t - started >= config.signalWindowMs) {
            missCount++
            endSignal(retry = true)
        }
    }

    private fun onTurnSignal(prev: TurnSignal?, next: TurnSignal?) {
        if (!signalActive) return
        if (next == null || next == TurnSignal.OFF || next == prev) return
        presses += next
        if (presses.size < config.answer.size) return
        if (presses.toList() == config.answer) {
            lastReply = FlashReply.LONG_OK
            signalStartedAt = null
            lastProgressAt = t
            phase = Phase.CLEARED
        } else {
            lastReply = FlashReply.TWO_SHORT_NO
            missCount++
            endSignal(retry = true)
        }
    }

    private fun endSignal(retry: Boolean) {
        signalStartedAt = null
        presses.clear()
        if (retry) signalNextAt = t + config.signalRetryMinMs + random.nextLong(config.signalRetryMaxMs - config.signalRetryMinMs + 1)
    }

    private fun signalStatus(): SignalStatus {
        val started = signalStartedAt
        return SignalStatus(
            active = started != null,
            elapsedMs = started?.let { t - it } ?: 0,
            presses = presses.toList(),
            showArrowHint = started != null && missCount >= 2,
            leverGlow = started != null && missCount >= 4,
            lastReply = lastReply,
        )
    }

    // ---- 출력 ----

    private fun publish() {
        val hint = if (phase == Phase.PLAYING) investigationHint() else null
        if (hint != null && hint != shownHintKey) hints++
        shownHintKey = hint
        _snapshot.value = GameSnapshot(
            phase = phase,
            gameTimeMs = t,
            view = view,
            car = car,
            steeringReadable = steeringReadable,
            thermalRevealProgress = heatOnAt?.let { ((t - it).toFloat() / config.thermalRevealMs).coerceIn(0f, 1f) } ?: 0f,
            thermalRevealed = thermalRevealed,
            thermalSeen = thermalSeen,
            glowChargeMs = cabinOnSince?.let { t - it } ?: 0,
            glowVisible = glowVisible,
            glowSeen = glowSeen,
            fogClearProgress = defrostOnSince?.let { ((t - it).toFloat() / config.fogClearMs).coerceIn(0f, 1f) } ?: 0f,
            pillarVisible = pillarVisible,
            pillarSeen = pillarSeen,
            monster = monsterStatus(),
            signal = signalStatus(),
            repelledCount = repelled,
            retryCount = retries,
            hintCount = hints,
            investigationHint = hint,
            signalEligible = signalEligible,
        )
    }

    companion object {
        const val HINT_LOOK_AHEAD = "앞을 봐야 한다."
    }
}
