package org.interview.big.data.beans;


public enum HiveTableFormat {
	TEXT,
	ORC,
	PARQUET,
	RCFILE,
	SEQUENCEFILE;

	public static HiveTableFormat indexOf(Integer index) {
		if (index == null) {
			return TEXT;
		}
		switch (index) {
		case 0:
			return TEXT;
		case 1:
			return ORC;
		case 2:
			return PARQUET;
		case 3:
			return RCFILE;
		case 4:
			return SEQUENCEFILE;
		default:
			return TEXT;
		}
	}

	public int index() {
		return this.ordinal();
	}
}
