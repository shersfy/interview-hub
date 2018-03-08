package org.shersfy.framework.producer.consumer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.math.RandomUtils;

/**
 * 生产者/消费者模式(Producer/Consumer Pattern)<br/>
 * <br/>
 * 生产者 --> 缓冲区 --> 消费者<br/>
 * <br/>
 * 生产者：产生数据的模块，可以是类、线程或应用模块<br/>
 * 缓冲区：数据存储仓库，用来存储生产者产生的数据<br/>
 * 消费者：消耗使用数据的模块，从缓冲区拿取数据<br/>
 * 
 * 优点：<br/>
 * 1. 解耦, 由于有缓冲区的存在，生产者和消费者之间不直接依赖，耦合度降低。<br/>
 * 2. 支持并发, 由于生产者与消费者是两个独立的并发体，他们之间是用缓冲区作为桥梁连接，生产者只需要往缓冲区里丢数据，就可以继续生产下一个数据，而消费者只需要从缓冲区了拿数据即可，这样就不会因为彼此的处理速度而发生阻塞。<br/>
 * 3. 支持忙闲不均, 缓冲区还有另一个好处。如果制造数据的速度时快时慢，缓冲区的好处就体现出来 了。当数据制造快的时候，消费者来不及处理，未处理的数据可以暂时存在缓冲区中。 等生产者的制造速度慢下来，消费者再慢慢处理掉。<br/>
 * 
 * 规则:<br/>
 * 1. 生产者仅仅在仓储未满时候生产，仓满则停止生产。<br/>
 * 2. 消费者仅仅在仓储有产品时候才能消费，仓空则等待。<br/>
 * 3. 当消费者发现仓储没产品可消费时候会通知生产者生产。<br/>
 * 4. 生产者在生产出可消费产品时候，应该通知等待的消费者去消费<br/>
 * 
 * 例：<br/>
 * kafka 消息系统
 * 
 * @author shersfy
 * @date 2018-03-02
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class APP {
	
	public static void main(String[] args) throws InterruptedException {
		
		final Broker broker = new Broker();
		final String topic  = "test"+System.nanoTime();
		broker.createTopic(topic);
		AtomicInteger cnt = new AtomicInteger(0);
		
		
		final CountDownLatch latch = new CountDownLatch(3);
		// 生产者线程A
		Thread pth1 = new Thread(new Runnable() {
			Producer producer = new Producer(broker);
			@Override
			public void run() {
				try {
					while(true) {
						cnt.addAndGet(1);
						producer.build(topic, "线程A产品");
						Thread.sleep(RandomUtils.nextInt(3)*1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				latch.countDown();
			}
		});
		// 生产者线程B
		Thread pth2 = new Thread(new Runnable() {
			Producer producer = new Producer(broker);
			@Override
			public void run() {
				try {
					while(true) {
						cnt.addAndGet(1);
						producer.build(topic, "线程B产品");
						Thread.sleep(RandomUtils.nextInt(5)*1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				latch.countDown();
			}
		});
		
		// 消费者线程
		Thread cth1 = new Thread(new Runnable() {
			Consumer consumer = new Consumer(broker);
			@Override
			public void run() {
				try {
					while(true) {
						consumer.consume(topic);
						Thread.sleep(RandomUtils.nextInt(2)*1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("consumer end");
				latch.countDown();
			}
		}); 
		
		
		pth1.start();
		pth2.start();
		cth1.start();
		
		while(latch.await(10000, TimeUnit.SECONDS)) {
			System.out.println("timeout continue");
		}
		
		System.out.println("end");
		
	}

}
