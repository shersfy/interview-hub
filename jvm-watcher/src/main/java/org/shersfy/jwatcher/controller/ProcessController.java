package org.shersfy.jwatcher.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.shersfy.jwatcher.connector.JMXLocalConnector;
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
	SystemInfoService systemInfoService;
	
	@RequestMapping("/local/{pid}")
	@ResponseBody
	public ModelAndView getLocalConnector(@PathVariable("pid") long pid){
		ModelAndView mv = new ModelAndView("pwatcher");
		String url = JMXLocalConnector.getLocalUrl(pid);
		try {
			mv.addObject("connector", systemInfoService.getConnector(url));
		} catch (IOException e) {
			mv.setViewName("/error");
			mv.addObject("msg", e.getMessage());
		}
		return mv;
	}

	@RequestMapping("/remote")
	@ResponseBody
	public ModelAndView getRemoteConnector(String url){
		ModelAndView mv = new ModelAndView("pwatcher");
		try {
			mv.addObject("connector", systemInfoService.getConnector(url));
		} catch (IOException e) {
			mv.setViewName("/error");
			mv.addObject("msg", e.getMessage());
		}
		return mv;
	}
}
