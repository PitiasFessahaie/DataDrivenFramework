package library;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {
	String url = null;
	XSSFWorkbook wb;
	XSSFSheet sheet;

	public ReadExcel(String url) {
		this.url = url;
		try {
			File file = new File(url);
			FileInputStream fis = new FileInputStream(file);
			wb = new XSSFWorkbook(fis);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String getData(int Sheet, int row, int column) {

		sheet = wb.getSheetAt(Sheet);

		String data = sheet.getRow(row).getCell(column).getStringCellValue();

		return data;
	}

	public int getRow(int sheet) {

		int row = wb.getSheetAt(sheet).getLastRowNum();
		row = row + 1;

		return row;
	}

	public int getColumn(int sheet) {

		int column = wb.getSheetAt(sheet).getRow(0).getPhysicalNumberOfCells();

		return column;
	}

}
