import os

from appium import webdriver
from appium.options.android import UiAutomator2Options


def test_opens_notes():
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        assert driver.current_package == "io.hexlet.notes"
    finally:
        driver.quit()
