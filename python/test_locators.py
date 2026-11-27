import os

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy


def test_matches_button():
    language = os.getenv("NOTES_LANG", "en")
    options = UiAutomator2Options().load_capabilities({
        "language": language,
        "locale": "RU" if language == "ru" else "US",
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        button = driver.find_element(AppiumBy.ID, "save")
        english = driver.find_elements(AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("Save")')
        assert button.text == ("Сохранить" if language == "ru" else "Save")
        assert english == ([] if language == "ru" else [button])
    finally:
        driver.quit()
