package org.interview.connector;

import java.sql.Connection;
import java.util.List;

import org.interview.beans.DBMeta;
import org.interview.beans.GridData;
import org.interview.beans.RowData;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.utils.AesUtil;

import junit.framework.TestCase;

public class DBConnectorTest extends TestCase{

	
	private DbConnectorInterface connector = null;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("before method");
		DBMeta db = new DBMeta();
		db.setCode("MySQL");
		db.setHost("192.168.100.136");
		db.setPort(3306);
		db.setDbName("datahub");
		db.setUserName("root");
		db.setPassword(AesUtil.encryptStr("lenovo", AesUtil.AES_SEED));
		connector = DbConnectorInterface.getInstance(db);
	}

	public void test01(){
		Connection conn = null;
		try {
			conn = connector.connection();
			List<DBMeta> dbs = connector.getDatabases(conn);
			for(DBMeta db:dbs) {
				System.out.println(db.getDbName());
			}
			
			String sql = "select * from db_type";
			GridData data = connector.executeQuery(conn, sql);
			System.out.println("========data==========");
			for(RowData row: data.getRows()) {
				System.out.println(row.getFields());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connector.close(conn);
		}
		
		
	}
	
	public void test02() {
		int cnt = DbConnectorInterface.countPartitionBlockCnt(1000000, 20000, 256);
		System.out.println(cnt);
	}
	

}
