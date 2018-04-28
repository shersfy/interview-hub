package org.shersfy.jwatcher.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JWatcherController {
	
	@Resource
	SystemInfoService systemInfoService;
	
	@RequestMapping("/")
	@ResponseBody
	public ModelAndView index(){
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("system", systemInfoService.getSystemInfo());
		return mv;
	}
	
	@RequestMapping("/system")
	@ResponseBody
	public Result getSystemInfo(){
		Result res = new Result();
		res.setModel(systemInfoService.getSystemInfo());
		return res;
	}
	
	
	@RequestMapping("/login")
	@ResponseBody
	public String login(HttpServletRequest req, HttpServletResponse res, String username, String pwd){
		
		return String.format("user=%s, pwd=%s", username, pwd);
	}

}
