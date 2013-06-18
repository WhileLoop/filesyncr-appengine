package org.buildndeploy.server.model;

import org.buildndeploy.server.util.ObjectifyUtil;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfTrue;

@Entity
public class ChannelState {

	@Id long seed;
	@Index String clientId;
	String chanelToken;
	long created;
	// We only care about finding active channels
	@Index(IfTrue.class) boolean active;
	String username;
	
	public ChannelState() {
		;
	}
	
	// ========================================================================= //
	//			SETTERS															 //
	// ========================================================================= //
	
	public ChannelState setSeed(long l) {
		seed = l;
		return this;
	}
	
	public ChannelState setClientId(String s) {
		clientId = s;
		return this;
	}
	
	public ChannelState setChannelToken(String s) {
		chanelToken = s;
		return this;
	}
	
	public ChannelState setCreated(long l) {
		created = l;
		return this;
	}
	
	public ChannelState setActive(boolean b) {
		active = b;
		return this;
	}
	
	public ChannelState setUsername(String s) {
		username = s;
		return this;
	}
	
	// ========================================================================= //
	//			GETTERS															 //
	// ========================================================================= //
	
	public long getSeed() {
		return seed;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getChannelToken() {
		return chanelToken;
	}
	
	public long getCreated() {
		return created;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public String getUsername() {
		return username;
	}
	
	// ========================================================================= //
	//			DATASTORE OPS													 //
	// ========================================================================= //
	
	public void save() {
		ObjectifyUtil.ofy().save().entity(this).now();
	}
	
}
