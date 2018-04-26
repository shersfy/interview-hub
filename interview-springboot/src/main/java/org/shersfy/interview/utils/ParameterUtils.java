package org.shersfy.interview.utils;

import java.lang.reflect.Method;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

public class ParameterUtils {
	
	
	public static String[] getParameterNames(Method method) {
		
		LocalVariableTableParameterNameDiscoverer discover = new LocalVariableTableParameterNameDiscoverer();
		return discover.getParameterNames(method);
	}

}
