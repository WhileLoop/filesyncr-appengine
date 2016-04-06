package org.buildndeploy.shared.model;

public interface MoveEvent {
	void setBlobKey(String s);
	void setFrom(int i);
	void setTo(int i);
	String getBlobKey();
	int getFrom();
	int getTo();
}
