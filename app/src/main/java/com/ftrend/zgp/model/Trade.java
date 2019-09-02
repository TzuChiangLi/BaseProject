package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * 交易流水表
 *
 * @author LZQ
 */
@Table(database = DBHelper.class)
public class Trade extends BaseModel {
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
    private float dscTotal;
    @Column
    private float total;
    @Column
    private String custType;
    @Column
    private String vipCode;
    @Column
    private String cardCode;
    @Column
    private float vipTotal = 0;
    @Column
    @NotNull
    private String status = "0";
    @Column
    private String CreateTime;
    @Column
    private String CreateIp;


//    public TradeUtil() {
//    }

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

    public float getDscTotal() {
        return dscTotal;
    }

    public void setDscTotal(float dscTotal) {
        this.dscTotal = dscTotal;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
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

    public float getVipTotal() {
        return vipTotal;
    }

    public void setVipTotal(float vipTotal) {
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

    public String getCreateIp() {
        return CreateIp;
    }

    public void setCreateIp(String createIp) {
        CreateIp = createIp;
    }
}