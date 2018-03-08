package org.interview.big.data.mapreduce;

import org.interview.beans.BaseMeta;

public class TmpMessage extends BaseMeta {
	
	private String key;
	private OutputFormatText path;
	
	public TmpMessage() {
		super();
	}
	public TmpMessage(String key, OutputFormatText path) {
		super();
		this.key = key;
		this.path = path;
	}
	public String getKey() {
		return key;
	}
	public OutputFormatText getPath() {
		return path;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setPath(OutputFormatText path) {
		this.path = path;
	}

}
