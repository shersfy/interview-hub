package org.shersfy.jwatcher.service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.entity.CPUInfo;
import org.shersfy.jwatcher.entity.Config;
import org.shersfy.jwatcher.entity.DiskInfo;
import org.shersfy.jwatcher.entity.JVMInfo;
import org.shersfy.jwatcher.entity.JVMProcess;
import org.shersfy.jwatcher.entity.Memory;
import org.shersfy.jwatcher.entity.SystemInfo;
import org.shersfy.jwatcher.utils.FileUtil;
import org.springframework.stereotype.Component;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

@Component
public class SystemInfoService extends BaseService{

	public static Config config;
	
	@PostConstruct
	private void init(){
		config = new Config("localhost");
	}

	public SystemInfo getSystemInfo(){

		SystemInfo info = new SystemInfo();
		Result res = getJmxConnector(config.getJmxRmiUri());
		if(!isJMXLocalConnector() && res.getCode()==SUCESS){
			JMXConnector connector = (JMXConnector) res.getModel();
			try {
				MBeanServerConnection conn = connector.getMBeanServerConnection();
				// Server OS Core
				String url = config.getJmxRmiUri();
				String str = "jndi/";
				if(url.contains(str)){
					url = url.substring(url.indexOf(str)+str.length());
					url = url.replace("rmi://", "http://");
				}
				URL jmxUrl = new URL(url);
				OperatingSystemMXBean mxOS = ManagementFactory.getPlatformMXBean(conn, OperatingSystemMXBean.class);
				info.setOs(String.format("%s %s %s", 
						mxOS.getName(),
						mxOS.getVersion(),
						mxOS.getArch()));
				info.setName(mxOS.getName());
				info.setHost(jmxUrl.getHost());
				info.setIp(jmxUrl.getHost());
				
				// CPU
				info.setCpu(String.valueOf(mxOS.getAvailableProcessors()));
				
				// memo
				MemoryMXBean mxMemo = ManagementFactory.getPlatformMXBean(conn, MemoryMXBean.class);
				long init = mxMemo.getHeapMemoryUsage().getInit() + mxMemo.getNonHeapMemoryUsage().getInit();
				long max  = mxMemo.getHeapMemoryUsage().getMax() + mxMemo.getNonHeapMemoryUsage().getCommitted();
				info.setRam(FileUtil.getLengthWithUnit(max>init?max:init));
			} catch (Exception e) {
				LOGGER.error("", e);
			} finally {
				IOUtils.closeQuietly(connector);
			}
			return info;
		}
		Map<String, String> env = System.getenv();
		Properties props = System.getProperties();
		OperatingSystem os = OperatingSystem.getInstance();
		Sigar sigar = new Sigar();

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOGGER.error("", e);
		}

		// Address
		info.setName(env.getOrDefault("COMPUTERNAME", ""));
		info.setDomain(env.getOrDefault("USERDOMAIN", ""));

		if(addr!=null){
			info.setHost(addr.getHostName());
			info.setIp(addr.getHostAddress());
		}

		// OS
		String sep = " ";
		StringBuffer osTmp = new StringBuffer(0);
		osTmp.append(props.getProperty("os.name")).append(sep);
		osTmp.append(os.getArch()).append(sep);
		osTmp.append(os.getDataModel()).append(sep);
		osTmp.append(os.getVersion());
		info.setOs(osTmp.toString());

		// CPU
		CpuInfo[] cpus = null;
		try {
			cpus = sigar.getCpuInfoList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}

		if(cpus!=null&&cpus.length!=0){
			info.setCpu(String.format("%s %s", cpus[0].getVendor(), cpus[0].getModel()));
		}

		// RAM
		Mem memo = null;
		try {
			memo = sigar.getMem();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		long ram = memo==null?0:memo.getRam() * 1024 * 1024;
		info.setRam(FileUtil.getLengthWithUnit(ram));

		// disk
		FileSystem fslist[] = null;
		File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		try {
			fslist = sigar.getFileSystemList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}

		if(fslist!=null){
			for(FileSystem fs :fslist){
				File root = new File(fs.getDirName());
				FileSystemUsage usage = null;
				try {
					if(fs.getType()!=FileSystem.TYPE_CDROM){
						usage = sigar.getFileSystemUsage(fs.getDirName());
					}
				} catch (SigarException e) {
					LOGGER.error("", e);
				}
				DiskInfo disk = new DiskInfo();
				String name = fsv.getSystemDisplayName(root);
				disk.setName(StringUtils.isBlank(name)?root.getPath():name);
				disk.setType(fsv.getSystemTypeDescription(root));
				disk.setFileSystem(fs.getSysTypeName());
				disk.setTotal(usage==null?0:usage.getTotal()*1024);
				disk.setUsed(usage==null?0:usage.getUsed()*1024);
				disk.setUsedPercent(usage==null?"0%":String.format("%.0f%%", usage.getUsePercent()*100));
				
				info.getDisks().add(disk);
			}
		}
		
//		FileSystemView fs = FileSystemView.getFileSystemView();
//		File[] roots = File.listRoots();
//		for(File root :roots){
//			DiskInfo disk = new DiskInfo();
//			disk.setName(fs.getSystemDisplayName(root));
//			disk.setType(fs.getSystemTypeDescription(root));
//			disk.setFileSystem(fileSystem);
//			disk.setTotal(root.getTotalSpace());
//			disk.setUsed(root.getUsableSpace());
//
//			info.getDisks().add(disk);
//		}

		return info;
	}
	
