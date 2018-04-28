package org.shersfy.jwatcher.entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
