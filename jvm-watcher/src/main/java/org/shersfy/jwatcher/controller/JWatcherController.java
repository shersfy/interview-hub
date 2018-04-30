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
public class JWatcherController extends BaseController{
	
	@Resource
	SystemInfoService systemInfoService;
	
	@RequestMapping("/")
	@ResponseBody
	public ModelAndView index(){
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("system", systemInfoService.getSystemInfo());
		return mv;
	}
	
	@RequestMapping("/capacity")
	@ResponseBody
	public ModelAndView capacity(){
		ModelAndView mv = new ModelAndView("capacity");
//		mv.addObject("cpu", systemInfoService.getCpuInfo());
//		mv.addObject("memo", systemInfoService.getMemory());
		return mv;
	}
	
	@RequestMapping("/memo")
	@ResponseBody
	public Result getMemoInfo(){
		Result res = new Result();
		res.setModel(systemInfoService.getMemory());
		return res;
	}
	
	
	@RequestMapping("/login")
	@ResponseBody
	public String login(HttpServletRequest req, HttpServletResponse res, String username, String pwd){
		
		return String.format("user=%s, pwd=%s", username, pwd);
	}

}
