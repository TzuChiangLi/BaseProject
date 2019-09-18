package com.ftrend.zgp.model;

/**
 * 交班界面适配器实体类
 *
 * @author liziqiang@ftrend.cn
 */
public class HandoverRecord {
    private String cashier;
    private String cashierName;

    private double saleCount = 0;
    private double saleTotal = 0;

    private double rtnCount = 0;
    private double rtnTotal = 0;

    private double total = 0;
    private double count = 0;

    private double moneyTotal = 0;
    private double moneyCount = 0;

    private double aliPayTotal = 0;
    private double aliPayCount = 0;

    private double wechatTotal = 0;
    private double wechatCount = 0;

    private double cardTotal = 0;
    private double cardCount = 0;


    private double payTotal = 0;
    private double payCount = 0;


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
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
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
        return payTotal;
    }

    public void setPayTotal(double payTotal) {
        this.payTotal = payTotal;
    }

    public double getPayCount() {
        return payCount;
    }

    public void setPayCount(double payCount) {
        this.payCount = payCount;
    }
}
