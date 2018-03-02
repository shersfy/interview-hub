package org.shersfy.framework.producer.consumer;

import org.interview.exception.StandardException;

public class Consumer {
	
	private Broker broker;

	public Consumer(Broker broker) {
		this.broker = broker;
	}
	
	/**
	 * 消耗产品
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param topicName
	 * @throws StandardException
	 */
	public void consume(String topicName) throws StandardException {
			String msg = broker.poll(topicName);
			if(msg == null) {
				return;
			}
		
	}
}
