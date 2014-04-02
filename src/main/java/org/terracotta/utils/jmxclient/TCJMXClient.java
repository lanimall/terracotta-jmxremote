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

	protected final String username;
	protected final String password;
	protected final String host;
	protected final int port;

	protected volatile JMXConnector jmxConnector;
	protected MBeanServerConnection mbs;

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

	protected abstract JMXConnector createJMXConnector() throws Exception;
	
	protected abstract void initConnectionInternal() throws Exception;

	protected boolean initConnection() {
		JMXConnector result = jmxConnector;
		if (result == null) {
			synchronized (this.getClass()) {
				result = jmxConnector;
				if (result == null) {
					try {
						result = createJMXConnector();
						jmxConnector = result;
						
						if(null != jmxConnector)
							mbs = jmxConnector.getMBeanServerConnection();
						
						initConnectionInternal();
					} catch (Exception e1) {
						log.error("Exception while trying to open the JMX connector to " + getHostPort(), e1);
					}
				}
			}
		}
		return (null != result);
	}
	
	public void close(){
		if (jmxConnector != null) {
			synchronized (this.getClass()) {
				try {
					//System.out.println("try to close the connection!");
					jmxConnector.close();
				} catch (Exception e1) {
					log.error("Exception while trying to close the JMX connector for port no: " + port, e1);
				} finally {
					jmxConnector = null;
				}
			}
		}
	}
	
	protected void handleJMXException(String message, Exception e) {
		log.error(String.format("Closing JMX Connection on %s due to error: %s", getHostPort(), message), e);
		close();
	}

	//DSOClientMBean
	public <T> T getMBean(ObjectName objName, Class<T> clazz) {
		T mBeanProxy = null;
		try {
			if (initConnection()) {
				if(log.isDebugEnabled())
					log.debug(String.format("Trying to get Mbean identified by %s and cast it to %s", objName.toString(), clazz.toString()));

				mBeanProxy = MBeanServerInvocationProxy.newProxyInstance(mbs, objName, clazz, false);
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get mbean", e);
		}

		return mBeanProxy;
	}

	public boolean hasEhcacheMBeans(){
		return true;
	}

	public SampledCacheManagerMBean getCacheManagerMBean(final String cacheManagerName, final String clientID) {
		SampledCacheManagerMBean cacheManagerMBean = null;
		try {
			if (initConnection()) {
				String cacheManagerMbeanQuery = getCacheManagerMBeanQuery(cacheManagerName, clientID);
				Set<ObjectName> cacheManagerNameSet = mbs.queryNames(ObjectName.getInstance(cacheManagerMbeanQuery), null);
				if(null != cacheManagerNameSet && cacheManagerNameSet.size() > 0){
					cacheManagerMBean = getMBean((ObjectName)cacheManagerNameSet.toArray()[0], SampledCacheManagerMBean.class);
				} else {
					log.warn(String.format("No cache manager mbean was found for query=%s",cacheManagerMbeanQuery));
				}
			} 
		} catch (Exception e) {
			handleJMXException(String.format("Failed to get cache manager mbean for cachemanagername=%s and clientid=%s", cacheManagerName, clientID), e);
		}
		return cacheManagerMBean;
	}

	public SampledCacheMBean getCacheMBean(final String cacheManagerName, final String cacheName, final String clientID) throws MalformedObjectNameException, NullPointerException, IOException {
		SampledCacheMBean cacheMbean = null;
		try {
			if (initConnection()) {
				String cacheMbeanQuery = getCacheMBeanQuery(cacheManagerName, cacheName, clientID);
				Set<ObjectName> cacheNameSet = mbs.queryNames(ObjectName.getInstance(cacheMbeanQuery), null);
				if(null != cacheNameSet && cacheNameSet.size() > 0){
					cacheMbean = getMBean((ObjectName)cacheNameSet.toArray()[0], SampledCacheMBean.class);
				} else {
					log.warn(String.format("No cache manager mbean was found for query=%s", cacheMbeanQuery));
				}
			} 
		} catch (Exception e) {
			handleJMXException(String.format("Failed to get cache manager mbean for cachemanagername=%s, cachename=%s, and clientid=%s", cacheManagerName, cacheName, clientID), e);
		}

		return cacheMbean;
	}

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
							log.warn("Null object name for cache manager");
							break;
						}

						SampledCacheManagerMBean cmMBean = null;
						try {
							cmMBean = getMBean(cmMBeanObjName, SampledCacheManagerMBean.class);
						} catch (Exception e) {
							handleJMXException(String.format("Error trying to get CacheManager MBeans with objectname=%s", cmMBeanObjName), e);
							break;
						}

						if(null != cmMBean){
							String cmClientID = cmMBeanObjName.getKeyProperty("node");
							String cmName = cmMBean.getName();
							String[] cacheNames = cmMBean.getCacheNames();

							if(log.isDebugEnabled()){
								StringWriter cacheNamesDebug = new StringWriter();
								if(null != cacheNames){
									for(String c : cacheNames){
										cacheNamesDebug.append(c).append(",");
									}
								}
								log.debug(String.format("CM mbean: name=%s, cmClientId=%s, cachenames=%s", cmName, cmClientID, cacheNamesDebug.toString()));
							}

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
						} else {
							log.warn("The cache manager mbean object is null...");
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
		return getCacheManagerMBeanQuery(null, null);
	}

	public String getCacheManagerMBeanQuery(String cacheManagerName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCacheManager");

		if(null != cacheManagerName)
			sw.append(",name=").append(cacheManagerName);

		if(null != clientID)
			sw.append(",clients=Clients").append(",node=").append(clientID);

		sw.append(",*");

		if(log.isDebugEnabled()){
			log.debug(String.format("getCacheManagerMBeanQuery(%s,%s)=%s", cacheManagerName, clientID, sw.toString()));
		}

		return sw.toString();
	}

	public String getCacheMBeanQuery(String cacheManagerName, String cacheName, String clientID) {
		StringWriter sw = new StringWriter();
		sw.append("net.sf.ehcache:type=SampledCache");
		sw.append(",SampledCacheManager=").append((null != cacheManagerName)?cacheManagerName:"");
		sw.append(",name=").append((null != cacheName)?cacheName:"");
		sw.append(",clients=Clients").append(",node=").append((null != clientID)?clientID:"");
		sw.append(",*");

		if(log.isDebugEnabled()){
			log.debug(String.format("getCacheMBeanQuery(%s,%s,%s)=%s", cacheManagerName, cacheName, clientID, sw.toString()));
		}

		return sw.toString();
	}
}
