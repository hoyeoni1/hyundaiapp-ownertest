# 쏘우카 프론트 방안 — 생성 이미지 판 + Compose 동적 레이어

- 작성: 2026-11-09. 백엔드(신호·엔진) 완료 뒤 화면 계획. 대상 화면: Pleos 에뮬레이터 2560×1440 @160dpi, 앱 영역 2560×1300(실측 `wm size`).

## 결론

| 항목 | 결정 |
|---|---|
| 방식 | **정적 배경판은 생성 이미지 4~5장**, 상태에 따라 바뀌는 건 전부 **Compose 레이어**(어둠·김·빛줄기·글씨·야광·괴물·붉은빛·손전등·새벽) |
| 왜 전부 생성 이미지가 아닌가 | 시점 7개 × 상태(실내등·김·전조등·괴물·새벽)를 전부 그림으로 만들면 수십 장, 생성물끼리 같은 차로 보이지 않는다. "화면 속 차가 진짜 차를 비춘다"는 10초에 걸쳐 김이 걷히고 브레이크를 밟는 동안만 붉어지는 **연속 반응**이라 프로그램 레이어여야 한다 |
| 왜 전부 손그림이 아닌가 | 시간. 분위기(어두운 지하주차장의 질감)는 생성 이미지가 가장 빨리 준다 |
| 일관성 확보 | **마스터 1장**(운전석에서 본 실내, 아주 넓게)을 만들고 정면·왼쪽·오른쪽·핸들 확대는 그 한 장을 **잘라서/당겨서** 쓴다. 따로 만드는 건 거울 속 풍경 3장과 기둥 1장뿐 |
| 스타일 | 어둡고 거칠고 디테일이 적게(밤 + 그늘이 생성물의 불일치를 가린다). 채도 낮은 청록·호박색. 로고·실제 차종 특징 없음 |
| 1차 제출 전 | 레이어 파이프라인을 **자리표시 판(단색·실루엣)** 으로 먼저 완성해 지금 당장 플레이되게 하고, 생성 이미지는 나오는 대로 갈아 끼운다 |

이 세션엔 이미지 생성 도구가 없다. 생성은 이민하가(ChatGPT 이미지·Midjourney·Stable Diffusion 중 상업 이용이 허용되는 것), 프롬프트·규격·갈아 끼우는 코드는 이쪽에서.

## 배경판 (생성 이미지)

| # | 판 | 쓰는 시점 | 규격 | 안에 비워 둘 것 |
|---|---|---|---|---|
| P1 | 마스터 실내 — 운전석에서 본 어두운 차 안, 아주 넓게(좌 A필러~우 A필러 바깥까지) | 정면(가운데 자르기), 왼쪽·오른쪽(당기기), 핸들 확대(잘라 확대, 필요하면 img2img로 선명하게) | 3840×1440 | **앞유리 안쪽은 거의 검게**(김·기둥·빛줄기 레이어가 올라감), 핸들 가운데·림은 비어 있게(쪽지 레이어), 룸미러 유리는 검게 |
| P2 | 룸미러 속 — 차 뒤 어두운 주차장 통로 | 룸미러 확대 | 1600×700 | 가운데를 비워 둠(괴물 실루엣·붉은빛 레이어) |
| P3 | 왼쪽 사이드미러 속 — B3로 올라가는 경사로, 꺼진 등 | 왼쪽 사이드미러 확대 | 1200×800 | 경사로 위쪽 한 점(손전등 레이어) |
| P4 | 오른쪽 사이드미러 속 — 기둥·벽·버려진 카트 | 오른쪽 사이드미러 확대 | 1200×800 | — |
| P5 | 기둥 — 앞유리 너머 콘크리트 기둥, 전조등을 받은 상태 | 정면(전조등 ON일 때 앞유리 영역에 합성) | 2560×1440(앞유리 영역만 쓰임) | 기둥 가운데 평평한 면(스프레이 글씨는 Compose 텍스트) |

아이템(열 감응지·야광 쪽지·괴물 실루엣·손전등)은 Compose로 그린다. 글씨는 전부 Compose 텍스트(선명도·수정 용이).

### 넣는 곳 — `app/src/main/res/drawable-nodpi/`

이미 같은 이름의 **자리표시 판**(단색 PNG, 규격 그대로)이 들어 있다. 생성한 그림을 **같은 이름으로 덮어쓰면** 끝이다. 코드는 이 이름을 참조한다.

