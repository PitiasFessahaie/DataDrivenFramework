package library;


	import java.io.File;
	import java.io.FileInputStream;
	import java.util.Iterator;

	import org.apache.log4j.Logger;
	import org.apache.poi.hssf.usermodel.HSSFWorkbook;
	import org.apache.poi.ss.usermodel.Cell;
	import org.apache.poi.ss.usermodel.DataFormatter;
	import org.apache.poi.ss.usermodel.Row;
	import org.apache.poi.ss.usermodel.Sheet;
	import org.apache.poi.ss.usermodel.Workbook;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;

	public class Excel {
		final static Logger logger = Logger.getLogger(Excel.class);

		private static String filePath;
		private static Workbook wb;
		private static Sheet sh;

		/***
			 * Constructor
			 ***/
			
			public Excel(String excelFile, String sheetName) {
				try {
					File excelDataFile = new File(excelFile);
					filePath = excelDataFile.getAbsolutePath();
					FileInputStream fs = new FileInputStream(excelDataFile);
					wb = getWorkbook(fs, filePath);
					sh = wb.getSheet(sheetName);
				} catch (Exception e) {
					logger.error("Error: ", e);
				}
			}

		public Excel(String excelFile, int sheetIndex) {
				try {
					File excelDataFile = new File(excelFile);
					filePath = excelDataFile.getAbsolutePath();
					FileInputStream fs = new FileInputStream(excelDataFile);
					wb = getWorkbook(fs, filePath);
					sh = wb.getSheetAt(sheetIndex);
				} catch (Exception e) {
					logger.error("Error: ", e);
				}
			}

		public String readExelData(int rowIndex, int colIndex) {
			String cellData = null;
			try {
				Row row = sh.getRow(rowIndex);
				Cell cell = row.getCell(colIndex);
				cellData = formatDataCellToString(cell);
				logger.info("Reading data cell at Row:" + rowIndex + ", column:" + colIndex + ", data: " + cellData);
			} catch (Exception e) {
				logger.error("Error: ", e);
			}
			return cellData;
		}

		public String[][] getExcelData() {
			String[][] arrayExcelData = null;
			try {
				Iterator<Row> iterator = sh.iterator();
				int totalCols = sh.getRow(0).getPhysicalNumberOfCells();
				int toatlRows = sh.getPhysicalNumberOfRows();
				arrayExcelData = new String[toatlRows - 1][totalCols];
				int iRowCount = 0;

				while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					// skipping row 1, because it's table header info
					if (iRowCount > 0) {
						Iterator<Cell> cellIterator = nextRow.iterator();
						int iColCount = 0;
						while (cellIterator.hasNext()) {
							Cell cell = cellIterator.next();
							// need to format the cells before read it as a string
							String data = formatDataCellToString(cell);
							arrayExcelData[iRowCount - 1][iColCount] = data;
							logger.info("Row:" + iRowCount + ", Col:" + iColCount + ", Data: " + data);
							iColCount++;
						}
					}
					iRowCount++;
				}
			} catch (Exception e) {
				logger.error("Error: ", e);
			}
			return arrayExcelData;
		}

		private String formatDataCellToString(Cell cell) {
			String cellString = null;
			try {
				DataFormatter formatter = new DataFormatter();
				cellString = formatter.formatCellValue(cell);
			} catch (Exception e) {
				logger.error("Error: ", e);
			}
			return cellString;
		}

		private Workbook getWorkbook(FileInputStream fis, String excelFilePath) {
			Workbook workbook = null;
			try {
				if (excelFilePath.endsWith("xlsx")) {
					workbook = new XSSFWorkbook(fis);
				} else if (excelFilePath.endsWith("xls")) {
					workbook = new HSSFWorkbook(fis);
				} else {
					throw new IllegalArgumentException("The specified file is not Excel file");
				}
			} catch (Exception e) {
				logger.error("Error: ", e);
			}
			return workbook;
		}

	

}
