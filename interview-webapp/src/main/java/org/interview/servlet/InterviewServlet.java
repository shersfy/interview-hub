package org.interview.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterviewServlet implements Servlet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InterviewServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOGGER.info("=========={} init(), {}==========", this.getClass().getName(), config);
	}
	
	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		LOGGER.info("=========={} service(), url={}==========", this.getClass().getName(), req.getRequestURL());
	}
	
	@Override
	public void destroy() {
		LOGGER.info("=========={} destroy()==========", this.getClass().getName());
	}

	@Override
	public ServletConfig getServletConfig() {
		LOGGER.info("=========={} getServletConfig()==========", this.getClass().getName());
		return null;
	}

	@Override
	public String getServletInfo() {
		LOGGER.info("=========={} getServletInfo()==========", this.getClass().getName());
		return null;
	}


}
