package org.terracotta.utils.jmxclient.beans;

public class L2ClientID {
	private final String clientId;
	private final String remoteAddress;
	private String nodeId;
	private String channelId;
	private boolean tunneledBeansRegistered;
	
	public L2ClientID(String clientId, String remoteAddress) {
		this(clientId, remoteAddress, null, null);
	}

	public L2ClientID(String clientId, String remoteAddress, String nodeId, String channelId) {
		super();
		this.clientId = clientId;
		this.remoteAddress = remoteAddress;
		this.nodeId = nodeId;
		this.channelId = channelId;
	}

	public L2ClientID(String clientId, String remoteAddress, String nodeId, String channelId, boolean tunneledBeansRegistered) {
		super();
		this.clientId = clientId;
		this.remoteAddress = remoteAddress;
		this.nodeId = nodeId;
		this.channelId = channelId;
		this.tunneledBeansRegistered = tunneledBeansRegistered;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public boolean isTunneledBeansRegistered() {
		return tunneledBeansRegistered;
	}

	public void setTunneledBeansRegistered(boolean tunneledBeansRegistered) {
		this.tunneledBeansRegistered = tunneledBeansRegistered;
	}
}
