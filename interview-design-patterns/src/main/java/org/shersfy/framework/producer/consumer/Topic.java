package org.shersfy.framework.producer.consumer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic {
	
	public static final int MAX = 10;
	
	private String name;
	private Queue<String> msgQueue;
	private AtomicInteger num;

	public Topic() {
		this.msgQueue = new ConcurrentLinkedDeque<>();
		num = new AtomicInteger(0);
	}
	
	public Topic(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Queue<String> getMsgQueue() {
		return msgQueue;
	}
	public void setMsgQueue(Queue<String> msgQueue) {
		this.msgQueue = msgQueue;
	}

	public AtomicInteger getNum() {
		return num;
	}

	public void setNum(AtomicInteger num) {
		this.num = num;
	}

}
