package org.shersfy.jwatcher.service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

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
import org.shersfy.jwatcher.entity.Memory;
import org.shersfy.jwatcher.entity.SystemInfo;
import org.shersfy.jwatcher.utils.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class SystemInfoService extends BaseService{

	private Sigar sigar;
	
	@PostConstruct
	private void init(){
		sigar = new Sigar();
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
		memo.setUsedPercent(mem.getUsedPercent());
		
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
			double free = 0;
			for(CpuPerc c :cpArr){
				user += c.getUser();
				sys  += c.getSys();
				wait += c.getWait();
				nice += c.getNice();
				used += c.getCombined();
				free += c.getIdle();
			}
			cpu.setUser(user/cpArr.length);
			cpu.setSystem(sys/cpArr.length);
			cpu.setWait(wait/cpArr.length);
			cpu.setNice(nice/cpArr.length);
			cpu.setUsed(used/cpArr.length);
			cpu.setFree(free/cpArr.length);
			
		}
		
		if(cpuArr!=null&&cpuArr.length!=0){
			cpu.setName(String.format("%s %s", cpuArr[0].getVendor(), cpuArr[0].getModel()));
		}
		
		return cpu;
	}

}
