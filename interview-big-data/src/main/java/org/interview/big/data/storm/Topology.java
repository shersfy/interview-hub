package org.interview.big.data.storm;

import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

public class Topology {

	private String inputPath;
	private String colSep;
	private int interval;
	private int timeout;
	
	public Topology(String inputPath, String colSep, int interval, int timeout) {
		this.inputPath = inputPath;
		this.colSep = colSep;
		this.interval = interval;
		this.timeout = timeout;
	}
	public void submitWordsTopology() {
		/**
		 * 实时统计每个姓氏人数
		 * 实时统计每个名字相同人数
		 * 
		 * ---------------
		 * id	|	name
		 * 1	|	zhang_san
		 * 2	|	li_si
		 * 3	|	wang_wu
		 * ---------------
		 * 
		 * 
		 * spout步骤中nextTuple()
		 * tuple1 values(1, 'zhang_san')
		 * tuple2 values(2, 'li_si')
		 * tuple3 values(3, 'wang_wu')
		 * 
		 * spout步骤中declareOutputFields()
		 * declare(new Fields("id", "name"))
		 * 
		 * bolt1步骤中execute(Tuple input)
		 * input.get(0)-->tuple1 1
		 * input.get(1)-->tuple1 'zhang_san'
		 * 
		 * tuple1 values(1, 'zhang', 'san')
		 * 
		 * bolt1步骤中declareOutputFields()
		 * declare(new Fields("id", "first_name", "last_name"))
		 * 
		 * bolt2步骤中execute(Tuple input)
		 * input.get(0)-->tuple1 1
		 * input.get(1)-->tuple1 'zhang'
		 * input.get(2)-->tuple1 'san'
		 * 
		 */
		
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("words-reader", new WordsReaderSpout());
		builder.setBolt("words-record", new WordsRecordBolt()).shuffleGrouping("words-reader");
		builder.setBolt("words-counter", new WordsCounterBolt()).shuffleGrouping("words-record");
		
		Config conf = new Config();
		conf.put("inputPath", inputPath);
		conf.put("colSep", colSep);
		conf.put("interval", interval);
		conf.setDebug(true);
		
		LocalCluster local = new LocalCluster();
		System.out.println("===========topology start=====================");
		local.submitTopology("words-counter-topology", conf, builder.createTopology());
		
		int sleep = 0;
		while(sleep<timeout) {
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sleep +=2;
		}
		System.out.println("===========topology end=====================");
		local.shutdown();
		
	}
	
	public void submitKafkaTopology() {
		
		String topic  = "top_py";
		String zkRoot = "/"+topic;
		String zkNodes= "demo8.leap.com:2181";
		
		BrokerHosts hosts = new ZkHosts(zkNodes);
		SpoutConfig kafkaSpoutConfig = new SpoutConfig(hosts, topic, zkRoot, UUID.randomUUID().toString());

		kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
		kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
//	    kafkaSpoutConfig.forceFromStart = true;
		kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout-kafka", new KafkaSpout(kafkaSpoutConfig));
		builder.setBolt("words-record", new WordsRecordBolt()).shuffleGrouping("spout-kafka");
		builder.setBolt("words-counter", new WordsCounterBolt()).shuffleGrouping("words-record");
		
		Config conf = new Config();
		conf.put("inputPath", inputPath);
		conf.put("colSep", colSep);
		conf.put("interval", interval);
		conf.setDebug(true);
		
		LocalCluster local = new LocalCluster();
		System.out.println("===========topology start=====================");
		local.submitTopology("words-counter-topology", conf, builder.createTopology());
		
		int sleep = 0;
		while(sleep<timeout) {
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sleep +=2;
		}
		System.out.println("===========topology end=====================");
		local.shutdown();
		
	}
}
