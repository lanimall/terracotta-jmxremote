package org.terracotta.utils.jmxclient.beans;

import org.terracotta.utils.jmxclient.utils.L2RuntimeState;


public class L2RuntimeStatus {
	private L2RuntimeState state;
	private String health;
	
	public L2RuntimeStatus() {
		super();
	}

	public L2RuntimeState getState() {
		return state;
	}

	public void setState(L2RuntimeState state) {
		this.state = state;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}
}
