# 인수인계 — 장호연 (팀장 · 기획·제출)

담당 경로: `shortform/` (공모전 숏폼 릴스), 저장소 설정·`CLAUDE.md`, 제출·릴리스

## 현재 상태 (2026-11-11 기준, 매번 덮어쓰기)

- **제출 규칙 변경(11-11 사이트 공지 「비공개 GitHub 저장소 제출 방법 및 접수 관련 추가 안내」, FAQ 7번)**: 심사용 계정 초대는 필요 없음. 접수 메일에 저장소 주소 + **최종 커밋 해시**를 적고, **11-16까지 저장소를 공개로 전환해 11-23까지 유지**해야 개발 산출물 가산점이 반영됨(공개가 어려우면 소스+APK 압축 파일 주소를 마감 전 메일로). README에 에뮬레이터 버전·화면 규격 필요. 서명 이미지 삽입·PDF 인정. 요약 도구로 읽었으니 발송 전 원문 직접 확인
- **기획서를 실제 앱에 맞춤(11-11 정정 완료, DOCX 재생성)**: 아래 작업 기록 참고. 기획서 빈칸은 이제 3곳 — 7번 **release APK 경로**(새로 빈칸으로 바꿈), 7번 시연 영상 링크, 8번 숏폼 링크. 디버그 APK가 138MB라 GitHub 일반 push 한도(100MB)를 넘음 → 이미지 WebP 변환 또는 GitHub Releases로 올린 뒤 경로를 적는다
- **기획서에 새로 적은 약속**: 5절 일정 "~12/2"에 "문이 잠긴 채 시작했을 때의 안내 문구"를 넣음(지금 앱에는 없음) → 이민하에게 전달 필요
- **기획서에서 아직 안 고친 것(앱 내용·방향 결정이 필요해 손대지 않음)**: 결말 대사 "문은 내가 잠갔어"(앱 TTS와 같이 바꿔야 함), 내연기관 차·지하주차장 페르소나와 시동 상태 안내, 6절 사업성, "첫 사례" 문장. 근거는 팀 폴더 `contest-critical-review-2026-11-11.md` 3절
- `docs/signal-mapping.md`(이민하)에는 "독서등", "1초 유지", 마지막 OFF 요구가 그대로 있어 코드·기획서와 다름 → 이민하에게 알릴 것

- 한 일 요약: 숏폼 챌린지용 세로 릴스 "오너 테스트"(진행자 스마일)를 **녹음만 빼고 편집 완료**했다. 최종본 `shortform/videos/final/ownertest-final-no-voice.mp4`(1080×1920, 42.9초)에 자막·미션 그래픽·효과음·BGM이 들어 있다. 자세한 구성은 [shortform/README.md](../../shortform/README.md).
- 저장소: `hoyeoni1/hyundaiapp-ownertest`(비공개). 팀원 4명을 write로 초대했고 minalee22만 수락함(wklee316·giljong·Aodlf123 대기, 2026-11-10 기준). 작업 규칙은 `CLAUDE.md`(코드는 브랜치·PR, 문서는 `main`, 머지는 장호연).
- 기획서: [docs/proposal.md](../proposal.md)(시나리오 v1.2 기준, 차슐랭·크루즈브레이커와 같은 8칸 형식) → `docs/submission/ownertest-application.docx`(18쪽, 에뮬레이터 화면 10장). 다시 만들기: `python tools/build-proposal-docx.py docs/submission/application-form-template.docx docs/proposal.md "오너테스트 (OwnerTest)" docs/submission/ownertest-application.docx`. 빈칸: 7번 시연 영상 링크, 8번 숏폼 링크, 01 참가신청서·04 서명(손으로).
- 진행 중 브랜치·PR: 없음. 저장소에는 **문서만** 커밋했다. 영상·이미지·스크립트(약 90MB)는 로컬 `shortform/`에만 있고 git에 올리지 않았다.
- 다음 할 일(우선순위 순):
  1. 대사 녹음 14줄(운전자 3, 스마일 11). 타이밍은 `shortform/videos/final/voice-cue-timing.srt`
  2. 녹음을 영상에 얹기. BGM을 3~6dB 낮춰 대사가 묻히지 않게
  3. 기획서 7·8번 칸 링크 기입 후 DOCX 재생성. 7번 칸에 적은 `apk/SamgakGorae_OwnerTest_release.apk`를 서명 빌드로 만들어 올리기(아직 없음, `tools/build-release.sh`는 오너테스트를 모름)
  4. 앱 이미지 용량(47장, 약 90MB) 제출 제한 확인. 필요하면 WebP 변환을 이민하에게 요청
  5. 해시태그 `#HMG차량용앱개발공모전 #PleosAppMarket #HMG앱공모전`로 게시 후 링크 제출. **마감 2026-11-15**
