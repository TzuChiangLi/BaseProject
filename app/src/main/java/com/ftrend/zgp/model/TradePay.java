package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * 交易支付表
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = DBHelper.class)
public class TradePay extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @Column
    private int id;
    @Column
    private String lsNo;
    @Column
    private String payTypeCode;
    @Column
    private float amount;
    @Column
    private float change;
    @Column
    private Date payTime;
    @Column
    private String payCode;

    public TradePay() {
    }

    public TradePay(String lsNo, String payTypeCode, float amount, float change, Date payTime, String payCode) {
        this.lsNo = lsNo;
        this.payTypeCode = payTypeCode;
        this.amount = amount;
        this.change = change;
        this.payTime = payTime;
        this.payCode = payCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLsNo() {
        return lsNo;
    }

    public void setLsNo(String lsNo) {
        this.lsNo = lsNo;
    }

    public String getPayTypeCode() {
        return payTypeCode;
    }

    public void setPayTypeCode(String payTypeCode) {
        this.payTypeCode = payTypeCode;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getChange() {
        return change;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }
}
