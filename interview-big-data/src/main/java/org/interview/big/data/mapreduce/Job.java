package org.interview.big.data.mapreduce;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.interview.action.Action;
import org.interview.beans.BaseMeta;
import org.interview.cluster.ClusterListener;
import org.interview.cluster.ClusterMessage;
import org.interview.cluster.ClusterServer;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.utils.FileUtil;
import org.interview.utils.HostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * MapReduce任务
 * @author shersfy
 * @date 2018-03-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Job extends BaseMeta implements ClusterListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);
	
	private Class<? extends Mapper<?, ?, ?, ?>>  mapperClass;
	private Class<? extends Reducer<?, ?, ?, ?>> reducerClass;
	
	private InputFormatDB inputFormat;
	private OutputFormatText outputFormat;
	private List<Node> nodes;
	private Map<String, OutputFormatText> cache;
	
	private Job() {
		nodes = new ArrayList<Node>();
		cache = new ConcurrentHashMap<>();
	}
	
	public static Job getInstance() {
		return new Job();
	}
	
	public boolean waitForCompletion() {
		if(mapperClass == null) {
			LOGGER.error("mapper class cannot empty");
			return false;
		}
		if(reducerClass == null) {
			LOGGER.error("reducer class cannot empty");
			return false;
		}
		
		try {
			LOGGER.info("mapper executing...");
			@SuppressWarnings("unchecked")
			Constructor<ETLMapper> mapCons  = (Constructor<ETLMapper>) mapperClass.getDeclaredConstructor(InputFormatDB.class, OutputFormatText.class);
			@SuppressWarnings("unchecked")
			Constructor<ETLReducer> redCons = (Constructor<ETLReducer>) reducerClass.getDeclaredConstructor(OutputFormatText.class, OutputFormatText.class);
			// 2. split输入将分配每个mapper到mapper节点
			// 3. 在mapper节点上执行map方法
			int blocks = nodes.size()*10;
			String[] splitSql = splitInput(blocks);
			String tmp = FileUtil.concat(getOutputFormat().getPath(), "tmp");
			int index = 1;
			for(String sql :splitSql) {
				
				int random = RandomUtils.nextInt(nodes.size())-1;
				random = random<0?0:random;
				String host = nodes.get(random).getAddress().getIpAddress().getHostAddress();
				String key  = String.format("%s_part_%s", host, index++);
				
				InputFormatDB input     = (InputFormatDB) getInputFormat().clone();
				input.setKey(key);
				input.setSplitSql(sql);
				
				OutputFormatText output = (OutputFormatText) getOutputFormat().clone();
				output.setKey(key);
				output.setPath(tmp);
				
				ETLMapper mapper = mapCons.newInstance(input, output);
				Action action = new ActionMapper(mapper);
				ClusterServer.getInstance(APP.clusterConf).sendMessage(new ClusterMessage(null, action));
			}
			
			// 4. 在mapper节点排序分区，存储在mapper节点硬盘上，把位置发送给master节点
			ClusterServer.getInstance(APP.clusterConf).addListener(this);
			while(true) {
				if(cache.keySet().size() == blocks) {
					LOGGER.info(String.format("completed %.2f%%", (double)cache.keySet().size()/blocks*100));
					break;
				}
				LOGGER.info(String.format("completed %.2f%%", (double)cache.keySet().size()/blocks*100));
				Thread.sleep(2000);
			}
			LOGGER.info("mapper executed");
			// 5. master节点汇总所有mapper结果，派发reduce节点执行reduce任务
			LOGGER.info("reducer executing...");
			for(Node node:nodes) {
				OutputFormatText tmpInput = (OutputFormatText) getOutputFormat().clone();
				tmpInput.setKey(node.getName());
				for(String key :cache.keySet()) {
					if(StringUtils.containsIgnoreCase(key, node.getName())) {
						tmpInput.getTmpFiles().add(cache.get(key).getPath());
					}
				}
				
				OutputFormatText output = (OutputFormatText) getOutputFormat().clone();
				output.setKey(HostUtil.HOSTNAME);
				
				ETLReducer reducer = redCons.newInstance(tmpInput, output);
				Action action = new ActionReducer(reducer);
				ClusterServer.getInstance(APP.clusterConf).sendMessage(new ClusterMessage(null, action));
			}
			LOGGER.info("reducer executed");
			
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			this.destroy();
		}
		
		
		return true;
	}
	
	@Override
	public void listen(String message) {
		try {
			TmpMessage tmp = JSON.parseObject(message, TmpMessage.class);
			cache.put(tmp.getKey(), tmp.getPath());
		} catch (Exception e) {
		}
	}
	
	@Override
	public void destroy() {
		ClusterServer.getInstance(APP.clusterConf).removeListener(this);
	}
	
	
	public String[] splitInput(int blockCnt) {

		String[] split = new String[blockCnt];
		String table   = getInputFormat().getTableName();
		
		DbConnectorInterface connector = null;
		Connection conn = null;
		try {
			connector = DbConnectorInterface.getInstance(getInputFormat().getDbinfo());
			conn = connector.connection();
			String countSql = "select count(1) from " + table;
			long total = connector.queryCount(countSql, conn);
			long dim = total/blockCnt;
			long start = 1;
			long end = total;
			for(int i=0; i<blockCnt; i++) {
				start = dim * i + 1;
				end = i==blockCnt-1?total:dim * (i+1);
				split[i] = String.format("select * from %s where id>=%s and id<=%s", 
						table, 
						start,
						end);
			}
			
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if(connector!=null) {
				connector.close(conn);
			}
		}
		return split;
	}

	public void setMapperClass(Class<? extends Mapper<?, ?, ?, ?>> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public void setReducerClass(Class<? extends Reducer<?, ?, ?, ?>> reducerClass) {
		this.reducerClass = reducerClass;
	}

	public InputFormatDB getInputFormat() {
		return inputFormat;
	}

	public OutputFormatText getOutputFormat() {
		return outputFormat;
	}

	public void setInputFormat(InputFormatDB inputFormat) {
		this.inputFormat = inputFormat;
	}

	public void setOutputFormat(OutputFormatText outputFormat) {
		this.outputFormat = outputFormat;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

}
