# 오너테스트 (OwnerTest)

재난 후의 지하주차장. 문이 잠긴 채 깨어난 플레이어가 **진짜 차의 실내등·핸들 열선·앞유리 제상·전조등·브레이크·방향지시등**으로 밤을 버틴다 — 어둠에서 쪽지를 읽고, 열로 글씨를 드러내고, 기둥의 경고를 보고, 붉은빛으로 뒤의 것을 쫓고, 왼쪽의 신호에 ◀ ◀ ▶ 로 답하면 아침이다. 결말: 차가 아무것도 모르는 플레이어를 내보내기 전에 훈련시키려고 가둔 것. 10~15분, 정차 전용. Pleos Connect 앱 · 2026 현대자동차그룹 앱 개발 공모전.

**상태 (2026-11-14): 제출용 서명 release APK가 `apk/`에 있다(11-11 빌드). 신호 계층·게임 엔진·단위 테스트 16개·에뮬레이터 전 과정 실측 통과. 화면은 **상황별 생성 이미지 46장**(`res/drawable-nodpi/`, [docs/image-list.md](docs/image-list.md))을 엔진 상태에 맞춰 고르고 크로스페이드한다 — 에뮬레이터 스크린샷 투어로 확인. PRD는 v0.6(구판)이라 시나리오 v1.2와 맞지 않음.**

## 심사용 안내 (GitHub 제출물)

| 항목 | 내용 |
|---|---|
| 앱 이름 | 오너테스트 (OwnerTest) — 팀 삼각고래, 일반 부문 |
| 한 줄 소개 | 정차한 차 안에서 실제 실내등·핸들 열선·앞유리 제상·전조등·브레이크·방향지시등을 직접 조작해 단서를 찾는 Pleos Connect 탈출 게임. 앱은 차량 상태를 읽기만 한다 |
| APK 위치 | `apk/SamgakGorae_OwnerTest_release.apk` (서명된 release 빌드, 약 54MB, 11-11 빌드) |
| 패키지 | `com.hyundaiapp.ownertest` |
| 기획서 | [docs/proposal.md](docs/proposal.md) (접수양식 02 항목과 같은 내용), 시나리오 [docs/scenario-script.md](docs/scenario-script.md) |
| 시연 순서 | [docs/scenario-script.md](docs/scenario-script.md)의 "시연 순서" |

### 실행 방법

1. Pleos Connect 에뮬레이터 v2.1.0을 켠다(시스템 이미지 `Pleos Connect v2.1.0 APIs`, AVD 프로필 `Pleos_Connect_v2_0` 2560×1440). 에뮬레이터 사용자 ID는 10이다.
2. APK를 설치한다: `adb install -r apk/SamgakGorae_OwnerTest_release.apk`
3. 앱을 켠다. 시작 화면 아래에 `차량 연결됨`이 보이면 차량 신호를 받고 있는 것이다. 따로 줄 권한은 없다.
4. 차량 조작은 에뮬레이터 Virtual Console로 값을 주입해 재현한다(Git Bash, 저장소 루트):
   - `bash tools/vconsole.sh up` (포트 포워딩·하트비트 시작) → `bash tools/demo-ownertest.sh reset`
   - `bash tools/demo-ownertest.sh lock` (문 잠금 = 시작) → `cabin on` → 화면의 핸들을 탭 → `heat 2` → 10초 뒤 `cabin off` → 핸들 밖을 탭(정면) → `defrost on`, `beam LowBeam` → 무언가 다가오면 룸미러를 탭하고 `brake on` 8초 뒤 `brake off` → 왼쪽에서 신호가 오면 왼쪽 끝을 탭, 사이드미러를 탭하고 `answer` → `unlock`, `door fl open`
   - 명령 대신 `tools/ownertest-console.html`을 브라우저로 열어 버튼으로 조작해도 된다
   - 끝나면 `bash tools/vconsole.sh down`
5. 전 과정은 10~15분 걸린다. 이벤트가 나타나는 시간을 줄여서 보려면 `adb shell am broadcast -a com.hyundaiapp.ownertest.DEBUG --es action fast` (게임이 처음부터 다시 시작된다)
6. 시연 고지: 에뮬레이터에는 실제 스위치가 없어 문 잠금·실내등·열선·제상·전조등·브레이크·방향지시등·기어 값을 콘솔로 주입한다(실제 차량에서는 직접 조작한다). 장면 이미지는 생성 이미지다.

### 테스트 환경 및 필요한 설정

