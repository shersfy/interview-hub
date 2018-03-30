package org.interview.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;

public class DynamicClassloader extends URLClassLoader {


	public DynamicClassloader(URL[] urls) {
		super(urls);
	}

	public void initClass(String classPath, String className) {
		File file = new File(classPath); 
		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			defineClass(className, bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initJars(String jarsPath) {
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	
}
