package org.shersfy.jwatcher.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JWatcherController {
	
	private Logger logger = LoggerFactory.getLogger(JWatcherController.class);
	
	@RequestMapping(value="/")
	@ResponseBody
	public ModelAndView index(){
		logger.info("url={}", "index");
		ModelAndView mv = new ModelAndView("index");
		return mv;
	}
	
	@RequestMapping("/login")
	@ResponseBody
	public String login(HttpServletRequest req, HttpServletResponse res, String username, String pwd){
		
		return String.format("user=%s, pwd=%s", username, pwd);
	}

}
