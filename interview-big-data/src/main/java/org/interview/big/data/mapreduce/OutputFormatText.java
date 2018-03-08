package org.interview.big.data.mapreduce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OutputFormatText implements Cloneable{

	private String key;
	private String columnSep = ",";
	private String path;
	private List<String> tmpFiles;
	

	public OutputFormatText() {
		tmpFiles = new ArrayList<>();
	}
	public OutputFormatText(String path) {
		this();
		this.path = path;
		if(path!=null && path.trim().length()!=0) {
			File dir = new File(path);
			if(!dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getColumnSep() {
		return columnSep;
	}
	
	public void setColumnSep(String columnSep) {
		this.columnSep = columnSep;
	}
	public List<String> getTmpFiles() {
		return tmpFiles;
	}
	public void setTmpFiles(List<String> tmpFiles) {
		this.tmpFiles = tmpFiles;
	}
	
}
