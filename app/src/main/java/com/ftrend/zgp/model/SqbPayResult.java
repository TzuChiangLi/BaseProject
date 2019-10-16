package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.wosai.upay.bean.UpayResult;

/**
 * 收钱吧交易结果
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/16
 */
@Table(database = ZgpDb.class)
public class SqbPayResult extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String requestNo;
    @Column
    private String sn;
    @Column
    private String clientSn;
    @Column
    private String tradeNo;
    @Column
    private String status;
    @Column
    private String orderStatus;
    @Column
    private String payway;
    @Column
    private String subPayway;
    @Column
    private String payerUid;
    @Column
    private String payerLogin;
    @Column
    private String totalAmount;
    @Column
    private String netAmount;
    @Column
    private String subject;
    @Column
    private String finishTime;
    @Column
    private String channelFinishTime;
    @Column
    private String operator;
    @Column
    private String description;
    @Column
    private String reflect;
    @Column
    private String qrCode;
    @Column
    private String resultCode;
    @Column
    private String errorCode;
    @Column
    private String errorMessage;

    public SqbPayResult() {
    }

    public SqbPayResult(UpayResult result, String requestNo) {
        this.requestNo = requestNo;
        this.sn = result.getSn();
        this.clientSn = result.getClient_sn();
        this.tradeNo = result.getTrade_no();
        this.status = result.getStatus();
        this.orderStatus = result.getOrder_status();
        this.payway = result.getPayway();
        this.subPayway = result.getSub_payway();
        this.payerUid = result.getPayer_uid();
        this.payerLogin = result.getPayer_login();
        this.totalAmount = result.getTotal_amount();
        this.netAmount = result.getNet_amount();
        this.subject = result.getSubject();
        this.finishTime = result.getFinish_time();
        this.channelFinishTime = result.getChannel_finish_time();
        this.operator = result.getOperator();
        this.description = result.getDescription();
        this.reflect = result.getReflect();
        this.qrCode = result.getQr_code();
        this.resultCode = result.getResult_code();
        this.errorCode = result.getError_code();
        this.errorMessage = result.getError_message();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
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

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayway() {
        return payway;
    }

    public void setPayway(String payway) {
        this.payway = payway;
    }

    public String getSubPayway() {
        return subPayway;
    }

    public void setSubPayway(String subPayway) {
        this.subPayway = subPayway;
    }

    public String getPayerUid() {
        return payerUid;
    }

    public void setPayerUid(String payerUid) {
        this.payerUid = payerUid;
    }

    public String getPayerLogin() {
        return payerLogin;
    }

    public void setPayerLogin(String payerLogin) {
        this.payerLogin = payerLogin;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getChannelFinishTime() {
        return channelFinishTime;
    }

    public void setChannelFinishTime(String channelFinishTime) {
        this.channelFinishTime = channelFinishTime;
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

    public String getReflect() {
        return reflect;
    }

    public void setReflect(String reflect) {
        this.reflect = reflect;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
