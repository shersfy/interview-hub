package org.interview.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class APP {

	
	public static void main(String[] args) throws Exception {
		APP app = new APP();
		List<String> names = app.getParamNames("org.interview.reflect.Controller", "login");
		names.forEach(System.out::println);
	}
	
	
	public List<String> getParamNames(String className, String methodName) throws Exception{
		Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
		Method[] methods = clazz.getDeclaredMethods();
		Method method = null;
		for(Method m : methods){
			if(m.getName().equals(methodName)){
				method = m;
				break;
			}
		}
		List<String> names = new ArrayList<>();
		if(method!=null){
			Parameter[] params = method.getParameters();
			for(Parameter p :params){
				names.add(p.getName());
			}
		}
		return names;
	}
	
	public List<String> getParamNames2(String className, String methodName) throws Exception{
		
		Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
		Method[] methods = clazz.getDeclaredMethods();
		Method method = null;
		for(Method m : methods){
			if(m.getName().equals(methodName)){
				method = m;
				break;
			}
		}
		List<String> names = new ArrayList<>();
		if(method!=null){
			Parameter[] params = method.getParameters();
			for(Parameter p :params){
				names.add(p.getName());
			}
		}
		return names;
	}

}
