package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class HelpComment extends BmobObject {

	public HelpComment(String comment, String helpID, boolean isRead,
			User commentUser, User beiCommentUser, Help help) {
		super();
		this.comment = comment;
		this.helpID = helpID;
		this.isRead = isRead;
		this.commentUser = commentUser;
		this.beiCommentUser = beiCommentUser;
		this.help = help;
	}
	public HelpComment(){
		
	}
	private static final long serialVersionUID = 1L;
	 private String comment;  //评论内容
	 private String helpID;
	 private boolean isRead;
	 private User commentUser;
	 private User beiCommentUser;
	 private Help help;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getHelpID() {
		return helpID;
	}
	public void setHelpID(String helpID) {
		this.helpID = helpID;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
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
	public Help getHelp() {
		return help;
	}
	public void setHelp(Help help) {
		this.help = help;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
