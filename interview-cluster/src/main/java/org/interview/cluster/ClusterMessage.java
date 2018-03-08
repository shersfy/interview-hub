package org.interview.cluster;

import org.interview.action.Action;
import org.jgroups.Address;
import org.jgroups.Message;

/**
 * 集群通信信息
 * @author shersfy
 * @date 2018-03-06
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class ClusterMessage extends Message{

	public ClusterMessage() {
		super();
	}
	/**
	 * 构造器
	 * @param action 操作
	 */
	public ClusterMessage(Action action) {
		this(null, action);
	}
	/**
	 * 构造器
	 * @param dest 目的地
	 * @param action 操作
	 */
	public ClusterMessage(Address dest, Action action) {
		super(dest, action==null?null:action.toString());
	}

	public ClusterMessage(Address dest, Object obj) {
		super(dest, obj);
	}

	public ClusterMessage(Address dest) {
		super(dest);
	}

}
