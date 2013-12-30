package org.terracotta.utils.jmxclient.beans;

public class L2ClientRuntimeInfo {
	private final L2ClientID clientID;
	private int liveObjectCount;
	private long objectFlushRate;
	private long objectFaultRate;
	private long pendingTransactionsCount;
	private long transactionRate;
	private long serverMapGetSizequestsCount;
	private long serverMapGetSizeRequestsRate;
	private long serverMapGetValueRequestsCount;
	private long serverMapGetValueRequestsRate;
	
	public L2ClientRuntimeInfo(L2ClientID clientID){
		this.clientID = clientID;
	}

	public L2ClientID getClientID() {
		return clientID;
	}

	public int getLiveObjectCount() {
		return liveObjectCount;
	}

	public void setLiveObjectCount(int liveObjectCount) {
		this.liveObjectCount = liveObjectCount;
	}

	public long getObjectFlushRate() {
		return objectFlushRate;
	}

	public void setObjectFlushRate(long objectFlushRate) {
		this.objectFlushRate = objectFlushRate;
	}

	public long getObjectFaultRate() {
		return objectFaultRate;
	}

	public void setObjectFaultRate(long objectFaultRate) {
		this.objectFaultRate = objectFaultRate;
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

	public long getServerMapGetSizequestsCount() {
		return serverMapGetSizequestsCount;
	}

	public void setServerMapGetSizequestsCount(long serverMapGetSizequestsCount) {
		this.serverMapGetSizequestsCount = serverMapGetSizequestsCount;
	}

	public long getServerMapGetSizeRequestsRate() {
		return serverMapGetSizeRequestsRate;
	}

	public void setServerMapGetSizeRequestsRate(long serverMapGetSizeRequestsRate) {
		this.serverMapGetSizeRequestsRate = serverMapGetSizeRequestsRate;
	}

	public long getServerMapGetValueRequestsCount() {
		return serverMapGetValueRequestsCount;
	}

	public void setServerMapGetValueRequestsCount(
			long serverMapGetValueRequestsCount) {
		this.serverMapGetValueRequestsCount = serverMapGetValueRequestsCount;
	}

	public long getServerMapGetValueRequestsRate() {
		return serverMapGetValueRequestsRate;
	}

	public void setServerMapGetValueRequestsRate(long serverMapGetValueRequestsRate) {
		this.serverMapGetValueRequestsRate = serverMapGetValueRequestsRate;
	}
}