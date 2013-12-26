package org.terracotta.utils.jmxclient;

import java.util.Hashtable;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tc.cli.CommandLineBuilder;

public class TCL1JMXClient extends TCJMXClient {
	private static Logger log = LoggerFactory.getLogger(TCL1JMXClient.class);

	private final boolean useRMI;
	private final boolean useJbossPre6;

	public TCL1JMXClient(String username, String password, String host, int port, boolean useRMI, Boolean verbose, Boolean includeSizeInBytes) {
		super(username, password, host, port);
		this.useRMI = useRMI;
		this.useJbossPre6 = true;
	}
	
	protected final void initConnectionInternal() throws Exception {
		System.out.println("Establishing a new JMX Connection to " + getHostPort());
		if(useRMI){
			log.info("\nCreate an RMI connector client and connect it to the RMI connector server");

			if(useJbossPre6){
				Hashtable<String, String> env = new Hashtable<String, String>();
				env.put(Context.PROVIDER_URL, String.format("jnp://%s:%s", host, new Integer(port).toString()));
				env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
				//env.put("jnp.disableDiscovery", "true");

				if(null != username){
					env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.JndiLoginInitialContextFactory");
					env.put(Context.SECURITY_PRINCIPAL, username);
					env.put(Context.SECURITY_CREDENTIALS, (null != password)?password:"");
				} else {
					env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
				}

				Context ctx = new InitialContext(env);  
				mbs = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");
				System.out.println("Version = " + (String)mbs.getAttribute(new ObjectName("jboss.system:type=Server"), new String("Version")));
			} else {
				//using normal RMI
				String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", host, new Integer(port).toString());
				if(log.isDebugEnabled())
					log.debug(String.format("JMX URL:[%s]", url));

				Hashtable<String, Object> env = new Hashtable<String, Object>(); 
				if (username != null)  
				{  
					String[] creds = new String[2];  
					creds[0] = username;  
					creds[1] = (null != password)?password:"";  
					env.put(JMXConnector.CREDENTIALS, creds);  
				}  

				JMXServiceURL serviceUrl = new JMXServiceURL(url);
				jmxConnector = JMXConnectorFactory.connect(serviceUrl, env);
				mbs = jmxConnector.getMBeanServerConnection();
			} 
		} else {
			log.info("\nCreate an JMX connector client");
			jmxConnector = CommandLineBuilder.getJMXConnector(username, password, host, port);
			mbs = jmxConnector.getMBeanServerConnection();
		}
	}
}
