package org.buildndeploy.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class BlobInfoJS  extends JavaScriptObject {
	
	  protected BlobInfoJS() { }

	  public final native String getBlobKey() /*-{ return this.blobKey.blobKey; }-*/;
	  
	  public final native String getContentType() /*-{ return this.contentType; }-*/;
	  
	  public final native String getCreation() /*-{ return this.creation; }-*/;
	  
	  public final native String getFilename() /*-{ return this.filename; }-*/;
	  
	  public final native String getMd5Hash() /*-{ return this.md5Hash; }-*/;
	  
	  public final native int getSize() /*-{ return this.size; }-*/;

}
