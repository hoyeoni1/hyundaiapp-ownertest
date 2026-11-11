# CLAUDE.md

오너테스트(OwnerTest) — Pleos Connect 정차 전용 차량 공포 탈출 게임. 2026 현대자동차그룹 앱 개발 공모전 출품작. 개요·코드 구조·실행 방법은 [README.md](README.md), 시나리오 기준 문서는 [docs/scenario-script.md](docs/scenario-script.md)(v1.2). `PRD.md`는 v0.6 구판이라 시나리오와 다르면 시나리오를 따른다.

## 파일·폴더 이름 규칙
- 파일명과 폴더명에 **한글을 쓰지 않는다.** 영문 소문자·숫자·하이픈(`-`)만 쓴다. 예: `scenario-script.md`, `e2e-ownertest.sh`
  - 이유: git·GitHub URL·터미널·Windows 콘솔(cp949)에서 한글 파일명이 깨지거나 이스케이프되어 링크와 명령이 꼬인다.
- 문서 **내용**은 한글로 써도 된다. 제목도 한글 가능.
- 기존 관례가 있는 이름은 그대로 둔다: `README.md`, `CLAUDE.md`, `PRD.md`, Gradle 프로젝트 이름(`OwnerTest`), 코드 파일의 언어별 관례.
- 이미지 리소스는 Android 규칙대로 영문 소문자·숫자·밑줄(`_`)만 쓴다(`res/drawable-nodpi/f_dark_fog0.webp`). 이름은 [docs/image-list.md](docs/image-list.md)와 맞춘다.
- 파일 이름을 바꾸면 그 파일을 가리키는 문서 속 링크·경로도 함께 고친다.

## 작업 방식 (사람·에이전트 모두)
- **담당: 이민하(`minalee22`)가 앱 전체**(`app/`, `docs/`, `tools/`), **장호연이 `shortform/`**(공모전 숏폼 릴스)와 저장소 설정·제출. 남의 영역을 고쳐야 하면 담당자에게 먼저 알린다.
- `shortform/`은 문서(`*.md`)만 커밋한다. 영상·이미지·스크립트는 로컬에만 둔다(`.gitignore`).
- **코드 작업은 브랜치에서 한다**(`feat/...`, `fix/...`). `app/`, `tools/` 같은 코드·스크립트는 `main`에 직접 커밋·푸시하지 않고 PR로 올린다.
- **문서는 PR 없이 `main`에 바로 커밋·푸시한다.** 대상: `PRD.md`, `README.md`, `CLAUDE.md`, `docs/` 아래 문서.
- **PR 머지는 장호연만 한다.** 에이전트는 PR을 머지하지 않는다(리뷰 코멘트만).
- **시작할 때** `docs/handoff/`에서 내 파일과 건드릴 영역 담당자의 파일을 먼저 읽는다.
- **끝낼 때 인수인계 문서 갱신은 필수다.** `docs/handoff/<영문-이름>.md`의 "현재 상태"를 덮어쓰고 "작업 기록"에 날짜 항목을 추가한다. 양식·규칙: [docs/handoff/README.md](docs/handoff/README.md)
- 에이전트는 누구의 작업을 하는지 확인하고, 그 사람의 파일에 기록한다. 기록 없이 작업을 끝내지 않는다.

## 앱 규칙 (바꾸려면 장호연과 먼저 상의)
- 앱은 차를 **읽기만** 한다. 제어 API와 `CONTROL_*` 퍼미션을 쓰지 않는다. 문도 잠그지 않는다.
- **정차 전용**(DO 미선언). 기어가 P를 벗어나면 엔진이 멈춘다.
- `game/GameEngine.kt`는 Android 의존 없는 순수 Kotlin으로 유지한다. 엔진 동작을 바꾸면 `GameEngineTest.kt`도 함께 고친다.
- 서버·계정·개인정보 수집 없음.

## 확인 방법
- 단위 테스트·빌드: `./gradlew :app:testDebugUnitTest :app:assembleDebug`
- 제출용 release APK: `bash tools/build-release.sh` → `apk/SamgakGorae_OwnerTest_release.apk` (서명 값은 `local.properties`의 `release.*`, 저장소에 올리지 않음)
- 에뮬레이터 전 과정: `bash tools/vconsole.sh up` → `bash tools/e2e-ownertest.sh` (약 2분)
- 장면 스크린샷: `bash tools/shots-ownertest.sh <폴더>`
- 개발 환경(JDK 17, Android SDK, 에뮬레이터 `Pleos_Connect_2_1_0`)은 CruiseBreaker `docs/dev-env-setup-log.md`와 같다.

## 함정 (README·인수인계에서 옮김)
- `object Img`의 리소스 id를 `const val`로 두면 런타임 `NoSuchMethodError`가 난다 → getter로 둔다.
- 이미지는 `ImageBitmap.imageResource`가 아니라 `BitmapFactory`로 읽는다(디코딩 실패 때 죽음).
- 이미지 46장은 WebP(품질 98, 합계 약 13MB)로 넣는다. PNG 원본은 장당 1.5~2.4MB(합계 약 89MB)라 그대로 넣으면 APK가 GitHub 한도(100MB)를 넘는다. 새 이미지도 WebP로 바꿔 넣고, `tools/build-release.sh`의 용량 확인을 통과하는지 본다.
- 앱은 에뮬레이터 화면(2560×1440)의 오른쪽 2/3(≈1707×1219)만 쓴다. 3:2 이미지는 양옆이 조금 잘린다.
- 콘솔 값 이름은 [docs/signal-mapping.md](docs/signal-mapping.md)의 실측 결과를 따른다.
