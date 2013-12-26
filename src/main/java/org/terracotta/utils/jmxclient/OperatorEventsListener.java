package org.terracotta.utils.jmxclient;

import java.util.Date;
//import java.util.LinkedList;
//import java.util.Queue;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.tc.operatorevent.TerracottaOperatorEvent;

public class OperatorEventsListener implements NotificationListener {
	
	//private LinkedList<String> eventQueue = new LinkedList<String>();
	
	public OperatorEventsListener() {
	}

	public void handleNotification(Notification notification, Object handback) {
		
		System.out.println("Recieved notification");
		System.out.println(notification);
	
		StringBuffer sb = new StringBuffer();

		TerracottaOperatorEvent event = (TerracottaOperatorEvent) notification.getSource();
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
		//logger.info(sb.toString());
		
		//L2StatsDumper.clientUpdateNotification(notification);
	}
	
	
	public void logClusterUnavialableMsg(String stripeId) {
		StringBuffer sb = new StringBuffer();
		sb.append(new Date());
		sb.append(",CRITICAL,HA,");
		sb.append(stripeId);
		sb.append(",");
		sb.append("There is no active server in ");
		sb.append(stripeId);
		sb.append(". The cluster might be unavailable.");
		System.out.println(sb.toString());
		//logger.info(sb.toString());
	}
}
