package org.shersfy.jwatcher.service;

import java.io.File;
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
import javax.swing.filechooser.FileSystemView;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
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

	private Sigar sigar;
	private JVMInfo jvm;
	
	@PostConstruct
	private void init(){
		sigar = new Sigar();
		jvm   = new JVMInfo();
	}

	public SystemInfo getSystemInfo(){

		Map<String, String> env = System.getenv();
		Properties props = System.getProperties();
		OperatingSystem os = OperatingSystem.getInstance();
		if(sigar ==null){
			sigar = new Sigar();
		}

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOGGER.error("", e);
		}

		// Address
		SystemInfo info = new SystemInfo();
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
					usage = sigar.getFileSystemUsage(fs.getDirName());
				} catch (SigarException e) {
					LOGGER.error("", e);
				}
				DiskInfo disk = new DiskInfo();
				disk.setName(fsv.getSystemDisplayName(root));
				disk.setType(fsv.getSystemTypeDescription(root));
				disk.setFileSystem(fs.getSysTypeName());
				if(usage!=null){
					disk.setTotal(usage.getTotal()*1024);
					disk.setUsed(usage.getUsed()*1024);
					disk.setUsedPercent(String.format("%.0f%%", usage.getUsePercent()*100));
				}
				
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
		Runtime runtime  = Runtime.getRuntime();
		
		jvm.setJavaVersion(String.format("%s Build %s", 
				props.getProperty("java.version"),
				props.getProperty("java.vm.version")));
		jvm.setJavaVendor(props.getProperty("java.vendor"));
		jvm.setJavaVendorUrl(props.getProperty("java.vendor.url"));
		jvm.setJavaHome(props.getProperty("java.home"));
		jvm.setJavaHome(props.getProperty("java.home"));
		
		jvm.setInitMemory(heap.getInit());
		jvm.setMaxMemory(heap.getMax());
		jvm.setUsedMemory(heap.getUsed());
		
		jvm.setTotalMemory(runtime.totalMemory());
		jvm.setFreeMemory(runtime.freeMemory());
		jvm.setProcessors(runtime.availableProcessors());
		
		return this.jvm;
	}
	
	public List<JVMProcess> getLocalJvmProcesses(){
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

}
