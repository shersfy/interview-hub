package org.interview.excel;

import java.util.ArrayList;
import java.util.List;

import org.interview.meta.Data;


/**
 * excel工作簿信息
 * @author shersfy
 * @date 2018-02-27
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class WorkbookMeta extends Data {
	
	private long totalRowSize;
	private List<SheetMeta> sheets;

	public WorkbookMeta(){
		sheets = new ArrayList<>();
	}
	
	public WorkbookMeta(String name){
		this();
		super.setName(name);
	}
	
	public List<SheetMeta> getSheets() {
		return sheets;
	}

	public void setSheets(List<SheetMeta> sheets) {
		this.sheets = sheets;
	}

	public long getTotalRowSize() {
		return totalRowSize;
	}

	public void setTotalRowSize(long totalRowSize) {
		this.totalRowSize = totalRowSize;
	}
	
	

}
