package org.terracotta.utils.jmxclient;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.tc.operatorevent.TerracottaOperatorEvent;

public class TCEventListener implements NotificationListener{
	
	//private LinkedList<String> eventQueue = new LinkedList<String>();
	private String events = new String("");
	
	public void handleNotification(Notification notification, Object handback) {
		
		System.out.println("Notification: " + notification);
		
		
		TerracottaOperatorEvent event = (TerracottaOperatorEvent) notification.getSource();
		//System.out.println(event.getEventType());
		
		if (event.getEventSubsystemAsString().equals("CLUSTER_TOPOLOGY")) {
			
			//TODO: finish this piece to map the "ClientID" to the actual node/port 
			
			String msg = event.getEventMessage();
			String[] msgTokens = msg.split(" ");
			if (msgTokens[1].startsWith("ClientID")) {
				// get the hostname/port id string 
				String l1NodeID = new String();
				
				
				// try to do somehting like in buildL1Map()...
				
			} else {

			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(event.getEventTime());
			System.out.println("Event Time is:" + event.getEventTime());
			sb.append(",");
			sb.append(event.getEventTypeAsString());
			System.out.println("Event Type is:" + event.getEventTypeAsString());
			sb.append(",");
			sb.append(event.getEventSubsystemAsString());
			System.out.println("Event Subsystem as string is:" + event.getEventSubsystemAsString());
			sb.append(",");
			sb.append(event.getNodeName());
			System.out.println("Event Node name is:" + event.getNodeName());
			sb.append(",");
			sb.append(event.getEventMessage());
			System.out.println("Event Message is:" + event.getEventMessage());
			
		}
	
	}
	
	public String getL1Events() {
		String ret = events;
		events = "";
		return ret;
	}
}