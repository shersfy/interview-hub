package org.interview.excel;

import org.interview.meta.Data;

/**
 * excel sheet信息
 * @author PengYang
 * @date 2017-06-23
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public class SheetMeta extends Data {
	
	private int index;
	private int rowSize;
	private int dataRowIndex;
	private RowMeta header;
	
	public SheetMeta(){
	}
	
	public SheetMeta(String name){
		this();
		setName(name);
	}
	public SheetMeta(int index, String name){
		this(name);
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}

	public int getDataRowIndex() {
		return dataRowIndex;
	}

	public void setDataRowIndex(int dataRowIndex) {
		this.dataRowIndex = dataRowIndex;
	}

	public RowMeta getHeader() {
		return header;
	}

	public void setHeader(RowMeta header) {
		this.header = header;
	}

}
