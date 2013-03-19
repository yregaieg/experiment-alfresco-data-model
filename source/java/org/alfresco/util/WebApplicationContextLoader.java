/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.util;

import java.util.Arrays;

import javax.servlet.ServletContext;

import org.apache.chemistry.opencmis.commons.server.CmisServiceFactory;
import org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class WebApplicationContextLoader
{
	private static ConfigurableApplicationContext instance;
	private static String[] usedConfiguration;
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
		if (configLocations == null)
		{
			throw new IllegalArgumentException("configLocations argument is mandatory.");
		}
		if (usedConfiguration != null && Arrays.deepEquals(configLocations, usedConfiguration))
		{
			// The configuration was used to create the current context
			return instance;
		}
		// The config has changed so close the current context (if any)
		closeApplicationContext();

		AbstractApplicationContext ctx = (AbstractApplicationContext)BaseApplicationContextHelper.getApplicationContext(configLocations);
		
		CmisServiceFactory factory = (CmisServiceFactory)ctx.getBean("CMISServiceFactory");
		
		DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(ctx.getBeanFactory());
		GenericWebApplicationContext gwac = new GenericWebApplicationContext(dlbf);
		servletContext.setAttribute(GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, gwac);
        servletContext.setAttribute(CmisRepositoryContextListener.SERVICES_FACTORY, factory);
		gwac.setServletContext(servletContext);
		gwac.refresh();

		usedConfiguration = configLocations;

		return gwac;
		
//		instance = new MyWebApplicationContext(servletContext, ctx);

//		return instance;
	}

	/**
	 * Closes and releases the application context.  On the next call to
	 * {@link #getApplicationContext()}, a new context will be given.
	 */
	public static synchronized void closeApplicationContext()
	{
		if (instance == null)
		{
			// Nothing to do
			return;
		}
		instance.close();
		instance = null;
		usedConfiguration = null;
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

	/**
	 * A wrapper around {@link ClassPathXmlApplicationContext} which allows us
	 * to enable lazy loading or prevent Subsystem autostart as requested.
	 */
//	protected static class VariableFeatureClassPathXmlApplicationContext extends ClassPathXmlApplicationContext
//	{
//		protected VariableFeatureClassPathXmlApplicationContext(String[] configLocations) throws BeansException
//		{
//			super(configLocations);
//		}
//
//		protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)
//		{
//			super.initBeanDefinitionReader(reader);
//
//			if (useLazyLoading)
//			{
//				LazyClassPathXmlApplicationContext.postInitBeanDefinitionReader(reader);
//			}
//			if (noAutoStart)
//			{
//				NoAutoStartClassPathXmlApplicationContext.postInitBeanDefinitionReader(reader);
//			}
//		}
//	}

	// 
	protected static class WebApplicationContext extends GenericWebApplicationContext //VariableFeatureClassPathXmlApplicationContext implements WebApplicationContext
	{
//		private ServletContext servletContext;

//		protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException
//		{
//
//		}

		protected WebApplicationContext(ServletContext servletContext, AbstractApplicationContext ctx) throws BeansException
		{
			super((DefaultListableBeanFactory)ctx.getBeanFactory(), servletContext);
//			this.servletContext = servletContext;
		}

//        protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)
//        {
//            super.initBeanDefinitionReader(reader);
//
//            if (useLazyLoading)
//            {
//                LazyClassPathXmlApplicationContext.postInitBeanDefinitionReader(reader);
//            }
//            if (noAutoStart)
//            {
//                NoAutoStartClassPathXmlApplicationContext.postInitBeanDefinitionReader(reader);
//            }
//        }

//		@Override
//		public ServletContext getServletContext()
//		{
//			return servletContext;
//		}
	}
}
