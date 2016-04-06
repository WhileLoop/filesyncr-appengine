package org.buildndeploy.client.resource;

import org.buildndeploy.client.resource.css.Css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	
	@Source("css/gwt.css")
	public Css css();
	
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("images/appengine.gif")
	public ImageResource appengine();
	
	@Source("images/arrow_down_small42.png")
	public ImageResource downArrowSmall();
	
	@Source("images/bullet_deny_small42.png")
	public ImageResource deleteSmall();
	
	@Source("images/bullet_info.png")
	public ImageResource info();
	
	@Source("images/bullet_info_small42.png")
	public ImageResource smallInfo();
	
	@Source("images/file_encrypted.png")
	public ImageResource file0();
	
	@Source("images/file_locked.png")
	public ImageResource file1();
	
	@Source("images/file_refresh.png")
	public ImageResource fileRefresh();
	
	@Source("images/file_upload.png")
	public ImageResource upload();
	
	@Source("images/file_white.png")
	public ImageResource file2();
	
	@Source("images/file_yellow.png")
	public ImageResource file3();

	@Source("images/file_zipped.png")
	public ImageResource file4();
	
	@Source("images/gwt30.png")
	public ImageResource gwt();
	
	@Source("images/music_off.png")
	public ImageResource file5();
	
	@Source("images/printers.png")
	public ImageResource file6();
	
	@Source("images/refreshing.png")
	public ImageResource refresh();
	
	@Source("images/star.png")
	public ImageResource star();
	
	@Source("images/user.png")
	public ImageResource user();
	
	@Source("images/smallphotostarred.png")
	public ImageResource smallPhotoStarred();
	
	@Source("images/trash_delete.png")
	public ImageResource trash_delete();
	
	@Source("images/folder_download.png")
	public ImageResource folder_download();
}