- 막힌 점·질문(누구에게): 없음
- 주의(함정·직접 겪은 이상한 동작):
  - Higgsfield 잔여 크레딧 약 5 → 영상 재생성 불가. 컷을 다시 뽑아야 하면 충전 필요.
  - Kling 영상은 프롬프트에 "차 정지·고정 카메라"를 넣지 않으면 배경이 흘러 차가 움직이는 것처럼 보인다(첫 cut03이 그래서 폐기됨).
  - 효과음·BGM은 저작권 문제를 피하려고 ffmpeg로 직접 합성했다. 소리는 레벨 수치로만 확인했으니 귀로 한 번 들어볼 것.
  - 영상 속 앱은 시동을 막거나 차를 제어하지 않는다는 전제(엔딩 안전 고지)를 유지할 것.
  - 에뮬레이터 화면 캡처 때 크롱(CruiseBreaker) 앱이 문 잠금 신호에 앞으로 튀어나와 화면을 가린다. `adb shell am force-stop com.hyundaiapp.crong` 후 오너테스트를 다시 앞으로 띄우고 찍는다.
- 확인 방법: `python shortform/scripts/build_final.py`(필요: `pip install imageio-ffmpeg`)로 최종본·SRT·효과음 파일이 다시 만들어진다. 약 45초.

## 작업 기록 (최신이 위, 지우지 않음)

### 2026-11-11 — 기획서를 실제 앱에 맞게 정정(장호연 "기획서 정정 진행")
- 한 일: `docs/proposal.md`에서 코드와 다른 문장 14곳을 고치고 DOCX 재생성(그림 10장 그대로, 고친 문장이 들어간 것은 DOCX 본문 텍스트로 확인. Word로 열어 보지는 않음). 앱 코드 변경 없음
- 고친 것(근거는 `game/GameEngine.kt`, `ui/GameScreen.kt`, `signals/PleosCarSignals.kt`, 매니페스트):
  - 3절 F2 제목 "화면 속 차 = 지금 앉은 차" → "화면 속 차가 실제 차를 따라간다"(그림은 범용 실내)
  - 3·4절 시작 장면의 "화면이 꺼지고/암전되고" → "화면이 바뀐다"(암전 연출 없음), 4절 "긁는 소리와 함께" 삭제(효과음 없음)
  - 5절 SDK 표: "실내등·독서등" → "실내등", 권한에 `TTS_SERVICE` 추가
  - 5절 판정 규칙: 시작(잠긴 채 열면 풀었다 다시 잠가야 함, 안내 문구는 2차 PT까지), 상태 판정("1초 연속 유지" → 바로 반영), 감열지(열선 10초 뒤 드러남, 읽으려면 실내등), 기둥(제상 10초 뒤 김 걷힘), 신호(세 번째 오른쪽을 켜는 순간 클리어)
  - 5절 개인정보("기기에 저장" → 저장하지 않음), 공포 수위("피 묘사 없음" → 사람이 다치는 장면 없음, 기둥 글씨는 붉은 스프레이 페인트)
  - 7절: release APK 경로를 【제출 전 기입】 빈칸으로, 결과 화면 그림 설명에 시연용 타이머 표시
- 결정한 것과 이유: 앱과 다른 사실만 고쳤다. 결말 대사·페르소나·사업성처럼 방향을 바꾸는 것은 장호연 결정이 필요해 그대로 둠
- 바꾼 파일: `docs/proposal.md`, `docs/submission/ownertest-application.docx`, 이 문서

