package org.interview.connector;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import oracle.sql.TIMESTAMP;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.interview.beans.ColumnMeta;
import org.interview.beans.ConnectorManager;
import org.interview.beans.DBAccessType;
import org.interview.beans.DBMeta;
import org.interview.beans.FieldData;
import org.interview.beans.FunctionDef;
import org.interview.beans.GridData;
import org.interview.beans.PartitionMeta;
import org.interview.beans.RowData;
import org.interview.beans.StoredProc;
import org.interview.beans.TableMeta;
import org.interview.beans.TablePartitions;
import org.interview.beans.TableType;
import org.interview.common.Const;
import org.interview.exception.StandardException;
import org.interview.exception.NetException;
import org.interview.exception.TooManyConnectionException;
import org.interview.utils.AesUtil;
import org.interview.utils.DateUtil;


/**
 * 数据库连接器接口抽象类<br/>
 * 其子类的命名规则为: 资源库数据表 [db_type.code]+Connector.java, 如OracleConnector.java
 * 
 * @author PengYang
 * @date 2016-10-25
 *
 * @copyright Copyright shersfy 2016 All Rights Reserved.
 */
public abstract class DbConnectorInterface {

	/**默认超时10s**/
	public static final int TIMEOUT = 10000;
	protected static Logger logger  = LoggerFactory.getLogger(DbConnectorInterface.class);
	protected DBMeta dbMeta			= null;
	/**资源管理器**/
    private ConnectorManager manager;

	/**
	 * 加载驱动类, 默认加载JDBC驱动
	 * 
	 * @author PengYang
	 * @date 2016-10-26
	 * 
	 * @param type
	 *            访问类型
	 * 
	 * @throws StandardException
	 */
	public abstract String loadDriverClass(DBAccessType type);

	/***
	 * 列出所有的数据库, 返回结果数据库连接密码已加密。
	 * 
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return List<DBMeta>
	 */
	public abstract List<DBMeta> getDatabases(Connection conn) throws StandardException;

	/**
	 * 获取创建表的DDL。根据列参数创建, 当列参数为空时, 显示已经存在的表DDL。 <br/>
	 * 返回: 建表SQL语句 <br/>
	 * 
	 * @author PengYang
	 * @date 2017-02-07
	 * 
	 * @param table 表元信息
	 * @param columns 各列
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return
	 * @throws StandardException
	 */
	public String getDDLByTable(TableMeta table, List<ColumnMeta> columns, Connection conn) 
			throws StandardException{

		if (table == null || StringUtils.isBlank(table.getName())) {
			return StringUtils.EMPTY;
		}
		
		table.setCatalog(StringUtils.isBlank(table.getCatalog())?null:table.getCatalog());
		table.setSchema(StringUtils.isBlank(table.getSchema())?null:table.getSchema());

		if (columns == null || columns.isEmpty()) {
			return showCreateTable(table, conn);
		}

		for(int i=0; i<columns.size(); i++){
			if(StringUtils.isBlank(columns.get(i).getName())){
				throw new StandardException(String.format("column name can not empty [index=%s]", i));
			}
		}

		String ddl = "CREATE TABLE %s (\n%s\n)";
		String pk = "PRIMARY KEY (%s)";

		StringBuffer pks = new StringBuffer(0);
		StringBuffer cols = new StringBuffer(0);

		String blank 	= " ";
		String colSep 	= ", ";
		String lineSep 	= ", \n";

		for (int i = 0; i < columns.size(); i++) {
			// 注释
			//			String comment = columns.get(i).getRemarks();
			//			if(StringUtils.isNotBlank(comment)){
			//				cols.append(" -- ");
			//				cols.append(comment);
			//				cols.append(lineSep);
			//			}
			// name
			cols.append(quotObject(columns.get(i).getName()));
			cols.append(blank);
			// type
			String type = javaTypeToDbType(columns.get(i).getDataType(), columns.get(i));
			cols.append(type);
			// is null
			cols.append(blank);
			if (ColumnMeta.NO.equals(columns.get(i).getIsNullable())) {
				cols.append(" NOT NULL ");
			}
			// PK
			if(columns.get(i).isPk()){
				pks.append(quotObject(columns.get(i).getName()));
				pks.append(colSep);
			}

			cols.append(lineSep);
		}

		if(pks.length() != 0){
			pk = String.format(pk, pks.substring(0, pks.length()-colSep.length()));
			cols.append(pk).append(lineSep);
		}

		ddl = String.format(ddl, 
				getFullTableName(table), 
				cols.substring(0, cols.length()-lineSep.length()));

		return ddl;

	}

	/**
	 * 给数据对象添加引号, 返回已添加引号的对象名
	 * 
	 * @author PengYang
	 * @date 2017-03-09
	 * 
	 * @param objName 引号前对象名
	 * @return String
	 */
	public String quotObject(String objName) {
		if(StringUtils.isNotBlank(objName)){
			objName = "\"" + objName + "\"";
		}
		return objName;
	}

	/**
	 * 显示建表SQL。
	 * 
	 * @param table 表
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return String
	 * @throws StandardException
	 */
	public abstract String showCreateTable(TableMeta table, Connection conn) 
			throws StandardException;

	/**
	 * 表是否存在, true 存在, false不存在
	 * 
	 * @author PengYang
	 * @date 2017-03-31
	 * 
	 * @param table
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return boolean
	 * @throws StandardException 
	 */
	public boolean exist(TableMeta table, Connection conn) throws StandardException{
		if(table==null|| conn==null){
			return false;
		}
		table.setCatalog(StringUtils.isBlank(table.getCatalog())?null:table.getCatalog());
		table.setSchema(StringUtils.isBlank(table.getSchema())?null:table.getSchema());
		List<ColumnMeta> list = new ArrayList<>();
		try {
			list = getColumns(table, conn);
		} catch (StandardException de) {
			if(de.getCause()!=null 
					&& de.getCause().getMessage()!=null 
					&& de.getCause().getMessage().contains("doesn't exist")){
				return false;
			}
			throw de;
		}
		return !list.isEmpty();
	}

	/**
	 * 获取数据库表的全限定名
	 * 
	 * @author PengYang
	 * @date 2017-03-09
	 * 
	 * @param table
	 * @return String
	 * @throws StandardException
	 */
	public String getFullTableName(TableMeta table) {

		StringBuffer name = new StringBuffer(0);
		if(table != null && StringUtils.isNotBlank(table.getName())){

			if(StringUtils.isNotBlank(table.getSchema())){
				name.append(quotObject(table.getSchema()));
				name.append(".");
			}

			name.append(quotObject(table.getName()));
		}

		return name.toString();
	}

