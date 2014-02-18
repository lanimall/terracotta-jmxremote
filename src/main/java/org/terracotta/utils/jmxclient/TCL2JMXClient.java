package org.terracotta.utils.jmxclient;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import net.sf.ehcache.management.sampled.SampledCacheMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.utils.jmxclient.beans.CacheStats;
import org.terracotta.utils.jmxclient.beans.L1UsageStats;
import org.terracotta.utils.jmxclient.beans.L2ClientID;
import org.terracotta.utils.jmxclient.beans.L2ClientRuntimeInfo;
import org.terracotta.utils.jmxclient.beans.L2DataStats;
import org.terracotta.utils.jmxclient.beans.L2ProcessInfo;
import org.terracotta.utils.jmxclient.beans.L2RuntimeStatus;
import org.terracotta.utils.jmxclient.beans.L2TransactionsStats;
import org.terracotta.utils.jmxclient.utils.L2RuntimeState;

import com.tc.admin.common.MBeanServerInvocationProxy;
import com.tc.cli.CommandLineBuilder;
import com.tc.config.schema.L2Info;
import com.tc.config.schema.ServerGroupInfo;
import com.tc.management.beans.L2MBeanNames;
import com.tc.management.beans.TCServerInfoMBean;
import com.tc.stats.api.DSOClientMBean;
import com.tc.stats.api.DSOMBean;

public class TCL2JMXClient extends TCJMXClient {
	private static Logger log = LoggerFactory.getLogger(TCL2JMXClient.class);

	private final boolean useRMI;

	private TCServerInfoMBean l2MBean;
	private DSOMBean dsoMbean;

	public TCL2JMXClient(String username, String password, String host, int port) {
		this(username, password, host, port, false);
	}

	public TCL2JMXClient(String username, String password, String host, int port, boolean useRMI) {
		super(username, password, host, port);
		this.useRMI = useRMI;
	}

	protected final void initConnectionInternal() throws Exception {
		if(log.isDebugEnabled()){
			log.debug(String.format("Entering initConnectionInternal()"));
		}
		
		log.info("Establishing a new JMX Connection to " + getHostPort());
		if(useRMI){ //using normal RMI
			log.info("\nCreate an RMI connector client and connect it to the RMI connector server");

			String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", host, new Integer(port).toString());
			if(log.isDebugEnabled())
				log.debug(String.format("JMX URL:[%s]", url));

			Hashtable<String, Object> env = new Hashtable<String, Object>(); 
			if (username != null && !"".equals(username))  
			{  
				String[] creds = new String[2];  
				creds[0] = username;  
				creds[1] = (null != password)?password:"";  
				env.put(JMXConnector.CREDENTIALS, creds);  
			}  

			JMXServiceURL serviceUrl = new JMXServiceURL(url);
			jmxConnector = JMXConnectorFactory.connect(serviceUrl, env);
			if(null != jmxConnector)
				mbs = jmxConnector.getMBeanServerConnection();
		} else {
			log.info("\nCreate an JMX connector client");
			jmxConnector = CommandLineBuilder.getJMXConnector(username, password, host, port);

			if(null != jmxConnector)
				mbs = jmxConnector.getMBeanServerConnection();
		}

		if(null != mbs){
			// get server l2 info mbean:
			l2MBean = MBeanServerInvocationProxy.newMBeanProxy(mbs, L2MBeanNames.TC_SERVER_INFO, TCServerInfoMBean.class, false);

			//dsoMbean - Needed to get client mbeans
			dsoMbean = (DSOMBean) MBeanServerInvocationProxy.newMBeanProxy(mbs, L2MBeanNames.DSO, DSOMBean.class, false);
		}
	}

	public boolean isNodeActive() {
		boolean active = false;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering isNodeActive()"));
			}
			
