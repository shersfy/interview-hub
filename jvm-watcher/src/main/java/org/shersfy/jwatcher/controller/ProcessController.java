package org.shersfy.jwatcher.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.connector.JMXLocalConnector;
import org.shersfy.jwatcher.connector.JVMConnector;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/process")
public class ProcessController extends BaseController {
	
	@Resource
	private SystemInfoService systemInfoService;
	
	@RequestMapping("/local/open/{pid}")
	@ResponseBody
	public ModelAndView openLocalConnector(@PathVariable("pid") long pid){
		ModelAndView mv = new ModelAndView("watcher");
		String url = JMXLocalConnector.getLocalUrl(pid);
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			systemInfoService.startWatcher(connector);
			mv.addObject("pid", pid);
			mv.addObject("url", url);
			mv.addObject("connector", connector);
		} catch (IOException e) {
			mv.setViewName("redirect:/error");
			mv.addObject("status", FAIL);
			mv.addObject("error", e.getMessage());
			mv.addObject("message", e.getMessage());
		}
		return mv;
	}
	
	@RequestMapping("/local/close/{pid}")
	@ResponseBody
	public Result closeLocalConnector(@PathVariable("pid") long pid){
		
		Result res = new Result();
		String url = JMXLocalConnector.getLocalUrl(pid);
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			systemInfoService.stopWatcher(connector);
			res.setModel(url);
		} catch (IOException e) {
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@RequestMapping("/local/data")
	@ResponseBody
	public Result getLocalData(long pid){
		
		Result res = new Result();
		String url = JMXLocalConnector.getLocalUrl(pid);
		try {
			res.setModel(systemInfoService.getData(url));
		} catch (IOException e) {
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		return res;
	}

	@RequestMapping("/remote/open")
	@ResponseBody
	public ModelAndView getRemoteConnector(String url){
		ModelAndView mv = new ModelAndView("watcher");
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			systemInfoService.startWatcher(connector);
			mv.addObject("connector", connector);
		} catch (IOException e) {
			mv.setViewName("redirect:/error");
			mv.addObject("status", FAIL);
			mv.addObject("error", e.getMessage());
			mv.addObject("message", e.getMessage());
		}
		return mv;
	}
}
