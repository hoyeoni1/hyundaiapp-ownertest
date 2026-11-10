package com.hyundaiapp.sawcar.signals

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.constant.area.DoorValue
import ai.pleos.playground.vehicle.constant.area.TurnSignalValue
import ai.pleos.playground.vehicle.constant.area.WindowValue
import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.BrakePedalStatusListener
import ai.pleos.playground.vehicle.listener.DoorLockStateListener
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import ai.pleos.playground.vehicle.listener.HvacDefrosterStateListener
import ai.pleos.playground.vehicle.listener.HvacSteeringWheelHeatStateListener
import ai.pleos.playground.vehicle.listener.LightStateListener
import ai.pleos.playground.vehicle.listener.TurnSignalStateListener
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Pleos Vehicle SDK → [CarState]. 읽기 전용.
 *
 * `Vehicle.initialize()` 직후엔 서비스 연결 전이라 호출이 실패할 수 있어, 기어 조회로 연결을 확인한 뒤 리스너를 건다
 * (실패하면 1초 뒤 다시, 최대 [MAX_ATTEMPTS]번). 크루즈브레이커 core의 VehicleSdkSource와 같은 방식.
 * 리스너는 값이 바뀔 때만 오므로 등록 직후 현재 값을 한 번씩 조회한다.
 */
class PleosCarSignals(context: Context) : CarSignals {
    private val context = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private val _state = MutableStateFlow(CarState())
    override val state: StateFlow<CarState> = _state
    override val status = MutableStateFlow(SourceStatus.OFF)
    override val lastError = MutableStateFlow<String?>(null)

    private var vehicle: Vehicle? = null
    private val unregister = mutableListOf<() -> Unit>()
    @Volatile private var running = false

    override fun start() {
        if (running) return
        running = true
        status.value = SourceStatus.CONNECTING
        val v = Vehicle(context).also { it.initialize() }
        vehicle = v
        connect(v, attempt = 1)
    }

    override fun stop() {
        running = false
        handler.removeCallbacksAndMessages(TOKEN)
        unregister.forEach { runCatching(it) }
        unregister.clear()
        runCatching { vehicle?.release() }
        vehicle = null
        status.value = SourceStatus.OFF
    }

    private fun connect(v: Vehicle, attempt: Int) {
        val onErr: (Exception) -> Unit = { e ->
            handler.post {
                if (!running) return@post
                if (attempt >= MAX_ATTEMPTS) {
                    fail("연결", e)
                    status.value = SourceStatus.FAILED
                } else {
                    handler.postDelayed({ connect(v, attempt + 1) }, TOKEN, RETRY_MS)
                }
            }
        }
        runCatching {
            v.getGear().getCurrentGearSelectionState({ g ->
                _state.update { it.copy(gear = g.toGear()) }
                handler.post { if (running) subscribeAll(v) }
            }, onErr)
        }.onFailure { onErr(it as? Exception ?: Exception(it)) }
    }

