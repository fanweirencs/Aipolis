package com.aibang.aipolis.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Help extends BmobObject implements Serializable{

	public Help(String belongPhonenumber, String content,
			Integer commentNumber, Boolean type, String title, Boolean isDone,
			User user, Help help) {
		super();
		this.belongPhonenumber = belongPhonenumber;
		this.content = content;
		this.commentNumber = commentNumber;
		this.type = type;
		this.title = title;
		this.isDone = isDone;
		this.user = user;
		this.help = help;
	}

	private static final long serialVersionUID = 1L;
	private String belongPhonenumber;
	private String content;
	private Integer commentNumber;
	private Boolean type;
	private String title;
	private Boolean isDone;



	public void setZiLeibie(String ziLeibie) {
		this.ziLeibie = ziLeibie;
	}

	public void setLeiBie(String leiBie) {
		this.leiBie = leiBie;
	}

	public void setYaoYueTime(String yaoYueTime) {
		this.yaoYueTime = yaoYueTime;
	}

	public void setDiDian(String diDian) {
		this.diDian = diDian;
	}

	private User user;
	private Help help;
	private Integer lookSum;
	private String beizhu;//备注
	private String ziLeibie;//子类别
	private String leiBie;//类别
	private String yaoYueTime;//邀约时间
	private String diDian;//地点

	public String getZiLeibie() {
		return ziLeibie;
	}

	public String getLeiBie() {
		return leiBie;
	}

	public String getYaoYueTime() {
		return yaoYueTime;
	}

	public String getDiDian() {
		return diDian;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setLookSum(Integer lookSum) {
		this.lookSum = lookSum;
	}

	public Integer getLookSum() {
		return lookSum;
	}

	public Help(){
		
	}

	public String getBelongPhonenumber() {
		return belongPhonenumber;
	}

	public void setBelongPhonenumber(String belongPhonenumber) {
		this.belongPhonenumber = belongPhonenumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getCommentNumber() {
		return commentNumber;
	}

	public void setCommentNumber(Integer commentNumber) {
		this.commentNumber = commentNumber;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsDone() {
		return isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
