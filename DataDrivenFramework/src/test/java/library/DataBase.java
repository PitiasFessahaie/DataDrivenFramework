package library;

import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DataBase {
	final static Logger logger = Logger.getLogger(DataBase.class);

	private String dataBaseServerName;
	private String dataBasePort;
	private String dataBaseName;
	private String dataBaseSID;
	private String userName;
	private String userPassword;

	private String connectionURL = null;
	private ResultSet resultSet = null;
	private Statement statement = null;
	private Connection connection = null;

	private void connectToOracleDB() {
		JavaPropertiesManager property = new JavaPropertiesManager("/Users/pitiasfessahaie/eclipse-workspace2/DataDriverFramework/src/test/resources/config.properties");
		
		dataBaseServerName = property.readProperties("dataBaseServerName");
		dataBasePort = property.readProperties("dataBasePort");
		dataBaseSID = property.readProperties("dataBaseSID");
		dataBaseName = property.readProperties("dataBaseName");
		userName = property.readProperties("userName");
		userPassword = property.readProperties("userPassword");

		try {
			connectionURL = "jdbc:oracle:thin:" + dataBaseName + "@//" + dataBaseServerName + ":" + dataBasePort + "/"
					+ dataBaseSID;
			Class.forName("oracle.jdbc.OracleDriver");
			connection = DriverManager.getConnection(connectionURL, userName, userPassword);
			statement = connection.createStatement(
					 ResultSet.TYPE_SCROLL_INSENSITIVE,
					    ResultSet.CONCUR_READ_ONLY
					
					);
			
			if(connection !=null) {
				logger.info("DataBase Connection is Success !!!");
				
			}else {
				logger.info("Failure to Connect to the Database !!!");
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public ResultSet runSQLQuery(String sqlQuery) {
		try {
			connectToOracleDB();
			logger.debug(connectionURL);
			resultSet = statement.executeQuery(sqlQuery);
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}

		return resultSet;
	}
}
