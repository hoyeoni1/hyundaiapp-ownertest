# 쏘우카 차량 신호 대응표 v1.2 — 조작 6개

- 작성: 2026-11-09 (v1.2 — [scenario-script.md](scenario-script.md) 기준. 신호가 마지막·클리어, 괴물은 시간 기반). 근거: 크루즈브레이커 `docs/vehicle-data-findings.md`(에뮬레이터 2.1.0, Vehicle SDK 2.3.2). "절"은 그 문서 절 번호. ✅ 실측 / ⚠ 조건부 / ❓ 미확인
- 콘솔 명령은 `tools/vconsole.sh put '<path?query>'` 형식(크루즈브레이커 `tools/vconsole.sh` 재사용)
- 모든 신호는 **읽기 전용**. 제어 API는 쓰지 않는다

## 조작 6개

| 조작 | 쓰이는 곳 | SDK 도메인·리스너 | 판정 | 퍼미션(`pleos.car.permission.`) | 콘솔 | 검증 |
|---|---|---|---|---|---|---|
| 실내등 | 핸들 확대 밝기, 야광 쪽지 충전(10초 ON → OFF) | `Light.getCurrentCabinLightState` 리스너, `getCurrentReadingLightState`(4석) | CABIN `ON` 또는 독서등 1개 `ON` = 켜짐 / 전부 `OFF` = 꺼짐. 10초 연속 ON은 앱 타이머 | CAR_LIGHTS | `body?cabin_light=true/false` | ✅ 6.2 (실내등 ON이면 독서등 4개도 ON으로 옴) |
| 핸들 열선 | 열 감응지 | `SteeringWheel.onHvacSteeringWheelHeatStateUpdated` | ≥ 1 (1초 유지) | CAR_DRIVE + CAR_CLIMATE 선언 상태에서 동작(어느 하나만 필요한지는 미분리) | `steering-wheel-heat?level=2` | ✅ 11-09 쏘우카 앱 실측 |
| 앞유리 제상 | 기둥(전조등과 함께) | `HVAC getHvacDefrosterState(FRONT_WINDSHIELD)` 리스너 | `true` 1초 | CAR_CLIMATE | `hvac?front_defroster=true` | ✅ 6.6 |
| 전조등 | 기둥(제상과 함께) | `Light.registerLowBeamLightState` 리스너(상향 `HighBeam`도 켜짐으로 침) | `ON` 1초 | CAR_LIGHTS | `lights?feature=headlamp_mode&value=LowBeam` (Off/Auto/Position/LowBeam), 상향 `lights?feature=high_beam&value=true` — 11-09 카탈로그 확인 | ✅ 6.2 리스너 수신 |
| 방향지시등 | 이벤트 A 응답 ◀ ◀ ▶ | `TurnSignal.onTurnSignalStateUpdated` | `LEFT`→`OFF`→`LEFT`→`OFF`→`RIGHT`→`OFF`. 다른 방향이 끼면 처음부터 | CAR_DRIVE | `turn-signal?mode=BlinkLeft` / `BlinkRight` / `Off` | ✅ 11-09 쏘우카 앱 실측: LEFT·OFF·LEFT·OFF·RIGHT·OFF 전부 콜백 수신 |
| 브레이크 | 이벤트 B 격퇴 | `Brake.onBrakePedalStatusUpdated` | `true` 연속 8초(앱 타이머, 떼면 리셋) | CAR_DRIVE | `controls?brake=50` / `=0` | ✅ 6.2 |

## 시작·규칙·결말

| 용도 | SDK | 판정 | 콘솔 | 검증 |
|---|---|---|---|---|
| 시작 | `Door.onDoorLockStateUpdated`(4도어 복합) | `false → true` | `body?doors_locked=false` → `=true` | ✅ 6.2 |
| 결말·조기 탈출 | `Door.onPositionUpdated` 4도어(HOOD·REAR 제외) | > 0 | `body?door_fl=true` | ✅ 6.2 |
| 정차 전용 | `Gear.registerGearSelection` | ≠ `GEAR_PARK` → 회색 시스템 화면 | `gear?gear=Drive` | ✅ 6.2 |
| 음성(결말·조기 탈출) | Pleos TTS SDK 3.3.1 `ON_DEVICE` `ko_KR` | 자막 동시 | — | ✅ 크루즈브레이커 11-01 |
| 초기값 | 시작 시 각 도메인 getter 1회 | "이미 켜져 있던 것" 분기 | — | 설계 |

