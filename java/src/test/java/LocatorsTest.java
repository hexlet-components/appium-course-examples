import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocatorsTest {
    @Test void matchesButton() throws Exception {
        String language = System.getenv().getOrDefault("NOTES_LANG", "en");
        var options = new UiAutomator2Options()
            .setLanguage(language).setLocale(language.equals("ru") ? "RU" : "US")
            .setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            var button = driver.findElement(AppiumBy.id("save"));
            var english = driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().text(\"Save\")"));
            assertEquals(language.equals("ru") ? "Сохранить" : "Save", button.getText());
            if (language.equals("ru")) assertEquals(0, english.size());
            else assertEquals(java.util.List.of(button), english);
        } finally {
            driver.quit();
        }
    }
}
