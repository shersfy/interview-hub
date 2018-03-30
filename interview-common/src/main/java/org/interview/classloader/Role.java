package org.interview.classloader;

public class Role {

	private int index;
	private String name;
	
	public Role() {
		super();
	}
	
	public Role(int index, String name) {
		super();
		this.index = index;
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public String getName() {
		return name;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Role [index=" + index + ", name=" + name + "]";
	}
}
