package org.buildndeploy.client.service;

import org.buildndeploy.shared.model.InitBundle;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface ClientService extends RemoteService {
	public Boolean deleteBlob(String s); 
	public Boolean sendMessage(String s);
	public boolean sendMoveEvent(String payload);
	public InitBundle getInitBundle(String username, String secretKey);
}
