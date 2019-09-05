package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.DBHelper;
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
@Table(database = DBHelper.class)
public class TradeProd extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String lsNo;
    @Column
    @NotNull
    private String sortNo;
    @Column
    @NotNull
    private String prodCode;
    @Column
    private String barCode;
    @Column
    @NotNull
    private String prodName = "splash";
    @Column
    private String depCode;
    @Column
    private float price = 0;
    @Column
    @NotNull
    private float amount = 0;
    @Column
    private float manuDsc = 0;
    @Column
    private float vipDsc = 0;
    @Column
    private float tranDsc;
    @Column
    private float total;
    @Column
    private float vipTotal;
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

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getManuDsc() {
        return manuDsc;
    }

    public void setManuDsc(float manuDsc) {
        this.manuDsc = manuDsc;
    }

    public float getVipDsc() {
        return vipDsc;
    }

    public void setVipDsc(float vipDsc) {
        this.vipDsc = vipDsc;
    }

    public float getTranDsc() {
        return tranDsc;
    }

    public void setTranDsc(float tranDsc) {
        this.tranDsc = tranDsc;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getVipTotal() {
        return vipTotal;
    }

    public void setVipTotal(float vipTotal) {
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
