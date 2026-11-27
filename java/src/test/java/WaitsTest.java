import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class WaitsTest {
    private String waitForTitle(AndroidDriver driver) {
        var wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        var result = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("result")));
        return result.getText();
    }

    private String readAfterPause(AndroidDriver driver) throws InterruptedException {
        Thread.sleep(3000);
        return driver.findElement(AppiumBy.id("result")).getText();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void delayedSave(int delayClicks) throws Exception {
        var options = new UiAutomator2Options()
            .setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            for (int i = 0; i < delayClicks; i++) {
                driver.findElement(AppiumBy.id("delay")).click();
            }
            driver.findElement(AppiumBy.id("title_input")).sendKeys("Atlas");
            driver.findElement(AppiumBy.id("save")).click();
            if (delayClicks == 0) {
                assertEquals("Atlas", readAfterPause(driver));
            } else if (delayClicks == 3) {
                assertThrows(TimeoutException.class, () -> waitForTitle(driver));
            } else {
                assertEquals("Atlas", waitForTitle(driver));
            }
        } finally {
            driver.quit();
        }
    }
}
