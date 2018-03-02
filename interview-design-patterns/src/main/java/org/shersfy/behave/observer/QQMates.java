package org.shersfy.behave.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QQMates {

	private String nikeName;
	private List<QQMates> group;
	private List<String> msgs;
	
	public QQMates() {
		group = new ArrayList<>();
		msgs  = new CopyOnWriteArrayList<>();
	}
	
	public QQMates(String nikeName) {
		this();
		this.nikeName = nikeName;
	}
	
	/**
	 * 在群里发送信息, 并通知所有好友收取信息
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {
		for(QQMates mate :group) {
			mate.msgs.add(msg);
			mate.receivedMsg(nikeName);
		}
	}
	
	/**
	 * 收信息
	 * 
	 * @author shersfy
	 * @date 2018-03-02
	 * 
	 * @param sendor 发送者
	 */
	public void receivedMsg(String sendor) {
		if(msgs.isEmpty()) {
			return;
		}
		System.out.println(String.format("=====%s start=====", nikeName));
		for(String msg :msgs) {
			System.out.println(String.format("%s: %s", sendor, msg));
		}
		System.out.println(String.format("=====%s end=====", nikeName));
	}

	public String getNikeName() {
		return nikeName;
	}
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}

	public List<QQMates> getGroup() {
		return group;
	}

	public void setGroup(List<QQMates> group) {
		this.group = group;
	}
	
}
