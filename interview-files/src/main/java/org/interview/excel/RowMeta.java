package org.interview.excel;

import org.interview.meta.Data;
import org.interview.meta.RowData;

/**
 * excel 行信息
 * @author PengYang
 * @date 2017-06-23
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public class RowMeta extends Data {
	
	private int index;
	private int columnSize;
	private RowData data;
	
	public RowMeta(){}
	public RowMeta(int index){
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	public int getColumnSize() {
		return columnSize;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}
	public RowData getData() {
		return data;
	}
	public void setData(RowData data) {
		this.data = data;
	}
	
}
