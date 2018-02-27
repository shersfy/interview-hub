package org.interview.excel;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFComment;
import org.interview.excel.SheetXMLHandler.SheetContentsHandler;
import org.interview.meta.FieldData;

/**
 * 处理excel sheet接口定义
 * @author PengYang
 * @date 2017-06-22
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public interface SimpleRowHandler extends SheetContentsHandler {

	/**
	 * sheet结束
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 *
	 */
	public void startSheet(int sheetIndex, String sheetName);
	
	/**
	 * sheet结束
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 *
	 */
	public void endSheet(int sheetIndex, String sheetName);
	
	@Override
	public void startRow(int rowInndex);
	@Override
	public void endRow(int rowInndex);
	
	/**
	 * A cell, with the given formatted value (may be null), and possibly a comment (may be null), was encountered.<br/>
	 * Only support 2007 version excel.
	 */
	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment);
	@Override
	public void headerFooter(String text, boolean isHeader, String tagName);
	
	/**
	 * 处理行, 2003格式专用
	 * 
	 * @author PengYang
	 * @date 2017-06-23
	 * 
	 * @param sheetIndex sheet索引
	 * @param rowIndex 行索引
	 * @param fields 行数据
	 */
	public void getRow(int sheetIndex, int rowIndex, List<FieldData> fields);
}
