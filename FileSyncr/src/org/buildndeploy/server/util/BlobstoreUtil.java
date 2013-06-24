package org.buildndeploy.server.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobstoreUtil {
	
	private static final Logger log = Logger.getLogger(BlobstoreUtil.class.getName());
	
	/**
	 * Create a Blobstore URL which points to the upload servlet. If the servlet mapping is changed
	 * this URL must also change.
	 */
	public static String getUrl() {
		return BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/upload");
	}

	public static String getFilename(BlobKey blobKey) {
		return new BlobInfoFactory().loadBlobInfo(blobKey).getFilename();
	}
	
	/**
	 * Extracts blob keys from a request, and returns them as a flat list.
	 */
	public static List<String> processRequest(HttpServletRequest req) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobKeyMap = blobstoreService.getUploads(req);
		List<String> blobKeyList = flattenBlobKeyMap(blobKeyMap);
		return blobKeyList;
	}
	
	/**
	 * Takes a Map of BlobKey lists returned by BlobstoreService.getUploads() and flattens them into one List. 
	 */
	public static List<String> flattenBlobKeyMap(Map<String, List<BlobKey>> blobMap) {
		List<String> flatList = new LinkedList<String>();
		for (List<BlobKey> l : blobMap.values()) {
			for (BlobKey b : l) {
				flatList.add(b.getKeyString());
			}
		}
		return flatList;
	}
	    
	/**
	 * Loads a list of BlobInfos from a list of blobkey Strings.
	 * Order is preserved.
	 * @param keyStrings - list of blobkeys as Strings
	 * @return - list of BlobInfos in the same order as the blob keys.
	 */
	public static List<BlobInfo> loadBlobInfos(List<String> keyStrings) {
		BlobInfoFactory infoFactory = new BlobInfoFactory();
		List<BlobInfo> blobInfos = new LinkedList<BlobInfo>();
		for (String keyString : keyStrings) {
			log.info("getting blob");
			BlobInfo blobInfo = infoFactory.loadBlobInfo(new BlobKey(keyString));
			log.info("got blob");
			if (blobInfo == null) {
				log.severe("Could not load BlobInfo from BlobKey " + keyString);
				continue;
			}
			blobInfos.add(blobInfo);
		}
		return blobInfos;
	}

	/**
	 * Deletes a Blob from the Blobstore.
	 * @param s - the Blob Key of the Blob to delete.
	 */
	public static void delete(String s) {
		BlobstoreServiceFactory.getBlobstoreService().delete(new BlobKey(s));
	}
}
