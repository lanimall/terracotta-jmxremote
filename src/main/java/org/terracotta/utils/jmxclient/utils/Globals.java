package org.terracotta.utils.jmxclient.utils;

public class Globals {
	public static final String LINESEPARATOR = "\n";
	public static final String PROPERTYSEPARATOR = "||";

	public static final String SAMPLEDCACHE = "net.sf.ehcache:*,type=SampledCache";
	public static final String SAMPLEDCACHEMANAGER = "net.sf.ehcache:*,type=SampledCacheManager";
	//public static final String DSOCLIENT = "org.terracotta:name=DSO,type=Terracotta Server,channelID=*";
	//public static final String DSOCLIENT = "org.terracotta:name=DSO,type=Terracotta Server";
	
	public static final long STATSENABLE_PAUSETIME = 5000L;
}
