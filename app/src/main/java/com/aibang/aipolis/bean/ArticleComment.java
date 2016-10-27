package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class ArticleComment extends BmobObject {

	public ArticleComment(String articleID, String comment,
						  ShouyeArticle article, boolean isRead, User commentUser,
						  User beiCommentUser) {
		super();
		this.articleID = articleID;
		this.comment = comment;
		this.article = article;
		this.isRead = isRead;
		this.commentUser = commentUser;
		this.beiCommentUser = beiCommentUser;
	}

	private static final long serialVersionUID = 1L;

    private String articleID;
    private String comment;
    private ShouyeArticle article;
    private boolean isRead;
    private User commentUser;
    private User beiCommentUser;
    
    public ArticleComment(){
    	
    }

	public String getArticleID() {
		return articleID;
	}

	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ShouyeArticle getArticle() {
		return article;
	}

	public void setArticle(ShouyeArticle article) {
		this.article = article;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
   
}

