package pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.Status;

import library.Base;
import library.DataBase;
import library.Excel;

public class LoginPage extends Base {

	Logger logger = Logger.getLogger(LoginPage.class);
	String title;

	public void login(String username, String password) {

		title = "Dashboard ‹ Wordpress Demo Site at Demo.Center — WordPress";

		driver.get("http://demosite.center/wordpress/wp-login.php");
		// driver.navigate().to("javascript:document.getElementById(‘overridelink’).click()");
		driver.findElement(By.id("user_login")).sendKeys(username);
		driver.findElement(By.id("user_pass")).sendKeys(password);
		driver.findElement(By.id("wp-submit")).click();

		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.titleIs(title));

		String url = driver.getCurrentUrl();
		String loginUrl = "http://demosite.center/wordpress/wp-admin/";

		Assert.assertEquals(url, loginUrl);

	}

	@DataProvider(name = "pass")
	public String[][] credentials() {
		String[][] data = null;
		try {
			Excel excel = new Excel("src/test/resources/login.xlsx", 0);
			logger.info("Reading data .........");
			data = excel.getExcelData();

		} catch (Exception e) {
			logger.info(e.getStackTrace());
		}
		return data;
	}

	@DataProvider(name = "dbase") // reading data from data base
	public String[][] credit() {

		String[][] data = null;
		try {
			
			DataBase db = new DataBase("select * from password");
			logger.info("Reading data .........");
			data = db.readSQLQuery();

		} catch (Exception e) {
			logger.error("Error :" + e.getMessage());
			test.log(Status.ERROR, e.getMessage());
		}
		return data;

	}
}
