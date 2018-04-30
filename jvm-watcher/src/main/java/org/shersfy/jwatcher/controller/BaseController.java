package org.shersfy.jwatcher.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shersfy.jwatcher.beans.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseController{

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();
	
	/**处理成功**/
	protected static final int SUCESS 	= ResultCode.SUCESS;
	/**处理失败**/
	protected static final int FAIL 	= ResultCode.FAIL;
	
	protected static final String basepath = "basePath";
	
	@ModelAttribute
	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		THREAD_LOCAL_REQUEST.set(request);
		THREAD_LOCAL_RESPONSE.set(response);
		setBasePath(request);
	}

	public HttpServletRequest getRequest() {
		return THREAD_LOCAL_REQUEST.get();
	}

	public HttpServletResponse getResponse() {
		return THREAD_LOCAL_RESPONSE.get();
	}

	/**获取根路径**/
	protected String getBasePath() {
		HttpServletRequest request = getRequest();
		return request.getAttribute(basepath).toString();
	}

	protected void setBasePath(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer(0);
		basePath.append(request.getScheme()).append("://");
		basePath.append(request.getServerName());
		if(request.getServerPort() != 80 && request.getServerPort() != 443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(request.getContextPath());
		request.setAttribute(basepath, basePath.toString());
	}

}
