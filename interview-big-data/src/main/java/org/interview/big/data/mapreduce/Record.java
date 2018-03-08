package org.interview.big.data.mapreduce;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Date time;
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Date getTime() {
		return time;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	

}
