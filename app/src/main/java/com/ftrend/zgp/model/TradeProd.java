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
    @Column
    private double singleDsc = 0;
    @Column
    private double wholeDsc = 0;
    @Column
    private int prodForDsc;
    @Column
    private int prodPriceFlag = 0;
    @Column
    private int prodIsLargess = 0;
    @Column
    private double prodMinPrice;
    /**
     * 为了改变变量选中状态而创建，与数据库字段无关
     */
    private transient boolean isSelect = false;
    // 退货字段（与数据库字段无关）：实退商品数量
    private transient double rtnAmount = 0;
    // 退货字段（与数据库字段无关）：实退商品单价
    private transient double rtnPrice;
    // 退货字段（与数据库字段无关）：实退小计金额
    private transient double rtnTotal;
    // 已退货数量（负数）
    private transient double lastRtnAmount = 0;
    // 已退货金额（负数）
    private transient double lastRtnTotal = 0;

    public TradeProd() {
    }

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
        return singleDsc + wholeDsc;
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

    public double getTotalDsc() {
        return singleDsc + wholeDsc + vipDsc + tranDsc;
    }

    public double getTotal() {
        total = price * amount - getTotalDsc();
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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public double getSingleDsc() {
        return singleDsc;
    }

    public void setSingleDsc(double singleDsc) {
        this.singleDsc = singleDsc;
    }

    public double getWholeDsc() {
        return wholeDsc;
    }

    public void setWholeDsc(double wholeDsc) {
        this.wholeDsc = wholeDsc;
    }

    public int getProdForDsc() {
        return prodForDsc;
    }

    public boolean isForDsc() {
        return prodIsLargess == 0 && prodForDsc == 1;
    }

    public void setProdForDsc(int prodForDsc) {
        this.prodForDsc = prodForDsc;
    }

    public int getProdPriceFlag() {
        return prodPriceFlag;
    }

    public void setProdPriceFlag(int prodPriceFlag) {
        this.prodPriceFlag = prodPriceFlag;
    }

    public int getProdIsLargess() {
        return prodIsLargess;
    }

    public void setProdIsLargess(int prodIsLargess) {
        this.prodIsLargess = prodIsLargess;
    }

    public double getProdMinPrice() {
        return prodMinPrice;
    }

    public void setProdMinPrice(double prodMinPrice) {
        this.prodMinPrice = prodMinPrice;
    }


    public double getRtnAmount() {
        return rtnAmount;
    }

    public void setRtnAmount(double rtnAmount) {
        this.rtnAmount = rtnAmount;
    }

    public double getRtnTotal() {
        return rtnTotal;
    }

    public void setRtnTotal(double rtnTotal) {
        this.rtnTotal = rtnTotal;
    }

    public double getRtnPrice() {
        return rtnPrice;
    }

    public void setRtnPrice(double rtnPrice) {
        this.rtnPrice = rtnPrice;
    }

    public double getLastRtnAmount() {
        return lastRtnAmount;
    }

    public void setLastRtnAmount(double lastRtnAmount) {
        this.lastRtnAmount = lastRtnAmount;
    }

    public double getLastRtnTotal() {
        return lastRtnTotal;
    }

    public void setLastRtnTotal(double lastRtnTotal) {
        this.lastRtnTotal = lastRtnTotal;
    }
}
