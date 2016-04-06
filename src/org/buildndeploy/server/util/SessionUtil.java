package org.buildndeploy.server.util;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtil {
	
	private static final long SESSION_TIMEOUT_MILIS = 1000 * 60 * 60 * 2; // 2 Hours
	private static final String SECRET_KEY = "Q";
	private static final String USERNAME = "username";
	private static final String ACL = "acl";
	
	private static Logger log = Logger.getLogger(SessionUtil.class.getName());
	
	// ========================================================================= //
	//			SESSION CREATION / AUTHENTICATION								 //
	// ========================================================================= //
	
	public enum AccessLevel {
		READ_ONLY,
		READ_WRITE;
	}
	
	/**
	 * Creates a new Session or returns an existing Session. Old Sessions will be invalidated.
	 */
	public static HttpSession getSession(HttpServletRequest req) {
		HttpSession oldSession = req.getSession(false);
		if (oldSession != null) {
			long created = oldSession.getCreationTime();
			log.info("was created " + new Date(created));
			long now = new Date().getTime();
			if (created + SESSION_TIMEOUT_MILIS < now) { 
				oldSession.invalidate(); 	// TODO does this do anything?
				log.info("invalidating sesssion " + oldSession.getId());
			} else {
				log.info("returning sesssion " + oldSession.getId());
				return oldSession;
			}
		}
		// If oldSession is null or was invalidated.
		HttpSession newSession = req.getSession(true);
		log.info("created new session " + newSession.getId());
		return newSession;
	}

	/**
	 * Set the username and access level attributes on the Session of the provided request.
	 * @param username - the username of the user. Note that the username is simply a user choosen label and does not persist.
	 * @param secretKey - the secret key entered by the user on client side. Will be comapared to the hard coded password.
	 * @param req - the request from which the Session should be extracted. 
	 * @return - The access level granted to the user.
	 */
	public static boolean authenticateUser(String username, String secretKey, HttpServletRequest req) {
		HttpSession session = SessionUtil.getSession(req);
		session.setAttribute(USERNAME, username);
		if (secretKey.equals(SECRET_KEY)) {
			log.info("granted read/write to " + username);
			session.setAttribute(ACL, AccessLevel.READ_WRITE);
			return true;
		} else {
			log.info("granted read only to " + username);
			session.setAttribute(ACL, AccessLevel.READ_ONLY);
			return false;
		}
	}
	
	// ========================================================================= //
	//			GETTERS															 //
	// ========================================================================= //
	
	/**
	 * Gets the username attribute from the Session of the provided request.
	 */
	public static String getUsername(HttpServletRequest req) {
		HttpSession session = getSession(req);
		return (String) session.getAttribute(USERNAME);
	}

	/**
	 * Gets the username from the Session of the request.
	 */
	public static String getSessionId(HttpServletRequest req) {
		return getSession(req).getId();
	}
	
//	public static String getChannelToken(HttpServletRequest req) {
//		HttpSession session = getSession(req);
//		log.info("Getting token from session id " + session.getId());
//		String channelToken = (String) session.getAttribute(CHANNEL_TOKEN);
//		if (channelToken != null) {
//			long channelCreated = (Long) session.getAttribute(CHANNEL_CREATED);
//			System.out.println("Found token created " + new Date(channelCreated));
//			long now = new Date().getTime();
//			if (channelCreated + TIMEOUT_MILIS > now) {
//				// Channel has not expired
//				System.out.println("Token not expired, returning " + channelToken);
//				return channelToken;
//			}
//		}
//		// Channel is null or has expired
//		String newToken = ChannelUtil.createConnectionToken(session.getId());
//		
////		String sessionId = getSessionId(req);
////		_ah_SESSION x = ObjectifyUtil.ofy().load().type(_ah_SESSION.class).id("_ahs" + sessionId).get();
////		x.setTokenId("STRING");
////		Key<_ah_SESSION> y = ObjectifyUtil.ofy().save().entity(x).now();
//		session.setAttribute(CHANNEL_TOKEN, newToken);
//		long now = new Date().getTime();
//		session.setAttribute(CHANNEL_CREATED, now);
//		log.info("Was expired or null, created " + newToken);
//		return newToken;
//	}
	
	// ========================================================================= //
	//			CHANNEL FAN-OUT													 //
	// ========================================================================= //
	
//	public static List<String> getOpenChannels() {
//		return getOpenChannels("");
//	}
//	
	/**
	 * Queries the datastore for all Session Ids. These Ids are used by the ChannelManager
	 * to push notifications to all clients. Channels are opened and tracked using Session Ids.
	 * @return - A list of Session Ids
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
//	public static List<String> getOpenChannels(String senderId) {
//		List<String> validSessionIds = new LinkedList<String>();
//		// Get a list of all the Sessions
//		List<_ah_SESSION> sessions = ObjectifyUtil.getAllSessions();
//		log.info("Found session count: " + sessions.size());
//		// Check if they have an open channel
//		for (_ah_SESSION s : sessions) {
//			String sessionKey = s.name;
//			// Strip _ahs from key to get actual Session Id
//			String sessionId = sessionKey.substring(4); 
//			log.info("Checking channel state of " + sessionId);
//			if (senderId.equals(sessionId)) {
//				log.info("     Skipping sender");
//				continue;
//			}
//			// Convert values blob into HashMap
//			HashMap<String, Object> sessionData = getSessionMap(s._values);
//			if (isChannelOpen(sessionData)) {
//				validSessionIds.add(sessionId);
//				log.info("Channel is open");
//			} else {
//				// TODO delete session?
//				log.info("Channel is closed");
//			}
//		}
//		return validSessionIds;
//	}
	
	public static String getUsernameFromClientId(String notifyingChannelId) {
//		// Get a list of all the Sessions
//		String key = "_ahs" + notifyingChannelId;
//		_ah_SESSION s = ObjectifyUtil.ofy().load().type(_ah_SESSION.class).id(key).get();
//		// Convert values blob into HashMap
//		HashMap<String, Object> sessionData = getSessionMap(s._values);
//		String username = (String) sessionData.get(USERNAME);
		return "unknown";
	}
	
	// ========================================================================= //
	//			PRIVATE METHODS													 //
	// ========================================================================= //
	
//	private static boolean isChannelOpen(HashMap<String, Object> sessionData) {
//		// Check if channel token exists
//		String channelToken = (String) sessionData.get(CHANNEL_TOKEN);
//		if (channelToken != null) {
//			// Token exists, check if it has expired
//			long channelCreated = (Long) sessionData.get(CHANNEL_CREATED);
//			log.info("created " + new Date(channelCreated));
//			long now = new Date().getTime();
//			if (channelCreated + TIMEOUT_MILIS > now) {
//				// Channel has not expired
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	@SuppressWarnings("unchecked")
//	private static HashMap<String, Object> getSessionMap(byte[] values) {
//		try {
//			ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(values));
//			HashMap<String, Object> actual = (HashMap<String, Object>) objIn.readObject();
//			objIn.close();
//			return actual;
//		} catch (Exception e) {
//			e.printStackTrace();
//			// Return a empty hashmap to prevent NPEs
//			return new HashMap<String, Object>();
//		}
//	}
	
}