			if (initConnection()) {
				active = l2MBean.isActive();
			} 
		} catch (Exception e) {
			handleJMXException("Failed to check if client is active", e);
		}

		if(log.isDebugEnabled())
			log.debug(String.format("isNodeActive()=%s", new Boolean(active).toString()));

		return active;
	}

	/*
	 * if -1 is returned, an error must have happened
	 * this method will not return the right count if the current connected server is passive...let's return an exception for now if server is not active
	 */
	public int getClientCount() {
		int clientCount = -1;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getClientCount()"));
			}
			
			if (initConnection()) {
				if(!l2MBean.isActive())
					throw new JMXClientException("Node must be active to provide accurate client count.");

				clientCount = dsoMbean.getClients().length;
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get client counts", e);
		}

		if(log.isDebugEnabled())
			log.debug(String.format("getClientCount()=%s", new Integer(clientCount).toString()));

		return clientCount;
	}

	public L2ClientID[] getClientIDs(){
		ArrayList<L2ClientID> clientIDs = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getClientIDs()"));
			}
			
			if (initConnection()) {
				if(null != l2MBean && !l2MBean.isActive())
					throw new JMXClientException("Node must be active to provide accurate client count.");

				ObjectName[] clientsMBeans = dsoMbean.getClients();
				if(null != clientsMBeans && clientsMBeans.length > 0){
					clientIDs = new ArrayList<L2ClientID>();
					for(ObjectName clientMBean: clientsMBeans){
						DSOClientMBean dsoClientMBean = getMBean(clientMBean, DSOClientMBean.class);
						if(null != dsoClientMBean){
							L2ClientID l2ClientID = new L2ClientID(
									dsoClientMBean.getClientID().toString(), 
									dsoClientMBean.getRemoteAddress(),
									dsoClientMBean.getNodeID(),
									dsoClientMBean.getChannelID().toString(),
									dsoClientMBean.isTunneledBeansRegistered()
									);

							clientIDs.add(l2ClientID);
						}
					}
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get client ids", e);
		}

		if(log.isDebugEnabled()){
			StringWriter debug = new StringWriter();
			if(null != clientIDs){
				for(L2ClientID c : clientIDs){
					debug.append(c.toString()).append("~~");
				}
			}
			log.debug(String.format("getClientIDs()=%s", debug));
		}
		return (null != clientIDs)?clientIDs.toArray(new L2ClientID[clientIDs.size()]):null;
	}

	public L2ClientRuntimeInfo[] getClients() {
		ArrayList<L2ClientRuntimeInfo> clientsInfoArray = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getClients()"));
			}
			
			if (initConnection()) {
				if(null != l2MBean && !l2MBean.isActive())
					throw new JMXClientException("Node must be active to provide accurate client count.");

				ObjectName[] clientsMBeans = dsoMbean.getClients();
				if(null != clientsMBeans && clientsMBeans.length > 0){
					clientsInfoArray = new ArrayList<L2ClientRuntimeInfo>();
					for(ObjectName clientMBean: clientsMBeans){
						DSOClientMBean dsoClientMBean = getMBean(clientMBean, DSOClientMBean.class);
						if(null != dsoClientMBean){
							L2ClientRuntimeInfo l2ClientInfo = new L2ClientRuntimeInfo(
									new L2ClientID(
											dsoClientMBean.getClientID().toString(), 
											dsoClientMBean.getRemoteAddress(),
											dsoClientMBean.getNodeID(),
											dsoClientMBean.getChannelID().toString(),
											dsoClientMBean.isTunneledBeansRegistered()
											)
									);

							l2ClientInfo.setLiveObjectCount(dsoClientMBean.getLiveObjectCount());
							l2ClientInfo.setObjectFaultRate(dsoClientMBean.getObjectFaultRate());
							l2ClientInfo.setObjectFlushRate(dsoClientMBean.getObjectFlushRate());
							l2ClientInfo.setPendingTransactionsCount(dsoClientMBean.getPendingTransactionsCount());
							l2ClientInfo.setServerMapGetSizequestsCount(dsoClientMBean.getServerMapGetSizeRequestsCount());
							l2ClientInfo.setServerMapGetSizeRequestsRate(dsoClientMBean.getServerMapGetSizeRequestsRate());
							l2ClientInfo.setServerMapGetValueRequestsCount(dsoClientMBean.getServerMapGetValueRequestsCount());
							l2ClientInfo.setServerMapGetValueRequestsRate(dsoClientMBean.getServerMapGetValueRequestsRate());
							l2ClientInfo.setTransactionRate(dsoClientMBean.getTransactionRate());

							clientsInfoArray.add(l2ClientInfo);
						}
					}
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get client runtime stats", e);
		}

		if(log.isDebugEnabled()){
			StringWriter debug = new StringWriter();
			if(null != clientsInfoArray){
				for(L2ClientRuntimeInfo c : clientsInfoArray){
					debug.append(c.toString()).append("~~");
				}
			}
			log.debug(String.format("getClients()=%s", debug));
		}
		
		return (null != clientsInfoArray)?clientsInfoArray.toArray(new L2ClientRuntimeInfo[clientsInfoArray.size()]):null;
	}

	public void enableCacheStats(final String cacheManagerName, final String cacheName, final String clientID) {
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering enableCacheStats(%s, %s, %s)", cacheManagerName, cacheName, clientID));
			}
			
			if (initConnection()) {
				SampledCacheMBean cacheMbean = getCacheMBean(cacheManagerName, cacheName, clientID);
				cacheMbean.enableStatistics();
				cacheMbean.enableSampledStatistics();
			} 
		} catch (Exception e) {
			handleJMXException("Failed to enable cache stats", e);
		}
	}

	public void disableCacheStats(final String cacheManagerName, final String cacheName, final String clientID) {
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering disableCacheStats(%s, %s, %s)", cacheManagerName, cacheName, clientID));
			}
			
			if (initConnection()) {
				SampledCacheMBean cacheMbean = getCacheMBean(cacheManagerName, cacheName, clientID);
				cacheMbean.disableSampledStatistics();
				cacheMbean.disableStatistics();
			} 
		} catch (Exception e) {
			handleJMXException("Failed to disable cache stats", e);
		}
	}

	public CacheStats enableStatisticsAndGetCacheStats(final String cacheManagerName, final String cacheName, final String clientID) {
		return enableStatisticsAndGetCacheStats(cacheManagerName, cacheName, clientID, false);
	}

	public CacheStats enableStatisticsAndGetCacheStats(final String cacheManagerName, final String cacheName, final String clientID, final boolean rawCountsOnly) {
		CacheStats cacheStats = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering enableStatisticsAndGetCacheStats(%s, %s, %s)", cacheManagerName, cacheName, clientID));
			}
			
			if (initConnection()) {
				SampledCacheMBean cacheMbean = getCacheMBean(cacheManagerName, cacheName, clientID);
				if(!cacheMbean.isStatisticsEnabled()){
					cacheMbean.enableStatistics();
					cacheMbean.enableSampledStatistics();
				}
				cacheStats = getCacheStatsFromMBean(cacheMbean, rawCountsOnly);
			}
		} catch (Exception e) {
			handleJMXException("Failed to enable stats and get cache stats", e);
		}
		return cacheStats;
	}

	public CacheStats getCacheStatsAndDisableStatistics(final String cacheManagerName, final String cacheName, final String clientID) {
		return getCacheStatsAndDisableStatistics(cacheManagerName, cacheName, clientID, false);
	}

	public CacheStats getCacheStatsAndDisableStatistics(final String cacheManagerName, final String cacheName, final String clientID, final boolean rawCountsOnly) {
		CacheStats cacheStats = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getCacheStatsAndDisableStatistics(%s, %s, %s)", cacheManagerName, cacheName, clientID));
			}
			
			if (initConnection()) {
				SampledCacheMBean cacheMbean = getCacheMBean(cacheManagerName, cacheName, clientID);
				cacheStats = getCacheStatsFromMBean(cacheMbean, rawCountsOnly);

				if(cacheMbean.isStatisticsEnabled()){
					cacheMbean.disableSampledStatistics();
					cacheMbean.disableStatistics();
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get cache stats and disable stats", e);
		}
		return cacheStats;
	}

	private CacheStats getCacheStatsFromMBean(final SampledCacheMBean cacheMbean, final boolean rawCountsOnly) {
		CacheStats cacheStats = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getCacheStatsFromMBean(cacheMbean, rawCountsOnly)"));
			}
			
			if (initConnection()) {
				if(null != cacheMbean){
					cacheStats = new CacheStats(cacheMbean.getCacheName());
					cacheStats.setEnabled(cacheMbean.isEnabled());
					cacheStats.setStatsEnabled(cacheMbean.isStatisticsEnabled());

					//sizes
					cacheStats.setCacheSize(cacheMbean.getSize());
					cacheStats.setLocalHeapSize(cacheMbean.getLocalHeapSize());
					cacheStats.setLocalOffHeapSize(cacheMbean.getLocalOffHeapSize());
					cacheStats.setLocalDiskSize(cacheMbean.getLocalDiskSize());

					//raw counts
					cacheStats.setCacheHitCount(cacheMbean.getCacheHitCount());
					cacheStats.setCacheMissCount(cacheMbean.getCacheMissCount());
					cacheStats.setEvictedCount(cacheMbean.getEvictedCount());
					cacheStats.setExpiredCount(cacheMbean.getExpiredCount());
					cacheStats.setPutCount(cacheMbean.getPutCount());
					cacheStats.setRemovedCount(cacheMbean.getRemovedCount());
					cacheStats.setInMemoryHitCount(cacheMbean.getInMemoryHitCount());
					cacheStats.setOffHeapHitCount(cacheMbean.getOffHeapHitCount());
					cacheStats.setOnDiskHitCount(cacheMbean.getOnDiskHitCount());
					cacheStats.setInMemoryMissCount(cacheMbean.getInMemoryMissCount());
					cacheStats.setOffHeapMissCount(cacheMbean.getOffHeapMissCount());
					cacheStats.setOnDiskMissCount(cacheMbean.getOnDiskMissCount());

					if(!rawCountsOnly){
						//hits rates
						cacheStats.setCacheHitRatio(cacheMbean.getCacheHitRatio());
						cacheStats.setCacheHitRate(cacheMbean.getCacheHitRate());
						cacheStats.setOnHeapHitRate(cacheMbean.getCacheInMemoryHitRate());
						cacheStats.setOffHeapHitRate(cacheMbean.getCacheOffHeapHitRate());
						cacheStats.setOnDiskHitRate(cacheMbean.getCacheOnDiskHitRate());

						//misses rates
						cacheStats.setCacheMissRate(cacheMbean.getCacheMissRate());
						cacheStats.setOnHeapMissRate(cacheMbean.getCacheInMemoryMissRate());
						cacheStats.setOffHeapMissRate(cacheMbean.getCacheOffHeapMissRate());
						cacheStats.setOnDiskMissRate(cacheMbean.getCacheOnDiskMissRate());

						//evictions/updates rates
						cacheStats.setCachePutRate(cacheMbean.getCachePutRate());
						cacheStats.setEvictionRate(cacheMbean.getCacheEvictionRate());
						cacheStats.setExpirationRate(cacheMbean.getCacheExpirationRate());
						cacheStats.setRemoveRate(cacheMbean.getCacheRemoveRate());
					}
				}
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get cache stats", e);
		}

		return cacheStats;
	}

	public CacheStats getCacheStats(final String cacheManagerName, final String cacheName, final String clientID) {
		return getCacheStats(cacheManagerName, cacheName, clientID, false);
	}

	public CacheStats getCacheStats(final String cacheManagerName, final String cacheName, final String clientID, final boolean rawCountsOnly) {
		CacheStats cacheStats = null;
		try {
			if (initConnection()) {
				SampledCacheMBean cacheMbean = getCacheMBean(cacheManagerName, cacheName, clientID);
				cacheStats = getCacheStatsFromMBean(cacheMbean, rawCountsOnly);
			} 
		} catch (Exception e) {
			handleJMXException("Failed to get cache stats", e);
		}

		return cacheStats;
	}

	public Map<String, L2Info[]> getFullTopologyInfo() {
		Map<String, L2Info[]> topologyInfo = null;
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("Entering getFullTopologyInfo()"));
			}
			
			if(initConnection()) {	
				try {
					topologyInfo = new HashMap<String, L2Info[]>();

					// get the server groups (stripes)
					ServerGroupInfo[] stripes = l2MBean.getServerGroupInfo();
					for (int i = 0; i < stripes.length; i++) {
						topologyInfo.put(stripes[i].name(), stripes[i].members());
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get topology info", e);
		}
		
		if(log.isDebugEnabled()){
			StringWriter debug = new StringWriter();
			if(null != topologyInfo){
				for(Entry<String, L2Info[]> c : topologyInfo.entrySet()){
					debug.append("stripe ").append(c.getKey()).append(":");
					L2Info[] l2Infos = c.getValue();
					for(L2Info l2 : l2Infos){
						debug.append("node ").append(l2.name()).append("~~");
					}
					debug.append("\n");
				}
			}
			log.debug(String.format("getFullTopologyInfo()=%s", debug));
		}
		
		return topologyInfo;
	}

	//useful to see if the node we are currently connecting to is the first one with potentially the ehcache client MBeans...
	public boolean isFirstL2Info() {
		L2Info firstNode = getFirstL2Info();
		if(null != firstNode){
			return firstNode.host().equals(host) && firstNode.jmxPort() == port;
		}
		return false;
	}

	public L2Info getFirstL2Info() {
		L2Info nodeFound = null;

		try {
			if(initConnection()) {
				L2Info[] l2Nodes = l2MBean.getL2Info();
				nodeFound = l2Nodes[0];

				/*ServerGroupInfo[] stripes = l2MBean.getServerGroupInfo();
				if(stripes.length > 0){
					if(stripes[0].members().length > 0){
						nodeFound = stripes[0].members()[0];
					}
				}*/
			}
		} catch (Exception e) {
			handleJMXException("Failed to get first l2 info", e);
		}

		return nodeFound;
	}

	public L2Info getL2Info() {
		L2Info nodeFound = null;

		try {
			if(initConnection()) {
				Map<String, L2Info[]> topologyInfo = getFullTopologyInfo();
				for(String stripeName : topologyInfo.keySet()){
					L2Info[] l2Nodes = topologyInfo.get(stripeName);
					for (int i = 0; i < l2Nodes.length; i++) {

						//check by host and port - not perfect - TODO: improve
						if(l2Nodes[i].host().equals(host) && l2Nodes[i].jmxPort() == port) {
							nodeFound = l2Nodes[i];
							break;
						}
					}
					if(null != nodeFound){
						break;
					}
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get current l2 info", e);
		}

		return nodeFound;
	}

	public L2ProcessInfo[] getAllL2Nodes() {
		L2ProcessInfo[] l2RuntimeInfoNodes = null;

		try {
			if(initConnection()) {
				L2Info[] l2Nodes = l2MBean.getL2Info();
				l2RuntimeInfoNodes = new L2ProcessInfo[l2Nodes.length];
				for (int i = 0; i < l2Nodes.length; i++) {
					L2ProcessInfo l2RuntimeInfo = new L2ProcessInfo();
					l2RuntimeInfo.setNodeName(l2Nodes[i].name());
					l2RuntimeInfo.setHostName(l2Nodes[i].safeGetCanonicalHostName());
					l2RuntimeInfo.setHostAddress(l2Nodes[i].safeGetHostAddress());
					l2RuntimeInfo.setHostPortJmxConnect(l2Nodes[i].jmxPort());

					l2RuntimeInfoNodes[i] = l2RuntimeInfo;
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get all l2 process info", e);
		}

		return l2RuntimeInfoNodes;
	}

	public L2ProcessInfo getL2ProcessInfo() {
		L2ProcessInfo l2RuntimeInfo = null;

		try {
			if(initConnection()) {
				L2Info nodeFound = null;
				String stripeFound = null;

				Map<String, L2Info[]> topologyInfo = getFullTopologyInfo();
				for(String stripeName : topologyInfo.keySet()){
					L2Info[] l2Nodes = topologyInfo.get(stripeName);
					for (int i = 0; i < l2Nodes.length; i++) {

						//check by host and port - not perfect - TODO: improve
						if(l2Nodes[i].host().equals(host) && l2Nodes[i].jmxPort() == port) {
							nodeFound = l2Nodes[i];
							break;
						}
					}
					if(null != nodeFound){
						stripeFound = stripeName;
						break;
					}
				}

				if(null != nodeFound){
					l2RuntimeInfo = new L2ProcessInfo();
					l2RuntimeInfo.setNodeName(nodeFound.name());
					l2RuntimeInfo.setHostName(nodeFound.safeGetCanonicalHostName());
					l2RuntimeInfo.setHostAddress(nodeFound.safeGetHostAddress());
					l2RuntimeInfo.setStripeName(stripeFound);
					l2RuntimeInfo.setHostPortJmxConnect(port);
					l2RuntimeInfo.setHostPortL1sConnect(l2MBean.getDSOListenPort());
					l2RuntimeInfo.setHostPortL2sConnect(l2MBean.getDSOGroupPort());
					l2RuntimeInfo.setStartTime(new Date(l2MBean.getStartTime()));
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get l2 process info", e);
		}

		return l2RuntimeInfo;
	}

	public L2RuntimeStatus getL2RuntimeStatus() {
		L2RuntimeStatus l2RuntimeStatus = null;
		try {
			if(initConnection()) {
				l2RuntimeStatus = new L2RuntimeStatus();
				l2RuntimeStatus.setHealth(l2MBean.getHealthStatus());
				if(l2MBean.isActive()){
					l2RuntimeStatus.setState(L2RuntimeState.ACTIVE);
				} else {
					if(l2MBean.isPassiveUninitialized())
						l2RuntimeStatus.setState(L2RuntimeState.PASSIVE_UNINITIALIZED);
					else if (l2MBean.isPassiveStandby())
						l2RuntimeStatus.setState(L2RuntimeState.PASSIVE_STANDBY);
					else //must be in error
						l2RuntimeStatus.setState(L2RuntimeState.ERROR);
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get l2 runtime status", e);
		}

		return l2RuntimeStatus;
	}

	public L2DataStats getL2DataStats() {
		L2DataStats dataStats = null;
		try {
			if(initConnection()) {
				dataStats = new L2DataStats();
				dataStats.setUsedHeap(l2MBean.getUsedMemory());
				dataStats.setMaxHeap(l2MBean.getMaxMemory());
				dataStats.setCachedObjectCount(dsoMbean.getCachedObjectCount());
				dataStats.setLiveObjectCount(dsoMbean.getLiveObjectCount());
				dataStats.setOffheapObjectCachedCount(dsoMbean.getOffheapObjectCachedCount());
				dataStats.setOffheapMapAllocatedMemory(dsoMbean.getOffheapMapAllocatedMemory());
				dataStats.setOffheapMaxDataSize(dsoMbean.getOffheapMaxDataSize());
				dataStats.setOffheapObjectAllocatedMemory(dsoMbean.getOffheapObjectAllocatedMemory());
				dataStats.setOffheapTotalAllocatedSize(dsoMbean.getOffheapTotalAllocatedSize());
			}
		} catch (Exception e) {
			handleJMXException("Failed to get l2 data statistics", e);
		}

		return dataStats;
	}

	public L2TransactionsStats getL2TransactionsStats() {
		L2TransactionsStats l2UsageStats = null;
		try {
			if(initConnection()) {
				l2UsageStats = new L2TransactionsStats();
				l2UsageStats.setGlobalLockRecallRate(dsoMbean.getGlobalLockRecallRate());
				l2UsageStats.setL2DiskFaultRate(dsoMbean.getL2DiskFaultRate());
				l2UsageStats.setOffHeapFaultRate(dsoMbean.getOffHeapFaultRate());
				l2UsageStats.setOffHeapFlushRate(dsoMbean.getOffHeapFlushRate());
				l2UsageStats.setOnHeapFaultRate(dsoMbean.getOnHeapFaultRate());
				l2UsageStats.setOnHeapFlushRate(dsoMbean.getOnHeapFlushRate());
				l2UsageStats.setTransactionRate(dsoMbean.getTransactionRate());
			}
		} catch (Exception e) {
			handleJMXException("Failed to get general l2 statistics", e);
		}

		return l2UsageStats;
	}

	public List<L1UsageStats> getAllL1sUsageStats() {
		List<L1UsageStats> l1UsageStatList = null;
		try {
			if(initConnection()) {
				l1UsageStatList = new ArrayList<L1UsageStats>();
				ObjectName[] clientMBeanPaths = dsoMbean.getClients();
				for(ObjectName clientMBeanPath : clientMBeanPaths){
					DSOClientMBean dsoClientMBean = getMBean(clientMBeanPath, DSOClientMBean.class);
					if(null != dsoClientMBean){
						L1UsageStats l1UsageStats = new L1UsageStats(dsoClientMBean.getClientID().toString());
						l1UsageStats.setRemoteAddress(dsoClientMBean.getRemoteAddress());
						l1UsageStats.setObjectFaultRate(dsoClientMBean.getObjectFaultRate());
						l1UsageStats.setObjectFlushRate(dsoClientMBean.getObjectFlushRate());
						l1UsageStats.setPendingTransactionsCount(dsoClientMBean.getPendingTransactionsCount());
						l1UsageStats.setTransactionRate(dsoClientMBean.getTransactionRate());

						l1UsageStatList.add(l1UsageStats);
					}
				}
			}
		} catch (Exception e) {
			handleJMXException("Failed to get general l1 client statistics", e);
		}

		return l1UsageStatList;
	}
}
