package com.ftrend.zgp.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ScreenLock;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.view.InitActivity;
import com.ftrend.zgp.view.LoginActivity;
import com.ftrend.zgp.view.RegisterActivity;
import com.ftrend.zgp.view.ScanActivity;
import com.ftrend.zgp.view.WakeLockActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基类
 *
 * @author liziqiang@ftrend.cn
 */
public abstract class BaseActivity extends AppCompatActivity {
    public Unbinder unbinder;
    public static Context mContext;
    private onNetStatusReceiver receiver = null;
    private ScreenLock mScreenLock = null;
    private boolean wakeLock = false;
    public final static int SCAN_SUNMI = 001, SCAN_CAMERA = 002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        unbinder = ButterKnife.bind(this);
        mContext = this;
        initView();
        initTitleBar();
        initData();
        setCurrentModule();
        setAllowScrRoate(false);
        //以下几个界面不会暂离息屏
        wakeLock = (this instanceof LoginActivity)
                || (this instanceof RegisterActivity)
                || (this instanceof InitActivity);
        if (!wakeLock) {
            mScreenLock = new ScreenLock(this);
            mScreenLock.begin(mStateListener);
        }
    }

    @Override
    protected void onRestart() {
        if (!wakeLock) {
            mScreenLock.wake();
        }
        setCurrentModule();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unRegisterReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            if (!wakeLock) {
                if (!ActivityUtils.getTopActivity().equals(this)) {
                    mScreenLock.sleep();
                }
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //ButterKnife解绑
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (!wakeLock) {
            mScreenLock.sleep();
            mScreenLock.unregisterListener();
        }
        super.onDestroy();
    }

    //屏幕状态监听
    ScreenLock.ScreenStateListener mStateListener = new ScreenLock.ScreenStateListener() {
        @Override
        public void onScreenOn(boolean isLocked) {
            if (isLocked && !ActivityUtils.isActivityExistsInStack(WakeLockActivity.class)) {
                Intent intent = new Intent(mContext, WakeLockActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onScreenOff() {
        }

        @Override
        public void onUserPresent() {
        }
    };

    public class onNetStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ZgParams.MSG_ONLINE)) {
                onNetWorkChange(true);
            } else {
                onNetWorkChange(false);
            }
        }
    }

    /**
     * 注册
     */
    private void registerReceiver() {
        IntentFilter msgFilter = new IntentFilter();
        msgFilter.addAction(ZgParams.MSG_ONLINE);
        msgFilter.addAction(ZgParams.MSG_OFFLINE);
        receiver = new onNetStatusReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, msgFilter);
    }

    /**
     * 解除注册
     */
    private void unRegisterReceiver() {
        if (receiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

    /**
     * 设置用户操作记录模块
     */
    private void setCurrentModule() {
        LogUtil.setCurrentModule(this.getClass().getName().replace("com.ftrend.zgp.", ""));
    }

    /**
     * 扫描方法
     */
    public void scan() {
        Intent intent;
        if (ZgParams.isSunmi()) {
            intent = new Intent("com.summi.scan");
            intent.setPackage("com.sunmi.sunmiqrcodescanner");
            startActivityForResult(intent, SCAN_SUNMI);
        } else {
            intent = new Intent(this, ScanActivity.class);
            startActivityForResult(intent, SCAN_CAMERA);
        }
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * @param isOnline 设置网络状态
     */
    public abstract void onNetWorkChange(boolean isOnline);

    /**
     * @return 布局ID
     */
    protected abstract int getLayoutID();

    /**
     * 初始化界面数据
     */
    protected abstract void initData();

    /**
     * 初始化界面UI
     */
    protected abstract void initView();

    /**
     * 初始化沉浸式顶栏颜色
     */
    protected abstract void initTitleBar();


    //region 屏幕方向
    /**
     * 是否允许旋转屏幕
     */
    private boolean isAllowScrRoate = false;

    /**
     * 设置是否允许横屏
     *
     * @param isAllowScrRoate
     */
    public void setAllowScrRoate(boolean isAllowScrRoate) {
        this.isAllowScrRoate = isAllowScrRoate;
        if (isAllowScrRoate) {
            LogUtil.d("允许横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    //endregion
}

//    class NetworkChangeReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//
//
//                //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
//                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    //获得ConnectivityManager对象
//                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    //获取ConnectivityManager对象对应的NetworkInfo对象
//                    //获取WIFI连接的信息
//                    NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                    //获取移动数据连接的信息
//                    NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//                    if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
////                    Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
//                    } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
////                    Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
//                    } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
////                    Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
//                    } else {
////                    Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
//                    }
//                    //API大于23时使用下面的方式进行网络监听
//                } else {
//                    //获得ConnectivityManager对象
//                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    //获取所有网络连接的信息
//                    Network[] networks = connMgr.getAllNetworks();
//                    //用于存放网络连接信息
//                    StringBuilder sb = new StringBuilder();
//                    //通过循环将网络信息逐个取出来
//                    LogUtil.d(String.valueOf(networks.length));
//                    if (networks.length > 0) {
//                        for (int i = 0; i < networks.length; i++) {
//                            //获取ConnectivityManager对象对应的NetworkInfo对象
//                            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
//                            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
//                            LogUtil.d(sb.toString());
//                        }
//                    } else {
////                    MessageUtil.show("当前无网络连接");
//                    }
//                }
//            } catch (Exception e) {
//                LogUtil.e(e.getMessage());
//            }
//        }
//    }