package org.terracotta.utils.jmxclient.beans;


public class L2RuntimeStatus {
	private String role;
	private String state;
	private String health;
	private long usedHeap;
	private long maxHeap;
	
	public L2RuntimeStatus() {
		super();
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
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
}
