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

    private int saleCount = 0;
    private double saleTotal = 0;

    private int rtnCount = 0;
    private double rtnTotal = 0;

    private double moneyTotal = 0;
    private int moneyCount = 0;

    private double sqbTotal = 0;
    private int sqbCount = 0;

    private double cardTotal = 0;
    private int cardCount = 0;

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

    public double getTotal() {
        return saleTotal + rtnTotal;
    }

    public int getCount() {
        return saleCount + rtnCount;
    }

    public double getMoneyTotal() {
        return moneyTotal;
    }

    public void setMoneyTotal(double moneyTotal) {
        this.moneyTotal = moneyTotal;
    }

    public int getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(int moneyCount) {
        this.moneyCount = moneyCount;
    }

    public double getSqbTotal() {
        return sqbTotal;
    }

    public void setSqbTotal(double sqbTotal) {
        this.sqbTotal = sqbTotal;
    }

    public int getSqbCount() {
        return sqbCount;
    }

    public void setSqbCount(int sqbCount) {
        this.sqbCount = sqbCount;
    }

    public double getCardTotal() {
        return cardTotal;
    }

    public void setCardTotal(double cardTotal) {
        this.cardTotal = cardTotal;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    public double getPayTotal() {
        return moneyTotal + sqbTotal + cardTotal;
    }

    public int getPayCount() {
        return moneyCount + sqbCount + cardCount;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }
}
