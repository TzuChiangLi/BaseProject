package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


/**
 * 交易商品表
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class TradeProd extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String lsNo;
    @Column
    @NotNull
    private Long sortNo;
    @Column
    @NotNull
    private String prodCode;
    @Column
    private String barCode;
    @Column
    @NotNull
    private String prodName;
    @Column
    private String depCode;
    @Column
    private double price = 0;
    @Column
    @NotNull
    private double amount = 0;
    @Column
    private double manuDsc = 0;
    @Column
    private double vipDsc = 0;
    @Column
    private double tranDsc;
    @Column
    private double total;
    @Column
    private double vipTotal;
    @Column
    private String saleInfo;
    @Column
    private String delFlag = "0";
//
//    public TradeProd() {
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLsNo() {
        return lsNo;
    }

    public void setLsNo(String lsNo) {
        this.lsNo = lsNo;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(Long sortNo) {
        this.sortNo = sortNo;
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

    public double getManuDsc() {
        return manuDsc;
    }

    public void setManuDsc(double manuDsc) {
        this.manuDsc = manuDsc;
    }

    public double getVipDsc() {
        return vipDsc;
    }

    public void setVipDsc(double vipDsc) {
        this.vipDsc = vipDsc;
    }

    public double getTranDsc() {
        return tranDsc;
    }

    public void setTranDsc(double tranDsc) {
        this.tranDsc = tranDsc;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getVipTotal() {
        return vipTotal;
    }

    public void setVipTotal(double vipTotal) {
        this.vipTotal = vipTotal;
    }

    public String getSaleInfo() {
        return saleInfo;
    }

    public void setSaleInfo(String saleInfo) {
        this.saleInfo = saleInfo;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
