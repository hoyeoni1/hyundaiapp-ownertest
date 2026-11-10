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
| 2~8 | 1인칭, 칠흑의 차 안. 센터 화면의 희미한 빛에만 손이 보임. 더듬다가 천장 스위치를 찾아 **실내등이 켜진다**(약 5초). 그 뒤로는 실내등 켠 상태 유지 | 숨소리, 딸깍, 마지막 딸깍 | 없음 | Veo ① (어둠 → 실내등) |
| 8~12 | 제상 버튼 → 레버 → 전조등. 김이 가운데부터 걷히며 기둥 (실내등 켠 상태) | 바람 → 정적 | (기둥 글씨) | Veo ② |
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

**POV 판 4장 — 운전자의 몸이 보이게** (2026-11-10 검토 둘: ① 어두운 판은 평균 밝기 5/255, 아래 절반 3/255라 손이 안 보인다 → 손 장면은 실내등 켠 상태. ② 만든 정면 판에는 사람이 없다. 빈 차에 손을 "등장"시키라고 하면 Veo가 손을 안 그리거나 떠 있는 손을 그린다 → **첫·끝 프레임에 핸들을 잡은 손과 무릎을 미리 넣는다.** 아래의 검은 바닥도 무릎이 채운다)

공통 몸 묘사(모든 판에 같은 문장): `the driver's body in first-person view: dark jeans on the lap at the bottom of the frame, two forearms in a dark long-sleeve jacket, hands with no watch and no rings`

손 장면(①⑤)은 **타이트 POV**(핸들·계기판·센터 콘솔이 화면을 채우고 위아래는 어둠으로 떨어짐), 기둥 장면(②)만 **와이드 POV**(앞유리가 보여야 함). 둘 다 같은 소매·같은 손.

| 파일 | 원본 | 프롬프트 |
|---|---|---|
| `v_pov_tight_lit` | f_lit_fog100.png (3:2) | `Reframe this same car interior as a tighter 9:16 first-person shot from the driver's eye level, looking slightly down: the steering wheel, instrument cluster and center console fill the frame; keep the same materials, colors and warm dome light. Add [공통 몸 묘사], both hands on the steering wheel at the 9 and 3 positions. The top and bottom edges fall off into darkness. No text, nothing taped to the wheel.` |
| `v_pov_tight_dark` | v_pov_tight_lit (위 결과를 편집) | `Keep this image exactly as it is but turn the dome light off: the cabin is nearly black, the hands and the wheel lit only by a faint glow from the center display, the fogged windshield barely visible. No text.` |
| `v_pov_lit_beam_fog100_hands` | f_lit_beam_fog100.png (3:2) | `Extend this image to a 9:16 vertical frame keeping every existing pixel; add the lit dome light housing above and the dashboard, steering column and the driver's lap below. Add [공통 몸 묘사], both hands on the steering wheel at the 9 and 3 positions. Dome light on, headlights glowing on the fogged windshield. No text.` |
| `v_pov_lit_beam_fog0_hands` | f_lit_beam_fog0.png (3:2) | `Extend this image to a 9:16 vertical frame keeping every existing pixel; add the lit dome light housing above and the dashboard, steering column and the driver's lap below. Add [공통 몸 묘사], both hands on the steering wheel at the 9 and 3 positions. Dome light on, headlights on, the graffiti on the pillar unchanged. No text.` |

`v_pov_tight_dark`를 `v_pov_tight_lit`에서 만들면 손·핸들이 픽셀 단위로 같아 클립 ①의 첫·끝 프레임 기하가 일치한다(Veo는 손 동작과 빛 변화만 만들면 됨). 검수: 손가락 다섯 개, 소매 색이 네 장 모두 같은지, 와이드 두 장의 핸들 위치가 원본과 같은지. `v_f_dark_fog100`·`v_f_dark_beam_fog100`·`v_f_dark_beam_fog0`는 쓰지 않는다(보관만).

**검수 결과 (2026-11-10, `shortform/images/`)**

