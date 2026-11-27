import assert from 'node:assert/strict';
import test from 'node:test';
import { writeFile } from 'node:fs/promises';
import { join } from 'node:path';
import { remote } from 'webdriverio';

test('compares saved title', { skip: !process.env.DIAG_DIR }, async () => {
  const directory = process.env.DIAG_DIR;
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
    await driver.$('id=io.hexlet.notes:id/title_input').setValue('Atlas');
    await driver.$('id=io.hexlet.notes:id/save').click();
    const actual = await driver.$('id=io.hexlet.notes:id/result').getText();
    const expected = 'Orbit';
    if (actual !== expected) {
      await driver.saveScreenshot(join(directory, 'screen.png'));
      await writeFile(join(directory, 'comparison.txt'), `${expected}\n${actual}`);
      await writeFile(join(directory, 'session.txt'), driver.sessionId);
    }
    assert.equal(actual, expected, 'saved title');
  } finally {
    await driver.deleteSession();
  }
});
