package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.FormatHelper;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * 交易支付表
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class TradePay extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String lsNo;
    @Column
    @NotNull
    private String payTypeCode;
    @Column
    @NotNull
    private String appPayType;
    @Column
    @NotNull
    private double amount;
    @Column
    private double change = 0;
    @Column
    private String payCode;
    @Column
    @NotNull
    private Date payTime;
    @Column
    private double balance = 0;//储值卡余额

    public TradePay() {
    }

    public TradePay(String lsNo, String payTypeCode, String appPayType,
                    double amount, double change, Date payTime, String payCode) {
        this.lsNo = lsNo;
        this.payTypeCode = payTypeCode;
        this.appPayType = appPayType;
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

    public String getAppPayType() {
        return appPayType;
    }

    public void setAppPayType(String appPayType) {
        this.appPayType = appPayType;
    }

    public double getAmount() {
        return FormatHelper.priceFormat(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getChange() {
        return FormatHelper.priceFormat(change);
    }

    public void setChange(double change) {
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

    public double getBalance() {
        return FormatHelper.priceFormat(balance);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "TradePay{" +
                "id=" + id +
                ", lsNo='" + lsNo + '\'' +
                ", payTypeCode='" + payTypeCode + '\'' +
                ", appPayType='" + appPayType + '\'' +
                ", amount=" + amount +
                ", change=" + change +
                ", payCode='" + payCode + '\'' +
                ", payTime=" + payTime +
                ", balance=" + balance +
                '}';
    }
}
