package com.tus.musicapp.test.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveSongsAndViewPagesTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:9091/";
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeEach
    public void setUp() {
    	WebDriverManager.chromedriver().setup();
    	WebDriverManager.chromedriver().clearDriverCache().setup();

        ChromeOptions options = new ChromeOptions();
        
        // Chrome stability flags
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized"); // Ensure the browser opens in maximized mode
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
    }

    @Test
    @DisplayName("Remove Song, View Pages")
    public void removeSong() throws InterruptedException {
        driver.get(baseUrl);

        // Wait until username field appears
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.clear();
        usernameField.sendKeys("Admin");

        // Wait until password field appears
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.clear();
        passwordField.sendKeys("Admin");
        
        // Wait until the login button is clickable
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
        loginButton.click();
        
        // Click on "🎼 User Playlist"
        WebElement userPlaylist = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'🎼 User Playlist')]")
        ));
        userPlaylist.click();

        // Find the First "Remove" Button in the Actions Column
        WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//td[contains(@class, 'sorting_1')]/following-sibling::td//button[contains(text(),'Remove')]")
        ));

        // Scroll to it
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", removeBtn);
        Thread.sleep(500); // ✅ Wait for scrolling to complete

        // Click Remove (Using JavaScript Fallback)
        try {
            removeBtn.click();
            System.out.println("✅ Clicked 'Remove' button.");
        } catch (Exception e) {
            System.out.println("⚠️ Normal click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", removeBtn);
        }
        
        // Handle Confirmation Alert
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("✅ Alert Found: " + alert.getText());
            alert.accept(); // ✅ Click "OK"
            System.out.println("✅ Accepted the alert.");
        } catch (Exception e) {
            System.out.println("⚠️ No alert found.");
        }

        // Verify Row Removal
        Thread.sleep(1000); // ✅ Give time for row removal
        List<WebElement> rowsAfter = driver.findElements(By.cssSelector("#trackTable tbody tr"));
        assertTrue(rowsAfter.size() < 1, "✅ The track should be removed.");

        // Navigate to Charts page
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'⭐ Charts')]"))).click();

        // Navigate to Gigs page
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🎤 Gigs')]"))).click();

        // Return to the homepage from the Gigs page
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🏠 Home')]"))).click();

        // Logout action
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🚪 Logout')]"))).click();

        // Refresh the page and verify the URL
        driver.navigate().refresh();
        assertEquals("http://localhost:9091/", driver.getCurrentUrl(), "Should return to the homepage after logout and refresh.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