| 판 | 결과 |
|---|---|
| `v_pov_tight_lit` | 통과. 손·소매·무릎·계기판·실내등 모두 좋음 |
| `v_pov_tight_dark` | 기하는 일치. 그러나 평균 밝기 5/255, 손 부분 7/255 → 압축되면 검정. **다시**: `Keep this image exactly as it is but make the glow from the center display a little stronger: the hands and the wheel rim clearly visible as dark teal-lit shapes, the rest of the cabin still nearly black. No text.` (목표: 손 부분 15~20/255) |
| `v_pov_lit_beam_fog100_hands` / `v_pov_lit_beam_fog0_hands` | 통과. 두 장의 구도·손 위치 일치, 기둥 글씨 정확 |
| `v_m_monster1` / `v_m_monster3` | 통과. 같은 마른 실루엣 |
| `v_m_monster5` | 형상이 다름(어깨 넓고 젖은 피부). **그대로 쓰기로 함(2026-11-10 결정)** — 대신 클립 ④ 프롬프트에 "가까워질수록 덩치가 불어난다"를 넣어 변화를 의도된 연출로 만든다 |

늘리기가 안 되면 아래 프롬프트로 새로 만든다(일관성은 떨어짐).

- `v_f_dark_fog100` — First-person view from the driver's seat of a parked car at night in an underground parking garage, vertical framing that shows the headliner and dome light above and the dashboard and dark footwell below. Steering wheel, dashboard with a dark center screen, rearview mirror. The windshield is completely fogged over with condensation; nothing outside is visible. Interior nearly black, only a faint dashboard glow. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_f_dark_beam_fog100` — Same vertical driver's seat view. The windshield is completely fogged over; the headlights are on, a diffuse white glow smeared across the fogged glass, nothing outside readable. Interior nearly black. Grainy cinematic photo, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_f_dark_beam_fog0` — Same vertical driver's seat view. The windshield is clear; the headlights are on, lighting a flat gray concrete pillar directly ahead. On the pillar's flat face, large rough dripping red spray-painted Korean (Hangul) graffiti on two lines reading exactly "그것은 빨간 불빛을" / "무서워해". Wet floor reflections. Interior nearly black. Grainy cinematic photo, desaturated teal and amber, heavy shadows, 9:16.
- `v_m_monster1` — View through a car's rearview mirror with the mirror frame visible, vertical framing with the dark headliner above and the dark windshield below the mirror: a dark concrete parking garage aisle behind the car, pillars receding, wet floor, one distant dim lamp. Far down the aisle a tall dark featureless humanoid silhouette stands, barely distinguishable from the dark. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_m_monster3` — Same vertical rearview mirror view. A tall dark featureless humanoid silhouette stands a few car lengths behind, clearly taller than a person. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.
- `v_m_monster5` — Same vertical rearview mirror view. The silhouette is right behind the car, filling most of the mirror, no face, only the wet floor visible at the bottom. Grainy cinematic photo, almost no light, desaturated teal and amber, heavy shadows, no text, 9:16.

## Veo 클립 6개 (Flow, Veo 3.1 Fast, 9:16, 8초, 각 ≈20크레딧)

2026-11-10 재작성: 첫 ② 프롬프트로 만든 클립에서 손이 가만히 있고 **와이퍼가 움직였다**. 원인은 "condensation clears"를 와이퍼로 해석한 것 → 모든 프롬프트에 "유리 안쪽 김이 송풍으로 마른다, 와이퍼 없음", "차 정지, 카메라 고정", 손 동작은 **초 단위 순서**로 적는다. 기둥은 타이트(버튼 누르는 손) ②a + 와이드(김 걷히며 드러남) ②b로 나눈다. 와이드가 필요한 이유: 타이트 판은 앞유리가 위쪽 띠로만 보여 글씨가 잘린다. 더 가깝게 보이고 싶으면 편집에서 ②b를 125%로 밀어 넣는다(글씨는 그대로 읽힘).

