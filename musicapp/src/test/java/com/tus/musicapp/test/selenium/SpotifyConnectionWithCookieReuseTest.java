//package com.tus.musicapp.test.selenium;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.*;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//import org.openqa.selenium.support.ui.*;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.io.File;
//import java.time.Duration;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class SpotifyConnectionWithCookieReuseTest {
//
//    private ChromeDriver driver;
//    private WebDriverWait wait;
//
//    @BeforeEach
//    void setUp() {
//        WebDriverManager.chromedriver().clearDriverCache().setup();
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-allow-origins=*");
//        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-popup-blocking");
//        options.addArguments("--start-maximized");
//
//        driver = new ChromeDriver(options);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//    }
//
//    @Test
//    void testMusicAppIntegrationWithLoadedSpotifyCookies() throws Exception {
//        // Load only Spotify-compatible cookies
//        ObjectMapper mapper = new ObjectMapper();
//        List<Map<String, Object>> cookies = mapper.readValue(
//                new File("src/test/resources/spotify_cookies.json"), new TypeReference<>() {}
//        );
//
//        // Navigate to Spotify first (required to set cookies)
//        driver.get("https://open.spotify.com");
//
//        // Set only safe cookies like sp_t (not __Host-*)
//        for (Map<String, Object> cookieMap : cookies) {
//            String name = (String) cookieMap.get("name");
//            if (!name.equals("sp_t")) continue; // only load sp_t
//
//            Cookie cookie = new Cookie.Builder(name, (String) cookieMap.get("value"))
//                    .domain(".spotify.com")
//                    .path((String) cookieMap.getOrDefault("path", "/"))
//                    .isHttpOnly((Boolean) cookieMap.getOrDefault("httpOnly", false))
//                    .isSecure((Boolean) cookieMap.getOrDefault("secure", false))
//                    .build();
//
//            try {
//                driver.manage().addCookie(cookie);
//            } catch (InvalidCookieDomainException | UnableToSetCookieException e) {
//                System.out.println("⚠️ Skipping invalid cookie: " + name);
//            }
//        }
//
//        driver.navigate().refresh(); // Apply cookies
//        wait.until(ExpectedConditions.urlContains("spotify.com"));
//
//        // Log into MusicApp
//        driver.get("http://localhost:9091/");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("Admin");
//        driver.findElement(By.id("password")).sendKeys("Admin", Keys.ENTER);
//
//        // Click Connect to Spotify
//        WebElement connectBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[contains(text(),'Connect to Spotify')]")));
//        connectBtn.click();
//
//        // Select playlist
//        WebElement playlistImage = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector("img[src='https://i.scdn.co/image/ab67616d00001e02638404f3498c5c96c220cbf8']")));
//        playlistImage.click();
//
//        WebElement playBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector(".btn.btn-primary.play-btn[data-url='https://open.spotify.com/track/1ZozJfi8u9cO2Ob8KwiwNT']")));
//        playBtn.click();
//
//        WebElement playOnSpotifyBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector(".btn.btn-success")));
//        playOnSpotifyBtn.click();
//
//        // Handle new tab
//        wait.until(driver -> driver.getWindowHandles().size() > 1);
//        String mainTab = driver.getWindowHandle();
//        Set<String> allTabs = driver.getWindowHandles();
//
//        for (String handle : allTabs) {
//            if (!handle.equals(mainTab)) {
//                driver.switchTo().window(handle);
//                break;
//            }
//        }
//
//        // Validate successful Spotify redirect
//        wait.until(ExpectedConditions.urlContains("open.spotify.com"));
//        String finalUrl = driver.getCurrentUrl();
//        System.out.println("✅ Spotify redirect successful: " + finalUrl);
//        assertTrue(finalUrl.contains("open.spotify.com"));
//
//        // Close Spotify tab and logout
//        driver.close();
//        driver.switchTo().window(mainTab);
//
//        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.tagName("button")));
//        logoutBtn.click();
//
//        driver.navigate().refresh();
//        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).isDisplayed());
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//}
