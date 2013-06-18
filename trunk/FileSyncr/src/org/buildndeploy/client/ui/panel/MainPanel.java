package org.buildndeploy.client.ui.panel;

import org.buildndeploy.client.ui.file.IconPanel;
import org.buildndeploy.client.ui.widget.UploadButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {
	
	// ========================================================================= //
	//			UI BINDER STUFF												 	 //
	// ========================================================================= //
	
	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {}

	@UiField
	public UploadButton uploadButton;
	
	@UiField
	public SimplePanel helpButton;
	
	@UiField
	public SimplePanel trashButton;
	
	@UiField
	public SimplePanel downloadButton;
	
	@UiField
	public IconPanel draggablePanel;
	
	@UiField
	public ChatPanel chatPanel;
	
		
	// ========================================================================= //
	//			INITILIZING													 	 //
	// ========================================================================= //
	
	public MainPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().setClassName("content");
		// Prepare the help button
		helpButton.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				helpButton.addStyleName("shakes");
				
				new Timer() {

					@Override
					public void run() {
						helpButton.removeStyleName("shakes");
					}
					
				};
			}
		}, ClickEvent.getType());
		
		// Prepare the form
		uploadButton.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadButton.setMethod(FormPanel.METHOD_POST);
		uploadButton.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// Ignore
			}
		});
				
	}	
	
}
