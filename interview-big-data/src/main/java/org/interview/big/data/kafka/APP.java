package org.interview.big.data.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.interview.exception.StandardException;
import org.junit.Test;

public class APP {

	@Test
	public void test01() throws StandardException {
		Producer<String, String> producer = KafkaClient.getProducer();
		KafkaClient.send(producer, "top_py", "hello world");
		producer.close();
	}

	@Test
	public void test02() throws StandardException {
		Consumer<String, String> consumer = KafkaClient.getConsumer();
		String[] topics = new String[] {"top_py"};
		SubscribeCallback callback = new SubscribeCallback() {
			
			@Override
			public void process(ConsumerRecord<String, String> record) {
				System.out.println(String.format("topic=%s, offset=%s, key=%s, value=%s", 
						record.topic(), record.offset(), record.key(), record.value()));
			}
		};
		KafkaClient.subscribe(consumer, Arrays.asList(topics), 2, callback);
		consumer.close();
	}
}
