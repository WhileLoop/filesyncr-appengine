package org.buildndeploy.client.ui.file;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.buildndeploy.client.controller.AppController;
import org.buildndeploy.client.model.BlobInfoJS;
import org.buildndeploy.client.ui.file.FileIcon.DeleteCallback;
import org.buildndeploy.shared.model.Beanery;
import org.buildndeploy.shared.model.MoveEvent;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An object which encapsulates a FlowPanel which contains 
 * draggable thumbnails which can be re-ordered by drag and drop.
 */
public class IconPanel extends Composite {
	
	/**
	 * There is only one, globally visible DragController, but each thumbnail
	 * has its own DropController.
	 */
	public static ImageDragController dragController;
	
	/**
	 * This empty thumbnail is used to allow users to drop images at the last 
	 * position of the flowpanel. Necessary because thumbnails insert to the 
	 * right of the thumbnail they are dropped on. Added to DOM on dragStart 
	 * and removed onDrop.
	 */
	private FocusPanel dummy;
	private ImageDropController dummydrop;
	private AbsolutePanel contentPanel = new AbsolutePanel();
	
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	// TODO grow on add?
	public IconPanel() {
		initWidget(contentPanel);
		dragController = new ImageDragController(RootPanel.get(), false);
	}
	
	// ========================================================================= //
	//			PUBLIC METHODS												 	 //
	// ========================================================================= //

