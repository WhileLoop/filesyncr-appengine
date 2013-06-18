package org.buildndeploy.client.js;

import java.util.LinkedList;
import java.util.List;

import org.buildndeploy.shared.model.MessageType;

public class ClientChannel {
	
	public ClientChannel(String channelKey) {
		System.out.println("trying to join " + channelKey);
		
		this.setOnOpen(new OpenCallback() {
			
			@Override
			public void onOpen() {
				System.out.println("open");
			}
		});
		
		this.setOnClose(new CloseCallback() {
			
			@Override
			public void onClose() {
				System.out.println("close");
			}
		});
		
		this.setOnError(new ErrorCallback() {
			
			@Override
			public void onError(int i, String s) {
				System.out.println("error " + i + " " + s); // TODO handle timeout
			}
		});
		join(channelKey);
	}
	
	// ========================================================================= //
	//			PRIVATE METHODS												 	 //
	// ========================================================================= //
    
    public native void join(String channelKey) /*-{
        var channel = new $wnd.goog.appengine.Channel(channelKey);
        var socket = channel.open();
        var self = this;

        socket.onmessage = function(evt) {
            self.@org.buildndeploy.client.js.ClientChannel::onMessage(Ljava/lang/String;)(evt.data);
        };

        socket.onopen = function() {
             self.@org.buildndeploy.client.js.ClientChannel::onOpen()();
        };

        socket.onerror = function(error) {
             self.@org.buildndeploy.client.js.ClientChannel::onError(ILjava/lang/String;)(error.code, error.description);
        };

        socket.onclose = function() {
             self.@org.buildndeploy.client.js.ClientChannel::onClose()();
        }; 
        
    }-*/;

	// ========================================================================= //
	//			CALLBACK OBJECTS											 	 //
	// ========================================================================= //
	
	private OpenCallback openCallback;
	private List<MessageCallback> messageCallbacks = new LinkedList<MessageCallback>();
	private ErrorCallback errorCallback;
	private CloseCallback closeCallback;
	
	// ========================================================================= //
	//			CALLBACK SETTERS											 	 //
	// ========================================================================= //
	
	public void setOnOpen(OpenCallback callback) {
		openCallback = callback;
	}
	
	public void addMessageHandler(MessageCallback callback) {
		messageCallbacks.add(callback);
	}
	
	public void setOnError(ErrorCallback callback) {
		errorCallback = callback;
	}
	
	public void setOnClose(CloseCallback callback) {
		closeCallback = callback;
	}
	
	// ========================================================================= //
	//			CALLBACK INTERFACES											 	 //
	// ========================================================================= //
	
	public static abstract class OpenCallback {
		public abstract void onOpen();
	}

	public static abstract class MessageCallback {
		public abstract void onMessage(String s, MessageType t);
	}
	
	public static abstract class ErrorCallback {
		public abstract void onError(int i, String s);
	}
	
	public static abstract class CloseCallback {
		public abstract void onClose();
	}
	
	// ========================================================================= //
	//			CALLBACK BRIDGE												 	 //
	// ========================================================================= //
	
	private void onOpen() {
		if (openCallback != null)
			openCallback.onOpen();
	}

	private void onMessage(String message) {
		if (message.contains("\r\n")) {
			message = message.replace("\r\n", "");
			System.out.println("trimmed new line");
		}
		String[] split = message.split("\\|");
		int i = Integer.parseInt(split[0]);
		MessageType t = MessageType.values()[i];
		for (MessageCallback c : messageCallbacks) {
			c.onMessage(split[1], t);
		}
    }
    
	private void onError(int i, String a) {
		if (errorCallback != null)
			errorCallback.onError(i, a);
	}
	
    private void onClose() {
    	if (closeCallback != null)
    		closeCallback.onClose();
    }

}