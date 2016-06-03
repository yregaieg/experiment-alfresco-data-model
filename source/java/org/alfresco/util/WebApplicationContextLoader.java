package org.alfresco.util;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.chemistry.opencmis.commons.server.CmisServiceFactory;
import org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class WebApplicationContextLoader
{
	private static ConfigurableApplicationContext instance;
	private static boolean useLazyLoading = false;
	private static boolean noAutoStart = false;

	/**
	 * Provides a static, single instance of the application context.  This method can be
	 * called repeatedly.
	 * <p/>
	 * If the configuration requested differs from one used previously, then the previously-created
	 * context is shut down.
	 * 
	 * @return Returns an application context for the given configuration
	 */
	public synchronized static ConfigurableApplicationContext getApplicationContext(ServletContext servletContext, String[] configLocations)
	{
		AbstractApplicationContext ctx = (AbstractApplicationContext)BaseApplicationContextHelper.getApplicationContext(configLocations);
		
		CmisServiceFactory factory = (CmisServiceFactory)ctx.getBean("CMISServiceFactory");
		
		DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(ctx.getBeanFactory());
		GenericWebApplicationContext gwac = new GenericWebApplicationContext(dlbf);
		servletContext.setAttribute(GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, gwac);
        servletContext.setAttribute(CmisRepositoryContextListener.SERVICES_FACTORY, factory);
		gwac.setServletContext(servletContext);
		gwac.refresh();

		return gwac;
	}

	public synchronized static ConfigurableApplicationContext getApplicationContext(ServletContext servletContext, final String[] configLocations,
			final String[] classLocations) throws IOException
	{
		final AbstractApplicationContext ctx = (AbstractApplicationContext)BaseApplicationContextHelper.getApplicationContext(configLocations, classLocations);
		DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(ctx.getBeanFactory());
		GenericWebApplicationContext gwac = new GenericWebApplicationContext(dlbf);
		CmisServiceFactory factory = (CmisServiceFactory)ctx.getBean("CMISServiceFactory");

		servletContext.setAttribute(GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, gwac);
        servletContext.setAttribute(CmisRepositoryContextListener.SERVICES_FACTORY, factory);
		gwac.setServletContext(servletContext);
		gwac.refresh();

		return gwac;
	}

	/**
	 * Should the Spring beans be initilised in a lazy manner, or all in one go?
	 * Normally lazy loading/intialising shouldn't be used when running with the
	 * full context, but it may be appropriate to reduce startup times when
	 * using a small, cut down context.
	 */
	public static void setUseLazyLoading(boolean lazyLoading)
	{
		useLazyLoading = lazyLoading;
	}

	/**
	 * Will the Spring beans be initilised in a lazy manner, or all in one go?
	 * The default it to load everything in one go, as spring normally does.
	 */
	public static boolean isUsingLazyLoading()
	{
		return useLazyLoading;
	}

	/**
	 * Should the autoStart=true property on subsystems be honoured, or should
	 * this property be ignored and the auto start prevented? Normally we will
	 * use the spring configuration to decide what to start, but when running
	 * tests, you can use this to prevent the auto start.
	 */
	public static void setNoAutoStart(boolean noAutoStart)
	{
		WebApplicationContextLoader.noAutoStart = noAutoStart;
	}

	/**
	 * Will Subsystems with the autoStart=true property set on them be allowed
	 * to auto start? The default is to honour the spring configuration and
	 * allow them to, but they can be prevented if required.
	 */
	public static boolean isNoAutoStart()
	{
		return noAutoStart;
	}

	/**
	 * Is there currently a context loaded and cached?
	 */
	public static boolean isContextLoaded()
	{
		return (instance != null);
	}

	protected static class WebApplicationContext extends GenericWebApplicationContext
	{
		protected WebApplicationContext(ServletContext servletContext, AbstractApplicationContext ctx) throws BeansException
		{
			super((DefaultListableBeanFactory)ctx.getBeanFactory(), servletContext);
		}
	}
}
