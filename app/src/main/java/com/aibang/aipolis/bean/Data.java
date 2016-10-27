package com.aibang.aipolis.bean;

/**
 * Created by zcf on 2016/6/1.
 */
public class Data {

    /**
     * birthday : 1994-2-13
     * mobilePhoneNumberVerified : true
     * followingNumber : 4
     * autographUrl : http://bmob-cdn-31.b0.upaiyun.com/2016/05/23/06608cb6b7114f5a9947f078d53882e8.JPG
     * location : 武汉
     * followersNumber : 4
     * password : 123456
     * mobilePhoneNumber : 13971261006
     * emailVerified : false
     * updatedAt : 2016-06-01 16:11:33
     * username : 13971261006
     * school : 华中师范大学
     * autograph : 4B7F83DA68B443CBA7D01672C40DEFE8.jpg
     * nickName : 公子莫慌
     * email : 321@qq.com
     * objectId : d722951452
     * createdAt : 2016-04-26 16:03:35
     * yuanxi : 教育信息技术学院
     * shenfen : 1
     * gender : male
     * qianming : 别管太多，只管努力就行 ！
     */

    private DataBean data;
    /**
     * data : {"birthday":"1994-2-13","mobilePhoneNumberVerified":true,"followingNumber":4,"autographUrl":"http://bmob-cdn-31.b0.upaiyun.com/2016/05/23/06608cb6b7114f5a9947f078d53882e8.JPG","location":"武汉","followersNumber":4,"password":"123456","mobilePhoneNumber":"13971261006","emailVerified":false,"updatedAt":"2016-06-01 16:11:33","username":"13971261006","school":"华中师范大学","autograph":"4B7F83DA68B443CBA7D01672C40DEFE8.jpg","nickName":"公子莫慌","email":"321@qq.com","objectId":"d722951452","createdAt":"2016-04-26 16:03:35","yuanxi":"教育信息技术学院","shenfen":1,"gender":"male","qianming":"别管太多，只管努力就行 ！"}
     * action : updateTable
     * objectId :
     * tableName : _User
     * appKey : 7959be2ae1fa0af5c8e58bbcc1e6bca9
     */

    private String action;
    private String objectId;
    private String tableName;
    private String appKey;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public static class DataBean {
        private String birthday;
        private boolean mobilePhoneNumberVerified;
        private int followingNumber;
        private String autographUrl;
        private String location;
        private int followersNumber;
        private String password;
        private String mobilePhoneNumber;
        private boolean emailVerified;
        private String updatedAt;
        private String username;
        private String school;
        private String autograph;
        private String nickName;
        private String email;
        private String objectId;
        private String createdAt;
        private String yuanxi;
        private int shenfen;
        private String gender;
        private String qianming;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public boolean isMobilePhoneNumberVerified() {
            return mobilePhoneNumberVerified;
        }

        public void setMobilePhoneNumberVerified(boolean mobilePhoneNumberVerified) {
            this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
        }

        public int getFollowingNumber() {
            return followingNumber;
        }

        public void setFollowingNumber(int followingNumber) {
            this.followingNumber = followingNumber;
        }

        public String getAutographUrl() {
            return autographUrl;
        }

        public void setAutographUrl(String autographUrl) {
            this.autographUrl = autographUrl;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getFollowersNumber() {
            return followersNumber;
        }

        public void setFollowersNumber(int followersNumber) {
            this.followersNumber = followersNumber;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobilePhoneNumber() {
            return mobilePhoneNumber;
        }

        public void setMobilePhoneNumber(String mobilePhoneNumber) {
            this.mobilePhoneNumber = mobilePhoneNumber;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getAutograph() {
            return autograph;
        }

        public void setAutograph(String autograph) {
            this.autograph = autograph;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getYuanxi() {
            return yuanxi;
        }

        public void setYuanxi(String yuanxi) {
            this.yuanxi = yuanxi;
        }

        public int getShenfen() {
            return shenfen;
        }

        public void setShenfen(int shenfen) {
            this.shenfen = shenfen;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getQianming() {
            return qianming;
        }

        public void setQianming(String qianming) {
            this.qianming = qianming;
        }
    }
}
