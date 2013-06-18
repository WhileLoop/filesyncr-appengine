package org.buildndeploy.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InitBundle implements IsSerializable {
	
	private String blobstoreUrl;
	private String channelToken;
	private String seed;
	private boolean accessLevel;
	private String filesJSON;
	
	public InitBundle () {
		; // Empty
	}

	public void setBlobstoreUrl(String s) {
		this.blobstoreUrl = s;
	}
	
	public void setChannelToken(String s) {
		this.channelToken = s;
	}
	
	public void setFiles(String files) {
		this.filesJSON = files;
	}
	
	public void setAccessLevel(boolean b) {
		this.accessLevel = b;
	}
	
	public void setClientToken(String s) {
		seed = s;
	}
	
	public String getBlobstoreUrl() {
		return blobstoreUrl;
	}
	
	public String getChannelToken() {
		return channelToken;
	}
	
	public boolean getAccessLevel() {
		return accessLevel;
	}
	
	public String getFiles() {
		return filesJSON;
	}
	
	public String getSessionId() {
		return seed;
	}
}
