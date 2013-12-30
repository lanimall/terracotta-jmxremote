package org.terracotta.utils.jmxclient.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CacheManagerInfo {
	private final String cmName;
	private final String cmClientID;
	
	private ArrayList<String> clientMbeansIDs = new ArrayList<String>();
	private String masterClientMbeanID = new String(); // this is used to keep track of one client who has all of the caches defined/connected.
	private Set<String> cacheNames = new HashSet<String>();
	
	public CacheManagerInfo(String cmClientID, String cmName){
		this.cmClientID = cmClientID;
		this.cmName = cmName;
		
		this.clientMbeansIDs.add(cmClientID);
		this.masterClientMbeanID = cmClientID;
	}
	
	public ArrayList<String> getClientMbeansIDs() {
		return clientMbeansIDs;
	}

	public String getMasterClientMbeanID() {
		return masterClientMbeanID;
	}

	public String getCmName() {
		return cmName;
	}

	public String getCmClientID() {
		return cmClientID;
	}

	public void addClientMbeanID(String clientMbeanID){
		clientMbeansIDs.add(clientMbeanID);
	}
	
	public void addCaches(String[] newCaches){
		HashSet<String> toAdd = new HashSet<String>(Arrays.asList(newCaches));
		cacheNames.addAll(toAdd);
	}
	
	public String getCacheList() {
		StringBuilder sb = new StringBuilder();
		if(null != cacheNames){
			for(String cache : cacheNames){
				sb.append(cache);
			}
		}
		return sb.toString();
	}
	
	public String[] getCaches() {
		return (String[])cacheNames.toArray(new String[cacheNames.size()]);
	}
	
	public void replaceMasterClientMbeanID(String newMasterClientMbeanID) {
		masterClientMbeanID = newMasterClientMbeanID;
	}
}
