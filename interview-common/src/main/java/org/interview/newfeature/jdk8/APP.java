package org.interview.newfeature.jdk8;

import java.util.Arrays;
import java.util.List;

public class APP {

	public static void main(String[] args) {
		// 1. 函数式接口
		System.out.println(Functional.charAt("hello world" , 1));
		System.out.println(new FunctionalImpl().indexOf("hello world" , "ello"));
		System.out.println(new FunctionalImpl().toString("hello world"));
		
		// 2. Lambda表达式
		System.out.println("===================");
		lambda();
		
		// 3. 方法引用
		System.out.println("===================");
		callMethod();
		
		// 4. 
	
		
	}

	public static void lambda(){
		// 常规写法: 创建匿名对象
		Functional obj1 = new Functional() {
			
			@Override
			public int indexOf(String str, String search) {
				return str.indexOf(search);
			}
		};
		System.out.println(obj1.indexOf("hello world" , "ello"));
		
		// lambda表达式 完整写法
		Functional obj2 = (String str, String search) -> { return str.indexOf(search);};
		System.out.println(obj2.indexOf("hello world" , "ello"));
		
		// lambda表达式 简化申明参数类型
		Functional obj3 = (str, search) -> {return str.indexOf(search);};
		System.out.println(obj3.indexOf("hello world" , "ello"));
		
		// lambda表达式 简化括号, 参数只有一个圆括号可以不写，花括号只有一句话，花括号可以不写
		Functional obj4 = (str, search) -> str.indexOf(search);
		System.out.println(obj4.indexOf("hello world" , "ello"));
		
		// 应用
		List<String> list = Arrays.asList("Lili", "Anna", "Bobo", "Jim");
		list.forEach(elment -> System.out.print(elment+"\t"));
		System.out.println();
		list.sort((o1, o2) -> o1.compareTo(o2));
		list.forEach(elment -> System.out.print(elment+"\t"));
		System.out.println();
	}
	
	public static void callMethod() {
		// 方法引用的产物是一个函数式接口实现类的实例对象
		// 1. 构造方法引用 ClassName::new, 调用无参构造方法 
		System.out.println("========1. 构造方法引用==============");
		Functional fun = Functional.getInstance(FunctionalImpl::new);
		System.out.println(fun.getClass().getName());
		
		// 2. 静态方法引用  ClassName::methodName, 静态方法须实现函数式方法
		System.out.println("========2. 静态方法引用==============");
		Functional fun2 = APP::indexOf;
		System.out.println(fun2.indexOf("hello world" , "ello"));
		
		// 3. 任意对象方法引用 ClassName::methodName;
		System.out.println("========3. 任意对象方法引用==============");
		List<FunctionalImpl> list = Arrays.asList((FunctionalImpl)fun, new FunctionalImpl("Marry"));
		list.forEach(FunctionalImpl::getName);
		
		// 4. 特定对象方法引用, instance::methodName;
		System.out.println("========4. 特定对象方法引用==============");
		FunctionalImpl fun3 = new FunctionalImpl("Marry");
		list.forEach(fun3::toString);
		
	}
	
	public static int indexOf(String str, String search) {
		return str.indexOf(search);
	}

}
