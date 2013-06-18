package org.buildndeploy.client.ui.animation;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class OneTimeCssAnimator {
	
	public OneTimeCssAnimator(final Widget w, final String className, int animationDuration) {
		w.addStyleName(className);
		new Timer() {
			@Override
			public void run() {
				w.removeStyleName(className);
			}
		}.schedule(animationDuration);
	}
}
