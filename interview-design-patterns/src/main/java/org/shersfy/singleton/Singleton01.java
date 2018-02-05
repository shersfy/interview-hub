package org.shersfy.singleton;

/**
 * 单例模式<br/>
 * 1、双检锁DCL: 双重校验锁（DCL，即 double-checked locking）<br/>
 * 是否 Lazy 初始化：是<br/>
 * 是否多线程安全：是<br/>
 * 
 * 描述：这种方式采用双锁机制，安全且在多线程情况下能保持高性能。<br/>
 * 优点：延迟加载、线程安全、高性能<br/>
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Singleton01 {
	
	// volatile: 一种比sychronized关键字更轻量级的同步机制
	// 1.在访问volatile变量时不会执行加锁操作，因此也就不会使执行线程阻塞，因此volatile变量是一种比sychronized关键字更轻量级的同步机制。
	// 2.非 volatile 变量进行读写的时候，每个线程先从内存拷贝变量到CPU缓存中。
	// 如果计算机有多个CPU，每个线程可能在不同的CPU上被处理，这意味着每个线程可以拷贝到不同的 CPU cache 中。
	// 而声明变量是 volatile 的，JVM 保证了每次读变量都从内存中读，跳过 CPU cache 这一步。
	private volatile static Singleton01 single;
	
	/***
	 * 私有化构造器，不让外面通过构造方法创建对象
	 */
	private Singleton01 (){}
	
	public static Singleton01 getInstance() {
		// 延迟加载
		if (single == null) {
			// 对类对象进行同步加锁, 多线程顺序执行以下语句
			synchronized (Singleton01.class) {
				if (single == null) {
					single = new Singleton01();
				}
			}
		}
		return single;
	}

}
