package org.terracotta.utils.jmxclient.beans;

public class CacheStats {
	private final String cacheName;
	private boolean enabled;
	private boolean statsEnabled;
	private long cacheSize;
	private long localHeapSize;
	private long localOffHeapSize;
	private long localDiskSize;
	private int cacheHitRatio;
	private long cacheHitRate;
	private long offHeapHitRate;
	private long onHeapHitRate;
	private long onDiskHitRate;
	private long cacheMissRate;
	private long onHeapMissRate;
	private long offHeapMissRate;
	private long onDiskMissRate;
	private long cachePutRate;
	private long evictionRate;
	private long expirationRate;
	private long removeRate;
	
	public CacheStats(String cacheName) {
		super();
		this.cacheName = cacheName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isStatsEnabled() {
		return statsEnabled;
	}

	public void setStatsEnabled(boolean statsEnabled) {
		this.statsEnabled = statsEnabled;
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

	public long getLocalHeapSize() {
		return localHeapSize;
	}

	public void setLocalHeapSize(long localHeapSize) {
		this.localHeapSize = localHeapSize;
	}

	public long getLocalOffHeapSize() {
		return localOffHeapSize;
	}

	public void setLocalOffHeapSize(long localOffHeapSize) {
		this.localOffHeapSize = localOffHeapSize;
	}

	public long getLocalDiskSize() {
		return localDiskSize;
	}

	public void setLocalDiskSize(long localDiskSize) {
		this.localDiskSize = localDiskSize;
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

	public long getOnDiskMissRate() {
		return onDiskMissRate;
	}

	public void setOnDiskMissRate(long onDiskMissRate) {
		this.onDiskMissRate = onDiskMissRate;
	}

	public long getCachePutRate() {
		return cachePutRate;
	}

	public void setCachePutRate(long cachePutRate) {
		this.cachePutRate = cachePutRate;
	}

	public long getOffHeapHitRate() {
		return offHeapHitRate;
	}

	public void setOffHeapHitRate(long offHeapHitRate) {
		this.offHeapHitRate = offHeapHitRate;
	}

	public long getOnHeapHitRate() {
		return onHeapHitRate;
	}

	public void setOnHeapHitRate(long onHeapHitRate) {
		this.onHeapHitRate = onHeapHitRate;
	}

	public long getOnDiskHitRate() {
		return onDiskHitRate;
	}

	public void setOnDiskHitRate(long onDiskHitRate) {
		this.onDiskHitRate = onDiskHitRate;
	}

	public long getOnHeapMissRate() {
		return onHeapMissRate;
	}

	public void setOnHeapMissRate(long onHeapMissRate) {
		this.onHeapMissRate = onHeapMissRate;
	}

	public long getOffHeapMissRate() {
		return offHeapMissRate;
	}

	public void setOffHeapMissRate(long offHeapMissRate) {
		this.offHeapMissRate = offHeapMissRate;
	}

	public long getEvictionRate() {
		return evictionRate;
	}

	public void setEvictionRate(long evictionRate) {
		this.evictionRate = evictionRate;
	}

	public long getExpirationRate() {
		return expirationRate;
	}

	public void setExpirationRate(long expirationRate) {
		this.expirationRate = expirationRate;
	}

	public long getRemoveRate() {
		return removeRate;
	}

	public void setRemoveRate(long removeRate) {
		this.removeRate = removeRate;
	}
}
