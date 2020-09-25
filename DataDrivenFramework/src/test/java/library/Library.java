package library;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertTrue;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Library {
	boolean isDemo = false;
	Logger logger = Logger.getLogger(Library.class);
	public List<String> errorScreenshots;

	private static WebDriver driver;
	boolean remote = false;

	public Library(WebDriver _driver) {
		Library.driver = _driver;
	}

	public static String removeExtention(String Path) {
		String fileNameWithOutExt = FilenameUtils.removeExtension(Path);
		return fileNameWithOutExt;
	}

	public WebDriver startBrowser(String browser) {
		try {

			// if (browser.toLowerCase().contains("chrome")) {

			switch (browser) {
			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver();
				break;
			case "chrome":
				System.out.println("----Chrome----");
				WebDriverManager.chromedriver().setup();
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--disable-popup-blocking");
				options.addArguments("start-maximized");
				options.addArguments("test-type");
				options.addArguments("allow-running-insecure-content");
				options.addArguments("disable-extensions");
				options.addArguments("--ignore-certificate-errors");
				options.addArguments("test-type=browser");
				options.addArguments("disable-infobars");
				driver = new ChromeDriver(options);
				break;

			case "edge":
				WebDriverManager.edgedriver().setup();
				driver = new EdgeDriver();
				break;
			case "ie":
				WebDriverManager.iedriver().setup();
				driver = new InternetExplorerDriver();
				break;
			case "remotechrome":
				DesiredCapabilities capabilities = new DesiredCapabilities().chrome();
				capabilities.setPlatform(Platform.ANY);
				try {
					driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				break;
			case "remotefirefox":
				DesiredCapabilities firefoxcapabilities = new DesiredCapabilities().firefox();
				firefoxcapabilities.setPlatform(Platform.ANY);
				try {
					driver = new RemoteWebDriver(new URL("url"), firefoxcapabilities);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				break;
			}

		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		return driver;
	}

	public WebDriver firefox() {
		try {
			logger.info("Start Firefox .....");
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		return driver;
	}

	public WebDriver startChrome() {
		try {
			logger.info("Starting Chrome.....");

			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		return driver;
	}

	public String getCurrentTime() {
		String finalTime = null;
		try {
			Date date = new Date();
			String tempTime = new Timestamp(date.getTime()).toString();
			logger.info(tempTime);
			finalTime = tempTime.replace("-", "").replace(" ", "").replace(":", "").replace(".", "");
			logger.info("Generating clock " + finalTime);

		} catch (Exception e) {
			logger.error(e.getMessage());
			assertTrue(false);
		}

		return finalTime;
	}

	public String screenCapture(String screenshotFileName) {
		String filePath = null;
		String fileName = null;
		try {
			fileName = screenshotFileName + getCurrentTime() + ".png";
			filePath = "target/screenshots/";

			File tempfile = new File(filePath);
			if (!tempfile.exists()) {
				tempfile.mkdirs();
			}
			filePath = tempfile.getAbsolutePath();

			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			Files.copy(scrFile, new File(filePath + "/" + fileName));

		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		logger.info(filePath + "/" + fileName);
		return filePath + "/" + fileName;
	}

	public List<String> automaticallyAttachErrorImgToEmail() {
		List<String> fileNames = new ArrayList<String>();
		JavaPropertiesManager propertyReader = new JavaPropertiesManager("src/test/resources/SessionTime.properties");
		String tempTimeStamp = propertyReader.readData("SessionTime");
		String numberTimeStamp = tempTimeStamp.replaceAll("_", "");
		long testStartTime = Long.parseLong(numberTimeStamp);
		File file = new File("target/screenshots"); // first check if error-screenshot folder has file
		if (file.isDirectory()) {
			if (file.list().length > 0) {
				File[] screenshotFiles = file.listFiles();
				for (int i = 0; i < screenshotFiles.length; i++) {
					// checking if file is a file, not a folder
					if (screenshotFiles[i].isFile()) {
						String eachFileName = screenshotFiles[i].getName();
						logger.debug("Testing file names: " + eachFileName);
						int indexOf20 = searchSubstringInString("20", eachFileName);
						String timeStampFromScreenshotFile = eachFileName.substring(indexOf20,
								eachFileName.length() - 4);
						logger.debug("Testing file timestamp: " + timeStampFromScreenshotFile);
						String fileNumberStamp = timeStampFromScreenshotFile.replaceAll("_", "");
						long screenshotfileTime = Long.parseLong(fileNumberStamp);

						testStartTime = Long.parseLong(numberTimeStamp.substring(0, 14));
						screenshotfileTime = Long.parseLong(fileNumberStamp.substring(0, 14));
						if (screenshotfileTime > testStartTime) {
							fileNames.add("target/screenshots/" + eachFileName);
							logger.info("Screenshots attaching: " + eachFileName);
						}
					}
				}
			}
		}
		errorScreenshots = new ArrayList<String>();
		errorScreenshots = fileNames;
		return fileNames;
	}

	public int searchSubstringInString(String target, String message) {
		int targetIndex = 0;
		for (int i = -1; (i = message.indexOf(target, i + 1)) != -1;) {
			targetIndex = i;
			break;
		}
		return targetIndex;
	}

	public void validateGet(String EndPoint, int statuesCode) {

		given().when().get(EndPoint).then().assertThat().statusCode(200).header("content-type",
				equalTo("application/json; charset=utf-8"));

	}

	public void validateGet(String EndPoint, int statuesCode, String jsonPath, String expected) {

		given().when().get(EndPoint).then().assertThat().statusCode(200).body(jsonPath, equalTo(expected))
				.header("content-type", equalTo("application/json; charset=utf-8"));

	}

	public void validatePost(String body, String EndPoint, int StatuesCode) {

		given().body(body).when().post(EndPoint).then().assertThat().statusCode(StatuesCode)
				.body("token", notNullValue()).header("content-type", equalTo("application/json; charset=utf-8"));
	}

	public void validatePost(String body, String EndPoint, int StatuesCode, String jsonPath, String expected) {

		given().body(body).when().post(EndPoint).then().assertThat().statusCode(StatuesCode)
				.body(jsonPath, equalTo(expected)).header("content-type", equalTo("application/json; charset=utf-8"));
	}

	public void switchToWindowByTitle(String title) {
		Set<String> windows = driver.getWindowHandles();
		System.out.println("Amount of windows that are currently present :: " + windows.size());
		for (String window : windows) {
			driver.switchTo().window(window);
			if (driver.getTitle().startsWith(title) || driver.getTitle().equalsIgnoreCase(title)) {
				break;
			} else {
				continue;
			}
		}
	}

	public void selectDropDown(By by, int index) {
		try {
			WebElement element = driver.findElement(by);
			// highlightElement(element);
			Select dropDown = new Select(element);
			dropDown.selectByIndex(index);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void selectDropDown(By by, String visibleText) {
		try {
			WebElement element = driver.findElement(by);
			// highlightElement(element);
			Select dropDown = new Select(element);
			dropDown.selectByVisibleText(visibleText);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void selectDropDown(String attributeValue, By by) {
		try {
			WebElement element = driver.findElement(by);
			// highlightElement(element);
			Select dropDown = new Select(element);
			dropDown.selectByValue(attributeValue);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void enterText(By by, String textString) {
		try {
			WebElement element = driver.findElement(by);
			// highlightElement(element);
			element.clear();
			element.sendKeys(textString);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void clickButton(WebElement element) {
		try {
			// highlightElement(element);
			element.click();
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}

	}

	public WebDriver switchToWindow(int browserIndex) {
		try {
			Set<String> allBrowsers = driver.getWindowHandles();
			Iterator<String> iterator = allBrowsers.iterator();
			List<String> windowHandles = new ArrayList<>();
			while (iterator.hasNext()) {
				String window = iterator.next();
				windowHandles.add(window);
			}
			// switch to index N
			driver.switchTo().window(windowHandles.get(browserIndex));
			highlightElement(By.tagName("body"));
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		return driver;
	}

	public void clickWithJS(WebElement elementtoclick) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.elementToBeClickable(elementtoclick));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementtoclick);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementtoclick);
	}

	public void waitForPresenceOfElementByCss(String css) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(css)));
	}

	public void waitForVissibilityOfElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, 20);

		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForElement(WebElement element) {
		int i = 0;
		while (i < 10) {
			try {
				element.isDisplayed();
				break;
			} catch (WebDriverException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				i++;
			}
		}
	}

	public boolean verifyElementIsNotPresent(String xpath) {
		List<WebElement> elemetns = driver.findElements(By.xpath(xpath));
		return elemetns.size() == 0;
	}

	public boolean verifyElementIsNotPresent(By by) {
		List<WebElement> elemetns = driver.findElements(by);
		return elemetns.size() == 0;
	}

	public void scrollToElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void hitEnterUsingRobot() {
		Robot rb;
		try {
			rb = new Robot();
			rb.keyPress(KeyEvent.VK_ENTER);
			rb.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean verifyAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException Ex) {
			System.out.println("Alert is not presenet");
		}
		return false;
	}

	public void waitUntilPageLoad() {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until((d) -> {
			Boolean isPageLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript("return document.readyState")
					.equals("complete");
			if (!isPageLoaded)
				System.out.println("Document is loading");
			return isPageLoaded;
		});
	}

	public void waitForStaleElement(WebElement element) {
		int y = 0;
		while (y <= 15) {
			try {
				element.isDisplayed();
				break;
			} catch (StaleElementReferenceException st) {
				y++;
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (WebDriverException e) {
				y++;
				try {
					Thread.sleep(300);
				} catch (InterruptedException ew) {
					e.printStackTrace();
				}
			}
		}
	}

	public WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
		WebDriverWait wait = new WebDriverWait(driver, timeToWaitInSec);
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	public WebElement waitForVisibility(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement waitForClickablility(WebElement element, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public WebElement waitForClickablility(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public void waitForPageToLoad(long timeOutInSeconds) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		try {
			System.out.println("Waiting for page to load...");
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			wait.until(expectation);
		} catch (Throwable error) {
			System.out.println(
					"Timeout waiting for Page Load Request to complete after " + timeOutInSeconds + " seconds");
		}
	}

	public String convertDateFormat(String OriginalFormat, String TargetFormat, String Date) {
		DateFormat original = new SimpleDateFormat(OriginalFormat, Locale.ENGLISH);
		DateFormat target = new SimpleDateFormat(TargetFormat);
		String formattedDate = null;
		try {
			Date date = original.parse(Date);
			formattedDate = target.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formattedDate;
	}

	public void highlightElement(By by) {
		try {
			if (isDemo == true) {
				WebElement element = driver.findElement(by);
				for (int i = 0; i < 4; i++) {
					WrapsDriver wrappedElement = (WrapsDriver) element;
					JavascriptExecutor js = (JavascriptExecutor) wrappedElement.getWrappedDriver();
					js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element,
							"color: red; border: 2px solid yellow");
					Thread.sleep(500);
					js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void fileUpload(By by, String fileRelativePath) {
		try {
			WebElement fileUploadElem = driver.findElement(by);
			// highlightElement(fileUploadElem);
			File tempFile = new File(fileRelativePath);
			String fullPath = tempFile.getAbsolutePath();
			logger.info("file uploading : " + fullPath);

			if (remote == true) {
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			}
			fileUploadElem.sendKeys(fullPath);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public Alert isAlertPresent() {
		Alert alert = null;
		try {
			alert = driver.switchTo().alert();
			logger.info("Popup Alert detected: {" + alert.getText() + "}");
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
		return alert;
	}

	public void closeAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			logger.info("Popup Alert detected: {" + alert.getText() + "}");
			alert.dismiss();
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void acceptAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			logger.info("Popup Alert detected: {" + alert.getText() + "}");
			alert.accept();
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void scrollByVertically(String verticalPixel) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("scroll(0," + verticalPixel + ")");
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void scrollByHorizontally(String horizontalPixel) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("scroll(" + horizontalPixel + ",0)");
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public void sendKeyBoard(CharSequence... keysToSend) {
		try {
			WebElement webpage = driver.findElement(By.tagName("body"));
			// highlightElement(webpage);
			webpage.sendKeys(keysToSend);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

}