	// deleteEvent
	public void delete(String blobKey) {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			FileIcon f = (FileIcon) contentPanel.getWidget(i);
			if (f.fileDetail.getBlobKey().equals(blobKey)) { // TODO use equals
				delete(i, true);
			}
		}
	}
	
	public void delete(int indexToDelete, boolean b) {
		FileIcon f = (FileIcon) contentPanel.getWidget(indexToDelete);
		f.delete(b);
	}
	
	public List<BlobInfoJS> getFiles() {
		List<BlobInfoJS> files = new LinkedList<BlobInfoJS>();
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			FileIcon f = (FileIcon) contentPanel.getWidget(i);
			BlobInfoJS d = f.fileDetail;
			files.add(d);
		}
		return files;
	}
	
	public void add(BlobInfoJS x) {
		insert(x, contentPanel.getWidgetCount());
	}
	
	public void insert(BlobInfoJS x, int index) {
		FileIcon fileToAdd = new FileIcon(x);
		final ImageDropController dropController = new ImageDropController(fileToAdd);
		fileToAdd.setOnDelete(new DeleteCallback() {
			
			@Override
			public void onDelete() {
				dragController.unregisterDropController(dropController);
			}
		});
		
		dragController.registerDropController(dropController);
		dragController.makeDraggable(fileToAdd);
		contentPanel.insert(fileToAdd, index);
		fileToAdd.addStyleName("fadesIn");
	}
	
	private int getWidgetCount() {
		return contentPanel.getWidgetCount();
	}
	
	private Widget getWidget(int index) {
		return contentPanel.getWidget(index);
	}
		
	// ========================================================================= //
	//			PRIVATE METHODS												 	 //
	// ========================================================================= //
	
	/**
	 * A new dummy object is created each time its added to panel.
	 * @return the dummy thumbnail to be added to the DOM.
	 */
	private void addDummy() {
		dummy = new FocusPanel();
		dummy.setSize("126px", "142px");
		dummy.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		dummy.addStyleName("FileIcon"); // TODO
		dummy.addStyleName("dummy");
		contentPanel.add(dummy);
		dummydrop = new ImageDropController(dummy);
		dragController.registerDropController(dummydrop);
		dragController.makeDraggable(dummy);
	}
	
	private void removeDummy() {
		if (dummy != null) {
			dummy.removeFromParent();
			dragController.unregisterDropController(dummydrop);
			dummy = null;
		}
	}
	
	// ========================================================================= //
	//			INNER CLASSES												 	 //
	// ========================================================================= //
		
	/**
	 * Implements the logic to insert thumbnails onDrop, and hover styling.
	 * Also removes the dummy thumbnail.
	 */
	public class ImageDropController extends SimpleDropController {
		Widget target;
		
		public ImageDropController(Widget dropTarget) {
			super(dropTarget);
			target = dropTarget;
		}
	  
		public void onDrop(DragContext context) {
			int beforeIndex = contentPanel.getWidgetIndex(context.draggable);
			super.onDrop(context);
			// the thumbnail being drag and dropped
			final FileIcon dragged = (FileIcon)context.draggable;
			String blobKey = dragged.getFileDetail().getBlobKey();
			int afterIndex = 0;
			// Find target widget and insert before
			for(Iterator<Widget> i = contentPanel.iterator(); i.hasNext();) {
				Widget compare = i.next();
				if(compare.equals(target)) {
					dragged.addStyleName("fadesIn");
					
					new Timer() {
						
						@Override
						public void run() {
							dragged.removeStyleName("fadesIn");							
						}
					}.schedule(700); 
					MoveEvent e = createMoveEvent();
					e.setBlobKey(blobKey);
					e.setFrom(beforeIndex);
					e.setTo(afterIndex);
					signalMoveEvent(e);
					contentPanel.insert(dragged, afterIndex);
					break;
				}
				afterIndex++;
			}
		}
		
		private MoveEvent createMoveEvent() {
			Beanery factory = AppController.getBeanFactory();
			return factory.createMoveEvent().as();
		}
		
		public void onEnter(DragContext context) {
			super.onEnter(context);
			target.addStyleName("imageDragAndDrop");
		}
		
		public void onLeave(DragContext context) {
			super.onLeave(context);
			target.removeStyleName("imageDragAndDrop");
		}
	}
	
	/**
	 * Implements the logic to add a place holder thumbnail when
	 * dragging begins, and removing it.
	 */
	public class ImageDragController extends PickupDragController {

		public ImageDragController(AbsolutePanel boundaryPanel,
				boolean allowDroppingOnBoundaryPanel) {
			super(boundaryPanel, allowDroppingOnBoundaryPanel);
			 // Widget must be dragged 5px before drag event fires, 
			 // Allows click events to fire.
			setBehaviorDragStartSensitivity(30);
			
			// Disable automatic scrolling when dragging begins.
			setBehaviorScrollIntoView(false);		
			setBehaviorDragProxy(true);
			setBehaviorMultipleSelection(false);
		}
		
		public void dragStart() {
			Widget w = context.draggable;
			if (!isLast(w))	{
				addDummy();
			}
			super.dragStart();
		}
		
		public void dragEnd() {
			super.dragEnd();
			removeDummy();
		}

	}
	
	private boolean isLast(Widget compareWidget) {
		int lastIndex = getWidgetCount() - 1;
		Widget lastWidget = getWidget(lastIndex);
		return lastWidget.equals(compareWidget);
	}

	// TODO y draggable?
	public FileIcon createFakeIcon() {
		FileIcon fileToAdd = FileIcon.createDummyFileIcon();
		final ImageDropController dropController = new ImageDropController(fileToAdd);
		fileToAdd.setOnDelete(new DeleteCallback() { 
			
			@Override
			public void onDelete() {
				dragController.unregisterDropController(dropController);
			}
		});
		dragController.registerDropController(dropController);
		dragController.makeDraggable(fileToAdd);
		contentPanel.add(fileToAdd);
		return fileToAdd;
	}
	
	// ========================================================================= //
	//			MOVE CALLBACK												 	 //
	// ========================================================================= //
	
	public static MoveCallback moveCallback;
	
	public void setMoveCallback(MoveCallback callback) {
		moveCallback = callback;
	}
	
	public static abstract class MoveCallback {
		public abstract void onMove(MoveEvent e);
	}
	
	public void signalMoveEvent(MoveEvent e) {
		if (moveCallback != null)
			moveCallback.onMove(e);
	}
	
}