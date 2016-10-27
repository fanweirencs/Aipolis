package com.aibang.aipolis.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Life extends BmobObject implements Serializable{
	public Life(String content, Integer zanNumber, Integer commentNumber,
			Integer imgNumber, String thumnailTaskURL, User user) {
		super();
		this.content = content;
		this.zanNumber = zanNumber;
		this.commentNumber = commentNumber;
		this.imgNumber = imgNumber;
		ThumnailTaskURL = thumnailTaskURL;
		this.user = user;
	}

	private static final long serialVersionUID = 1L;
	private String content;            //信息内容
	private Integer zanNumber;         //点赞数
	private Integer commentNumber;     //评论数           //信息发布者学校
	private Integer imgNumber;         //照片数量
	private String ThumnailTaskURL;//缩略图地址
	private User user;
	private Integer lookSum;//浏览量

	public Integer getLookSum() {
		return lookSum;
	}

	public void setLookSum(Integer lookSum) {
		this.lookSum = lookSum;
	}

	public Life(){
		
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getZanNumber() {
		return zanNumber;
	}

	public void setZanNumber(Integer zanNumber) {
		this.zanNumber = zanNumber;
	}

	public Integer getCommentNumber() {
		return commentNumber;
	}

	public void setCommentNumber(Integer commentNumber) {
		this.commentNumber = commentNumber;
	}

	public Integer getImgNumber() {
		return imgNumber;
	}

	public void setImgNumber(Integer imgNumber) {
		this.imgNumber = imgNumber;
	}

	public String getThumnailTaskURL() {
		return ThumnailTaskURL;
	}

	public void setThumnailTaskURL(String thumnailTaskURL) {
		ThumnailTaskURL = thumnailTaskURL;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
