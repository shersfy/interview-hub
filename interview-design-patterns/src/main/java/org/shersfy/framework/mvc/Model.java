package org.shersfy.framework.mvc;

import java.util.Date;

/**
 * 数据模型层，模型代表一个存取数据的对象或 JAVA POJO。它也可以带有逻辑，在数据变化时更新控制器。
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Model {
	
	private int id;
	private String name;
	private Date time;
	
	public Model() {}
	
	public Model(int id) {
		super();
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Date getTime() {
		return time;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTime(Date time) {
		this.time = time;
	}

}
