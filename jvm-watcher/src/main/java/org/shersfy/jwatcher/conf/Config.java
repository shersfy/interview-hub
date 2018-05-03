package org.shersfy.jwatcher.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.management.remote.JMXConnector;

import org.hyperic.sigar.Sigar;
import org.shersfy.jwatcher.entity.BaseEntity;
import org.shersfy.jwatcher.entity.JVMProcess;
import org.shersfy.jwatcher.service.JMXLocalConnector;

public class Config extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Sigar sigar;
	private List<JVMProcess> localJvmProcesses;
	private JMXConnector localDefault;
	
	public Config(){
		super();
		localDefault = new JMXLocalConnector();
		localJvmProcesses = Collections.synchronizedList(new ArrayList<>());
	}

	public Sigar getSigar() {
		return sigar;
	}

	public List<JVMProcess> getLocalJvmProcesses() {
		return localJvmProcesses;
	}

	public JMXConnector getLocalDefault() {
		return localDefault;
	}

	public void setSigar(Sigar sigar) {
		this.sigar = sigar;
	}

	public void setLocalJvmProcesses(List<JVMProcess> localJvmProcesses) {
		this.localJvmProcesses = localJvmProcesses;
	}

	public void setLocalDefault(JMXConnector localDefault) {
		this.localDefault = localDefault;
	}

}
