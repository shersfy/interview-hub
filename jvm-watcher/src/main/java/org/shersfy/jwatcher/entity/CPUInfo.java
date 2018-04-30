package org.shersfy.jwatcher.entity;

public class CPUInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double user;
	private double system;
	private double wait;
	private double nice;
	
	private double used;
	private double free;
	
	public double getUser() {
		return user;
	}
	public double getSystem() {
		return system;
	}
	public double getWait() {
		return wait;
	}
	public double getNice() {
		return nice;
	}
	public double getUsed() {
		return used;
	}
	public double getFree() {
		return free;
	}
	public void setUser(double user) {
		this.user = user;
	}
	public void setSystem(double system) {
		this.system = system;
	}
	public void setWait(double wait) {
		this.wait = wait;
	}
	public void setNice(double nice) {
		this.nice = nice;
	}
	public void setUsed(double used) {
		this.used = used;
	}
	public void setFree(double free) {
		this.free = free;
	}
	
	

}