| 판 | 파일 이름 | 크기 | 형식 |
|---|---|---|---|
| P1 | `plate_interior_master.png` | 3840×1440 | PNG 또는 WebP(무손실/품질 90). WebP로 넣을 땐 `.png`를 지우고 `plate_interior_master.webp` — 확장자만 다르고 이름이 같은 파일 둘은 빌드가 거부한다 |
| P2 | `plate_rearview.png` | 1600×700 | 〃 |
| P3 | `plate_mirror_left.png` | 1200×800 | 〃 |
| P4 | `plate_mirror_right.png` | 1200×800 | 〃 |
| P5 | `plate_pillar.png` | 2560×1440 | 〃 |

- 이름은 Android 리소스 규칙: 영문 소문자·숫자·밑줄만. 바꾸지 말 것
- `drawable-nodpi`라 화면 밀도에 따라 확대·축소되지 않는다(원본 픽셀 그대로)
- 크기가 조금 달라도 된다(코드가 맞춰 자르고 늘린다). 비율은 지킬 것 — P1 8:3, P2 16:7, P3·P4 3:2, P5 16:9
- 파일 하나 2MB 안쪽 권장. P1은 메모리 약 22MB(ARGB)라 더 키우지 않는다
- 넣은 뒤: `./gradlew :app:assembleDebug` → 설치 → 정면·좌우·핸들 확대·거울 확대에서 확인

### 프롬프트 초안 (공통 꼬리말을 모든 판에 붙인다)

공통 꼬리말: `dark underground parking garage at night, almost no light, grainy cinematic photo, desaturated teal and amber, heavy shadows, low detail, no text, no logos, no people, 16:9`

| 판 | 프롬프트 |
|---|---|
| P1 | `view from the driver's seat inside a parked car, very wide angle showing both A-pillars, dashboard, steering wheel, rearview mirror, door tops and side windows; the windshield glass is nearly black; interior lit only by a faint glow from the dashboard; generic modern car interior` + 꼬리말 |
| P2 | `view through a car's rearview mirror: a dark concrete parking garage aisle behind the car, pillars receding into blackness, wet floor, one distant dead lamp` + 꼬리말 |
| P3 | `view in a car side mirror: a concrete ramp curving up to the next parking level, a small sign reading nothing, flickering fluorescent tube far up the ramp` + 꼬리말 |
| P4 | `view in a car side mirror: a concrete pillar and wall, an abandoned shopping cart, drain grate on the floor` + 꼬리말 |
| P5 | `a square concrete parking garage pillar lit from below by car headlights, flat gray surface facing the camera, rest of the frame black` + 꼬리말 |

일관성 팁: P1을 먼저 확정하고, P2~P5는 P1을 참조 이미지로 넣어(img2img·스타일 참조) 같은 톤으로. 핸들 확대가 흐리면 P1의 핸들 부분을 잘라 img2img로 한 번 더.

## 생성 이미지 검토 (2026-11-09, `ssowcar_background_assets/` → `drawable-nodpi/`에 복사 완료)

| 판 | 평가 | 쓸 때 주의 |
|---|---|---|
| P1 실내 | 좋다. 앞유리·룸미러 유리가 검어 레이어 자리가 확보됨. 좌우 끝에 사이드미러가 작게 들어 있어 왼쪽·오른쪽 시점도 이 한 장으로 됨 | 천장등이 희미하게 켜진 상태로 그려짐 → 실내등 OFF는 어둠 레이어로 누른다. 앞유리 영역은 대략 x 600~3350, y 100~835 (3840×1440 기준) — 구현 때 정확히 맞춘다. **핸들 확대는 이 판을 잘라 키우면 흐리다(약 3.5배)** → 전용 판 P6 필요 |
| P2 룸미러 속 | 좋다. 거울 테두리까지 들어 있어 그대로 룸미러 확대 화면. 가운데 통로가 비어 있어 괴물·붉은빛 자리 충분 | 가운데 등이 켜져 있음(요청은 꺼진 등) — 괴물 실루엣의 배경으로 오히려 좋다 |
| P3 왼쪽 미러 속 | 좋다. 경사로 위 형광등 자리가 손전등 깜빡임 위치. 빈 표지판은 Compose로 "B3"를 넣거나 비워 둠 | — |
| P4 오른쪽 미러 속 | 좋다. 기둥·카트·배수구 | — |
| P5 기둥 | 좋다. 평평한 면이 넓어 스프레이 글씨 자리 충분. 아래 40%가 검어 대시보드에 가려지는 부분으로 자연스럽다 | 정면 앞유리 영역에 맞춰 잘라 넣는다 |

