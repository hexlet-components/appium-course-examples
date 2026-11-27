import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionTest {
    @Test void opensNotes() throws Exception {
        var options = new UiAutomator2Options()
            .setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            assertEquals("io.hexlet.notes", driver.getCurrentPackage());
        } finally {
            driver.quit();
        }
    }
}
