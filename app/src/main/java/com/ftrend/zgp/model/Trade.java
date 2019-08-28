package com.ftrend.zgp.model;

import com.dbflow5.annotation.Column;
import com.dbflow5.annotation.PrimaryKey;
import com.dbflow5.annotation.Table;
import com.ftrend.zgp.utils.db.DBHelper;

import java.util.Date;

/**
 * 交易流水表
 *
 * @author LZQ
 */
@Table(database = DBHelper.class)
public class Trade {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String depCode;
    @Column
    private String lsNo;
    @Column
    private Date tradeTime;
    @Column
    private String tradeFlag;
    @Column
    private String cashier;
    @Column
    private float dscTotal;
    @Column
    private float total;
    @Column
    private String custType;
    @Column
    private String vipCode;
    @Column
    private String cardCode;
    @Column
    private float vipTotal;
    @Column
    private String status;

    public Trade() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getLsNo() {
        return lsNo;
    }

    public void setLsNo(String lsNo) {
        this.lsNo = lsNo;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(String tradeFlag) {
        this.tradeFlag = tradeFlag;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public float getDscTotal() {
        return dscTotal;
    }

    public void setDscTotal(float dscTotal) {
        this.dscTotal = dscTotal;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public float getVipTotal() {
        return vipTotal;
    }

    public void setVipTotal(float vipTotal) {
        this.vipTotal = vipTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
