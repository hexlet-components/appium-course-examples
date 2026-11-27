# Мобильное тестирование с Appium

В трёх проектах тесты работают с одним приложением Notes. Выберите каталог своего языка и запускайте один клиент за раз. Приложение открывает пустую форму. Введите Atlas и нажмите Save. На экране Saved появится Atlas. При русском языке Android кнопка подписана «Сохранить».

## Подготовка

Установите Android Studio и в SDK Manager выберите Android SDK Platform 35, Android SDK Platform-Tools и Android Emulator. В Device Manager создайте телефон с Android 15 (API 35) и запустите его. Для Apple Silicon подходит образ arm64-v8a, для компьютера x86-64 выбирайте x86_64. Установите JDK 17 и Node.js 22. Команды ниже рассчитаны на Bash. Укажите путь к SDK из SDK Manager в ANDROID_HOME, путь к JDK в JAVA_HOME, добавьте каталоги platform-tools и emulator из SDK в PATH.

Скачайте [учебную сборку notes.apk](https://github.com/hexlet-components/appium-course-examples/releases/download/v1.0/notes.apk) и установите её на учебный эмулятор. Серийный номер возьмите из строки со статусом device в ответе adb devices.

```sh
adb devices
export ANDROID_SERIAL=emulator-5554
adb -s "$ANDROID_SERIAL" install -r notes.apk
npm install --global appium@2.19.0
appium driver install uiautomator2@4.2.9
appium driver doctor uiautomator2
appium --address 127.0.0.1
```

Оставьте сервер запущенным. Откройте второй терминал в каталоге клона, задайте в нём тот же ANDROID_SERIAL. Для Java установите Gradle 8.9. Для Python подойдёт версия 3.12.

| Язык | Подготовка из каталога клона | Первый запуск с проверкой приложения |
| --- | --- | --- |
| Java | Зависимости скачает Gradle | `gradle -p java test --tests SessionTest --rerun-tasks` |
| Python | `python3 -m venv .venv`, затем `source .venv/bin/activate` и `python -m pip install -r python/requirements.txt` | `python -m pytest python/test_session.py` |
| JavaScript | `npm install --prefix javascript` | `node --test javascript/session.test.js` |

Папки java/src/test/java, python и javascript содержат сценарии курса. Для проверки сохранения подставьте в команду NotesTest, python/test_notes.py или javascript/notes.test.js соответственно.

## Управление данными

Начальная задержка сохранения равна нулю. Кнопка Delay перебирает значения 2000 мс, 4000 мс, No response и снова 0 мс. В режиме No response приложение остаётся на экране Saving. Чтобы вернуться к форме, перезапустите приложение. В разделе Records кнопка Load 80 records заменяет список записями от Note 1 до Note 80. Нажатие на строку открывает заполненную форму. New note открывает пустую. Сохранённые записи переживают перезапуск приложения. Для очистки только данных учебного приложения выполните `adb -s "$ANDROID_SERIAL" shell pm clear io.hexlet.notes`.

Для самостоятельной сборки выполните `gradle -p android assembleDebug`. APK появится в android/build/outputs/apk/debug.
