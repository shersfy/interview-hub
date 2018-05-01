package org.shersfy.jwatcher.entity;

public class JVMProcess extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long pid;
	private String exePath;
	
	public long getPid() {
		return pid;
	}
	public String getExePath() {
		return exePath;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public void setExePath(String exePath) {
		this.exePath = exePath;
	}
	
	

}