    private fun subscribeAll(v: Vehicle) {
        val light = v.getLight()
        val hvac = v.getHVAC()
        val steering = v.getSteeringWheel()
        val turn = v.getTurnSignal()
        val brake = v.getBrake()
        val door = v.getDoor()
        val gear = v.getGear()

        // 실내등·전조등
        val cabin = lightListener("실내등") { on -> _state.update { it.copy(cabinLightOn = on) } }
        val low = lightListener("하향등") { on -> _state.update { it.copy(lowBeamOn = on) } }
        val high = lightListener("상향등") { on -> _state.update { it.copy(highBeamOn = on) } }
        light.registerCabinLightState(cabin); unregister += { light.unregisterCabinLightState(cabin) }
        light.registerLowBeamLightState(low); unregister += { light.unregisterLowBeamLightState(low) }
        light.registerHighBeamLightState(high); unregister += { light.unregisterHighBeamLightState(high) }
        light.getCurrentCabinLightState({ s -> _state.update { it.copy(cabinLightOn = s.isOn()) } }, { fail("실내등 조회", it) })
        light.getCurrentLowBeamLightState({ s -> _state.update { it.copy(lowBeamOn = s.isOn()) } }, { fail("하향등 조회", it) })
        light.getCurrentHighBeamLightState({ s -> _state.update { it.copy(highBeamOn = s.isOn()) } }, { fail("상향등 조회", it) })

        // 앞유리 제상
        val frontGlass = WindowArea(WindowValue.FRONT_WINDSHIELD)
        val defrost = object : HvacDefrosterStateListener {
            override fun onHvacDefrosterStateUpdated(value: Boolean?) { _state.update { it.copy(defrostFrontOn = value) }; log("제상 $value") }
            override fun onFailed(exception: Exception) = fail("제상", exception)
        }
        hvac.registerHvacDefrosterState(frontGlass, defrost); unregister += { hvac.unregisterHvacDefrosterState(frontGlass, defrost) }
        hvac.getHvacDefrosterState(frontGlass, { on -> _state.update { it.copy(defrostFrontOn = on) } }, { fail("제상 조회", it) })

        // 핸들 열선
        val heat = object : HvacSteeringWheelHeatStateListener {
            override fun onHvacSteeringWheelHeatStateUpdated(value: Int?) { _state.update { it.copy(steeringHeat = value) }; log("핸들 열선 $value") }
            override fun onFailed(exception: Exception) = fail("핸들 열선", exception)
        }
        steering.registerHvacSteeringWheelHeatState(heat); unregister += { steering.unregisterHvacSteeringWheelHeatState(heat) }
        steering.getHvacSteeringWheelHeatState({ lv -> _state.update { it.copy(steeringHeat = lv) } }, { fail("핸들 열선 조회", it) })

        // 방향지시등
        val turnListener = object : TurnSignalStateListener {
            override fun onTurnSignalStateUpdated(value: TurnSignalValue?) { _state.update { it.copy(turnSignal = value.toTurn()) }; log("방향지시등 $value") }
            override fun onFailed(exception: Exception) = fail("방향지시등", exception)
        }
        turn.registerTurnSignalState(turnListener); unregister += { turn.unregisterTurnSignalState(turnListener) }
        turn.getCurrentTurnSignalState({ t -> _state.update { it.copy(turnSignal = t.toTurn()) } }, { fail("방향지시등 조회", it) })

        // 브레이크
        val brakeListener = object : BrakePedalStatusListener {
            override fun onBrakePedalStatusUpdated(value: Boolean?) { _state.update { it.copy(brakePressed = value) }; log("브레이크 $value") }
            override fun onFailed(exception: Exception) = fail("브레이크", exception)
        }
        brake.registerBrakePedalStatus(brakeListener); unregister += { brake.unregisterBrakePedalStatus(brakeListener) }
        brake.getCurrentBrakePedalStatus({ b -> _state.update { it.copy(brakePressed = b) } }, { fail("브레이크 조회", it) })

        // 문 잠금(4도어 복합 영역)·문 열림(도어별)
        val allDoors = DoorArea(DoorValue.ROW_1_LEFT, DoorValue.ROW_1_RIGHT, DoorValue.ROW_2_LEFT, DoorValue.ROW_2_RIGHT)
        val lock = object : DoorLockStateListener {
            override fun onDoorLockStateUpdated(value: Boolean?) { _state.update { it.copy(doorsLocked = value) }; log("잠금 $value") }
            override fun onFailed(exception: Exception) = fail("잠금", exception)
        }
        door.registerDoorLockState(allDoors, lock); unregister += { door.unregisterDoorLockState(allDoors, lock) }
        door.getDoorLockState(allDoors, { locked -> _state.update { it.copy(doorsLocked = locked) } }, { fail("잠금 조회", it) })
        DOORS.forEach { (id, value) ->
            val area = DoorArea(value)
            val pos = object : DoorPositionListener {
                override fun onPositionUpdated(value: Int?) { setDoor(id, value); log("문 $id $value") }
                override fun onFailed(exception: Exception) = fail("문 $id", exception)
            }
            runCatching { door.registerDoorPosition(area, pos); unregister += { door.unregisterDoorPosition(area, pos) } }
                .onFailure { fail("문 $id 등록", it as? Exception ?: Exception(it)) }
        }
        // 문 열림은 게임의 핵심 규칙(시작·조기 탈출·결말)인데, 도어별 리스너가 한 프로세스에서 안 오는 경우를 봤다(11-09 e2e 1회차).
        // 앱이 떠 있는 동안만 1초마다 조회해 리스너를 보완한다
        pollDoors(v)

        // 기어
        val gearListener = object : GearSelectionListener {
            override fun onSelected(value: VehicleGear?) { _state.update { it.copy(gear = value.toGear()) }; log("기어 $value") }
            override fun onFailed(exception: Exception) = fail("기어", exception)
        }
        gear.registerGearSelection(gearListener); unregister += { gear.unregisterGearSelection(gearListener) }

        status.value = SourceStatus.READY
        Log.i(TAG, "vehicle signals ready")
    }

