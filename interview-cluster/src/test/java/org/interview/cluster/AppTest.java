package org.interview.cluster;

import org.interview.jgroups.action.DeleteFileAction;
import org.interview.jgroups.cluster.ClusterMessage;
import org.interview.jgroups.cluster.ClusterServer;
import org.junit.Test;


public class AppTest {

	@Test
	public void testApp() throws Exception {
		
		ClusterServer cluster   = ClusterServer.getInstance(null);
		String[] pathes = new String[] {"D:\\data\\bigFiles\\test2.sql.ok"};
		cluster.sendMessage(new ClusterMessage(new DeleteFileAction(pathes)));
	}
	
	public static void main(String[] args) {
		ClusterServer.getInstance(null);
	}
}
