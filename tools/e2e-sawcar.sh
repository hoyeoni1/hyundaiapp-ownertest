#!/usr/bin/env bash
# 시나리오 v1.2 전 과정을 에뮬레이터에서 돌린다(콘솔 = 손, 시점은 adb 브로드캐스트). 빠른 타이머(괴물 15~20초, 신호 10~15초).
# 앱이 실행 중이어야 한다. 먼저 `bash tools/vconsole.sh up`. usage: bash tools/e2e-sawcar.sh > e2e.log 2>&1
set -uo pipefail
export PATH="$PATH:/c/Users/lmh31/AppData/Local/Android/Sdk/platform-tools"
HERE=$(cd "$(dirname "$0")" && pwd)
A=${ADB:-adb}
PKG=com.hyundaiapp.sawcar
dbg() { "$A" shell am broadcast -a $PKG.DEBUG "$@" >/dev/null; }
demo() { bash "$HERE/demo-sawcar.sh" "$@" >/dev/null; }
say() { echo; echo "### $* ($(date +%H:%M:%S))"; }
last() { "$A" logcat -d -s SawCarGame:I | grep "phase=" | tail -1 | sed 's/^.*SawCarGame: //'; }
waitfor() { # waitfor <regex> <seconds>
  local re="$1" secs="$2" i=0
  while [ $i -lt "$secs" ]; do
    if last | grep -qE "$re"; then echo "    ok: $(last | grep -oE "$re")"; return 0; fi
    sleep 1; i=$((i+1))
  done
  echo "    TIMEOUT waiting for /$re/ — last: $(last)"; return 1
}

say "준비: 콘솔 초기화 + 엔진 처음부터(빠른 타이머)"
demo reset; sleep 1; "$A" logcat -c; dbg --es action fast; sleep 1; last

say "시작: 문 잠금 → PLAYING"
demo lock; waitfor "phase=PLAYING" 5

say "조사 ①: 실내등 ON, 핸들 확대, 열선 2 → 10초 뒤 열 감응지"
demo cabin on; dbg --es view STEERING; demo heat 2; waitfor "thermal=true/true" 15

say "야광: 실내등 10초 이상 켠 뒤 끄기 → glow 보임/봄"
sleep 2; demo cabin off; waitfor "glow=true/true" 5

say "조사 ②: 정면, 제상 + 전조등 → 10초 뒤 기둥"
dbg --es view FRONT; demo defrost on; demo beam LowBeam; waitfor "pillar=true/true" 15

say "괴물: 15~20초 안에 등장 → 룸미러 → 브레이크 8초 → 격퇴 (등장 뒤 25초 안에 끝내야 한다)"
waitfor "monster=true" 30; dbg --es view REARVIEW
demo brake on; waitfor "repelled=1" 12; demo brake off

say "신호: 자격 갖춤 → 10~15초 뒤 신호 → 왼쪽 미러 → ◀ ◀ ▶ → CLEARED"
waitfor "eligible=true" 5; waitfor "signal=true" 30; dbg --es view LEFT_MIRROR
demo answer; waitfor "phase=CLEARED" 5

say "결말: 문 열기 → ENDED"
demo unlock; demo door fl open; waitfor "phase=ENDED" 5; demo door fl close

say "분기 1: 게임 오버 — 처음부터(빠르게), 잠금, 괴물 25초 방치 → GAME_OVER → 다시"
demo reset; sleep 1; dbg --es action fast; sleep 1; demo lock; waitfor "phase=PLAYING" 5
waitfor "monster=true" 30; sleep 11; echo "    힌트(10초, 기둥 안 봄 → NONE): $(last | grep -oE 'hint=[A-Z_]+')"; sleep 15; waitfor "phase=GAME_OVER" 3
dbg --es action retry; waitfor "phase=PLAYING.*retry=1.*invHint=앞을 봐야 한다" 5

say "분기 2: 조기 탈출 — 문 열기 → EARLY_EXIT → 닫고 다시 타기"
demo unlock; demo door fl open; waitfor "phase=EARLY_EXIT" 5; demo door fl close; sleep 1
dbg --es action reboard; waitfor "phase=PLAYING" 5

say "분기 3: 기어 D → PAUSED_GEAR → P → PLAYING"
demo gear Drive; waitfor "phase=PAUSED_GEAR" 5; demo gear Park; waitfor "phase=PLAYING" 5

say "끝. 콘솔 초기화"
demo reset
