package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class HandoverPay extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String handoverNo;
    @Column
    @NotNull
    private String cashier;
    @Column
    @NotNull
    private String cashierName;
    @Column
    @NotNull
    private String payType;
    @Column
    @NotNull
    private String payTypeName;
    @Column
    @NotNull
    private int saleCount = 0;
    @Column
    @NotNull
    private double saleTotal = 0;
    @Column
    @NotNull
    private int rtnCount = 0;
    @Column
    @NotNull
    private double rtnTotal = 0;

    public HandoverPay() {
    }

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

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public int getRtnCount() {
        return rtnCount;
    }

    public void setRtnCount(int rtnCount) {
        this.rtnCount = rtnCount;
    }

    public double getRtnTotal() {
        return rtnTotal;
    }

    public void setRtnTotal(double rtnTotal) {
        this.rtnTotal = rtnTotal;
    }

    public void init() {
        saleCount = 0;
        saleTotal = 0;
        rtnCount = 0;
        rtnTotal = 0;
    }

    public void add(HandoverPay other) {
        saleCount += other.saleCount;
        saleTotal += other.saleTotal;
        rtnCount += other.rtnCount;
        rtnTotal += other.rtnTotal;
    }

    public int getCount() {
        return saleCount + rtnCount;
    }

    public double getTotal() {
        return saleTotal + rtnTotal;
    }
}
