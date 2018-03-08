package org.interview.action;

import java.io.Serializable;

import org.interview.beans.BaseMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Action extends BaseMeta implements Serializable{
	
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER 	= LoggerFactory.getLogger(Action.class);
	
	public Action() {
		super();
		setName(this.getClass().getName());
	}

	/**
	 * action还没发送到集群前调用
	 * 
	 * @author shersfy
	 * @date 2018-03-06
	 * 
	 * @return 返回true，发送action到集群；false终止发送action 
	 */
	public boolean preAction() {
		return true;
	}
	
	/**
	 * 集群节点执行action处理
	 * 
	 * @author shersfy
	 * @date 2018-03-06
	 *
	 */
	public abstract void doAction();
}
