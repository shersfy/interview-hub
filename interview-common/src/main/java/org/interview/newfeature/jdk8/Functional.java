package org.interview.newfeature.jdk8;

import java.util.function.Supplier;

@FunctionalInterface
public interface Functional {
	
	/**1. 有且仅有一个自定义抽象方法接口**/
	public abstract int indexOf(String str, String search);
	
	/**可以有若干个非抽象方法(static或default方法，包含方法体)**/
	public static Functional getInstance(Supplier<Functional> supp) {
		return supp.get();
	}
	
	/**可以有若干个非抽象方法(static或default方法，包含方法体)**/
	public static char charAt(String str, int index) {
		if(str==null){
			return '\b';
		}
		return str.charAt(index);
	}
	
	/**可以有若干个非抽象方法(static或default方法，包含方法体)**/
	public default String toString(String str){
		return str;
	}

	/**可以定义Object父类中的public方法**/
	@Override
	int hashCode();

	/**可以定义Object父类中的public方法**/
	boolean equals(Object obj);

	/**可以定义Object父类中的public方法**/
	@Override
	String toString();


}
