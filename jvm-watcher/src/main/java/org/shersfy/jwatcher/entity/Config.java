package org.shersfy.jwatcher.entity;

public class Config extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Config(){}
	
	public Config(String jmxRmiUri) {
		super();
		this.jmxRmiUri = jmxRmiUri;
	}

	private String jmxRmiUri;

	public String getJmxRmiUri() {
		return jmxRmiUri;
	}

	public void setJmxRmiUri(String jmxRmiUri) {
		this.jmxRmiUri = jmxRmiUri;
	}

}
