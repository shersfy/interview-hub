package org.shersfy.singleton;

import org.shersfy.build.singleton.Singleton01;
import org.shersfy.build.singleton.Singleton02;
import org.shersfy.build.singleton.Singleton03;
import org.shersfy.build.singleton.Singleton04;

import junit.framework.TestCase;

public class SingeltonTest extends TestCase {

	public void test01() {
		Singleton01 obj1 = Singleton01.getInstance();
		Singleton01 obj2 = Singleton01.getInstance();
		System.out.println(obj1);
		System.out.println(obj2);
		assertSame(obj1, obj2);
	}

	public void test02() {
		Singleton02 obj1 = Singleton02.getInstance();
		Singleton02 obj2 = Singleton02.getInstance();
		System.out.println(obj1);
		System.out.println(obj2);
		assertSame(obj1, obj2);
	}
	
	public void test03() {
		Singleton03 obj1 = Singleton03.getInstance();
		Singleton03 obj2 = Singleton03.getInstance();
		System.out.println(obj1);
		System.out.println(obj2);
		assertSame(obj1, obj2);
	}
	
	public void test04() {
		Singleton04 obj1 = Singleton04.getInstance();
		Singleton04 obj2 = Singleton04.getInstance();
		System.out.println(obj1);
		System.out.println(obj2);
		assertSame(obj1, obj2);
	}
}
