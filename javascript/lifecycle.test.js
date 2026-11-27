import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

test('returns to draft', async () => {
  const appId = 'io.hexlet.notes';
  const driver = await remote({
    hostname: '127.0.0.1',
    port: 4723,
    capabilities: {
      platformName: 'Android',
      'appium:automationName': 'UiAutomator2',
      'appium:udid': process.env.ANDROID_SERIAL,
      'appium:appPackage': appId,
      'appium:appActivity': '.Notes',
    },
  });
  try {
    const title = 'id=io.hexlet.notes:id/title_input';
    await driver.$(title).setValue('Harbor');
    await driver.execute('mobile: backgroundApp', { seconds: -1 });
    await driver.waitUntil(async () =>
      (await driver.execute('mobile: queryAppState', { appId })) === 3,
    { timeout: 8000 });
    await driver.execute('mobile: activateApp', { appId });
    const field = await driver.$(title);
    await field.waitForDisplayed({ timeout: 8000 });
    assert.equal(await field.getText(), 'Harbor');
  } finally {
    await driver.deleteSession();
  }
});
