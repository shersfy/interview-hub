package org.interview.connector.lock;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import org.interview.beans.DBMeta;
import org.interview.beans.TableMeta;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.exception.StandardException;
import org.interview.utils.AesUtil;
import org.junit.Test;

public class APP {

	public static final String DDL = "CREATE TABLE test(id int PRIMARY KEY, name varchar(255), age int)";

	public static void main(String[] args) throws StandardException, SQLException, InterruptedException {
		/**
		 *  共享锁(S)
		 *  又称读锁，若事务T对数据对象A加上S锁，则事务T可以读A但不能修改A
		 *  其他事务只能再对A加S锁，而不能加X锁，直到T释放A上的S锁。
		 *  这保证了其他事务可以读A，但在T释放A上的S锁之前不能对A做任何修改。
		 *  
		 *  排他锁(X)
		 *  又称写锁。若事务T对数据对象A加上X锁，事务T可以读A也可以修改A
		 *  其他事务不能再对A加任何锁，直到T释放A上的锁。
		 *  这保证了其他事务在T释放A上的锁之前不能再读取和修改A。
		 */
		APP app = new APP();
		DbConnectorInterface connector = app.getConnector();
		Connection conn = connector.connection();
		app.start(connector, conn);
		connector.close(conn);
		
		// 以下case以mysql为例
		// 1. 共享锁 记录锁 lock in share mode
		// 2. 排他锁 记录锁 for update
		// 3. 共享锁 表锁 lock table xxx read  /unlock tables 释放被当前线程持有的任何锁
		// 4. 排他锁 表锁 lock table xxx write /unlock tables 释放被当前线程持有的任何锁
		
	}
	
	/**
	 * case1: 事务A获得表test的排它锁, 事务2不能再获得排他锁, 直到事务A commit
	 * 
	 * @author shersfy
	 * @throws StandardException 
	 * @throws InterruptedException 
	 * @date 2018-03-13
	 *
	 */
	@Test
	public void case01() throws StandardException, InterruptedException {
		DbConnectorInterface connector = getConnector();
		Connection conn1 = connector.connection();
		Connection conn2 = connector.connection();
		
		String slock1 = "select * from test where id = 5 for update"; // 排他
		String slock2 = "select * from test where id = 5 for update"; // 排他
		
		CountDownLatch latch = new CountDownLatch(2);
		new MultiThread("Thread_A", latch, conn1, slock1, true).start();
		new MultiThread("Thread_B", latch, conn2, slock2, true).start();
		
		latch.await();
		connector.close(conn1);
		connector.close(conn2);
	}
	
	/**
	 * case2: 事务A获得表test的排它锁, 事务2不能再获得共享锁, 直到事务A commit
	 * 
	 * @author shersfy
	 * @throws StandardException 
	 * @throws InterruptedException 
	 * @date 2018-03-13
	 *
	 */
	@Test
	public void case02() throws StandardException, InterruptedException {
		DbConnectorInterface connector = getConnector();
		Connection conn1 = connector.connection();
		Connection conn2 = connector.connection();
		
		String slock1 = "select * from test where id = 5 for update"; // 排他
		String slock2 = "select * from test where id = 5 lock in share mode"; // 共享
		
		CountDownLatch latch = new CountDownLatch(2);
		new MultiThread("Thread_A", latch, conn1, slock1, true).start();
		Thread.sleep(1000);
		new MultiThread("Thread_B", latch, conn2, slock2, true).start();
		
		latch.await();
		connector.close(conn1);
		connector.close(conn2);
	}
	
	/**
	 * case3: 事务A获得表test的共享锁, 事务2不能再获得排他锁, 直到事务A commit
	 * 
	 * @author shersfy
	 * @throws StandardException 
	 * @throws InterruptedException 
	 * @date 2018-03-13
	 *
	 */
	@Test
	public void case03() throws StandardException, InterruptedException {
		DbConnectorInterface connector = getConnector();
		Connection conn1 = connector.connection();
		Connection conn2 = connector.connection();
		
		String slock1 = "select * from test where id = 5 lock in share mode"; // 共享
		String slock2 = "select * from test where id = 5 for update"; // 排他
		
		CountDownLatch latch = new CountDownLatch(2);
		new MultiThread("Thread_A", latch, conn1, slock1, true).start();
		Thread.sleep(1000);
		new MultiThread("Thread_B", latch, conn2, slock2, true).start();
		
		latch.await();
		connector.close(conn1);
		connector.close(conn2);
	}

	/**
	 * case4: 事务A获得表test的共享锁, 事务2可以获得共享锁
	 * 
	 * @author shersfy
	 * @throws StandardException 
	 * @throws InterruptedException 
	 * @date 2018-03-13
	 *
	 */
	@Test
	public void case04() throws StandardException, InterruptedException {
		DbConnectorInterface connector = getConnector();
		Connection conn1 = connector.connection();
		Connection conn2 = connector.connection();
		
		String slock1 = "select * from test where id = 5 lock in share mode"; // 共享
		String slock2 = "select * from test where id = 5 lock in share mode"; // 共享
		
		CountDownLatch latch = new CountDownLatch(2);
		new MultiThread("Thread_A", latch, conn1, slock1, true).start();
		new MultiThread("Thread_B", latch, conn2, slock2, true).start();
		
		latch.await();
		connector.close(conn1);
		connector.close(conn2);
	}
	
	public void start(DbConnectorInterface connector, Connection conn) throws StandardException, SQLException {
		if(!connector.exist(new TableMeta(null, null, "test"), conn)) {
			connector.createTable(DDL, conn);
		}
		insert(conn);
		connector.close(conn);
	}

	public DbConnectorInterface getConnector() throws StandardException {
		DBMeta db = new DBMeta();
		db.setCode("MySQL");
		db.setHost("localhost");
		db.setPort(3306);
		db.setDbName("test");
		db.setUserName("root");
		db.setPassword(AesUtil.encryptStr("lenovo", AesUtil.AES_SEED));
		return DbConnectorInterface.getInstance(db);
	}

	public void insert(Connection conn) throws SQLException, StandardException {
		String sql = "insert into test values (%s, '%s', %s)";
		Statement pst = conn.createStatement();

		pst.addBatch("delete from test");
		for(int id=1; id<=5; id++) {
			System.out.println(String.format(sql, id, "name"+id, 20+id));
			pst.addBatch(String.format(sql, id, "name"+id, 20+id));
		}
		pst.executeBatch();
		pst.close();
	}

}
