package library;

import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DataBase {
	
//This is a data base reader script
	final static Logger logger = Logger.getLogger(DataBase.class);

	private String dataBaseServerName;
	private String dataBasePort;
	private String dataBaseName;
	private String dataBaseSID;
	private String userName;
	private String userPassword;

	private String connectionURL = null;
	private ResultSet rs = null;
	private Statement statement = null;
	private Connection connection = null;
	private String sqlQuery;

	public DataBase(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
    
	private void connectToOracleDB() {
		JavaPropertiesManager property = new JavaPropertiesManager(
				"/Users/pitiasfessahaie/git/DataDrivenFramework/DataDrivenFramework/src/test/resources/config.properties");

		dataBaseServerName = property.readData("dataBaseServerName");
		dataBasePort = property.readData("dataBasePort");
		dataBaseSID = property.readData("dataBaseSID");
		dataBaseName = property.readData("dataBaseName");
		userName = property.readData("userName");
		userPassword = property.readData("userPassword");

		try {
			connectionURL = "jdbc:oracle:thin:" + dataBaseName + "@//" + dataBaseServerName + ":" + dataBasePort + "/"
					+ dataBaseSID;
			Class.forName("oracle.jdbc.OracleDriver");
			logger.info("Reading From ---> "+connectionURL);
			logger.info("Signing with credentials .........");
			connection = DriverManager.getConnection(connectionURL, userName, userPassword);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY

			);

			if (connection != null) {
				logger.info("DataBase Connection is Success !!!");

			} else {
				logger.info("Failure to Connect to the Database !!!");
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}
	}

	public String[][] readSQLQuery() {
		String data[][] = null;
		int col = 0;
		int row = 0;
		try {
			
			connectToOracleDB();
			logger.debug(connectionURL);
			rs = statement.executeQuery(sqlQuery);
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

				}
			}

		} catch (Exception e) {
			logger.error("Error: ", e);
			assertTrue(false);
		}

		return data;
	}
}
