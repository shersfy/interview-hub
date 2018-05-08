package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

public class MemoSegment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long createTime;
	private List<MemoUsage> heapPools;
	private List<MemoUsage> nonHeapPools;
	
	public MemoSegment(){
		heapPools = new ArrayList<>();
		nonHeapPools = new ArrayList<>();
	}
	
	public List<MemoUsage> getHeapPools() {
		return heapPools;
	}
	
	public List<MemoUsage> getNonHeapPools() {
		return nonHeapPools;
	}
	
	public void setHeapPools(List<MemoUsage> heapPools) {
		this.heapPools = heapPools;
	}
	
	public void setNonHeapPools(List<MemoUsage> nonHeapPools) {
		this.nonHeapPools = nonHeapPools;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
