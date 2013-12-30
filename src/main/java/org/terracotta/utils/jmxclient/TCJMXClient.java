package org.terracotta.utils.jmxclient;

import java.io.IOException;
import java.io.StringWriter;
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
import org.terracotta.utils.jmxclient.beans.CacheManagerInfo;

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

	public boolean initialize(){
		boolean initialize = false;
		try {
			initialize = initConnection();
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return initialize;
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
	
	public boolean hasEhcacheMBeans(){
		return true;
	}
	
	public SampledCacheManagerMBean getCacheManagerMBean(final String cacheManagerName, final String clientID) throws MalformedObjectNameException, NullPointerException, IOException {
		SampledCacheManagerMBean cacheManagerMBean = null;
		try {
			if (initConnection()) {
				String cacheManagerMbeanQuery = getSingleCacheManagerMBeanQuery(cacheManagerName, clientID);
				Set<ObjectName> cacheManagerNameSet = mbs.queryNames(ObjectName.getInstance(cacheManagerMbeanQuery), null);
				if(null != cacheManagerNameSet){
					cacheManagerMBean = getMBean((ObjectName)cacheManagerNameSet.toArray()[0], SampledCacheManagerMBean.class);

				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}
		return cacheManagerMBean;
	}

	public SampledCacheMBean getCacheMBean(final String cacheManagerName, final String cacheName, final String clientID) throws MalformedObjectNameException, NullPointerException, IOException {
		SampledCacheMBean cacheMbean = null;
		try {
			if (initConnection()) {
				String cacheMbeanQuery = getSingleCacheMBeanQuery(cacheManagerName, cacheName, clientID);
				Set<ObjectName> cacheNameSet = mbs.queryNames(ObjectName.getInstance(cacheMbeanQuery), null);
				if(null != cacheNameSet){
					cacheMbean = getMBean((ObjectName)cacheNameSet.toArray()[0], SampledCacheMBean.class);
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get l2 health ", e);
		}

		return cacheMbean;
	}
	
/*	public String[] getCacheNames() throws MalformedObjectNameException, NullPointerException, IOException {
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
	}*/

	/*public Map<String, SampledCacheMBean> getCacheMBeans(String cacheManagerName) throws MalformedObjectNameException, NullPointerException, IOException {
		String[] cacheNames = getCacheNames(cacheManagerName);
		return getCacheMBeans(cacheManagerName, cacheNames);
	}*/

	/*public Map<String, SampledCacheMBean> getCacheMBeans(String cacheManagerName, String[] cacheNames) throws MalformedObjectNameException, NullPointerException, IOException {
		Map<String, SampledCacheMBean> cacheMbeans = new HashMap<String, SampledCacheMBean>();
		try {
			if (initConnection()) {
				if(null != cacheNames){
					for(String cachename : cacheNames){
						String cacheMbeanQuery = getCacheMBeanQuery(cacheManagerName, cachename);

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
	}*/

	public Map<String, CacheManagerInfo> getCacheManagerInfo() {
		Map<String, CacheManagerInfo> cacheManagersMap = null;

		try {
			if(initConnection()) {
				cacheManagersMap = new HashMap<String, CacheManagerInfo>();

				String cacheManagerMbeanQuery = getAllSampledCacheManagerMBeanQuery();
				Set<ObjectName> cacheManagerNames = mbs.queryNames(ObjectName.getInstance(cacheManagerMbeanQuery), null);

				//String forcedClientID = null;
				for(ObjectName cmMBeanObjName : cacheManagerNames){
					try {
						if (cmMBeanObjName == null) {
							System.out.println("Null object name for cache manager");
							break;
						}

						SampledCacheManagerMBean cmMBean = null;
						try {
							cmMBean = (SampledCacheManagerMBean) MBeanServerInvocationProxy.newProxyInstance(
									mbs, cmMBeanObjName, SampledCacheManagerMBean.class, false);
						} catch (Exception e) {
							handleJMXException("Error trying to get CacheManager MBeans", e);
							break;
						}

						String cmClientID = cmMBeanObjName.getKeyProperty("node");
						String cmName = cmMBean.getName();
						String[] cacheNames = cmMBean.getCacheNames();

						CacheManagerInfo cmInfo = cacheManagersMap.get(cmName);
						if (cmInfo == null) {
							// newly found cachemanager
							cmInfo = new CacheManagerInfo(cmClientID, cmName);
							cmInfo.addCaches(cacheNames);

							// add it to the map of CacheManagerInfo objects
							cacheManagersMap.put(cmName, cmInfo);
						} else {
							// repeat cachemanager due to additional clients, just add the client						
							cmInfo.addClientMbeanID(cmClientID);
							cmInfo.addCaches(cacheNames);

							//check if the current CM instance has more caches - if so, replace the one in the cmInfo.
							// this covers the case where certain clients may not define / use all the caches in a given cache manager
							if (cacheNames.length > cmInfo.getCaches().length) {
								cmInfo.replaceMasterClientMbeanID(cmClientID);
							}
						}
					} catch (Exception e) {
						log.error("Error during CacheManager " + cmMBeanObjName + " bean crawl", e);
					}
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get start time", e);
		}

		return cacheManagersMap;
	}
	
	public String getEhCacheMBeanQuery() {
		return "net.sf.ehcache:*";
	}

	public String getAllSampledCacheManagerMBeanQuery(){
		return getMultiCacheManagerMBeanQuery(null, null);
	}

	public String getSingleCacheManagerMBeanQuery(String cacheManagerName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCacheManager");
		sw.append(",name=").append((null != cacheManagerName)?cacheManagerName:"");
		sw.append(",clients=Clients").append(",node=").append((null != clientID)?clientID:"");
		sw.append(",*");

		return sw.toString();
	}
	
	public String getMultiCacheManagerMBeanQuery(String cacheManagerName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCacheManager");

		if(null != cacheManagerName)
			sw.append(",name=").append(cacheManagerName);

		if(null != clientID)
			sw.append(",clients=Clients").append(",node=").append(clientID);

		sw.append(",*");

		return sw.toString();
	}

	public String getSingleCacheMBeanQuery(String cacheManagerName, String cacheName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCache");
		sw.append(",SampledCacheManager=").append((null != cacheManagerName)?cacheManagerName:"");
		sw.append(",name=").append((null != cacheName)?cacheName:"");
		sw.append(",clients=Clients").append(",node=").append((null != clientID)?clientID:"");
		sw.append(",*");

		return sw.toString();
	}
	
	public String getMultiCacheMBeanQuery(String cacheManagerName, String cacheName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCache");

		if(null != cacheManagerName)
			sw.append(",SampledCacheManager=").append(cacheManagerName);

		if(null != cacheName)
			sw.append(",name=").append(cacheName);

		if(null != clientID)
			sw.append(",clients=Clients").append(",node=").append(clientID);
		
		sw.append(",*");

		return sw.toString();
	}
	
//	public String getClientMBeanName(String cacheManagerName, String cacheName, String clientID) {
//		return "net.sf.ehcache:SampledCacheManager="+cacheManagerName+",name="+cacheName+",type=SampledCache,clients=Clients,node="+clientID;
//		//String clientObjectNames = "net.sf.ehcache:SampledCacheManager="+cmInfo.getName()+",name="+cmInfo.getCaches()[i]+",type=SampledCache,clients=Clients,node=*";
//	}
}