톤은 다섯 장이 같은 지하주차장으로 보인다(청록·호박, 젖은 바닥, 거친 입자). 일관성 걱정은 해소.

## 상황별 이미지로 다 만들까? — 정책

| 상황 | 결론 | 이유 |
|---|---|---|
| 글씨(열 감응지·야광 화살표·기둥 스프레이·힌트) | **레이어** | 생성기는 글자, 특히 한글을 못 쓴다. 수정도 잦다 |
| 김 걷힘(10초), 야광 페이드(30초), 열 감응지 번짐(10초), 괴물 접근·후퇴(브레이크 누르는 동안), 손전등 깜빡임, 붉은빛, 좌측 가장자리 깜빡임 | **레이어** | 연속 변화라 그림 두 장을 섞으면(크로스페이드) 생성물의 기하가 조금만 달라도 겹쳐 보인다 |
| 핸들 확대(어두움 / 밝음) | **이미지 P6** | 해상도 때문에 전용 판이 필요. 어두움은 P6에 어둠 레이어를 얹는 것으로 충분하므로 한 장만 |
| 새벽(결말) | **이미지 P7** + 레이어 | P1을 바탕으로 앞유리에 푸른 새벽빛이 든 변형을 img2img로 만들면 레이어보다 설득력 있다. 전환은 P1→P7 크로스페이드(같은 기하라 안전) |
| 괴물 | **레이어 우선, 이미지는 선택** | 실루엣 Compose 도형으로 먼저. P2를 바탕으로 "멀리 선 형체"·"가까이 선 형체" 2장을 img2img로 만들어 섞어 보고 나으면 교체 |
| 실내등 ON/OFF, 전조등 ON/OFF, 김 있음/없음 조합(정면 8가지) | **레이어** | 조합이 8가지고 전조등·제상은 각각 10초 연속 변화. 그림으로 만들면 조합마다 기하가 어긋남 |

요약: **배경판 5 + 추가 2(P6 핸들 확대, P7 새벽)**, 나머지는 전부 레이어. 괴물 2장은 선택.

### 추가 생성 요청

| 판 | 파일 이름(drawable-nodpi) | 크기 | 프롬프트 (공통 꼬리말 붙임) |
|---|---|---|---|
| P6 핸들 확대 | `plate_steering.png` | 2560×1440 | P1의 핸들 부분을 잘라 참조 이미지로 넣고: `close-up of a car steering wheel from the driver's seat at night, the whole wheel fills the frame, center horn pad and the lower rim are plain and empty, two stalks visible behind the wheel, dark dashboard behind, faint dashboard glow` |
| P7 새벽 | `plate_interior_dawn.png` | 3840×1440 | P1을 참조 이미지(img2img, 낮은 변화량)로: `same car interior, same camera, the windshield now shows the faint blue-gray light of dawn entering a parking garage, interior still dim` — **기하를 그대로 유지**해야 크로스페이드가 된다 |
| (선택) 괴물 멀리/가까이 | `plate_rearview_far.png`, `plate_rearview_near.png` | 1600×700 | P2를 참조로: `same view, a tall dark featureless silhouette standing far down the aisle` / `…standing close behind the car, filling the center of the mirror` |

## 동적 레이어 (Compose) — 엔진 스냅샷과 1:1

