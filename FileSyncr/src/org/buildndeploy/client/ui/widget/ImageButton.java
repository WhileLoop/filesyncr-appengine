package org.buildndeploy.client.ui.widget;

import org.buildndeploy.client.js.Native;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageButton extends Composite {

	private static ImageButtonUiBinder uiBinder = GWT
			.create(ImageButtonUiBinder.class);

	interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {}

	@UiField
	SimplePanel image;

	@UiField
	InlineLabel label;
	
	
	private String url;
	
	public @UiConstructor ImageButton(String text, final String url, String img) {
		initWidget(uiBinder.createAndBindUi(this));
		this.url= url;
		image.addStyleName(img);
		label.setText(text);
		this.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doClick();
			}
		}, ClickEvent.getType());
		
		this.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == NativeEvent.BUTTON_MIDDLE) {
					doClick();
				}
			}
		}, MouseDownEvent.getType());
	}
	
	private void doClick() {
		Anchor a = new Anchor();
		a.setHref(url);
		a.setTarget("_blank");
		RootPanel.get().add(a);
		Native.click(a.getElement());
		a.removeFromParent();
	}
}
