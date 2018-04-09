package org.interview.newfeature.jdk8;

import java.util.function.Supplier;

public class CallMethod {

	private String name;
	
	public CallMethod(){}
	
	public CallMethod(String name) {
		super();
		this.name = name;
	}
	
	public static CallMethod getInstance(Supplier<CallMethod> sup) {
		return sup.get();
	}
	
	public static void getInstance() {
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
