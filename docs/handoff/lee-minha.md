# 인수인계 — 이민하 (오너테스트 기획·구현)

담당 경로: 저장소 전체 (`app/`, `docs/`, `tools/`)

## 현재 상태 (2026-11-10 기준, 매번 덮어쓰기)

- 한 일 요약: 시나리오 v1.2([docs/scenario-script.md](../scenario-script.md)) 기준으로 **백엔드와 화면이 모두 동작**한다. 신호 계층(읽기 전용) → 게임 엔진(순수 Kotlin, 단위 테스트 16개) → 상황별 생성 이미지 46장을 고르는 1인칭 화면. 에뮬레이터에서 전 과정(시작 → 쪽지 → 기둥 → 괴물 격퇴 → 신호 응답 → 결말 → 문 열어 종료)과 분기 3개(기어 이탈·조기 탈출·게임 오버)를 자동 스크립트로 확인했다. 결말 음성(Pleos TTS)도 나온다.
- 아직 안 되는 것: 소리 효과(긁는 소리·철컥 등) 없음. 정면 화면의 작은 룸미러 속 괴물 암시가 희미하다. 실차에서 탭 자리(`ui/Frames.kt`의 `Spots`)와 글씨 크기를 확인하지 않았다.
- 진행 중 브랜치·PR: 없음(`main`에 직접 올림, 2026-11-10 첫 커밋)
- 다음 할 일(우선순위 순):
  1. `PRD.md`를 시나리오 v1.2에 맞춰 다시 쓰기(지금 것은 v0.6 방탈출 구판)
  2. 소리: 괴물 접근(긁는 소리), 문 철컥, 결말 전 정적
  3. 실차 확인: 안쪽 손잡이 당김 횟수(설명서 문구), ACC 상태에서 신호 수신 여부, 실제 화면 크기에서의 탭 자리
- 막힌 점·질문(누구에게): 없음
- 주의(함정·직접 겪은 이상한 동작):
  - 앱은 Pleos 에뮬레이터 화면(2560×1440)의 **오른쪽 2/3(≈1707×1219)** 만 쓴다. 이미지는 3:2라 양옆이 조금 잘린다.
  - `object Img`의 리소스 id는 `const val`로 두면 런타임 `NoSuchMethodError`가 난다 → getter로 둔다. 이미지는 `ImageBitmap.imageResource`가 아니라 `BitmapFactory`로 읽는다(디코딩 실패 때 null 캐스트로 죽음).
  - 에뮬레이터 시계가 호스트와 다르다(logcat 시각 ≈ 호스트 −17h). 스크립트의 `logcat -c`는 앱 시작 로그를 지운다.
  - 문 열림 리스너가 한 번 조용히 멈춘 적이 있어 1초 조회로 보완해 두었다(`signals/PleosCarSignals.kt`).
  - 콘솔 값 이름은 [docs/signal-mapping.md](../signal-mapping.md)의 실측 결과를 따른다(전조등 `lights?feature=headlamp_mode&value=LowBeam`, 깜빡이 `turn-signal?mode=BlinkLeft` 등).
- 확인 방법:
  - 단위 테스트·빌드: `./gradlew :app:testDebugUnitTest :app:assembleDebug`
  - 전 과정 자동: `bash tools/vconsole.sh up` → `bash tools/e2e-ownertest.sh` (약 2분, 빠른 타이머). 장면 스크린샷: `bash tools/shots-ownertest.sh <폴더>`
  - 손으로: `tools/ownertest-console.html`을 브라우저로 열어 차 실내 배치의 버튼으로 조작. 앱 로그 태그 `OwnerTestGame`(엔진 상태 한 줄), `OwnerTestSignals`(신호), `OwnerTestUi`(이미지), `OwnerTestVoice`(TTS)

## 작업 기록 (최신이 위, 지우지 않음)

### 2026-11-10
- 한 일: 저장소에 첫 업로드. 전용 웹 콘솔 `tools/ownertest-console.html` 추가(Virtual Console API에 직접 PUT, 하트비트·상태 폴링 포함). **프로젝트 이름을 쏘우카(SAW CAR) → 오너테스트(OwnerTest)로 변경**: 패키지 `com.hyundaiapp.sawcar` → `com.hyundaiapp.ownertest`, 앱 이름, 로그 태그(`OwnerTestGame` 등), 디버그 브로드캐스트 액션, 도구 파일 이름(`*-ownertest.sh`), 문서. 로컬 폴더 이름(`hyundaiapp-sawcar`)만 아직 그대로다.
- 결정한 것과 이유: 팀장이 올린 자리표시 README는 오너테스트 README로 바꿨다. 인수인계 양식(`docs/handoff/README.md`)과 `.gitignore`는 팀 공용이라 그대로 둔다. 예전 합성용 배경판 폴더 `ssowcar_background_assets/`는 더 쓰지 않아 저장소에서 제외했다.
- 바꾼 파일: 전체(첫 커밋)

### 2026-11-09
- 한 일: 시나리오 v1.2 확정 → 백엔드(신호·엔진·TTS) 구현과 에뮬레이터 실측 → Compose 레이어 합성 화면을 만들었다가 버리고 **상황별 생성 이미지 46장**(글씨까지 생성) 방식으로 교체. 차 대사를 네 문장으로 줄이고 화면 문구를 "실패/성공"으로 바꿈. 감열지에 핸들 열선 버튼 그림을 열 표시로 넣음.
- 결정한 것과 이유: 이미지 합성 대신 상황마다 한 장씩 두고 크로스페이드하는 쪽이 보기 좋고 단순하다. 글씨도 생성기가 쓰고(기둥 스프레이·감열지·야광 ◀ ◀ ▶), 앱은 상황마다 바뀌는 힌트·자막만 올린다. 괴물은 기둥을 본 뒤가 아니라 **시간 기준**으로 오고, 신호는 마지막에 와서 정답이 클리어다.
- 바꾼 파일: `app/` 전체, `docs/scenario-script.md`, `docs/signal-mapping.md`, `docs/image-list.md`, `tools/*.sh`, `README.md`
