import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

test('matches button', async () => {
  const language = process.env.NOTES_LANG ?? 'en';
  const driver = await remote({
    hostname: '127.0.0.1',
    port: 4723,
    capabilities: {
      'appium:language': language,
      'appium:locale': language === 'ru' ? 'RU' : 'US',
      platformName: 'Android',
      'appium:automationName': 'UiAutomator2',
      'appium:udid': process.env.ANDROID_SERIAL,
      'appium:appPackage': 'io.hexlet.notes',
      'appium:appActivity': '.Notes',
    },
  });
  try {
    const button = await driver.$('id=io.hexlet.notes:id/save');
    const english = await driver.$$('android=new UiSelector().text("Save")');
    assert.equal(await button.getText(), language === 'ru' ? 'Сохранить' : 'Save');
    assert.deepEqual(english.map(element => element.elementId), language === 'ru' ? [] : [button.elementId]);
  } finally {
    await driver.deleteSession();
  }
});