- 확인한 환경: Windows 11, Android Studio 2025.3.2(번들 JDK 21; JDK 17 이상), Android Gradle Plugin 8.9.2, Kotlin 2.1.20, Android Emulator 36.4, Pleos Connect 에뮬레이터 v2.1.0(API 34 `pleos_car_emulator`)
- 화면 규격: 에뮬레이터 화면 2560×1440. 앱은 그 가운데 오른쪽 2/3(약 1707×1219)를 쓴다
- 서버: 없음. 계정·개인정보 수집이 없고 인터넷 연결 없이 동작한다
- 권한: `pleos.car.permission.CAR_LIGHTS`(실내등·전조등), `CAR_CLIMATE`(앞유리 제상), `CAR_DRIVE`(방향지시등·브레이크·기어·핸들 열선), `CAR_DOORS`(문 잠금·열림), `TTS_SERVICE`(결말 음성). 모두 조회용이며 제어 권한(`CONTROL_*`)은 선언하지 않았다
- 주행 중 화면: DO(`distractionOptimized`)를 선언하지 않은 정차 전용 앱이다. 기어가 P를 벗어나면 게임이 멈춘다
- 소스에서 빌드: `./gradlew :app:testDebugUnitTest :app:assembleDebug`. release 빌드는 `bash tools/build-release.sh`(서명 키 필요)

### 오픈소스 고지

| 구성 요소 | 라이선스 |
|---|---|
| AndroidX (Core KTX, Jetpack Compose BOM·Material 3, Activity Compose, Lifecycle) | Apache License 2.0 |
| Kotlin 2.1.20, kotlinx-coroutines 1.9.0 | Apache License 2.0 |
| Pleos Playground SDK (Vehicle 2.3.2, TextToSpeech 3.3.1) | 현대자동차그룹 Pleos Connect 개발 환경 제공, 공모전·개발자 약관에 따름 |
| 테스트 전용: JUnit 4.13.2 (EPL 1.0), kotlinx-coroutines-test 1.9.0 (Apache License 2.0) | — |

앱 소스 코드와 문서의 권리는 팀 삼각고래에 있으며 공모전 약관(지식재산권 조항)을 따른다. 위 구성 요소 외에 다른 서드파티 코드는 포함하지 않았다. 장면 이미지 46장은 생성 이미지다.

## 문서

- 시나리오·진행: [docs/scenario-script.md](docs/scenario-script.md) — 구조도, 시점, 조사 ①②, 이벤트(괴물·신호), 시간 처리(힌트·게임 오버), 결말, 분기, 시연 순서
- 숏폼(이민하 안): 타임라인 [docs/shortform-timeline.md](docs/shortform-timeline.md) · 소재·프롬프트 [docs/shortform-plan.md](docs/shortform-plan.md)
- 화면 이미지: [docs/image-list.md](docs/image-list.md) — 생성 이미지 46장의 파일 이름과 프롬프트 (구판 합성 계획: [docs/frontend-plan.md](docs/frontend-plan.md))
- 신호: [docs/signal-mapping.md](docs/signal-mapping.md) — 조작 6개의 SDK·퍼미션·콘솔 명령·**실측 결과**
- 사용 가능 기능 목록: [docs/function-inventory.md](docs/function-inventory.md) · 조작별 퍼즐 예시: [docs/puzzle-ideas.md](docs/puzzle-ideas.md)
- 실측 기록: [docs/measurements/](docs/measurements/) (신호 24단계, 전 과정 e2e)
- 공모전 숏폼 릴스(오너 테스트 · 스마일): [shortform/README.md](shortform/README.md) — 최종 영상, 녹음 큐시트, 빌드 스크립트
- 구판: [PRD.md](PRD.md) v0.6(방탈출), [docs/review-log.md](docs/review-log.md)

## 코드 구조

