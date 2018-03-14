package org.interview.big.data.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 订阅消息回调, 每条消息回调一次
 * @author shersfy
 * @date 2018-03-14
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public interface SubscribeCallback {
	
	/**
	 * 处理每条消费消息记录
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @param record 消费消息记录
	 */
	public void process(ConsumerRecord<String, String> record);

}
