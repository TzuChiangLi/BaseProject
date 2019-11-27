package com.ftrend.zgp.utils.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.ftrend.zgp.utils.log.LogUtil;

public class ScreenLock {
    /**
     * 锁屏时间(当前为0，锁屏即暂离)
     */
    private static final long TIME = 0;
    private long lockTime = -1;
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;
    public boolean isScreenOn, isSleep = false;

    public ScreenLock(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                if (isSleep) {
                    return;
                }
                if (lockTime == -1) {
                    mScreenStateListener.onScreenOn(false);
                } else {
                    mScreenStateListener.onScreenOn(System.currentTimeMillis() - lockTime > TIME);
                }
                isScreenOn = true;
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                if (isSleep) {
                    return;
                }
                lockTime = System.currentTimeMillis();
                mScreenStateListener.onScreenOff();
                isScreenOn = false;
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                if (isSleep) {
                    return;
                }
                mScreenStateListener.onUserPresent();
                isScreenOn = true;
            }
        }
    }

    /**
     * 开始监听screen状态
     *
     * @param listener
     */
    public void begin(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }

    public void sleep() {
        isSleep = true;
    }

    public void wake() {
        isSleep = false;
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        if (manager.isInteractive()) {
            if (mScreenStateListener != null) {
                if (isSleep) {
                    return;
                }
                mScreenStateListener.onScreenOn(lockTime == -1 ? false : System.currentTimeMillis() - lockTime > TIME);
            }
        } else {
            if (mScreenStateListener != null) {
                if (isSleep) {
                    return;
                }
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public interface ScreenStateListener {// 返回给调用者屏幕状态信息

        /**
         * 亮屏
         *
         * @param isLocked 是否启动暂离锁
         */
        public void onScreenOn(boolean isLocked);

        /**
         * 锁屏
         */
        public void onScreenOff();

        /**
         * 解锁屏幕（注意与亮屏的区别）
         */
        public void onUserPresent();
    }
}
