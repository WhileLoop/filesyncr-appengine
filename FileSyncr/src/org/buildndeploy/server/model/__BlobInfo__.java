package org.buildndeploy.server.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class __BlobInfo__ {

	@Id
	String blobKey;
	String content_type;
	String creation; 	
	String filename; 	
	String md5_hash;
	String size;
	
	public __BlobInfo__() {
		
	}
	
	public void setBlobKey(String s) {
		blobKey = s;
	}
	
	public void setFilename(String s) {
		filename = s;
	}
	
	public String getBlobKey() {
		return blobKey;
	}
	
	public String getContentType() {
		return content_type;
	}
	
	public String getCreation() {
		return creation;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getSize() {
		return size;
	}
	
	public String getHash() {
		return md5_hash;
	}
	
}
