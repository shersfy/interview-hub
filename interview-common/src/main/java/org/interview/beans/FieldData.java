package org.interview.beans;

/***
 * 字段数据
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class FieldData extends Data{

	/**字段数据值**/
	private Object value;
	/**字段Java类型**/
	private String type = String.class.getName();
	/**字段数据格式**/
	private String format;
	/**位置**/
	private PointData position;

	public FieldData() {
	}
	
	public FieldData(Object value) {
		this.value = value;
		if(value != null){
			this.type = value.getClass().getName();
		}
	}
	
	
	public FieldData(String name, Object value) {
		this(value);
		setName(name);
	}
	
	public FieldData(String name, Object value, String format) {
		this(name, value);
		this.format = format;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public String getFormat() {
		return format;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public PointData getPosition() {
		return position;
	}

	public void setPosition(PointData position) {
		this.position = position;
	}

}