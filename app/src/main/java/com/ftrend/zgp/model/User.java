package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


/**
 * 可登录用户信息
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class User extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String userCode;
    @Column
    @NotNull
    private String userName;
    @Column
    @NotNull
    private String userPwd;
    @Column
    @NotNull
    private String userRights;
    @Column
    @NotNull
    private int maxDscRate = 0;
    @Column
    @NotNull
    private double maxDscTotal = 0;
    @Column
    @NotNull
    private double maxTHTotal = 0;

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

    public double getMaxDscTotal() {
        return maxDscTotal;
    }

    public void setMaxDscTotal(double maxDscTotal) {
        this.maxDscTotal = maxDscTotal;
    }

    public double getMaxTHTotal() {
        return maxTHTotal;
    }

    public void setMaxTHTotal(double maxTHTotal) {
        this.maxTHTotal = maxTHTotal;
    }
}
