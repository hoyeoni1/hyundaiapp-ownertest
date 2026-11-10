package com.hyundaiapp.sawcar.game

import com.hyundaiapp.sawcar.signals.CarState
import com.hyundaiapp.sawcar.signals.DoorId
import com.hyundaiapp.sawcar.signals.GearState
import com.hyundaiapp.sawcar.signals.TurnSignal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {
    private val cfg = GameConfig()
    private var now = 1_000_000L
    private var car = CarState(doorsLocked = false, gear = GearState.PARK, cabinLightOn = false, lowBeamOn = false, highBeamOn = false,
        defrostFrontOn = false, steeringHeat = 0, turnSignal = TurnSignal.OFF, brakePressed = false)
    private val engine = GameEngine(Random(1), cfg)

    private fun set(transform: CarState.() -> CarState) { car = car.transform(); engine.onCarState(car, now) }
    private fun advance(ms: Long) { now += ms; engine.tick(now) }
    private fun view(v: View) = engine.onView(v, now)
    private val s get() = engine.snapshot.value

    private fun start() { engine.onCarState(car, now); set { copy(doorsLocked = true) } }

    /** 쪽지 둘과 기둥을 보고 괴물을 한 번 쫓은 상태 */
    private fun prepareForSignal() {
        start()
        set { copy(cabinLightOn = true) }
        view(View.STEERING)
        set { copy(steeringHeat = 2) }
        advance(cfg.thermalRevealMs)
        advance(1)
        set { copy(cabinLightOn = false) }
        advance(1)
        view(View.FRONT)
        set { copy(defrostFrontOn = true, lowBeamOn = true) }
        advance(cfg.fogClearMs + 1)
        assertTrue(s.thermalSeen); assertTrue(s.glowSeen); assertTrue(s.pillarSeen)
        waitForMonster()
        set { copy(brakePressed = true) }
        advance(cfg.monsterRepelHoldMs)
        assertEquals(1, s.repelledCount)
        set { copy(brakePressed = false) }
    }

    private fun waitForMonster() {
        var guard = 0
        while (!s.monster.active && guard++ < 1000) advance(1_000)
        assertTrue("monster should have appeared", s.monster.active)
    }

    /** 신호를 기다리는 동안 괴물이 오면 브레이크로 쫓는다(시간 기반이라 언제든 올 수 있다) */
    private fun waitForSignal() {
        var guard = 0
        while (!s.signal.active && guard++ < 1000) {
            if (s.monster.active) {
                set { copy(brakePressed = true) }
                advance(cfg.monsterRepelHoldMs)
                set { copy(brakePressed = false) }
            } else {
                advance(1_000)
            }
        }
        assertTrue("signal should have started", s.signal.active)
    }

    private fun press(dir: TurnSignal) { set { copy(turnSignal = dir) }; set { copy(turnSignal = TurnSignal.OFF) } }

    @Test fun `starts only on unlocked-to-locked transition`() {
        engine.onCarState(car.copy(doorsLocked = true), now)
        assertEquals(Phase.TITLE, s.phase) // 처음부터 잠겨 있으면 시작 안 함
        engine.onCarState(car.copy(doorsLocked = false), now)
        engine.onCarState(car.copy(doorsLocked = true), now)
        assertEquals(Phase.PLAYING, s.phase)
    }

    @Test fun `steering notes need cabin light, thermal needs heat for 10s`() {
        start()
        view(View.STEERING)
        assertFalse(s.steeringReadable)
        set { copy(cabinLightOn = true) }
        assertTrue(s.steeringReadable)
        set { copy(steeringHeat = 1) }
        advance(cfg.thermalRevealMs - 1)
        assertFalse(s.thermalRevealed)
        advance(2)
        assertTrue(s.thermalRevealed)
        assertTrue(s.thermalSeen)
    }

    @Test fun `glow shows only after 10s charge then light off, for 30s`() {
        start()
        view(View.STEERING)
        set { copy(cabinLightOn = true) }
        advance(cfg.glowChargeMs - 1_000)
        set { copy(cabinLightOn = false) }
        advance(1)
        assertFalse("too short charge", s.glowVisible)
        set { copy(cabinLightOn = true) }
        advance(cfg.glowChargeMs)
        set { copy(cabinLightOn = false) }
        advance(1)
        assertTrue(s.glowVisible)
        assertTrue(s.glowSeen)
        advance(cfg.glowVisibleMs)
        assertFalse(s.glowVisible)
    }

    @Test fun `pillar needs defrost and headlight and the front view`() {
        start()
        view(View.LEFT)
        set { copy(lowBeamOn = true) }
        advance(cfg.fogClearMs + 1)
        assertFalse(s.pillarVisible)
        set { copy(defrostFrontOn = true) }
        advance(cfg.fogClearMs + 1)
        assertTrue(s.pillarVisible)
        assertFalse("not looking front", s.pillarSeen)
        view(View.FRONT)
        assertTrue(s.pillarSeen)
        set { copy(lowBeamOn = false) }
        assertFalse(s.pillarVisible)
        assertTrue("seen stays", s.pillarSeen)
    }

    @Test fun `monster comes after 90s plus jitter regardless of investigation`() {
        start()
        advance(cfg.firstMonsterMs - 1)
        assertFalse(s.monster.active)
        waitForMonster()
        assertTrue(s.gameTimeMs <= cfg.firstMonsterMs + cfg.monsterJitterMs)
    }

    @Test fun `brake held 8s repels, releasing resets the hold`() {
        start()
        waitForMonster()
        set { copy(brakePressed = true) }
        advance(cfg.monsterRepelHoldMs - 1_000)
        set { copy(brakePressed = false) }
        advance(500)
        set { copy(brakePressed = true) }
        advance(cfg.monsterRepelHoldMs - 1_000)
        assertTrue("hold restarted", s.monster.active)
        advance(1_000)
        assertFalse(s.monster.active)
        assertEquals(1, s.repelledCount)
        assertEquals(Phase.PLAYING, s.phase)
    }

    @Test fun `no brake for 25s is game over, hints only when pillar seen`() {
        start()
        waitForMonster()
        advance(cfg.monsterHint2Ms)
        assertEquals("pillar not seen → no hint", MonsterHint.NONE, s.monster.hint)
        advance(cfg.monsterTimeoutMs - cfg.monsterHint2Ms)
        assertEquals(Phase.GAME_OVER, s.phase)
        engine.retry(now)
        assertEquals(Phase.PLAYING, s.phase)
        assertEquals(1, s.retryCount)
        assertEquals(GameEngine.HINT_LOOK_AHEAD, s.investigationHint)
        assertFalse(s.monster.active)
    }

    @Test fun `monster hints appear at 10s and 18s when pillar was seen`() {
        start()
        view(View.FRONT)
        set { copy(defrostFrontOn = true, lowBeamOn = true) }
        advance(cfg.fogClearMs + 1)
        assertTrue(s.pillarSeen)
        waitForMonster()
        advance(cfg.monsterHint1Ms)
        assertEquals(MonsterHint.PILLAR_FLASH, s.monster.hint)
        advance(cfg.monsterHint2Ms - cfg.monsterHint1Ms)
        assertEquals(MonsterHint.RED_IS_BEHIND, s.monster.hint)
    }

    @Test fun `signal only after both notes, pillar and one repel`() {
        start()
        advance(300_000)
        assertFalse(s.signalEligible)
        assertFalse(s.signal.active)
    }

    @Test fun `correct answer L L R clears the game`() {
        prepareForSignal()
        assertTrue(s.signalEligible)
        waitForSignal()
        press(TurnSignal.LEFT); press(TurnSignal.LEFT)
        assertEquals(2, s.signal.presses.size)
        press(TurnSignal.RIGHT)
        assertEquals(Phase.CLEARED, s.phase)
        assertEquals(FlashReply.LONG_OK, s.signal.lastReply)
        set { copy(doorsLocked = false) }
        engine.onCarState(car.copy(doorOpen = mapOf(DoorId.FRONT_LEFT to true)), now)
        assertEquals(Phase.ENDED, s.phase)
    }

    @Test fun `wrong answer replies two short blinks and the signal comes again`() {
        prepareForSignal()
        waitForSignal()
        press(TurnSignal.RIGHT); press(TurnSignal.LEFT); press(TurnSignal.LEFT)
        assertEquals(Phase.PLAYING, s.phase)
        assertFalse(s.signal.active)
        assertEquals(FlashReply.TWO_SHORT_NO, s.signal.lastReply)
        waitForSignal()
        assertTrue(s.signal.presses.isEmpty())
    }

    @Test fun `missing the 40s window is not a penalty and hints build up after two misses`() {
        prepareForSignal()
        waitForSignal()
        advance(cfg.signalWindowMs)
        assertFalse(s.signal.active)
        waitForSignal()
        advance(cfg.signalWindowMs)
        waitForSignal()
        assertTrue(s.signal.showArrowHint)
        assertFalse(s.signal.leverGlow)
    }

    @Test fun `opening a door before the end pauses as early exit and reboarding resumes`() {
        start()
        advance(10_000)
        set { copy(doorOpen = mapOf(DoorId.FRONT_LEFT to true)) }
        assertEquals(Phase.EARLY_EXIT, s.phase)
        val frozen = s.gameTimeMs
        advance(60_000)
        assertEquals("clock frozen while out", frozen, s.gameTimeMs)
        engine.reboard(now)
        assertEquals("door still open", Phase.EARLY_EXIT, s.phase)
        set { copy(doorOpen = mapOf(DoorId.FRONT_LEFT to false)) }
        engine.reboard(now)
        assertEquals(Phase.PLAYING, s.phase)
        advance(1_000)
        assertEquals(frozen + 1_000, s.gameTimeMs)
    }

    @Test fun `leaving park pauses and returning resumes`() {
        start()
        set { copy(gear = GearState.DRIVE) }
        assertEquals(Phase.PAUSED_GEAR, s.phase)
        set { copy(gear = GearState.PARK) }
        assertEquals(Phase.PLAYING, s.phase)
    }

    @Test fun `investigation hints escalate with idle time`() {
        // 괴물이 끼어들지 않게 첫 등장을 멀리 둔다
        val e = GameEngine(Random(1), cfg.copy(firstMonsterMs = 10_000_000))
        var c = car
        fun put(transform: CarState.() -> CarState) { c = c.transform(); e.onCarState(c, now) }
        fun go(ms: Long) { now += ms; e.tick(now) }
        fun v() = e.snapshot.value
        e.onCarState(c, now); put { copy(doorsLocked = true) }
        assertNull(v().investigationHint)
        go(60_000)
        assertEquals("어둡다. 손 닿는 곳에 불이 있을 것이다.", v().investigationHint)
        go(60_000)
        assertEquals("머리 위.", v().investigationHint)
        assertEquals(2, v().hintCount)
        put { copy(cabinLightOn = true) }
        go(1)
        assertNull("progress resets idle", v().investigationHint)
        go(60_000)
        assertEquals("핸들에 뭔가 붙어 있다.", v().investigationHint)
    }

    @Test fun `monster waits while a signal is active`() {
        prepareForSignal()
        waitForSignal()
        var guard = 0
        while (s.signal.active && guard++ < 100) {
            assertFalse("no monster during signal", s.monster.active)
            advance(1_000)
        }
    }
}
