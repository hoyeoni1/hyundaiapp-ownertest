#!/usr/bin/env bash
# 전 과정을 돌리며 장면마다 스크린샷을 찍는다(화면 확인용). 본편은 촬영용 타이머(괴물 70초 뒤·60초 여유), 분기는 빠른 타이머.
# usage: bash tools/shots-sawcar.sh <출력 폴더>   (앱 실행 중, `tools/vconsole.sh up` 뒤)
set -uo pipefail
export PATH="$PATH:/c/Users/lmh31/AppData/Local/Android/Sdk/platform-tools"
HERE=$(cd "$(dirname "$0")" && pwd)
OUT=${1:-shots}; mkdir -p "$OUT"
A=${ADB:-adb}; PKG=com.hyundaiapp.sawcar
dbg() { "$A" shell am broadcast -a $PKG.DEBUG "$@" >/dev/null; }
demo() { bash "$HERE/demo-sawcar.sh" "$@" >/dev/null; }
shot() { sleep "${2:-0.8}"; "$A" exec-out screencap -p > "$OUT/$1.png"; echo "shot $1"; }
last() { "$A" logcat -d -s SawCarGame:I | grep "phase=" | tail -1 | sed 's/^.*SawCarGame: //'; }
waitfor() { local re="$1" secs="$2" i=0; while [ $i -lt "$secs" ]; do last | grep -qE "$re" && return 0; sleep 1; i=$((i+1)); done; echo "TIMEOUT /$re/: $(last)"; return 1; }

demo reset; sleep 1; "$A" logcat -c; dbg --es action shots; sleep 1
shot 01-title 1
demo lock; waitfor "phase=PLAYING" 5
shot 02-front-dark 1.5
demo cabin on; shot 03-front-lit 1.5
dbg --es view STEERING; shot 04-steering-lit 1.5
demo heat 2; sleep 10; shot 05-steering-thermal 1.5
demo cabin off; shot 06-steering-glow 1.5
dbg --es view FRONT; demo defrost on; shot 07-front-fog-clearing 5
demo beam LowBeam; shot 08-front-pillar 6
dbg --es view LEFT; shot 09-left 1
dbg --es view RIGHT; shot 10-right 1
dbg --es view RIGHT_MIRROR; shot 11-right-mirror 1
dbg --es view FRONT
waitfor "monster=true" 60; shot 12-front-monster-hint 10
dbg --es view REARVIEW; shot 13-rearview-monster 8
demo brake on; shot 14-rearview-brake 2.5
waitfor "repelled=1" 12; demo brake off
waitfor "signal=true" 30; dbg --es view LEFT_MIRROR
for i in 1 2 3 4 5 6; do shot "15-left-mirror-signal-$i" 0.35; done
demo answer; waitfor "phase=CLEARED" 5
dbg --es view FRONT; shot 16-cleared-dawn-1 7; shot 17-cleared-dawn-2 12
demo unlock; demo door fl open; waitfor "phase=ENDED" 5; shot 18-result 1.5
demo door fl close
# 분기 화면(빠른 타이머)
dbg --es action fast; sleep 1; demo lock; waitfor "phase=PLAYING" 5
demo gear Drive; shot 19-paused-gear 1; demo gear Park; waitfor "phase=PLAYING" 5
demo unlock; demo door fl open; shot 20-early-exit 1.5; demo door fl close; sleep 1; dbg --es action reboard; waitfor "phase=PLAYING" 5
waitfor "monster=true" 40; sleep 25; waitfor "phase=GAME_OVER" 3; shot 21-game-over 4.5
dbg --es action debug; shot 22-debug-overlay 1; dbg --es action debug
demo reset
echo done
