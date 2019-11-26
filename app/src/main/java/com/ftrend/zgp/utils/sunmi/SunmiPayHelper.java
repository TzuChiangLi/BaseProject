package com.ftrend.zgp.utils.sunmi;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ftrend.zgp.App;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ByteUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

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
        //TODO 2019年9月25日09:05:55 出现过一次空指针崩溃
        try {
            if (!sdkConnected) {
                return;
            }
            cancelCheckCard();
            kernel.destroyPaySDK();
            kernel = null;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
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
            callback.onError("刷卡服务不可用");
            return;
        }
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
        cancelCheckCard();
    }

    /**
     * 写卡：更新卡余额
     *
     * @param data
     * @param callback
     */
    public void writeCard(VipCardData data, WriteCardCallback callback) {
        if (!sdkConnected) {
            callback.onError("刷卡服务不可用");
            return;
        }
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
        cancelCheckCard();
    }

    /**
     * 卡片初始化：写入全部内容
     *
     * @param data
     * @param callback
     */
    public void initCard(VipCardData data, WriteCardCallback callback) {
        if (!sdkConnected) {
            callback.onError("刷卡服务不可用");
            return;
        }
        this.writeCardData = data;
        this.writeCardCallback = callback;
        this.opType = OpType.Init;
        checkCard(false);
    }

    /**
     * 校验卡信息
     *
     * @param cardData
     */
    private void verifyCard(final VipCardData cardData) {
        readCard(new ReadCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                if (cardData.getCardCode().equals(data.getCardCode())
                        && cardData.getMoney().compareTo(data.getMoney()) == 0
                        && cardData.getVipPwd().equals(data.getVipPwd())) {
                    writeCardSuccess(cardData);
                } else {
                    writeCardFail("写卡失败");
                }
            }

            @Override
            public void onError(String msg) {
                writeCardFail(msg);
            }
        });
    }

    /**
     * 连接状态回调
     */
    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {

        @Override
        public void onConnectPaySDK() {
            Log.e(TAG, "onConnectPaySDK");
            try {
                readCardOptV2 = kernel.mReadCardOptV2;
                cardConfig = ZgParams.getCardConfig();
                sdkConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnectPaySDK() {
            Log.e(TAG, "onDisconnectPaySDK");
            sdkConnected = false;
        }

    };

    /**
     * 检卡
     */
    private void checkCard(boolean isRetry) {
        try {
            if (!isRetry) {
                cardState = CardState.Finding;
                failCount = 0;
            }
            readCardOptV2.checkCard(cardConfig.getCardTypes(), mCheckCardCallback, 120);
        } catch (Exception e) {
            cardState = CardState.Error;
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
            }//磁卡只能读，不能写
        }

        @Override
        public void findICCard(String atr) throws RemoteException {

        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            Log.e(TAG, "findRFCard:" + uuid);
            cardState = CardState.Found;
            switch (opType) {
                case Read:
                default:
                    m1ReadCard();
                    break;
                case Update:
                    m1UpdateMoney(writeCardData);
                    break;
                case Init:
                    m1InitCard(writeCardData);
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
                return;
            }
            checkCard(true);
        }

    };

    /**
     * 读取M1卡数据
     */
    private void m1ReadCard() {
        int startBlockNo = cardConfig.getM1Sector() * 4;
        boolean result = m1Auth(cardConfig.getM1KeyType(), startBlockNo, cardConfig.getM1KeyBytes());
        if (result) {
            VipCardData data = m1ReadSector(cardConfig.getM1MSector());
            if (data.isValid()) {
                readCardSuccess(data);
            } else {
                readCardFail("读取卡片数据失败");
            }
        } else {
            readCardFail("卡片认证失败");
        }
    }

    /**
     * M1卡初始化（测试用方法）
     *
     * @param initData
     */
    private void m1InitCard(VipCardData initData) {
        if (initData == null) {
            writeCardFail("写卡信息无效");
        }
        int startBlockNo = cardConfig.getM1Sector() * 4;
        boolean result = m1Auth(cardConfig.getM1KeyType(), startBlockNo, cardConfig.getM1KeyBytes());
        if (result) {
            int res = m1WriteBlock(startBlockNo, initData.getCardCode());
            if (res < 0) {
                writeCardFail("写卡失败：" + res);
            }
            res = m1WriteBlock(startBlockNo + 1, encryptIcCardMoney(initData.getMoney()));
            if (res < 0) {
                writeCardFail("写卡失败：" + res);
            }
            res = m1WriteBlock(startBlockNo + 2, initData.getVipPwd());
            if (res < 0) {
                writeCardFail("写卡失败：" + res);
            }
            //校验
            verifyCard(initData);
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
        }
        int startBlockNo = cardConfig.getM1Sector() * 4;
        boolean result = m1Auth(cardConfig.getM1KeyType(), startBlockNo, cardConfig.getM1KeyBytes());
        if (result) {
            VipCardData cardData = m1ReadSector(cardConfig.getM1MSector());
            if (!TextUtils.isEmpty(updateData.getCardCode())
                    && !updateData.getCardCode().equals(cardData.getCardCode())) {
                //卡号不一致，不允许写卡
                writeCardFail("卡号不一致");
            }
            if (updateData.getMoney() < 0) {//仅支付时校验密码，退款和充值不校验
                if (!TextUtils.isEmpty(cardData.getVipPwd())
                        && !cardData.getVipPwd().equals(updateData.getVipPwd())) {
                    //密码不一致，不允许写卡
                    writeCardFail("密码不一致");
                }
            }
            Double money = cardData.getMoney() + updateData.getMoney();
            if (money < 0) {
                //余额不足，不允许写卡
                writeCardFail("卡余额不足");
            }
            int res = m1WriteBlock(startBlockNo + 1, encryptIcCardMoney(money));
            if (res < 0) {
                //写卡失败
                writeCardFail("写卡失败");
            }
            cardData.setMoney(money);
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
            data.setVipPwd(pwd);//这里不对密码进行解密处理，使用是再解密
        }
        return data;
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
        }
        return Double.valueOf(decStr) / 100;//卡内余额单位为分
    }

    /**
     * IC卡余额加密
     *
     * @param money
     * @return
     */
    private String encryptIcCardMoney(Double money) {
        StringBuilder sb = new StringBuilder();
        sb.append(Math.round(money * 100));//卡内余额单位为分
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
        boolean val = false;
        try {
            String hexStr = ByteUtil.bytes2HexStr(keyData);
            Log.e(TAG, "block:" + block + " m1KeyType:" + keyType + " m1KeyBytes:" + hexStr);

            int result = readCardOptV2.mifareAuth(keyType, block, keyData);
            Log.e(TAG, "m1Auth result:" + result);
            val = result == 0;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (val) {
            Log.e(TAG, "card_auth_success");
            return true;
        } else {
            Log.e(TAG, "card_auth_fail");
            checkCard(false);
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
            Log.e(TAG, "handleMagneticData result: isEmpty");
            readCardFail("读卡失败：卡号为空");
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
        }
    }

    /**
     * 读卡失败
     *
     * @param msg
     */
    private void readCardFail(final String msg) {
        opType = OpType.None;
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
        if (writeCardCallback != null) {
            writeCardCallback.onError(msg);
        }
    }

    /**
     * 写卡成功
     */
    private void writeCardSuccess(final VipCardData data) {
        opType = OpType.None;
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

}
