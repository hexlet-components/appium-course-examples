#!/usr/bin/env bash
set -eu
export ANDROID_SERIAL="$(adb devices | awk '/^emulator-.*device$/ {print $1; exit}')"
adb shell input keyevent KEYCODE_WAKEUP
adb shell wm dismiss-keyguard
adb shell settings put global verifier_verify_adb_installs 0
adb install -r android/build/outputs/apk/debug/*.apk
mkdir -p screenshots
adb shell am start -W -n io.hexlet.notes/.Notes
adb exec-out screencap -p > screenshots/form.png
adb shell am force-stop io.hexlet.notes
appium --address 127.0.0.1 > appium.log 2>&1 &
trap 'kill "$!" || true' EXIT
curl --retry 30 --retry-connrefused --retry-delay 1 -fsS http://127.0.0.1:4723/status
language=${1:?Specify java, python or javascript}
case "$language" in
    java)
        gradle -p java test --rerun-tasks
        NOTES_LANG=ru gradle -p java test --tests LocatorsTest --rerun-tasks
        ;;
    python)
        python -m pytest python
        NOTES_LANG=ru python -m pytest python/test_locators.py
        ;;
    javascript)
        npm --prefix javascript test
        NOTES_LANG=ru node --test javascript/locators.test.js
        ;;
    *) exit 2 ;;
esac
bash run-diagnosis.sh "$language" appium.log
bash run-diagnosis.sh "$language" appium.log exercise