	public JVMInfo getJvmInfo(){
		
		JVMInfo jvm = new JVMInfo();
		Properties props = System.getProperties();
		MemoryMXBean mx  = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mx.getHeapMemoryUsage();
		MemoryUsage nonheap = mx.getNonHeapMemoryUsage();
		
		jvm.setProcessors(Runtime.getRuntime().availableProcessors());
		
		Result res = getJmxConnector(config.getJmxRmiUri());
		if(!isJMXLocalConnector() && res.getCode()==SUCESS){
			JMXConnector connector = (JMXConnector) res.getModel();
			try {
				MBeanServerConnection conn = connector.getMBeanServerConnection();
				RuntimeMXBean remoteRt = ManagementFactory.getPlatformMXBean(conn, RuntimeMXBean.class);
				props.clear();
				props.putAll(remoteRt.getSystemProperties());
				
				MemoryMXBean remoteMemo = ManagementFactory.getPlatformMXBean(conn, MemoryMXBean.class);
				heap = remoteMemo.getHeapMemoryUsage();
				nonheap = remoteMemo.getNonHeapMemoryUsage();
				
				OperatingSystemMXBean remoteOS = ManagementFactory.getPlatformMXBean(conn, OperatingSystemMXBean.class);
				jvm.setProcessors(remoteOS.getAvailableProcessors());
				
			} catch (Exception e) {
				LOGGER.error("", e);
				return new JVMInfo();
			} finally {
				IOUtils.closeQuietly(connector);
			}
		}
		
		jvm.setJavaVersion(String.format("%s Build %s", 
				props.getProperty("java.version"),
				props.getProperty("java.vm.version")));
		jvm.setJavaVendor(props.getProperty("java.vendor"));
		jvm.setJavaVendorUrl(props.getProperty("java.vendor.url"));
		jvm.setJavaHome(props.getProperty("java.home"));
		jvm.setJavaHome(props.getProperty("java.home"));
		
		jvm.setInitMemory(heap.getInit()+nonheap.getInit());
		jvm.setMaxMemory(heap.getMax()+nonheap.getCommitted());
		jvm.setUsedMemory(heap.getUsed()+nonheap.getUsed());
		
		jvm.setTotalMemory(jvm.getMaxMemory());
		jvm.setFreeMemory(jvm.getTotalMemory()- jvm.getUsedMemory());
		if(jvm.getMaxMemory()<jvm.getInitMemory()){
			jvm.setMaxMemory(jvm.getInitMemory());
		}
		
		
		return jvm;
	}
	
	public List<JVMProcess> getLocalJvmProcesses(){
		
		Sigar sigar = new Sigar();
		List<JVMProcess> list = new ArrayList<>();
		try {
			MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
			// 取得所有在活动的虚拟机集合
			Set<Integer> pids = local.activeVms();
			// 遍历PID和进程名
			for(Integer pid : pids){
				MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + pid));
				String mainClass = MonitoredVmUtil.mainClass(vm, true);
				JVMProcess p = new JVMProcess();
				p.setPid(pid);
				p.setName(mainClass);
				p.setExePath(sigar.getProcExe(pid).getName());
				list.add(p);
			}

		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return list;
	}
	
	public Memory getMemory(){
		Sigar sigar = new Sigar();
		Memory memo = new Memory();
		// RAM
		Mem mem = null;
		try {
			mem = sigar.getMem();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		long ram = mem==null?0:mem.getRam() * 1024 * 1024;
		memo.setTotal(ram);
		memo.setUsed(mem.getUsed());
		memo.setUsedPercent((double)mem.getUsed()/mem.getTotal()*100);
		
		return memo;
	}
	
	public CPUInfo getCpuInfo(){
		Sigar sigar = new Sigar();
		CPUInfo cpu = new CPUInfo();
		CpuPerc[] cpArr  = null;
		CpuInfo[] cpuArr = null;
		try {
			cpuArr = sigar.getCpuInfoList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}

		try {
			cpArr = sigar.getCpuPercList();
			cpuArr = sigar.getCpuInfoList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		if(cpArr!=null){
			double user = 0;
			double sys  = 0;
			double wait = 0;
			double nice = 0;
			double used = 0;
			double idle = 0;
			for(CpuPerc c :cpArr){
				user += c.getUser();
				sys  += c.getSys();
				wait += c.getWait();
				nice += c.getNice();
				used += c.getCombined();
				idle += c.getIdle();
			}
			cpu.setUser(user/cpArr.length);
			cpu.setSystem(sys/cpArr.length);
			cpu.setWait(wait/cpArr.length);
			cpu.setNice(nice/cpArr.length);
			cpu.setUsed(used/cpArr.length);
			cpu.setIdle(idle/cpArr.length);
			
		}
		
		if(cpuArr!=null&&cpuArr.length!=0){
			cpu.setName(String.format("%s %s", cpuArr[0].getVendor(), cpuArr[0].getModel()));
		}
		
		return cpu;
	}
	
	/**
	 * 创建JMX RMI连接
	 * 
	 * @param jmxRmiUri 连接串
	 * @return 返回 model: code=200, 返回MBeanServerConnection 对象或null(本地时)
	 */
	public Result getJmxConnector(String jmxRmiUri){
		Result res = new Result();
		if(StringUtils.isBlank(jmxRmiUri) || "localhost".equalsIgnoreCase(jmxRmiUri)){
			res.setModel(new JMXLocalConnector());
			return res;
		}
		
		try {
			
			JMXServiceURL serviceURL = new JMXServiceURL(jmxRmiUri);
			JMXConnector connector   = JMXConnectorFactory.connect(serviceURL);
			res.setModel(connector);
			
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		
		return res;
	}
	
	public boolean isJMXLocalConnector(){
		return "localhost".equalsIgnoreCase(config.getJmxRmiUri());
	}

}
