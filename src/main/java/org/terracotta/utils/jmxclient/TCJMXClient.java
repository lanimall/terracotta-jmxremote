package org.terracotta.utils.jmxclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import net.sf.ehcache.management.sampled.SampledCacheMBean;
import net.sf.ehcache.management.sampled.SampledCacheManagerMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.utils.jmxclient.beans.ClientInfo;

import com.tc.admin.common.MBeanServerInvocationProxy;

public abstract class TCJMXClient {
	private static Logger log = LoggerFactory.getLogger(TCJMXClient.class);

	private volatile boolean initialized = false;

	protected final String username;
	protected final String password;
	protected final String host;
	protected final int port;
	
	protected MBeanServerConnection mbs;
	protected JMXConnector jmxConnector;
	
	protected TCJMXClient(String username, String password, String host, int port) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getHostPort() {
		return String.format("%s:%s", host, new Integer(port).toString());
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	public synchronized void close(){
		if (jmxConnector != null) {
			try {
				//System.out.println("try to close the connection!");
				jmxConnector.close();
			} catch (Exception e1) {
				log.error("Exception while trying to close the JMX connector for port no: " + port, e1);
			} finally {
				jmxConnector = null;
			}
		} 
		jmxConnector = null;
		initialized = false;
	}
	
	protected abstract void initConnectionInternal() throws Exception;
	
	protected synchronized boolean initConnection() {
		if(!initialized) {
			try {
				initConnectionInternal();
				initialized = true;
			} catch(Exception e) {
				handleJMXException("Connection refused to " + getHostPort(), e);
			}
		}
		return initialized;
	}
	
	protected void handleJMXException(String message, Exception e) {
		log.error("Closing JMX Connection on " + getHostPort() + " due to error: " + message, e);
		close();
	}
	
	//DSOClientMBean
	
	public <T> T getMBean(ObjectName objName, Class<T> clazz) throws MalformedObjectNameException, NullPointerException, IOException {
		T mBeanProxy = null;
		try {
			if (initConnection()) {
				mBeanProxy = MBeanServerInvocationProxy.newProxyInstance(mbs, objName, clazz, false);
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return mBeanProxy;
	}
	
	public Map<String, SampledCacheManagerMBean> getCacheManagers(final String cacheManagerName) throws MalformedObjectNameException, NullPointerException, IOException {
		Map<String, SampledCacheManagerMBean> cacheManagersMbeans = new HashMap<String, SampledCacheManagerMBean>();
		try {
			if (initConnection()) {
				String cacheManagerMbeanQuery = getSampledCacheManagerMBeanQuery(cacheManagerName);
				Set<ObjectName> cacheManagerNameSet = mbs.queryNames(ObjectName.getInstance(cacheManagerMbeanQuery), null);
				if(null != cacheManagerNameSet){
					for(ObjectName name: cacheManagerNameSet){
						SampledCacheManagerMBean cacheManagerMBean = getMBean(name, SampledCacheManagerMBean.class);

						if(null != cacheManagerMBean){
							cacheManagersMbeans.put(cacheManagerMBean.getName(), cacheManagerMBean);
						}
					}
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return cacheManagersMbeans;
	}

	public String[] getCacheNames() throws MalformedObjectNameException, NullPointerException, IOException {
		return getCacheNames(null);
	}

	public String[] getCacheNames(String cacheManagerName) throws MalformedObjectNameException, NullPointerException, IOException {
		String[] cacheNames = null;
		try {
			if (initConnection()) {
				String cacheManagerMbeanQuery = getSampledCacheManagerMBeanQuery(cacheManagerName);

				Set<ObjectName> cacheManagerNameSet = mbs.queryNames(ObjectName.getInstance(cacheManagerMbeanQuery), null);
				SampledCacheManagerMBean cacheManagerMBean = getMBean((ObjectName)cacheManagerNameSet.toArray()[0], SampledCacheManagerMBean.class);
				
				if(null != cacheManagerMBean)
					cacheNames = cacheManagerMBean.getCacheNames();
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return cacheNames;
	}

	public SampledCacheMBean getSingleCacheMBean(String cacheManagerName, String cacheName) throws MalformedObjectNameException, NullPointerException, IOException {
		SampledCacheMBean cacheMbean = null;
		Map<String, SampledCacheMBean> cacheMBeans = getCacheMBeans(cacheManagerName, new String[]{cacheName});
		if(null != cacheMBeans){
			cacheMbean = cacheMBeans.get(cacheName);
		}

		return cacheMbean;
	}

	public Map<String, SampledCacheMBean> getCacheMBeans(String cacheManagerName) throws MalformedObjectNameException, NullPointerException, IOException {
		String[] cacheNames = getCacheNames(cacheManagerName);
		return getCacheMBeans(cacheManagerName, cacheNames);
	}

	public Map<String, SampledCacheMBean> getCacheMBeans(String cacheManagerName, String[] cacheNames) throws MalformedObjectNameException, NullPointerException, IOException {
		Map<String, SampledCacheMBean> cacheMbeans = new HashMap<String, SampledCacheMBean>();
		try {
			if (initConnection()) {
				if(null != cacheNames){
					for(String cachename : cacheNames){
						String cacheMbeanQuery = getSampledCacheMBeanQuery(cacheManagerName, cachename);

						Set<ObjectName> cacheNameSet = mbs.queryNames(ObjectName.getInstance(cacheMbeanQuery), null);

						SampledCacheMBean cacheMBean = getMBean((ObjectName)cacheNameSet.toArray()[0], SampledCacheMBean.class);
						
						cacheMbeans.put(cachename, cacheMBean);
					}
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return cacheMbeans;
	}
	
	public String getSampledCacheManagerMBeanQuery(){
		return getSampledCacheManagerMBeanQuery(null);
	}
	
	public String getSampledCacheManagerMBeanQuery(String cacheManagerName) {
		return "net.sf.ehcache:type=SampledCacheManager" + ((null != cacheManagerName)?",name=" + cacheManagerName + ",*":",*");
	}

	public String getSampledCacheMBeanQuery(String cacheManagerName, String cacheName) {
		return "net.sf.ehcache:type=SampledCache" + ((null != cacheManagerName)?",SampledCacheManager=" + cacheManagerName:",*") + ((null != cacheName)?",name=" + cacheName + ",*":",*");
	}
}
