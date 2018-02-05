package org.shersfy.singleton;

/**
 * 单例模式<br/>
 * 3、懒汉式，线程安全<br/>
 * 是否 Lazy 初始化：是<br/>
 * 是否多线程安全：是<br/>
 * 描述：这种方式具备很好的 lazy loading，能够在多线程中很好的工作，但是，效率很低，99% 情况下不需要同步。<br/>
 * 优点：第一次调用才初始化，避免内存浪费。<br/>
 * 缺点：必须加锁 synchronized 才能保证单例，但加锁会影响效率。<br/>
 * 
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Singleton03 {
	
	private static Singleton03 single;
	/***
	 * 私有化构造器，不让外面通过构造方法创建对象
	 */
	private Singleton03() {}

	public synchronized static Singleton03 getInstance() {
		if(single == null) {
			single = new Singleton03();
		}
		return single;
	}
}
