package com.aibang.aipolis.bean;

public class Comment01 {	
	public Comment01(int type, String s1, String s2) {
		super();
		this.type = type;
		this.s1 = s1;
		this.s2 = s2;
	}
	public Comment01() {
		super();
		
	}
	int type;
	private String s1=null;
	private String s2=null;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getS1() {
		return s1;
	}
	public void setS1(String s1) {
		this.s1 = s1;
	}
	public String getS2() {
		return s2;
	}
	public void setS2(String s2) {
		this.s2 = s2;
	}	

}
