package org.buildndeploy.server.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.buildndeploy.server.model.ChannelConnection;
import org.buildndeploy.shared.model.MessageType;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ChannelUtil {
	// 24 * 60
	private static final int CHANNEL_TOKEN_TIMEOUT_MINUTES =  24 * 60 - 1; // One day is max
	private static final int CHANNEL_TOKEN_TIMEOUT_MILIS = CHANNEL_TOKEN_TIMEOUT_MINUTES * 60 * 1000;
	private static final long FIVE_MINUTES = 60 * 5 * 1000;
	
	private static SecureRandom rand = new SecureRandom();
	private static Logger log = Logger.getLogger(ChannelUtil.class.getName());

	// ========================================================================= //
	//				CHANNEL MANAGMENT											 //
	// ========================================================================= //
	
	public static String proccesClientId(String username) {
		long now = new Date().getTime();
		// Check for available channel
		log.info("begin query");
		ChannelConnection availableChannel = ObjectifyUtil.ofy().load()
			.type(ChannelConnection.class)
			.filter("active", false)
			.filter("expires >", now + FIVE_MINUTES)
			.first().get();
		if (availableChannel != null) {
			log.info("found available channel " + availableChannel.getChannelToken());
			availableChannel.setUsername(username).save();
			return availableChannel.getChannelToken();
		} else {
			// Generate new client id
			log.info("gen rand");
			String newClientId = new BigInteger(130, rand).toString(32);
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			// Open new channel
			log.info("duration minutes: " + CHANNEL_TOKEN_TIMEOUT_MINUTES);
			String newChannelToken = channelService.createChannel(newClientId, CHANNEL_TOKEN_TIMEOUT_MINUTES);
			// Save new entity
			long expires = new Date().getTime() + CHANNEL_TOKEN_TIMEOUT_MILIS;
			new ChannelConnection()
				.setClientId(newClientId)
				.setChannelToken(newChannelToken)
				.setExpires(expires)
				.setActive(false)
				.setUsername(username)
				.save();
			log.info("created new channel " + newChannelToken);
			log.info("from client id " + newClientId);
			log.info("expires " + new Date(expires));
			// Return token to client
			return newChannelToken;
		}
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
		List<ChannelConnection> activeChannels = ObjectifyUtil.ofy().load().type(ChannelConnection.class).filter("active", true).list();
		for (ChannelConnection channel : activeChannels) {
			String clientId = channel.getClientId();
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
