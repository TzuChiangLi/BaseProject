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
    // 支持的卡类型：1-磁卡，8-M1卡
    private int cardTypes;
    // 磁卡轨道号（会员卡号所在轨道）：1～3
    private int magTrackNo;
    // M1卡扇区号（会员卡号所在扇区）：0～15
    private int m1Sector;
    // M1卡块号（会员卡号所在块）：0～2
    private int m1Block;
    // M1卡读取密码，6字节（12个十六进制字符），默认为“FFFFFFFFFFFF”
    private String m1Key;
    // M1卡密码类型：0-KeyA，1-KeyB
    private int m1KeyType;
    // M1卡扇区号（卡余额所在扇区）：0～15
    private int m1MSector;
    // M1卡块号（卡余额所在块）：0～2
    private int m1MBlock;
    // M1卡写密码，6字节（12个十六进制字符），默认为“FFFFFFFFFFFF”
    private String m1WKey;
    // M1卡写密码类型：0-KeyA，1-KeyB
    private int m1WKeyType;
    // 失败重试次数
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
        config.magTrackNo = 2;
        config.m1Sector = 1;
        config.m1Block = 0;
        config.m1Key = "FFFFFFFFFFFF";
        config.m1KeyType = 0;
        config.m1MSector = 1;
        config.m1MBlock = 1;
        config.m1WKey = "FFFFFFFFFFFF";
        config.m1WKeyType = 0;
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
//        return cardTypes;
        return def().cardTypes;
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
    }

    public byte[] getM1KeyBytes() {
        return ByteUtil.hexStr2Bytes(m1Key);
    }

    public int getM1KeyType() {
        return m1KeyType;
    }

    public void setM1Sector(int m1Sector) {
        this.m1Sector = m1Sector;
    }

    public void setM1Block(int m1Block) {
        this.m1Block = m1Block;
    }

    public void setM1KeyType(int m1KeyType) {
        this.m1KeyType = m1KeyType;
    }

    public int getM1MSector() {
        return m1MSector;
    }

    public void setM1MSector(int m1MSector) {
        this.m1MSector = m1MSector;
    }

    public int getM1MBlock() {
        return m1MBlock;
    }

    public void setM1MBlock(int m1MBlock) {
        this.m1MBlock = m1MBlock;
    }

    public String getM1WKey() {
        return m1WKey;
    }

    public void setM1WKey(String m1WKey) {
        this.m1WKey = m1WKey;
    }

    public byte[] getM1WKeyBytes() {
        return ByteUtil.hexStr2Bytes(m1WKey);
    }

    public int getM1WKeyType() {
        return m1WKeyType;
    }

    public void setM1WKeyType(int m1WKeyType) {
        this.m1WKeyType = m1WKeyType;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
