package org.terracotta.utils.jmxclient.utils;

public enum L2RuntimeState
{
	ACTIVE( "ACTIVE", 0),
	PASSIVE_STANDBY( "PASSIVE_STANDBY", 2),
	PASSIVE_UNINITIALIZED( "PASSIVE_UNINITIALIZED", 4),
	ERROR( "ERROR", 8);
    
    final private String stateName;
    final private int stateIntValue;
    
	private L2RuntimeState(String stateName, int stateIntValue) {
		this.stateName = stateName;
		this.stateIntValue = stateIntValue;
	}

	public String getStateName() {
		return stateName;
	}

	public int getStateIntValue() {
		return stateIntValue;
	}
	
	public static L2RuntimeState parse(String value) {
        if(value == null)
            throw new IllegalArgumentException("Cannot derive a L2RuntimeState type from a null value.");
        
        for(L2RuntimeState v : values())
            if(value.equalsIgnoreCase(v.getStateName())) return v;
        
        throw new IllegalArgumentException(String.format("Could not find a valid L2RuntimeState type from input value [%s]", value));
    }
}
