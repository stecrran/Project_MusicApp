package com.tus.musicapp.test.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AddSong {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // Dynamically resolve the ChromeProfile path
        Path profilePath = Paths.get("src", "test", "resources", "profile", "ChromeProfile").toAbsolutePath();
        options.addArguments("--user-data-dir=" + profilePath.toString());
        options.addArguments("--profile-directory=Default");

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @DisplayName("Verify Spotify Connection")
    @Test
    void testLoginAndSpotifyIntegration() throws InterruptedException {
        driver.get("http://localhost:9091/");
        
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
                
        // Click on "My Playlist #1"
        WebElement playlistImg = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("img[src='https://i.scdn.co/image/ab67616d00001e02638404f3498c5c96c220cbf8']")
        ));
        playlistImg.click();

        // Click on "🎵 Play"
        WebElement playButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".btn.btn-primary.play-btn[data-url='https://open.spotify.com/track/1ZozJfi8u9cO2Ob8KwiwNT']")
        ));
        playButton.click();

        // Click on "Play on Spotify"
        WebElement playOnSpotify = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".btn.btn-success")
        ));
        playOnSpotify.click();
        
        // Wait for a new tab to open
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        // Switch to the newly opened tab
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }

        // Confirm URL contains "open.spotify.com"
        String currentUrl = driver.getCurrentUrl();
        System.out.println("🔗 Spotify Opened URL: " + currentUrl);
        assertTrue(currentUrl.contains("open.spotify.com"), "Spotify page did not open.");

        // Close the Spotify tab and switch back
        driver.close();
        driver.switchTo().window(driver.getWindowHandles().iterator().next());

        System.out.println("✅ Spotify page opened successfully and verified.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}