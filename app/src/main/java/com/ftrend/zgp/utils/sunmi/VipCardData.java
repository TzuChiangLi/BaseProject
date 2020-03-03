package com.ftrend.zgp.utils.sunmi;

import android.text.TextUtils;

import com.ftrend.zgp.utils.common.EncryptUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

import java.util.Locale;

/**
 * 会员卡数据
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/18
 */
public class VipCardData {
    private AidlConstantsV2.CardType cardType;
    /**
     * 卡号
     */
    private String cardCode;
    /**
     * 储值余额
     */
    private Double money;
    /**
     * 消费密码
     */
    private String vipPwd;

    public VipCardData(AidlConstantsV2.CardType cardType) {
        this.cardType = cardType;
        this.cardCode = "";
        this.money = 0.0;
        this.vipPwd = "";
    }

    public VipCardData(VipCardData src) {
        copy(src);
    }

    /**
     * 校验卡数据是否有效
     *
     * @return
     */
    public boolean isValid() {
        return (cardType != null) && !TextUtils.isEmpty(cardCode);
    }

    /**
     * 数据复制
     *
     * @param src
     */
    public void copy(VipCardData src) {
        this.cardType = src.cardType;
        this.cardCode = src.cardCode;
        this.money = src.money;
        this.vipPwd = src.vipPwd;
    }

    /**
     * 判断卡片内容是否一致（仅判断卡号和金额）
     *
     * @param other
     * @return
     */
    public boolean equals(VipCardData other) {
        return cardCode.equalsIgnoreCase(other.cardCode)
                && Math.abs(money - other.money) < 0.01D;//防止浮点数误差
    }

    public AidlConstantsV2.CardType getCardType() {
        return cardType;
    }

    public void setCardType(AidlConstantsV2.CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getVipPwd() {
        return vipPwd;
    }

    public String getVipPwdDecrypted() {
        return EncryptUtil.cardPwdDecrypt(vipPwd).trim();
    }

    public void setVipPwd(String vipPwd) {
        this.vipPwd = vipPwd;
    }

    public String toLogString() {
        return String.format(Locale.CHINA, "卡号:%s, 余额:%.2f, 密码:%s", cardCode, money,
                TextUtils.isEmpty(getVipPwdDecrypted()) ? "无" : "有");
    }

    @Override
    public String toString() {
        return "VipCardData{" +
                "cardCode='" + cardCode + '\'' +
                ", money=" + money +
                ", vipPwd='" + (TextUtils.isEmpty(getVipPwdDecrypted()) ? "无" : "有") + '\'' +
                '}';
    }
}
