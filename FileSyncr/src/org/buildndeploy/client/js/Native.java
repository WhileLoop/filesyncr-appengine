package org.buildndeploy.client.js;

import com.google.gwt.dom.client.Element;

public class Native {
	
	public static native void log(String s) /*-{
	  console.log(s);
	}-*/;
	
	public static native void click(Element elem) /*-{
		elem.click();
	}-*/;
	


	
	public static native int countFiles(Element fileInput) /*-{
		 
		// files is a FileList object (similar to NodeList)
		return fileInput.files.length;
	}-*/;
	

	
}
