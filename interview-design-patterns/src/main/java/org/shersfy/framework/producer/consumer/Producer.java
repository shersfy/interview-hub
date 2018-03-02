package org.shersfy.framework.producer.consumer;

import org.interview.exception.StandardException;

public class Producer {

	private Broker broker;
	public Producer(Broker broker) {
		this.broker = broker;
	}
	
	/**
	 * 生产产品
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param topic
	 * @param msg
	 * @throws StandardException
	 */
	public void build(String topic, String msg) throws StandardException {
			broker.push(topic, msg);
	}
	
}
