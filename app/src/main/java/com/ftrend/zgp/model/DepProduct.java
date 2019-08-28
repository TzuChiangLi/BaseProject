package com.ftrend.zgp.model;

/**
 * 商品信息
 *
 * @author liziqiang@ftrend.cn
 */
public class DepProduct {
    private int id;
    private String depCode;
    private String prodCode;
    private String barCode;
    private String prodName;
    private String clsCode;
    private String cargoNo;
    private String spec;
    private String unit;
    private float price;
    private String brand;
    private int priceFlag;
    private int isLargess;
    private int forSaleRet;
    private int forDsc;
    private int forLargess;
    private float scoreSet;
    private float vipPrice1;
    private float vipPrice2;
    private float vipPrice3;
    private float vipRate1;
    private float vipRate2;
    private float vipRate3;
    private float minimumPrice;

    public DepProduct() {
    }

    public DepProduct(String depCode, String prodCode, String barCode, String prodName, String clsCode, String spec, float price) {
        this.depCode = depCode;
        this.prodCode = prodCode;
        this.barCode = barCode;
        this.prodName = prodName;
        this.clsCode = clsCode;
        this.spec = spec;
        this.price = price;
    }

    public DepProduct(String prodCode, String prodName, String depCode, String clsCode) {
        this.prodCode = prodCode;
        this.prodName = prodName;
        this.depCode = depCode;
        this.clsCode = clsCode;
    }


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }


    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getClsCode() {
        return clsCode;
    }

    public void setClsCode(String clsCode) {
        this.clsCode = clsCode;
    }

    public String getCargoNo() {
        return cargoNo;
    }

    public void setCargoNo(String cargoNo) {
        this.cargoNo = cargoNo;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getPriceFlag() {
        return priceFlag;
    }

    public void setPriceFlag(int priceFlag) {
        this.priceFlag = priceFlag;
    }

    public int getIsLargess() {
        return isLargess;
    }

    public void setIsLargess(int isLargess) {
        this.isLargess = isLargess;
    }

    public int getForSaleRet() {
        return forSaleRet;
    }

    public void setForSaleRet(int forSaleRet) {
        this.forSaleRet = forSaleRet;
    }

    public int getForDsc() {
        return forDsc;
    }

    public void setForDsc(int forDsc) {
        this.forDsc = forDsc;
    }

    public int getForLargess() {
        return forLargess;
    }

    public void setForLargess(int forLargess) {
        this.forLargess = forLargess;
    }

    public float getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(float scoreSet) {
        this.scoreSet = scoreSet;
    }

    public float getVipPrice1() {
        return vipPrice1;
    }

    public void setVipPrice1(float vipPrice1) {
        this.vipPrice1 = vipPrice1;
    }

    public float getVipPrice2() {
        return vipPrice2;
    }

    public void setVipPrice2(float vipPrice2) {
        this.vipPrice2 = vipPrice2;
    }

    public float getVipPrice3() {
        return vipPrice3;
    }

    public void setVipPrice3(float vipPrice3) {
        this.vipPrice3 = vipPrice3;
    }

    public float getVipRate1() {
        return vipRate1;
    }

    public void setVipRate1(float vipRate1) {
        this.vipRate1 = vipRate1;
    }

    public float getVipRate2() {
        return vipRate2;
    }

    public void setVipRate2(float vipRate2) {
        this.vipRate2 = vipRate2;
    }

    public float getVipRate3() {
        return vipRate3;
    }

    public void setVipRate3(float vipRate3) {
        this.vipRate3 = vipRate3;
    }

    public float getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(float minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
}
