package com.ftrend.zgp.utils.pop;

import com.ftrend.zgp.utils.common.CommonUtil;

import java.util.Locale;

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
    //商品单位（整单优惠无效）
    private String unit;
    //商品原价（整单优惠时为总价）
    private double price;
    //小计（应收金额，已扣除原有优惠）
    private double total;

    //优惠前单价（整单优惠无效）
    private double priceBefore;
    //优惠前优惠金额（单项优惠时为所有优惠金额；整单优惠时为整单优惠金额）
    private double dscMoneyBefore;
    //优惠前其他优惠金额（单项优惠无效；整单优惠时为除整单优惠以外的优惠金额）
    private double dscOtherBefore;
    //优惠前小计
    private double totalBefore;

    //优惠后单价（整单优惠无效）
    private double priceAfter;
    //优惠后优惠金额（单项优惠时为所有优惠金额；整单优惠时为整单优惠金额）
    private double dscMoneyAfter;
    //优惠后其他优惠金额（单项优惠无效；整单优惠时为除整单优惠以外的优惠金额）
    private double dscOtherAfter;
    //优惠后小计
    private double totalAfter;

    //最大允许折扣比例
    private int dscRateMax;
    //最大允许优惠金额（单项优惠时为所有数量的总优惠）
    private double dscMoneyMax;

    /**
     * 计算优惠后参数
     * @param dscMoney 优惠金额
     * @param dscOther 其他优惠（单项优惠时为0）
     */
    public void dscCalc(double dscMoney, double dscOther) {
        totalAfter = total - dscMoney - dscOther;
        priceAfter = totalAfter / amount;
        dscMoneyAfter = dscMoney;
        dscOtherAfter = dscOther;
    }

    public static String formatRate(int value) {
        return String.format(Locale.CHINA, "%d%%", value);
    }

    public static String formatPrice(double price, double oriPrice) {
        return String.format(Locale.CHINA, "%s(-%s)",
                CommonUtil.moneyToString(price),
                CommonUtil.moneyToString(oriPrice - price));
    }

    public static String formatDsc(double money, double other) {
        return String.format(Locale.CHINA, "%s(+%s)",
                CommonUtil.moneyToString(money),
                CommonUtil.moneyToString(other));
    }

    /**
     * 获取单价优惠金额（单项）
     *
     * @return
     */
    public double getDscMoneyAfterByPrice() {
        return dscMoneyAfter / amount;
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
        return getDscRateAfter() <= dscRateMax && dscMoneyAfter <= dscMoneyMax;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPriceBefore() {
        return priceBefore;
    }

    public void setPriceBefore(double priceBefore) {
        this.priceBefore = priceBefore;
    }

    public double getDscMoneyBefore() {
        return dscMoneyBefore;
    }

    public void setDscMoneyBefore(double dscMoneyBefore) {
        this.dscMoneyBefore = dscMoneyBefore;
    }

    public double getDscOtherBefore() {
        return dscOtherBefore;
    }

    public void setDscOtherBefore(double dscOtherBefore) {
        this.dscOtherBefore = dscOtherBefore;
    }

    public int getDscRateBefore() {
        return (int) Math.round((dscMoneyBefore + dscOtherBefore) * 100 / total);
    }

    public double getTotalBefore() {
        return totalBefore;
    }

    public void setTotalBefore(double totalBefore) {
        this.totalBefore = totalBefore;
    }

    public double getPriceAfter() {
        return priceAfter;
    }

    public void setPriceAfter(double priceAfter) {
        this.priceAfter = priceAfter;
    }

    public double getDscMoneyAfter() {
        return dscMoneyAfter;
    }

    public void setDscMoneyAfter(double dscMoneyAfter) {
        this.dscMoneyAfter = dscMoneyAfter;
    }

    public double getDscOtherAfter() {
        return dscOtherAfter;
    }

    public void setDscOtherAfter(double dscOtherAfter) {
        this.dscOtherAfter = dscOtherAfter;
    }

    public int getDscRateAfter() {
        return (int) Math.round((dscMoneyAfter + dscOtherAfter) * 100 / total);
    }

    public double getTotalAfter() {
        return totalAfter;
    }

    public void setTotalAfter(double totalAfter) {
        this.totalAfter = totalAfter;
    }
}
