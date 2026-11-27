import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

async function waitForTitle(driver) {
  const result = await driver.$('id=io.hexlet.notes:id/result');
  await result.waitForDisplayed({
    timeout: 8000,
    timeoutMsg: 'Saved title did not appear',
  });
  return result.getText();
}

async function readAfterPause(driver) {
  await driver.pause(3000);
  return driver.$('id=io.hexlet.notes:id/result').getText();
}

for (const delayClicks of [0, 1, 2, 3]) {
  test(`delayed save, delay button clicks: ${delayClicks}`, async () => {
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
      for (let i = 0; i < delayClicks; i++) {
        await driver.$('id=io.hexlet.notes:id/delay').click();
      }
      await driver.$('id=io.hexlet.notes:id/title_input').setValue('Atlas');
      await driver.$('id=io.hexlet.notes:id/save').click();
      if (delayClicks === 0) {
        assert.equal(await readAfterPause(driver), 'Atlas');
      } else if (delayClicks === 3) {
        await assert.rejects(waitForTitle(driver), /Saved title did not appear/);
      } else {
        assert.equal(await waitForTitle(driver), 'Atlas');
      }
    } finally {
      await driver.deleteSession();
    }
  });
}
