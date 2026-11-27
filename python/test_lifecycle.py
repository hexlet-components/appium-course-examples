import os

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.support.ui import WebDriverWait


def test_returns_to_draft():
    app_id = "io.hexlet.notes"
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": app_id,
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        title = (AppiumBy.ID, "title_input")
        driver.find_element(*title).send_keys("Harbor")
        wait = WebDriverWait(driver, 8)
        driver.execute_script("mobile: backgroundApp", {"seconds": -1})
        wait.until(lambda d: d.execute_script("mobile: queryAppState", {"appId": app_id}) == 3)
        driver.execute_script("mobile: activateApp", {"appId": app_id})
        field = wait.until(ec.visibility_of_element_located(title))
        assert field.text == "Harbor"
    finally:
        driver.quit()
