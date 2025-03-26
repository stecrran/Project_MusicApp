package com.tus.musicapp.test.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyConnectionTest {

    private ChromeDriver driver;
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
    void testSpotifyLogin() throws InterruptedException {
        driver.get("https://accounts.spotify.com/en/login");

        // Wait until username field is visible
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-password")));

        // Use JavaScript to autofill (CDP-style)
        ((JavascriptExecutor) driver).executeScript("""
            document.querySelector('#login-username').value = 'johntest422@gmail.com';
            document.querySelector('#login-password').value = 'Admin1234!';
        """);

        // Wait to simulate real typing (Spotify sometimes detects bots)
        Thread.sleep(800);

        // Now click login using Selenium
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[data-testid='login-button']")));
        loginButton.click();

        // Wait for Spotify to redirect after successful login
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("spotify.com"),
                ExpectedConditions.not(ExpectedConditions.urlContains("login"))
        ));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("✅ Redirected to: " + currentUrl);

        assertFalse(currentUrl.contains("login"), "Still on login page – login may have failed.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
