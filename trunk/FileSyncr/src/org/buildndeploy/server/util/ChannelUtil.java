package org.buildndeploy.server.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.buildndeploy.server.model.ChannelState;
import org.buildndeploy.shared.model.MessageType;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ChannelUtil {
	
	public static final int CHANNEL_TOKEN_TIMEOUT_MINUTES = 24 * 60; // One day is max
	
	private static Logger log = Logger.getLogger(FileCollectionUtil.class.getName());

	// ========================================================================= //
	//				CHANNEL MANAGMENT											 //
	// ========================================================================= //
	
	private static SecureRandom rand = new SecureRandom();
	
	public static String proccesClientId(HttpServletRequest req, HttpServletResponse res, String username) {
		// Try to find SEED cookie
		Cookie[] cookies = req.getCookies(); // Returns null if empty
		Cookie seedCookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("SEED")) {
					seedCookie = cookies[i];
					break;
				}
			}
		}
		
		if (seedCookie != null && !seedCookie.getValue().isEmpty()) {
			// TODO check channel created date for expiration? Cookie should have expired.
			long oldSeed = Long.parseLong(seedCookie.getValue());
			ChannelState channelState = ObjectifyUtil.ofy().load().type(ChannelState.class).id(oldSeed).get();
			if (channelState == null) {
				log.severe("old seed query returned null!");
			} else {
				String oldChannelToken = channelState.getChannelToken();
				channelState.setUsername(username).save();
				return oldChannelToken;
			}
		}
		// If no SEED cookie exists or no channel could be be found create, a new channel
		// Generate seed and write back into request
		long newSeed = rand.nextLong();
		Cookie newSeedCookie = new Cookie("SEED", newSeed + "");
		newSeedCookie.setPath("/");
		newSeedCookie.setMaxAge(CHANNEL_TOKEN_TIMEOUT_MINUTES * 60);
		res.addCookie(newSeedCookie);
		String newClientId = new BigInteger(64, rand).toString(32);
		// Generate token
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String newChannelToken = channelService.createChannel(newClientId, CHANNEL_TOKEN_TIMEOUT_MINUTES);
		// Save new entity
		long created = new Date().getTime();
		new ChannelState()
			.setSeed(newSeed)
			.setClientId(newClientId)
			.setChannelToken(newChannelToken)
			.setCreated(created)
			.setActive(false) // Don't assume that a client will join the channel immediately after requesting the channel token
			.setUsername(username)
			.save();
		// Return token to client
		return newChannelToken;
	}
	
	public static String parsePresence(HttpServletRequest req) throws IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		String clientId = presence.clientId();
		return clientId;
	}
	
	// ========================================================================= //
	//				PUSH MESSAGES												 //
	// ========================================================================= //
		
	public static void pushMessage(String payload, MessageType type) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		// Find all active channels
		List<ChannelState> activeChannels = ObjectifyUtil.ofy().load().type(ChannelState.class).filter("active", true).list();
		for (ChannelState channelState : activeChannels) {
			String clientId = channelState.getClientId();
			// Payloads are not logged
			log.info("Sending message to " + clientId + " of type " + type);
			ChannelMessage msg = createMessage(clientId, type, payload);
			channelService.sendMessage(msg);
		}
	}
	
	private static ChannelMessage createMessage(String id, MessageType type, String payload) {
		String typeOrdinal = type.ordinal() + ""; // Encode enum
		return new ChannelMessage(id, typeOrdinal + "|" + payload); // Append message type and delimiter
	}

}