```
app/src/main/java/com/hyundaiapp/ownertest/
├─ signals/CarSignals.kt        CarState(실내등·하향/상향등·제상·핸들 열선·방향지시등·브레이크·잠금·문 4개·기어), CarSignals, FakeCarSignals
├─ signals/PleosCarSignals.kt   Vehicle SDK → CarState (읽기 전용. 리스너 + 초기 조회 + 문 열림 1초 조회 보완)
├─ game/GameEngine.kt           시나리오 v1.2 상태기계 (Android 의존 없음). onCarState / onView / tick / retry / reboard → snapshot
├─ voice/CarVoice.kt            Pleos TTS(기기 내, ko_KR). 결말·조기 탈출의 첫 목소리
├─ ui/Images.kt                 Img(이미지 46장 id), ImageCache(LRU·미리 읽기), ImageStack(겹쳐 그리기 + 크로스페이드), seq(단계 섞기)
├─ ui/Frames.kt                 엔진 상태 → 겹칠 이미지들(정면·좌우·핸들·룸미러·미러), Spots(탭 자리)
├─ ui/Layers.kt                 이미지 위에 코드로 얹는 것들: 방향지시등 화살표, 레버 빛, 정면 룸미러의 그림자, 가장자리 번쩍임, 메모·한 줄 글
├─ ui/SceneScreen.kt            1인칭 장면: 시점별 이미지 + 레이어 + 탭 판정
├─ ui/SystemScreens.kt          시작(title)·기어 이탈·실패·조기 탈출·성공(end_door) 화면
├─ ui/GameScreen.kt             단계 분기, 결말 자막·음성, 디버그 오버레이
├─ AppContainer.kt              신호 → 엔진 연결, 250ms 틱, 엔진 상태 로그(OwnerTestGame), FAST/SHOTS 타이머(시연·촬영용)
└─ MainActivity.kt              앱 진입 + adb 브로드캐스트(시점·분기·음성)
app/src/main/res/drawable-nodpi/          생성 이미지 46장 (1536×1024 WebP 품질 98, docs/image-list.md의 이름 그대로)
app/src/test/.../game/GameEngineTest.kt   16개 (시작·쪽지·야광·기둥·괴물·게임 오버·힌트·신호·클리어·조기 탈출·기어)
tools/
├─ vconsole.sh      에뮬레이터 콘솔 조작(크롱과 동일)
├─ demo-ownertest.sh   콘솔 = 손: reset / lock / cabin / heat / defrost / beam / high / turn / answer / brake / door / gear
├─ vtest-ownertest.sh  신호 24단계 실측(콘솔 PUT → 앱 로그)
├─ e2e-ownertest.sh    전 과정 + 분기 3개 자동 실행(빠른 타이머)
├─ shots-ownertest.sh  전 과정을 돌리며 장면마다 스크린샷(촬영용 타이머)
└─ ownertest-console.html  오너테스트 전용 콘솔(차 실내 배치): 잠금·문·실내등·열선·제상·전조등·깜빡이·브레이크·기어 + 상태 표시
```

## 시작하기

1. 개발 환경은 크롱(`hyundaiapp-crong`) 저장소의 `docs/dev-env-setup-log.md`와 같다 — JDK 17, Android SDK, Pleos 에뮬레이터 `Pleos_Connect_2_1_0`
2. `local.properties`에 `sdk.dir` (이 PC는 들어 있음)
3. 단위 테스트·빌드: `./gradlew :app:testDebugUnitTest :app:assembleDebug`
4. 설치·실행: `adb install -r app/build/outputs/apk/debug/app-debug.apk` → 앱 실행 → 시작 화면 아래 `차량 연결됨`
5. 콘솔 준비: `bash tools/vconsole.sh up` (하트비트) → `bash tools/demo-ownertest.sh reset`
6. 전 과정 자동 실행: `bash tools/e2e-ownertest.sh > e2e.log 2>&1` (약 2분, 빠른 타이머)
7. 손으로: `bash tools/demo-ownertest.sh lock` → `cabin on` → 화면의 핸들을 탭 → `heat 2` → 10초 뒤 `cabin off` → 핸들 밖을 탭(정면) → `defrost on` + `beam LowBeam` → 괴물이 오면 룸미러 탭, `brake on` 8초 → 신호가 오면 왼쪽 끝 탭 → 사이드미러 탭 → `answer` → `unlock` + `door fl open`
8. 시점·분기를 adb로: `adb shell am broadcast -a com.hyundaiapp.ownertest.DEBUG --es view STEERING` / `--es action retry|reboard|restart|fast|shots|speak|debug`
9. 화면 확인: `bash tools/shots-ownertest.sh <폴더>` → 장면 22장의 PNG
10. 손으로 플레이: `bash tools/vconsole.sh up` 뒤 `tools/ownertest-console.html`을 브라우저로 열기(파일로 바로 열어도 됨, 콘솔 API가 CORS를 허용). 다른 주소면 `?url=http://host:port`

## 규칙

- 앱은 차를 **읽기만** 한다. 제어 API·CONTROL_* 퍼미션을 쓰지 않는다. 문을 잠그지도 않는다
- 정차 전용(DO 미선언). 기어가 P를 벗어나면 엔진이 멈춘다
- 파일·폴더 이름은 영문 소문자·하이픈만(삼각고래 CLAUDE.md 규칙과 동일)

## 다음

- 소리(긁는 소리·철컥 등)는 아직 없음. 실차에서 탭 자리(Spots)와 글씨 크기 확인
- PRD를 시나리오 v1.2에 맞춰 다시 쓰기
- 실차 확인: 안쪽 손잡이 당김 횟수(설명서 페이지 문구), ACC 상태에서 신호 수신 여부
