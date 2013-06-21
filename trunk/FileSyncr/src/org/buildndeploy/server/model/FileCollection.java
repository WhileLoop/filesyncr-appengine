package org.buildndeploy.server.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.buildndeploy.server.util.ObjectifyUtil;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * This list of BlobKeys is used to track the order in which files should be displayed in the IconPanel. Any time a 
 * file is modified in the IconPanel, a new list of FileDetails, in the order they appear in the IconPanel, are sent to 
 * the server, where a new FileCollection is created from the BlobKeys of those FileDetails. When the new FileCollection
 * is saved to the datastore, it overwrites the old entity, so it acts as a Singleton. When the save happens all clients 
 * are notified that there has been a change in the FileCollection through a Channel push. Upon receiving this notification
 * the clients will make a request for the new FileCollection.
 * TODO Allow multiple FileCollections, each representing the contents of a directory. The FileCollections will then be 
 * queried by String names, which will represent the directory path of that FileCollection.
 */
@Entity
public class FileCollection implements IsSerializable {

	private static Logger log = Logger.getLogger(FileCollection.class.getName());
	
	@Id	Long id;
	/**
	 * The order of the blob keys in this list determine the order that 
	 * FileDetails are shown on the client.
	 */
	private List<String> blobKeys = new LinkedList<String>();
	
    /**
     * Get a FileCollection entity from the datastore. There should always be only one FileCollection entity in the datastore. 
     * @return - an instance of a FileCollection.
     */
	public static FileCollection get() {
		FileCollection fc = ObjectifyUtil.ofy().load().type(FileCollection.class).first().get();
		if (fc == null) {
			fc = new FileCollection();
			log.info("Created new File Collection");
		}
		return fc;
	}
	
	public FileCollection() {
		; // Ignore
	}
	
	/** @return - the number of blob keys contained within. */
	public int size() {
		return blobKeys.size();
	}

	/** @param b - the blob key string to add. */
	public void add(String b) {
//		Key<__BlobInfo__> key = Key.create(__BlobInfo__.class, b);
		blobKeys.add(b);
	}
	
	public List<String> getBlobKeys() {
		return blobKeys;
	}
	
	public FileCollection move(int to, int from) {
		String toAdd = blobKeys.get(from);
		blobKeys.add(to, toAdd);
//		// Increment to account for shift after insert
		if (from > to)
			from++;
		blobKeys.remove(from);
		return this;
	}
	
	public void save() {
		ObjectifyUtil.ofy().save().entity(this).now();
	}
	
	/**  @return - the underlying list of blob key strings.	 */
	public Collection<__BlobInfo__> getBlobInfos() {
		List<__BlobInfo__> blobs = new LinkedList<__BlobInfo__>();
		for (String s : blobKeys) {
			blobs.add(ObjectifyUtil.ofy().load().type(__BlobInfo__.class).id(s).get());
		}
		return blobs;
	}

	/**
	 * Remove a blob key string.
	 * @param s - the blob key string to remove.
	 * @return 
	 */
	public FileCollection remove(String s) {
		Key<__BlobInfo__> key = Key.create(__BlobInfo__.class, s);
		blobKeys.remove(key);
		return this;
	}

}
