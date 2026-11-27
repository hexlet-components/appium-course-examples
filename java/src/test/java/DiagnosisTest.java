import static org.junit.jupiter.api.Assertions.assertEquals;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.OutputType;

class DiagnosisTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "DIAG_DIR", matches = ".+")
    void comparesSavedTitle() throws Exception {
        var dir = Path.of(System.getenv("DIAG_DIR"));
        var options = new UiAutomator2Options().setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage("io.hexlet.notes").setAppActivity(".Notes");
        var driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        try {
            driver.findElement(AppiumBy.id("title_input")).sendKeys("Atlas");
            driver.findElement(AppiumBy.id("save")).click();
            var actual = driver.findElement(AppiumBy.id("result")).getText();
            var expected = "Orbit";
            if (!expected.equals(actual)) {
                Files.write(dir.resolve("screen.png"), driver.getScreenshotAs(OutputType.BYTES));
                Files.writeString(dir.resolve("comparison.txt"), expected + "\n" + actual);
                Files.writeString(dir.resolve("session.txt"), driver.getSessionId().toString());
            }
            assertEquals(expected, actual, "saved title");
        } finally {
            driver.quit();
        }
    }
}
