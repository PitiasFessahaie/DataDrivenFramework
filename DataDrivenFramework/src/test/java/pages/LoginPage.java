package pages;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.log4testng.Logger;


import com.aventstack.extentreports.Status;

import library.Base;
import library.DataBase;
import library.ReadExcel;

public class LoginPage extends Base {
	
	Logger logger = Logger.getLogger(LoginPage.class);
	String title;

	
	public void login(String username, String password){

		title = "Dashboard ‹ Wordpress Demo Site at Demo.Center — WordPress";
		
		driver.get("http://demosite.center/wordpress/wp-login.php");
		//driver.navigate().to("javascript:document.getElementById(‘overridelink’).click()");
		driver.findElement(By.id("user_login")).sendKeys(username);
		driver.findElement(By.id("user_pass")).sendKeys(password);
		driver.findElement(By.id("wp-submit")).click();
		
		WebDriverWait wait =new WebDriverWait(driver,4);
		wait.until(ExpectedConditions.titleIs(title));
        
        String url = driver.getCurrentUrl();
		String loginUrl = "http://demosite.center/wordpress/wp-admin/";
		
        Assert.assertEquals(url,loginUrl);
        
	}

	
	@DataProvider(name = "pass")          //reading Excel data
	public Object[][] credentials() {

		ReadExcel excel = new ReadExcel("src/test/resources/login.xlsx");
		int row = excel.getRow(0);

		Object[][] data = new Object[row][2];

		for (int i = 0; i < row; i++) {
			data[i][0] = excel.getData(0, i, 0);
			data[i][1] = excel.getData(0, i, 1);

		}
		return data;

	}

	@DataProvider(name = "dbase")         //reading data from data base
	public String[][] credit() {

		int col = 0;
		int row = 0;
		String data[][] = null;
		DataBase db = new DataBase();
		try {

			ResultSet rs = db.runSQLQuery("select * from password");
			ResultSetMetaData rsmd = rs.getMetaData();
			col = rsmd.getColumnCount();

			while (rs.next())
				row++;

			data = new String[row][col];

			rs.beforeFirst();

			for (int i = 0; i < row; i++) {
				rs.next();
				for (int j = 1; j <= col; j++) {
					data[i][j - 1] = rs.getString(j);
					logger.info(data[i][j - 1]);
					test.log(Status.DEBUG, data[i][j - 1]);
				}
			}

		} catch (Exception e) {
			logger.error("Error :" + e.getMessage());
             test.log(Status.ERROR, e.getMessage());
		}
		return data;

	}
}
