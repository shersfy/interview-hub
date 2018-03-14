package org.interview.big.data.kafka;

import org.interview.beans.BaseMeta;

public class KafkaMeta extends BaseMeta{

	private String bootServers;
	private int retries;
	
	public KafkaMeta() {
		super();
	}
	
	public KafkaMeta(String bootServers) {
		super();
		this.bootServers = bootServers;
	}
	public String getBootServers() {
		return bootServers;
	}
	public int getRetries() {
		return retries;
	}
	public void setBootServers(String bootServers) {
		this.bootServers = bootServers;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
}
