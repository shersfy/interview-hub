package org.shersfy.framework.producer.consumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.interview.exception.StandardException;

/**
 * 存储区/缓冲区
 * @author shersfy
 * @date 2018-03-02
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Broker {

	private Map<String, Topic> topices;

	public Broker(){
		topices = new ConcurrentHashMap<>();
	}

	public Topic createTopic(String topicName) {
		Topic topic = topices.get(topicName);
		if(topic!=null) {
			System.out.println("topic exist: "+topicName);
			return topic;
		}
		topic = new Topic(topicName);
		topices.put(topicName, topic);
		return topic;
	}

	/**
	 * 供生产者调用
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param topicName
	 * @param msg
	 * @throws StandardException
	 */
	public void push(String topicName, String msg) throws StandardException{
		Topic topic = topices.get(topicName);
		if(topic == null) {
			throw new StandardException("topic not exist: "+topicName);
		}

		try {
			while(true) {
				if(topic.getMsgQueue().size() < Topic.MAX) {
					break;
				}
				System.out.println(String.format("topic %s is full", topicName));
				// 队列已满，访问线程阻塞
				Thread.sleep(1000);
			}

//			if(topic.getMsgQueue().size() == Topic.MAX || topic.getNum().get() == Topic.MAX) {
//				topic.getNum().set(0);
//			}

			topic.getNum().addAndGet(1);
			msg = msg+topic.getNum().get();
			topic.getMsgQueue().add(msg);
			System.out.println("生产："+msg);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}



	}

	/**
	 * 供消费者调用
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param topicName
	 * @return
	 * @throws StandardException
	 */
	public String poll(String topicName) throws StandardException{
		Topic tpc = topices.get(topicName);
		if(tpc == null) {
			throw new StandardException("topic not exist: "+topicName);
		}

		String msg = null;
		try {

			while(true) {
				if(!tpc.getMsgQueue().isEmpty()) {
					break;
				}
				// 队列为空，访问线程阻塞
				System.out.println("msg queue is empty...");
				Thread.sleep(1000);
			}

			msg = tpc.getMsgQueue().poll();
			System.out.println("消耗："+msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return msg;

	}

	public Map<String, Topic> getTopices() {
		return topices;
	}

}
