package org.interview.newfeature.jdk8;

import java.util.function.Supplier;

public class ClallMethod {

	private String name;
	
	public ClallMethod(){}
	
	public ClallMethod(String name) {
		super();
		this.name = name;
	}
	
	public static ClallMethod getInstance(Supplier<ClallMethod> sup) {
		return sup.get();
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
