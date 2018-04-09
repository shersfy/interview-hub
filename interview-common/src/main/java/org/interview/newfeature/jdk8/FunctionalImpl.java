package org.interview.newfeature.jdk8;

public class FunctionalImpl implements Functional{
	
	private String name;
	
	public FunctionalImpl(){
	}
	
	public FunctionalImpl(String name){
		this.name = name;
	}
	
	@Override
	public int indexOf(String str, String search) {
		return str.indexOf(search);
	}

	
	public String getName() {
		System.out.println("name="+name);
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString(String str) {
		return Functional.super.toString(str);
	}
	
	@Override
	public String toString() {
		String str = "FunctionalImpl [name=" + name + "]";
		System.out.println(str);
		return str;
	}
	
	public String toString(Functional fun) {
		return fun.toString();
	}
	
}
