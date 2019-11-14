package com.ftrend.zgp.utils.pay;

import com.blankj.utilcode.util.GsonUtils;

/**
 * 收钱吧SDK配置参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/22
 */
public class SqbConfig {

    /** 收钱吧接入域名 */
//    public static final String apiDomain = "https://api.shouqianba.com";
    /**
     * 服务商ID
     */
    private final String vendorId = "20160407103537";
    /**
     * 服务商Key
     */
    private final String vendorKey = "a8bbaa0aeb3daf40f5924a3a9b694d00";
    /**
     * 激活码
     */
    private String activateCode = "";
    /**
     * 商户号
     */
    private String merchantNo = "";
    /**
     * 校验码
     */
    private String verifyCode = "";
    /** 服务商AppId */
//    private String appId = "";//ftrend.zgpos
    /** 终端号 */
//    private String terminalSn = "100035370009266786";
    /** 终端密钥 */
//    private String terminalKey = "316849581b75841b8b3f61f7cac0194f";
// "terminal_sn":"100035370009266786","terminal_key":"316849581b75841b8b3f61f7cac0194f",
// "merchant_sn":"1680000635494","merchant_name":"青岛方象",
// "store_sn":"1580000002217123","store_name":"方象测试"
    /**
     * 是否打开交易完成时,成功和失败的提示声音
     */
    private boolean playSound = false;

    public SqbConfig() {
    }

    /**
     * 从json解析配置参数
     *
     * @param json
     * @return
     */
    public static SqbConfig fromJson(String json) {
        try {
            return GsonUtils.fromJson(json, SqbConfig.class);
        } catch (Exception e) {
            return new SqbConfig();
        }
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getVendorKey() {
        return vendorKey;
    }

    public String getActivateCode() {
        return activateCode;
    }

    public void setActivateCode(String activateCode) {
        this.activateCode = activateCode;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }
}
