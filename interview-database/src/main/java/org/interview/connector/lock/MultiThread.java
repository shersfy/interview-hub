package org.interview.connector.lock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThread extends Thread {

	protected static final Logger LOGGER  = LoggerFactory.getLogger(MultiThread.class);
	
	private CountDownLatch latch;
	private Connection conn;
	private String lockSql;
	private boolean query;
	
	public MultiThread(String name, CountDownLatch latch, Connection conn, String lockSql, boolean query) {
		super.setName(name);
		this.latch = latch;
		this.conn = conn;
		this.lockSql = lockSql;
		this.query = query;
	}
	

	@Override
	public void run() {
		try {
			if(query) {
				executeQuery();
			} else {
				executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		latch.countDown();
	}


	private void executeQuery() throws InterruptedException, SQLException {
		conn.setAutoCommit(false);
		Statement st = conn.createStatement();
		
		LOGGER.info("thead {} query transaction start...", this.getName());
		LOGGER.info("thead {} query :{}", this.getName(), lockSql);
		ResultSet rs = st.executeQuery(lockSql);
		int cnt = rs.getMetaData().getColumnCount();
		StringBuffer line = new StringBuffer(0);
		while(rs.next()) {
			line.setLength(0);
			for(int i=1; i<=cnt; i++) {
				line.append(rs.getString(i)).append("\t");
			}
			LOGGER.info("thead {} result: {}", this.getName(), line);
		}
		sleep(5000);
		conn.commit();
		st.close();
		LOGGER.info("thead {} query transaction commited", this.getName());
	}
	
	private void executeUpdate() throws InterruptedException, SQLException {
		conn.setAutoCommit(false);
		Statement st = conn.createStatement();
		
		LOGGER.info("thead {} update transaction start...", this.getName());
		LOGGER.info("thead {} update :{}", this.getName(), lockSql);
		st.executeUpdate(lockSql);
		sleep(5000);
		conn.commit();
		st.close();
		LOGGER.info("thead {} update transaction commited", this.getName());
	}
	
	
	
}
