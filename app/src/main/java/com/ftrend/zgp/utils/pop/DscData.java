package com.ftrend.zgp.utils.pop;

/**
 * 优惠计算参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/29
 */
public class DscData {
    //商品名称（整单优惠时为空）
    private String prodName;
    //商品数量（整单优惠时为1）
    private double amount;
    //商品原价（整单优惠时为总价）
    private double price;
    //小计（应收金额，已扣除原有优惠）
    private double total;
    //折扣比例
    private int dscRate;
    //优惠金额（单项优惠时为所有数量的总优惠）
    private double dscMoney;
    //其他优惠金额（整单优惠计算时用）
    private double dscOther;
    //最大允许折扣比例
    private int dscRateMax;
    //最大允许优惠金额（单项优惠时为所有数量的总优惠）
    private double dscMoneyMax;

    /**
     * 获取商品原价（单项）/总价（整单）
     *
     * @return
     */
    public double getOriPrice() {
        return price;
    }

    /**
     * 获取商品小计（单项）/应收（整单）
     *
     * @return
     */
    public double getOriTotal() {
        return price * amount;
    }

    /**
     * 获取商品小计优惠金额（单项）/应收优惠金额（整单）
     *
     * @return
     */
    public double getOriDscMoney() {
        return price * amount - total;
    }

    /**
     * 获取商品优惠价（单项）
     *
     * @return
     */
    public double getDscPrice() {
        return price - dscMoney / amount;
    }

    /**
     * 获取商品实收（单项/整单）
     *
     * @return
     */
    public double getDscTotal() {
        return price * amount - dscMoney;
    }

    /**
     * 获取单价优惠金额（单项）
     *
     * @return
     */
    public double getDscMoneyByPrice() {
        return dscMoney / amount;
    }

    /**
     * 获取单价优惠金额上限（单项）
     *
     * @return
     */
    public double getDscMoneyMaxByPrice() {
        return dscMoneyMax / amount;
    }

    /**
     * 数据是否有效（未超过折扣上限）
     *
     * @return
     */
    public boolean isValid() {
        return dscRate <= dscRateMax
                && dscMoney <= dscMoneyMax;
    }

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
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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

    public double getDscOther() {
        return dscOther;
    }

    public void setDscOther(double dscOther) {
        this.dscOther = dscOther;
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
