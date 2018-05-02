package org.shersfy.jwatcher.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.management.remote.JMXConnector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/watch")
public class JWatcherController extends BaseController{
	
	@Resource
	SystemInfoService systemInfoService;
	
	
	@RequestMapping("/capacity")
	@ResponseBody
	public Result getMemoInfo(){
		Result res = new Result();
		ModelMap model = new ModelMap();
		model.put("memo", systemInfoService.getMemory());
		model.put("cpu", systemInfoService.getCpuInfo());
		res.setModel(model);
		return res;
	}
	
	@RequestMapping("/config")
	@ResponseBody
	public Result config(String jmxRmiUri){
		jmxRmiUri = StringUtils.isBlank(jmxRmiUri)?"localhost":jmxRmiUri;
		
		Result res = new Result();
		JMXConnector connector = null;
		try {
			connector = systemInfoService.getJmxConnector(jmxRmiUri);
		} catch (IOException e) {
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
			return res;
		} finally {
			IOUtils.closeQuietly(connector);
		}
		
		SystemInfoService.conf.setJmxRmiUri(jmxRmiUri);
		res.setModel(SystemInfoService.conf);
		return res;
	}

}
