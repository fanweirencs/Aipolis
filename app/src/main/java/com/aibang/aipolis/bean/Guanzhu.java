package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

public class Guanzhu extends BmobObject {
	public Guanzhu(boolean isRead, User guanzhu, User beiguanzhu, int leixing) {
		super();
		this.isRead = isRead;
		this.guanzhu = guanzhu;
		this.beiguanzhu = beiguanzhu;
		this.leixing = leixing;
	}

	private static final long serialVersionUID = 1L;
	private boolean isRead;
	private User guanzhu;
	private User beiguanzhu;
	private int leixing;
	
	public Guanzhu(){
		
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public User getGuanzhu() {
		return guanzhu;
	}

	public void setGuanzhu(User guanzhu) {
		this.guanzhu = guanzhu;
	}

	public User getBeiguanzhu() {
		return beiguanzhu;
	}

	public void setBeiguanzhu(User beiguanzhu) {
		this.beiguanzhu = beiguanzhu;
	}

	public int getLeixing() {
		return leixing;
	}

	public void setLeixing(int leixing) {
		this.leixing = leixing;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
