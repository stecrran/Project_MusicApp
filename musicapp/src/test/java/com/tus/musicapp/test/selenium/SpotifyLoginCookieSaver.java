//package com.tus.musicapp.test.selenium;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.*;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.Duration;
//import java.util.*;
//
//public class SpotifyLoginCookieSaver {
//
//    private ChromeDriver driver;
//    private WebDriverWait wait;
//    private final File cookieFile = new File("src/test/resources/spotify_cookies.json");
//
//    @BeforeEach
//    void setUp() {
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//
//        driver = new ChromeDriver(options);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
//    }
//
//    @Test
//    void saveSpotifyLoginCookies() throws IOException {
//        driver.get("https://accounts.spotify.com/en/login");
//
//        System.out.println("👉 Please log into Spotify manually...");
//        new Scanner(System.in).nextLine(); // Wait for user to hit Enter after login
//
//        Set<Cookie> cookies = driver.manage().getCookies();
//        new ObjectMapper().writeValue(cookieFile, cookies);
//
//        System.out.println("✅ Cookies saved to " + cookieFile.getAbsolutePath());
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (driver != null) driver.quit();
//    }
//}
