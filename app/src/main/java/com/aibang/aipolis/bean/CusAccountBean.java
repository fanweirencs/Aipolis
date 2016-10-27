package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;


public class CusAccountBean extends BmobObject {
	private static final long serialVersionUID = 1L;
	
    private String cname;
    private String aid;
    private String cid;
    
    public CusAccountBean(){
 	   super();
    }
    public CusAccountBean(String cid,String aid,String cname){
 	   super();
 	   this.cid = cid;
 	   this.aid = aid;
 	   this.cname = cname;
    }
		public String getCname() {
			return cname;
		}
		public void setCname(String cname) {
			this.cname = cname;
		}
		public String getAid() {
			return aid;
		}
		public void setAid(String aid) {
			this.aid = aid;
		}
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
		}
	       
}
