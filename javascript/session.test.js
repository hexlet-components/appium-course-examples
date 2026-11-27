import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

test('opens Notes', async () => {
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
    assert.equal(await driver.getCurrentPackage(), 'io.hexlet.notes');
  } finally {
    await driver.deleteSession();
  }
});
