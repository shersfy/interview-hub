package org.interview.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.interview.exception.StandardException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * excel 2007以上版本读取类
 * @author PengYang
 * @date 2017-06-22
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public class Excel2007Reader extends ExcelReader{
	
	public Excel2007Reader(String filePath){
		this.filePath 	= filePath;
	}

	@Override
	public void process(Integer[] sheetIndexs) throws StandardException{
		// call processWorkbook
		super.process(sheetIndexs);
		if(sheetIndexs == null){
			sheetIndexs = new Integer[]{};
		}
		this.processWorkbook(sheetIndexs, METHOD_PROCESS);
	}
	
	/**
	 * 处理工作簿
	 * 
	 * @author PengYang
	 * @date 2017-06-23
	 * 
	 * @param sheetIndexs 指定处理sheet索引
	 * @param methodType 处理方法
	 * @throws StandardException
	 */
	private void processWorkbook(Integer[] sheetIndexs, int methodType) throws StandardException{

		OPCPackage pkg = null;
		InputStream sheetInputStream = null;

		try {
			
			pkg = OPCPackage.open(filePath, PackageAccess.READ);
			
			XSSFReader xssfReader 	= new XSSFReader(pkg);
			StylesTable styles 		= xssfReader.getStylesTable();
			
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
			// 遍历sheet
			XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			Integer sheetIndex = -1;
			
			List<Integer> indexs = Arrays.asList(sheetIndexs);
			while(iter.hasNext()){
				sheetIndex++;
				if(!indexs.isEmpty() && !indexs.contains(sheetIndex)){
					IOUtils.closeQuietly(iter.next());
					continue;
				}
				
				sheetInputStream = iter.next();
				String sheetName = iter.getSheetName();
				if(METHOD_PROCESS == methodType){
					handler.startSheet(sheetIndex, sheetName);
					processSheet(styles, strings, sheetInputStream);
					handler.endSheet(sheetIndex, sheetName);
				}
				else {
					IOUtils.closeQuietly(sheetInputStream);
					continue;
				}
			}
			
		} catch (Throwable e) {
			throw new StandardException(e, "Excel read error, file %s", filePath);
		} finally {
			IOUtils.closeQuietly(pkg);
		}
	
	}
	
	/**
	 * 处理sheet
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 * 
	 * @param styles
	 * @param strings
	 * @param sheetInputStream
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream) 
			throws SAXException, ParserConfigurationException, IOException{
		
		try {
			
			XMLReader sheetParser = SAXHelper.newXMLReader();
			sheetParser.setContentHandler(new SheetXMLHandler(styles, strings, handler, false));
			sheetParser.parse(new InputSource(sheetInputStream));
			
		} finally {
			IOUtils.closeQuietly(sheetInputStream);
		}
	}

}
