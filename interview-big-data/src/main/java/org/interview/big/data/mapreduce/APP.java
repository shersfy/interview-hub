package org.interview.big.data.mapreduce;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.interview.beans.DBMeta;
import org.interview.cluster.ClusterServer;
import org.interview.cluster.InitJgroups;
import org.interview.exception.StandardException;
import org.interview.utils.AesUtil;


/**
 * MapReduce是一种编程模型, 基于集群的高性能并行计算<br/>
 * Map（映射）<br/>
 * ReduceReduce（简化）<br/>
 * 案例<br/>
 * 从数据库A抽取数据，进行简单转换，加载到数据库B(ETL过程)。 如果数据量庞大，TB级别时，采用集群并发多个节点共同处理<br/>
 * 
 * @author shersfy
 * @date 2018-03-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class APP 
{
	public static final String clusterConf = "conf/cluster.xml";
	private static Properties conf;
	
	static {
		conf = new Properties();
		InputStream input = null;
		try {
			input = APP.class.getClassLoader().getResourceAsStream("conf/conf.properties");
			if(input!=null) {
				conf.load(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
    public static void main( String[] args )
    {
    	String cluster = conf.getProperty("cluster");
    	String[] hosts = cluster.split(",");
    	
    	if(args == null || args.length==0) {
    		InitJgroups.initClusterConfig(clusterConf, cluster, 7800);
    		// 普通节点运行
    		ClusterServer.getInstance(clusterConf);
    		return ;
    	}
    	
    	/**
    	 * 1. 用户提交MapReduce作业到Master节点上
    	 * 2. 由Master节点将M个Map任务和R个Reduce任务分配到空闲的节点上运行(就近原则)。输入文件被分成固定大小 （默认为64 MB， 用户可以调整） 的M个分片（split） 。
    	 * 	  Master节点会尽量将任务分配到离输入分片较近的节点上执行， 以减少网络通信量
    	 * 3. 在Map阶段， 被分配到Map任务的节点以输入分片作为输入， 对于每条记录， 会执行map函数， 产生一系列 key intermediate /value intermediate 对， 这些数据会缓存于内存中。
    	 * 4. 缓存的 key intermediate /value intermediate 对会按 key intermediate进行排序， 利用分区函数将输出结果分为R个区， 以一定的时间间隔写入节点的硬盘， 并将数据的位置信息传送到Master节点。
    	 * 
    	 * 5. Master节点接收到中间结果的位置信息后，会将位置信息传送给Reduce任务的节点， Reduce任务节点远程读取中间结果。
    	 *    在这一阶段中， 数据会在不同节点之间进行相互传输， 因此这一阶段也被称为数据混洗 （shuffle） 阶段。当Reduce任务读取到全部的中间结果后，
    	 *    会按 key intermediate 进行再次排序，以保证数据按 key intermediate 连续存放。
    	 * 6. 在 Reduce 阶 段 ， 处 理 有 序 的 key intermediate /value intermediate 对集合， 对相同的 key intermediate 结果进行合并处理， 执行用户提供的reduce函数， 并将最终结果写入到分布式文件系统上。
    	 */
    	
    	Node master = new Node("MasterNode", true);
    	
    	try {
    		Job job = Job.getInstance();
    		job.setName("MySQL ETL job");

    		job.setMapperClass(ETLMapper.class);
    		job.setReducerClass(ETLReducer.class);
    		
    		InputFormatDB inputFormat 	  = new InputFormatDB(getDBMeta(), conf.getProperty("tablename"));
    		OutputFormatText outputFormat = new OutputFormatText("/data/mapreduce");
			job.setInputFormat(inputFormat);
			job.setOutputFormat(outputFormat);
			
//			job.getNodes().add(node0);
			for(String host :hosts) {
				job.getNodes().add(new Node(host.trim(), false));
			}
			// 1. 提交任务到master节点
    		boolean ok = master.submitAndStart(job);
    		// 0: 正常终止
    		// 非0：异常终止
    		System.exit(ok?0:1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	private static DBMeta getDBMeta() throws StandardException {
		DBMeta db = new DBMeta();
		db.setCode("MySQL");
		
		try {
			db.setHost(conf.getProperty("host"));
			db.setPort(Integer.valueOf(conf.getProperty("port")));
			db.setDbName(conf.getProperty("dbname"));
			db.setUserName(conf.getProperty("username"));
			db.setPassword(AesUtil.encryptStr(conf.getProperty("password"), AesUtil.AES_SEED));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return db;
	}
}
