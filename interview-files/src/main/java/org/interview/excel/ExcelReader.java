package org.interview.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.interview.common.Const;
import org.interview.exception.StandardException;
import org.interview.meta.DataFileMeta;
import org.interview.meta.FieldData;
import org.interview.meta.GridData;
import org.interview.meta.PointData;
import org.interview.meta.RowData;
import org.interview.utils.DataFileUtil;
import org.interview.utils.DateUtil;
import org.interview.utils.FunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 事件驱动模式, excel读取接口定义
 * @author PengYang
 * @date 2017-06-23
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public abstract class ExcelReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);
	
	/**98-2003格式, *.xls**/
	public static final String EXT_XLS 		= "xls";
	/**2007以后格式,*.xlsx**/
	public static final String EXT_XLSX 	= "xlsx";
	
	/**读取每一行操作**/
	public static final int METHOD_PROCESS 			= 1;
	
	/**excel文件名  **/
	protected String filePath;
	/**sheet 内容处理  **/
	protected SimpleRowHandler handler;
	/**
	 * 事件驱动模式, 读取excel工作簿
	 * 
	 * @author PengYang
	 * @date 2017-06-23
	 * 
	 * @param sheetIndexs 指定处理sheet的索引, 为null或0个元素时处理这个工作簿
	 * @throws StandardException
	 */
	public void process(Integer[] sheetIndexs) throws StandardException{
		if(StringUtils.isBlank(filePath)){
			throw new StandardException("Excel file name is empty.");
		}
		
		if(!new File(filePath).isFile()){
			throw new StandardException(String.format("Excel file not exist %s.", filePath));
		}
		
		if(handler == null){
			throw new StandardException("handler is null.");
		}
	}
	
	/**
	 * 获取Excel工作簿信息, 支持*.xls,*.xlsx两种格式, 支持大数据量文件
	 * 
	 * @param filePath Excel文件名
	 * @param sheetIndexes 指定sheet索引, 从0开始, 不指定默认全部
	 * @param headerIndex 指定header行索引, 从0开始, 不指定默认0行
	 * @return WorkbookMeta
	 * @throws StandardException
	 */
	public static WorkbookMeta getExcelWorkbook(String filePath, Integer[] sheetIndexes, Integer headerIndex)
			throws StandardException {
		
		if(StringUtils.isBlank(filePath)){
			throw new StandardException("file name is blank.");
		}

		ExcelReader reader = null;
		String ext = ExcelReader.getExcelRealType(filePath);
		if(ExcelReader.EXT_XLS.equalsIgnoreCase(ext)){
			reader = new Excel2003Reader(filePath);
		}
		else if(ExcelReader.EXT_XLSX.equalsIgnoreCase(ext)){
			reader = new Excel2007Reader(filePath);
		}
		else{
			//not support
		}
		
		if(reader == null){
			throw new StandardException(String.format("not support file type *.%s", ext));
		}
		
		return reader.getWorkbookMeta(sheetIndexes, headerIndex);
	}
	
	/**
	 * 获取工作簿信息
	 * 
	 * @author PengYang
	 * @date 2017-06-24
	 * 
	 * @param sheetIndexs 指定sheet, 为空默认全部
	 * @param headerIndex 指定标题行, 为空表示默认第一行
	 * @return WorkbookMeta
	 * @throws StandardException
	 */
	public WorkbookMeta getWorkbookMeta(Integer[] sheetIndexs, Integer headerIndex) throws StandardException {
		
		final WorkbookMeta workbook = new WorkbookMeta();
		final Integer header = headerIndex==null?0:headerIndex;
		workbook.setName(filePath);
		SimpleRowHandler handler = new SimpleRowHandler() {
			
			int rowSize	 			= 0;
			int colSize	 			= 0;
			Integer curSheet 		= 0;
			
			List<FieldData> fields 			= null;
			Map<Integer, SheetMeta> sheets	= null;
			
			@Override
			public void startSheet(int sheetIndex, String sheetName) {
				if(sheets == null){
					sheets = new HashMap<Integer, SheetMeta>();
				}
				if(sheetIndex > curSheet){
					rowSize  = 0;
					curSheet = sheetIndex;
				}
				if(!sheets.keySet().contains(curSheet)){
					SheetMeta sheetMeta = new SheetMeta(sheetName);
					sheetMeta.setIndex(curSheet);
					workbook.getSheets().add(sheetMeta);
					sheets.put(curSheet, sheetMeta);
				}
				LOGGER.info("loading sheet [{}] ...", sheetName);
			}

			@Override
			public void endSheet(int sheetIndex, String sheetName) {
				LOGGER.info("count sheet [{}] total {} records.", sheetName, rowSize);
			}

			@Override
			public void startRow(int rowIndex) {
				fields 	= new ArrayList<FieldData>();
			}

			@Override
			public void endRow(int rowIndex) {
				sheets.get(curSheet).setRowSize(++rowSize);
				if(header == rowIndex){
					RowMeta rm = new RowMeta(header);
					rm.setColumnSize(colSize);
					rm.setData(new RowData(fields.toArray(new FieldData[fields.size()])));
					sheets.get(curSheet).setHeader(rm);
				}
				else{
					colSize = 0;
				}
				if(fields!=null){
					fields.clear();
				}
			}

			@Override
			public void cell(String cellReference, String formattedValue, XSSFComment comment) {
				// 列数取最后一个单元格的Y值
				PointData position = ExcelReader.getPosition(cellReference);
				this.colSize = 1+Long.valueOf(position==null?0:position.getYvalue()).intValue();
				FieldData field = new FieldData();
				//设置单元格的数据
				field.setValue(formattedValue);
				this.fields.add(field);
			}

			@Override
			public void headerFooter(String text, boolean isHeader, String tagName) {
			}

			@Override
			public void getRow(int sheetIndex, int rowIndex, List<FieldData> fields) {
				this.fields 	= fields;
				// 列数取最后一个单元格的Y值
				if(fields.isEmpty()){
					this.colSize = 0;
				} else {
					this.colSize = 1+Long.valueOf(fields.get(fields.size()-1).getPosition().getYvalue()).intValue();
				}
			}
			
		};
		this.setHandler(handler);
		this.process(sheetIndexs);
		// 计算总行数
		long totalRowSize = 0;
		for(SheetMeta sheet :workbook.getSheets()){
			totalRowSize += sheet.getRowSize();
		}
		workbook.setTotalRowSize(totalRowSize);
		return workbook;
	}
	
	
	public int getColumnSize(int sheetIndex, int rowIndex) throws StandardException{
		
		SimpleRowHandler handler = null;
		this.setHandler(handler);
		this.process(new Integer[]{sheetIndex});
		
		return 0;
	}
	
	
	/**
	 * 设置回调, 处理行数据
	 * 
	 * @author PengYang
	 * @date 2017-06-23
	 * 
	 * @param handler
	 * @return Excel2007Reader
	 */
	public ExcelReader setHandler(SimpleRowHandler handler) {
		this.handler = handler;
		return this;
	}
	
	/**
	 * 写数据到文件
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 * 
	 * @param dataFile 数据文件
	 * @param dataBlock 数据块
	 * @param outColSep 列分隔符
	 * @param isAppend 是否追加
	 * @throws StandardException 
	 */
	public DataFileMeta writeToFile(File dataFile, GridData dataBlock, String outColSep, boolean isAppend) 
			throws StandardException{
		
		DataFileMeta df = new DataFileMeta(dataFile);
		return DataFileUtil.writeToFile(df, dataBlock, outColSep, isAppend);
	}
	
	/**
	 * 
	 * @return array of built-in data formats
	 */
	public static String[] getCellDataFormatAll(){
		return BuiltinFormats.getAll();
	}
	/**
	 * Get the format string that matches the given format index
	 * 
	 * @param index of a built in format
	 * @return string represented at index of format or null if there is not a built-in format at that index
	 */
	public static String getCellDataFormat(int index){
		return BuiltinFormats.getBuiltinFormat(index);
	}
	/**
	 * Get the format index that matches the given format string.
	 * Automatically converts "text" to excel's format string to represent text.
	 * 
	 * @param pFmt string matching a built-in format
	 * @return index of format or -1 if undefined.
	 */
	public static int getCellDataFormat(String pFmt){
		return BuiltinFormats.getBuiltinFormat(pFmt);
	}
	
	/**
	 * 格式化数值型数据
	 * 
	 * @param formatter 格式化对象
	 * @param number 数值
	 * @param formatString 格式
	 * @param formatIndex 格式索引
	 * @return 已格式化的字符串
	 */
	public static String formatCellNumberData(DataFormatter formatter, 
			String number, String formatString, int formatIndex){

		String formatedStr = number;
		if(number == null || formatString==null || number.length()<1){
			return number;
		}

		String[] arr = formatString.split(";");
		// 去掉\_()"特殊符号
		String fmt 	= arr[0].trim().replaceAll("[\\\\_()\"*]", "");
		fmt = Const.toDateFormatFromXls(fmt);
		switch (fmt) {
		case Const.yyyy:
		case Const.yyyyMM:
		case Const.yyyyMMdd:
		case Const.yyyyMMddHHmmss:
		case Const.HHmmss:
			formatedStr = DateUtil.format(HSSFDateUtil.getJavaDate(Double.parseDouble(number)), fmt);
			break;
//		case "reserved-0x17":
//		case "reserved-0x18":
//		case "reserved-0x19":
//		case "reserved-0x1A":
//		case "reserved-0x1B":
//		case "reserved-0x1C":
//		case "reserved-0x1D":
		case "reserved-0x1E":
		case "reserved-0x1F":
//		case "reserved-0x21":
//		case "reserved-0x22":
//		case "reserved-0x23":
//		case "reserved-0x24":
			formatedStr = DateUtil.format(HSSFDateUtil.getJavaDate(Double.parseDouble(number)), Const.yyyyMMdd);
			break;
		case "reserved-0x20":
			formatedStr = DateUtil.format(HSSFDateUtil.getJavaDate(Double.parseDouble(number)), Const.HHmmss);
			break;
		default:
			/**
			 * Utility to identify built-in formats.  The following is a list of the formats as
			 * returned by this class.<p/>
			 *<p/>
			 *       0, "General"<br/>
			 *       1, "0"<br/>
			 *       2, "0.00"<br/>
			 *       3, "#,##0"<br/>
			 *       4, "#,##0.00"<br/>
			 *       5, "$#,##0_);($#,##0)"<br/>
			 *       6, "$#,##0_);[Red]($#,##0)"<br/>
			 *       7, "$#,##0.00);($#,##0.00)"<br/>
			 *       8, "$#,##0.00_);[Red]($#,##0.00)"<br/>
			 *       9, "0%"<br/>
			 *       0xa, "0.00%"<br/>
			 *       0xb, "0.00E+00"<br/>
			 *       0xc, "# ?/?"<br/>
			 *       0xd, "# ??/??"<br/>
			 *       0xe, "m/d/yy"<br/>
			 *       0xf, "d-mmm-yy"<br/>
			 *       0x10, "d-mmm"<br/>
			 *       0x11, "mmm-yy"<br/>
			 *       0x12, "h:mm AM/PM"<br/>
			 *       0x13, "h:mm:ss AM/PM"<br/>
			 *       0x14, "h:mm"<br/>
			 *       0x15, "h:mm:ss"<br/>
			 *       0x16, "m/d/yy h:mm"<br/>
			 *<p/>
			 *       // 0x17 - 0x24 reserved for international and undocumented
			 *       0x25, "#,##0_);(#,##0)"<br/>
			 *       0x26, "#,##0_);[Red](#,##0)"<br/>
			 *       0x27, "#,##0.00_);(#,##0.00)"<br/>
			 *       0x28, "#,##0.00_);[Red](#,##0.00)"<br/>
			 *       0x29, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)"<br/>
			 *       0x2a, "_($* #,##0_);_($* (#,##0);_($* \"-\"_);_(@_)"<br/>
			 *       0x2b, "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)"<br/>
			 *       0x2c, "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"<br/>
			 *       0x2d, "mm:ss"<br/>
			 *       0x2e, "[h]:mm:ss"<br/>
			 *       0x2f, "mm:ss.0"<br/>
			 *       0x30, "##0.0E+0"<br/>
			 *       0x31, "@" - This is text format.<br/>
			 *       0x31  "text" - Alias for "@"<br/>
			 * <p/>
			 * 0: General
			 * 1: 0
			 * 2: 0.00
			 * 3: #,##0
			 * 4: #,##0.00
			 * 5: "$"#,##0_);("$"#,##0)
			 * 6: "$"#,##0_);[Red]("$"#,##0)
			 * 7: "$"#,##0.00_);("$"#,##0.00)
			 * 8: "$"#,##0.00_);[Red]("$"#,##0.00)
			 * 9: 0%
			 * 10: 0.00%
			 * 11: 0.00E+00
			 * 12: # ?/?
			 * 13: # ??/??
			 * 14: m/d/yy
			 * 15: d-mmm-yy
			 * 16: d-mmm
			 * 17: mmm-yy
			 * 18: h:mm AM/PM
			 * 19: h:mm:ss AM/PM
			 * 20: h:mm
			 * 21: h:mm:ss
			 * 22: m/d/yy h:mm
			 * 23: reserved-0x17
			 * 24: reserved-0x18
			 * 25: reserved-0x19
			 * 26: reserved-0x1A
			 * 27: reserved-0x1B
			 * 28: reserved-0x1C
			 * 29: reserved-0x1D
			 * 30: reserved-0x1E
			 * 31: reserved-0x1F
			 * 32: reserved-0x20
			 * 33: reserved-0x21
			 * 34: reserved-0x22
			 * 35: reserved-0x23
			 * 36: reserved-0x24
			 * 37: #,##0_);(#,##0)
			 * 38: #,##0_);[Red](#,##0)
			 * 39: #,##0.00_);(#,##0.00)
			 * 40: #,##0.00_);[Red](#,##0.00)
			 * 41: _("$"* #,##0_);_("$"* (#,##0);_("$"* "-"_);_(@_)
			 * 42: _(* #,##0_);_(* (#,##0);_(* "-"_);_(@_)
			 * 43: _(* #,##0.00_);_(* (#,##0.00);_(* "-"??_);_(@_)
			 * 44: _("$"* #,##0.00_);_("$"* (#,##0.00);_("$"* "-"??_);_(@_)
			 * 45: mm:ss
			 * 46: [h]:mm:ss
			 * 47: mm:ss.0
			 * 48: ##0.0E+0
			 * 49: @
			 */
			switch (formatIndex) {
			case 5:
			case 6:
			case 7:
			case 8:
			case 23:
			case 24:
			case 25:
			case 26:
			case 41:
			case 42:
			case 44:
				break;
			default:
				formatedStr = formatter.formatRawCellContents(Double.parseDouble(number), formatIndex, formatString);
				break;
			}
			break;
		}
		return formatedStr;
	}
	
	/**
	 *  获取excel单元格位置
	 * @param cellReference excel单元格位置
	 * @return excel单元格位置
	 */
	public static PointData getPosition(String cellReference){
		
		if(StringUtils.isNotBlank(cellReference)){
			
			Pattern p1 = Pattern.compile("[0-9]+");
			Matcher m1 = p1.matcher(cellReference);
			StringBuffer sb1 = new StringBuffer();
			while (m1.find()) {
				sb1.append(m1.group());
			}

			Pattern p2 = Pattern.compile("[A-Z]+");
			Matcher m2 = p2.matcher(cellReference);
			StringBuffer sb2 = new StringBuffer();
			while (m2.find()) {
				sb2.append(m2.group());
			}
			
			long x = Long.parseLong(sb1.toString())-1;
			long y = FunUtil.convetChar2Num(sb2.toString())-1;
			PointData data = new PointData(x, y);
			return data;
		}
		return null;
	}
	
	/**
	 * 填充两个单元格之间空白单元格
	 * 
	 * @param rowFields 整行数据
	 * @param startCell 开始填充位置
	 * @param endCell 填充结束位置
	 * @return 填充完毕的整行数据
	 */
	public static List<FieldData> fillEmptyCell(List<FieldData> rowFields, PointData startCell, PointData endCell){

		if(rowFields == null || startCell == null || endCell == null){
			return rowFields;
		}
		
		// 相同相邻不填充
		if(startCell.getYvalue() == endCell.getYvalue() 
				|| startCell.getYvalue() == endCell.getYvalue()-1
				|| startCell.getXvalue() != endCell.getXvalue()){
			return rowFields;
		}
		
		long startX = startCell.getXvalue();
		long startY = startCell.getYvalue();
		
		if(rowFields.size()!=0){
			startX = rowFields.get(rowFields.size()-1).getPosition().getXvalue();
			startY = rowFields.get(rowFields.size()-1).getPosition().getYvalue();
		}
		
		long count = endCell.getYvalue() - startY -1 ;
		
		while (count> 0) {
			count--;
			startY++;
			
			FieldData field = new FieldData();
			PointData pt 	= new PointData(startX, startY);
			
			field.setName(pt.toString());
			field.setValue("");
			field.setPosition(pt);
			rowFields.add(field);
		}
		
		return rowFields;
	}
	
	/**
	 * 获取excel文件真实类型, 非excel文件类型返回原扩展名
	 * 
	 * @param filePath 文件路径
	 * @return 返回excel真实文件类型
	 * @throws StandardException
	 */
	public static String getExcelRealType(String filePath) throws StandardException{

		if(StringUtils.isBlank(filePath)){
			return null;
		}

		File file = new File(filePath);
		if(!file.isFile()){
			String err = String.format("file not exist: %s", filePath);
			throw new StandardException(err);
		}

		String ext = FilenameUtils.getExtension(filePath);

		if(!ExcelReader.EXT_XLS.equalsIgnoreCase(ext) 
				&& !ExcelReader.EXT_XLSX.equalsIgnoreCase(ext)){
			return ext;
		}

		// xls
		if(ExcelReader.EXT_XLS.equalsIgnoreCase(ext)){
			if(isExcle2003(filePath)){
				return ExcelReader.EXT_XLS;
			}
			if(isExcle2007(filePath)){
				return ExcelReader.EXT_XLSX;
			}
		} 
		else if(ExcelReader.EXT_XLSX.equalsIgnoreCase(ext)) {
			if(isExcle2007(filePath)){
				return ExcelReader.EXT_XLSX;
			}
			if(isExcle2003(filePath)){
				return ExcelReader.EXT_XLS;
			}
		}

		return ext;
	}
	
	/**
	 * 判定是2003(*.xls)格式
	 * 
	 * @param filePath 文件路径
	 * @return 
	 */
	public static boolean isExcle2003(String filePath){
		File file = new File(filePath);
		InputStream input 	= null;
		POIFSFileSystem fs 	= null;
		try {
			input 	= new FileInputStream(file);
			fs 		= new POIFSFileSystem(input);
			return true;
		} catch (Exception e) {
			// nothing
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(fs);
		}
		return false;
	}
	/**
	 * 判定是2007(*.xlsx)格式
	 * 
	 * @param filePath 文件路径
	 * @return 
	 */
	public static boolean isExcle2007(String filePath){
		// xlsx
		OPCPackage pk = null;
		try {
			pk = OPCPackage.open(filePath, PackageAccess.READ);
			return true;
		} catch (Exception e) {
			// nothing
		} finally {
			IOUtils.closeQuietly(pk);
		}
		return false;
	}
	
}
