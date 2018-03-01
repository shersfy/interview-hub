package org.interview.beans;

import com.alibaba.fastjson.JSON;

public class BaseMeta implements Cloneable{


	private String name;
	
	public BaseMeta(){}
	public BaseMeta(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	

}
