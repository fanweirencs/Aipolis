package com.aibang.aipolis.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser implements Serializable{
	public User(String nickName, String school, String yuanxi, String gender,
				String birthday, String location, String autograph,
				String qianming, String autographUrl) {
		super();
		this.nickName = nickName;
		this.school = school;
		this.yuanxi = yuanxi;
		this.gender = gender;
		this.birthday = birthday;
		this.location = location;
		this.autograph = autograph;
		this.qianming = qianming;
		this.autographUrl = autographUrl;
	}

	private static final long serialVersionUID = 1L;
	private String nickName;
	private String school;	//学校
	private String yuanxi;
	private String gender;
	private String birthday;//生日
	private String location;//城市
	private String autograph;
	private String qianming;//签名
	private String autographUrl;//头像

	private Integer followingNumber;//我关注的人数
	private Integer followersNumber;//关注我的人数
	private Integer likes;//收藏
	private Integer shenfen;//空或者1代表正常，2代表禁言
	private String backgroundImg;//背景

	public String getBackgroundImg() {
		return backgroundImg;
	}

	public void setBackgroundImg(String backgroundImg) {
		this.backgroundImg = backgroundImg;
	}

	public Integer getShenfen() {
		return shenfen;
	}

	public void setShenfen(Integer shenfen) {
		this.shenfen = shenfen;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public Integer getLikes() {

		return likes;
	}

	public void setFollowingNumber(Integer followingNumber) {
		this.followingNumber = followingNumber;
	}

	public void setFollowersNumber(Integer followersNumber) {
		this.followersNumber = followersNumber;
	}

	public Integer getFollowingNumber() {

		return followingNumber;
	}

	public Integer getFollowersNumber() {
		return followersNumber;
	}

	public User(){
		super();
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getYuanxi() {
		return yuanxi;
	}

	public void setYuanxi(String yuanxi) {
		this.yuanxi = yuanxi;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAutograph() {
		return autograph;
	}

	public void setAutograph(String autograph) {
		this.autograph = autograph;
	}

	public String getQianming() {
		return qianming;
	}

	public void setQianming(String qianming) {
		this.qianming = qianming;
	}

	public String getAutographUrl() {
		return autographUrl;
	}

	public void setAutographUrl(String autographUrl) {
		this.autographUrl = autographUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
