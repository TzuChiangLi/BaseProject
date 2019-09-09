package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 商品信息
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = DBHelper.class)
public class DepProduct extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String prodCode;
    @Column
    private String barCode;
    @Column
    @NotNull
    private String prodName;
    @Column
    @NotNull
    private String clsCode;
    @Column
    private String cargoNo;
    @Column
    private String spec;
    @Column
    private String unit;
    @Column
    private double price = 0;
    @Column
    private String brand;
    @Column
    private int priceFlag = 0;
    @Column
    private int isLargess = 0;
    @Column
    private int forSaleRet;
    @Column
    private int forDsc;
    @Column
    private int forLargess = 0;
    @Column
    private double scoreSet;
    @Column
    private double vipPrice1;
    @Column
    private double vipPrice2;
    @Column
    private double vipPrice3;
    @Column
    private double vipRate1;
    @Column
    private double vipRate2;
    @Column
    private double vipRate3;
    @Column
    private double minimumPrice;

    public DepProduct() {
    }

    public DepProduct(String depCode, String prodCode, String barCode, String prodName, String clsCode, String spec, double price) {
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public double getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(double scoreSet) {
        this.scoreSet = scoreSet;
    }

    public double getVipPrice1() {
        return vipPrice1;
    }

    public void setVipPrice1(double vipPrice1) {
        this.vipPrice1 = vipPrice1;
    }

    public double getVipPrice2() {
        return vipPrice2;
    }

    public void setVipPrice2(double vipPrice2) {
        this.vipPrice2 = vipPrice2;
    }

    public double getVipPrice3() {
        return vipPrice3;
    }

    public void setVipPrice3(double vipPrice3) {
        this.vipPrice3 = vipPrice3;
    }

    public double getVipRate1() {
        return vipRate1;
    }

    public void setVipRate1(double vipRate1) {
        this.vipRate1 = vipRate1;
    }

    public double getVipRate2() {
        return vipRate2;
    }

    public void setVipRate2(double vipRate2) {
        this.vipRate2 = vipRate2;
    }

    public double getVipRate3() {
        return vipRate3;
    }

    public void setVipRate3(double vipRate3) {
        this.vipRate3 = vipRate3;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
}
