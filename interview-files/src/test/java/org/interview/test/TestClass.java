package org.interview.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.interview.files.WriteData2File;
import org.junit.Test;

public class TestClass {
	
	@Test
	public void test01() throws IOException {
		String text = "1, 张三, 2018-03-01 12:20:10\n2, 李四, 2017-03-01 12:20:10";
		File file = new File("D:\\data\\txt\\WriteTextAndStream2File.txt");
		InputStream input = getInputStream();
		WriteData2File wd = new WriteData2File();
		wd.writeTextAndStream2File(text, input, file);
		IOUtils.closeQuietly(input);
	}

	private InputStream getInputStream() {
		InputStream input = null;
		Connection conn   = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "select * from \"DATAHUB\".\"input_stream\" where \"id\"=1";
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.100.124.207:1521:ora11", "datahub", "datahub");
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			String type = rs.getMetaData().getColumnTypeName(2);
			while(rs.next()) {
				input = rs.getBinaryStream(2);
				if("RAW".equalsIgnoreCase(type) 
						|| "hierarchyid".equalsIgnoreCase(type)){ 
//					byte[] bytes = (byte[])rs.getObject(2);
//					String content = bytes == null?null:new String(bytes);
//					input = IOUtils.toInputStream(content, "utf8");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn!=null) {
				try {
					rs.close();
					pst.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return input;
	}

}
