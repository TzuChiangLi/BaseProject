package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 交班记录（交易统计）
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/22
 */
@Table(database = ZgpDb.class)
public class HandoverSum extends BaseModel {
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
    @Column
    @NotNull
    private double delCount = 0;
    @Column
    @NotNull
    private double delTotal = 0;
    @Column
    @NotNull
    private double cancelCount = 0;
    @Column
    @NotNull
    private double cancelTotal = 0;
    @Column
    @NotNull
    private double hangupCount = 0;
    @Column
    @NotNull
    private double hangupTotal = 0;

    public HandoverSum() {
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

    public double getDelCount() {
        return delCount;
    }

    public void setDelCount(double delCount) {
        this.delCount = delCount;
    }

    public double getDelTotal() {
        return delTotal;
    }

    public void setDelTotal(double delTotal) {
        this.delTotal = delTotal;
    }

    public double getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(double cancelCount) {
        this.cancelCount = cancelCount;
    }

    public double getCancelTotal() {
        return cancelTotal;
    }

    public void setCancelTotal(double cancelTotal) {
        this.cancelTotal = cancelTotal;
    }

    public double getHangupCount() {
        return hangupCount;
    }

    public void setHangupCount(double hangupCount) {
        this.hangupCount = hangupCount;
    }

    public double getHangupTotal() {
        return hangupTotal;
    }

    public void setHangupTotal(double hangupTotal) {
        this.hangupTotal = hangupTotal;
    }
}
