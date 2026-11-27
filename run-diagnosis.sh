#!/usr/bin/env bash
set -eu

language=${1:?Usage: bash run-diagnosis.sh java|python|javascript [path/to/appium.log] [demo|exercise]}
server_log=${2:-appium.log}
export DIAG_CASE=${3:-demo}
case "$DIAG_CASE" in
  demo) expected=Orbit; actual=Atlas ;;
  exercise) expected=Cedar; actual=Cider ;;
  *) echo "Unknown case: $DIAG_CASE" >&2; exit 2 ;;
esac
test -r "$server_log" || { echo "Start Appium with --log appium.log" >&2; exit 1; }
export DIAG_DIR
DIAG_DIR=$(mktemp -d "${TMPDIR:-/tmp}/notes-diagnosis.XXXXXX")
echo "Diagnosis artifacts: $DIAG_DIR"

case "$language" in
  java) command=(gradle -p java test --tests DiagnosisTest --rerun-tasks) ;;
  python) command=(python -m pytest --color=no python/test_diagnosis.py) ;;
  javascript) command=(node --test --test-reporter=tap javascript/diagnosis.test.js) ;;
  *) echo "Unknown client: $language" >&2; exit 2 ;;
esac

if "${command[@]}" >"$DIAG_DIR/report.txt" 2>&1; then
  echo "Expected the saved title comparison to fail" >&2
  exit 1
fi

if [ "$language" = java ] && [ -f java/build/test-results/test/TEST-DiagnosisTest.xml ]; then
  cp java/build/test-results/test/TEST-DiagnosisTest.xml "$DIAG_DIR/report.xml"
  report="$DIAG_DIR/report.xml"
else
  report="$DIAG_DIR/report.txt"
fi

if [ "$DIAG_CASE" = exercise ]; then
  printf '%s' "$actual" | cmp - "$DIAG_DIR/input.txt"
fi
printf '%s\n%s' "$expected" "$actual" | cmp - "$DIAG_DIR/comparison.txt"
session=$(cat "$DIAG_DIR/session.txt")
test -n "$session"
grep -F -e "$session" -e "${session:0:8}" "$server_log" >"$DIAG_DIR/appium-session.log"
grep -Eq '/value([^[:alnum:]_]|$)' "$DIAG_DIR/appium-session.log"
grep -Eq '/click([^[:alnum:]_]|$)' "$DIAG_DIR/appium-session.log"
grep -Eq '/text([^[:alnum:]_]|$)' "$DIAG_DIR/appium-session.log"
grep -Fq "saved title" "$report"
grep -Fq "$actual" "$report"
grep -Fq "$expected" "$report"
test "$(od -An -tx1 -N8 "$DIAG_DIR/screen.png" | tr -d ' \n')" = 89504e470d0a1a0a
case "$language" in
  java) grep -Fq 'org.opentest4j.AssertionFailedError' "$report" ;;
  python) grep -Fq 'AssertionError: saved title' "$report" ;;
  javascript) grep -Fq 'ERR_ASSERTION' "$report" ;;
esac
echo "Confirmed saved title mismatch. Inspect $DIAG_DIR"
