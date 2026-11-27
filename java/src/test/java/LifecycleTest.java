import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LifecycleTest {
    @Test void returnsToDraft() throws Exception {
        String appId = "io.hexlet.notes";
        var options = new UiAutomator2Options()
            .setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage(appId).setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            var title = AppiumBy.id("title_input");
            driver.findElement(title).sendKeys("Harbor");
            var wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            driver.executeScript("mobile: backgroundApp", Map.of("seconds", -1));
            wait.until(d -> ((Number) driver.executeScript(
                "mobile: queryAppState", Map.of("appId", appId))).intValue() == 3);
            driver.executeScript("mobile: activateApp", Map.of("appId", appId));
            var field = wait.until(ExpectedConditions.visibilityOfElementLocated(title));
            assertEquals("Harbor", field.getText());
        } finally {
            driver.quit();
        }
    }
}
