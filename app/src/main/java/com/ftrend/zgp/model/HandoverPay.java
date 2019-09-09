package com.ftrend.zgp.model;

import com.bin.david.form.annotation.SmartTable;
import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * @author liziqiang@ftrend.cn
 */
@SmartTable(name = "HandoverPay")
@Table(database = DBHelper.class)
public class HandoverPay {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String handoverNo;
    @Column
    @NotNull
    private String tradeFlag;
    @Column
    @NotNull
    private String payType;
    @Column
    @NotNull
    private double saleCount = 0;
    @Column
    @NotNull
    private double saleTotal = 0;
    @Column
    @NotNull
    private double rtnCount = 0;
    @Column
    @NotNull
    private double rtnTotal = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHandoverNo() {
        return handoverNo;
    }

    public void setHandoverNo(String handoverNo) {
        this.handoverNo = handoverNo;
    }

    public String getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(String tradeFlag) {
        this.tradeFlag = tradeFlag;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public double getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(double saleCount) {
        this.saleCount = saleCount;
    }

    public double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public double getRtnCount() {
        return rtnCount;
    }

    public void setRtnCount(double rtnCount) {
        this.rtnCount = rtnCount;
    }

    public double getRtnTotal() {
        return rtnTotal;
    }

    public void setRtnTotal(double rtnTotal) {
        this.rtnTotal = rtnTotal;
    }
}
