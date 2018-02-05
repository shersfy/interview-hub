package org.interview.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * 自动检测编码格式工具类
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class CharsetUtil {
	private static Logger logger = LoggerFactory.getLogger(CharsetUtil.class);
	/**
	 * 通过URL,检测编码格式
	 * 
	 * @author PengYang
	 * @date 2016-12-13
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Charset detectCode(String fileName){
		Charset cs = getUTF8();
		InputStream in = null;
		try {
			CodepageDetectorProxy pro = CodepageDetectorProxy.getInstance();
		    //JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码 .
			pro.add(JChardetFacade.getInstance());
			// 字节顺序标记探测编码
			pro.add(new ByteOrderMarkDetector());
			// ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于指示是否显示探测过程的详细信息，为false不显示。 
			pro.add(new ParsingDetector(false));
			// ASCIIDetector用于ASCII编码测定  
			pro.add(ASCIIDetector.getInstance());
			// UnicodeDetector用于Unicode家族编码的测定  
			pro.add(UnicodeDetector.getInstance());
			in = new BufferedInputStream(new FileInputStream(fileName));
			cs = pro.detectCodepage(in, 1024);
			if("x-EUC-TW".equalsIgnoreCase(cs.name())){
				cs = Charset.forName("GBK");
			}
			if ("void".equals(cs.name())) {
				cs = CharsetUtil.getUTF8();
			}
		} catch (Exception e) {
			logger.info("Detect code Exception, set Charset default UTF-8.");
			logger.info(e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return cs;
	}
	
	public static Charset getUTF8(){
		return Charset.forName("UTF-8");
	}
	
	public static Charset forName(String name){
		return Charset.forName(name);
	}

}
