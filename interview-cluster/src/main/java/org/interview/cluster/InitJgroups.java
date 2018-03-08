package org.interview.cluster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.interview.utils.CharsetUtil;
import org.interview.utils.HostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitJgroups {
	
	private static final Logger LOGGER 	= LoggerFactory.getLogger(InitJgroups.class);

	/**
	 * 初始化jgroups配置
	 * 
	 * @author shersfy
	 * @date 2018-03-06
	 * 
	 * @param jgroupsUseDefault 是否使用默认配置，true不修改配置，false修改配置
	 * @param nodes 集群节点IP，逗号分隔
	 * @param jgroupsPort jgroup端口号
	 */
	public static void initClusterConfig(String jgroupsConf, String nodes, int jgroupsPort) {
		// 使用默认
		if(StringUtils.isBlank(jgroupsConf)){
			return;
		}
		String bind_addr 	 = "bind_addr=\"";
		String bind_port 	 = "bind_port=\"";
		String initial_hosts = "initial_hosts=\"";
		String initialHosts  = initialHosts(nodes, jgroupsPort);

		BufferedReader reader = null;
		OutputStream output   = null;
		try {

			StringBuffer content = new StringBuffer(0);
			String path = InitJgroups.class.getClassLoader().getResource(jgroupsConf).getPath();

			FileInputStream input = new FileInputStream(path);
			reader = new BufferedReader(new InputStreamReader(input, CharsetUtil.getUTF8()));
			while(reader.ready()){
				String line = reader.readLine();
				if(line == null){
					continue;
				}

				boolean contains = false;
				String desstr = null;
				String search = null;

				if(line.contains(bind_addr)){
					contains = true;
					search = bind_addr;
					desstr = HostUtil.IP;
				}
				else if(line.contains(bind_port)){
					contains = true;
					search = bind_port;
					desstr = String.valueOf(jgroupsPort);

				}
				else if(line.contains(initial_hosts)){
					contains = true;
					search = initial_hosts;
					desstr = initialHosts;
				}

				if(contains){
					int start = line.indexOf(search)+search.length();
					int end   = line.length()-1;
					String substr = line.substring(start, end);
					line = line.replace(substr, desstr);
				}

				content.append(line).append("\n");
			}
			output = new FileOutputStream(path, false);
			IOUtils.write(content.toString(), output);

		} catch (Exception ex) {
			LOGGER.error("", ex);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(output);
		}
	}

	public static String initialHosts(String zknodes, int jgroupsPort){
		String initialHosts = "${jgroups.tcpping.initial_hosts:%s}";
		// jgroups.tcpping.initial_hosts:localhost[7800]
		// demo10.leap.com:2181,demo8.leap.com:2181,demo9.leap.com:2181
		if(StringUtils.isNotBlank(zknodes)){
			String format = "%s[%s]";
			String sep 	  = ",";

			StringBuffer buffer = new StringBuffer(0);
			String[] hosts = zknodes.split(sep);
			for(String host :hosts){
				buffer.append(String.format(format, host.split(":")[0].trim(), jgroupsPort));
				buffer.append(sep);
			}

			if(buffer.toString().endsWith(sep)){
				initialHosts = String.format(initialHosts, buffer.substring(0, buffer.length()-sep.length()));
			}
		}
		return initialHosts;
	}
}
