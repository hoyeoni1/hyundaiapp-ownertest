# 오너테스트 (OwnerTest)

재난 후의 지하주차장. 문이 잠긴 채 깨어난 플레이어가 **진짜 차의 실내등·핸들 열선·앞유리 제상·전조등·브레이크·방향지시등**으로 밤을 버틴다 — 어둠에서 쪽지를 읽고, 열로 글씨를 드러내고, 기둥의 경고를 보고, 붉은빛으로 뒤의 것을 쫓고, 왼쪽의 신호에 ◀ ◀ ▶ 로 답하면 아침이다. 결말: 차가 아무것도 모르는 플레이어를 내보내기 전에 훈련시키려고 가둔 것. 10~15분, 정차 전용. Pleos Connect 앱 · 2026 현대자동차그룹 앱 개발 공모전.

**상태 (2026-11-09): 백엔드 완료(신호 계층·게임 엔진·단위 테스트 16개·에뮬레이터 전 과정 실측 통과). 화면은 **상황별 생성 이미지 46장**(`res/drawable-nodpi/`, [docs/image-list.md](docs/image-list.md))을 엔진 상태에 맞춰 고르고 크로스페이드한다 — 에뮬레이터 스크린샷 투어로 확인. PRD는 v0.6(구판)이라 시나리오 v1.2와 맞지 않음.**

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
├─ vconsole.sh      에뮬레이터 콘솔 조작(크루즈브레이커와 동일)
├─ demo-ownertest.sh   콘솔 = 손: reset / lock / cabin / heat / defrost / beam / high / turn / answer / brake / door / gear
├─ vtest-ownertest.sh  신호 24단계 실측(콘솔 PUT → 앱 로그)
├─ e2e-ownertest.sh    전 과정 + 분기 3개 자동 실행(빠른 타이머)
├─ shots-ownertest.sh  전 과정을 돌리며 장면마다 스크린샷(촬영용 타이머)
└─ ownertest-console.html  오너테스트 전용 콘솔(차 실내 배치): 잠금·문·실내등·열선·제상·전조등·깜빡이·브레이크·기어 + 상태 표시
```

## 시작하기

1. 개발 환경은 크루즈브레이커 `docs/dev-env-setup-log.md`와 같다 — JDK 17, Android SDK, Pleos 에뮬레이터 `Pleos_Connect_2_1_0`
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
