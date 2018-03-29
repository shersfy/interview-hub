package org.interview.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.interview.common.Keywords;
import org.junit.Test;

public class TestClass {
	
	@Test
	public void test01() throws IOException, ClassNotFoundException {
		Keywords word = new Keywords("py", 10, "guizhou");
		System.out.println(word.toString());
		System.out.println(word.getAddr());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(word);
		
		byte[] buf = os.toByteArray();
		
		InputStream in = new ByteArrayInputStream(buf);
		Keywords obj = (Keywords) new ObjectInputStream(in).readObject();
		System.out.println(obj.getName());
		System.out.println(obj.getAddr());
	}

}
