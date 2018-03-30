package org.interview.servlet;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterviewListener implements 
	ServletRequestListener,
	ServletRequestAttributeListener,
	
	HttpSessionListener,
	HttpSessionAttributeListener,
	
	ServletContextListener,
	ServletContextAttributeListener,
	
	HttpSessionBindingListener,
	HttpSessionActivationListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InterviewListener.class);

	//=======================1. ServletRequestListener===================
	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		
	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		
	}
	
	//=======================2. ServletRequestListener===================

	@Override
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
	}

	@Override
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		
	}

	@Override
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		
	}
	
	//=======================3. HttpSessionListener===================
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		LOGGER.info("=========={} sessionCreated(), id={}==========", this.getClass().getName(), event.getSession().getId());
		event.getSession().setAttribute("interview", "hello");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		
	}
	
	//=======================4. HttpSessionAttributeListener===================
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		LOGGER.info("=========={} attributeAdded(), {}={} ==========", this.getClass().getName(), event.getName(), event.getValue());
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		
	}
	
	//=======================5. ServletContextListener===================
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
	}
	
	//=======================6. ServletContextAttributeListener===================

	@Override
	public void attributeAdded(ServletContextAttributeEvent arg0) {
		
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		
	}

	//=======================7. HttpSessionBindingListener===================
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		LOGGER.info("=========={} valueBound(), value={}==========", this.getClass().getName(), event.getValue());
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		LOGGER.info("=========={} valueUnbound(), value={}==========", this.getClass().getName(), event.getValue());
	}

	//=======================8. HttpSessionActivationListener===================
	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		LOGGER.info("=========={} sessionDidActivate()==========", this.getClass().getName());
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		LOGGER.info("=========={} sessionDidActivate()==========", this.getClass().getName());
	}

	
}
