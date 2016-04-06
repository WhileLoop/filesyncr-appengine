package org.buildndeploy.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.buildndeploy.server.util.BlobstoreUtil;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * A simple servlet to download files from the Blobstore.
 */
@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(UploadServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		// Writes the file bytes into the response
		blobstoreService.serve(blobKey, res);
		String filename = BlobstoreUtil.getFilename(blobKey);
		log.info("Serving " + blobKey.getKeyString() + " as " + filename);
		res.setHeader("Content-Disposition","attachment;filename=" + filename);
	}
}
