import assert from 'node:assert/strict';
import test from 'node:test';
import { remote } from 'webdriverio';

async function openNote(driver, number) {
  const records = await driver.$('id=io.hexlet.notes:id/records');
  const selector = `android=new UiSelector().text("Note ${number}")`;
  let canScroll = true;
  while (true) {
    const rows = await driver.$$(selector);
    if (rows.length > 0) {
      await rows[0].click();
      return true;
    }
    if (!canScroll) {
      return false;
    }
    canScroll = await driver.execute('mobile: scrollGesture', {
      elementId: records.elementId, direction: 'down', percent: 0.75,
    });
  }
}

test('searches seeded list', async () => {
  const driver = await remote({
    hostname: '127.0.0.1', port: 4723,
    capabilities: {
      platformName: 'Android',
      'appium:automationName': 'UiAutomator2',
      'appium:udid': process.env.ANDROID_SERIAL,
      'appium:appPackage': 'io.hexlet.notes',
      'appium:appActivity': '.Notes',
    },
  });
  try {
    await driver.$('id=io.hexlet.notes:id/open_list').click();
    await driver.$('id=io.hexlet.notes:id/seed').click();
    assert.equal(await openNote(driver, 80), true);
    assert.equal(await driver.$('id=io.hexlet.notes:id/title_input').getText(), 'Note 80');
    await driver.$('id=io.hexlet.notes:id/open_list').click();
    assert.equal(await openNote(driver, 81), false);
  } finally {
    await driver.deleteSession();
  }
});
