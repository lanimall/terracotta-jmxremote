package org.terracotta.utils.jmxclient.beans;

public class L2DataStats {
	private long usedHeap;
	private long maxHeap;
	private long offheapMaxDataSize;
	private long offheapTotalAllocatedSize;
	private long offheapMapAllocatedMemory;
	private long offheapObjectAllocatedMemory;
	private int liveObjectCount;
	private int cachedObjectCount;
	private long offheapObjectCachedCount;
	
	public L2DataStats() {
		super();
	}

	public long getUsedHeap() {
		return usedHeap;
	}

	public void setUsedHeap(long usedHeap) {
		this.usedHeap = usedHeap;
	}

	public long getMaxHeap() {
		return maxHeap;
	}

	public void setMaxHeap(long maxHeap) {
		this.maxHeap = maxHeap;
	}

	public int getLiveObjectCount() {
		return liveObjectCount;
	}

	public void setLiveObjectCount(int liveObjectCount) {
		this.liveObjectCount = liveObjectCount;
	}

	public int getCachedObjectCount() {
		return cachedObjectCount;
	}

	public void setCachedObjectCount(int cachedObjectCount) {
		this.cachedObjectCount = cachedObjectCount;
	}

	public long getOffheapObjectCachedCount() {
		return offheapObjectCachedCount;
	}

	public void setOffheapObjectCachedCount(long offheapObjectCachedCount) {
		this.offheapObjectCachedCount = offheapObjectCachedCount;
	}

	public long getOffheapMaxDataSize() {
		return offheapMaxDataSize;
	}

	public void setOffheapMaxDataSize(long offheapMaxDataSize) {
		this.offheapMaxDataSize = offheapMaxDataSize;
	}

	public long getOffheapTotalAllocatedSize() {
		return offheapTotalAllocatedSize;
	}

	public void setOffheapTotalAllocatedSize(long offheapTotalAllocatedSize) {
		this.offheapTotalAllocatedSize = offheapTotalAllocatedSize;
	}

	public long getOffheapMapAllocatedMemory() {
		return offheapMapAllocatedMemory;
	}

	public void setOffheapMapAllocatedMemory(long offheapMapAllocatedMemory) {
		this.offheapMapAllocatedMemory = offheapMapAllocatedMemory;
	}

	public long getOffheapObjectAllocatedMemory() {
		return offheapObjectAllocatedMemory;
	}

	public void setOffheapObjectAllocatedMemory(long offheapObjectAllocatedMemory) {
		this.offheapObjectAllocatedMemory = offheapObjectAllocatedMemory;
	}
}
