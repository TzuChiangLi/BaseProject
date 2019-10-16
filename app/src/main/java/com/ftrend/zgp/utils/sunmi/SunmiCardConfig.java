package com.ftrend.zgp.utils.sunmi;

import com.blankj.utilcode.util.GsonUtils;
import com.ftrend.zgp.utils.common.ByteUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

/**
 * 商米读卡器参数设置
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/23
 */
public class SunmiCardConfig {
    // （1）支持的卡类型：1-磁卡，8-M1卡
    private int cardTypes;
    // （2）磁卡轨道号（会员卡号所在轨道）：1～3
    private int magTrackNo;
    // （3）M1卡扇区号（会员卡号所在扇区）：0～15
    private int m1Sector;
    // （4）M1卡块号（会员卡号所在块）：0～2
    private int m1Block;
    // （5）M1卡读取密码，6字节（12个十六进制字符），默认为“FFFFFFFFFFFF”
    private String m1Key;
    private transient byte[] m1KeyBytes;
    // （6）M1卡密码类型：0-KeyA，1-KeyB
    private int m1KeyType;
    // （7）失败重试次数
    private final int retryCount = 10;

    /**
     * 返回读卡器默认参数
     *
     * @return
     */
    public static SunmiCardConfig def() {
        SunmiCardConfig config = new SunmiCardConfig();
        config.cardTypes = AidlConstantsV2.CardType.MIFARE.getValue()
                | AidlConstantsV2.CardType.MAGNETIC.getValue();
        config.m1Sector = 1;
        config.m1Block = 0;
        config.m1Key = "FFFFFFFFFFFF";
        config.m1KeyType = 0;
        return config;
    }

    /**
     * 从json解析读卡器参数
     *
     * @param json
     * @return
     */
    public static SunmiCardConfig fromJson(String json) {
        try {
            return GsonUtils.fromJson(json, SunmiCardConfig.class);
        } catch (Exception e) {
            return SunmiCardConfig.def();
        }
    }

    public int getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(int cardTypes) {
        this.cardTypes = cardTypes;
    }

    public int getMagTrackNo() {
        return magTrackNo;
    }

    public void setMagTrackNo(int magTrackNo) {
        this.magTrackNo = magTrackNo;
    }

    public int getM1Sector() {
        return m1Sector;
    }

    public int getM1Block() {
        return m1Block;
    }

    public String getM1Key() {
        return m1Key;
    }

    public void setM1Key(String m1Key) {
        this.m1Key = m1Key;
        this.m1KeyBytes = ByteUtil.hexStr2Bytes(m1Key);
    }

    public byte[] getM1KeyBytes() {
        return m1KeyBytes;
    }

    public int getM1KeyType() {
        return m1KeyType;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
