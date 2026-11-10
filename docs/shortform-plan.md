# 숏폼 챌린지 영상 계획 (이민하 제안안)

> **상태: 제안.** 숏폼 담당은 장호연이고, [shortform/README.md](../shortform/README.md)에 진행자 "스마일"이 네 가지 테스트(시동·운전자세 메모리·차로 유지 보조·AUTO HOLD)를 내는 42.9초 영상이 녹음만 남기고 완성되어 있다. 이 문서는 **앱의 실제 내용(공포 탈출, 실내등·열선·제상·전조등·브레이크·방향지시등)에 맞춘 대안**이며, 어느 쪽을 제출할지는 장호연과 상의해 정한다.

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
| 밝기 | 소재가 매우 어둡다(평균 5~10/255). 휴대폰 자동 밝기와 Shorts 압축에서 검정이 뭉개지므로 최종본은 **그림자 +10~15, 검정 바닥을 0이 아니라 8~10/255**에 두고 그레인을 얹어 밴딩을 막는다. 가장 어두운 컷은 휴대폰에서 밝기 50%로 한 번 확인 |
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

### 손 클립용 밝은 판 2장 (2026-11-11 추가)

늘려 만든 6장은 평균 밝기 5~10/255, 아래쪽 45%(손이 움직일 자리)는 2~4/255로 사실상 검정이다. 이 프레임으로 손 클립을 만들면 손이 보이지 않고, 편집에서 밝기를 올려도 정보가 없어 노이즈만 올라온다. 손 클립 ①⑤는 **센터 화면이 켜진 판**을 따로 만들어 첫 프레임으로 쓴다(앱이 센터 화면에서 돌아간다는 설정과도 맞다).

- `v_f_dark_fog100_hand` ← `v_f_dark_fog100.png` 업로드 — `Keep the composition and everything in the image exactly the same, but the center screen is now ON, showing a very dark app screen with a faint teal glow, and the instrument cluster rings glow faint teal. This light softly illuminates the steering wheel, both stalks, the lower dashboard and the driver's footwell so their shapes are clearly readable. Keep the windshield fogged and the garage outside dark. Still nighttime and moody, but the lower half of the frame is about three stops brighter than before. No text, no people.`
- `v_f_dark_beam_fog0_hand` ← `v_f_dark_beam_fog0.png` 업로드 — `Keep the composition, the pillar and the red graffiti exactly the same, but the center screen is now ON with a faint teal glow, the cluster rings glow faint teal, and warm light bounced back from the headlit pillar softly lights the steering wheel, both stalks and the lower dashboard so their shapes are clearly readable. Still nighttime and moody, lower half about three stops brighter than before. No text other than the existing graffiti, no people.`

확인 기준: 아래쪽 45% 평균 밝기 20~35/255, 95퍼센타일 60 이상. 핸들 림과 레버 윤곽이 썸네일 크기에서도 보여야 한다.

늘리기가 안 되면 아래 프롬프트로 새로 만든다(일관성은 떨어짐).

- `v_f_dark_fog100` — First-person view from the driver's seat of a parked car at night in an underground parking garage, vertical framing that shows the headliner and dome light above and the dashboard and dark footwell below. Steering wheel, dashboard with a dark center screen, rearview mirror. The windshield is completely fogged over with condensation; nothing outside is visible. Interior nearly black, only a faint dashboard glow. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_f_dark_beam_fog100` — Same vertical driver's seat view. The windshield is completely fogged over; the headlights are on, a diffuse white glow smeared across the fogged glass, nothing outside readable. Interior nearly black. Grainy cinematic photo, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_f_dark_beam_fog0` — Same vertical driver's seat view. The windshield is clear; the headlights are on, lighting a flat gray concrete pillar directly ahead. On the pillar's flat face, large rough dripping red spray-painted Korean (Hangul) graffiti on two lines reading exactly "그것은 빨간 불빛을" / "무서워해". Wet floor reflections. Interior nearly black. Grainy cinematic photo, desaturated teal and amber, heavy shadows, 9:16.
- `v_m_monster1` — View through a car's rearview mirror with the mirror frame visible, vertical framing with the dark headliner above and the dark windshield below the mirror: a dark concrete parking garage aisle behind the car, pillars receding, wet floor, one distant dim lamp. Far down the aisle a tall dark featureless humanoid silhouette stands, barely distinguishable from the dark. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_m_monster3` — Same vertical rearview mirror view. A tall dark featureless humanoid silhouette stands a few car lengths behind, clearly taller than a person. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_m_monster5` — Same vertical rearview mirror view. The silhouette is right behind the car, filling most of the mirror, no face, only the wet floor visible at the bottom. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.

## Veo 클립 5개 (Flow, Veo 3.1 Fast, 9:16, 8초, 각 ≈20크레딧)

| # | 첫 / 끝 프레임 | 참조 | 프롬프트 |
|---|---|---|---|
| ① 더듬는 손 | v_f_dark_fog100_hand / — | s_lit, f_dark_fog0 | `POV from the driver's seat of a dark car at night, windshield fogged. The center screen and the cluster rings glow faint teal and are the only light. The same right hand, dark long sleeve, no watch, no rings, enters from the lower right, catches the teal light, gropes across the dashboard, touches the stalk, presses two buttons; nothing happens except faint clicks. The hand and the wheel stay clearly readable in the dim light. Handheld, slightly unsteady. Audio: breathing, small clicks, no music. Grainy cinematic, low key but legible, desaturated teal and amber.` |
| ② 기둥 | v_f_dark_beam_fog100 / v_f_dark_beam_fog0 | — | `Driver's seat view, headlights on. Condensation on the windshield clears from the center outward until a concrete pillar with red spray-painted Korean graffiti is fully visible. The letters must match the final frame exactly. Static camera. Audio: low wind, then silence. Grainy cinematic, desaturated teal and amber, heavy shadows, no music.` |
| ③ 접근 1 | v_m_monster1 / v_m_monster3 | — | `Rearview mirror view of a dark parking garage aisle. A tall featureless silhouette walks slowly toward the car, growing larger, no face. Static camera, faint flicker from a distant lamp. Audio: faint scraping, no music. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ④ 접근 2 | v_m_monster3 / v_m_monster5 | — | `Same rearview mirror. The silhouette comes close behind the car until it fills most of the mirror, no face, slight sway. Audio: scraping grows louder, a slow heartbeat, no music. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ⑤ 급한 손 | v_f_dark_beam_fog0_hand / — | s_lit, f_dark_fog0 | `POV from the driver's seat, headlights lighting a concrete pillar ahead; warm light bounces back into the cabin and the center screen glows faint teal. The same right hand, dark long sleeve, no watch, no rings, frantically flips the stalk, jabs buttons, grabs the wheel; quick nervous movements, the hand clearly readable in the bounced light. The graffiti on the pillar stays exactly as in the first frame. Audio: rapid clicks, breathing, no music. Grainy cinematic, low key but legible, desaturated teal and amber.` |

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
