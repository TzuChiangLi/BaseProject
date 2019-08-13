package com.ftrend.zgp.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ftrend.zgp.utils.LogUtil;
import com.ftrend.zgp.utils.ToastUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    public Unbinder unbinder;
    //监听网络变化
    private IntentFilter mIntentFilter;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID(0));
        unbinder = ButterKnife.bind(this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, mIntentFilter);
        initView();
        initTitleBar();
        initData();
    }


    /**
     * @param i layout布局
     * @return
     */
    protected abstract int getLayoutID(int i);

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

    /**
     * 网络状态设置
     *
     * @param status 网络状态 0 网络断开，1 手机网络，2 Wifi网络
     */
    public void onNetWorkChanged(int status) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭网络监听

    }

    @Override
    protected void onResume() {
        super.onResume();
        //网络监听

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNetworkChangeReceiver);
        //ButterKnife解绑
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("网络状态发生变化");
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                }
                //API大于23时使用下面的方式进行网络监听
            } else {

                System.out.println("API level 大于23");
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                LogUtil.d(String.valueOf(networks.length));
                if (networks.length > 0) {
                    for (int i = 0; i < networks.length; i++) {
                        //获取ConnectivityManager对象对应的NetworkInfo对象
                        NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                        sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                        LogUtil.d(sb.toString());
                    }
                    Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    ToastUtil.show("当前无网络连接");
                }
            }
        }
    }

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
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    //endregion
}
