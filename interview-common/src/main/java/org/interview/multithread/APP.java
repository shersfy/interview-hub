package org.interview.multithread;

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
        CountDownLatch latch = new CountDownLatch(3);
        latch.countDown();
        latch.countDown();
        latch.countDown();
        latch.await();
        latch.await(1, TimeUnit.HOURS);
        // 2.多线程安全集合类
        new ConcurrentHashMap<>();
        new CopyOnWriteArrayList<>();
        new CopyOnWriteArraySet<>();
    }
}
