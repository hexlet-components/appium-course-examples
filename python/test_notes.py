import os

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy


def test_saves_title():
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        driver.find_element(AppiumBy.ID, "title_input").send_keys("Atlas")
        driver.find_element(AppiumBy.ID, "save").click()
        assert driver.find_element(AppiumBy.ID, "result").text == "Atlas"
    finally:
        driver.quit()
