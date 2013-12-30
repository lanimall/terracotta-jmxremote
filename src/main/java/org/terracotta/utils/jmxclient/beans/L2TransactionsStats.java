package org.terracotta.utils.jmxclient.beans;

public class L2TransactionsStats {
	private long transactionRate;
	private long onHeapFaultRate;
	private long onHeapFlushRate;
	private long offHeapFaultRate;
	private long offHeapFlushRate;
	private long l2DiskFaultRate;
	private long globalLockRecallRate;
	
	public L2TransactionsStats() {
		super();
	}

	public long getTransactionRate() {
		return transactionRate;
	}

	public void setTransactionRate(long transactionRate) {
		this.transactionRate = transactionRate;
	}
	
	public long getOnHeapFaultRate() {
		return onHeapFaultRate;
	}

	public void setOnHeapFaultRate(long onHeapFaultRate) {
		this.onHeapFaultRate = onHeapFaultRate;
	}

	public long getOnHeapFlushRate() {
		return onHeapFlushRate;
	}

	public void setOnHeapFlushRate(long onHeapFlushRate) {
		this.onHeapFlushRate = onHeapFlushRate;
	}

	public long getOffHeapFaultRate() {
		return offHeapFaultRate;
	}

	public void setOffHeapFaultRate(long offHeapFaultRate) {
		this.offHeapFaultRate = offHeapFaultRate;
	}

	public long getOffHeapFlushRate() {
		return offHeapFlushRate;
	}

	public void setOffHeapFlushRate(long offHeapFlushRate) {
		this.offHeapFlushRate = offHeapFlushRate;
	}

	public long getL2DiskFaultRate() {
		return l2DiskFaultRate;
	}

	public void setL2DiskFaultRate(long l2DiskFaultRate) {
		this.l2DiskFaultRate = l2DiskFaultRate;
	}

	public long getGlobalLockRecallRate() {
		return globalLockRecallRate;
	}

	public void setGlobalLockRecallRate(long globalLockRecallRate) {
		this.globalLockRecallRate = globalLockRecallRate;
	}
}
