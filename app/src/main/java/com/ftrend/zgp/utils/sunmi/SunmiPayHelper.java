package com.ftrend.zgp.utils.sunmi;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ftrend.zgp.App;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Date;
import java.util.Locale;

import sunmi.paylib.SunmiPayKernel;

/**
 * 商米支付SDK功能封装
 * 商品支付SDK集成在设备中，以后台服务方式运行
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/20
 */
public class SunmiPayHelper {
    private final String TAG = "SunmiPayHelper";
    private SunmiPayKernel kernel = null;
    // 是否已连接到支付SDK
    private boolean sdkConnected = false;
    // 读卡器操作对象
    private ReadCardOptV2 readCardOptV2 = null;
    // 读卡器参数
    private SunmiCardConfig cardConfig = null;
    // 读卡回调
    private ReadCardCallback readCardCallback = null;
    // 写卡回调
    private WriteCardCallback writeCardCallback = null;
    // 写卡数据
    private VipCardData writeCardData = null;
    // 上一次写卡的信息（用于写卡校验失败后，避免重复写卡）
    private WriteCardInfo lastWriteInfo = null;
    // 上次写卡的交易流水号（用于写卡校验失败后，避免重复写卡）
    private String lastWriteLs = "";
    // 卡片状态
    private CardState cardState = CardState.None;
    // 操作类型
    private OpType opType = OpType.None;
    // 失败次数
    private int failCount = 0;

    private static SunmiPayHelper helper = null;

    /**
     * 获取静态实例
     *
     * @return
     */
    public static SunmiPayHelper getInstance() {
        if (helper == null) {
            helper = new SunmiPayHelper();
        }
        return helper;
    }

    /**
     * 连接到支付SDK服务
     */
    public void connectPayService() {
        if (sdkConnected) {
            return;
        }
        kernel = SunmiPayKernel.getInstance();
        kernel.initPaySDK(App.getContext(), mConnectCallback);
    }

    /**
     * 释放到支付SDK服务的连接
     */
    public void disconnectPayService() {
        try {
            if (!sdkConnected) {
                return;
            }
            cancelCheckCard();
            kernel.destroyPaySDK();
            kernel = null;
        } catch (Exception e) {
            LogUtil.u(TAG, "释放刷卡SDK", e.getMessage());
        }
    }

    /**
     * 服务是否可用
     *
     * @return
     */
    public boolean serviceAvailable() {
        return sdkConnected;
    }

    /**
     * 当前是否正在读卡
     *
     * @return
     */
    public boolean isReading() {
        return opType == OpType.Read;
    }

    /**
     * 当前是否正在写卡
     *
     * @return
     */
    public boolean isWriting() {
        return opType == OpType.Init || opType == OpType.Update;
    }

    /**
     * 读取卡片信息：检卡、认证、读卡
     *
     * @param callback 读卡回调
     */
    public void readCard(ReadCardCallback callback) {
        if (!sdkConnected) {
            LogUtil.u(TAG, "读取卡片", "刷卡服务不可用");
            callback.onError("刷卡服务不可用");
            return;
        }
        LogUtil.u(TAG, "读取卡片", "读取卡片信息");
        this.readCardCallback = callback;
        this.opType = OpType.Read;
        checkCard(false);
    }

    /**
     * 取消读卡
     */
    public void cancelReadCard() {
        if (!sdkConnected) {
            return;
        }
        LogUtil.u(TAG, "取消读卡", "取消读卡");
        cancelCheckCard();
    }

    /**
     * 写卡：更新卡余额
     *
     * @param data
     * @param callback
     */
    public void writeCard(VipCardData data, String lsNo, WriteCardCallback callback) {
        if (!sdkConnected) {
            LogUtil.u(TAG, "写卡", "刷卡服务不可用");
            callback.onError("刷卡服务不可用");
            return;
        }
        //记录上次写卡流水号，防止重复写卡
        lastWriteLs = lsNo;

        LogUtil.u(TAG, "写卡", "更新卡余额：" + data.toLogString());
        this.writeCardData = data;
        this.writeCardCallback = callback;
        this.opType = OpType.Update;
        checkCard(false);
    }

    /**
     * 取消写卡（只能在找到卡之前取消）
     */
    public void cancelWriteCard() {
        if (!sdkConnected) {
            return;
        }
        LogUtil.u(TAG, "取消写卡", "取消写卡");
        cancelCheckCard();
    }

