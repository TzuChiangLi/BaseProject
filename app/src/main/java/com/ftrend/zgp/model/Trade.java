package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 交易流水表
 *
 * @author LZQ
 */
@Table(database = ZgpDb.class)
public class Trade extends BaseModel implements Cloneable {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String lsNo;
    @Column
    private Date tradeTime;
    @Column
    @NotNull
    private String tradeFlag;
    @Column
    @NotNull
    private String cashier;
    @Column
    private double dscTotal;
    @Column
    private double total;
    @Column
    private String custType;
    @Column
    private String vipCode;
    @Column
    private String cardCode;
    @Column
    private double vipTotal = 0;
    @Column
    @NotNull
    private String status = "0";
    @Column
    private String CreateTime;
    @Column
    private String CreateIp;
    //取单字段(非数据库字段)：流水单内第一个商品
    private transient String prodName;
    //取单字段(非数据库字段)：第一个商品的数量
    private transient double prodNum;
    //取单字段(非数据库字段)：该流水单的商品总件数
    private transient double amount;
    //退货字段(非数据库字段)：实退金额
    private transient double rtnTotal;
    //退货字段（非数据库字段）：实退商品数量
    private transient double rtnAmount = 0;
    //退货字段（非数据库字段）：是否退货 1-已退  0-未退
    private transient String rtnFlag = "0";


    public Trade() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getLsNo() {
        return lsNo;
    }

    /**
     * 获取完整流水号：日期+lsNo格式
     *
     * @return
     */
    public String getFullLsNo() {
        return CommonUtil.dateToYyyyMmDd(tradeTime) + lsNo;
    }

    public void setLsNo(String lsNo) {
        this.lsNo = lsNo;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(String tradeFlag) {
        this.tradeFlag = tradeFlag;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public double getDscTotal() {
        return dscTotal;
    }

    public void setDscTotal(double dscTotal) {
        this.dscTotal = dscTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
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

    public double getVipTotal() {
        return vipTotal;
    }

    public void setVipTotal(double vipTotal) {
        this.vipTotal = vipTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(createTime);
    }

    public String getCreateIp() {
        return CreateIp;
    }

    public void setCreateIp(String createIp) {
        CreateIp = createIp;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public double getProdNum() {
        return prodNum;
    }

    public void setProdNum(double prodNum) {
        this.prodNum = prodNum;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRtnTotal() {
        return rtnTotal;
    }

    public void setRtnTotal(double rtnTotal) {
        this.rtnTotal = rtnTotal;
    }

    public double getRtnAmount() {
        return rtnAmount;
    }

    public void setRtnAmount(double rtnAmount) {
        this.rtnAmount = rtnAmount;
    }

    public String getRtnFlag() {
        return rtnFlag;
    }

    public void setRtnFlag(String rtnFlag) {
        this.rtnFlag = rtnFlag;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Trade trade = null;
        try {
            trade = (Trade) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return trade;
    }

}
