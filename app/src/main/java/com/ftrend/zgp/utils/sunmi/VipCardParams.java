package com.ftrend.zgp.utils.sunmi;

import com.blankj.utilcode.util.GsonUtils;

import java.util.Map;

/**
 * 会员卡参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/24
 */
public class VipCardParams {
    /**
     * 磁卡卡号是否加密：1-加密，0-不加密
     */
    private boolean decryptCard = false;

    /**
     * 磁卡卡号加密密码
     */
    private String cardPass = "";

    /**
     * 卡号最大长度
     */
    private int vipCodeMaxLen = 13;

    /**
     * 卡号和会员编码固定长度：1-固定，0-不固定
     */
    private boolean vipAndCardFixLen = true;

    /**
     * 卡号前缀
     */
    private String cardPreCode = "0000000000000";

    public VipCardParams() {
    }

    public static VipCardParams fromJson(String json) {
        VipCardParams params = new VipCardParams();
        try {
            Map<String, Object> map = GsonUtils.fromJson(json, Map.class);
            for (String key : map.keySet()) {
                if (key.equalsIgnoreCase("decryptCard")) {
                    params.decryptCard = "1".equals(map.get(key));
                } else if (key.equalsIgnoreCase("cardPass")) {
                    params.cardPass = String.valueOf(map.get(key));
                } else if (key.equalsIgnoreCase("vipCodeMaxLen")) {
                    params.vipCodeMaxLen = Integer.valueOf(map.get(key).toString());
                } else if (key.equalsIgnoreCase("vipAndCardFixLen")) {
                    params.vipAndCardFixLen = "1".equals(map.get(key));
                } else if (key.equalsIgnoreCase("cardPreCode")) {
                    params.cardPreCode = String.valueOf(map.get(key));
                }
            }
        } catch (Exception e) {
            //
        }
        return params;
    }

    public boolean isDecryptCard() {
        return decryptCard;
    }

    public String getCardPass() {
        return cardPass;
    }

    public int getVipCodeMaxLen() {
        return vipCodeMaxLen;
    }

    public boolean isVipAndCardFixLen() {
        return vipAndCardFixLen;
    }

    public String getCardPreCode() {
        return cardPreCode;
    }

    public void setDecryptCard(boolean decryptCard) {
        this.decryptCard = decryptCard;
    }

    public void setCardPass(String cardPass) {
        this.cardPass = cardPass;
    }

    public void setVipCodeMaxLen(int vipCodeMaxLen) {
        this.vipCodeMaxLen = vipCodeMaxLen;
    }

    public void setVipAndCardFixLen(boolean vipAndCardFixLen) {
        this.vipAndCardFixLen = vipAndCardFixLen;
    }

    public void setCardPreCode(String cardPreCode) {
        this.cardPreCode = cardPreCode;
    }
}
