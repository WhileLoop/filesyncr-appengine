package org.buildndeploy.server.util;

import java.util.Collection;

import org.buildndeploy.server.model.FileCollection;
import org.buildndeploy.server.model.__BlobInfo__;

/**
 * A set of helper methods for manipulating the FileCollection entity.
 */
public class FileCollectionUtil {

//	private static Logger log = Logger.getLogger(FileCollectionUtil.class.getName());

		public static Collection<__BlobInfo__> getFileList() {
			return FileCollection.get().getBlobInfos();
	}
	
}