	/***
	 * 将java.sql.Types类型映射为数据库类型<br/>
	 * 返回: 数据库类型
	 * 
	 * @author PengYang
	 * @date 2017-02-09
	 * 
	 * @param javaType java.sql.Types类型
	 * @param column 列元信息
	 * @return String
	 * @throws StandardException
	 */
	public abstract String javaTypeToDbType(int javaType, ColumnMeta column) 
			throws StandardException;

	/**
	 * 创建表<br/>
	 * 返回true：创建成功<br/>
	 * 返回false：创建失败<br/>
	 * 
	 * @param sql 建表SQL 
	 * @param conn 数据库连接
	 * @return
	 * @throws StandardException
	 */
	public boolean createTable(String sql, Connection conn) throws StandardException{
		if(conn==null||StringUtils.isBlank(sql)){
			return false;
		}
		Statement st	= null;
		try {
			logger.info(sql);
			st = conn.createStatement();
			st.execute(sql);
			// true if the first result is a ResultSet object; 
			// false if it is an update count or there are no results
			return true;
		} catch (Exception e) {
			throw new StandardException(e, "create table error\n%s", e.getMessage());
		} finally {
			close(st);
		}
	}

	/**
	 * 批量创建表<br/>
	 * 返回true：创建成功<br/>
	 * 返回false：创建失败<br/>
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @param sqls 建表SQL 
	 * @return 成功返回true, 失败返回false, sqls为空返回null
	 * @throws StandardException
	 */
	public boolean[] createTable(String[] sqls, Connection conn){

		if(conn == null || sqls == null || sqls.length==0){
			return null;
		}

		Statement st	= null;
		boolean[] res 	= new boolean[sqls.length];

		try {
			if(conn.getMetaData().supportsBatchUpdates()){
				st = conn.createStatement();
				List<Integer> addIndex = new ArrayList<>();
				for(int i=0; i<sqls.length; i++){
					if(StringUtils.isBlank(sqls[i])){
						res[i] = false;
						continue;
					}
					st.addBatch(sqls[i]);
					logger.info(sqls[i]);
					addIndex.add(i);
				}
				int[] arr = st.executeBatch();
				for(int index=0; index<arr.length; index++){
					int resIndex 	= addIndex.get(index);
					if(arr[index] == Statement.SUCCESS_NO_INFO || arr[index]>=0){
						res[resIndex] 	= true;
					}
					else{
						res[resIndex] 	= false;
					}
				}

			} else {
				for(int i=0; i<sqls.length; i++){
					try {
						res[i] = this.createTable(sqls[i], conn);
					} catch (Exception e) {
						logger.error("create table error", e);
					}
				}
			}

		} catch (Exception e) {
			logger.error("create table error", e);
		} finally {
			close(st);
		}

		return res;
	}

	/**
	 * 删除表
	 * 
	 * @param table
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return boolean
	 * @throws StandardException
	 */
	public boolean dropTable(TableMeta table, Connection conn) throws StandardException{
		if(conn == null
				|| table==null
				|| StringUtils.isBlank(table.getName())){
			return false;
		}
		Statement st	= null;
		try {
			String sql = "DROP TABLE %s";
			sql = String.format(sql, getFullTableName(table));
			sql = StringEscapeUtils.unescapeJava(sql);
			logger.info(sql);
			st = conn.createStatement();
			boolean res = st.execute(sql);
			// true if the first result is a ResultSet object; 
			// false if it is an update count or there are no resultsThrows:
			if(!res){
				return true;
			}
		} catch (Exception e) {
			StandardException de = new StandardException("drop table error");
			logger.error("drop table error", e);
			throw de;
		} finally {
			close(st);
		}
		return false;
	}
	/***
	 * 列出所有的存储过程
	 * 
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return List<DBMeta>
	 */
	public List<StoredProc> getStoredProces(Connection conn) 
			throws StandardException{
		return null;
	}
	/***
	 * 列出所有用户定义功能函数
	 * 
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return List<DBMeta>
	 */
	public List<FunctionDef> getFunctonsByUserDef(Connection conn) throws StandardException{
		return null;
	}
	/**
	 * 分页查询
	 * 
	 * @author PengYang
	 * @date 2016-11-07
	 * 
	 * @param baseSql
	 *            表名(传SELECT * FROM TBL_NAME)或标准SQL语句
	 * @param pageNo
	 *            页编号
	 * @param pageSize
	 *            页大小
	 * @param totalPage
	 *            总页数
	 * @return String 处理过的SQL语句
	 */
	public abstract String queryByPage(String baseSql, long pageNo,
			long pageSize)  throws StandardException;
	
	/**
	 * 获取分块切分点的查询SQL
	 * 
	 * 
	 * @param partColumn 分块字段
	 * @param baseSql 基础SQL
	 * @param totalSize 分块整体记录数量
	 * @param blockCnt 分块块数
	 * @return 查询SQL
	 */
	public abstract String getCutPointSql(TableMeta table, ColumnMeta partColumn, 
			String where, long totalSize, int blockCnt);
	
	/**
	 * 
	 * 获取数据库条件
	 * 
	 * @param partColumn 分块字段
	 * @param cutPoints 切分点
	 * @return 分块条件
	 */
	public List<TablePartitions> getBlocks(ColumnMeta partColumn, List<FieldData> cutPoints){
		List<TablePartitions> blocks = new ArrayList<>();
		if(partColumn == null || cutPoints == null){
			return blocks;
		}
		
		String partName = this.quotObject(partColumn.getName());
		StringBuffer block = new StringBuffer(0);
		if(cutPoints.size() == 1){
			block.setLength(0);
			block.append(partName);
			block.append(" = N\'").append(cutPoints.get(0).getValue()).append("\'");
			
			TablePartitions part = new TablePartitions(block.toString());
			part.setIndex(0);
			part.setPartColumn(partColumn);
			blocks.add(part);
		}
		
		for(int i=0; i<cutPoints.size()-1; i++){
			block.setLength(0);
			String start = String.valueOf(cutPoints.get(i).getValue());
			String end = String.valueOf(cutPoints.get(i+1).getValue());
			
			block.append(partName);
			block.append(" >= N\'").append(start).append("\'");
			block.append(" AND ");
			block.append(partName).append(i==cutPoints.size()-2?" <= ":" < ").append(" N\'").append(end).append("\'");
			
			TablePartitions part = new TablePartitions(block.toString());
			part.setIndex(i);
			part.setPartColumn(partColumn);
			blocks.add(part);
		}
		return blocks;
	}

