package org.interview.meta;

/**
 * 单值
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class SingleData extends Data {

	private Object value;
	private long number;

	public SingleData(){
		
	}
	public SingleData(Object value){
		this.value = value;
		
	}
	public SingleData(Object value, long number){
		this(value);
		this.number = number;
		
	}
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public long getNumber() {
		return number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
}
