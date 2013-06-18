package org.buildndeploy.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.buildndeploy.server.model.ChannelConnection;
import org.buildndeploy.server.util.ChannelUtil;
import org.buildndeploy.server.util.ObjectifyUtil;
import org.buildndeploy.shared.model.MessageType;

@SuppressWarnings("serial")
public class DisconnectServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(DisconnectServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String clientId = ChannelUtil.parsePresence(req);
		ChannelConnection s = ObjectifyUtil.ofy().load().type(ChannelConnection.class).id(clientId).get(); 
		s.setActive(false).save();
		String username = s.getUsername();
		ChannelUtil.pushMessage(username, MessageType.DisconnectedEvent);
		log.info("User disconnected from Channel " + username);
	}
	
}
