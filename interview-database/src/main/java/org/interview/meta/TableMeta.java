package org.interview.meta;

import java.util.List;

/**
 * 表元数据对象
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class TableMeta extends BaseMeta{
	
	/**table catalog **/
	private String catalog;
	/**table schema  **/
	private String schema;
	/**table type,
	 * Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". 
	 *  **/
	private TableType type;
	/**table REMARKS   **/
	private String remarks;
	/**name of the designated "identifier" column of a typed table   **/
	private String selfReferencingColName; 
	/**specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED".   **/
	private String refGeneration;
	/***Column Separator**/
	private String columnSep;
	/***is or not is partition table**/
	private boolean partitionTable;
	/***partitions**/
	private List<PartitionMeta> partitions;
	/***create table DDL**/
	private String ddl;
	/***table format**/
	private String format;
	
	public TableMeta(){}
	
	public TableMeta(TableMeta meta) {
		setName(meta.getName());
		setCatalog(meta.getCatalog());
		setColumnSep(meta.getColumnSep());
		setPartitions(meta.getPartitions());
		setRefGeneration(meta.getRefGeneration());
		setRemarks(meta.getRemarks());
		setSchema(meta.getSchema());
		setSelfReferencingColName(meta.getSelfReferencingColName());
		setType(meta.getType());
	}
	
	public TableMeta(String name){
		super(name);
	}
	public TableMeta(String catalog, String schema, String name){
		this(name);
		this.catalog = catalog;
		this.schema = schema;
	}
	
	public String getCatalog() {
		return catalog;
	}
	public String getSchema() {
		return schema;
	}
	public TableType getType() {
		return type;
	}
	public String getRemarks() {
		return remarks;
	}
	public String getSelfReferencingColName() {
		return selfReferencingColName;
	}
	public String getRefGeneration() {
		return refGeneration;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public void setType(TableType type) {
		this.type = type;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public void setSelfReferencingColName(String selfReferencingColName) {
		this.selfReferencingColName = selfReferencingColName;
	}
	public void setRefGeneration(String refGeneration) {
		this.refGeneration = refGeneration;
	}
	public String getColumnSep() {
		return columnSep;
	}
	public void setColumnSep(String columnSep) {
		this.columnSep = columnSep;
	}
	public List<PartitionMeta> getPartitions() {
		return partitions;
	}
	public void setPartitions(List<PartitionMeta> partitions) {
		this.partitions = partitions;
	}
	public final boolean isPartitionTable() {
		return partitionTable;
	}
	public final void setPartitionTable(boolean partitionTable) {
		this.partitionTable = partitionTable;
	}
	public String getDdl() {
		return ddl;
	}
	public String getFormat() {
		return format;
	}
	public void setDdl(String ddl) {
		this.ddl = ddl;
	}
	public void setFormat(String format) {
		this.format = format;
	}

}
