package org.interview.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当前jvm运行环境机器信息
 * @author shersfy
 * @date 2018-03-06
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class HostUtil {

	public static final String MAC;
	public static final String IP;
	public static final String HOSTNAME;
	private static final Logger LOGGER = LoggerFactory.getLogger(HostUtil.class);

	static {
		String mac;
		String ip;
		String hostname;
		try {
			mac = getMacAddress();
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
			hostname = addr.getHostName();
		} catch (UnknownHostException e) {
			LOGGER.error("Can't find out address: " + e.getMessage());
			mac = "UNKNOWN";
			ip = "UNKNOWN";
			hostname = "UNKNOWN";
		} catch (Exception e) {
			LOGGER.error("Can't find out address: " + e.getMessage());
			mac = "UNKNOWN";
			ip = "UNKNOWN";
			hostname = "UNKNOWN";
		}
		if (ip.equals("127.0.0.1") || ip.equals("::1") || ip.equals("UNKNOWN")) {
			try {
				Process process = Runtime.getRuntime().exec("hostname -i");
				if (process.waitFor() == 0) {
					ip = new String(IOUtils.toByteArray(process.getInputStream()), "UTF8");
				}
				process = Runtime.getRuntime().exec("hostname");
				if (process.waitFor() == 0) {
					hostname = (new String(IOUtils.toByteArray(process.getInputStream()), "UTF8")).trim();
				}
			} catch (Exception e) {
				LOGGER.warn("get hostname failed {}", e.getMessage());
			}
		}
		MAC = mac;
		IP = ip;
		HOSTNAME = hostname;
	}
	
	private static String getMacAddress() throws Exception {
        Enumeration<NetworkInterface> objs = NetworkInterface.getNetworkInterfaces();
        while(objs.hasMoreElements()){
        	NetworkInterface obj = objs.nextElement();
        	if(obj == null){
        		continue;
        	}
        	byte[] bytes = obj.getHardwareAddress();
        	if(bytes == null || bytes.length != 6){
        		continue;
        	}
        	StringBuffer mac = new StringBuffer();  
        	for(byte b:bytes){
        		//与11110000作按位与运算以便读取当前字节高4位 
        		mac.append(Integer.toHexString((b&240)>>4));
        		//与00001111作按位与运算以便读取当前字节低4位
        		mac.append(Integer.toHexString(b&15));
        		mac.append(":");
        	}
        	if(mac.length()!=0){
        		String macAddr = mac.substring(0, mac.length()-1).toUpperCase();
        		LOGGER.debug("name={}, mac={}", obj.getDisplayName(), macAddr);
        		return macAddr;
        	}
        }
        throw new Exception("unkown mac");
    }  
}
