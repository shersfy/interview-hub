package org.interview.big.data.storm;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordsRecordBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(WordsReaderSpout.class);
	private TopologyContext context;
	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		LOGGER.info("==========thisId={}, execute prepare()==============", context.getThisComponentId());
		this.context = context;
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		LOGGER.info("==========execute execute(Tuple input)==============", context.getThisComponentId());
		LOGGER.info("thisId={}, msgId={}", context.getThisComponentId(), input.getMessageId());
		// id
		String id  = input.getString(0);
		// name
		String name = input.getString(1);
		
		String[] arr = name.split("_");
		String firstName = arr[0];
		String lastName	 = arr[1];
		collector.emit(new Values(id, firstName, lastName));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		LOGGER.info("==========this={}, execute declareOutputFields(OutputFieldsDeclarer declarer)==============", this.getClass().getSimpleName());
		declarer.declare(new Fields("id", "first_name", "last_name"));
	}
	
	@Override
	public void cleanup() {
		LOGGER.info("==========thisId={}, execute cleanup()==============", context.getThisComponentId());
	}


	@Override
	public Map<String, Object> getComponentConfiguration() {
		LOGGER.info("==========this={}, execute getComponentConfiguration()==============", this.getClass().getSimpleName());
		return null;
	}

}
