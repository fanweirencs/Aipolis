package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class Collection extends BmobObject {
	private static final long serialVersionUID = 1L;
	private String id;
	private String username;
	private String articleId;
	public Collection(String id, String username, String articleId) {
		super();
		this.id = id;
		this.username = username;
		this.articleId = articleId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
