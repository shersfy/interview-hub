package org.interview.excel;

import java.io.File;
import java.util.Collection;

import org.interview.exception.StandardException;
import org.junit.Test;

public class ExcelTest {

	
	private String xsl  = "D:/data/excel/xls/10x256x3sheet - 副本.xls";
	private String xslx = "D:/data/excel/xlsx/3_sheet - 副本.xlsx";
	private String out  = "D:/data/excel/out";
	
	@Test
	public void test2003() throws StandardException {
		WorkbookMeta book  = ExcelReader.getExcelWorkbook(xslx, null, null);
		ExcelReader reader = ExcelReader.getReaderInstance(xslx);
		
		Collection<File> files = reader.parseBigDataToTxt(out, null, null, book.getTotalRowSize(), false);
		
		for(File txt :files) {
			System.out.println(txt);
		}
		
	}
	@Test
	public void test2007() throws StandardException{
		
	}
	
	@Test
	public void test2003Book() throws StandardException{
		WorkbookMeta book = ExcelReader.getExcelWorkbook(xsl, null, null);
		System.out.println(book);
	}
	
	@Test
	public void test2007Book() throws StandardException{
		WorkbookMeta book = ExcelReader.getExcelWorkbook(xslx, null, null);
		System.out.println(book);
	}

}
