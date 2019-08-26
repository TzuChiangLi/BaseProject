package com.ftrend.zgp.model;

/**
 * 商品信息
 *
 * @author liziqiang@ftrend.cn
 */
public class DepProduct {
    private int ID;
    private String ProdCode;
    private String BarCode;
    private String ProdName;
    private String DepCode;
    private String ClsCode;
    private String CargoNo;
    private String Spec;
    private String Unit;
    private float Price;
    private String Brand;
    private int PriceFlag;
    private int IsLargess;
    private int ForSaleRet;
    private int ForDsc;
    private int ForLargess;
    private float ScoreSet;
    private float VipPrice1;
    private float VipPrice2;
    private float VipPrice3;
    private float VipRate1;
    private float VipRate2;
    private float VipRate3;
    private float MinimumPrice;

    public DepProduct(String prodCode, String prodName, String depCode, String clsCode) {
        ProdCode = prodCode;
        ProdName = prodName;
        DepCode = depCode;
        ClsCode = clsCode;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProdCode() {
        return ProdCode;
    }

    public void setProdCode(String prodCode) {
        ProdCode = prodCode;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public String getDepCode() {
        return DepCode;
    }

    public void setDepCode(String depCode) {
        DepCode = depCode;
    }

    public String getClsCode() {
        return ClsCode;
    }

    public void setClsCode(String clsCode) {
        ClsCode = clsCode;
    }

    public String getCargoNo() {
        return CargoNo;
    }

    public void setCargoNo(String cargoNo) {
        CargoNo = cargoNo;
    }

    public String getSpec() {
        return Spec;
    }

    public void setSpec(String spec) {
        Spec = spec;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public int getPriceFlag() {
        return PriceFlag;
    }

    public void setPriceFlag(int priceFlag) {
        PriceFlag = priceFlag;
    }

    public int getIsLargess() {
        return IsLargess;
    }

    public void setIsLargess(int isLargess) {
        IsLargess = isLargess;
    }

    public int getForSaleRet() {
        return ForSaleRet;
    }

    public void setForSaleRet(int forSaleRet) {
        ForSaleRet = forSaleRet;
    }

    public int getForDsc() {
        return ForDsc;
    }

    public void setForDsc(int forDsc) {
        ForDsc = forDsc;
    }

    public int getForLargess() {
        return ForLargess;
    }

    public void setForLargess(int forLargess) {
        ForLargess = forLargess;
    }

    public float getScoreSet() {
        return ScoreSet;
    }

    public void setScoreSet(float scoreSet) {
        ScoreSet = scoreSet;
    }

    public float getVipPrice1() {
        return VipPrice1;
    }

    public void setVipPrice1(float vipPrice1) {
        VipPrice1 = vipPrice1;
    }

    public float getVipPrice2() {
        return VipPrice2;
    }

    public void setVipPrice2(float vipPrice2) {
        VipPrice2 = vipPrice2;
    }

    public float getVipPrice3() {
        return VipPrice3;
    }

    public void setVipPrice3(float vipPrice3) {
        VipPrice3 = vipPrice3;
    }

    public float getVipRate1() {
        return VipRate1;
    }

    public void setVipRate1(float vipRate1) {
        VipRate1 = vipRate1;
    }

    public float getVipRate2() {
        return VipRate2;
    }

    public void setVipRate2(float vipRate2) {
        VipRate2 = vipRate2;
    }

    public float getVipRate3() {
        return VipRate3;
    }

    public void setVipRate3(float vipRate3) {
        VipRate3 = vipRate3;
    }

    public float getMinimumPrice() {
        return MinimumPrice;
    }

    public void setMinimumPrice(float minimumPrice) {
        MinimumPrice = minimumPrice;
    }
}
