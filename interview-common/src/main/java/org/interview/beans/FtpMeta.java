package org.interview.beans;

public class FtpMeta extends BaseMeta {
	/** 主机名称 **/
	private String host;
	/** 端口号 **/
	private Integer port;
    /** 用户名 **/
    private String userName;
    /** 密码 **/
    private String password;
    /** 协议类型 **/
    private FTPProtocolType protocolType;

    public FtpMeta() {
		super();
		this.protocolType = FTPProtocolType.FTP;
	}
    
    public FtpMeta(String host, Integer port, String userName, String password) {
    	this();
    	this.host = host;
    	this.port = port;
    	this.userName = userName;
    	this.password = password;
    }

	public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

	public FTPProtocolType getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(FTPProtocolType protocolType) {
		this.protocolType = protocolType;
	}

}