package org.shersfy.composite.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * 美国服务器
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class USServer implements IServer{
	
	private String token;
	private String[] tokens;
	
	public USServer() {
		this.tokens = new String[] {"dl24nfew3", "hkjjugf35", "sh82fhfsf", "cd2321ds", "bjfewoffe"};
	}
	public USServer(String token) {
		this();
		this.token  = token;
	}
	
	@Override
	public String accessResource() {
		List<String> list = Arrays.asList(tokens);
		if(!list.contains(token)) {
			System.out.println("permission denied");
			return "";
		}
		
		StringBuffer content  = new StringBuffer(0);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("logo.txt")));
			while(reader.ready()) {
				content.append(reader.readLine()).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		
		return content.toString();
	}

}
