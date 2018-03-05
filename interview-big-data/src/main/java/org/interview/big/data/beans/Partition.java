package org.interview.big.data.beans;

import org.interview.beans.BaseMeta;

public class Partition extends BaseMeta{

	private String field;
	private String value;

	public Partition() {
		super();
	}
	
	public Partition(String field, String value) {
		super();
		this.setName(field);
		this.field = field;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
