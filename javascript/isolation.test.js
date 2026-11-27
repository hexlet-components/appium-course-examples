import assert from 'node:assert/strict';
import { afterEach, beforeEach, test } from 'node:test';
import { remote } from 'webdriverio';

const app = { appId: 'io.hexlet.notes' };
let driver;
const byId = (id) => driver.$('id=io.hexlet.notes:id/' + id);

beforeEach(async () => {
  driver = await remote({
    hostname: '127.0.0.1',
    port: 4723,
    capabilities: {
      platformName: 'Android',
      'appium:automationName': 'UiAutomator2',
      'appium:udid': process.env.ANDROID_SERIAL,
      'appium:appPackage': app.appId,
      'appium:appActivity': '.Notes',
    },
  });
});
afterEach(async () => { if (driver) await driver.deleteSession(); });

async function emptyFormAndStorage() {
  await driver.execute('mobile: terminateApp', app);
  await driver.execute('mobile: clearApp', app);
  await driver.execute('mobile: activateApp', app);
}

async function restartKeepingNotes() {
  await driver.execute('mobile: terminateApp', app);
  await driver.execute('mobile: activateApp', app);
}

async function saveTitle(value) {
  await byId('title_input').setValue(value);
  await byId('save').click();
}

async function createNote() {
  await saveTitle('Harbor');
  assert.equal(await byId('result').getText(), 'Harbor');
}

async function editNote() {
  await byId('open_list').click();
  const rows = await driver.$$('android=new UiSelector().text("Harbor")');
  if (rows.length === 0) throw new Error('Harbor is missing');
  assert.equal(rows.length, 1);
  await rows[0].click();
  await saveTitle('Port');
  assert.equal(await byId('result').getText(), 'Port');
}

async function independentCreate() {
  await emptyFormAndStorage();
  await createNote();
}

async function independentEdit() {
  await emptyFormAndStorage();
  await saveTitle('Harbor');
  await editNote();
}

test('dependent orders', async () => {
  await emptyFormAndStorage();
  await createNote();
  await restartKeepingNotes();
  await editNote();

  await emptyFormAndStorage();
  await assert.rejects(editNote, { message: 'Harbor is missing' });
  await restartKeepingNotes();
  await createNote();
});

test('edit alone', async () => {
  await emptyFormAndStorage();
  await assert.rejects(editNote, { message: 'Harbor is missing' });
  await independentEdit();
});

test('independent orders', async () => {
  await independentCreate();
  await independentEdit();
  await independentEdit();
  await independentCreate();
});
