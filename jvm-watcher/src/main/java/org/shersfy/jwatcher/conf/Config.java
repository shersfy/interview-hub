package org.shersfy.jwatcher.conf;


import javax.management.remote.JMXConnector;

import org.hyperic.sigar.Sigar;
import org.shersfy.jwatcher.entity.BaseEntity;
import org.shersfy.jwatcher.service.JMXLocalConnector;

public class Config extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Sigar sigar;
	private JMXConnector localDefault;
	
	public Config(){
		super();
		localDefault = new JMXLocalConnector();
	}

	public Sigar getSigar() {
		return sigar;
	}


	public JMXConnector getLocalDefault() {
		return localDefault;
	}

	public void setSigar(Sigar sigar) {
		this.sigar = sigar;
	}

	public void setLocalDefault(JMXConnector localDefault) {
		this.localDefault = localDefault;
	}

}
