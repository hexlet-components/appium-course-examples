import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebElement;

class GesturesTest {
    static boolean openNote(AndroidDriver driver, int number) {
        var list = (RemoteWebElement) driver.findElement(AppiumBy.id("records"));
        var selector = AppiumBy.androidUIAutomator("new UiSelector().text(\"Note " + number + "\")");
        boolean canScroll = true;
        while (true) {
            var rows = driver.findElements(selector);
            if (!rows.isEmpty()) {
                rows.get(0).click();
                return true;
            }
            if (!canScroll) {
                return false;
            }
            canScroll = (Boolean) driver.executeScript("mobile: scrollGesture",
                Map.of("elementId", list.getId(), "direction", "down", "percent", 0.75));
        }
    }

    @Test void searchesSeededList() throws Exception {
        var options = new UiAutomator2Options().setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            driver.findElement(AppiumBy.id("open_list")).click();
            driver.findElement(AppiumBy.id("seed")).click();
            assertTrue(openNote(driver, 80));
            assertEquals("Note 80", driver.findElement(AppiumBy.id("title_input")).getText());
            driver.findElement(AppiumBy.id("open_list")).click();
            assertFalse(openNote(driver, 81));
        } finally {
            driver.quit();
        }
    }
}
