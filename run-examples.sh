#!/usr/bin/env bash
set -eu
export ANDROID_SERIAL="$(adb devices | awk '/^emulator-.*device$/ {print $1; exit}')"
adb install -r android/build/outputs/apk/debug/*.apk
appium --address 127.0.0.1 > appium.log 2>&1 &
trap 'kill "$!" || true' EXIT
curl --retry 30 --retry-connrefused --retry-delay 1 -fsS http://127.0.0.1:4723/status
gradle -p java test --rerun-tasks
python -m pytest python
npm --prefix javascript test
export NOTES_LANG=ru
gradle -p java test --tests LocatorsTest --rerun-tasks
python -m pytest python/test_locators.py
node --test javascript/locators.test.js
