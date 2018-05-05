package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

public class JVMMemoSegment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long createTime;
	private List<JVMMemoUsage> heapPools;
	private List<JVMMemoUsage> nonHeapPools;
	
	public JVMMemoSegment(){
		heapPools = new ArrayList<>();
		nonHeapPools = new ArrayList<>();
	}
	
	public List<JVMMemoUsage> getHeapPools() {
		return heapPools;
	}
	
	public List<JVMMemoUsage> getNonHeapPools() {
		return nonHeapPools;
	}
	
	public void setHeapPools(List<JVMMemoUsage> heapPools) {
		this.heapPools = heapPools;
	}
	
	public void setNonHeapPools(List<JVMMemoUsage> nonHeapPools) {
		this.nonHeapPools = nonHeapPools;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