    private fun pollDoors(v: Vehicle) {
        if (!running) return
        DOORS.forEach { (id, value) ->
            v.getDoor().getCurrentDoorPosition(DoorArea(value), { p ->
                val open = p != null && p > 0
                if (_state.value.doorOpen[id] != open) log("문 $id $p (조회)")
                setDoor(id, p)
            }, { fail("문 $id 조회", it) })
        }
        handler.postDelayed({ pollDoors(v) }, TOKEN, DOOR_POLL_MS)
    }

    private fun lightListener(name: String, set: (Boolean?) -> Unit) = object : LightStateListener {
        override fun onLightStateUpdated(value: LightState?) { set(value.isOn()); log("$name $value") }
        override fun onFailed(exception: Exception) = fail(name, exception)
    }

    private fun setDoor(id: DoorId, position: Int?) {
        if (position == null) return
        _state.update { it.copy(doorOpen = it.doorOpen + (id to (position > 0))) }
    }

    private fun fail(what: String, e: Exception?) {
        Log.w(TAG, "$what: ${e?.message}")
        lastError.value = "$what: ${e?.message}"
    }

    private fun log(msg: String) = Log.i(TAG, msg)

    companion object {
        const val TAG = "SawCarSignals"
        private val TOKEN = Any()
        private const val MAX_ATTEMPTS = 15
        private const val RETRY_MS = 1_000L
        private const val DOOR_POLL_MS = 1_000L
        private val DOORS = listOf(
            DoorId.FRONT_LEFT to DoorValue.ROW_1_LEFT,
            DoorId.FRONT_RIGHT to DoorValue.ROW_1_RIGHT,
            DoorId.REAR_LEFT to DoorValue.ROW_2_LEFT,
            DoorId.REAR_RIGHT to DoorValue.ROW_2_RIGHT,
        )
    }
}

private fun LightState?.isOn(): Boolean? = when (this) {
    null -> null
    LightState.OFF -> false
    else -> true // ON, DAYTIME_RUNNING
}

private fun TurnSignalValue?.toTurn(): TurnSignal? = when (this) {
    null -> null
    TurnSignalValue.LEFT -> TurnSignal.LEFT
    TurnSignalValue.RIGHT -> TurnSignal.RIGHT
    else -> TurnSignal.OFF
}

private fun VehicleGear?.toGear(): GearState? = when (this) {
    null -> null
    VehicleGear.GEAR_PARK -> GearState.PARK
    VehicleGear.GEAR_REVERSE -> GearState.REVERSE
    VehicleGear.GEAR_NEUTRAL -> GearState.NEUTRAL
    VehicleGear.GEAR_UNKNOWN -> GearState.UNKNOWN
    else -> GearState.DRIVE
}
