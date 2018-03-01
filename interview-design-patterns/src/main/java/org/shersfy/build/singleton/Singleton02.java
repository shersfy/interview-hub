package org.shersfy.build.singleton;

/**
 * 单例模式<br/>
 * 2、懒汉式，线程不安全<br/>
 * 是否 Lazy 初始化：是<br/>
 * 是否多线程安全：否<br/>
 * 描述：这种方式是最基本的实现方式，这种实现最大的问题就是不支持多线程。因为没有加锁 synchronized，所以严格意义上它并不算单例模式。<br/>
 * 使用场景：<br/>
 * 1、要求生产唯一序列号。<br/>
 * 2、WEB 中的计数器，不用每次刷新都在数据库里加一次，用单例先缓存起来。<br/>
 * 3、创建的一个对象需要消耗的资源过多，比如 I/O 与数据库的连接等。<br/>
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Singleton02 {
	
	private static Singleton02 single = null;

	/***
	 * 私有化构造器，不让外面通过构造方法创建对象
	 */
	private Singleton02() {}

	public static Singleton02 getInstance() {
		if(single == null) {
			single = new Singleton02(); 
		}
		return single;
	}
}
