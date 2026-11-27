import os
from pathlib import Path

import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy


@pytest.mark.skipif("DIAG_DIR" not in os.environ, reason="run with run-diagnosis.sh")
def test_compares_saved_title():
    directory = Path(os.environ["DIAG_DIR"])
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        exercise = os.environ.get("DIAG_CASE") == "exercise"
        title = driver.find_element(AppiumBy.ID, "title_input")
        title.send_keys("Cider" if exercise else "Atlas")
        if exercise:
            (directory / "input.txt").write_text(title.text)
        driver.find_element(AppiumBy.ID, "save").click()
        actual = driver.find_element(AppiumBy.ID, "result").text
        expected = "Cedar" if exercise else "Orbit"
        if actual != expected:
            (directory / "screen.png").write_bytes(driver.get_screenshot_as_png())
            (directory / "comparison.txt").write_text(f"{expected}\n{actual}")
            (directory / "session.txt").write_text(driver.session_id)
        assert actual == expected, "saved title"
    finally:
        driver.quit()
