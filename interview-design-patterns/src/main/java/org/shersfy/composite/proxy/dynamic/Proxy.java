package org.shersfy.composite.proxy.dynamic;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * 模拟代理类
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Proxy {
	
	public static Object newProxyInstance(Class<?> interfaces, InvocationHandler h)throws Exception{
		StringBuffer methodStr = new StringBuffer();
		String tr = "\r\n";
		Method[] methods = interfaces.getMethods();
		//拼接代理类的方法
		for (Method method : methods) {
			methodStr.append(
					"    public "+ method.getReturnType()+ " " +method.getName()+"() {" + tr +
					"        try {" + tr +
					"            java.lang.reflect.Method md = " + interfaces.getName() + "." + "class.getMethod(\""  + method.getName() + "\");" + tr +
					"            h.invoke(this,md);" + tr +
					"        }catch(Exception e) {e.printStackTrace();}" + tr +
					"    }" + tr 
					);
		}

		//拼接代理类
		String src = "package com.test;" + tr +
				"import com.test.Moveable;" + tr +
				"public class TimeProxy implements " + interfaces.getName() + " {" + tr +
				"    private com.test.InvocationHandler h;" + tr +
				"    public TimeProxy(com.test.InvocationHandler h) {" + tr +
				"        this.h = h;" + tr +
				"    }" + tr +
				methodStr.toString() + tr +
				"}";
		//创建代理类
		String fileName = System.getProperty("user.dir") + "/src/com/test/TimeProxy.java";
		File file = new File(fileName);
		FileWriter writer = new FileWriter(file);
		writer.write(src);
		writer.flush();
		writer.close();
		//编译
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> units = fileMgr.getJavaFileObjects(fileName);
		CompilationTask ct = compiler.getTask(null, fileMgr, null, null, null, units);
		ct.call();
		fileMgr.close();
		//加载类到内存：
		Class<?> c = ClassLoader.getSystemClassLoader().loadClass("com.test.TimeProxy");
		Constructor<?> constructor = c.getConstructor(InvocationHandler.class); //得到参数为InvocationHandler类型的构造方法
		Object m = constructor.newInstance(h); //通过该构造方法得到实例
		return m;

	}
}
