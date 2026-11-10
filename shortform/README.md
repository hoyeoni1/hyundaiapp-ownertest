# 오너 테스트 — 숏폼 릴스 (HMG 차량용 앱 개발 공모전 숏폼 챌린지)

- 가이드: https://appdevchallenge.auto-hmg.io/guide/shortform · **마감 2026-11-15**
- 세로 9:16, 30초~1분. 필수 해시태그 `#HMG차량용앱개발공모전` (+ `#PleosAppMarket` `#HMG앱공모전`)
- 수상작은 시상식에서 상영될 수 있음 → 저작권·초상권은 참가자 책임 (효과음·BGM은 직접 합성해서 문제 없음)

## 현재 상태 (2026-11-10)

**녹음만 빼고 편집 완료.** 남은 일: ① 대사 녹음 → ② 영상에 얹기(BGM 3~6dB 낮추기) → ③ 게시 + 링크 제출.

| 파일 | 내용 |
|---|---|
| `videos/final/ownertest-final-no-voice.mp4` | 1080×1920 · 30fps · 42.9초 · 자막/미션 그래픽/효과음/BGM 포함 · -19.6 LUFS |
| `videos/final/voice-cue-timing.srt` | 대사 14줄의 시작·끝 시간 (녹음 큐시트) |
| `videos/final/ownertest-sfx-bgm.wav` | 효과음+BGM만 분리한 스템 |

## 콘셉트

- 진행자 **스마일**: 하얀 웃는 가면(감은 눈 초승달, 크게 웃는 입), 검은 정장, 검은 장갑, 머리 크기 보통. 살짝 압도적이지만 공포는 아님. (빨간 볼·나비넥타이 금지 — 직쏘/빌리와 비슷해짐)
- 운전자: 20대 한국 남성, 회색 후드티. 새 차 받은 첫날.
- 슬로건: **"설명서 대신, 테스트로."**
- 원칙: 1~7컷은 차가 **완전히 정지**. 앱은 차를 제어하거나 시동을 막지 않음 (엔딩에 안전 고지).

## 컷 구성 (최종 편집 기준, 시작 시각)

| # | 시작 | 파일 / 사용 구간 | 내용 | 대사 |
|---|---|---|---|---|
| 1 | 0.00 | cut01-engine 0–5s | 브레이크 없이 시동 버튼 → ACC만 켜짐 | 운전자 "어?"(3.3s) "왜 안 돼?"(4.4s) |
| 2 | 5.00 | cut02-dashboard 0.8–2.6s, 1.4배 슬로 | 대시보드 화면에 스마일 | 운전자 "…뭐야?" |
| 3 | 7.52 | cut03-mirror-v2 0–5s, 4/3배 슬로, 105→122% 확대, 차갑게 | 룸미러에 스마일 | 스마일 "안녕, 새 주인." "시동도 못 거는군…" "오너 테스트를 시작하지." |
| 4 | 14.19 | cut04-test1-start 0–4s | TEST 1 · 시동 | "브레이크를 밟고, 다시." |
| 5 | 18.19 | cut05-test2-seat 0–5s | TEST 2 · 운전자세 메모리 (P) | "너의 자세를 기억시켜라." |
| 6 | 23.19 | cut06-test3-lane 0–4s | TEST 3 · 차로 유지 보조 (흰색 대기 아이콘) | "차선을 지켜줄 버튼을 찾아라." |
| 7 | 27.19 | cut07-final-autohold 0–4s, 1.25배 슬로 | FINAL · AUTO HOLD (D + 브레이크) | "마지막. D에 놓고," "브레이크를 밟은 채로." "발을 떼도 멈춰 있지?" |
| 8 | 32.19 | cut08-depart 0–5s, 1.25배 슬로, 따뜻하게 | PASS → 출발 | "합격." "이제 진짜 오너야." |
| 엔딩 | 38.44 | 08-ending.png 4.5s | 로고 · 슬로건 · 해시태그 · 안전 고지 | — |

정확한 대사 타이밍은 `videos/final/voice-cue-timing.srt`.

## 폴더

```
shortform/
├─ 01~08-*.png          장면 원본 이미지 (*-v2가 최종: 03, 04, 06, 07)
├─ videos/cut0N-*.mp4   Higgsfield Kling 3.0 생성 컷 (720×1280). cut03-mirror.mp4는 배경이 움직여서 미사용
├─ videos/preview-merged.mp4   자막 없는 단순 이어붙이기 미리보기
├─ videos/edit-guide.md      초기 편집 가이드 (구간·타이밍은 위 표가 최신)
├─ videos/final/        최종 결과물
├─ scripts/build_final.py      최종본 빌드 (컷 자르기·슬로·색·확대·자막·미션 그래픽·효과음/BGM 합성·SRT)
├─ scripts/build_preview.py    미리보기 빌드
└─ source/smile-eight-scenes-v2.zip   GPT로 뽑은 장면 이미지 원본 압축
```

## 다시 빌드하기

```bash
python scripts/build_final.py
```

- 필요: `pip install imageio-ffmpeg` (ffmpeg를 PATH 대신 이 패키지의 바이너리로 씀), 맑은 고딕 폰트(Windows 기본)
- 중간 파일은 `scripts/_work/`에 생김. 효과음 중 노이즈가 랜덤이라 wav는 매번 미세하게 달라짐(들리는 차이 없음)
- 자막 문구·컷 길이·색은 스크립트 상단의 `clips` 목록과 자막 정의에서 수정

## Higgsfield 메모

- 잔여 크레딧 약 5 (총 약 67 사용) → **영상 재생성은 사실상 불가**
- 영상: `kling3_0` std, 9:16, start_image. 5초 무음 7.5cr / 소리 켜면 10cr
- 프롬프트에 꼭 넣을 것: "The car is parked and completely stationary. Locked-off static camera, no camera movement, no zoom. Background does not move at all." 확대(push-in)는 편집에서.
- 미디어 ID: 01 `b46ba0e6-7fba-43c9-a4c4-c1a3ab7459a5` · 02 `1a6aacb8-1c23-43c3-bb35-430a333afa0c` · 03 `bc69b9fe-e713-4534-843d-9bc1a1f4bc4a` · 05 `6b592211-31e6-4b4b-b813-fb466e6e6cf2` · 07v2 `a31f37aa-3a2a-4301-975b-4889e5bddf56`

## 실제 기능 검토 결과 (시나리오에 반영됨)

- 앱이 시동을 막는 건 불가능 → 훅은 "브레이크 없이 눌러서 ACC만 켜짐"으로
- AUTO HOLD는 D/N/R에서만 동작 → FINAL에서 D + 브레이크
- 차로 유지 보조 아이콘은 보조 중일 때만 초록 → 정차 상태라 흰색 대기 아이콘
- 운전자세 메모리는 P에서 저장
