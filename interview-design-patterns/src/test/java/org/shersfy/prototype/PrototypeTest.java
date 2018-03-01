package org.shersfy.prototype;

import java.util.Date;

import org.shersfy.build.prototype.Prototype;

import junit.framework.TestCase;

public class PrototypeTest extends TestCase {
	
	public void test01() {
		Prototype obj = new Prototype();
		obj.setId(1L);
		obj.setName("demo");
		obj.setType(2);
		obj.setCreateTime(new Date());
		
		// 快速clone对象
		System.out.println("origin: "+obj);
		for(int i=0; i<5; i++) {
			Prototype clone = (Prototype) obj.clone();
			System.out.println("proto : "+clone);
			// 断言不是同一个对象
			assertNotSame(obj, clone);
		}
	}

}
