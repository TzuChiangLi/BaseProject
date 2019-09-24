package com.ftrend.zgp.model;

/**
 * 会员信息
 *
 * @author liziqiang@ftrend.cn
 */

public class VipInfo {
    // {vipName=王美女,
    // rateRule=-1.0,优惠率规则
    // cardCode=0000000000083,
    // vipPriceType=0.0,
    // vipDscRate=100.0,会员优惠率
    // vipCode=0000000000083,
    // dscProdIsDsc=0,促销商品打折，暂时不做促销单，本参数无用
    // vipGrade=普卡,
    // forceDsc=0，强制打折}
    private String vipName;
    private String vipCode;
    private double rateRule;
    private String cardCode;
    private double vipPriceType;
    private double vipDscRate;
    private String dscProdIsDsc;
    private String vipGrade;
    private String forceDsc;

    public VipInfo() {
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
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


    public double getVipDscRate() {
        return vipDscRate;
    }

    public void setVipDscRate(double vipDscRate) {
        this.vipDscRate = vipDscRate;
    }

    public String getDscProdIsDsc() {
        return dscProdIsDsc;
    }

    public void setDscProdIsDsc(String dscProdIsDsc) {
        this.dscProdIsDsc = dscProdIsDsc;
    }

    public String getVipGrade() {
        return vipGrade;
    }

    public void setVipGrade(String vipGrade) {
        this.vipGrade = vipGrade;
    }

    public String getForceDsc() {
        return forceDsc;
    }

    public void setForceDsc(String forceDsc) {
        this.forceDsc = forceDsc;
    }

    public double getRateRule() {
        return rateRule;
    }

    public void setRateRule(double rateRule) {
        this.rateRule = rateRule;
    }

    public double getVipPriceType() {
        return vipPriceType;
    }

    public void setVipPriceType(double vipPriceType) {
        this.vipPriceType = vipPriceType;
    }
}
