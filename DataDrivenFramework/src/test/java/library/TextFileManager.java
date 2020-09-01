package library;

import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.log4j.Logger;

public class TextFileManager {
	final static Logger logger = Logger.getLogger(TextFileManager.class);

	private String fileName;

	public TextFileManager(String filePath) {
		fileName = filePath;
	}

	public String readFile() {
		String finalTxt = null;
		String line = null;
		String newLine = System.lineSeparator();
		try {
			FileReader file = new FileReader(fileName);
			BufferedReader bfr = new BufferedReader(file);
			StringBuffer sb = new StringBuffer();

			while ((line = bfr.readLine()) != null) {
				sb.append(line + newLine);

			}
			finalTxt = sb.toString();
			bfr.close();
			file.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
			assertTrue(false);
		}
		return finalTxt;
	}

	public void writeFile(String inputData) {
		try {
			FileWriter file = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(file);
			bw.write(inputData);
			bw.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
			assertTrue(false);
		}

	}

}
