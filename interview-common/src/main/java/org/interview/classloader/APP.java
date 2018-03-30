package org.interview.classloader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

/**
 * 动态类或jar加载
 * @author shersfy
 * @date 2018-03-30
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class APP {

	public static void main( String[] args ) throws Exception {
		// 自定义类加载类，继承URLClassLoader
		// 1. 读取class文件二进制
		// 2. class文件：调用ClassLoader.defineClass(name, bytes, off, len)方法
		// 2. jar文件：调用URLClassLoaderaddURL(URL url)方法将jarURL方式添加
		// 3. 调用ClassLoader.loadClass(className), 得到class对象
		// 4. 利用反射获取对象实例，调用实例方法
		normal();
		dynamicLoadClasses();
		dynamicLoadJars();
	}

	/**正常访问**/
	private static void normal() {
		Role role = new Role(2, "All");
		System.out.println(role.toString());
	}
	
	/**
	 * 动态加载*.class文件
	 */
	private static void dynamicLoadClasses() throws Exception {
		String classPath = "libs/User.class";
		String className = "org.shersfy.bean.User";
		String methodSay = "say";
		
		DynamicClassloader loader = new DynamicClassloader(new URL[0]);
		loader.initClass(classPath, className);
		
		Class<?> clazz = loader.loadClass(className);
		newinstanceAndInvoke(clazz, methodSay);
		
		loader.close();
	}
	
	/**
	 * 动态加载jar文件
	 */
	private static void dynamicLoadJars() throws Exception {
		String jarsPath  = "libs";
		String className = "org.shersfy.bean.User";
		String methodSay = "say";
		
		DynamicClassloader loader = new DynamicClassloader(new URL[0]);
		Collection<File> jars = FileUtils.listFiles(new File(jarsPath), new String[] {"jar"}, false);
		for(File jar :jars) {
			loader.addURL(jar.toURI().toURL());
		}
		Class<?> clazz = loader.loadClass(className);
		newinstanceAndInvoke(clazz, methodSay);
		
		loader.close();
	}
	
	private static void newinstanceAndInvoke(Class<?> clazz,String methodName ) throws Exception {
		// public User(String name, int age)
		Constructor<?> Constructor = clazz.getConstructor(String.class, int.class);
		Object obj = Constructor.newInstance("Jimzhang", 22);
		Method method = clazz.getMethod(methodName);
		method.invoke(obj);
	}

}
