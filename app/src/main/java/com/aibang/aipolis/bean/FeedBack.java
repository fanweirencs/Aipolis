package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class FeedBack extends BmobObject {

	public FeedBack(String thumnailTaskURL, String contactNum, String content,
			User user) {
		super();
		ThumnailTaskURL = thumnailTaskURL;
		this.contactNum = contactNum;
		this.content = content;
		this.user = user;
	}

	private String ThumnailTaskURL;
	private String contactNum;
	private String content;
	private User user;
	
	public FeedBack(){
		
	}

	public String getThumnailTaskURL() {
		return ThumnailTaskURL;
	}

	public void setThumnailTaskURL(String thumnailTaskURL) {
		ThumnailTaskURL = thumnailTaskURL;
	}

	public String getContactNum() {
		return contactNum;
	}

	public void setContactNum(String contactNum) {
		this.contactNum = contactNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