| 레이어 | 입력(GameSnapshot) | 표현 |
|---|---|---|
| 어둠 | `car.cabinLightOn` | 실내등 꺼짐: 판 위에 검정 70%. 켜짐: 위쪽에서 내려오는 따뜻한 방사형 빛, 검정 10% |
| 김 | `fogClearProgress`, `car.defrostFrontOn` | 앞유리 영역에 흰 반투명 노이즈. 진행률만큼 가운데부터 원형으로 걷힘. 제상 OFF면 30초에 걸쳐 다시 낌(UI 보간) |
| 빛줄기·기둥 | `car.headlightOn`, `pillarVisible` | 앞유리 영역에 P5를 알파로 합성 + 위로 퍼지는 빛 그라데이션. `pillarVisible`이면 기둥 면에 스프레이 글씨 "그것은 빨간 불빛을 무서워해" |
| 핸들 확대 | `steeringReadable`, `thermalRevealProgress`, `glowVisible`, `glowChargeMs` | 어두우면 종이 윤곽만. 열 감응지: 진행률만큼 갈색으로 번지며 글씨 알파 ↑. 야광 쪽지: 밝을 땐 태양(진함)·달(흐림), `glowVisible`이면 어둠 속 연두빛 달 + ◀ ◀ ▶ (30초 페이드) |
| 레버 | `car.turnSignal`, `signal.leverGlow` | 핸들 확대의 레버 화살표가 실제로 깜빡임. 네 번 놓치면 왼쪽 레버 희미하게 빛남 |
| 괴물 | `monster.active/elapsedMs/brakeHeldMs`, `car.brakePressed` | 룸미러 확대: 어둠 속 실루엣이 `elapsedMs`에 따라 커지고 가까워짐. 브레이크 ON: 바닥·벽에 붉은 그라데이션, `brakeHeldMs`에 따라 실루엣 후퇴. 다른 시점에선 룸미러(작은 것)에 움직임 + 화면 가장자리 어두워짐 |
| 괴물 힌트 | `monster.hint` | 정면 구석 메모의 기둥 글씨 한 번 깜빡 / 그 아래 "차에서 빨간 불이 나는 곳은 뒤다." |
| 신호 | `signal.active/elapsedMs/lastReply`, `signal.showArrowHint` | 어느 시점이든 왼쪽 가장자리가 두 번 희게 깜빡. 왼쪽 미러 확대: 경사로 위 손전등이 10초마다 세 번. 응답: 길게 한 번 / 짧게 두 번. 두 번 놓치면 구석 메모에 ◀ ◀ ▶ |
| 조사 힌트 | `investigationHint` | 정면 아래 한 줄, 4초 떴다 사라짐 |
| 새벽·결말 | `phase == CLEARED/ENDED` | 앞유리 위쪽부터 푸른 새벽빛이 번짐. 자막 + TTS(`CarVoice`). ENDED: 문 너머 빛 → 결과 화면 |
| 이야기 밖 화면 | `phase` | TITLE(S1 "문을 잠그면 시작합니다"), PAUSED_GEAR(회색), GAME_OVER(붉은 암전 → "실패" → [다시]), EARLY_EXIT(목소리 + [다시 타기]) |

## 시점 이동

| 어디서 | 어떻게 | 어디로 |
|---|---|---|
| 정면 | 양쪽 화살표(상시) 또는 스와이프 | 왼쪽 / 오른쪽 |
| 정면 | 룸미러 탭 / 핸들 탭 | 룸미러 확대 / 핸들 확대 |
| 왼쪽·오른쪽 | 사이드미러 탭 | 미러 확대 |
| 확대 | 뒤로(화살표 ↓ 또는 아무 곳 탭) | 이전 시점 |

시점 변경은 전부 `engine.onView(view)`로 전달한다(이미 구현). 터치 피드백 0.25초, 전환 애니메이션 200ms.

## 세로(portrait) 대응

규정상 두 형태 모두 지원. 세로에서는 마스터 판의 가운데를 세로로 자르고 좌우 당기기를 넓게 둔다. 핸들·거울 확대는 그대로.

## 소리

| 소리 | 출처 |
|---|---|
| 철컥(잠금·문), 긁는 소리(괴물), 손전등 딸깍, 바람·물방울 앰비언스, 새벽 새소리 | CC0 라이브러리(freesound CC0) 또는 직접 녹음. 생성 음원을 쓰면 상업 이용 허용 확인 |
| 차의 목소리 | Pleos TTS(완료) |

## 권리

- 생성 이미지는 상업 이용을 허용하는 서비스에서, 로고·실제 차종 식별 요소 없이. 공모전 약관상 제3자 권리 침해 책임은 참가자에게 있다.
- 기획서·PT에서 "배경은 생성 이미지, 연출은 앱 렌더링"이라고 밝힌다(설명회: 생성형 AI 사용 가능, 구조·로직은 직접 설명).

## 순서

| 날 | 할 일 |
|---|---|
| 1 | Compose 레이어 파이프라인 + 자리표시 판(단색·실루엣)으로 시점 7개·레이어 전부 동작 → e2e를 화면으로 확인 |
| 2 | 이민하: P1~P5 생성(위 프롬프트). 이쪽: 판 갈아 끼우기 + 색·투명도 맞춤, 소리 |
| 3 | 세로 대응, 결과 화면, 시연 영상 녹화 |