## 화면이 비추는 실제 상태

| 그림 | 실제 신호 |
|---|---|
| 차 안 밝기, 핸들 확대의 글씨 읽힘 | 실내등·독서등 |
| 야광 쪽지의 빛남 | 실내등 10초 이상 ON 뒤 OFF(앱 규칙, 30초 뒤 소멸) |
| 열 감응지의 글씨 | 핸들 열선 ≥ 1(한 번 드러나면 유지) |
| 앞유리 김 → 걷힘 | 제상 ON |
| 기둥 조명 | 전조등 ON |
| 핸들 확대의 레버 화살표 깜빡임 | 방향지시등 |
| 룸미러 확대의 붉은 벽 | 브레이크 |

## 이벤트 타이머 (신호가 아니라 앱 규칙)

| 이벤트 | 시작 조건 | 간격 | 응답 창 | 실패 |
|---|---|---|---|---|
| B 괴물 | 없음(시간) | 시작 후 90초 + 0~60초, 격퇴 뒤 60~150초. 신호 진행 중엔 안 옴 | 25초 | 게임 오버 → [다시](상태 유지). 기둥을 안 봤으면 힌트 없음 |
| A 신호 (마지막) | 열 감응지 ∧ 야광 화살표 ∧ 기둥 ∧ 괴물 1회 격퇴 | 20~60초, 놓치면 30~90초 | 40초 | 없음(다시 온다) |
| 결말 | A 정답 | 5초 뒤 새벽 | — | — |

## 쓰지 않는 신호 (v1.1)

온도·내외기·시트 열선(이 판에서 뺌), 창문·창문 잠금·와이퍼·뒷유리 열선·A/C·풍량·앰비언트·충전구·SoC·주행거리·VIN·내비. 비상등·경적·EPB·차일드 락·좌석 점유는 불가·불안정.

## 실측 결과 (2026-11-09, 쏘우카 앱 `tools/vtest-sawcar.sh`·`tools/e2e-sawcar.sh`)

| 항목 | 결과 |
|---|---|
| 전조등 콘솔 항목명 | `lights?feature=headlamp_mode&value=LowBeam/Off`, 상향 `lights?feature=high_beam&value=true` — 하향등·상향등 리스너 ON/OFF 수신 |
| 방향지시등 OFF 콜백 | 온다. LEFT·OFF·LEFT·OFF·RIGHT·OFF 전부 수신 → ◀ ◀ ▶ 판정 그대로 |
| 핸들 열선 | CAR_DRIVE + CAR_CLIMATE 선언 상태에서 0↔2 수신 |
| 실내등·제상·브레이크·잠금·문 열림·기어 | 전부 수신. 실내등은 CabinLightState만 쓴다(독서등 리스너는 등록 안 함) |
| 문 열림 리스너 | 한 프로세스에서 콜백이 안 온 적이 있어(원인 미확인) 앱이 떠 있는 동안 1초 조회로 보완 |
| 전 과정(빠른 타이머) | 잠금 → 쪽지 둘 → 기둥 → 괴물 격퇴 → 신호 ◀ ◀ ▶ → CLEARED → 문 열기 → ENDED. 분기 셋(게임 오버→다시, 조기 탈출→다시 타기, 기어 D→일시정지→P) 전부 통과. 기록: `docs/measurements/e2e-2026-11-09.log` |
| 에뮬레이터 라이트 기본값 | 시연 전 `tools/demo-sawcar.sh reset`으로 전부 OFF |
| 차 목소리(TTS) | Pleos TTS 3.3.1 기기 내·ko_KR로 "문은 내가 잠갔어." 재생(`tts ready → speak → start → done`). 앱 시작 때 미리 연결하고, 준비 전 문장은 준비되는 대로 읽는다 |
