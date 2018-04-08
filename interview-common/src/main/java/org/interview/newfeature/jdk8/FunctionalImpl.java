package org.interview.newfeature.jdk8;

public class FunctionalImpl implements Functional{
	
	public FunctionalImpl(){}
	
	@Override
	public int indexOf(String str, String search) {
		return str.indexOf(search);
	}

	@Override
	public String toString(String str) {
		return Functional.super.toString(str);
	}
	
}
