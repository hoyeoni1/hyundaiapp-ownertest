# 숏폼 챌린지 영상 계획

- 작성 2026-11-10. 규칙: 30초~1분 세로, Reels/Shorts, 해시태그 `#HMG차량용앱개발공모전 #PleosAppMarket`(총 2~3개), 접수양식 8번에 링크, 마감 11.15(일) 23:59. 가산점 없음, 별도 특별상.
- 컨셉: 차가 플레이어를 가뒀다. 대사는 두 줄뿐. 1인칭으로 헤매다 기둥을 보고, 괴물이 다가오고, 암전. 그 뒤 실제 플레이 화면과 한 문장, 로고.
- 소리: 음악 없음. 철컥·딸깍·바람·긁는 소리·심장·쿵·웅웅. 차 목소리는 내비 안내 톤 TTS(변조 없음).
- 남기는 것(스포일러 금지): 쪽지 내용, ◀ ◀ ▶, 신호, 새벽, 차가 왜 가뒀는지.

## 콘티 (약 40초, 1080×1920)

| 시간 | 화면 | 소리 | 글 | 소스 |
|---|---|---|---|---|
| 0~2 | 검정 | 철컥 → "게임을 시작하지." | **게임을 시작하지.** (0.3초에) | 편집 + TTS |
| 2~8 | 1인칭, 칠흑의 차 안. 손이 대시보드·레버·버튼을 더듬음. 아무 일 없음 | 숨소리, 딸깍 | 없음 | Veo ① |
| 8~12 | 제상 버튼 → 레버 → 전조등. 김이 가운데부터 걷히며 기둥 | 바람 → 정적 | (기둥 글씨) | Veo ② |
| 12~16 | 룸미러: 통로 끝 실루엣, 가까워짐 | 긁는 소리 시작 | 없음 | Veo ③ |
| 16~19 | 룸미러, 실루엣 멈춤 | "너는 설명서를 제대로 읽지 않았지…" | **너는 설명서를 제대로 읽지 않았지…** | Veo ③ 끝 프레임 정지 + TTS |
| 19~24 | 0.5초 교차: 실루엣 더 가까이 / 급한 손 / 거울을 채움 / 손 | 긁는 소리 커짐, 심장 | 없음 | Veo ④ ↔ Veo ⑤ |
| 24~25 | 암전 | 쿵, 정적 1초 | 없음 | 편집 |
| 25~33 | 실제 플레이 3~4컷: 핸들 확대 쪽지(글씨 안 읽히는 거리) → 룸미러 괴물 → 브레이크 붉은빛에 물러남 → 왼쪽 미러 손전등 | 낮은 웅웅 | 없음 | 에뮬레이터 녹화 |
| 33~37 | 검정, 한 글자씩 | 정적 | 당신은 차에 대해 얼마나 알고 있지? | 편집 |
| 37~40 | 로고 + 한 줄 | 철컥 → 루프 | **오너테스트** / 설명서를 체화하는 차 안의 방탈출 | `docs/logo/` |

## 일관성 — 영상이 앱과 같은 세계로 보이게

| 방법 | 내용 |
|---|---|
| 앱 이미지를 프레임으로 | Veo 클립의 **첫·끝 프레임에 앱 이미지의 세로판**을 넣는다. 끝점은 정확히 그 그림이 되므로 컷이 바뀌어도 같은 차, 같은 주차장 |
| 세로판은 늘리기(outpaint) | 새로 그리지 말고 기존 1536×1024를 **위아래로 늘려** 9:16을 만든다. 차 실내는 픽셀 그대로, 천장과 바닥만 추가 |
| 참조 이미지(ingredients) | 프레임을 못 주는 손 클립(①⑤)에는 `s_lit`, `f_dark_fog0`, `f_dark_beam_fog0` 세로판 3장을 참조로 넣는다. 같은 핸들, 같은 대시보드 |
| 손 | 모든 손 클립에 같은 문장: `the same right hand, dark long sleeve, no watch, no rings` |
| 꼬리말 | 이미지와 같은 문장으로 끝낸다: `grainy cinematic, almost no light, desaturated teal and amber, heavy shadows, no music` |
| 이어 붙이기 | ③의 끝 프레임 = ④의 첫 프레임(m_monster3). 접근이 한 호흡으로 이어진다 |
| 후보정 | CapCut에서 모든 컷에 같은 색보정(틸·앰버, 대비 +)과 얇은 필름 그레인. 작은 차이는 그레인이 덮는다 |
| 한계 | Veo는 8초 안에서도 손 모양·글씨가 흔들릴 수 있다. 글씨가 든 ②는 끝 프레임을 앱 이미지로 고정해 마지막은 정확하게, 중간은 흐림으로 넘긴다. 손은 재시도 1~2번 예산 |

## 세로 이미지 6장 (Nano Banana, 기존 이미지 업로드 → 늘리기)

공통 프롬프트(업로드한 이미지에 적용):

`Extend this image to a 9:16 vertical frame. Keep every existing pixel exactly as it is; only add more [위] above and more [아래] below, matching the lighting, color and grain. No new objects, no people, no text.`

