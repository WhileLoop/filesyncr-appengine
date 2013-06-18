package org.buildndeploy.client.ui.panel;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SpacedPanel extends Composite implements HasWidgets {
	
	private FlowPanel content;
	
	// TODO ui binder?
	public SpacedPanel() {
		content = new FlowPanel();
		initWidget(content);
		content.getElement().getStyle().setProperty("display", "table");
		content.getElement().getStyle().setTextAlign(TextAlign.CENTER);
	}
	
	public void add(Widget w) {
		SpacedCell s = new SpacedCell();
		s.add(w);
		content.add(s);
	}
	
//	
//	
//	@Override
//	public void add(Widget w) {
//		content.add(new SpacedCell(w));
//	}
//	
//	@Override
//	public void insert(Widget w, int indexBefore) {
//		content.insert(new SpacedCell(w), indexBefore);
//	}
//	
//	@Override
//	public void clear() {
//		content.clear();
//	}
//	
//	@Override
//	public Widget getWidget(int index) {
//		SpacedCell p = (SpacedCell) content.getWidget(index);
//		return p.getWidget();
//	}
//	
//	@Override
//	public int getWidgetCount() {
//		return content.getWidgetCount();
//	}
	
	// NOT IMPLEMENTED
//	public int getWidgetIndex(Widget child) {
//		return null;
//	}
	
	private class SpacedCell extends SimplePanel {
		
		private SpacedCell() {
			this.getElement().getStyle().setProperty("display", "table-cell");
		}
	}


@Override
public void clear() {
	// TODO Auto-generated method stub
	
}

@Override
public Iterator<Widget> iterator() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean remove(Widget arg0) {
	// TODO Auto-generated method stub
	return false;
}

}