	/**
	 * 获取表名。包括获取系统表名，视图等。
	 * 
	 * @param catalog
	 * @param schema
	 * @param types表类型 ，为null获取全部
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return List<String>
	 * @throws StandardException
	 */
	public List<TableMeta> getTables(String catalog, String schema, TableType[] types, 
			Connection conn) throws StandardException {

		List<TableMeta> list = new ArrayList<TableMeta>();
		ResultSet res = null;
		try {
			if(conn == null){
				return list;
			}
			String[] tps = null;
			if(types != null){
				tps = new String[types.length];
				for(int i=0 ; i<tps.length; i++){
					tps[i] = types[i].getTypeName();
				}
			}

			if(StringUtils.isBlank(catalog)){
				catalog = null;
			}
			if(StringUtils.isBlank(schema)){
				schema = null;
			}

			res = conn.getMetaData().getTables(catalog,
					schema, "%", tps);

			while (res.next()) {
				TableMeta meta = new TableMeta();
				meta.setCatalog(res.getString("TABLE_CAT"));
				meta.setName(res.getString("TABLE_NAME"));
				meta.setSchema(res.getString("TABLE_SCHEM"));
				TableType type = TableType.valueOfByName(res.getString("TABLE_TYPE"));
				meta.setType(type);
				meta.setRemarks(res.getString("REMARKS"));
				//meta.setRefGeneration(res.getString("REF_GENERATION"));
				//meta.setSelfReferencingColName(res.getString("SELF_REFERENCING_COL_NAME"));
				list.add(meta);
			}
			if(!list.isEmpty()){
				list.sort(new Comparator<TableMeta>() {
					@Override
					public int compare(TableMeta o1, TableMeta o2) {
						String name1 = o1.getName().toLowerCase();
						String name2 = o2.getName().toLowerCase();
						return name1.compareTo(name2);
					}
				});
			}
		} catch (Exception e) {
			String err = String.format("Get tables error [catalog=%s, schema=%s]", catalog, schema);
			throw new StandardException(err, e);
		} finally {
			close(res);
		}
		
		return list;

	}

	/**
	 * 获取表的分区字段
	 * 
	 * @param table 表信息
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return List<ColumnMeta>
	 * @throws StandardException
	 */
	public List<ColumnMeta> getPartitionColumns(TableMeta table, Connection conn)
			throws StandardException {
		if(table == null || StringUtils.isBlank(table.getName())){
			throw new StandardException("table name can not empty");
		}

		if(StringUtils.isBlank(table.getSchema())){
			table.setSchema(null);
		}

		if(StringUtils.isBlank(table.getCatalog())){
			table.setCatalog(null);
		}
		List<ColumnMeta> cols = new ArrayList<ColumnMeta>();
		return cols;
	}
	
	public List<ColumnMeta> getPartitionColumns(TableMeta table, Connection conn, String ddl) 
			throws StandardException{
		return getPartitionColumns(table, conn);
	}

	/**
	 * 
	 * 获取分区字段的分区值
	 * @author PengYang
	 * @date 2017-03-13
	 * 
	 * @param table 表信息
	 * @param partCol 分区字段
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return List<String>
	 * @throws StandardException
	 */
	public List<PartitionMeta> getPartitions(TableMeta table, Connection conn)
			throws StandardException {
		if(table == null || StringUtils.isBlank(table.getName())){
			throw new StandardException("table name can not empty");
		}

		if(StringUtils.isBlank(table.getSchema())){
			table.setSchema(null);
		}

		if(StringUtils.isBlank(table.getCatalog())){
			table.setCatalog(null);
		}
		List<PartitionMeta> parts = new ArrayList<PartitionMeta>();
		return parts;
	}

	/**
	 * 获取hive表的列分隔符
	 * 
	 * @author PengYang
	 * @date 2017-02-14
	 * 
	 * @param table 表
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return String
	 */
	public String getColumnSep(String ddl) {
		String sep = Const.COLUMN_SEP;
		if(StringUtils.isBlank(ddl)){
			return sep;
		}

		String search = "FIELDS TERMINATED BY";
		int index = ddl.indexOf(search);
		if(index == -1){
			search = "'field.delim'";
			index = ddl.indexOf(search);
			index = index==-1?-1:index+search.length();
		}
		if(index != -1){
			index = ddl.indexOf("'", index);
			if(index != -1 && ddl.length()>(index+1)){
				sep = ddl.substring(index+1, ddl.indexOf("'", index+1));
				// 忽略转义
				sep = StringEscapeUtils.unescapeJava(sep);
			}
		}

		return sep;
	}

