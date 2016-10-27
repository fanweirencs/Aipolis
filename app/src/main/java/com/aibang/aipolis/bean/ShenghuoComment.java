package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class ShenghuoComment extends BmobObject {


	public ShenghuoComment(String comment, String shenghuoID, boolean isRead,
			String shenhuoContent, User commentUser, User beiCommentUser,
			Life shenghuo) {
		super();
		this.comment = comment;
		this.shenghuoID = shenghuoID;
		this.isRead = isRead;
		this.shenhuoContent = shenhuoContent;
		this.commentUser = commentUser;
		this.beiCommentUser = beiCommentUser;
		this.shenghuo = shenghuo;
	}
	private static final long serialVersionUID = 1L;
	private String comment;  //评论内容
	private String shenghuoID;
	private boolean isRead;
	private String shenhuoContent;
	private User commentUser;
	private User beiCommentUser;
	private Life shenghuo;
	public ShenghuoComment(){
		super();
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getShenghuoID() {
		return shenghuoID;
	}
	public void setShenghuoID(String shenghuoID) {
		this.shenghuoID = shenghuoID;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getShenhuoContent() {
		return shenhuoContent;
	}
	public void setShenhuoContent(String shenhuoContent) {
		this.shenhuoContent = shenhuoContent;
	}
	public User getCommentUser() {
		return commentUser;
	}
	public void setCommentUser(User commentUser) {
		this.commentUser = commentUser;
	}
	public User getBeiCommentUser() {
		return beiCommentUser;
	}
	public void setBeiCommentUser(User beiCommentUser) {
		this.beiCommentUser = beiCommentUser;
	}
	public Life getShenghuo() {
		return shenghuo;
	}
	public void setShenghuo(Life shenghuo) {
		this.shenghuo = shenghuo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
