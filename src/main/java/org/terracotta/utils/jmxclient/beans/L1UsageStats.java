package org.terracotta.utils.jmxclient.beans;

public class L1UsageStats {
	private final String clientID;
	private String remoteAddress;
	private long objectFaultRate;
	private long objectFlushRate;
	private long pendingTransactionsCount;
	private long transactionRate;
	
	public L1UsageStats(String clientID) {
		super();
		this.clientID = clientID;
	}

	public String getClientID() {
		return clientID;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public long getObjectFaultRate() {
		return objectFaultRate;
	}

	public void setObjectFaultRate(long objectFaultRate) {
		this.objectFaultRate = objectFaultRate;
	}

	public long getObjectFlushRate() {
		return objectFlushRate;
	}

	public void setObjectFlushRate(long objectFlushRate) {
		this.objectFlushRate = objectFlushRate;
	}

	public long getPendingTransactionsCount() {
		return pendingTransactionsCount;
	}

	public void setPendingTransactionsCount(long pendingTransactionsCount) {
		this.pendingTransactionsCount = pendingTransactionsCount;
	}

	public long getTransactionRate() {
		return transactionRate;
	}

	public void setTransactionRate(long transactionRate) {
		this.transactionRate = transactionRate;
	}
}
