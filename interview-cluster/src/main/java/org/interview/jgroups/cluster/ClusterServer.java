package org.interview.jgroups.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.interview.jgroups.action.Action;
import org.interview.utils.HostUtil;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 集群服务
 * @author shersfy
 * @date 2018-03-06
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class ClusterServer extends ReceiverAdapter {

	private static final Logger LOGGER 	= LoggerFactory.getLogger(ClusterServer.class);
	private static final String NODE 	= String.format("%s/%s", HostUtil.HOSTNAME, HostUtil.IP);
	/**Datahub集群配置文件**/
	public static final String defaultConf 	= "cluster.xml";

	private JChannel channel;
	private List<String> state;
	private static ClusterServer cluster;
	private List<ClusterListener> listerers;
	
	/**
	 * 单例模式
	 * 
	 * @author shersfy
	 * @date 2018-03-08
	 * 
	 * @param clusterXml jgroups配置, 为空加载默认配置
	 * @return ClusterServer
	 */
	public static ClusterServer getInstance(String clusterXml) {
		
		if(cluster == null) {
			synchronized (ClusterServer.class) {
				try {
					cluster = new ClusterServer(clusterXml);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		return cluster;
	}


	/**
	 * 创建集群通信实例
	 * @throws Exception
	 */
	private ClusterServer(String clusterXml) throws Exception{

		listerers = new ArrayList<>();
		
		try {
			this.state   = Collections.synchronizedList(new LinkedList<>());
			this.channel = new JChannel(StringUtils.isBlank(clusterXml)?defaultConf:clusterXml);//使用默认配置udp.xml
			this.channel.setReceiver(this); //指定Receiver用来收消息和得到View改变的通知
			this.channel.connect("DatahubCluster"); //连接到集群
			//刚加入集群时，我们通过getState()获取聊天历史记录
			//getState()的第一个参数代表目的地地址，这里传null代表第一个实例（coordinator）
			//第二个参数代表等待超时时间，我们等待10秒。如果时间到了, State传递不过来，会抛例外。也可以传0代表永远等下去
			this.channel.getState(null, 10000);
		} catch (Exception ex) {
			LOGGER.error("datahub cluster server node start error: {}", NODE);
			LOGGER.error("", ex);
			throw ex;
		}
	}

	/**
	 * 发送消息</br>
	 * Message构造函数</br>
	 * 第一个参数代表目的地地址, 这里传null代表要发消息给集群内的所有地址</br>
	 * 第二个参数表示源地址, 传null即可，框架会自动赋值</br>
	 * 第三个参数line会被序列化成byte[]然后发送, 推荐自己序列化而不是用java自带的序列化 </br>
	 * 
	 * @param msg 消息
	 * @throws Exception
	 */
	public void sendMessage(ClusterMessage msg) throws Exception{
		if(msg == null){
			return;
		}
		LOGGER.debug("send message to node:{}, message:{}", msg.getSrc(), msg.getObject());
		if(!this.processMsg(msg, true)){
			return;
		}
		this.channel.send(msg);
	}
	
	@Override
	public void receive(Message msg) {
		if(msg == null){
			return;
		}
		// 有消息时，byte[]会被反序列化成Message对象，也可以用Message.getBuffer得到byte[]然后自己反序列化。
		//加入到历史记录
		synchronized (state) { 
			state.add(msg.getObject()!=null?msg.getObject().toString():"null");
		}
		// 处理消息
		for(ClusterListener listener :listerers) {
			listener.listen(msg.getObject()!=null?msg.getObject().toString():null);
		}
		this.processMsg(msg, false);
	}

	@Override
	public void viewAccepted(View view) {
		// 每当有实例加入或者离开集群(或崩溃)的时候，viewAccepted方法会被调用
		LOGGER.info("==========datahub cluster server node list start==========", view);
		for(Address node : view.getMembers()){
			LOGGER.info("node: {}", node.toString());
		}
		// 重新注册
		LOGGER.info("==========datahub cluster server node list end  ==========", view);
	}
	
	public boolean addListener(ClusterListener listener) {
		return listerers.add(listener);
	}
	
	public boolean removeListener(ClusterListener listener) {
		return listerers.remove(listener);
	}

	@Override
	public void getState(OutputStream output) throws Exception {
		//当JChannel.getState()被调用时, 某个原来就在集群中的实例的getState会被调用用来得到集群的共享state
		//Util.objectToStream方法将state序列化为output二进制流 
		synchronized (state) {
			Util.objectToStream(state, new DataOutputStream(output)); 
		} 
	}

	@Override
	public void setState(InputStream input) throws Exception {
		//当以上集群的共享state被得到后, 新加入集群的实例的setState方法就会被调用了
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));
		synchronized (state) {
			state.clear();
			state.addAll(list);
		}
	}
	/**
	 * 关闭通信信道
	 * 
	 */
	public void close(){
		this.channel.close();
	}
	/**
	 * 验证是否转发消息
	 * 
	 * @author PengYang
	 * @date 2017-11-09
	 * 
	 * @param msg 消息
	 * @param isSend 是否发送到集群
	 * @return true需要继续转发, false不需要继续转发
	 */
	public boolean processMsg(Message msg, boolean isSend){

		String name = "";
		String text = "";
		try {
			text = msg.getObject()==null?text:msg.getObject().toString();
			JSONObject json = JSON.parseObject(text);
			name = json==null?name:json.getString("name");
			
		} catch (Exception e) {
			
		}
		
		try {
			if(StringUtils.isNotBlank(name)){
				Class<?> clazz = (Class<?>) Class.forName(name);
				if(Action.class.getName().equals(clazz.getSuperclass().getName())) {
					Action action = (Action) JSON.parseObject(text, clazz);
					// 发送消息
					if(isSend) {
						return action.preAction();
					}
					action.doAction();
				}
				return true;
			}
			
			if(isSend) {
				LOGGER.info("received message from node:{}, message:{}", msg.getSrc(), msg.getObject());
			}
		} catch (Throwable ex) {
			LOGGER.error("", ex);
		}

		return true;
	}

}
