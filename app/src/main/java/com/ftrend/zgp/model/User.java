package com.ftrend.zgp.model;

import com.dbflow5.annotation.Column;
import com.dbflow5.annotation.PrimaryKey;
import com.dbflow5.annotation.Table;
import com.ftrend.zgp.utils.db.DBHelper;

/**
 * 可登录用户信息
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = DBHelper.class)
public class User {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String userCode;
    @Column
    private String userName;
    @Column
    private String userPwd;
    @Column
    private String userRights;
    @Column
    private int maxDscRate;
    @Column
    private float maxDscTotal;
    @Column
    private float maxTHTotal;

    public User() {
    }

    public User(String userCode, String userName, String userPwd, String userRights, int maxDscRate, float maxDscTotal, float maxTHTotal) {
        this.userCode = userCode;
        this.userName = userName;
        this.userPwd = userPwd;
        this.userRights = userRights;
        this.maxDscRate = maxDscRate;
        this.maxDscTotal = maxDscTotal;
        this.maxTHTotal = maxTHTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserRights() {
        return userRights;
    }

    public void setUserRights(String userRights) {
        this.userRights = userRights;
    }

    public int getMaxDscRate() {
        return maxDscRate;
    }

    public void setMaxDscRate(int maxDscRate) {
        this.maxDscRate = maxDscRate;
    }

    public float getMaxDscTotal() {
        return maxDscTotal;
    }

    public void setMaxDscTotal(float maxDscTotal) {
        this.maxDscTotal = maxDscTotal;
    }

    public float getMaxTHTotal() {
        return maxTHTotal;
    }

    public void setMaxTHTotal(float maxTHTotal) {
        this.maxTHTotal = maxTHTotal;
    }
}
