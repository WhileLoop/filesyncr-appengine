package org.buildndeploy.shared.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface Beanery extends AutoBeanFactory {
	AutoBean<MoveEvent> createMoveEvent();
}