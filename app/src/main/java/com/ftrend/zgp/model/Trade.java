package com.ftrend.zgp.model;

import java.util.Date;

/**
 * 交易流水表
 *
 * @author LZQ
 */
public class Trade {
    private int id;
    private String depCode;
    private String lsNo;
    private Date tradeTime;
    private String tradeFlag;
    private String cashier;
    private float dscTotal;
    private float total;
    private String custType;
    private String vipCode;
    private String cardCode;
    private float vipTotal;
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
