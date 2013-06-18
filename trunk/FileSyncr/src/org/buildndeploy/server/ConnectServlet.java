package org.buildndeploy.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.buildndeploy.server.model.ChannelState;
import org.buildndeploy.server.util.ChannelUtil;
import org.buildndeploy.server.util.ObjectifyUtil;
import org.buildndeploy.shared.model.MessageType;

@SuppressWarnings("serial")
public class ConnectServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(ConnectServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String clientId = ChannelUtil.parsePresence(req);
		// Have to query on indexed clientId property, should only be one result
		ChannelState s = ObjectifyUtil.ofy().load().type(ChannelState.class).filter("clientId", clientId).first().get(); 
		s.setActive(true).save();
		String username = s.getUsername();
		ChannelUtil.pushMessage(username, MessageType.ConnectedEvent);
		log.info("User connected to Channel " + username);
	}

}
