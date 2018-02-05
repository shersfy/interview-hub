package org.interview.meta;

import java.util.ArrayList;
import java.util.List;

public class GridData extends Data {

	private List<ColumnMeta> headers;

	private List<RowData> rows;

	/**
	 * 默认构造函数，初始化headers、rows
	 */
	public GridData() {
		this.headers = new ArrayList<ColumnMeta>();
		this.rows = new ArrayList<RowData>();
	}

	/**
	 * 带参数的构造函数，用于优化headers、rows
	 * 
	 * @param headers
	 * @param rows
	 */
	public GridData(List<ColumnMeta> headers, List<RowData> rows) {
		this.headers = headers;
		this.rows = rows;
	}

	/**
	 * 增加一行数据
	 * 
	 * @param dataRow
	 */
	public void addRow(RowData dataRow) {
		rows.add(dataRow);
	}


	public List<ColumnMeta> getHeaders() {
		return headers;
	}

	public void setHeaders(List<ColumnMeta> headers) {
		this.headers = headers;
	}

	public List<RowData> getRows() {
		return rows;
	}

	public void setRows(List<RowData> rows) {
		this.rows = rows;
	}
	
	public void clearAll() {
		this.rows.clear();
		this.headers.clear();
	}

	@Override
	public String toString() {
		return "DataSet [headers=" + headers + ", rows=" + rows + "]";
	}

}
