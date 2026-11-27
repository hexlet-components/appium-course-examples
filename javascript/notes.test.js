import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

test('saves title', async () => {
  const driver = await remote({
    hostname: '127.0.0.1',
    port: 4723,
    capabilities: {
      platformName: 'Android',
      'appium:automationName': 'UiAutomator2',
      'appium:udid': process.env.ANDROID_SERIAL,
      'appium:appPackage': 'io.hexlet.notes',
      'appium:appActivity': '.Notes',
    },
  });
  try {
    await driver.$('id=io.hexlet.notes:id/title_input').setValue('Atlas');
    await driver.$('id=io.hexlet.notes:id/save').click();
    assert.equal(await driver.$('id=io.hexlet.notes:id/result').getText(), 'Atlas');
  } finally {
    await driver.deleteSession();
  }
});
