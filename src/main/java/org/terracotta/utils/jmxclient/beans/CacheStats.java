package org.terracotta.utils.jmxclient.beans;

public class CacheStats {
	private final String cacheName;
	private long cacheSize;
	private int cacheHitRatio;
	private long cacheHitRate;
	private long cacheMissRate;
	private long cachePutRate;
	
	public CacheStats(String cacheName) {
		super();
		this.cacheName = cacheName;
	}

	public String getCacheName() {
		return cacheName;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getCacheHitRatio() {
		return cacheHitRatio;
	}

	public void setCacheHitRatio(int cacheHitRatio) {
		this.cacheHitRatio = cacheHitRatio;
	}

	public long getCacheHitRate() {
		return cacheHitRate;
	}

	public void setCacheHitRate(long cacheHitRate) {
		this.cacheHitRate = cacheHitRate;
	}

	public long getCacheMissRate() {
		return cacheMissRate;
	}

	public void setCacheMissRate(long cacheMissRate) {
		this.cacheMissRate = cacheMissRate;
	}

	public long getCachePutRate() {
		return cachePutRate;
	}

	public void setCachePutRate(long cachePutRate) {
		this.cachePutRate = cachePutRate;
	}
}
