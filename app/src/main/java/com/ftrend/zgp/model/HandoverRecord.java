package com.ftrend.zgp.model;

/**
 * 交班界面适配器实体类
 *
 * @author liziqiang@ftrend.cn
 */
public class HandoverRecord {
    private String cashier;
    private String cashierName;

    private String depCode;

    private double saleCount = 0;
    private double saleTotal = 0;

    private double rtnCount = 0;
    private double rtnTotal = 0;

    private double moneyTotal = 0;
    private double moneyCount = 0;

    private double sqbTotal = 0;
    private double sqbCount = 0;

    private double aliPayTotal = 0;
    private double aliPayCount = 0;

    private double wechatTotal = 0;
    private double wechatCount = 0;

    private double cardTotal = 0;
    private double cardCount = 0;

    public HandoverRecord() {
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

    public double getTotal() {
        return saleTotal + rtnTotal;
    }

    public double getCount() {
        return saleCount + rtnCount;
    }

    public double getMoneyTotal() {
        return moneyTotal;
    }

    public void setMoneyTotal(double moneyTotal) {
        this.moneyTotal = moneyTotal;
    }

    public double getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(double moneyCount) {
        this.moneyCount = moneyCount;
    }

    public double getAliPayTotal() {
        return aliPayTotal;
    }

    public double getSqbTotal() {
        return sqbTotal;
    }

    public void setSqbTotal(double sqbTotal) {
        this.sqbTotal = sqbTotal;
    }

    public double getSqbCount() {
        return sqbCount;
    }

    public void setSqbCount(double sqbCount) {
        this.sqbCount = sqbCount;
    }

    public void setAliPayTotal(double aliPayTotal) {
        this.aliPayTotal = aliPayTotal;
    }

    public double getAliPayCount() {
        return aliPayCount;
    }

    public void setAliPayCount(double aliPayCount) {
        this.aliPayCount = aliPayCount;
    }

    public double getWechatTotal() {
        return wechatTotal;
    }

    public void setWechatTotal(double wechatTotal) {
        this.wechatTotal = wechatTotal;
    }

    public double getWechatCount() {
        return wechatCount;
    }

    public void setWechatCount(double wechatCount) {
        this.wechatCount = wechatCount;
    }

    public double getCardTotal() {
        return cardTotal;
    }

    public void setCardTotal(double cardTotal) {
        this.cardTotal = cardTotal;
    }

    public double getCardCount() {
        return cardCount;
    }

    public void setCardCount(double cardCount) {
        this.cardCount = cardCount;
    }

    public double getPayTotal() {
        return moneyTotal + aliPayTotal + wechatTotal + cardTotal;
    }

    public double getPayCount() {
        return moneyCount + aliPayCount + wechatCount + cardCount;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }
}
