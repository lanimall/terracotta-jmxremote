package org.terracotta.utils.jmxclient.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CacheManagerInfo {
	private final String mbeanName;
	private final String mbeanID;
	
	private ArrayList<String> clientMbeansIDs = new ArrayList<String>();
	private String masterClientMbeanID = new String(); // this is used to keep track of one client who has all of the caches defined/connected.
	private HashSet<String> caches;
	
	public CacheManagerInfo(String cmMBeanID, String cmMBeanName){
		this.mbeanID = cmMBeanID;
		this.mbeanName = cmMBeanName;
		
		this.clientMbeansIDs.add(cmMBeanID);
		this.masterClientMbeanID = cmMBeanID;
	}
	
	public ArrayList<String> getClientMbeansIDs() {
		return clientMbeansIDs;
	}

	public String getMasterClientMbeanID() {
		return masterClientMbeanID;
	}

	public String getMbeanName() {
		return mbeanName;
	}

	public String getMbeanID() {
		return mbeanID;
	}

	public void addClientMbeanID(String clientMbeanID){
		clientMbeansIDs.add(clientMbeanID);
	}
	
	public void addCaches(String[] newCaches){
		HashSet<String> toAdd = new HashSet<String>(Arrays.asList(newCaches));
		this.caches.addAll(toAdd);
	}
	
	public String getCacheList() {
		StringBuilder sb = new StringBuilder();
		if(null != caches){
			for(String cache : caches){
				sb.append(cache);
			}
		}
		return sb.toString();
	}
	
	public String[] getCaches() {
		return (String[])this.caches.toArray(new String[caches.size()]);
	}
	
	public void replaceClientMbeanID(String newMasterClientMbeanID) {
		masterClientMbeanID = newMasterClientMbeanID;
	}
}