| 파일 | 원본 | [위] | [아래] |
|---|---|---|---|
| `v_f_dark_fog100` | f_dark_fog100 | headliner and dome light housing | dashboard, steering column and dark footwell |
| `v_f_dark_beam_fog100` | f_dark_beam_fog100 | 위와 같음 | 위와 같음 |
| `v_f_dark_beam_fog0` | f_dark_beam_fog0 | 위와 같음 | 위와 같음 |
| `v_m_monster1` | m_monster1 | dark headliner above the mirror | the mirror's stem and the dark windshield below |
| `v_m_monster3` | m_monster3 | 위와 같음 | 위와 같음 |
| `v_m_monster5` | m_monster5 | 위와 같음 | 위와 같음 |

늘리기가 안 되는 도구라면 [docs/image-list.md](image-list.md)의 해당 프롬프트에서 `3:2`를 `9:16`으로 바꿔 새로 만든다(일관성은 떨어짐).

## Veo 클립 5개 (Flow, Veo 3.1 Fast, 9:16, 8초, 각 ≈20크레딧)

| # | 첫 / 끝 프레임 | 참조 | 프롬프트 |
|---|---|---|---|
| ① 더듬는 손 | v_f_dark_fog100 / — | s_lit, f_dark_fog0 | `POV from the driver's seat of a car in near-total darkness, windshield fogged. The same right hand, dark long sleeve, no watch, no rings, gropes across the dashboard, touches the stalk, presses two buttons; nothing happens except faint clicks. Handheld, slightly unsteady. Audio: breathing, small clicks, no music. Grainy cinematic, almost no light, desaturated teal and amber, heavy shadows.` |
| ② 기둥 | v_f_dark_beam_fog100 / v_f_dark_beam_fog0 | — | `Driver's seat view, headlights on. Condensation on the windshield clears from the center outward until a concrete pillar with red spray-painted Korean graffiti is fully visible. The letters must match the final frame exactly. Static camera. Audio: low wind, then silence. Grainy cinematic, desaturated teal and amber, heavy shadows, no music.` |
| ③ 접근 1 | v_m_monster1 / v_m_monster3 | — | `Rearview mirror view of a dark parking garage aisle. A tall featureless silhouette walks slowly toward the car, growing larger, no face. Static camera, faint flicker from a distant lamp. Audio: faint scraping, no music. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ④ 접근 2 | v_m_monster3 / v_m_monster5 | — | `Same rearview mirror. The silhouette comes close behind the car until it fills most of the mirror, no face, slight sway. Audio: scraping grows louder, a slow heartbeat, no music. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ⑤ 급한 손 | v_f_dark_beam_fog0 / — | s_lit, f_dark_fog0 | `POV from the driver's seat, headlights lighting a concrete pillar ahead. The same right hand, dark long sleeve, no watch, no rings, frantically flips the stalk, jabs buttons, grabs the wheel; quick nervous movements. Audio: rapid clicks, breathing, no music. Grainy cinematic, almost no light, desaturated teal and amber, heavy shadows.` |

예산: 5클립 100 + 재시도(①⑤ 위주) 100 = 월 200 안.

## 실제 플레이 녹화 (25~33초)

1. 에뮬레이터 실행, 앱 설치, `bash tools/vconsole.sh up`
2. OBS로 에뮬레이터 창 녹화(앱 영역만 크롭: 오른쪽 2/3) — 소리는 쓰지 않으므로 `adb shell screenrecord`도 가능
3. `adb shell am broadcast -a com.hyundaiapp.ownertest.DEBUG --es action shots`로 타이머를 줄인 뒤 `tools/ownertest-console.html`로 조작: 실내등 → 핸들 탭(쪽지) → 룸미러 탭(괴물) → 브레이크 → 왼쪽 끝 → 사이드미러
4. CapCut에서 가로 컷을 세로 캔버스 가운데에, 위아래는 검정

## 차 목소리 (TTS 2문장)

- "게임을 시작하지." — 낮고 평탄하게, 끝을 내리고 1초 쉼
- "너는 설명서를 제대로 읽지 않았지…" — 같은 톤, 말끝을 길게
- 내비 안내 음성에 가까운 차분한 목소리. 변조·에코 없음. CapCut TTS 또는 에뮬레이터 Pleos TTS 녹음(`--es action speak --es text '…'`)

## 로고

[docs/logo/ownertest-logo.svg](logo/ownertest-logo.svg) — 시안 3종(가로·세로·마크). 마크는 핸들 테 안의 자물쇠. 색은 앱과 같은 앰버 `#e0a450` / 먹색 `#0b0e12`. 영상 끝 카드는 세로 시안(B)을 쓴다.

## 일정

| 날 | 할 일 |
|---|---|
| 11.10 | 세로 이미지 6장(늘리기), 로고 확정 |
| 11.11 | Flow 클립 5개 + 재시도, 에뮬레이터 플레이 녹화, TTS 2문장 |
| 11.12 | CapCut 편집: 컷·자막·효과음·색보정·그레인. 훅 자막 타이밍 확인 |
| 11.13 | Reels·Shorts 게시(해시태그 2개), 공개 설정 확인 |
| 11.14 | 접수양식 8번에 링크 기재 |
