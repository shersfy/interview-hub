package org.interview.big.data.storm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordsCounterBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(WordsReaderSpout.class);
	
	private TopologyContext context;
	private Map<String, Integer> counter1;
	private Map<String, Integer> counter2;
	private int interval;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		LOGGER.info("==========thisId={}, execute prepare()==============", context.getThisComponentId());
		this.counter1 = new HashMap<>();
		this.counter2 = new HashMap<>();
		this.context = context;
		interval = Integer.valueOf(String.valueOf(conf.get("interval")));
		
		// 另起一个线程打印
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(interval*1000);
					} catch (InterruptedException e) {
						LOGGER.error("", e);
					}
					print();
				}
				
			}
		}).start();
	}

	@Override
	public void execute(Tuple input) {
		LOGGER.info("==========execute execute(Tuple input)==============", context.getThisComponentId());
		LOGGER.info("thisId={}, msgId={}, record id={}", 
				context.getThisComponentId(), input.getMessageId(), input.getString(0));
		
		String firstName = input.getString(1);
		String lastName  = input.getStringByField("last_name");
		
		if(!counter1.keySet().contains(firstName)) {
			counter1.put(firstName, 1);
		} else {
			counter1.put(firstName, counter1.get(firstName)+1);
		}
		
		if(!counter2.keySet().contains(lastName)) {
			counter2.put(lastName, 1);
		} else {
			counter2.put(lastName, counter2.get(lastName)+1);
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		LOGGER.info("==========this={}, execute declareOutputFields(OutputFieldsDeclarer declarer)==============", this.getClass().getSimpleName());
	}

	@Override
	public void cleanup() {
		LOGGER.info("==========thisId={}, execute cleanup()==============", context.getThisComponentId());
		super.cleanup();
	}
	
	private void print() {
		LOGGER.info("========same first_name count start===============");
		for(Entry<String, Integer> entry :counter1.entrySet()) {
			LOGGER.info("========{}: {}", entry.getKey(), entry.getValue());
		}
		LOGGER.info("========same first_name count end===============");
		LOGGER.info("========same last_name count start===============");
		for(Entry<String, Integer> entry :counter2.entrySet()) {
			LOGGER.info("========{}: {}", entry.getKey(), entry.getValue());
		}
		LOGGER.info("========same last_name count end===============");
	}


}
