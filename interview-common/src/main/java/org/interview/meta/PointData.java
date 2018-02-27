package org.interview.meta;

/**
 * 点数据
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class PointData extends Data {
	
	private long xvalue;
	
	private long yvalue;

	public PointData() {
		super();
	}
	
	public PointData(long xvalue, long yvalue) {
		super();
		this.xvalue = xvalue;
		this.yvalue = yvalue;
	}

	public long getXvalue() {
		return xvalue;
	}

	public long getYvalue() {
		return yvalue;
	}

	public void setXvalue(long xvalue) {
		this.xvalue = xvalue;
	}

	public void setYvalue(long yvalue) {
		this.yvalue = yvalue;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", xvalue, yvalue);
	}
	
}
