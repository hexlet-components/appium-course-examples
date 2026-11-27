#!/usr/bin/env bash
set -eu
export ANDROID_SERIAL="$(adb devices | awk '/^emulator-.*device$/ {print $1; exit}')"
adb install -r android/build/outputs/apk/debug/*.apk
appium --address 127.0.0.1 > appium.log 2>&1 &
trap 'kill "$!" || true' EXIT
curl --retry 30 --retry-connrefused --retry-delay 1 -fsS http://127.0.0.1:4723/status
mvn -B -f java/pom.xml test
python -m pytest python
npm --prefix javascript test
