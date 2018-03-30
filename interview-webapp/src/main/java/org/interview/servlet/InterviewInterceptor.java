package org.interview.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  HandlerInterceptor SpringMVC拦截器， AOP的应用<br/>
 *  优点：可以使用DI依赖注入
 *  缺点：只能拦截Controller处理, 其它静态资源拦截不了
 *  
 * @author shersfy
 * @date 2018-03-30
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class InterviewInterceptor implements HandlerInterceptor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InterviewInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LOGGER.info("url={}", request.getRequestURL());
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			Object modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

	
}
