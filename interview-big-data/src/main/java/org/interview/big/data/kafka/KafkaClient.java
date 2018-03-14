package org.interview.big.data.kafka;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.interview.exception.StandardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaClient{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClient.class);
	
	private static final String path = "kafka-client.properties";
	private static Properties conf;
	private static Properties producerConf;
	private static Properties consumerConf;
	
	static {
		conf = new Properties();
		InputStream input = null;
		try {
			input = KafkaClient.class.getClassLoader().getResourceAsStream(path);
			conf.load(input);
			
			producerConf = new Properties();
			producerConf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, conf.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
			producerConf.put(ProducerConfig.ACKS_CONFIG, conf.get(ProducerConfig.ACKS_CONFIG));
			producerConf.put(ProducerConfig.RETRIES_CONFIG, conf.get(ProducerConfig.RETRIES_CONFIG));
			producerConf.put(ProducerConfig.BATCH_SIZE_CONFIG, conf.get(ProducerConfig.BATCH_SIZE_CONFIG));
			producerConf.put(ProducerConfig.LINGER_MS_CONFIG, conf.get(ProducerConfig.LINGER_MS_CONFIG));
			producerConf.put(ProducerConfig.BUFFER_MEMORY_CONFIG, conf.get(ProducerConfig.BUFFER_MEMORY_CONFIG));
			producerConf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, conf.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
			producerConf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, conf.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
			
			consumerConf = new Properties();
			consumerConf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, conf.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
			consumerConf.put(ConsumerConfig.GROUP_ID_CONFIG, conf.get(ConsumerConfig.GROUP_ID_CONFIG));
			consumerConf.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, conf.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
			consumerConf.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, conf.get(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG));
			consumerConf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, conf.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
			consumerConf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, conf.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
			
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	private KafkaClient() {}
	
	
	/**
	 * 获取kafka生产者对象
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @return Producer
	 */
	public static Producer<String, String> getProducer() {
		Producer<String, String> producer = new KafkaProducer<>(producerConf);
		return producer;
	}
	
	/**
	 * 获取kafka消费者对象
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @return Consumer
	 */
	public static Consumer<String, String> getConsumer() {
		Consumer<String, String> consumer = new KafkaConsumer<>(consumerConf);
		return consumer;
	}
	
	/**
	 * 发送消息
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @param producer 生产者实例
	 * @param topic topic名称
	 * @param data 发送数据
	 * @return Future
	 * @throws StandardException 
	 */
	public static Future<RecordMetadata> send(Producer<String, String> producer, String topic, 
			String data) throws StandardException {
		if(producer == null) {
			throw new StandardException("producer cannot null");
		}
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, data);
		return producer.send(record);
	}
	
	/**
	 * 发送消息
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @param producer 生产者实例
	 * @param topic topic名称
	 * @param data 发送数据
	 * @param callback 发送的消息被server确认后回调
	 * @return Future
	 * @throws StandardException 
	 */
	public static Future<RecordMetadata> send(Producer<String, String> producer, String topic, 
			String data, Callback callback) throws StandardException {
		if(producer == null) {
			throw new StandardException("producer cannot null");
		}
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, data);
		return producer.send(record, callback);
	}
	
	/**
	 * 订阅topic消息
	 * 
	 * @author shersfy
	 * @date 2018-03-14
	 * 
	 * @param consumer 消费者实例
	 * @param topics 被订阅的topic
	 * @param interval 更新时间间隔
	 * @param callback 每条消息记录回调
	 * @throws StandardException
	 */
	public static void subscribe(Consumer<String, String> consumer, List<String> topics, 
			int interval, SubscribeCallback callback) throws StandardException {
		
		interval = interval < 1?1:interval;
		if(consumer == null|| topics==null) {
			throw new StandardException("consumer cannot null");
		}
		
		consumer.subscribe(topics);
		while(true) {
			ConsumerRecords<String, String> records = consumer.poll(interval*1000);
			for(ConsumerRecord<String, String> record :records) {
				callback.process(record);
			}
		}
	}
	
}
