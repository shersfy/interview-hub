package org.interview.big.data.mapreduce;

import java.net.InetSocketAddress;

import org.interview.beans.BaseMeta;
import org.jgroups.stack.IpAddress;

public class Node extends BaseMeta{
	
	private boolean master;
	private IpAddress address;

	public Node(String hostname, boolean isMaster) {
		super();
		this.address = new IpAddress(new InetSocketAddress(hostname, 7800));
		this.master = isMaster;
		this.setName(hostname);
	}
	
	public boolean submitAndStart(Job job) {
		System.out.println(String.format("node %s running job %s", getName(), job.getName()));
		return job.waitForCompletion();
	}



	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public IpAddress getAddress() {
		return address;
	}

	public void setAddress(IpAddress address) {
		this.address = address;
	}

}
