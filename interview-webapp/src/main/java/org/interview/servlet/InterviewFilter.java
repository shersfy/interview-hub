package org.interview.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterviewFilter implements Filter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InterviewFilter.class);
	

	@Override
	public void init(FilterConfig config) throws ServletException {
		LOGGER.info("=========={} init(), ignores='{}'==========", this.getClass().getName(), config.getInitParameter("ignores"));
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		LOGGER.info("=========={} doFilter(), url={}==========", this.getClass().getName(), req.getRequestURL());
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
		LOGGER.info("=========={} destroy()==========", this.getClass().getName());
	}

	
}
