package org.shersfy.jwatcher.connector;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.shersfy.jwatcher.entity.JVMMemoSegment;
import org.shersfy.jwatcher.entity.JVMMemoUsage;
import org.shersfy.jwatcher.service.SystemInfoService;

public class JVMConnector {

	/**jmx连接串**/
	private String url;
	/**启用监听**/
	private AtomicBoolean enable;
	/**监听最大Segments缓存数**/
	private AtomicInteger maxSegments;
	/**监听时间间隔(毫秒)**/
	private AtomicLong interval;
	/**更新时间 连接超时判定用**/
	private AtomicLong updatetime;
	/**jmx连接**/
	private JMXConnector jmxConnector;
	/**监听jvm不同时刻的内存数据**/
	private LinkedList<JVMMemoSegment> memoSegments;
	
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
		} else {
			JMXServiceURL serviceURL = new JMXServiceURL(url);
			obj.jmxConnector = JMXConnectorFactory.connect(serviceURL);
		}
		
		obj.jmxConnector.connect();
		obj.updatetime();
		if(SystemInfoService.conf!=null){
			SystemInfoService.conf.getCache().put(url, obj);
		}
		
		obj.setEnable(new AtomicBoolean(false));
		obj.setInterval(new AtomicLong(5000));
		obj.setMaxSegments(new AtomicInteger(0));
		obj.memoSegments = (LinkedList<JVMMemoSegment>) Collections.synchronizedList(new LinkedList<JVMMemoSegment>());
		
		return obj;
	}
	
	public MemoryMXBean getMemoryMXBean() throws IOException {
		MemoryMXBean memoBean = null;
		if(isLocal()){
			memoBean = ManagementFactory.getMemoryMXBean();
		} else {
			MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
			memoBean = ManagementFactory.getPlatformMXBean(conn, MemoryMXBean.class);
		}
		return memoBean;
	}
	
	public List<MemoryPoolMXBean> getMemoryPoolMXBeans() throws IOException{
		List<MemoryPoolMXBean> poolBeans = null;
		if(isLocal()){
			poolBeans = ManagementFactory.getMemoryPoolMXBeans();
		} else {
			MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
			poolBeans = ManagementFactory.getPlatformMXBeans(conn, MemoryPoolMXBean.class);
		}
		return poolBeans;
	}
	
	public OperatingSystemMXBean getOperatingSystemMXBean() throws IOException{
		OperatingSystemMXBean osBean = null;
		if(isLocal()){
			osBean = ManagementFactory.getOperatingSystemMXBean();
		} else {
			MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
			osBean = ManagementFactory.getPlatformMXBean(conn, OperatingSystemMXBean.class);
		}
		return osBean;
	}
	
	public ThreadMXBean getThreadMXBean() throws IOException{
		ThreadMXBean threadBean = null;
		if(isLocal()){
			threadBean = ManagementFactory.getThreadMXBean();
		} else {
			MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
			threadBean = ManagementFactory.getPlatformMXBean(conn, ThreadMXBean.class);
		}
		return threadBean;
	}
	
	public synchronized void startWatcher(WatcherCallback callback) throws IOException{
		if(this.enable.get()){
			return;
		}
		if(SystemInfoService.conf==null 
				|| SystemInfoService.conf.getJvmWatcherThreadsPool()==null){
			throw new IOException("configuration instance's JvmWatcherThreadsPool is null");
		}
		
		ExecutorService pool = SystemInfoService.conf.getJvmWatcherThreadsPool();
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				while(getEnable().get()){
					JVMMemoSegment segment = new JVMMemoSegment();
					try {
						MemoryMXBean memoBean = getMemoryMXBean();
						MemoryUsage heap = memoBean.getHeapMemoryUsage();
						MemoryUsage non  = memoBean.getNonHeapMemoryUsage();
						
						segment.getHeapPools().add(new JVMMemoUsage("heap", heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
						segment.getNonHeapPools().add(new JVMMemoUsage("non-heap", non.getInit(), non.getUsed(), non.getCommitted(), non.getMax()));
						
						List<MemoryPoolMXBean> poolBeans = getMemoryPoolMXBeans();
						poolBeans.forEach(bean->{
							if(isHeap(bean.getName())){
								segment.getHeapPools().add(new JVMMemoUsage(bean.getName(), heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
							} else {
								segment.getNonHeapPools().add(new JVMMemoUsage(bean.getName(), heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
							}
						});
						
						if(getMemoSegments().size()>=getMaxSegments().get()){
							getMemoSegments().poll();
						}
						getMemoSegments().push(segment);
					} catch (Throwable e) {
						JVMConnector.this.stopWatcher();
						callback.setException(e);
					} finally {
						callback.callbackWatchMemo(url, segment);
					}
				}
			}
			
		});
	}
	
	public synchronized void stopWatcher(){
		this.close();
	}
	
	
	public void close(){
		this.enable.set(false);
		if(SystemInfoService.conf!=null && SystemInfoService.conf.getCache().containsKey(url)){
			SystemInfoService.conf.getCache().remove(url);
		}
		IOUtils.closeQuietly(jmxConnector);
	}
	
	public boolean isHeap(String name){
		if(StringUtils.containsIgnoreCase(name, "Eden")){
			return true;
		}
		if(StringUtils.containsIgnoreCase(name, "Survivor")){
			return true;
		}
		if(StringUtils.containsIgnoreCase(name, "Old Gen")
				|| StringUtils.containsIgnoreCase(name, "Tenured Gen")){
			return true;
		}
		return false;
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
		return updatetime.get();
	}

	public long updatetime() {
		this.updatetime = updatetime==null?new AtomicLong(0):updatetime;
		this.updatetime.set(System.currentTimeMillis());
		return this.updatetime.get();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LinkedList<JVMMemoSegment> getMemoSegments() {
		return memoSegments;
	}

	public void setMemoSegments(LinkedList<JVMMemoSegment> memoSegments) {
		this.memoSegments = memoSegments;
	}

	public AtomicBoolean getEnable() {
		return enable;
	}

	public void setEnable(AtomicBoolean enable) {
		this.enable = enable;
	}

	public AtomicLong getInterval() {
		return interval;
	}

	public void setInterval(AtomicLong interval) {
		this.interval = interval;
	}

	public AtomicInteger getMaxSegments() {
		return maxSegments;
	}

	public void setMaxSegments(AtomicInteger maxSegments) {
		this.maxSegments = maxSegments;
	}

}
