package org.interview.beans;

import org.apache.commons.lang.StringUtils;

/**
 * HDFS元信息
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class HdfsMeta extends BaseMeta{
	
	/** 认证类型 **/
	private int authType;
	/** 连接url **/
	private String url;
    /** Hadoop配置core-site.xml文件路径 **/
    private String coreSiteXml;
    /** Hadoop配置hdfs-site.xml文件路径 **/
    private String hdfsSiteXml;
    /** HDFS用户名 **/
    private String userName;
    /** HDFS用代理用户 **/
    private String appUser;
    /** 主机名称 **/
    private String host;
    /** 端口号 **/
    private Integer port;
    /** keytab文件路径**/
    private String keytab;
    /** Kerberos(KRB5) 认证**/
    private String krb5Conf;
    /** 用户域 **/
    private String principal;
    
	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

	public HdfsMeta(){
    }

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getUrl() {
		return url;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAppUser() {
		return appUser;
	}

	public void setAppUser(String appUser) {
		this.appUser = StringUtils.isBlank(appUser)?this.userName:appUser;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeytab() {
		return keytab;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setKeytab(String keytab) {
		this.keytab = keytab;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getCoreSiteXml() {
		return coreSiteXml;
	}

	public String getHdfsSiteXml() {
		return hdfsSiteXml;
	}

	public void setCoreSiteXml(String coreSiteXml) {
		this.coreSiteXml = coreSiteXml;
	}

	public void setHdfsSiteXml(String hdfsSiteXml) {
		this.hdfsSiteXml = hdfsSiteXml;
	}

	public final String getKrb5Conf() {
		return krb5Conf;
	}

	public final void setKrb5Conf(String krb5Conf) {
		this.krb5Conf = krb5Conf;
	}


}