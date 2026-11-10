#!/usr/bin/env bash
# 신호 6개(+시작·결말·규칙) 실측: 콘솔 PUT → 2초 뒤 앱 로그(SawCarSignals)에 콜백이 찍히는지.
# 앱이 설치·실행 중이어야 한다. 먼저 `bash tools/vconsole.sh up`.
# usage: bash tools/vtest-sawcar.sh > vtest.log 2>&1
set -uo pipefail
export PATH="$PATH:/c/Users/lmh31/AppData/Local/Android/Sdk/platform-tools"
HERE=$(cd "$(dirname "$0")" && pwd)
A=${ADB:-adb}
step() {
  local label="$1"; shift
  "$A" logcat -c
  for req in "$@"; do bash "$HERE/vconsole.sh" put "$req" >/dev/null; done
  sleep 2
  echo "### $label  ($*)"
  "$A" logcat -d -s SawCarSignals:I | grep -v "^---" | sed 's/^.*SawCarSignals: //' | sed 's/^/    /'
}
bash "$HERE/demo-sawcar.sh" reset >/dev/null
sleep 2
step "실내등 ON" 'body?cabin_light=true'
step "실내등 OFF" 'body?cabin_light=false'
step "핸들 열선 2" 'steering-wheel-heat?level=2'
step "핸들 열선 0" 'steering-wheel-heat?level=0'
step "앞유리 제상 ON" 'hvac?front_defroster=true'
step "앞유리 제상 OFF" 'hvac?front_defroster=false'
step "전조등 LowBeam" 'lights?feature=headlamp_mode&value=LowBeam'
step "전조등 Off" 'lights?feature=headlamp_mode&value=Off'
step "상향등 ON" 'lights?feature=high_beam&value=true'
step "상향등 OFF" 'lights?feature=high_beam&value=false'
step "방향지시등 LEFT" 'turn-signal?mode=BlinkLeft'
step "방향지시등 OFF" 'turn-signal?mode=Off'
step "방향지시등 LEFT 다시" 'turn-signal?mode=BlinkLeft'
step "방향지시등 OFF 다시" 'turn-signal?mode=Off'
step "방향지시등 RIGHT" 'turn-signal?mode=BlinkRight'
step "방향지시등 OFF (3)" 'turn-signal?mode=Off'
step "브레이크 ON" 'controls?brake=50'
step "브레이크 OFF" 'controls?brake=0'
step "잠금" 'body?doors_locked=true'
step "잠금 해제" 'body?doors_locked=false'
step "운전석 문 열림" 'body?door_fl=true'
step "운전석 문 닫힘" 'body?door_fl=false'
step "기어 D" 'gear?gear=Drive'
step "기어 P" 'gear?gear=Park'