	/**
	 * 获取hive表的列分隔符
	 * 
	 * @author PengYang
	 * @date 2017-02-14
	 * 
	 * @param table 表
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return String
	 */
	public String getColumnSep(TableMeta table, Connection conn){

		String sep = Const.COLUMN_SEP;
		try {
			String ddl = showCreateTable(table, conn);
			sep = getColumnSep(ddl);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return sep;
	}
	/**
	 * 获取表字段信息, 并判定是否为分区字段
	 * 
	 * @author PengYang
	 * @date 2017-11-21
	 * 
	 * @param table 表
	 * @param conn 连接
	 * @return 
	 * @throws StandardException
	 */
	public List<ColumnMeta> getColumnsWithPartition(TableMeta table, Connection conn)
			throws StandardException {
		return getColumns(table, conn);
	}
	
	public List<ColumnMeta> getColumnsWithPartition(TableMeta table, Connection conn, String ddl)
			throws StandardException {
		return getColumns(table, conn);
	}

	/**
	 * 获取表字段信息
	 * 
	 * @author PengYang
	 * @date 2017-11-21
	 * 
	 * @param table 表
	 * @param conn 连接
	 * @return 
	 * @throws StandardException
	 */
	public List<ColumnMeta> getColumns(TableMeta table, Connection conn)
			throws StandardException {

		if(table == null || StringUtils.isBlank(table.getName())){
			throw new StandardException("table name can not empty");
		}

		if(StringUtils.isBlank(table.getSchema())){
			table.setSchema(null);
		}

		if(StringUtils.isBlank(table.getCatalog())){
			table.setCatalog(null);
		}

		ResultSet resPks = null;
		ResultSet res = null;
		Map<String, ColumnMeta> pks = new HashMap<String, ColumnMeta>();
		List<ColumnMeta> list = new ArrayList<ColumnMeta>();

		try {
			if(conn == null){
				return list;
			}
			// 获取主键
			resPks = conn.getMetaData().getPrimaryKeys(table.getCatalog(),
					table.getSchema(), table.getName());
			while (resPks.next()) {
				ColumnMeta meta = new ColumnMeta();
				meta.setName(resPks.getString("COLUMN_NAME"));
				meta.setAlias(quotObject(meta.getName()));
				meta.setKeySeq(resPks.getInt("KEY_SEQ"));
				meta.setPkName(resPks.getString("PK_NAME"));
				meta.setPk(true);
				pks.put(meta.getName(), meta);
			}

			res = conn.getMetaData().getColumns(table.getCatalog(),
					table.getSchema(), table.getName(), "%");
			while (res.next()) {
				ColumnMeta meta = new ColumnMeta();
				meta.setName(res.getString("COLUMN_NAME"));
				if(pks.containsKey(meta.getName())){
					meta.setPk(pks.get(meta.getName()).isPk());
					meta.setKeySeq(pks.get(meta.getName()).getKeySeq());
					meta.setPkName(pks.get(meta.getName()).getPkName());
				}

				String schema	= res.getString("TABLE_SCHEM");
				String tableName= res.getString("TABLE_NAME");
				if(StringUtils.isNotBlank(schema) && !schema.equalsIgnoreCase(table.getSchema())){
					continue;
				}
				if(StringUtils.isNotBlank(tableName) && !tableName.equalsIgnoreCase(table.getName())){
					continue;
				}

				meta.setTypeName(res.getString("TYPE_NAME"));
				meta.setAlias(quotObject(meta.getName()));
				meta.setColumnSize(res.getInt("COLUMN_SIZE"));
				meta.setColumnDef(res.getString("COLUMN_DEF"));
				meta.setRemarks(res.getString("REMARKS"));
				//meta.setOrdinalPosition(res.getInt("ordinalPosition"));
				meta.setDataType(res.getInt("DATA_TYPE"));
				//meta.setIsAutoincrement(res.getString("IS_AUTOINCREMENT"));
				//meta.setIsGeneratedcolumn(res.getString("IS_GENERATEDCOLUMN"));
				meta.setIsNullable(res.getString("IS_NULLABLE"));
				meta.setDecimalDigits(res.getInt("DECIMAL_DIGITS"));
				//meta.setDecimalDigits(res.getInt("DECIMAL_DIGITS"));
				//meta.setNullable(res.getInt("nullable"));
				//meta.setNumPrecRadix(res.getInt("numPrecRadix"));
				// 兼容Oracle nchar
				if(Types.OTHER == meta.getDataType()){
					if("nchar".equalsIgnoreCase(meta.getTypeName())){
						meta.setDataType(Types.NCHAR);
					}
					// 兼容Oracle nvarchar2
					else if("nvarchar2".equalsIgnoreCase(meta.getTypeName())){
						meta.setDataType(Types.NVARCHAR);
					}
					// 兼容Oracle nclob
					else if("nclob".equalsIgnoreCase(meta.getTypeName())){
						meta.setDataType(Types.NCLOB);
					}
				}
				list.add(meta);
			}
		} catch (Exception e) {
			String err = String.format("Get columns error [tableName=%s, catalog=%s, schema=%s]", 
					table.getName(), table.getCatalog(), table.getSchema());
			throw new StandardException(err, e);

		} finally {
			close(resPks, res);
		}
		return list;

	}
	
	private Connection connect(String url, Properties pro) throws Throwable{
		if(manager!=null && manager.getMaxConnections()<=0){
			throw new TooManyConnectionException("max connections has been used up");
		}
		try {
			Connection conn = DriverManager.getConnection(url, pro);
			if(manager!=null){
				manager.useConnection();
			}
			return conn;
		} catch (Throwable ex) {
			ex = connectionException(ex);
			throw ex;
		}
	}
	
	/**
	 * 获取数据库连接, 特别地, 由此方法创建的连接必须由该connector对象的close()方法释放连接
	 * 
	 * @author PengYang
	 * @date 2016-10-26
	 * 
	 * @return Connection
	 * @throws StandardException
	 */
	public Connection connection() throws StandardException {
		if (dbMeta == null) {
			return null;
		}
		Connection conn = null;
		Properties pro 	= new Properties();
		try {
			if(!checkAvailable()){
				return null;
			}
			pro.putAll(dbMeta.getParams());
			pro.put("user", dbMeta.getUserName());
			try {
				pro.put("password", AesUtil.decryptStr(dbMeta.getPassword(),
						AesUtil.AES_SEED));
			} catch (StandardException de) {
				logger.error("", de);
				throw new StandardException("password mistake");
			}
			String driver = this.loadDriverClass(dbMeta.getAccessType());
			Class.forName(driver);
			
			String connName = dbMeta.getName();
			// 等待获取连接
			long timeout = dbMeta.getRetryTimeout();
			long times   = 0;
			long spend   = 0;
			long sleep   = 5;// 秒
			
			while(conn==null){

				if(spend>=timeout){
					throw new StandardException(connName + " reconnect timeout "+timeout);
				}
				
				try {
					
					conn = this.connect(dbMeta.getUrl(), pro);
					if(conn!=null){
						break;
					}
					
					
				} catch (TooManyConnectionException | NetException ex) {
					// 连接过多
					// 数据库  %s 连接数过多, 请稍后重试...
					if(!dbMeta.isAwait()){
						String err = "database %s too many connections, please try again later...";
						err = String.format(err, connName);
						throw new StandardException(err);
					}
					// 网络异常
					if(times>dbMeta.getRetryTimes() && ex instanceof NetException){
						String err = "database %s network error, please try again later...";
						err = String.format(err, connName);
						throw new StandardException(err);
					}
					// 网络异常
					if(ex instanceof NetException){
						logger.error(StandardException.getCauseMsg(ex));
					}
					
					String info = "DB connector {} {}, please await {} seconds retry...";
					logger.info(info, connName, (ex instanceof NetException)?"network error":"too many connections", sleep);
					
					Thread.sleep(sleep*1000L);
					sleep = RandomUtils.nextInt(10);
					sleep = sleep==0?1:sleep;
					spend += sleep;
					times++;
					
					continue;
				}

			}

		} catch (Throwable ex) {
			if(ex instanceof StandardException){
				throw (StandardException)ex;
			} 

			// "数据库连接异常, %s\n%s", dbMeta.getUrl(), e.getMessage()
			String errCode = "";
			if(ex instanceof SQLException){
				errCode = "ErrCode: "+((SQLException)ex).getErrorCode()+", ";
			}
			String err = errCode+ex.getMessage();
			err = String.format("database connection exception, %s\\n%s", dbMeta.getUrl(), err);
			StandardException de = new StandardException(err, ex);
			throw de;
		}

		return conn;
	}

	/**
	 * 合法性check
	 * 
	 * @author PengYang
	 * @date 2017-07-03
	 * 
	 * @return
	 * @throws StandardException 
	 */
	public boolean checkAvailable() throws StandardException{
		String prefix = getJDBCPrefix();
		if(!dbMeta.getUrl().startsWith(prefix)){
			throw new StandardException(String.format("invalid JDBC url format %s connect to %s", dbMeta.getUrl(), dbMeta.getCode()));
		}
		return true;
	}
	/**
	 * 获取JDBC连接串前缀
	 * 
	 * @author shersfy
	 *  
	 * @return JDBC连接串前缀
	 */
	public String getJDBCPrefix() {
		return "jdbc:"+this.dbMeta.getCode().toLowerCase();
	}

	/**
	 * 获取真实数据库实现类的实例
	 * 
	 * @author PengYang
	 * @date 2016-10-26
	 * 
	 * @param typeCode
	 * @return
	 * @throws StandardException
	 */
	public static DbConnectorInterface getInstance(DBMeta dbMeta)
			throws StandardException {

		if(dbMeta==null 
				|| StringUtils.isBlank(dbMeta.getCode())){
			return null;
		}
		DbConnectorInterface db = null;
		StringBuffer clazz = new StringBuffer(0);
		clazz.append(DbConnectorInterface.class.getPackage().getName());
		clazz.append(".");
		clazz.append(dbMeta.getCode());
		clazz.append("Connector");
		try {
			db = (DbConnectorInterface) Class.forName(clazz.toString())
					.newInstance();
			db.setDbMeta(dbMeta);
		} catch (Exception e) {
			String err = "initialize database connector exception [%s]";
			err = String.format(err,  clazz.toString());
			throw new StandardException(err, e);
		} 
		return db;
	}


	/**
	 * 查询sql语句。
	 * 
	 * @author PengYang
	 * @date 2017-05-27
	 * 
	 * @param conn 指定连接
	 * @param close 是否关闭连接
	 * @param sql 执行SQL
	 * @return GridData
	 * @throws StandardException
	 */
	public GridData executeQuery(Connection conn, String sql)
			throws StandardException {
		GridData dataSet = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if(StringUtils.isBlank(sql)){
				throw new StandardException("SQL is blank.");
			}
			if(conn == null || conn.isClosed()){
				conn = connection();
			}
			stmt = conn.createStatement();
			logger.info(sql);
			rs = stmt.executeQuery(sql);

			dataSet = packageData(conn, rs);

		} catch (Exception e) {
			logger.error(sql);
			throw new StandardException("execute sql error: "+sql, e);
		} finally {
			close(rs, stmt);
		}
		
		if(dataSet == null){
			dataSet = new GridData();
		}
		return dataSet;
	}

