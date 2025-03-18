package com.tus.musicapp.test.selenium;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class LoginConnectToSpotifyTest {
	
  private WebDriver driver;
  private String baseUrl;
  private WebDriverWait wait;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  JavascriptExecutor js;
  
  @BeforeEach
  public void setUp() throws Exception {
  	WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    
    // Add Chrome stability flags
    options.addArguments("--incognito"); 
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    options.addArguments("--start-maximized"); // Ensure the browser opens in maximized mode
    
    driver = new ChromeDriver(options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void test1() throws Exception {
    driver.get("http://localhost:9091/");
    driver.findElement(By.id("username")).click();
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys("Admin");
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("Admin");
    driver.findElement(By.id("password")).sendKeys(Keys.ENTER);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='app']/div/div/div/button")));
    button.click();
    driver.get("https://accounts.spotify.com/en/login?continue=https%3A%2F%2Faccounts.spotify.com%2Fauthorize%3Fscope%3Duser-read-private%2Bplaylist-read-private%2Bplaylist-read-collaborative%26response_type%3Dtoken%26redirect_uri%3Dhttp%253A%252F%252Flocalhost%253A9091%252F%26client_id%3D95ac7e92f6d2415d82a2992f37e23a5b");
    driver.findElement(By.xpath("//div[@id='root']/div/div/div/div/ul/li/button/span[2]")).click();
    driver.get("https://accounts.google.com/v3/signin/identifier?opparams=%253F&dsh=S1832382845%3A1742328138147542&access_type=offline&client_id=1046568431490-ij1gi5shcp2gtorls09frkc56d4mjbe2.apps.googleusercontent.com&ddm=1&o2v=2&redirect_uri=https%3A%2F%2Faccounts.spotify.com%2Flogin%2Fgoogle%2Fredirect&response_type=code&scope=profile+email+openid&service=lso&state=AQBDepQXfHDLu0X05rPs%2BhqVLGn1xNTdvu4oju7kpKprlF5aIqlZzraMFX%2FvAwGXXc3LrySFHYvp6duB%2FWNOtae%2BcqmGIMucR%2BKtSkDVFEOh92nS2gHD7vOa%2F1GjkaRC97VIRFzFZK9qknPkbdGtR1GDuJv5Tsw8yBj%2FMBv%2Fk%2FxQIpmuVqDbSePT%2BFzwEvG2JMWAipQJq4mIrVnx9jW9dlOTkGG7ama9G20DeP53SEXnChkL7z7yrY86hkTCWDZvA%2FfHVs2i9C%2FF4evo1Llcw%2BWpRqvenx%2FDp98ACOEc1g6J2bdbq5mLo4IhlnjFZUleU3J9W5GxGpVFMR3UkrSKVp0lnvNC4hxKqdSR6O8FPPRPaEDniy3hdKP5ellHGukeItoctpqdpeIX4f5095Pf4ck6WsLFPO4LKmiRdHtcSg7JiFwfXeKNp6q1XFvjf%2BuE%2Bo8qyv4%2FzZCovimFUxT1aH6fxAXSYKWX36ViLth9O%2FpqOXxY2pi6HnFEHGn3BfdIBriA0290%2BODnG7FZ%2F%2BkojsBpY%2BfEvKeX0tl5EGnOLLFMbasF3f3hlq%2BLZkVgqxSKYZsrNFin3SgKl1e6r23uHiXWCbffXOsVhMIksfFicPnC7SVKaX2uyDuBVNQYjN3iaAtLzncaSnc%3D&flowName=GeneralOAuthFlow&continue=https%3A%2F%2Faccounts.google.com%2Fsignin%2Foauth%2Fconsent%3Fauthuser%3Dunknown%26part%3DAJi8hAPRPiZLVT8Odc57CTfi2BEvH-ewPI-87mVm1teqwmMvJuEVf8epqo9vRtT6UGJc_cJD-nGJ0W71DHQhNvMetAnFToSQM4bL__kO8UrTo6PyRdSi3D5cxmJSFIuj61G9w54P3bmDgEVRrQoxLqs7YC6_z2xMSFOsxk-Q7lMc_xgTQqQBgSgYHHc4Np3U9my9X-5mIIwXYDxK73etWekV_B84tTH-eh5ZqnWL3859pKHPgUXSvIV1XbSqtLtJVb3arZMTxwxMNEGea3d4JC71XhBjXfD_rQXbmuTR-YYojovoET51dTG-DNLF1Vpl9bXewh3_3Gv8sO3oeaDY7dfeesR4R5Rgkalx6jtEltlgG5yQ3UUBdtQI5Tsb57qkfyaWy1-NC6rMlXrWBL2-3Xstw0Hs78eM4f-pby0-xJqhGmrOdzvIuC_TPTEcsg15jRpVDymcKWqddO1n81CkFpBMA6Dbt9PRUWGyPhFsijiA6Kb2fTeUhqw%26flowName%3DGeneralOAuthFlow%26as%3DS1832382845%253A1742328138147542%26client_id%3D1046568431490-ij1gi5shcp2gtorls09frkc56d4mjbe2.apps.googleusercontent.com%23&app_domain=https%3A%2F%2Faccounts.spotify.com&rart=ANgoxceEMiVXKeg9AXDpEcnnek50DoIiP1KtgoZnV5w-dD5SR60_Ac9HS_A6eIXPGr9VSlql47bf_ESqrDpMQl4MQN4GscUdcb_F_b_6FZd8Nryycc0KFf8");
    driver.findElement(By.id("identifierId")).clear();
    driver.findElement(By.id("identifierId")).sendKeys("emilagvan");
    driver.findElement(By.id("identifierId")).sendKeys(Keys.ENTER);
    driver.get("https://accounts.google.com/v3/signin/challenge/pwd?TL=ADBLaQBChJD1x15w8PyMwKtfkoa9ghDSKVlpuhMB1IDYaydZGmNZYUCsKguGVn6u&access_type=offline&app_domain=https%3A%2F%2Faccounts.spotify.com&checkConnection=youtube%3A1173&checkedDomains=youtube&cid=2&client_id=1046568431490-ij1gi5shcp2gtorls09frkc56d4mjbe2.apps.googleusercontent.com&continue=https%3A%2F%2Faccounts.google.com%2Fsignin%2Foauth%2Fconsent%3Fauthuser%3Dunknown%26part%3DAJi8hAPRPiZLVT8Odc57CTfi2BEvH-ewPI-87mVm1teqwmMvJuEVf8epqo9vRtT6UGJc_cJD-nGJ0W71DHQhNvMetAnFToSQM4bL__kO8UrTo6PyRdSi3D5cxmJSFIuj61G9w54P3bmDgEVRrQoxLqs7YC6_z2xMSFOsxk-Q7lMc_xgTQqQBgSgYHHc4Np3U9my9X-5mIIwXYDxK73etWekV_B84tTH-eh5ZqnWL3859pKHPgUXSvIV1XbSqtLtJVb3arZMTxwxMNEGea3d4JC71XhBjXfD_rQXbmuTR-YYojovoET51dTG-DNLF1Vpl9bXewh3_3Gv8sO3oeaDY7dfeesR4R5Rgkalx6jtEltlgG5yQ3UUBdtQI5Tsb57qkfyaWy1-NC6rMlXrWBL2-3Xstw0Hs78eM4f-pby0-xJqhGmrOdzvIuC_TPTEcsg15jRpVDymcKWqddO1n81CkFpBMA6Dbt9PRUWGyPhFsijiA6Kb2fTeUhqw%26flowName%3DGeneralOAuthFlow%26as%3DS1832382845%253A1742328138147542%26client_id%3D1046568431490-ij1gi5shcp2gtorls09frkc56d4mjbe2.apps.googleusercontent.com%23&ddm=1&dsh=S1832382845%3A1742328138147542&flowName=GeneralOAuthFlow&o2v=2&opparams=%253F&pstMsg=1&rart=ANgoxceEMiVXKeg9AXDpEcnnek50DoIiP1KtgoZnV5w-dD5SR60_Ac9HS_A6eIXPGr9VSlql47bf_ESqrDpMQl4MQN4GscUdcb_F_b_6FZd8Nryycc0KFf8&redirect_uri=https%3A%2F%2Faccounts.spotify.com%2Flogin%2Fgoogle%2Fredirect&response_type=code&scope=profile%20email%20openid&service=lso&state=AQBDepQXfHDLu0X05rPs%2BhqVLGn1xNTdvu4oju7kpKprlF5aIqlZzraMFX%2FvAwGXXc3LrySFHYvp6duB%2FWNOtae%2BcqmGIMucR%2BKtSkDVFEOh92nS2gHD7vOa%2F1GjkaRC97VIRFzFZK9qknPkbdGtR1GDuJv5Tsw8yBj%2FMBv%2Fk%2FxQIpmuVqDbSePT%2BFzwEvG2JMWAipQJq4mIrVnx9jW9dlOTkGG7ama9G20DeP53SEXnChkL7z7yrY86hkTCWDZvA%2FfHVs2i9C%2FF4evo1Llcw%2BWpRqvenx%2FDp98ACOEc1g6J2bdbq5mLo4IhlnjFZUleU3J9W5GxGpVFMR3UkrSKVp0lnvNC4hxKqdSR6O8FPPRPaEDniy3hdKP5ellHGukeItoctpqdpeIX4f5095Pf4ck6WsLFPO4LKmiRdHtcSg7JiFwfXeKNp6q1XFvjf%2BuE%2Bo8qyv4%2FzZCovimFUxT1aH6fxAXSYKWX36ViLth9O%2FpqOXxY2pi6HnFEHGn3BfdIBriA0290%2BODnG7FZ%2F%2BkojsBpY%2BfEvKeX0tl5EGnOLLFMbasF3f3hlq%2BLZkVgqxSKYZsrNFin3SgKl1e6r23uHiXWCbffXOsVhMIksfFicPnC7SVKaX2uyDuBVNQYjN3iaAtLzncaSnc%3D");
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    WebElement passwordField = wait.until(
    	    ExpectedConditions.presenceOfElementLocated(By.name("Passwd"))
    	);
    	passwordField.sendKeys("Turki99!");
    	passwordField.sendKeys(Keys.ENTER);
    driver.get("https://accounts.google.ie/accounts/SetSID");
    driver.get("http://localhost:9091/#access_token=BQCqEcFzujm35MLQzp6ixuuaZxblnnyzlsHbkmIZwMymRfOTIxqsTttAZwJApv-viC3R8OVbMWc7W5N4KselH5xHBWxjlAGACtLHYcYa--n_9iEvqgzig30CWeuOum1gN69Cfls35C1wfZSyEwH-zx6pqflY9_LmndnS4p7vN7pyDJLwEtUvV_kjiCaqLJDN_ZjMv-WxDwYZeFoiZ5hBfdgaJEZBWy0K1Sghq0UAwtTOADpMNAWVIvvLsBZctQ&token_type=Bearer&expires_in=3600");
    driver.get("http://localhost:9091/");
    driver.findElement(By.xpath("//img[contains(@src,'https://i.scdn.co/image/ab67616d00001e02638404f3498c5c96c220cbf8')]")).click();
    driver.findElement(By.xpath("//table[@id='trackTable']/tbody/tr/td[4]/button")).click();
    driver.findElement(By.xpath("//div[@id='playModal']/div/div/div[3]/button[2]")).click();
    //ERROR: Caught exception [ERROR: Unsupported command [selectWindow | win_ser_1 | ]]
    driver.get("https://open.spotify.com/track/1ZozJfi8u9cO2Ob8KwiwNT");
    //ERROR: Caught exception [ERROR: Unsupported command [selectWindow | win_ser_local | ]]
    driver.get("http://localhost:9091/");
    driver.findElement(By.xpath("//div[@id='navbarNav']/div/button[5]")).click();
    driver.get("https://open.spotify.com/track/1ZozJfi8u9cO2Ob8KwiwNT");
    acceptNextAlert = true;
    driver.get("http://localhost:9091/");
    driver.findElement(By.xpath("//table[@id='musicTable']/tbody/tr/td[6]/button")).click();
    assertTrue(closeAlertAndGetItsText().matches("^Are you sure you want to delete this song[\\s\\S]$"));
    driver.findElement(By.xpath("//div[@id='navbarNav']/div/button[7]")).click();
  }

  @AfterEach
  public void tearDown() {
      if (driver != null) {
          driver.quit();
      }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
