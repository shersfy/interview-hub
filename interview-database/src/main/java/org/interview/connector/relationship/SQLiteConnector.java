package org.interview.connector.relationship;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.interview.beans.ColumnMeta;
import org.interview.beans.DBAccessType;
import org.interview.beans.DBMeta;
import org.interview.beans.TableMeta;
import org.interview.beans.TableType;
import org.interview.exception.StandardException;

public class SQLiteConnector extends DbConnectorInterface {

	@Override
	public String loadDriverClass(DBAccessType type) {

		String driver = "org.sqlite.JDBC";
		switch (type) {
		case ODBC:
			break;
		case OCI:
			break;
		case JNDI:
			break;
		default:
			break;
		}

		return driver;
	}

	@Override
	public String queryByPage(String baseSql, long pageNo,
			long pageSize) throws StandardException {
		return null;
	}

	@Override
	public List<DBMeta> getDatabases(Connection conn) throws StandardException {
		List<DBMeta> dbs = new ArrayList<DBMeta>();
		ResultSet rs = null;
		try {
			if(conn == null){
				throw new SQLException("connection is null");
			}
			rs = conn.getMetaData().getSchemas();
			while(rs.next()){
				DBMeta meta = new DBMeta();
				BeanUtils.copyProperties(meta, getDbMeta());
				meta.setName(rs.getString("TABLE_SCHEM"));
				meta.setSchema(rs.getString("TABLE_SCHEM"));
				meta.setDbName(meta.getName());
				dbs.add(meta);
			}

		} catch (Exception e) {
			throw new StandardException("show databases error", e);
		} finally {
			close(rs);
		}
		return dbs;
	}

	@Override
	public void setDbMeta(DBMeta dbMeta) {
		
		dbMeta.setSchema(dbMeta.getDbName());
		if(StringUtils.isBlank(dbMeta.getUrl())){
			dbMeta.setUrl(String.format("jdbc:sqlite:%s", dbMeta.getDbName()));
		}
		this.dbMeta = dbMeta;
	}

	@Override
	public List<TableMeta> getTables(String catalog, String schema, TableType[] types, 
			Connection conn) throws StandardException {
		if(StringUtils.isNotBlank(catalog)){
			getDbMeta().setDbName(catalog);
		}
		return super.getTables(catalog, schema, types, conn);
	}

	@Override
	public String showCreateTable(TableMeta table, Connection conn) throws StandardException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String javaTypeToDbType(int javaType, ColumnMeta column) throws StandardException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCutPointSql(TableMeta table, ColumnMeta partColumn, String where, long totalSize, int blockCnt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
