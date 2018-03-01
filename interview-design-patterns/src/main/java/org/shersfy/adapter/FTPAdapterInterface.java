package org.shersfy.adapter;

import java.util.List;

import org.interview.exception.StandardException;

public interface FTPAdapterInterface {
	
	public void connect(String host, int port, String user, String password) throws StandardException;

	public List<String> listFiles(String path);
}