### 2026-11-11 — 세 작품 심사위원 관점 검토, 제출 규칙 공지 확인(장호연 "3개 다 비판적으로 평가해서 개선안 보고해줘")
- 한 일: 차슐랭·크롱·오너테스트의 기획서·화면·코드를 읽기 전용으로 검토(오너테스트는 단위 테스트 16개 재실행 통과). 보고서는 팀 폴더(git 아님) `contest-critical-review-2026-11-11.md`에 두고 저장소에는 넣지 않음(11-16 공개 전환 때문). 앱 코드·기획서 변경 없음
- 새로 확인한 것: 위 "현재 상태"의 제출 규칙 변경, 기획서와 앱이 다른 곳
- 이민하에게 알릴 것(앱 담당): 문이 잠긴 채 앱을 열면 진행 방법이 화면에 없음(`ui/SystemScreens.kt` 타이틀), 기어 확인이 차량 신호가 바뀔 때만 돎(`game/GameEngine.kt` `onCarState`), 디버그용 브로드캐스트 리시버가 release에서도 열려 있음(`MainActivity.kt`), README에 APK 위치·테스트 환경(지금은 다른 저장소 문서 참조)
- 바꾼 파일: 이 문서

### 2026-11-10 (기획서)
- 한 일: 기획서 `docs/proposal.md` 작성(시나리오 v1.2·신호 대응표 기준, PRD v0.6은 쓰지 않음), 차슐랭의 `tools/build-proposal-docx.py`와 양식을 가져와 DOCX 생성. 디버그 APK를 에뮬레이터에 설치해 `tools/shots-ownertest.sh`로 장면을 찍고 앱 영역만 잘라 `docs/images/ot-*.png`로 넣음(1024px, 256색). 단위 테스트·빌드 통과.
- 결정한 것과 이유: 기획 배경 수치는 출처가 있는 것만(Talker Research·LIQUI MOLY 2024 조사). MAU·Retention은 PRD v0.6의 가정(출고 첫 달 30%, 누적 월 3%, 30일 25%)을 그대로 씀. 팀명은 삼각고래.
- 바꾼 파일: `docs/proposal.md`, `docs/images/`, `docs/submission/`, `tools/build-proposal-docx.py`, 이 문서

### 2026-11-10
- 한 일: Higgsfield(GPT Image 2 + Kling 3.0)로 8컷 생성, ffmpeg 편집으로 최종본·녹음 큐시트·효과음/BGM 스템 제작. 작업물을 `ai-idol` 폴더에서 이 저장소의 `shortform/`으로 옮기고, 한글 파일명을 영문으로 바꿨다(`ownertest-final-no-voice.mp4`, `ownertest-sfx-bgm.wav`, `voice-cue-timing.srt`, `edit-guide.md`). 빌드 스크립트 경로를 스크립트 위치 기준으로 고쳐 새 위치에서 다시 빌드되는 것을 확인했다.
- 결정한 것과 이유: 쏘우카 → 오너 테스트(쏘카 브랜드와 겹침, "내 차 탈출"이 어색함). 시동 불가 장면은 "브레이크 없이 눌러 ACC만 켜짐"으로(앱이 시동을 막는 건 불가능). AUTO HOLD는 D + 브레이크, 차로 유지 보조는 흰색 대기 아이콘으로(실제 동작 조건에 맞춤). 대사는 AI 음성 대신 직접 녹음. 대용량 미디어는 git에 넣지 않음.
- 한 일(저장소): 저장소 생성(README·`.gitignore`·인수인계 양식), 팀원 4명 초대, `CLAUDE.md` 추가(차슐랭·크루즈브레이커와 같은 규칙, 담당은 이민하가 저장소 전체).
- 바꾼 파일: `CLAUDE.md`, `shortform/README.md`, `shortform/videos/edit-guide.md`, `README.md`(숏폼 링크 한 줄), `.gitignore`(`shortform/scripts/_work/`), 이 문서
