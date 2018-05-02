package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

import javax.management.remote.JMXConnector;

import org.hyperic.sigar.Sigar;
import org.shersfy.jwatcher.service.JMXLocalConnector;

public class Config extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String jmxRmiUri;
	private Sigar sigar;
	private SystemInfo systemInfo;
	private Memory ramMemo;
	private CPUInfo cpu;
	private JVMInfo jvm;
	private List<JVMProcess> localJvmProcesses;
	private JMXConnector localDefault;
	
	public Config(){
		super();
		localDefault = new JMXLocalConnector();
		localJvmProcesses = new ArrayList<>();
	}
	
	public Config(String jmxRmiUri) {
		this();
		this.jmxRmiUri = jmxRmiUri;
	}

	public String getJmxRmiUri() {
		return jmxRmiUri;
	}

	public Sigar getSigar() {
		return sigar;
	}

	public SystemInfo getSystemInfo() {
		return systemInfo;
	}

	public JVMInfo getJvm() {
		return jvm;
	}

	public Memory getRamMemo() {
		return ramMemo;
	}

	public CPUInfo getCpu() {
		return cpu;
	}

	public List<JVMProcess> getLocalJvmProcesses() {
		return localJvmProcesses;
	}

	public JMXConnector getLocalDefault() {
		return localDefault;
	}

	public void setJmxRmiUri(String jmxRmiUri) {
		this.jmxRmiUri = jmxRmiUri;
	}

	public void setSigar(Sigar sigar) {
		this.sigar = sigar;
	}

	public void setSystemInfo(SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}

	public void setJvm(JVMInfo jvm) {
		this.jvm = jvm;
	}

	public void setRamMemo(Memory ramMemo) {
		this.ramMemo = ramMemo;
	}

	public void setCpu(CPUInfo cpu) {
		this.cpu = cpu;
	}

	public void setLocalJvmProcesses(List<JVMProcess> localJvmProcesses) {
		this.localJvmProcesses = localJvmProcesses;
	}

	public void setLocalDefault(JMXConnector localDefault) {
		this.localDefault = localDefault;
	}

}
