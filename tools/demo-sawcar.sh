#!/usr/bin/env bash
# 쏘우카 시연·실측용 콘솔 주입 (콘솔 = 손). 먼저 `bash tools/vconsole.sh up`.
# usage:
#   bash tools/demo-sawcar.sh reset        # 시작 전 상태(잠금 해제, 불·제상·열선·브레이크·깜빡이 끔, P)
#   bash tools/demo-sawcar.sh lock         # 시작(문 잠금)
#   bash tools/demo-sawcar.sh cabin on|off
#   bash tools/demo-sawcar.sh heat 0..3
#   bash tools/demo-sawcar.sh defrost on|off
#   bash tools/demo-sawcar.sh beam Off|Auto|Position|LowBeam
#   bash tools/demo-sawcar.sh high on|off
#   bash tools/demo-sawcar.sh turn Off|BlinkLeft|BlinkRight|FlashLeft|FlashRight
#   bash tools/demo-sawcar.sh answer       # ◀ ◀ ▶ (각 1.5초 켜고 끔)
#   bash tools/demo-sawcar.sh brake on|off
#   bash tools/demo-sawcar.sh door fl|fr|rl|rr open|close
#   bash tools/demo-sawcar.sh gear Park|Drive
#   bash tools/demo-sawcar.sh state        # 현재 콘솔 상태(JSON)
set -euo pipefail
HERE=$(cd "$(dirname "$0")" && pwd)
put() { bash "$HERE/vconsole.sh" put "$@"; }
b() { case "$1" in on|true|open) echo true;; *) echo false;; esac; }

case "${1:-}" in
  reset)
    put 'gear?gear=Park' 'body?doors_locked=false' 'body?cabin_light=false' 'lights?feature=headlamp_mode&value=Off' \
        'lights?feature=high_beam&value=false' 'hvac?front_defroster=false' 'steering-wheel-heat?level=0' \
        'controls?brake=0' 'turn-signal?mode=Off' 'body?door_fl=false' 'body?door_fr=false' 'body?door_rl=false' 'body?door_rr=false' ;;
  lock)    put 'body?doors_locked=true' ;;
  unlock)  put 'body?doors_locked=false' ;;
  cabin)   put "body?cabin_light=$(b "$2")" ;;
  heat)    put "steering-wheel-heat?level=$2" ;;
  defrost) put "hvac?front_defroster=$(b "$2")" ;;
  beam)    put "lights?feature=headlamp_mode&value=$2" ;;
  high)    put "lights?feature=high_beam&value=$(b "$2")" ;;
  turn)    put "turn-signal?mode=$2" ;;
  answer)
    for m in BlinkLeft Off BlinkLeft Off BlinkRight Off; do put "turn-signal?mode=$m"; sleep 1.5; done ;;
  brake)   if [ "$(b "$2")" = true ]; then put 'controls?brake=50'; else put 'controls?brake=0'; fi ;;
  door)    put "body?door_$2=$(b "$3")" ;;
  gear)    put "gear?gear=$2" ;;
  state)   bash "$HERE/vconsole.sh" state ;;
  *) sed -n '2,18p' "$0"; exit 1 ;;
esac
