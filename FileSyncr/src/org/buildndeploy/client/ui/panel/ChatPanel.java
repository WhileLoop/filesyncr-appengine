package org.buildndeploy.client.ui.panel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class ChatPanel extends FlowPanel {//TODO make composite w/ ui binder
	
	private Button send;
	private TextBox in;
	
	public ChatPanel() {
		super();
		in = new TextBox();
		in.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 13) {
					signalChatMessageEvent(in.getText());
					in.setText("");
				}
			}
		});
		in.setWidth("100%");
		in.getElement().getStyle().setProperty("maxWidth", "400px");
		
		
		send = new Button("Send");
		send.getElement().getStyle().setMarginLeft(5, Unit.PX);
		send.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				signalChatMessageEvent(in.getText());
				in.setText("");
			}
		});
		
		
		this.add(in);
		this.add(send);
	}
	
	public void handleMessage(String s) {
		this.add(new Label(s));
	}
	
	// ========================================================================= //
	//			CHAT MESSAGE CALLBACK											 //
	// ========================================================================= //
	
	public static ChatMessageCallback chatMessageCallback;
	
	public void setChatMessageCallback(ChatMessageCallback callback) {
		chatMessageCallback = callback;
	}
	
	public static abstract class ChatMessageCallback {
		public abstract void onMessage(String s);
	}
	
	public void signalChatMessageEvent(String s) {
		if (chatMessageCallback != null)
			chatMessageCallback.onMessage(s);
	}

}
