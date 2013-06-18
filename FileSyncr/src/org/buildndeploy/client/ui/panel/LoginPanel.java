package org.buildndeploy.client.ui.panel;

import org.buildndeploy.client.controller.AppController;
import org.buildndeploy.shared.model.InitBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends PopupPanel {
	
	private static LoginPanelUiBinder uiBinder = GWT.create(LoginPanelUiBinder.class);

	interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {}
	
	interface UiStyle extends CssResource {
		String greenPanel();
		String darkGlass();
	}
	
	@UiField UiStyle style;
	
	@UiField
	InputElement 
	usernameInput,
	secretKeyInput;
	
	@UiField
	Button loginButton;
	
	private LoginPanel instance;
	
	private HandlerRegistration r;
	
	public LoginPanel() {
		super(false, true);
		instance = this;
		this.getElement().getStyle().setWidth(80, Unit.PCT);
		this.getElement().getStyle().setProperty("maxWidth", "540px");
		setWidget(uiBinder.createAndBindUi(this));
		this.setStyleName(style.greenPanel());
//		setGlassStyleName("darkGlass");
		setGlassStyleName(style.darkGlass());
		setGlassEnabled(true);
		loginButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String username = usernameInput.getValue();
				if (username.equals("")) {
					username = "Anonymous";
				}
				
				String secretKey = secretKeyInput.getValue();
				instance.hide();
				AppController.getService().getInitBundle(username, secretKey, new AsyncCallback<InitBundle>() {
					
					@Override
					public void onSuccess(InitBundle result) {
						signalLoginEvent(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						System.out.println("init user failed");
					}
				});
			}
		});
	}
	
	@Override
	public void onLoad() {
		System.out.println("a");
		r = Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent arg0) {
				instance.center();
			}
		});
	}
	
	@Override
	public void onUnload() {
		System.out.println("b");
		r.removeHandler();
	}
	
	// ========================================================================= //
	//			LOGIN CALLBACK												 	 //
	// ========================================================================= //
	
	public static LoginCallback loginCallback;
	
	public void setLoginCallback(LoginCallback callback) {
		loginCallback = callback;
	}
	
	public static abstract class LoginCallback {
		public abstract void onLogin(InitBundle b);
	}
	
	public void signalLoginEvent(InitBundle b) {
		if (loginCallback != null)
			loginCallback.onLogin(b);
	}

}
