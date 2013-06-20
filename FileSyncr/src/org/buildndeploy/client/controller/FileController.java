package org.buildndeploy.client.controller;

import static org.buildndeploy.client.controller.AppController.getChannel;
import static org.buildndeploy.client.controller.AppController.getService;
import static org.buildndeploy.client.controller.AppController.ignoredCallback;

import java.util.List;

import org.buildndeploy.client.js.ClientChannel.MessageCallback;
import org.buildndeploy.client.js.IntroJS;
import org.buildndeploy.client.js.Native;
import org.buildndeploy.client.model.BlobInfoJS;
import org.buildndeploy.client.ui.animation.OneTimeCssAnimator;
import org.buildndeploy.client.ui.file.FileIcon;
import org.buildndeploy.client.ui.file.IconPanel;
import org.buildndeploy.client.ui.file.IconPanel.MoveCallback;
import org.buildndeploy.client.ui.panel.MainPanel;
import org.buildndeploy.shared.model.Beanery;
import org.buildndeploy.shared.model.MessageType;
import org.buildndeploy.shared.model.MoveEvent;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class FileController {

	private MainPanel mainPanel;
	
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	public FileController(String blobUrl, JsArray<BlobInfoJS> files, final MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		initChannel();
		initUi(blobUrl, files);

		// TODO unify dnd logic?
		SimpleDropController deleteDropController = new SimpleDropController(mainPanel.trashButton) {
			
			public void onDrop(DragContext context) {
				FileIcon f = (FileIcon) context.draggable;
				f.delete(false);
				AppController.getService().deleteBlob(f.fileDetail.getBlobKey(), ignoredCallback);
				new OneTimeCssAnimator(mainPanel.trashButton, "shakes", 1000);
			}
		};
		IconPanel.dragController.registerDropController(deleteDropController);
		
		SimpleDropController downloadDropController = new SimpleDropController(mainPanel.downloadButton) {
			
			public void onDrop(DragContext context) {
				((FileIcon) context.draggable).download();
				new OneTimeCssAnimator(mainPanel.downloadButton, "shakes", 1000);
			}
		};
		IconPanel.dragController.registerDropController(downloadDropController);
		
		SimpleDropController helpDropController = new SimpleDropController(mainPanel.helpButton) {
			
			public void onDrop(DragContext context) {
				((FileIcon) context.draggable).showDetail();
				new OneTimeCssAnimator(mainPanel.helpButton, "shakes", 1000);
			}
		};
		IconPanel.dragController.registerDropController(helpDropController);
	}

	private void initChannel() {
		getChannel().addMessageHandler(new MessageCallback() {
			
			@Override
			public void onMessage(String s, MessageType t) {
				System.out.println("got message " + s);
				Beanery factory = AppController.getBeanFactory();
				IconPanel iconPanel = mainPanel.draggablePanel;
				switch(t) {
				case AddEvent:
					JsArray<BlobInfoJS> c = JsonUtils.safeEval(s);
					for (int i = 0; i < c.length(); i++) {
						BlobInfoJS x = c.get(i);
						iconPanel.add(x);
					}
				break;
				case MoveEvent:
					AutoBean<MoveEvent> moveBean = AutoBeanCodex.decode(factory, MoveEvent.class, s);
					MoveEvent moveEvent = moveBean.as();
					moveIcon(moveEvent.getBlobKey(), moveEvent.getFrom(), moveEvent.getTo());
					break;
				case DeleteEvent:
					iconPanel.delete(s);
				break;
				default:
					// Ignore
				}
			}
		});
	}
	
	private void moveIcon(String blobKey, int oldIndex, int newIndex) {
		IconPanel iconPanel = mainPanel.draggablePanel;
		List<BlobInfoJS> oldFiles = iconPanel.getFiles();
		BlobInfoJS toAdd = oldFiles.get(oldIndex);
		if (toAdd.getBlobKey().equals(blobKey)) {
			iconPanel.insert(toAdd, newIndex);
			// Increment to account for shift after insert
			if (oldIndex > newIndex)
				oldIndex++;
			iconPanel.delete(oldIndex, false);
		}
	}

	private void initUi(String blobUrl, JsArray<BlobInfoJS> files) {
		populateFilePanel(files);
		setHelpClicked();
		mainPanel.uploadButton.setAction(blobUrl);
		mainPanel.draggablePanel.setMoveCallback(new MoveCallback() {
			
			@Override
			public void onMove(MoveEvent e) {
				AutoBean<MoveEvent> autoBean = AutoBeanUtils.getAutoBean(e);
				String payload = AutoBeanCodex.encode(autoBean).getPayload();
				getService().sendMoveEvent(payload, ignoredCallback);
			}
		});
	}

	// ========================================================================= //
	//			CLICK HANDLERS												 	 //
	// ========================================================================= //
	
	@SuppressWarnings("unused")
	private void setUploadClicked() {
		// Ignore
	}
	
	private void setHelpClicked() {
		mainPanel.helpButton.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setupHelp();
			}
		}, ClickEvent.getType());
	}

	// ========================================================================= //
	//			PRIVATE METHODS												 	 //
	// ========================================================================= //
	
	private void populateFilePanel(JsArray<BlobInfoJS> c) {
		IconPanel iconPanel = mainPanel.draggablePanel;
		
		for (int i = 0; i < c.length(); i++) {
			BlobInfoJS x = c.get(i);
			iconPanel.add(x);
		}
	}
	
	// ========================================================================= //
	//			HELP LOGIC													 	 //
	// ========================================================================= //
		
		private void setupHelp() {
			final FileIcon f = mainPanel.draggablePanel.createFakeIcon();
			IntroJS.mark("Click here to upload a file.", mainPanel.uploadButton);
			IntroJS.mark("These are your uploaded files.", "top", mainPanel.draggablePanel);
			IntroJS.mark("Click here to download.","top",  f.download);
			IntroJS.mark("Click here to view file details.","top",  f.info);
			IntroJS.mark("Click here to delete.", "top", f.delete);
			IntroJS.mark("Drag files here to delete them.", "top", mainPanel.trashButton);
			IntroJS.mark("Drag files here to download them.", "top", mainPanel.downloadButton);
			IntroJS.mark("You can send chat messages to other users here.", "top", mainPanel.chatPanel);
			
			IntroJS.setOnChange(new IntroJS.ChangeCallback() {
				
				@Override
				public void onChange(Widget w, int step) {
					switch(step) {
					case 3:
						Native.click(f.getElement());
					break;
					default:
						// Do nothing
					}
				}
			});
			
			IntroJS.setOnExit(new IntroJS.ExitCallback() {

				@Override
				public void onExit() {
					IntroJS.clearAll();
					f.delete(true);
				}
			});
			
			IntroJS.startIntro();
		}

}
