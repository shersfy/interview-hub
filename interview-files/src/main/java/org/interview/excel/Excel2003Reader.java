package org.interview.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.interview.beans.FieldData;
import org.interview.beans.PointData;
import org.interview.exception.StandardException;

public class Excel2003Reader extends ExcelReader 
	implements HSSFListener{

	/** POI文件系统对象   **/
	private POIFSFileSystem fs;
	private int lastRowNumber;
	@SuppressWarnings("unused")
	private int lastColumnNumber;

	/** Should we output the formula, or the value it has? */
	private boolean outputFormulaValues;
	/** For parsing Formulas */  
	private SheetRecordCollectingListener workbookBuildingListener;
	/**excel2003工作薄    **/
	private HSSFWorkbook subWorkbook;
	// Records we pick up as we process  
	private SSTRecord sstRecord;
	/**格式监听  **/
	private FormatTrackingHSSFListener formatListener;
	/**sheet索引--绑定的sheet对象  **/
	private Map<Integer, BoundSheetRecord>  sheetRecords;
	// For handling formulas with string results 
	private int nextRow;
	private int nextColumn;
	private boolean outputNextStringRecord;
	/**存储行记录的容器  **/
	private List<FieldData> fields;
	/**当前正在处理sheet索引**/
	private Integer sheetSize;
	/**指定处理的sheet索引**/
	private Integer[] sheetIndexs;
	/**当前正在处理sheet索引**/
	private Integer sheetIndex;
	/**当前正在处理sheet名**/
	private String sheetName;
	/**当前行索引**/
	private int rowIndex;

	public Excel2003Reader(String filePath){

		this.sheetIndex = -1;
		this.sheetSize 	= 0;
		this.rowIndex 	= -1;
		
		this.outputFormulaValues = true;
		this.sheetRecords = new HashMap<Integer, BoundSheetRecord>();
		
		this.fields 	=  new ArrayList<FieldData>();
		this.filePath 	= filePath;
	}
	
	@Override
	public void process(Integer[] sheetIndexs) throws StandardException {
		
		super.process(sheetIndexs);
		if(sheetIndexs == null){
			sheetIndexs = new Integer[]{};
		}
		
		this.sheetIndexs 	=  sheetIndexs;
		InputStream input 	= null;
		
		try {
			
			if(StringUtils.isBlank(filePath)){
				throw new StandardException("Excel file name is empty.");
			}
			
			if(handler == null){
				throw new IOException("handler is null.");
			}
			
			input = new FileInputStream(filePath);
			this.fs = new POIFSFileSystem(input);
			MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
			formatListener = new FormatTrackingHSSFListener(listener, Locale.getDefault());
			
			HSSFEventFactory factory 	= new HSSFEventFactory();
			HSSFRequest request 		= new HSSFRequest();
			
			if (outputFormulaValues) {
				request.addListenerForAllRecords(formatListener);
			}
			else {
				workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
				request.addListenerForAllRecords(workbookBuildingListener);
			}
			
			factory.processWorkbookEvents(request, fs);
			// 结束最后一个sheet
			handler.endSheet(sheetIndex, sheetName);
			
		} catch (Throwable e) {
			throw new StandardException(e, "Excel read error, file %s", filePath);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(fs);
		}
	}

	/** 
	 * HSSFListener 监听方法，处理 Record 
	 */  
	@Override
	public void processRecord(Record record) {
		
		int thisRow 	= -1;
		int thisColumn 	= -1;
		
		PointData position;
//		String value 	= null;
		FieldData field = null;
		
		List<Integer> indexes = Arrays.asList(this.sheetIndexs);
		// 超出指定sheet
		if(record == null || sheetIndex > sheetSize){
			return;
		}
		// 1.WorkBook
		// 2.WorkSheet
		// 3.Cell
		switch (record.getSid()) {
		case BoundSheetRecord.sid: // sheet对象
			if(!indexes.isEmpty() && !indexes.contains(sheetSize)){
				sheetSize++;
				break;
			}
			BoundSheetRecord sheetRec = (BoundSheetRecord) record;
			sheetRecords.put(sheetSize, sheetRec);
			sheetSize++;
			break;
			
		case BOFRecord.sid:
			BOFRecord br = (BOFRecord) record; // BOF对象(sheet和workbook)
			if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
				// 如果有需要，则建立子工作薄 
				if (workbookBuildingListener != null && subWorkbook == null) {
					subWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
				}
				
//				if (orderedBSRs == null) {
//					orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
//				}
				sheetIndex++;
				BoundSheetRecord sheet = sheetRecords.get(sheetIndex);
				if(sheet ==null ){
					return;
				}
				sheetName = sheet.getSheetname();
				// 结束前一个sheet
				if(sheetRecords.keySet().contains(sheetIndex-1)){
					handler.endSheet(sheetIndex-1, sheetName);
				}
				// 开始下一个sheet
				handler.startSheet(sheetIndex, sheetName);
			}
			break;

		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;

		case BlankRecord.sid: // 空白单元格
			BlankRecord blank = (BlankRecord) record;
			thisRow 		  = blank.getRow();
			thisColumn 		  = blank.getColumn();
			
			field = new FieldData(null, StringUtils.EMPTY);
			break;
			
		case BoolErrRecord.sid: // 单元格为布尔类型
			BoolErrRecord bool = (BoolErrRecord) record;
			thisRow 			= bool.getRow();
			thisColumn 			= bool.getColumn();
			
			field = new FieldData(bool.getBooleanValue());
			break;

		case FormulaRecord.sid: // 单元格为公式类型
			FormulaRecord frmRec 	= (FormulaRecord) record;
			thisRow 				= frmRec.getRow();
			thisColumn 				= frmRec.getColumn();
			
			if (outputFormulaValues) {
				if (Double.isNaN(frmRec.getValue())) {
					// Formula result is a string  
					// This is stored in the next record  
					outputNextStringRecord = true;
					nextRow 	= frmRec.getRow();
					nextColumn 	= frmRec.getColumn();
				}
				else {
					field = new FieldData(formatListener.formatNumberDateCell(frmRec));
				}
			}
			else {
				field = new FieldData(HSSFFormulaParser.toFormulaString(subWorkbook,
						frmRec.getParsedExpression()));
			}
//			formatListener.getFormatString(frmRec);
			break;
			
		case StringRecord.sid:// 单元格中公式文本
			if (outputNextStringRecord) {
				// String for formula 跳过
//				StringRecord srec 	= (StringRecord) record;
//				thisStr 			= srec.getString();
				thisRow 			= nextRow;
				thisColumn 			= nextColumn;
				outputNextStringRecord = false;
			}
			break;
			
		case LabelRecord.sid:
			LabelRecord lable 	= (LabelRecord) record;
			rowIndex = thisRow 	= lable.getRow();
			thisColumn 			= lable.getColumn();
			
			field = new FieldData(lable.getValue());
			break;
			
		case LabelSSTRecord.sid:// 单元格为字符串类型 
			LabelSSTRecord text = (LabelSSTRecord) record;
			rowIndex = thisRow 	= text.getRow();
			thisColumn 			= text.getColumn();
			
			field = new FieldData(sstRecord == null 
					? StringUtils.EMPTY 
					: sstRecord.getString(text.getSSTIndex()).toString());
			break;
			
		case NumberRecord.sid:// 单元格为数字类型
			NumberRecord numberRecord 	= (NumberRecord) record;
			rowIndex = thisRow 			= numberRecord.getRow();
			thisColumn 					= numberRecord.getColumn();
			
			String formatString = formatListener.getFormatString(numberRecord);
			int formatIndex     = formatListener.getFormatIndex(numberRecord);
			String number 		= String.valueOf(numberRecord.getValue());
			
//			HSSFDataFormatter formatter = new HSSFDataFormatter(Locale.US);
			DataFormatter formatter = new DataFormatter();
			
			String formated = formatCellNumberData(formatter, number, formatString, formatIndex);
			field = new FieldData(formated);
			
			break;
			
		default:
			break;
		}
		// 空值的操作
		if (record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord missing 	= (MissingCellDummyRecord) record;
			rowIndex = thisRow 				= missing.getRow();
			thisColumn 						= missing.getColumn();
			field = new FieldData(StringUtils.EMPTY);
		}
		
		if(!sheetRecords.keySet().contains(sheetIndex)){
			return;
		}
		
		if(field != null){
			position = new PointData(thisRow, thisColumn);
			field.setName(position.toString());
			field.setPosition(position);
			Object val = field.getValue();
			boolean append = true;
			append = append && fields.size()==0;
			append = append && val!=null;
			append = append && String.valueOf(val).length()!=0;
			if(fields.size()!=0 || append){
				fields.add(field);
			}
		}

		// 遇到新行的操作  
		if (thisRow > -1 && thisRow != lastRowNumber) {
			handler.startRow(rowIndex);
			lastColumnNumber = -1;
		}

		// 更新行的值
		if (thisRow > -1){
			lastRowNumber = thisRow;
		}
		// 更新列的值
		if (thisColumn > -1){
			lastColumnNumber = thisColumn;
		}
		// 行结束时的操作
		if (record instanceof LastCellOfRowDummyRecord) {

//			if (minColumns > 0) {
//				// 列值重新置空
//				if (lastColumnNumber == -1) {
//					lastColumnNumber = 0;
//				}
//			}
			lastColumnNumber = -1;
			// 每行结束时， 调用getRows() 方法 
			handler.getRow(sheetIndex, rowIndex, fields);
			handler.endRow(rowIndex);
			// 清空容器 
			fields.clear();
		}
	}
	
}
