package org.interview.common;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class Keywords implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	/**
	 * transient 修饰的成员变量不被序列化
	 * 
	 */
	private transient String addr;
	
	public Keywords(String name, int age, String addr) {
		super();
		this.name = name;
		this.age = age;
		this.addr = addr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
