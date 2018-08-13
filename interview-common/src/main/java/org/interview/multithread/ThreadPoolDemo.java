package org.interview.multithread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * http://note.youdao.com/noteshare?id=c06acf2838b0c5d9380db134ae8489e1&sub=2D7FC87923774E22983D0FA2AD03A6E3
 * @author py
 * @date 2018年8月13日
 */
public class ThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException {

        int coreSize    = 2;
        int maxPoolSize = 3;
        int queueSize   = 2;

        int requestSize = 10;

        CountDownLatch latch = new CountDownLatch(requestSize);
        ExecutorService pool = new ThreadPoolExecutor(coreSize, maxPoolSize, 0L, 
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("pool size="+executor.getPoolSize());
                r.run();
            }
        });
        for(int index=0; index<requestSize; index++) {
            pool.submit(new Thread("Thread: "+index) {

                @Override
                public void run() {
                    for(int i=0; i<5; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(getName()+", latch="+latch.getCount());
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("finished, latch="+latch.getCount());
        pool.shutdown();
    }

}
