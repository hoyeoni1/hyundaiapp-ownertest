package com.hyundaiapp.ownertest.signals

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** 방향지시등 상태. SDK `TurnSignalValue`를 그대로 옮긴다 */
enum class TurnSignal { OFF, LEFT, RIGHT }

/** 기어. DRIVE는 D와 수동 단수 전부 */
enum class GearState { PARK, REVERSE, NEUTRAL, DRIVE, UNKNOWN }

/** 승객 도어 4개 */
enum class DoorId { FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT }

/** 신호 소스 연결 상태 */
enum class SourceStatus { OFF, CONNECTING, READY, FAILED }

/**
 * 게임이 읽는 차량 상태. 값이 아직 안 왔으면 null.
 * 전부 읽기 전용 신호다 — 앱은 차를 제어하지 않는다.
 */
data class CarState(
    val cabinLightOn: Boolean? = null,
    val lowBeamOn: Boolean? = null,
    val highBeamOn: Boolean? = null,
    val defrostFrontOn: Boolean? = null,
    /** 핸들 열선 단계 0~3 */
    val steeringHeat: Int? = null,
    val turnSignal: TurnSignal? = null,
    val brakePressed: Boolean? = null,
    /** 4도어 일괄 잠금 */
    val doorsLocked: Boolean? = null,
    /** 도어별 열림(위치 > 0) */
    val doorOpen: Map<DoorId, Boolean> = emptyMap(),
    val gear: GearState? = null,
) {
    /** 하향등 또는 상향등이 켜져 있으면 "비추고 있다" */
    val headlightOn: Boolean get() = lowBeamOn == true || highBeamOn == true
    val anyDoorOpen: Boolean get() = doorOpen.values.any { it }
}

interface CarSignals {
    val state: StateFlow<CarState>
    val status: StateFlow<SourceStatus>
    val lastError: StateFlow<String?>
    fun start()
    fun stop()
}

/** 테스트·미리보기용. 값을 직접 넣는다 */
class FakeCarSignals(initial: CarState = CarState()) : CarSignals {
    private val _state = MutableStateFlow(initial)
    override val state: StateFlow<CarState> = _state
    override val status = MutableStateFlow(SourceStatus.OFF)
    override val lastError = MutableStateFlow<String?>(null)

    override fun start() { status.value = SourceStatus.READY }
    override fun stop() { status.value = SourceStatus.OFF }

    fun set(transform: CarState.() -> CarState) = _state.update(transform)
    fun setDoor(door: DoorId, open: Boolean) = set { copy(doorOpen = doorOpen + (door to open)) }
}
