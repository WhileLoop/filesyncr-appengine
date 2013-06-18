package org.buildndeploy.client.ui.file;

import static org.buildndeploy.client.controller.AppController.ignoredCallback;

import org.buildndeploy.client.controller.AppController;
import org.buildndeploy.client.js.Native;
import org.buildndeploy.client.model.BlobInfoJS;
import org.buildndeploy.client.resource.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FileIcon extends Composite  implements HasMouseMoveHandlers, HasMouseDownHandlers, HasMouseOverHandlers, HasFocusHandlers, HasAllTouchHandlers {

	private static FileIconUiBinder uiBinder = GWT.create(FileIconUiBinder.class);

	interface FileIconUiBinder extends UiBinder<Widget, FileIcon> {}
		
	@UiField
	FlowPanel innerPanel;
	
	@UiField
	Label filename;
	
	@UiField
	public SimplePanel 
	delete,
	info,
	download;

	private boolean clicked = false;

	/** Each FileIcon encapsulates a {@link FileDetail} */
	public BlobInfoJS fileDetail;
	
	public BlobInfoJS getFileDetail() {
		return fileDetail;
	}
	
	private FileIcon INSTANCE;
	private Timer infoTimer;
	private Timer deleteTimer;
	private Timer downloadTimer;
	private DeleteCallback deleteCallback;
	
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	public void download() {
		String url = ("/serve?blob-key=" + fileDetail.getBlobKey());
		Anchor a = new Anchor(url);
		a.setHref(url);
		RootPanel.get().add(a);
		Native.click(a.getElement());
		a.removeFromParent();
	}
	
	// Protected so only IconPanel can create
	protected FileIcon(final BlobInfoJS x) {
		initWidget(uiBinder.createAndBindUi(this));
		this.fileDetail = x;
		

		INSTANCE = this;
		// TODO uihandlers
		this.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				System.out.println("clicked");
				clicked = !clicked;
				fadeButtons(clicked);
				event.stopPropagation();
			}
		}, ClickEvent.getType());
	
		// Create name label
		filename.setText(x.getFilename());
		
		// Delete button
		delete.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Delete?")) {
					AppController.getService().deleteBlob(x.getBlobKey(), ignoredCallback);
					delete(true);
					event.stopPropagation();
					// TODO investigate alert box repeat wierdness
				}
			}
		}, ClickEvent.getType());
		
		// Info button
		info.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				INSTANCE.showDetail();
			}
		}, ClickEvent.getType());
		
		// Create Download button
		download.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Download?")) {
					String url = ("/serve?blob-key=" + x.getBlobKey());
					Anchor a = new Anchor(url);
					a.setHref(url);
					RootPanel.get().add(a);
					Native.click(a.getElement());
					a.removeFromParent();
					event.stopPropagation();
				}
			}
		}, ClickEvent.getType());
		
		innerPanel.addStyleName(getImage());
		
		
	}

	public void showDetail() {
		new DetailPopup(fileDetail);
	}

	public static FileIcon createDummyFileIcon() {
		BlobInfoJS x = JsonUtils.safeEval("{\"blobKey\":\"cVyMM3e_UpW8ss2GwM8iVw\",\"content_type\":\"text/plain\",\"creation\":\"Sun Jun 09 07:46:40 UTC 2013\",\"filename\":\"example.txt\",\"md5_hash\":\"d41d8cd98f00b204e9800998ecf8427e\",\"size\":\"0\"}");
		return new FileIcon(x);
	}
	
	// ========================================================================= //
	//			PRIVATE METHODS												 	 //
	// ========================================================================= //
	
	private String getImage() {
		int keyHash = fileDetail.getBlobKey().hashCode();
		int hashSwitch = keyHash % 7;
		if (hashSwitch < 0) {
			hashSwitch = -hashSwitch;
		}
		switch(hashSwitch) {
		case 0:	return Resources.INSTANCE.css().file0();
		case 1: return Resources.INSTANCE.css().file1();
		case 2: return Resources.INSTANCE.css().file2();
		case 3: return Resources.INSTANCE.css().file3();
		case 4: return Resources.INSTANCE.css().file4();
		case 5: return Resources.INSTANCE.css().file5();
		case 6: return Resources.INSTANCE.css().file6();
		}
		return Resources.INSTANCE.css().file6();
	}
	
	private void fadeButtons(boolean fadeDirection) {
		if (fadeDirection) {
			delete.addStyleName("fadesIn");
			download.addStyleName("fadesIn");
			info.addStyleName("fadesIn");
			
			delete.getElement().getStyle().setDisplay(Display.INLINE);
			download.getElement().getStyle().setDisplay(Display.INLINE);
			info.getElement().getStyle().setDisplay(Display.INLINE);
			
			// Will throw NPE first time
			try {
				infoTimer.cancel();
				deleteTimer.cancel();
				downloadTimer.cancel();
			} catch (Exception e) {
				;
			}
			delete.removeStyleName("fadesOut");
			download.removeStyleName("fadesOut");
			info.removeStyleName("fadesOut");
		} else {
			delete.addStyleName("fadesOut");
			download.addStyleName("fadesOut");
			info.addStyleName("fadesOut");

			delete.removeStyleName("fadesIn");
			download.removeStyleName("fadesIn");
			info.removeStyleName("fadesIn");
			
			infoTimer = new Timer() {
				
				@Override
				public void run() {
					info.getElement().getStyle().setDisplay(Display.NONE);
				}
			};
			
			infoTimer.schedule(1000);
			
			downloadTimer = new Timer() {
				
				@Override
				public void run() {
					download.getElement().getStyle().setDisplay(Display.NONE);
				}
			};
			
			downloadTimer.schedule(1000);
			
			deleteTimer = new Timer() {
				
				@Override
				public void run() {
					delete.getElement().getStyle().setDisplay(Display.NONE);
				}
			};
			
			deleteTimer.schedule(1000);
		}
	}
	
	// ========================================================================= //
	//			CALLBACK SETTERS											 	 //
	// ========================================================================= //
	
	public void setOnDelete(DeleteCallback callback) {
		deleteCallback = callback;
	}
		
	// ========================================================================= //
	//			CALLBACK INTERFACES											 	 //
	// ========================================================================= //
	
	public static abstract class DeleteCallback {
		public abstract void onDelete();
	}
	
	public void delete(boolean b) {
		
		if (b) {
			INSTANCE.addStyleName("fadesOut");
			new Timer() {

				@Override
				public void run() {
					INSTANCE.removeFromParent();
					// Callback must fire after widget has been removed. Ideally this should trigger after onUnload event // TODO?
					if (deleteCallback != null) {
						deleteCallback.onDelete();
					}
				}
			}.schedule(1000);
		} else {
			INSTANCE.removeFromParent();
			// Callback must fire after widget has been removed. Ideally this should trigger after onUnload event // TODO?
			if (deleteCallback != null) {
				deleteCallback.onDelete();
			}
		}
	}
	
	// Focus
	
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addDomHandler(handler, FocusEvent.getType());
	}

	// Mouse
	
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}
	
	// Touch

	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return addDomHandler(handler, TouchCancelEvent.getType());
	}

	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}

}
