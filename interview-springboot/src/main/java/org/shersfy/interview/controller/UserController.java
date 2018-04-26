package org.shersfy.interview.controller;

import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shersfy.interview.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Resource
	private UserService userService;
	
	@RequestMapping("/login")
	@ResponseBody
	public WebAsyncTask<String> login(HttpServletRequest req, HttpServletResponse res, String username, String pwd){
		
//		DeferredResult<String> result = new DeferredResult<String>(30000L);
//		result.onCompletion(new Runnable() {
//			
//			@Override
//			public void run() {
//				userService.login(username, pwd);
//				result.setResult(String.format("user: %s, pwd: %s", username, pwd));
//				System.out.println();
//			}
//		});
		
		Callable<String> cb = new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				System.out.println("----------starting--------------");
				userService.login(username, pwd);
				System.out.println("----------finished--------------");
				return String.format("user: %s, pwd: %s", username, pwd);
			}
		};
		
		return new WebAsyncTask<>(50000, cb);
	}

}
