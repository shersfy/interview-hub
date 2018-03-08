package org.interview.big.data.sqoop;

import org.interview.beans.BaseMeta;
import org.interview.beans.HdfsMeta;

public class OutputParam extends BaseMeta{
	
	private HdfsMeta hdfsInfo;
	private String tarHdfsPath;
	private int numExtractors;
	
	public OutputParam() {
		super();
		numExtractors = 16;
	}
	
	public OutputParam(HdfsMeta hdfsInfo, String tarHdfsPath) {
		this();
		this.hdfsInfo = hdfsInfo;
		this.tarHdfsPath = tarHdfsPath;
	}
	
	public HdfsMeta getHdfsInfo() {
		return hdfsInfo;
	}
	public String getTarHdfsPath() {
		return tarHdfsPath;
	}
	public void setHdfsInfo(HdfsMeta hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}
	public void setTarHdfsPath(String tarHdfsPath) {
		this.tarHdfsPath = tarHdfsPath;
	}

	public int getNumExtractors() {
		return numExtractors;
	}

	public void setNumExtractors(int numExtractors) {
		this.numExtractors = numExtractors;
	}
	
}
