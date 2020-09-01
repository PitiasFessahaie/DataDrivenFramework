package library;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.io.Files;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Library {
	Logger logger = Logger.getLogger(Library.class);
	public List<String> errorScreenshots;
	
	private WebDriver driver;

	public Library(WebDriver driver) {
		this.driver = driver;
	}
	
	public static String removeExtention(String Path) {
		String fileNameWithOutExt = FilenameUtils.removeExtension(Path);
		return fileNameWithOutExt;
	}

	public WebDriver startBrowser(String browser) {
		try {

			if (browser.toLowerCase().contains("chrome")) {
				driver = startChrome();
			} else if (browser.toLowerCase().contains("firefox")) {
				driver = firefox();
			} else {
				logger.info("The " + browser + " typed is not recognized Starting a default Chrome Browser");
				driver = startChrome();
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

	static public void screenShotOnError(WebDriver driver) {
		try {
			long epoch = System.currentTimeMillis();
			String ssTimestamp = String.valueOf(epoch);
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			Files.copy(scrFile, new File(
					"/Users/pitiasfessahaie/eclipse-workspace2/Keyword/DataDrivenFramework/screenshots/" + ssTimestamp + ".jpg"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public List<String> automaticallyAttachErrorImgToEmail() {
		List<String> fileNames = new ArrayList<String>();
		JavaPropertiesManager propertyReader = new JavaPropertiesManager("src/test/resources/SessionTime.properties");
		String tempTimeStamp = propertyReader.readProperties("SessionTime");
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
						}}}}}
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
	
	
	public void validateGet(String EndPoint,int statuesCode) {
		
		given().when().get(EndPoint).then().assertThat().statusCode(200)
		.header("content-type",equalTo("application/json; charset=utf-8"));
	   
	}

	public void validateGet(String EndPoint,int statuesCode,String jsonPath,String expected) {
		
		given().when().get(EndPoint).then().assertThat().statusCode(200)
		.body(jsonPath,equalTo(expected))
		.header("content-type",equalTo("application/json; charset=utf-8"));
	   
	}
	public void validatePost(String body,String EndPoint,int StatuesCode) {
	
		given().body(body).when().post(EndPoint).then().assertThat().statusCode(StatuesCode)
		.body("token", notNullValue()).header("content-type", equalTo("application/json; charset=utf-8"));
	}
	public void validatePost(String body,String EndPoint,int StatuesCode,String jsonPath,String expected) {
		
		given().body(body).when().post(EndPoint).then().assertThat().statusCode(StatuesCode)
		.body(jsonPath, equalTo(expected)).header("content-type", equalTo("application/json; charset=utf-8"));
	}
	
}
