package org.buildndeploy.client.js;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class IntroJS {
	
	public static ChangeCallback changeCallback;
	public static ExitCallback exitCallback;
	
	private static List<Widget> markedWidgets = new LinkedList<Widget>();

	public static int step = 1;
	
	public static native void startIntro() /*-{
		
		var intro = $wnd.introJs();
		
		intro.onchange(function(e) {
			@org.buildndeploy.client.js.IntroJS::onChange(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
		
		intro.onexit(function() {
			@org.buildndeploy.client.js.IntroJS::onExit()();
		});
		
		intro.oncomplete(function() {
			@org.buildndeploy.client.js.IntroJS::onExit()();
		});
		
		 intro.start();
	}-*/;
	
	// ========================================================================= //
	//			MARKING														 	 //
	// ========================================================================= //
	
	public static void mark(String s, Widget w) {
		markedWidgets.add(w);
		w.getElement().setAttribute("data-intro", s);
		w.getElement().setAttribute("data-step", step++ + "");
	}
	
	public static void mark(String s, String p, Widget w) {
		markedWidgets.add(w);
		w.getElement().setAttribute("data-intro", s);
		w.getElement().setAttribute("data-step", step++ + "");
		w.getElement().setAttribute("data-position", p);
	}
	
	public static void clearAll() {
		for (Widget w : markedWidgets) {
			w.getElement().removeAttribute("data-intro");
			w.getElement().removeAttribute("data-step");
			w.getElement().removeAttribute("data-position");
		}
		markedWidgets.clear();
		step = 1;
	}
	
	// ========================================================================= //
	//			NATIVE TO GWT CONVERSION									 	 //
	// ========================================================================= //
	
	public static native Element findByClass(Element e) /*-{
		var spans = e.getElementsByClassName('introjs-helperLayer');
		return spans[0];
	}-*/;
	
	private static void onChange(JavaScriptObject e) throws Exception {
		Widget w = matchWidget(e);
		String dataStep = w.getElement().getAttribute("data-step");
		int step = Integer.parseInt(dataStep);
		if (changeCallback != null)
			changeCallback.onChange(w, step);
	}
	
	private static void onExit() throws Exception {
		if (exitCallback != null)
			exitCallback.onExit();
	}

	private static Widget matchWidget(JavaScriptObject e) throws Exception {
		Element provided = Element.as(e);
		for (Widget w : markedWidgets) {
			Element compare = w.getElement();
			if (provided.equals(compare)) {
				return w;
			}
		}
		throw new Exception("Widget was not found!");
	}

	// ========================================================================= //
	//			CALLBACK SETTERS											 	 //
	// ========================================================================= //
	
	public static void setOnExit(ExitCallback callback) {
		exitCallback = callback;
	}
	
	public static void setOnChange(ChangeCallback callback) {
		changeCallback = callback;
	}
	
	// ========================================================================= //
	//			CALLBACK INTERFACES											 	 //
	// ========================================================================= //
	
	public static abstract class ChangeCallback {
		public abstract void onChange(Widget w, int step);
	}
	
	public static abstract class ExitCallback {
		public abstract void onExit();
	}
}