    /**
     * 校验卡信息
     *
     * @param cardData
     */
    private void verifyCard(final VipCardData cardData) {
        this.readCardCallback = new ReadCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                if (cardData.equals(data)) {
                    lastWriteInfo = null;
                    writeCardSuccess(cardData);
                    LogUtil.u(TAG, "IC卡写卡成功", data.toLogString());
                } else {
                    LogUtil.u(TAG, "IC卡写卡失败",
                            String.format(Locale.CHINA, "写卡校验失败(%.2f/%.2f)", cardData.getMoney(), data.getMoney()));
                    writeCardFail(
                            String.format(Locale.CHINA, "写卡校验失败(%.2f/%.2f)", cardData.getMoney(), data.getMoney()));
                }
            }

            @Override
            public void onError(String msg) {
                LogUtil.u(TAG, "IC卡写卡失败", "校验读卡失败：" + msg);
                writeCardFail("写卡校验失败：" + msg);
            }
        };
        this.opType = OpType.Read;
        m1ReadCard(false);//不重新认证
    }

    /**
     * 连接状态回调
     */
    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {

        @Override
        public void onConnectPaySDK() {
            try {
                readCardOptV2 = kernel.mReadCardOptV2;
                cardConfig = ZgParams.getCardConfig();
                sdkConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.u(TAG, "SDK初始化", "SDK初始化异常：" + e.getMessage());
            }
        }

        @Override
        public void onDisconnectPaySDK() {
            sdkConnected = false;
        }

    };

    /**
     * 检卡
     */
    private void checkCard(boolean isRetry) {
        try {
            if (!isRetry) {
                //检卡前，先取消检卡
                cancelCheckCard();
                cardState = CardState.Finding;
                failCount = 0;
            }
            readCardOptV2.checkCard(cardConfig.getCardTypes(), mCheckCardCallback, 120);
            LogUtil.u(TAG, "检卡", String.format("卡片类型：%s", String.valueOf(cardConfig.getCardTypes())));
        } catch (Exception e) {
            cardState = CardState.Error;
            LogUtil.u(TAG, "检卡", "检卡异常：" + e.getMessage());
            //回调
            readCardFail("检卡异常");
        }
    }

    /**
     * 取消检卡
     */
    private void cancelCheckCard() {
        try {
            readCardOptV2.cardOff(cardConfig.getCardTypes());
            readCardOptV2.cancelCheckCard();
            LogUtil.u(TAG, "取消检卡", "取消检卡");
        } catch (Exception e) {
            LogUtil.u(TAG, "取消检卡",
                    String.format(Locale.CHINA, "取消检卡异常：%s" + e.getMessage()));
        }
    }

    /**
     * 查找卡片回调
     */
    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2.Stub() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            cardState = CardState.Found;
            if (opType == OpType.Read) {
                handleMagneticData(bundle);
                LogUtil.u(TAG, "检卡成功", "磁卡");
            }//磁卡只能读，不能写
        }

        @Override
        public void findICCard(String atr) throws RemoteException {

        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            cardState = CardState.Found;
            LogUtil.u(TAG, "检卡成功", "IC卡");
            switch (opType) {
                case Read:
                default:
                    m1ReadCard();
                    break;
                case Update:
                    m1UpdateMoney(writeCardData);
                    break;
            }
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            failCount++;
            if (failCount > cardConfig.getRetryCount()) {
                //超过失败重试限制
                cardState = CardState.Error;
                readCardFail("检卡超时");
                LogUtil.u(TAG, "检卡失败", "检卡超时");
                return;
            }
            LogUtil.u(TAG, "检卡失败",
                    String.format(Locale.CHINA, "(%d)%s，第%d次失败", code, message, failCount));
            checkCard(true);
        }
    };

    /**
     * 读取M1卡数据
     */
    private void m1ReadCard() {
        m1ReadCard(true);
    }

    private void m1ReadCard(boolean doAuth) {
        int startBlockNo = cardConfig.getM1Sector() * 4;
        boolean result;
        if (doAuth) {
            result = m1Auth(cardConfig.getM1KeyType(), startBlockNo, cardConfig.getM1KeyBytes());
        } else {
            result = true;
        }
        if (result) {
            VipCardData data = m1ReadSector(cardConfig.getM1MSector());
            if (data != null && data.isValid()) {
                readCardSuccess(data);
                LogUtil.u(TAG, "IC卡读卡", data.toLogString());
            } else {
                readCardFail("读卡异常");
                LogUtil.u(TAG, "IC卡读卡", "读取卡片数据失败");
            }
        } else {
            readCardFail("卡片认证失败");
            LogUtil.u(TAG, "IC卡读卡", "卡片认证失败");
        }
    }

    /**
     * M1卡更新卡余额
     *
     * @param updateData
     */
    private void m1UpdateMoney(VipCardData updateData) {
        if (updateData == null) {
            writeCardFail("写卡信息无效");
            LogUtil.u(TAG, "IC卡更新卡余额", "写卡信息无效");
        }
        LogUtil.u(TAG, "IC卡更新卡余额",
                String.format(Locale.CHINA, "卡号：%s，金额：%.2f", updateData.getCardCode(), updateData.getMoney()));
        int startBlockNo = cardConfig.getM1Sector() * 4;
        boolean result = m1Auth(cardConfig.getM1KeyType(), startBlockNo, cardConfig.getM1KeyBytes());
        if (result) {
            VipCardData cardData = m1ReadSector(cardConfig.getM1MSector());
            if (!TextUtils.isEmpty(updateData.getCardCode())
                    && !updateData.getCardCode().equals(cardData.getCardCode())) {
                //卡号不一致，不允许写卡
                writeCardFail("卡号不一致");
                LogUtil.u(TAG, "IC卡更新卡余额", "卡号不一致");
            }
            if (updateData.getMoney() < 0) {//仅支付时校验密码，退款和充值不校验
                if (!TextUtils.isEmpty(cardData.getVipPwd())
                        && !cardData.getVipPwd().equals(updateData.getVipPwd())) {
                    //密码不一致，不允许写卡
                    writeCardFail("密码不一致");
                    LogUtil.u(TAG, "IC卡更新卡余额", "密码不一致");
                }
            }
            Double updateMoney = updateData.getMoney();
            if (lastWriteInfo != null && lastWriteLs.equalsIgnoreCase(lastWriteInfo.lsNo)) {
                if (new Date().getTime() - lastWriteInfo.time.getTime() < 5 * 60 * 1000) {
                    if (cardData.equals(lastWriteInfo.data)) {
                        //上次写卡已经扣款成功，冲减上次扣款金额
                        updateMoney -= lastWriteInfo.updateMoney;
                    }
                }
            }

            Double money = cardData.getMoney() + updateMoney;// + updateData.getMoney();
            if (money < 0) {
                //余额不足，不允许写卡
                writeCardFail("卡余额不足");
                LogUtil.u(TAG, "IC卡更新卡余额", "卡余额不足");
            }
            int res = m1WriteBlock(startBlockNo + 1, encryptIcCardMoney(money));
            if (res < 0) {
                //写卡失败
                writeCardFail("写卡失败：" + res);
                LogUtil.u(TAG, "IC卡更新卡余额", "写卡失败：" + res);
                return;
            }

            cardData.setMoney(money);
            // 记录上次写卡信息，防止重复扣款
            lastWriteInfo = new WriteCardInfo(cardData, lastWriteLs, updateData.getMoney());
            //校验余额是否写成功
            verifyCard(cardData);
        }
    }

    /**
     * 读取M1卡片数据。
     * 此方法不进行卡片认证，必须先调用m1Auth方法认证成功后才可读取数据。
     *
     * @param sectorNo 扇区号，读取该扇区0~2块的数据
     * @return
     */
    private VipCardData m1ReadSector(int sectorNo) {
        try {
            int startBlockNo = sectorNo * 4;
            VipCardData data = new VipCardData(AidlConstants.CardType.MIFARE);
            byte[] outData = new byte[128];
            //卡号
            int res = m1ReadBlock(startBlockNo, outData);
            if (res >= 0 && res <= 16) {
                String code = new String(outData, 0, 16).trim();
                data.setCardCode(code);
            }
            //余额
            outData = new byte[128];
            res = m1ReadBlock(startBlockNo + 1, outData);
            if (res >= 0 && res <= 16) {
                String str = new String(outData, 0, 16).trim();
                data.setMoney(decryptIcCardMoney(str));
            }
            //密码
            outData = new byte[128];
            res = m1ReadBlock(startBlockNo + 2, outData);
            if (res >= 0 && res <= 16) {
                String pwd = new String(outData, 0, 16).trim();
                data.setVipPwd(pwd);//这里不对密码进行解密处理，使用时再解密
            }
            return data;
        } catch (Exception e) {
            LogUtil.u(TAG, "IC卡读卡", "读卡异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * IC卡余额解密
     *
     * @param str
     * @return
     */
    private Double decryptIcCardMoney(String str) {
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ (byte) 77);
        }
        String decStr = new String(bytes);
        if (decStr.startsWith("<>")) {
            //余额有校验，去除校验参数（兼容5000新版本）
            decStr = decStr.substring(2, 12);
            return Double.valueOf(decStr) / 100;//卡内余额单位为分
        }
        return Double.valueOf(decStr);//卡内余额单位为元
    }

    /**
     * IC卡余额加密
     *
     * @param money
     * @return
     */
    private String encryptIcCardMoney(Double money) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINA, "%.2f", money));//卡内余额单位为元，按无校验格式写入
        while (sb.length() < 16) {
            sb.append(" ");
        }
        //余额不做校验位计算（兼容5000旧版本）
        byte[] bytes = sb.toString().getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ (byte) 77);
        }
        return new String(bytes);
    }

    /**
     * M1卡片认证
     */
    private boolean m1Auth(int keyType, int block, byte[] keyData) {
        try {
            int result = readCardOptV2.mifareAuth(keyType, block, keyData);
            if (result == 0) {
                LogUtil.u(TAG, "IC卡认证", "IC卡认证结果成功");
                return true;
            } else {
                LogUtil.u(TAG, "IC卡认证", "卡片认证失败");
                return false;
            }
        } catch (RemoteException e) {
            LogUtil.u(TAG, "IC卡认证", "卡片认证异常：" + e.getMessage());
            return false;
        }
    }

    /**
     * M1卡读取指定块数据
     */
    private int m1ReadBlock(final int block, byte[] blockData) {
        try {
            return readCardOptV2.mifareReadBlock(block, blockData);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -123;
        }
    }

    /**
     * M1卡写指定块数据
     *
     * @param block
     * @param blockData
     * @return
     */
    private int m1WriteBlock(final int block, final String blockData) {
        try {
            //需补齐16位，否则写入失败
            StringBuilder sb = new StringBuilder(blockData);
            while (sb.length() < 16) {
                sb.append(" ");
            }
            if (sb.length() > 16) {
                sb.delete(16, sb.length());
            }
            return readCardOptV2.mifareWriteBlock(block, sb.toString().getBytes());
        } catch (RemoteException e) {
            e.printStackTrace();
            return -123;
        }
    }

    /**
     * 磁卡解析数据
     *
     * @param bundle
     */
    private void handleMagneticData(final Bundle bundle) {
        String track1 = bundle.getString("TRACK1");
        String track2 = bundle.getString("TRACK2");
        String track3 = bundle.getString("TRACK3");
        boolean isEmpty = TextUtils.isEmpty(track1) && TextUtils.isEmpty(track2) && TextUtils.isEmpty(track3);
        if (isEmpty) {
            readCardFail("读卡失败：卡号为空");
            LogUtil.u(TAG, "磁卡读卡", "读卡失败：卡号为空");
        } else {
            // 根据配置参数返回卡号
            VipCardData data = new VipCardData(AidlConstants.CardType.MAGNETIC);
            switch (cardConfig.getMagTrackNo()) {
                case 1:
                default:
                    data.setCardCode(track1);
                    break;
                case 2:
                    data.setCardCode(track2);
                    break;
                case 3:
                    data.setCardCode(track3);
                    break;
            }
            readCardSuccess(data);
            LogUtil.u(TAG, "磁卡读卡", data.toLogString());

        }
    }

    /**
     * 读卡失败
     *
     * @param msg
     */
    private void readCardFail(final String msg) {
        opType = OpType.None;
        cancelReadCard();
        if (readCardCallback != null) {
            readCardCallback.onError(msg);
        }
    }

    /**
     * 读卡成功
     *
     * @param data
     */
    private void readCardSuccess(final VipCardData data) {
        opType = OpType.None;
        cancelReadCard();
        if (readCardCallback != null) {
            readCardCallback.onSuccess(data);
        }
    }

    /**
     * 写卡失败
     *
     * @param msg
     */
    private void writeCardFail(final String msg) {
        opType = OpType.None;
        cancelReadCard();
        if (writeCardCallback != null) {
            writeCardCallback.onError(msg);
        }
    }

    /**
     * 写卡成功
     */
    private void writeCardSuccess(final VipCardData data) {
        opType = OpType.None;
        cancelReadCard();
        if (writeCardCallback != null) {
            writeCardCallback.onSuccess(data);
        }
    }

    /**
     * 卡片状态
     */
    enum CardState {
        None,    //无卡
        Finding, //正在查找
        Found,   //已找到卡
        Error    //SDK异常
    }

    /**
     * 卡片操作类型
     */
    enum OpType {
        None,   //无操作
        Read,   //读卡
        Init,   //初始化卡
        Update, //更新卡余额
        Verify  //验证卡信息
    }

    /**
     * 读卡回调
     */
    public interface ReadCardCallback {
        void onSuccess(final VipCardData data);

        void onError(final String msg);
    }

    /**
     * 写卡回调
     */
    public interface WriteCardCallback {
        void onSuccess(final VipCardData data);

        void onError(final String msg);
    }

    /**
     * 写卡信息
     */
    class WriteCardInfo {
        public VipCardData data;
        public Date time;
        public String lsNo;
        public Double updateMoney;

        public WriteCardInfo() {
        }

        public WriteCardInfo(VipCardData data, String lsNo, Double updateMoney) {
            this.data = data;
            this.time = new Date();
            this.lsNo = lsNo;
            this.updateMoney = updateMoney;
        }
    }

}
