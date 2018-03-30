package org.interview.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerInterceptor {

	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception;
	
	void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, Object modelAndView)
			throws Exception;
	
	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception;
}
