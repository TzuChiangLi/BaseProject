package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.wosai.upay.bean.UpayOrder;

import java.util.Date;

/**
 * 收钱吧交易请求
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/16
 */
@Table(database = ZgpDb.class)
public class SqbPayOrder extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String posCode;
    @Column
    private String depCode;
    @Column
    private String userCode;
    @Column
    private String lsNo;
    @Column
    private Date createTime;
    @Column
    private String requestNo;
    @Column
    private String requestType;
    @Column
    private String sn;
    @Column
    private String clientSn;
    @Column
    private String totalAmount;
    @Column
    private String payway;
    @Column
    private String dynamicId;
    @Column
    private String subject;
    @Column
    private String operator;
    @Column
    private String description;
    @Column
    private String extended;
    @Column
    private String reflect;
    @Column
    private String refundRequestNo;
    @Column
    private String refundAmount;
    @Column
    private String payModel;
    @Column
    private String refundModel;
    @Column
    private String revokeModel;

    public SqbPayOrder() {
    }

    public SqbPayOrder(UpayOrder order, String requestNo, String requestType, String lsNo) {
        this.posCode = ZgParams.getPosCode();
        this.depCode = ZgParams.getCurrentDep().getDepCode();
        this.userCode = ZgParams.getCurrentUser().getUserCode();
        this.lsNo = lsNo;
        this.createTime = new Date();
        this.requestNo = requestNo;
        this.requestType = requestType;
        this.sn = order.getSn();
        this.clientSn = order.getClient_sn();
        this.totalAmount = order.getTotal_amount();
        this.payway = order.getPayway();
        this.dynamicId = order.getDynamic_id();
        this.subject = order.getSubject();
        this.operator = order.getOperator();
        this.description = order.getDescription();
        this.extended = order.getExtended();
        this.reflect = order.getReflect();
        this.refundRequestNo = order.getRefund_request_no();
        this.refundAmount = order.getRefund_amount();
        this.payModel = order.getPayModel().name();
        //当前采用无UI模式，以下2个参数无效
        this.refundModel = "";//order.getRefundModel().name()
        this.revokeModel = "";//order.getRevokeModel().name()
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getLsNo() {
        return lsNo;
    }

    public void setLsNo(String lsNo) {
        this.lsNo = lsNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getClientSn() {
        return clientSn;
    }

    public void setClientSn(String clientSn) {
        this.clientSn = clientSn;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayway() {
        return payway;
    }

    public void setPayway(String payway) {
        this.payway = payway;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }

    public String getReflect() {
        return reflect;
    }

    public void setReflect(String reflect) {
        this.reflect = reflect;
    }

    public String getRefundRequestNo() {
        return refundRequestNo;
    }

    public void setRefundRequestNo(String refundRequestNo) {
        this.refundRequestNo = refundRequestNo;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getPayModel() {
        return payModel;
    }

    public void setPayModel(String payModel) {
        this.payModel = payModel;
    }

    public String getRefundModel() {
        return refundModel;
    }

    public void setRefundModel(String refundModel) {
        this.refundModel = refundModel;
    }

    public String getRevokeModel() {
        return revokeModel;
    }

    public void setRevokeModel(String revokeModel) {
        this.revokeModel = revokeModel;
    }
}
