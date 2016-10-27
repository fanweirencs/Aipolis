package com.aibang.aipolis.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class ShouyeArticle extends BmobObject implements Serializable{
	public ShouyeArticle(String title, String content, String imgUrl,
						 Integer zanNum, Integer commentNum, String belongPhonenumber,
						 String belongUserNickname, String belongUserImgUrl, User zuozhe) {
		super();
		this.title = title;
		this.content = content;
		this.imgUrl = imgUrl;
		this.zanNum = zanNum;
		this.commentNum = commentNum;
		this.belongPhonenumber = belongPhonenumber;
		this.belongUserNickname = belongUserNickname;
		this.belongUserImgUrl = belongUserImgUrl;
		this.zuozhe = zuozhe;
	}
	public ShouyeArticle() {
		// TODO Auto-generated constructor stub
	}
	private static final long serialVersionUID = 1L;
	private String title;
	private String content;
	private String imgUrl;
	private Integer zanNum;
	private Integer commentNum;
	private String belongPhonenumber;
	private String belongUserNickname;
	private String belongUserImgUrl;
	private User zuozhe;
	private Boolean isVideo;//是否包含视频
	private String videoUrl;//视频地址
	private String preloadImgUrl;//预加载图片地址
	private Integer lookSum;//

	public void setLookSum(Integer lookSum) {
		this.lookSum = lookSum;
	}

	public void setVideo(Boolean video) {
		isVideo = video;
	}

	public Integer getLookSum() {

		return lookSum;
	}

	public void setPreloadImgUrl(String preloadImgUrl) {
		this.preloadImgUrl = preloadImgUrl;
	}

	public String getPreloadImgUrl() {

		return preloadImgUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public Boolean getIsVideo() {
		return isVideo;
	}

	public void setIsVideo(Boolean video) {
		isVideo = video;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public Integer getZanNum() {
		return zanNum;
	}
	public void setZanNum(Integer zanNum) {
		this.zanNum = zanNum;
	}
	public Integer getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}
	public String getBelongPhonenumber() {
		return belongPhonenumber;
	}
	public void setBelongPhonenumber(String belongPhonenumber) {
		this.belongPhonenumber = belongPhonenumber;
	}
	public String getBelongUserNickname() {
		return belongUserNickname;
	}
	public void setBelongUserNickname(String belongUserNickname) {
		this.belongUserNickname = belongUserNickname;
	}
	public String getBelongUserImgUrl() {
		return belongUserImgUrl;
	}
	public void setBelongUserImgUrl(String belongUserImgUrl) {
		this.belongUserImgUrl = belongUserImgUrl;
	}
	public User getZuozhe() {
		return zuozhe;
	}
	public void setZuozhe(User zuozhe) {
		this.zuozhe = zuozhe;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
