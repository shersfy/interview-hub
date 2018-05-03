package org.shersfy.jwatcher.connector;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.shersfy.jwatcher.service.SystemInfoService;

public class JVMConnector {

	private String url;
	/**更新时间 连接超时判定用**/
	private long updatetime;
	private JMXConnector jmxConnector;
	
	private JVMConnector(){
		if(SystemInfoService.conf!=null){
			this.jmxConnector = SystemInfoService.conf.getLocalDefault();
		}
	}
	
	/**
	 * 获取JMX连接器
	 * @param url 连接串
	 * @return JVMConnector
	 * @throws IOException 
	 */
	public synchronized static JVMConnector getConnector(String url) throws IOException{
		JVMConnector obj = null;
		if(StringUtils.isNotBlank(url) && SystemInfoService.conf!=null){
			obj = SystemInfoService.conf.getCache().get(url);
		}
		if(obj!=null){
			return obj;
		}
		
		obj = new JVMConnector();
		obj.url = url;
		if(StringUtils.isBlank(url) || url.startsWith("localhost")){
			obj.jmxConnector = obj.jmxConnector==null?new JMXLocalConnector():obj.jmxConnector;
		}
		
		obj.jmxConnector.connect();
		obj.updatetime();
		if(SystemInfoService.conf!=null){
			SystemInfoService.conf.getCache().put(url, obj);
		}
		
		return obj;
	}
	
	
	public void close(){
		IOUtils.closeQuietly(jmxConnector);
		if(SystemInfoService.conf!=null){
			SystemInfoService.conf.getCache().remove(url);
		}
	}
	
	/**
	 * 是否是本地连接
	 * @return
	 */
	public boolean isLocal(){
		return jmxConnector instanceof JMXLocalConnector;
	}

	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public long getUpdatetime() {
		return updatetime;
	}

	public long updatetime() {
		this.updatetime = System.currentTimeMillis();
		return this.updatetime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
