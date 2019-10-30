package com.ftrend.zgp.utils.pop;

/**
 * 优惠计算参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/29
 */
public class DscData {
    private String prodName;
    private double price;
    private double amount;
    private int dscRate;
    private double dscMoney;
    private int dscRateMax;
    private double dscMoneyMax;

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return price * amount;
    }

    public int getDscRate() {
        return dscRate;
    }

    public void setDscRate(int dscRate) {
        this.dscRate = dscRate;
    }

    public double getDscMoney() {
        return dscMoney;
    }

    public void setDscMoney(double dscMoney) {
        this.dscMoney = dscMoney;
    }

    public int getDscRateMax() {
        return dscRateMax;
    }

    public void setDscRateMax(int dscRateMax) {
        this.dscRateMax = dscRateMax;
    }

    public double getDscMoneyMax() {
        return dscMoneyMax;
    }

    public void setDscMoneyMax(double dscMoneyMax) {
        this.dscMoneyMax = dscMoneyMax;
    }
}