	/**
	 * 
	 * 执行带参数的SQL语句
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @param sql
	 * @param args 参数
	 * @return 二维数据
	 * @throws StandardException
	 */
	public GridData executeQueryByParams(Connection conn, String sql, Object...args)
			throws StandardException {
		if(args == null || args.length==0){
			return executeQuery(conn, sql);
		}

		GridData dataSet 		= null;
		PreparedStatement stmt 	= null;
		ResultSet rs 			= null;
		try {
			stmt = conn.prepareStatement(sql);

			StringBuffer params = new StringBuffer("params: ");
			for(int index = 0; index < args.length; index++){
				Object obj = args[0];

				if(obj != null){
					stmt.setObject(index+1, obj);
					params.append(obj.getClass().getSimpleName());
					params.append("(");
					params.append(obj.toString());
					params.append(")");
					params.append(", ");
				}
			}
			logger.debug(sql);
			logger.debug(params.substring(0, params.length() - ", ".length()));
			rs = stmt.executeQuery();
			dataSet = packageData(conn, rs);

		} catch (Exception e) {
			logger.error(sql);
			throw new StandardException("execute sql error: "+sql, e);
		} finally {
			close(rs, stmt);
		}

		if(dataSet == null){
			dataSet = new GridData();
		}

		return dataSet;
	}

	/**
	 * 组装数据
	 * 
	 * @author PengYang
	 * @date 2017-06-20
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws StandardException 
	 */
	public GridData packageData(Connection conn, ResultSet rs) throws SQLException{
		GridData dataSet = new GridData();
		List<ColumnMeta> headers = new ArrayList<ColumnMeta>();
		List<RowData> rows = new ArrayList<RowData>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Set<String> aliSet = new HashSet<>();
		// 输出列名
		for (int i = 1; i <= columnCount; i++) {
			ColumnMeta fm = new ColumnMeta(rsmd.getColumnName(i));
			fm.setTypeName(rsmd.getColumnTypeName(i));
			fm.setDataType(rsmd.getColumnType(i));
			fm.setColumnSize(rsmd.getColumnDisplaySize(i));
			fm.setDecimalDigits(rsmd.getScale(i));
			fm.setAlias(rsmd.getColumnLabel(i));
			fm.setRemarks(rsmd.getColumnLabel(i));
			//fm.setIsAutoincrement(String.valueOf(rsmd.isAutoIncrement(i)));
			fm.setNullable(rsmd.isNullable(i));
			fm.setOrdinalPosition(i);
			// 别名
			String alias = StringUtils.isNotBlank(fm.getAlias())?fm.getAlias():fm.getName();
			for(int k=1; k <= columnCount; k++){
				if(!aliSet.contains(alias)){
					aliSet.add(alias);
					fm.setAlias(alias);
					break;
				}
				alias = fm.getName() + k;
			}
			
			headers.add(fm);
		}
		// 输出数据
		while (rs.next()) {
			RowData dataRow = new RowData();
			List<FieldData> fields = new ArrayList<FieldData>();
			for (int i = 1; i <= columnCount; i++) {
				ColumnMeta header = headers.get(i-1);
				FieldData fd = new FieldData(getResultObject(header, rs));
				fd.setName(header.getName());
				fields.add(this.formatFieldData(conn, header, fd));
			}
			dataRow.setFields(fields);
			rows.add(dataRow);
		}
		dataSet.setHeaders(headers);
		dataSet.setRows(rows);
		return dataSet;
	}
	/**
	 * 查询统计。
	 * 
	 * @param countSql
	 *            被执行的sql语句
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * 
	 * @return DataSet
	 * @throws StandardException
	 */
	public Long queryCount(String countSql, Connection conn)
			throws StandardException {
		long cnt = 0;
		Statement stmt = null;
		ResultSet rs   = null;
		if(StringUtils.isBlank(countSql)){
			return cnt;
		}
		String sql = countSql;
		try {
			stmt = conn.createStatement();
			logger.debug(sql);
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				cnt = rs.getLong(1);
			}
		} catch (Exception e) {
			logger.error(sql);
			throw new StandardException("execute sql error: "+sql, e);
		} finally {
			close(rs, stmt);
		}
		
