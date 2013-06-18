package org.buildndeploy.client.controller;

import org.buildndeploy.client.js.ClientChannel.MessageCallback;
import org.buildndeploy.client.ui.panel.ChatPanel;
import org.buildndeploy.client.ui.panel.ChatPanel.ChatMessageCallback;
import org.buildndeploy.shared.model.MessageType;
import static org.buildndeploy.client.controller.AppController.getChannel;
import static org.buildndeploy.client.controller.AppController.ignoredCallback;

public class ChatController {
	
	
	public ChatController(final ChatPanel chatPanel) {
		
		// Handle Chat messages from the server
		getChannel().addMessageHandler(new MessageCallback() {
			
			@Override
			public void onMessage(String s, MessageType t) {
				switch(t) {
				case ChatMessage:
					chatPanel.handleMessage(s);
				break;
				case ConnectedEvent:
					chatPanel.handleMessage(s + " joined");
				break;
				case DisconnectedEvent:
					chatPanel.handleMessage(s + " left");
				}
			}
		});
		
		// Send new chat messages 
		chatPanel.setChatMessageCallback(new ChatMessageCallback() {
			
			@Override
			public void onMessage(String s) {
				AppController.getService().sendMessage(s, ignoredCallback);
			}
		});

	}	

}
