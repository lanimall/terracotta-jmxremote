package org.terracotta.utils.jmxclient.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class L2ProcessInfo {
	private static final DateFormat DEFAULT_DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy HHmmss");

	private String nodeName;
	private String stripeName;
	private String hostName;
	private String hostAddress;
	private int hostPortJmxConnect;
	private int hostPortL1sConnect;
	private int hostPortL2sConnect;
	private Date startTime;
	
	public L2ProcessInfo() {
		super();
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	public int getHostPortJmxConnect() {
		return hostPortJmxConnect;
	}

	public void setHostPortJmxConnect(int hostPortJmxConnect) {
		this.hostPortJmxConnect = hostPortJmxConnect;
	}

	public int getHostPortL1sConnect() {
		return hostPortL1sConnect;
	}

	public void setHostPortL1sConnect(int hostPortL1sConnect) {
		this.hostPortL1sConnect = hostPortL1sConnect;
	}

	public int getHostPortL2sConnect() {
		return hostPortL2sConnect;
	}

	public void setHostPortL2sConnect(int hostPortL2sConnect) {
		this.hostPortL2sConnect = hostPortL2sConnect;
	}

	public String getStripeName() {
		return stripeName;
	}

	public void setStripeName(String stripeName) {
		this.stripeName = stripeName;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public String getStartTimeString() {
		return getStartTimeString(null);
	}
	
	public String getStartTimeString(DateFormat dtformat) {
		if(null == startTime)
			return "N/A";
		
		return (null != dtformat)?dtformat.format(startTime):DEFAULT_DATEFORMAT.format(startTime);
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public String getServerInfoSummary(){
		return String.format("%s(%s)-%s:%d", 
				getNodeName(),
				getStripeName(),
				getHostAddress(),
				getHostPortJmxConnect()
				);
	}
}