		return cnt;
	}

	/**
	 * 执行update sql语句
	 * 
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @param sql
	 *            被执行的SQL
	 * @return 影响行数
	 * @throws StandardException
	 */
	public int executeUpdate(Connection conn, String sql) throws StandardException {
		Statement stmt = null;
		int cnt = 0;
		try {
			stmt = conn.createStatement();
			logger.info(sql);
			cnt = stmt.executeUpdate(sql);
		} catch (Exception e) {
			logger.error(sql);
			throw new StandardException("execute sql error: "+sql, e);
		} finally {
			close(stmt);
		}
		return cnt;
	}

	/**
	 * 执行update sql语句
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @param sql
	 *            被执行的SQL
	 * @param params
	 *            参数
	 * @return 影响行数
	 * @throws StandardException
	 */
	public int executeUpdate(Connection conn, String sql, Object... params)
			throws StandardException {
		int cnt = 0;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			StringBuffer str = new StringBuffer("params: ");
			for (int index = 1; index <= params.length; index++) {
				stmt.setObject(index, params[index - 1]);
				str.append(params[index - 1].getClass().getSimpleName());
				str.append("(");
				str.append(params[index - 1].toString());
				str.append(")");
				str.append(", ");
			}
			logger.debug(sql);
			logger.debug(str.substring(0, str.length() - 2));
			cnt = stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(sql);
			throw new StandardException("execute sql error: "+sql, e);
		} finally {
			close(stmt);
		}
		
		return cnt;
	}

	public DBMeta getDbMeta() {
		return dbMeta;
	}

	public void setDbMeta(DBMeta dbMeta) throws StandardException{
		this.dbMeta = dbMeta;
	}

	/**
	 * 特别地, 如果对象为Connection实例, 只能释放由DbConnectorInterface创建的Connection实例</br>
	 * </br>
	 * 释放对象资源</br>
	 * 
	 * @param objs
	 */
	public void close(AutoCloseable ...objs){
		for(AutoCloseable obj : objs){
			if(obj!=null){
				try {
					obj.close();
					if((obj instanceof Connection) && manager!=null){
						manager.releaseConnection();
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	/***
	 * 设置公共属性
	 * 
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return DBMeta
	 */
	public DBMeta initPublicInfo(DbConnectorInterface connector, Connection conn){

		DBMeta meta = connector.getDbMeta();
		try {
			meta.setTables(connector.getTables(meta.getCatalog(), meta.getSchema(), 
					new TableType[]{TableType.TABLE}, conn));
			meta.setViews(connector.getTables(meta.getCatalog(), meta.getSchema(), 
					new TableType[]{TableType.VIEW}, conn));
			meta.setStoredProces(getStoredProces(conn));
			meta.setFunctions(getFunctonsByUserDef(conn));

		} catch (StandardException e) {
			logger.error(e.getSuperMessage());
		}
		return meta;
	}
	/**
	 * 统计分页页数，返回页数
	 * 
	 * @param sql 统计countSql
	 * @param pageSize 分页大小
	 * @param conn 数据库连接
	 * @param isClose  是否关闭连接
	 * @return long
	 * @throws StandardException
	 */
	public long countPages(String countSql, int pageSize, Connection conn) throws StandardException{
		long totalSize = queryCount(countSql, conn);
		return this.countPages(totalSize, pageSize);
	}

	/**
	 * 计算页数
	 * 
	 * @author PengYang
	 * @date 2017-05-04
	 * 
	 * @return long
	 * @throws StandardException
	 */
	public long countPages(long totalSize, long pageSize) throws StandardException{
		if(totalSize%pageSize==0){
			totalSize = totalSize/pageSize;
		}
		else{
			totalSize = totalSize/pageSize+1;
		}
		return totalSize;
	}


	/**
	 * 获取查询SQL中的表名或子查询及where条件<br/>
	 * 返回值 String[0]:表名或子查询<br/>
	 * 返回值 String[1]:where条件<br/>
	 * 
	 * @author PengYang
	 * @date 2017-04-07
	 * 
	 * @param baseSql 查询SQL
	 * @param replaceAlias 如果条件使用了别名，用replaceAlias替换
	 * @return String[] 
	 */
	@Deprecated
	public String[] getNameAndWhere(String baseSql, String replaceAlias){

		String[] nameAndWhere = new String[]{"", ""};
		if(StringUtils.isBlank(baseSql)){
			return nameAndWhere;
		}

		String tmp = baseSql.toUpperCase();
		String fromStr 	= " FROM ";
		String whereStr = " WHERE ";
		int fromIndex = tmp.indexOf(fromStr);
		int whereIndex = tmp.lastIndexOf(whereStr);
		
		String name 	= "";
		String alias 	= "";
		String where 	= "";

		if(fromIndex == -1){
			return nameAndWhere;
		}
		
		name =  baseSql.substring(fromIndex+fromStr.length()).trim();
		
		if(whereIndex == -1){
			name =  baseSql.substring(fromIndex+fromStr.length());
		}
		else{
			// where 保证是主查询的条件
			int index = name.indexOf(")");
			if(index!=-1 && whereIndex<index){
				where = "";
				name =  baseSql.substring(fromIndex+fromStr.length());
			}
			else{
				name =  baseSql.substring(fromIndex+fromStr.length(), whereIndex);
				where = baseSql.substring(whereIndex+whereStr.length()).trim();
			}
		}
		int nameIndex = tmp.indexOf(name)+name.length();

		name = name.trim();
		String arr[] = name.split(" ");
		// 没有别名
		if(arr.length == 1){
			name = arr[0].trim();
		}
		// 有别名
		else if(arr.length == 2){
			name  = arr[0].trim();
			alias = arr[1].trim();
		}
		// SQL语句, 有别名
		else if(arr.length > 2 && !name.endsWith(")")){
			alias = arr[arr.length-1].trim();
			name  = name.substring(0, name.length()-alias.length()).trim();
		}

		if(where.length()>0){
			where = where.replace(alias+".", replaceAlias+".");
		}
		// 去掉括号
		if(name.startsWith("(") && name.endsWith(")")){
			name = name.substring(1, name.length()-1);
		}

		//  where在表名之后
		if(whereIndex == -1 || whereIndex<nameIndex){
			where = "";
		}

		nameAndWhere[0] = name;
		nameAndWhere[1] = where;

		return nameAndWhere;
	}

	/**
	 * 格式化要写入表的字段值
	 * 
	 * @author LinXibiao
	 * @date 2017-09-08
	 * 
	 * @param column
	 * @param data
	 * @return
	 * @throws SQLException 
	 * @throws StandardException 
	 */
	public FieldData formatWrittenFieldData(Connection conn, ColumnMeta column, FieldData field) throws SQLException{

		if(field == null){
			return null;
		}
		switch (column.getDataType()) {
		case Types.BLOB:
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			field.setType(Byte.class.getName());
			if("RAW".equalsIgnoreCase(column.getTypeName())  // Oracle RAW类型
					|| "HIERARCHYID".equalsIgnoreCase(column.getTypeName())){ // Sql server hierarchyid类型
				byte[] bytes = (byte[])field.getValue();
				field.setValue(new String(bytes));
				break;
			}
			
			InputStream input 			 = null;
			ByteArrayOutputStream output = null;

			try {
				StringBuffer content = new StringBuffer(0);
				if (field.getValue() instanceof Blob) {

					Blob blob = (Blob) field.getValue();
					input = blob.getBinaryStream();

					if (input != null) {

						byte[] data = null;
						byte[] bytes = new byte[1024];
						int len = input.read(bytes);
						output = new ByteArrayOutputStream();
						while (len != -1) {
							output.write(bytes);
							len = input.read(bytes);
						}

						data = output.toByteArray();
						field.setValue(data);
						field.setType(Byte[].class.getName());
						break;
					}
				} 
				else if (field.getValue() instanceof InputStream) {
					input = (InputStream) field.getValue();
					if (input != null) {

						byte[] bytes = new byte[1024];
						int len = input.read(bytes);
						while (len != -1) {
							content.append(new String(bytes, 0, len));
							len = input.read(bytes);
						}
						field.setValue(content);
						field.setType(Byte.class.getName());
					}
					break;
				} 
				else if (field.getValue() instanceof byte[]) {
					byte[] bytes = (byte[]) field.getValue();
					field.setValue(new String(bytes, 0, bytes.length));
					break;
				}
				
			} catch (Throwable e) {
				logger.error(e.getMessage());
			} finally {
				if (input != null) {
					IOUtils.closeQuietly(input);
				}
				if (output != null) {
					IOUtils.closeQuietly(output);
				}
			}
			break;
		case Types.CLOB:
		case Types.NCLOB:
			// CLOB
			if(field.getValue()  instanceof Clob){
				Clob clob = (Clob)field.getValue();
				if(clob!=null){
					Reader reader = null;
					try {
						reader = clob.getCharacterStream();

						StringBuffer content 	= new StringBuffer(0);
						char[] cbuf 			= new char[1024]; 
						int len 				= reader.read(cbuf);

						while(len!=-1){
							for(int i=0; i<len; i++){
								content.append(cbuf[i]!=0 ?cbuf[i] :"");
							}
							len = reader.read(cbuf);
						}
						field.setValue(content);
					} catch (Throwable e) {
						logger.error(e.getMessage());
					} finally{
						IOUtils.closeQuietly(reader);
					}
				}
			}
			break;
		case Types.DOUBLE:
			// DOUBLE
			if(field.getValue()  instanceof Double){
				Double dbl = (Double)field.getValue();
				if(Double.POSITIVE_INFINITY == dbl){
					dbl = Double.MAX_VALUE;
				}
				else if(Double.NEGATIVE_INFINITY == dbl){
					dbl = -1*Double.MAX_VALUE;
				}
				field.setValue(dbl);
			}
			break;
		case Types.TIMESTAMP:
		case Types.TIMESTAMP_WITH_TIMEZONE:
			//TIMESTAMP
			if(field.getValue() instanceof TIMESTAMP){
				TIMESTAMP ts = (TIMESTAMP)field.getValue();
				field.setValue(DateUtil.format(ts.dateValue(), Const.FORMAT_TIMESTAMP));
			}
			else if(field.getValue()  instanceof Date){
				field.setValue(DateUtil.format((Date) field.getValue(), Const.FORMAT_TIMESTAMP));
				field.setFormat(Const.FORMAT_TIMESTAMP);
			}
			break;
		default:
			break;
		}
		return field;
	}
	
	/**
	 * 格式化字段值
	 * 
	 * @author PengYang
	 * @date 2017-06-20
	 * 
	 * @param column
	 * @param data
	 * @return
	 * @throws SQLException 
	 * @throws StandardException 
	 */
	public FieldData formatFieldData(Connection conn, ColumnMeta column, FieldData field) 
			throws SQLException {

		if(field == null){
			return null;
		}
		switch (column.getDataType()) {
		case Types.BLOB:
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			field.setType(Byte.class.getName());
			// 兼容Oracle RAW类型
			// 兼容Sql server hierarchyid类型
			if("RAW".equalsIgnoreCase(column.getTypeName()) 
					|| "hierarchyid".equalsIgnoreCase(column.getTypeName())){ 
				byte[] bytes = (byte[])field.getValue();
				field.setValue(bytes == null?null:new String(bytes));
			} else{
				field.setValue("");
			}
			break;
		case Types.CLOB:
		case Types.NCLOB:
			// CLOB
			if(field.getValue()  instanceof Clob){
				Clob clob = (Clob)field.getValue();
				if(clob!=null){
					Reader reader = null;
					try {
						reader = clob.getCharacterStream();

						StringBuffer content 	= new StringBuffer(0);
						char[] cbuf 			= new char[1024]; 
						int len 				= reader.read(cbuf);

						while(len!=-1){
							for(int i=0; i<len; i++){
								content.append(cbuf[i]!=0 ?cbuf[i] :"");
							}
							len = reader.read(cbuf);
						}
						field.setValue(content);
					} catch (Throwable e) {
						logger.error(e.getMessage());
					} finally{
						IOUtils.closeQuietly(reader);
					}
				}
			}
			break;
		case Types.DOUBLE:
			// DOUBLE
			if(field.getValue()  instanceof Double){
				Double dbl = (Double)field.getValue();
				if(Double.POSITIVE_INFINITY == dbl){
					dbl = Double.MAX_VALUE;
				}
				else if(Double.NEGATIVE_INFINITY == dbl){
					dbl = -1*Double.MAX_VALUE;
				}
				field.setValue(dbl);
			}
			break;
		case Types.TIMESTAMP:
		case Types.TIMESTAMP_WITH_TIMEZONE:
			// TIMESTAMP
			if(field.getValue()  instanceof Date){
				field.setValue(DateUtil.format((Date)field.getValue(), Const.FORMAT_TIMESTAMP));
				field.setFormat(Const.FORMAT_TIMESTAMP);
			}
			break;
		default:
			break;
		}
		return field;
	}
	
	/**
	 * 从结果集获取指定字段的值
	 * 
	 * @param col 字段
	 * @param result 结果集
	 * @return 字段值
	 * @throws SQLException
	 */
	public Object getResultObject(ColumnMeta col, ResultSet result) throws SQLException{
		Object obj = null;
		switch (col.getDataType()) {
		case Types.BIT:
			obj = result.getObject(col.getOrdinalPosition());
			// 兼容postgresql bool类型
			if(obj != null && !"bool".equalsIgnoreCase(col.getTypeName())){
				obj = result.getInt(col.getOrdinalPosition());
			}
			break;
		case Types.SQLXML:
			obj = result.getString(col.getOrdinalPosition());
			break;
		default:
			obj = result.getObject(col.getOrdinalPosition());
			break;
		}
		return obj;
	}
	

	/**
	 * 获取数据仓库的位置
	 * 
	 * @param conn 连接
	 * @param dbName 数据库名称
	 * @param shortPath true短路径, false 全路径
	 * @return 返回数据库位置
	 * @throws StandardException 
	 */
	public String getDBWarehouseLocation(Connection conn, String dbName, boolean shortPath) throws StandardException{
		return "";
	}
	
	/**
	 * 连接字段名, 逗号", "间隔
	 * 
	 * @author PengYang
	 * @date 2017-06-27
	 * 
	 * @param columns 为null或长度为0返回"*"
	 * @return String
	 */
	public String concatFieldName(List<ColumnMeta> columns){
		StringBuffer all = new StringBuffer("*");
		String colSep = ", ";
		if(columns == null || columns.isEmpty()){
			return all.toString();
		}
		all.setLength(0);
		for(ColumnMeta col :columns){
			all.append(quotObject(col.getName())).append(colSep);
		}

		return all.substring(0, all.length()-colSep.length());
	}

	/**
	 * 连接异常判定
	 * 
	 * @param ex 异常
	 * @return 返回判定后的异常
	 */
	public Throwable connectionException(Throwable ex){
		if(ex == null){
			ex = new Exception("Exception is null.");
		}
		return ex;
	}
	
	/**baseSql是SQL加括号，是表不加括号**/
	public void appendBaseSql(StringBuffer sql, String baseSql){
		if(baseSql.trim().contains(" ")){
			sql.append(" FROM (");
			sql.append(baseSql);
			sql.append(") ");
		} else{
			sql.append(" FROM ");
			sql.append(baseSql);
			sql.append(" ");
		}
	}

	/**
	 * 解析URL得到DB连接信息
	 * 
	 * @param url 连接url
	 * @return DB连接信息
	 * @throws StandardException
	 */
	public static DBMeta getMetaByUrl(String url) throws StandardException{
		if(StringUtils.isBlank(url)){
			return null;
		}
		String err = "url format error: %s";
		if(!url.contains("//")||!url.substring(url.indexOf("//")+2).contains("/")||!url.contains(":")){
			throw new StandardException(String.format(err, url));
		}
		
		String arr[]  = null;
		int index = url.indexOf("//");
		index = index+2;
		
		String host  = url.substring(index, url.indexOf("/", index));
		arr = host.split(",")[0].trim().split(":");
		if(arr.length <2){
			throw new StandardException(String.format(err, url));
		}
		Integer port = Integer.valueOf(arr[1]);
		
		arr = host.split(",");
		if(arr.length == 1){
			host = arr[0].trim().split(":")[0];
		}
		
		arr = url.split("[;|?]");
		String tmp    = arr[0].trim();
		String dbName = tmp.substring(tmp.lastIndexOf("/")+1);
		
		DBMeta meta = new DBMeta();
		meta.setHost(host.trim());
		meta.setPort(port);
		meta.setDbName(dbName.trim());
		meta.setUrl(url);
		
		// 参数
		Properties params = new Properties();
		for(String str : arr){
			if(str.contains("/") || !str.contains("=")){
				continue;
			}
			str = str.trim();
			String key   = str.split("=")[0].trim();
			String value = str.split("=")[1].trim();
			params.setProperty(key, value);
		}
		meta.setParams(params);
		
		return meta;
	}
	
	/**
	 * 转化成数据库指定的日期函数，默认直接字符串比较，如
	 * oralce-->TO_DATE, TO_TIMESTAMP
	 * mysql -->可用 >. =, <直接比较
	 * 
	 * @param formatDateStr 日期字符串
	 * @return 功能函数
	 */
	public String getDateFunction(String formatDateStr){
		return "'" + formatDateStr + "'";
	}
	
	/**
	 * 获取同步插入的sql，实现表中有数据就更新，无则插入
	 * 
	 * @author LinXibiao
	 * @date 2017-11-16
	 *
	 * @param cols 待插入的列信息
	 * @param fullTableName 表全名
	 * @return  update inset sql
	 */
	public String getUpdateInsert(List<ColumnMeta> cols, String fullTableName) throws StandardException{
		return null;
	}
	
	/**
	 * 处理PreparedStatement对象, 支持游标式读取数据
	 * 
	 * @param pstmt
	 * @throws SQLException 
	 */
	public void prepareStatementByCursor(PreparedStatement pstmt) throws SQLException{
		// nothing
	}

	public ConnectorManager getManager() {
		return manager;
	}

	public void setManager(ConnectorManager manager) {
		this.manager = manager;
	}

}
