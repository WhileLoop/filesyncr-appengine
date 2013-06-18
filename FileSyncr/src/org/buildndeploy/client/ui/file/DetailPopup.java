package org.buildndeploy.client.ui.file;

import org.buildndeploy.client.model.BlobInfoJS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DetailPopup extends PopupPanel {

	private static DetailPopupUiBinder uiBinder = GWT
			.create(DetailPopupUiBinder.class);

	interface DetailPopupUiBinder extends UiBinder<Widget, DetailPopup> {}
	
	interface UiStyle extends CssResource {
		String greenBox();
		String darkGlass();
	}
	
	@UiField UiStyle style;
	
	@UiField 
	protected TableCellElement 
	name,
	size,
	created,
	type,
	hash;
	
	@UiField Button button;
	
	private DetailPopup instance;

	protected DetailPopup(BlobInfoJS b) {
		setWidget(uiBinder.createAndBindUi(this));
		instance = this;
		this.setStyleName(style.greenBox());
		setGlassStyleName(style.darkGlass());
		setGlassEnabled(true);
		name.setInnerText(b.getFilename());
		size.setInnerText(b.getSize() + " bytes");
		created.setInnerText(b.getCreation());
		type.setInnerText(b.getContentType());
		hash.setInnerText(b.getMd5Hash());
		center();
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		hide();
	}
	
	private HandlerRegistration r;
	
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
	
}
