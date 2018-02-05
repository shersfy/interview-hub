package org.interview.meta;

import java.util.List;

/**
 * 分块条件
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class TablePartitions extends BaseMeta{

	/**分块索引**/
	private int index;
	/**分块执行SQL**/
	private String partSql;
	/**分块条件**/
	private String condition;
	/**分块条件参数**/
	private List<Object> conditionArgs;
	/**分块字段**/
	private ColumnMeta partColumn;

	public TablePartitions(){}
	
	public TablePartitions (String condition){
		this.condition = condition;
	}

	public int getIndex() {
		return index;
	}

	public String getCondition() {
		return condition;
	}

	public List<Object> getConditionArgs() {
		return conditionArgs;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setConditionArgs(List<Object> conditionArgs) {
		this.conditionArgs = conditionArgs;
	}

	public ColumnMeta getPartColumn() {
		return partColumn;
	}

	public void setPartColumn(ColumnMeta partColumn) {
		this.partColumn = partColumn;
	}

	public String getPartSql() {
		return partSql;
	}

	public void setPartSql(String partSql) {
		this.partSql = partSql;
	}

	@Override
	public String getName() {
		String name = super.getName();
		if(name==null){
			name = String.format("part_%s", this.index);
		}
		return name;
	}

}
