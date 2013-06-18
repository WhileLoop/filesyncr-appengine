package org.buildndeploy.server.model;

import org.buildndeploy.server.util.ObjectifyUtil;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class ChannelConnection {

	// TODO private
	@Id String clientId;
	String chanelToken;
	@Index long expires;
	@Index boolean active;
	String username;
	
	public ChannelConnection() {
		;
	}
	
	// ========================================================================= //
	//			SETTERS															 //
	// ========================================================================= //
	
	public ChannelConnection setClientId(String s) {
		clientId = s;
		return this;
	}
	
	public ChannelConnection setChannelToken(String s) {
		chanelToken = s;
		return this;
	}
	
	public ChannelConnection setExpires(long l) {
		expires = l;
		return this;
	}
	
	public ChannelConnection setActive(boolean b) {
		active = b;
		return this;
	}
	
	public ChannelConnection setUsername(String s) {
		username = s;
		return this;
	}
	
	// ========================================================================= //
	//			GETTERS															 //
	// ========================================================================= //
	
	public String getClientId() {
		return clientId;
	}
	
	public String getChannelToken() {
		return chanelToken;
	}
	
	public long getExpires() {
		return expires;
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
	
	public void delete() {
		ObjectifyUtil.ofy().delete().entity(this).now();
	}
}
