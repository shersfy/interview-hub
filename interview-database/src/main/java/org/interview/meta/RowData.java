package org.interview.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据行
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class RowData extends Data{

	// 一行数据的各个字段
	private List<FieldData> fields;

	public RowData() {
		fields =  new ArrayList<FieldData>();
	}

	public RowData(List<FieldData> fields) {
		this.fields = fields;
	}
	
	public RowData(FieldData[] objects) {
		fields = new ArrayList<FieldData>();
		for (FieldData object : objects) {
			fields.add(object);
		}
	}

	public List<FieldData> getFields() {
		return fields;
	}

	public void setFields(List<FieldData> fields) {
		this.fields = fields;
	}
	
	/**
	 * 行转map, key为字段名
	 * 
	 * @param headers
	 *            字段名
	 * @param row
	 *            行数据
	 * @return Map<String, Object>
	 */
	public Map<String, Object> rowToMap(List<ColumnMeta> headers, RowData row) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (headers != null && row != null) {
			List<FieldData> values = row.getFields();
			for (int i = 0; i < headers.size(); i++) {
				map.put(headers.get(i).getName(), values.get(i));
			}
		}
		return map;
	}


}