| # | 첫 / 끝 프레임 | 프롬프트 |
|---|---|---|
| ① 더듬는 손 → 실내등 | v_pov_tight_dark / v_pov_tight_lit | `First-person view from the driver's seat of a parked car at night. The car is completely still; nothing outside the windows moves. Near-total darkness: the steering wheel and center console fill the frame, both hands rest on the wheel in dark long sleeves, lit only by the faint glow of the center display. The windshield is fogged on the inside and stays fogged; the wipers never move. Seconds 0-3: the right hand lifts off the wheel and pats slowly across the dashboard, feeling for anything, touching the stalk behind the wheel. Seconds 3-5: the right hand presses two buttons on the center console; nothing happens. Seconds 5-6: the hand hesitates, then reaches up past the top of the frame toward the ceiling. Seconds 6-8: a click; warm dome light fills the cabin, and the right hand comes back down to the wheel, ending with both hands on the wheel exactly like the final frame. Left hand never leaves the wheel. Same hand, same dark sleeve, no watch, no rings. Camera locked at eye level with only a subtle natural sway. Audio: slow breathing, two soft button clicks, one final switch click, no music, no voice. Grainy cinematic, desaturated teal and amber, heavy shadows.` |
| ②a 제상·전조등 켜는 손 (타이트) | v_pov_tight_lit / 없음 | `First-person view from the driver's seat of a parked car at night, dome light on. The car is completely still. The steering wheel and center console fill the frame; both hands rest on the wheel in dark long sleeves. Through the top of the frame the windshield is fogged white on the inside; the wipers never move. Seconds 0-3: the right hand lifts, hovers over the climate buttons below the center screen, hesitates, then presses the front-defroster button once; a small indicator on the button lights up. Seconds 3-6: the right hand moves to the left stalk behind the wheel and twists its end ring one notch; the instrument cluster brightens. Seconds 6-8: a soft white glow spreads across the fogged windshield at the top of the frame as the headlights come on outside; the right hand returns to the wheel. Left hand stays on the wheel. Same hand, same dark sleeve, no watch, no rings. Camera locked at eye level with only a subtle natural sway. Audio: a click, a second click, a low fan starting to blow, no music, no voice. Grainy cinematic, desaturated teal and amber, heavy shadows.` |
| ②b 김 걷히며 기둥 (와이드) | v_pov_lit_beam_fog100_hands / v_pov_lit_beam_fog0_hands | `First-person view from the driver's seat of a parked car at night, dome light on, headlights on, both hands resting on the steering wheel in dark long sleeves. The car is completely still and the camera does not move. The windshield is fogged white on the INSIDE of the glass. Over eight seconds the fog thins and fades evenly from the center of the glass outward, like warm air from the defroster vents drying it; it does not get wiped. There are no windshield wipers in the shot and nothing sweeps across the glass. As the fog fades, the headlights reveal a flat concrete pillar directly ahead with red dripping spray-painted Korean graffiti, exactly as in the final frame. The hands and the cabin stay completely still. Audio: a low fan hum, a faint wind, then silence, no music, no voice. Grainy cinematic, desaturated teal and amber, heavy shadows.` |
| ③ 접근 1 | v_m_monster1 / v_m_monster3 | `View through a car's rearview mirror in a dark underground parking garage. The car is parked and completely still: the mirror frame, the housing and the headliner do not move at all; the camera is locked. Inside the mirror, a thin tall featureless humanoid silhouette stands far down the aisle and walks slowly toward the car along the center line, taking four slow steps over eight seconds and growing from small to medium size. No face, no details, no other people. One distant ceiling lamp flickers faintly; wet floor reflections. Audio: faint slow scraping, a distant drip, no music, no voice. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ④ 접근 2 | v_m_monster3 / v_m_monster5 | `Same view through the car's rearview mirror in the dark parking garage. The car is parked and completely still: the mirror frame, the housing and the headliner do not move at all; the camera is locked. Inside the mirror, the featureless silhouette walks the last few steps toward the car; as it nears, its body swells broader and heavier and its skin glistens wet, until it fills most of the mirror with its head near the top edge of the glass. No face, no eyes, no mouth. It sways slightly and stops. Audio: scraping grows louder and closer, a slow heartbeat, no music, no voice. Grainy cinematic, almost no light, desaturated teal and amber.` |
| ⑤ 급한 손 | v_pov_tight_lit / 없음 | `First-person view from the driver's seat of a parked car at night, dome light on. The car is completely still; nothing outside moves. The steering wheel and center console fill the frame; both hands grip the wheel in dark long sleeves. Panic: the right hand lets go and jabs three buttons on the center console in quick succession, yanks the stalk behind the wheel up and down, slaps the dashboard, then grabs the wheel again; the left hand grips the wheel tighter and tighter. Nothing on the dashboard responds; the windshield does not change; the wipers never move. Both hands stay inside the frame the whole time. Same hands, same dark sleeves, no watch, no rings. Camera locked with a slightly stronger natural shake. Audio: rapid clicks, fast breathing, no music, no voice. Grainy cinematic, desaturated teal and amber, heavy shadows.` |

