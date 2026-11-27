import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IsolationTest {
    private AndroidDriver driver;
    private final Map<String, String> app = Map.of("appId", "io.hexlet.notes");

    @BeforeEach void connect() throws Exception {
        var options = new UiAutomator2Options().setUdid(System.getenv("ANDROID_SERIAL"))
            .setAppPackage(app.get("appId")).setAppActivity(".Notes");
        driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
    }

    @AfterEach void disconnect() {
        if (driver != null) driver.quit();
    }

    void emptyFormAndStorage() {
        driver.executeScript("mobile: terminateApp", app);
        driver.executeScript("mobile: clearApp", app);
        driver.executeScript("mobile: activateApp", app);
    }

    void restartKeepingNotes() {
        driver.executeScript("mobile: terminateApp", app);
        driver.executeScript("mobile: activateApp", app);
    }

    void saveTitle(String value) {
        var field = driver.findElement(AppiumBy.id("title_input"));
        field.clear();
        field.sendKeys(value);
        driver.findElement(AppiumBy.id("save")).click();
    }

    void createNote() {
        saveTitle("Harbor");
        assertEquals("Harbor", driver.findElement(AppiumBy.id("result")).getText());
    }

    void editNote() {
        driver.findElement(AppiumBy.id("open_list")).click();
        var rows = driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().text(\"Harbor\")"));
        if (rows.isEmpty()) throw new AssertionError("Harbor is missing");
        assertEquals(1, rows.size());
        rows.get(0).click();
        saveTitle("Port");
        assertEquals("Port", driver.findElement(AppiumBy.id("result")).getText());
    }

    void independentCreate() {
        emptyFormAndStorage();
        createNote();
    }

    void independentEdit() {
        emptyFormAndStorage();
        saveTitle("Harbor");
        editNote();
    }

    void expectMissingNote() {
        var error = assertThrows(AssertionError.class, this::editNote);
        assertEquals("Harbor is missing", error.getMessage());
    }

    @Test void dependentOrders() {
        emptyFormAndStorage();
        createNote();
        restartKeepingNotes();
        editNote();

        emptyFormAndStorage();
        expectMissingNote();
        restartKeepingNotes();
        createNote();
    }

    @Test void editAlone() {
        emptyFormAndStorage();
        expectMissingNote();
        independentEdit();
    }

    @Test void independentOrders() {
        independentCreate();
        independentEdit();
        independentEdit();
        independentCreate();
    }
}
