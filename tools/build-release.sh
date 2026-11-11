#!/usr/bin/env bash
# 제출용 release APK를 만들고 확인한 뒤 apk/SamgakGorae_OwnerTest_release.apk로 복사한다.
#
# usage (저장소 루트에서):
#   bash tools/build-release.sh
#
# local.properties에 필요한 값(저장소에 올리지 않음):
#   release.storeFile=... release.storePassword=... release.keyAlias=... release.keyPassword=...   서명
#
# 전제: JAVA_HOME(예: export JAVA_HOME="/e/AndroidStudio/jbr"), local.properties의 sdk.dir
# GitHub는 100MB가 넘는 파일을 받지 않는다. 이미지를 새로 넣었다면 아래 용량 확인에서 걸린다.
set -euo pipefail
cd "$(dirname "$0")/.."

prop() { sed -n "s/^$1=//p" local.properties 2>/dev/null | tail -1 | tr -d '\r'; }
fail() { echo "✗ $*" >&2; exit 1; }

OUT="apk/SamgakGorae_OwnerTest_release.apk"
MAX_MB=95

[ -f local.properties ] || fail "local.properties 없음"
[ -n "$(prop release.storeFile)" ] || fail "release.storeFile 없음 → 서명되지 않은 APK가 나옴"

./gradlew -q :app:testDebugUnitTest :app:assembleRelease

APK=app/build/outputs/apk/release/app-release.apk
[ -f "$APK" ] || fail "$APK 없음(서명 설정 확인)"

SDK=$(prop sdk.dir)
APKSIGNER=$(ls -d "$SDK"/build-tools/*/ 2>/dev/null | sort -V | tail -1)apksigner
[ -x "$APKSIGNER" ] || APKSIGNER="${APKSIGNER}.bat"
if [ -e "$APKSIGNER" ]; then
  "$APKSIGNER" verify "$APK" || fail "서명 확인 실패"
else
  echo "! apksigner를 찾지 못해 서명 확인을 건너뜀"
fi

SIZE_MB=$(( $(wc -c < "$APK") / 1000000 ))
[ "$SIZE_MB" -lt "$MAX_MB" ] || fail "APK가 ${SIZE_MB}MB → GitHub 한도(100MB)에 걸린다. 이미지를 WebP로 줄이거나 GitHub Releases에 올릴 것"

mkdir -p apk
cp "$APK" "$OUT"
echo "✓ $OUT (${SIZE_MB}MB) — 단위 테스트 통과, 서명됨"