콘티 반영: 8~12초 구간은 ②a 앞 4초(고민 → 제상 버튼) + ②b 뒤 4~5초(김 걷힘 → 기둥). 전조등 레버 돌리는 2초는 ②a에서 잘라 쓰거나 생략.

편집 메모: 플랫폼 압축이 검정을 뭉개니 CapCut에서 전체 섀도 +8~12, 얇은 그레인. 룸미러 컷은 거울 부분이 밝아 그대로 둬도 된다.

예산: 6클립 120 + 재시도(①②a⑤ 위주) 80 = 월 200 안.

### Flow 조작 (2026-11 UI, Google 도움말 기준)

1. 프로젝트를 열면 오른쪽에 **에이전트 세션 창**("무엇을 만들고 싶으신가요?")이 떠 있다. 프레임 방식은 에이전트가 아니라 **일반 프롬프트 상자**로 한다 → 세션 창을 닫거나(X) 프롬프트 상자의 에이전트 토글을 끈다.
2. 이미지 올리기: 가운데 "미디어를 드롭하세요"에 7장을 끌어다 놓거나, 애셋 패널의 **미디어 업로드**. 올린 것은 왼쪽 "전체 미디어 → 업로드"에 모인다.
3. 프롬프트 상자의 **모델 이름 클릭 → Veo 3.1 - Fast**.
4. **동영상(Video) → 프레임(Frames)**. "+ 시작 프레임 추가" / "+ 종료 프레임 추가" 칸이 생기면 업로드한 이미지를 각각 끌어다 놓는다. 끝 프레임 없는 클립(⑤)은 시작 칸만.
5. 프롬프트를 붙여 넣고, 프롬프트 상자의 **설정(슬라이더 아이콘)**에서 세로(9:16)·출력 1개·Veo 3.1 Fast 확인 → 생성.
6. 완성된 클립 위에 마우스 → **더보기 → 다운로드** → MP4. 남은 크레딧은 오른쪽 위 **프로필 사진** 아래.
7. 비용(Google 도움말): Veo 3.1 Fast 20크레딧(4·6·8초 동일), Quality 100, Lite 10, Omni Flash 1.2 720p 초당 3. AI Plus는 월 200. **Veo 3.1 Fast·Quality는 2026-12-05에 종료**되므로 그 전에 만든다(이후엔 Omni Flash 1.2 또는 Veo 3.1 Lite).
8. 에이전트로도 가능: 세션 프롬프트 상자에 이미지 두 장을 끌어 넣고 "이 둘을 시작·종료 프레임으로 9:16 Veo 3.1 Fast 영상 1개, 프롬프트는 …"라고 쓰면 크레딧을 쓰기 전에 확인을 묻는다. 다만 프레임 지정이 확실한 건 4번 방식.

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
