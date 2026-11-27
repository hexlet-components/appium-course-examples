import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotesTest {
    @Test void savesTitle() throws Exception {
        var options = new UiAutomator2Options()
            .setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            driver.findElement(AppiumBy.id("title_input")).sendKeys("Atlas");
            driver.findElement(AppiumBy.id("save")).click();
            assertEquals("Atlas", driver.findElement(AppiumBy.id("result")).getText());
        } finally {
            driver.quit();
        }
    }
}
