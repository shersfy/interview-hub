package org.interview.big.data.storm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordsReaderSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(WordsReaderSpout.class);

	private TopologyContext context;
	private SpoutOutputCollector collector;
	private String inputPath;
	private String colSep;
	private int interval;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		LOGGER.info("==========thisId={}, execute open()==============", context.getThisComponentId());
		this.context = context;
		this.collector = collector;
		// 读取配置
		inputPath = String.valueOf(conf.get("inputPath"));
		colSep = String.valueOf(conf.get("colSep"));
		interval = Integer.valueOf(String.valueOf(conf.get("interval")));
	}

	@Override
	public void nextTuple() {
		LOGGER.info("==========thisId={}, execute nextTuple() start==============", context.getThisComponentId());
		// 读取文件
		Collection<File> files = FileUtils.listFiles(new File(inputPath), null, false);
		for(File file : files) {
			try {
				List<String> lines = FileUtils.readLines(file);
				for(String line :lines) {
					String[] values = line.split(colSep);
					// 发送到下一个Bolt
					this.collector.emit(new Values(values[0], values[1]), 
							String.format("%s_%s_%s", context.getThisComponentId(), values[0], file.getName()));
				}
				// 移到备份
				File dir = new File(file.getParent(), "bak");
				File bak = new File(dir, file.getName());
				if(bak.exists()) {
					bak.delete();
				}
				FileUtils.moveFileToDirectory(file, new File(file.getParent(), "bak"), true);
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}
		if(files.isEmpty()) {
			try {
				Thread.sleep(interval*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOGGER.info("==========thisId={}, execute nextTuple() end==============", context.getThisComponentId());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		LOGGER.info("==========this={}, execute declareOutputFields(OutputFieldsDeclarer declarer)==============", this.getClass().getSimpleName());
		declarer.declare(new Fields("id", "name"));
	}

	@Override
	public void close() {
		LOGGER.info("==========thisId={}, execute close()==============", context.getThisComponentId());
	}

	@Override
	public void activate() {
		LOGGER.info("==========this={}, execute activate()==============", this.getClass().getSimpleName());
		
	}

	@Override
	public void deactivate() {
		LOGGER.info("==========thisId={}, execute deactivate()==============", context.getThisComponentId());
	}

	@Override
	public void ack(Object msgId) {
		LOGGER.info("==========thisId={}, execute ack(Object msgId={})==============", context.getThisComponentId(), msgId);
		
	}

	@Override
	public void fail(Object msgId) {
		LOGGER.info("==========thisId={}, execute fail(Object msgId={})==============", context.getThisComponentId(), msgId);
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		LOGGER.info("==========this={}, execute getComponentConfiguration()==============", this.getClass().getSimpleName());
		return null;
	}
	
}
