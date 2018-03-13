package org.interview.multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 */
public class APP 
{
    public static void main( String[] args ) throws InterruptedException
    {
    	// 1.多线程并行控制类
        CountDownLatch latch = new CountDownLatch(2);
        latch.countDown();
        latch.countDown();
        latch.await();
        latch.await(1, TimeUnit.HOURS);
        
        // 2.多线程安全集合类
        new ConcurrentHashMap<>();
        new CopyOnWriteArrayList<>();
        new CopyOnWriteArraySet<>();
        
        // 3. Collections 创建线程安全集合类
        Collections.synchronizedCollection(new ArrayList<>());
        Collections.synchronizedList(new ArrayList<>());
        Collections.synchronizedSet(new HashSet<>());
        Collections.synchronizedMap(new HashMap<>());
        
        // 4. 多线程直接通信
        // 1) 共享内存, 使用线程安全的数据结构存储共享信息;
        // 2) wait/notify, 面试点, 怎么用
        // 必须在同步代码块中使用, 结合synchronized一起使用, 例：
        List<String> container = new ArrayList<>();
//        synchronized (container) {
//        	boolean where1 = false;
//        	boolean where2 = false;
//        	if(where1) {
//        		// 当线程A执行wait()时，会把当前的对象锁释放，然后让出CPU，进入等待状态。
//        		container.wait();
//        	}
//        	if(where2) {
//        		// 当线程B执行notify/notifyAll方法时，会唤醒一个处于等待该 对象锁 的线程A，然后继续往下执行，直到执行完退出对象锁锁住的区域（synchronized修饰的代码块）后再释放锁。
//        		container.notify();
//        	}
//        }
        
        CountDownLatch latch2 = new CountDownLatch(2);
		new MultiThread("ThreadA", latch2, container).start();
        new MultiThread("ThreadB", latch2, container).start();
        latch2.await();
    }
}
