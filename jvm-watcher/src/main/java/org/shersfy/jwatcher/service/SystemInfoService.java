package org.shersfy.jwatcher.service;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.shersfy.jwatcher.conf.Config;
import org.shersfy.jwatcher.entity.CPUInfo;
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

	public static Config conf;
	
	@PostConstruct
	private void init(){
		LOGGER.info("=========init starting===========");
		conf = new Config();
		conf.setSigar(new Sigar());
		LOGGER.info("=========init finished===========");
	}

	public SystemInfo getSystemInfo(){

		SystemInfo info = new SystemInfo();
		Map<String, String> env = System.getenv();
		Properties props = System.getProperties();
		OperatingSystem osBean = OperatingSystem.getInstance();
		Sigar sigar = conf.getSigar();

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
		String os = String.format("%s %s %s %s", 
				props.getProperty("os.name"),
				osBean.getArch(),
				osBean.getDataModel(),
				osBean.getVersion());
		
		info.setOs(os);

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
			info.getDisks().clear();
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
		
		Properties props = System.getProperties();
		MemoryMXBean mx  = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mx.getHeapMemoryUsage();
		MemoryUsage nonheap = mx.getNonHeapMemoryUsage();
		
		JVMInfo jvm = new JVMInfo();
		
		jvm.setProcessors(Runtime.getRuntime().availableProcessors());
		
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
		
		Sigar sigar = conf.getSigar();
		List<JVMProcess> list = new ArrayList<>();
		list.clear();
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
	
	public JVMProcess findByPid(long pid){
		JVMProcess process = new JVMProcess();
		
		return process;
	} 
	
	public Memory getMemory(){
		Sigar sigar = conf.getSigar();
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
		memo.setUsedPercent((double)mem.getUsed()/mem.getTotal());
		
		return memo;
	}
	
	public CPUInfo getCpuInfo(){
		Sigar sigar = conf.getSigar();
		CPUInfo cpu = new CPUInfo();
		
		CpuInfo[] cpuInfos = null;
		CpuPerc[] cpuPercs = null;
		try {
			cpuInfos = sigar.getCpuInfoList();
			cpuPercs = sigar.getCpuPercList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		if(cpuPercs!=null){
			double user = 0;
			double sys  = 0;
			double wait = 0;
			double nice = 0;
			double used = 0;
			double idle = 0;
			for(CpuPerc perc :cpuPercs){
				user += perc.getUser();
				sys  += perc.getSys();
				wait += perc.getWait();
				nice += perc.getNice();
				used += perc.getCombined();
				idle += perc.getIdle();
			}
			
			int size = cpuPercs.length;
			cpu.setUser(user/size);
			cpu.setSystem(sys/size);
			cpu.setWait(wait/size);
			cpu.setNice(nice/size);
			cpu.setUsed(used/size);
			cpu.setIdle(idle/size);
			
		}
		
		if(cpuInfos!=null&&cpuInfos.length!=0){
			cpu.setName(String.format("%s %s", cpuInfos[0].getVendor(), cpuInfos[0].getModel()));
		}
		
		return cpu;
	}
	
	/**
	 * 创建JMX RMI连接
	 * 
	 * @param jmxRmiUri 连接串
	 * @return 返回 model: code=200, 返回MBeanServerConnection 对象或null(本地时)
	 * @throws IOException 
	 */
	public JMXConnector getJmxConnector(String jmxRmiUri) throws IOException{

		if(StringUtils.isBlank(jmxRmiUri) || "localhost".equalsIgnoreCase(jmxRmiUri)){
			return conf.getLocalDefault();
		}

		JMXServiceURL serviceURL = new JMXServiceURL(jmxRmiUri);
		JMXConnector connector   = JMXConnectorFactory.connect(serviceURL);

		return connector;
	}
	
}
