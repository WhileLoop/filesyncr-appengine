package org.buildndeploy.client.controller;

import java.util.logging.Logger;

import org.buildndeploy.client.js.ClientChannel;
import org.buildndeploy.client.model.BlobInfoJS;
import org.buildndeploy.client.resource.Resources;
import org.buildndeploy.client.service.ClientService;
import org.buildndeploy.client.service.ClientServiceAsync;
import org.buildndeploy.client.ui.panel.LoginPanel;
import org.buildndeploy.client.ui.panel.LoginPanel.LoginCallback;
import org.buildndeploy.client.ui.panel.MainPanel;
import org.buildndeploy.shared.model.Beanery;
import org.buildndeploy.shared.model.InitBundle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class AppController implements EntryPoint {
	
	private static Logger log = Logger.getLogger(AppController.class.getName());

	static { Resources.INSTANCE.css().ensureInjected(); }
	
	// ========================================================================= //
	//			GLOBAL SINGLETONS AND GETTERS									 //
	// ========================================================================= //

	private static Beanery factory = GWT.create(Beanery.class);
	public static Beanery getBeanFactory() { return factory; }
	
	private static ClientServiceAsync service = GWT.create(ClientService.class);
	public static ClientServiceAsync getService() { return service; }
	
	private static ClientChannel channel;
	public static ClientChannel getChannel() { return channel; }
	
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	private MainPanel mainPanel;

	public void onModuleLoad() {
		// Trap unhandled errors
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable unhandled) {
				Throwable unwrapped = unwrap(unhandled);
				unwrapped.printStackTrace();
				// TODO log on server
			}
		});
		
		
//		log.severe("module load");
		
		// Show the Login Panel
		LoginPanel l = new LoginPanel();
		l.center();
		l.setLoginCallback(new LoginCallback() {
			
			@Override
			public void onLogin(InitBundle b) {
				System.out.println(b.getChannelToken() + " on client");
				channel = new ClientChannel(b.getChannelToken());
				JsArray<BlobInfoJS> files = JsonUtils.safeEval(b.getFiles());
				new FileController(b.getBlobstoreUrl(), files, mainPanel);
				new ChatController(mainPanel.chatPanel);
			}
		});
		
		// Initilize UI
		mainPanel = new MainPanel();
		mainPanel.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					signalUiLoaded();
			}
		});
		RootPanel.get().add(mainPanel);
	}

	// ========================================================================= //
	//			PRIVATE METHODS												 	 //
	// ========================================================================= //
	
	private static void signalUiLoaded() {
		DOM.getElementById("loading").addClassName("fadesOut");
		new Timer() {
			
			@Override
			public void run() {
				DOM.getElementById("loading").removeFromParent();
			}
		}.schedule(1000);
	}
	
	/**
	 * Some code to "unwrap" GWT Umbrella exceptions. More info here: 
	 * http://www.summa-tech.com/blog/2012/06/11/7-tips-for-exception-handling-in-gwt/
	 * @param e	A GWT {@link UmbrellaException}
	 * @return A {@link Throwable} representing an unwrapped {@link UmbrellaException}
	 */
	private Throwable unwrap(Throwable e) {
		if(e instanceof UmbrellaException) {
			UmbrellaException ue = (UmbrellaException) e;
			if(ue.getCauses().size() == 1) {
				return unwrap(ue.getCauses().iterator().next());
			}
		}
		return e;
	}
	
	public static AsyncCallback<Boolean> ignoredCallback = new AsyncCallback<Boolean>() {

		@Override
		public void onFailure(Throwable caught) {
			// Ignore // TODO log on server?
		}

		@Override
		public void onSuccess(Boolean result) {
			// Ignore
		}
	};
	
}
