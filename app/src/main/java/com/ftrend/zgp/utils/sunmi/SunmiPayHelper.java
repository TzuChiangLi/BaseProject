package com.ftrend.zgp.utils.sunmi;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ftrend.zgp.App;
import com.ftrend.zgp.utils.common.ByteUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Arrays;

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

    // TODO: 2019/9/20 读卡需要以下参数，应在服务端设置
    // （1）支持的卡类型：1-磁卡，2-M1卡
    private int cardTypes = AidlConstantsV2.CardType.MIFARE.getValue()
            | AidlConstantsV2.CardType.MAGNETIC.getValue();
    // （2）磁卡轨道号（会员卡号所在轨道）：1～3
    private int magTrackNo = 2;
    // （3）M1卡扇区号（会员卡号所在扇区）：0～15
    private final int m1Sector = 1;
    // （4）M1卡块号（会员卡号所在块）：0～2
    private final int m1Block = 0;
    // （5）M1卡读取密码，6字节（12个十六进制字符），默认为“FFFFFFFFFFFF”
    private byte[] m1KeyBytes = ByteUtil.hexStr2Bytes("FFFFFFFFFFFF");
    // （6）M1卡密码类型：0-KeyA，1-KeyB
    private final int m1KeyType = 0;

    private SunmiPayKernel kernel = null;
    // 读卡器操作对象
    private ReadCardOptV2 readCardOptV2 = null;
    // 是否已连接到支付SDK
    private boolean sdkConnected = false;
    // 卡片状态
    private CardState cardState = CardState.None;
    // 失败重试次数
    private final int retryCount = 10;
    // 失败次数
    private int failCount = 0;
    // 读卡回调
    private ReadCardCallback readCardCallback = null;

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
     * M1卡读取：检卡、认证、读卡
     *
     * @param callback 读卡回调
     */
    public void readCard(ReadCardCallback callback) {
        this.readCardCallback = callback;
        checkCard(false);
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
            readCardOptV2.checkCard(cardTypes, mCheckCardCallback, 120);
        } catch (Exception e) {
            cardState = CardState.Error;
            e.printStackTrace();
            //回调
            if (readCardCallback != null) {
                readCardCallback.onError("检卡异常");
            }
        }
    }

    /**
     * 取消检卡
     */
    private void cancelCheckCard() {
        try {
            readCardOptV2.cardOff(cardTypes);
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
            handleMagneticData(bundle);
        }

        @Override
        public void findICCard(String atr) throws RemoteException {

        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            Log.e(TAG, "findRFCard:" + uuid);
            cardState = CardState.Found;
            readSector();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            failCount++;
            Log.e(TAG, "checkCard: retry...");
            if (failCount > retryCount) {
                //超过失败重试限制
                cardState = CardState.Error;
                Log.e(TAG, "checkCard: max retry");
                //回调
                if (readCardCallback != null) {
                    readCardCallback.onError("检卡超时");
                }
                return;
            }
            checkCard(true);
        }

    };

    /**
     * M1卡读取指定扇区数据
     */
    private void readSector() {
        int startBlockNo = m1Sector * 4;
        boolean result = m1Auth(m1KeyType, startBlockNo, m1KeyBytes);
        if (result) {
            byte[] outData = new byte[128];
            int res = m1ReadBlock(startBlockNo, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(Arrays.copyOf(outData, res));
                Log.e(TAG, "read block 0:" + hexStr);
                //回调
                String code = new String(outData, 0, 16);
                if (readCardCallback != null && m1Block == 0) {
                    readCardCallback.onSuccess(code);
                }
            } else {
                Log.e(TAG, "read block 0: FAILED");
                //回调
                if (readCardCallback != null && m1Block == 0) {
                    readCardCallback.onError("读取数据失败");
                }
            }

            outData = new byte[128];
            res = m1ReadBlock(startBlockNo + 1, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(Arrays.copyOf(outData, res));
                Log.e(TAG, "read block 1:" + hexStr);
                //回调
                String code = new String(outData, 0, 16);
                if (readCardCallback != null && m1Block == 1) {
                    readCardCallback.onSuccess(code);
                }
            } else {
                Log.e(TAG, "read block 1: FAILED");
                //回调
                if (readCardCallback != null && m1Block == 1) {
                    readCardCallback.onError("读取数据失败");
                }
            }

            outData = new byte[128];
            res = m1ReadBlock(startBlockNo + 2, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(Arrays.copyOf(outData, res));
                Log.e(TAG, "read block 2:" + hexStr);
                //回调
                String code = new String(outData, 0, 16);
                if (readCardCallback != null && m1Block == 2) {
                    readCardCallback.onSuccess(code);
                }
            } else {
                Log.e(TAG, "read block 2: FAILED");
                //回调
                if (readCardCallback != null && m1Block == 2) {
                    readCardCallback.onError("读取数据失败");
                }
            }
        }
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
    private int m1ReadBlock(int block, byte[] blockData) {
        try {
            int result = readCardOptV2.mifareReadBlock(block, blockData);
            Log.e(TAG, "m1ReadBlock result:" + result);
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -123;
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
            //回调
            if (readCardCallback != null) {
                readCardCallback.onError("读卡失败：卡号为空");
            }
        } else {
            //回调
            if (readCardCallback != null) {
                // 根据配置参数返回卡号
                switch (magTrackNo) {
                    case 1:
                        readCardCallback.onSuccess(track1);
                        break;
                    case 2:
                        readCardCallback.onSuccess(track2);
                        break;
                    case 3:
                        readCardCallback.onSuccess(track3);
                        break;
                    default:
                        break;
                }
            }
            Log.e(TAG, "handleMagneticData result 1: " + track1);
            Log.e(TAG, "handleMagneticData result 2: " + track2);
            Log.e(TAG, "handleMagneticData result 3: " + track3);
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
     * 读卡回调
     */
    public interface ReadCardCallback {
        void onSuccess(final String cardNo);

        void onError(final String msg);
    }

}
