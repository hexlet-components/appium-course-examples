import os

import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy

APP = {"appId": "io.hexlet.notes"}


@pytest.fixture
def driver():
    options = UiAutomator2Options().load_capabilities({
        "udid": os.environ["ANDROID_SERIAL"],
        "appPackage": APP["appId"],
        "appActivity": ".Notes",
    })
    session = webdriver.Remote("http://127.0.0.1:4723", options=options)
    try:
        yield session
    finally:
        session.quit()


def empty_form_and_storage(driver):
    driver.execute_script("mobile: terminateApp", APP)
    driver.execute_script("mobile: clearApp", APP)
    driver.execute_script("mobile: activateApp", APP)


def restart_keeping_notes(driver):
    driver.execute_script("mobile: terminateApp", APP)
    driver.execute_script("mobile: activateApp", APP)


def save_title(driver, value):
    field = driver.find_element(AppiumBy.ID, "title_input")
    field.clear()
    field.send_keys(value)
    driver.find_element(AppiumBy.ID, "save").click()


def create_note(driver):
    save_title(driver, "Harbor")
    assert driver.find_element(AppiumBy.ID, "result").text == "Harbor"


def edit_note(driver):
    driver.find_element(AppiumBy.ID, "open_list").click()
    rows = driver.find_elements(AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("Harbor")')
    if not rows:
        raise AssertionError("Harbor is missing")
    assert len(rows) == 1
    rows[0].click()
    save_title(driver, "Port")
    assert driver.find_element(AppiumBy.ID, "result").text == "Port"


def independent_create(driver):
    empty_form_and_storage(driver)
    create_note(driver)


def independent_edit(driver):
    empty_form_and_storage(driver)
    save_title(driver, "Harbor")
    edit_note(driver)


def test_dependent_orders(driver):
    empty_form_and_storage(driver)
    create_note(driver)
    restart_keeping_notes(driver)
    edit_note(driver)

    empty_form_and_storage(driver)
    with pytest.raises(AssertionError, match="^Harbor is missing$"):
        edit_note(driver)
    restart_keeping_notes(driver)
    create_note(driver)


def test_edit_alone(driver):
    empty_form_and_storage(driver)
    with pytest.raises(AssertionError, match="^Harbor is missing$"):
        edit_note(driver)
    independent_edit(driver)


def test_independent_orders(driver):
    independent_create(driver)
    independent_edit(driver)
    independent_edit(driver)
    independent_create(driver)
