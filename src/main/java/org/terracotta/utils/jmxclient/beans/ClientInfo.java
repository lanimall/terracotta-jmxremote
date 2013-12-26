package org.terracotta.utils.jmxclient.beans;

import java.util.Map;

public class ClientInfo {
	
	private String nodeID = "";
	private Float cacheAverageGetTimeMillis = null;
	private long hits = 0, misses = 0, puts = 0;
	Map<String, long[]> cacheMetrics = null;
	
	public ClientInfo(String nodeID){
		this.nodeID = nodeID;
	}
	
	public String getNodeID () {
		return this.nodeID;
	}
	
	public Map<String, long[]> getCacheMetrics() {
		return cacheMetrics;
	}

	public void setCacheMetrics(Map<String, long[]> cacheMetrics) {
		this.cacheMetrics = cacheMetrics;
	}

	public Float getCacheAverageGetTimeMillis() {
		return cacheAverageGetTimeMillis;
	}

	public void setCacheAverageGetTimeMillis(Float cacheAverageGetTimeMillis) {
		this.cacheAverageGetTimeMillis = cacheAverageGetTimeMillis;
	}

	public long getHits() {
		return hits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	public long getMisses() {
		return misses;
	}

	public void setMisses(long misses) {
		this.misses = misses;
	}

	public long getPuts() {
		return puts;
	}

	public void setPuts(long puts) {
		this.puts = puts;
	}
	
	public long getTPS() {
		return this.hits + this.misses + this.puts;
	}
}