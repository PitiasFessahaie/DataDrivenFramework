package automationScripts;

import org.testng.annotations.Test;


import library.Base;
import pages.LoginPage;



public class Script extends Base {

	@Test(dataProvider = "pass", dataProviderClass = LoginPage.class, enabled=false)
	public void dataExcel(String username, String password) {
		LoginPage log = new LoginPage();
		log.login(username, password);     //set the Credentials

	}

	@Test(dataProvider = "dbase", dataProviderClass = LoginPage.class , enabled=true)
	public void dataBase(String username, String password) {
		LoginPage log = new LoginPage();
		log.login(username, password);

	}
	
	
}
