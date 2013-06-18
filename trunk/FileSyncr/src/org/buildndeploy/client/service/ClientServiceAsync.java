package org.buildndeploy.client.service;

import org.buildndeploy.shared.model.InitBundle;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClientServiceAsync {
	public void deleteBlob(String s, AsyncCallback<Boolean> callback); 
	public void sendMessage(String message, AsyncCallback<Boolean> callback);
	public void sendMoveEvent(String payload, AsyncCallback<Boolean> callback);
	public void getInitBundle(String username, String secretKey,
			AsyncCallback<InitBundle> callback);
}
