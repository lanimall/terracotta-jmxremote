package org.terracotta.utils.jmxclient.beans;

public class L2OffheapStats {
	private long offheapMaxDataSize;
	private long offheapTotalAllocatedSize;
	private long offheapMapAllocatedMemory;
	private long offheapObjectAllocatedMemory;
	
	public L2OffheapStats() {
		super();
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
