import os

import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait


def wait_for_title(driver):
    wait = WebDriverWait(driver, 8)
    result = wait.until(EC.visibility_of_element_located((AppiumBy.ID, "result")))
    return result.text


@pytest.mark.parametrize("delay_clicks", [1, 2, 3])
def test_delayed_save(delay_clicks):
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": "io.hexlet.notes",
        "appActivity": ".Notes",
    })
    driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        for _ in range(delay_clicks):
            driver.find_element(AppiumBy.ID, "delay").click()
        driver.find_element(AppiumBy.ID, "title_input").send_keys("Atlas")
        driver.find_element(AppiumBy.ID, "save").click()
        if delay_clicks == 3:
            with pytest.raises(TimeoutException):
                wait_for_title(driver)
        else:
            assert wait_for_title(driver) == "Atlas"
    finally:
        driver.quit()
