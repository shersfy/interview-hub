package org.interview.meta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 结构化数据文件源信息
 * @author PengYang
 * @date 2018-02-27
 *
 * @copyright Copyright Lenovo Corporation 2018 All Rights Reserved.
 */
public class DataFileMeta extends FileMeta{
	
	private List<ColumnMeta> headers;
	/**文件**/
	private File file;
	/**列分隔符**/
	private String columnSep;
	/**文件大小**/
	private long size;
	/**列数**/
	private long cols;
	/**行数**/
	private long lines;
	
	public DataFileMeta() {
		super();
		this.headers = new ArrayList<ColumnMeta>();
	}
	public DataFileMeta(File file) {
		super();
		this.headers = new ArrayList<ColumnMeta>();
		this.file = file;
		if(file!=null){
			this.setName(file.getName());
			this.setPath(file.getAbsolutePath());
			this.setSize(file.length());
			this.setFile(file.isFile());
			this.setDirectory(file.isDirectory());
		}
	}
	public List<ColumnMeta> getHeaders() {
		return headers;
	}
	public void setHeaders(List<ColumnMeta> headers) {
		this.headers = headers;
	}
	public File getFile() {
		return file;
	}
	public String getColumnSep() {
		return columnSep;
	}
	public long getCols() {
		return cols;
	}
	public long getLines() {
		return lines;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setColumnSep(String columnSep) {
		this.columnSep = columnSep;
	}
	public void setCols(long cols) {
		this.cols = cols;
	}
	public void setLines(long lines) {
		this.lines = lines;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
}
