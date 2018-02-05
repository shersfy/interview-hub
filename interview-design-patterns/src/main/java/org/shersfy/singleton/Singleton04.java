package org.shersfy.singleton;

/**
 * 单例模式<br/>
 * 4、饿汉式<br/>
 * 是否 Lazy 初始化：否<br/>
 * 是否多线程安全：是<br/>
 * 
 * 描述：这种方式比较常用，但容易产生垃圾对象。<br/>
 * 它基于 classloder 机制避免了多线程的同步问题，不过，instance 在类装载时就实例化，
 * 虽然导致类装载的原因有很多种，在单例模式中大多数都是调用 getInstance 方法， 
 * 但是也不能确定有其他的方式（或者其他的静态方法）导致类装载，
 * 这时候初始化 instance 显然没有达到 lazy loading 的效果。<br/>
 * 
 * 优点：没有加锁，执行效率会提高。<br/>
 * 缺点：类加载时就初始化，浪费内存。<br/>
 * 
 * 使用场景：<br/>
 * 1、要求生产唯一序列号。<br/>
 * 2、WEB 中的计数器，不用每次刷新都在数据库里加一次，用单例先缓存起来。<br/>
 * 3、创建的一个对象需要消耗的资源过多，比如 I/O 与数据库的连接等。<br/>
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Singleton04 {
	
	private static Singleton04 single = new Singleton04();
	// 或
//	private static Singleton04 single = null;
//	
//	static {
//		single = new Singleton04();
//	}

	/***
	 * 私有化构造器，不让外面通过构造方法创建对象
	 */
	private Singleton04() {}

	public static Singleton04 getInstance() {
		return single;
	}
}
