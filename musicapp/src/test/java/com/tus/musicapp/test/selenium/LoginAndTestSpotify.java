package com.tus.musicapp.test.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class LoginAndTestSpotify {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--user-data-dir=C:/SeleniumChromeProfile");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    void testLoginAndSpotifyIntegration() {
        driver.get("http://localhost:9091/");

        driver.findElement(By.id("username")).sendKeys("Admin");
        driver.findElement(By.id("password")).sendKeys("Admin");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🔗 Connect to Spotify')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Continue with Google']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("identifierId"))).sendKeys("emilagvan", Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Passwd"))).sendKeys("Turki99!", Keys.ENTER);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[@class='sorting_1']//img"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[@class='even']//button[contains(text(),'🎵 Play')]"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Play on Spotify']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🎼 User Playlist')]"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[@class='odd']//button[contains(text(),'🗑 Remove')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'⭐ Charts')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🎤 Gigs')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🏠 Home')]"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'🚪 Logout')]"))).click();

        driver.navigate().refresh();

        assertEquals("http://localhost:9091/", driver.getCurrentUrl(), "Should return to the homepage after logout and refresh.");
    }


    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
