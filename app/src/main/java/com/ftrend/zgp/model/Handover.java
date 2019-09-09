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
@Table(database = DBHelper.class)
public class Handover {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String handoverNo;
    @Column
    @NotNull
    private String handoverTime;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String cashier;
    @Column
    @NotNull
    private String lsNoMin;
    @Column
    @NotNull
    private String lsNoMax;
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
    private double hanupCount = 0;
    @Column
    @NotNull
    private double hanupTotal = 0;
    @Column
    @NotNull
    private String status = "0";

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

    public String getHandoverTime() {
        return handoverTime;
    }

    public void setHandoverTime(String handoverTime) {
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

    public double getHanupCount() {
        return hanupCount;
    }

    public void setHanupCount(double hanupCount) {
        this.hanupCount = hanupCount;
    }

    public double getHanupTotal() {
        return hanupTotal;
    }

    public void setHanupTotal(double hanupTotal) {
        this.hanupTotal = hanupTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
