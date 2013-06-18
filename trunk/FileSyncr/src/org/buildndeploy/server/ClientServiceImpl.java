package org.buildndeploy.server;

import java.util.Collection;
import java.util.logging.Logger;

import org.buildndeploy.client.service.ClientService;
import org.buildndeploy.server.model.FileCollection;
import org.buildndeploy.server.model.__BlobInfo__;
import org.buildndeploy.server.util.BlobstoreUtil;
import org.buildndeploy.server.util.ChannelUtil;
import org.buildndeploy.server.util.FileCollectionUtil;
import org.buildndeploy.server.util.SessionUtil;
import org.buildndeploy.shared.model.Beanery;
import org.buildndeploy.shared.model.InitBundle;
import org.buildndeploy.shared.model.MessageType;
import org.buildndeploy.shared.model.MoveEvent;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

/**
 * All client to server communication except Blobstore uploads and downloads are done here.
 * Uses GWT-RPC for serialization.
 */
@SuppressWarnings("serial")
public class ClientServiceImpl extends RemoteServiceServlet implements ClientService {
	
	private static Logger log = Logger.getLogger(ClientServiceImpl.class.getName());
    
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	public InitBundle getInitBundle(String username, String secretKey) {
		
		log.fine("test");
		log.finer("test");
		log.finest("test");
		
		InitBundle b = new InitBundle();
		// Access Level
		boolean accessLevel = SessionUtil.authenticateUser(username, secretKey, getThreadLocalRequest());
		b.setAccessLevel(accessLevel);
		// Blobstore URL
		String blobstoreUrl = BlobstoreUtil.getUrl();
		b.setBlobstoreUrl(blobstoreUrl);
		// Channel Token
		// TODO seed is exposed to crafted request
		String channelToken = ChannelUtil.proccesClientId(getThreadLocalRequest(), getThreadLocalResponse(), username);
		b.setChannelToken(channelToken);
		// Initial Files
		Collection<__BlobInfo__> files = FileCollectionUtil.getFileList();
		String json = new Gson().toJson(files);
		b.setFiles(json);
		// Session Id for client side cookie
		String s = SessionUtil.getSessionId(getThreadLocalRequest());
		b.setClientToken(s);
		return b;
	}

	// ========================================================================= //
	//			FILE COLLECTION UPDATES											 //
	// ========================================================================= //
    
	/**
	 * Called by the client when it first loads or whenever it is notified of a view change.
	 * Creates a ordered list of FileDetails from the BlobKeys in the FileCollection.
	 * Ideally, the querying of the FileCollection and the BlobKeys it references could
	 * be done in one datastore transaction. TODO
	 */
//    @Override
//    public List<FileDetail> getFileCollection() {
//		return FileCollectionUtil.getFileList();
//    }
	
    /**
     * When a client deletes a file, the blob is removed, and the BlobKey is removed from the
     * FileCollection and all clients are notified of a view change. Again, clients are notified of
     * the change and respond by downloading the entire new FileCollection.
     * TODO Look into ways to delete Blobs through the Datastore/Objectify, then then blob,
     * and the blobkey entry in the FileCollection could be deleted in one transaction.
     * @return - true if successful
     */
	@Override
	public Boolean deleteBlob(String deletedBlobkey) {
		BlobstoreUtil.delete(deletedBlobkey);
		FileCollection.get().remove(deletedBlobkey).save();
		ChannelUtil.pushMessage(deletedBlobkey, MessageType.DeleteEvent);
		return true;
	}
	
	// ========================================================================= //
	//			MISC														 	 //
	// ========================================================================= //
	
	/**
	 * Pushes a chat message to all clients. Messages are XSS safe. 
	 * @return - true if successful.
	 */
	@Override
	public Boolean sendMessage(String s) {
		// TODO get from DB
		String username = SessionUtil.getUsername(getThreadLocalRequest());
		String messageString = username + ": " + s;
	    ChannelUtil.pushMessage(messageString, MessageType.ChatMessage); // Push to everyone
		return true;
	}

	@Override
	public boolean sendMoveEvent(String moveEventJson) {
		// AutoBean decode
		Beanery beanFactory = AutoBeanFactorySource.create(Beanery.class);
		MoveEvent e = AutoBeanCodex.decode(beanFactory, MoveEvent.class, moveEventJson).as();
		int toIndex = e.getTo();
		int fromIndex = e.getFrom();
		FileCollection.get().move(toIndex, fromIndex).save();
		ChannelUtil.pushMessage(moveEventJson, MessageType.MoveEvent);
		return true;
	}

}