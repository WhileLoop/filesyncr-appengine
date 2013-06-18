package org.buildndeploy.server.util;

import org.buildndeploy.server.model.ChannelConnection;
import org.buildndeploy.server.model.FileCollection;
import org.buildndeploy.server.model.__BlobInfo__;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class ObjectifyUtil {
	
    static {
//        factory().register(_ah_SESSION.class);
        factory().register(FileCollection.class);
        factory().register(__BlobInfo__.class);
        factory().register(ChannelConnection.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
    
//    public static List<_ah_SESSION> getAllSessions() {
//    	return ObjectifyService.ofy().load().type(_ah_SESSION.class).list();
//    }
}