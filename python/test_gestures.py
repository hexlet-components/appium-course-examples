import os

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy


def open_note(driver, number):
    records = driver.find_element(AppiumBy.ID, "records")
    selector = f'new UiSelector().text("Note {number}")'
    can_scroll = True
    while True:
        rows = driver.find_elements(AppiumBy.ANDROID_UIAUTOMATOR, selector)
        if rows:
            rows[0].click()
            return True
        if not can_scroll:
            return False
        can_scroll = driver.execute_script("mobile: scrollGesture", {
            "elementId": records.id, "direction": "down", "percent": 0.75,
        })


def test_searches_seeded_list():
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        driver.find_element(AppiumBy.ID, "open_list").click()
        driver.find_element(AppiumBy.ID, "seed").click()
        assert open_note(driver, 80)
        assert driver.find_element(AppiumBy.ID, "title_input").text == "Note 80"
        driver.find_element(AppiumBy.ID, "open_list").click()
        assert not open_note(driver, 81)
    finally:
        driver.quit()
