package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * 交班记录
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class Handover extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String handoverNo;
    @Column
    @NotNull
    private Date handoverTime;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String lsNoMin;
    @Column
    @NotNull
    private String lsNoMax;
    @Column
    @NotNull
    private String status = "0";

    @Column
    @NotNull
    @Deprecated
    private String cashier = "";
    @Column
    @NotNull
    @Deprecated
    private double saleCount = 0;
    @Column
    @NotNull
    @Deprecated
    private double saleTotal = 0;
    @Column
    @NotNull
    @Deprecated
    private double rtnCount = 0;
    @Column
    @NotNull
    @Deprecated
    private double rtnTotal = 0;
    @Column
    @NotNull
    @Deprecated
    private double delCount = 0;
    @Column
    @NotNull
    @Deprecated
    private double delTotal = 0;
    @Column
    @NotNull
    @Deprecated
    private double cancelCount = 0;
    @Column
    @NotNull
    @Deprecated
    private double cancelTotal = 0;
    @Column
    @NotNull
    @Deprecated
    private double hangupCount = 0;
    @Column
    @NotNull
    @Deprecated
    private double hangupTotal = 0;

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

    public Date getHandoverTime() {
        return handoverTime;
    }

    public void setHandoverTime(Date handoverTime) {
        this.handoverTime = handoverTime;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getLsNoMin() {
        return lsNoMin;
    }

    public void setLsNoMin(String lsNoMin) {
        this.lsNoMin = lsNoMin;
    }

    public String getLsNoMax() {
        return lsNoMax;
    }

    public void setLsNoMax(String lsNoMax) {
        this.lsNoMax = lsNoMax;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
