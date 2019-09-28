package com.ftrend.zgp.presenter;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.task.LsUploadThread;
import com.ftrend.zgp.utils.task.ServerWatcherThread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 主界面P层----所有业务逻辑在此
 *
 * @author liziqiang@ftrend.cn
 */
public class HomePresenter implements Contract.HomePresenter {
    private Contract.HomeView mView;


    public HomePresenter(Contract.HomeView mView) {
        this.mView = mView;
    }

    public static HomePresenter createPresenter(Contract.HomeView mView) {
        return new HomePresenter(mView);
    }

    @Override
    public void initSunmiPaySdk() {
        SunmiPayHelper.getInstance().connectPayService();
    }

    @Override
    public void initServerThread() {
        //启动后台服务心跳检测线程
        ServerWatcherThread watcherThread = new ServerWatcherThread();
        watcherThread.start();
        //启动数据上传线程
        LsUploadThread lsUploadThread = new LsUploadThread();
        lsUploadThread.start();
    }

    @Override
    public void initMenuList() {
        List<Menu> menuList = new ArrayList<>();
        List<Menu.MenuList> childList = new ArrayList<>();
        String[] menuName = {"收银", "取单", "退货", "交班", "交班报表", "交易统计", "流水查询", "数据同步", "操作指南", "参数设置"
                , "修改密码", "注销登录"};
        int[] menuImg = {R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb, R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb,
                R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb};
        for (int i = 0; i < 4; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("交易", childList));
        childList = new ArrayList<>();
        for (int i = 4; i < 7; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("报表查询", childList));
        childList = new ArrayList<>();
        for (int i = 7; i < menuName.length; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("系统功能", childList));
        mView.setMenuList(menuList);

    }

    @Override
    public void setInfo() {
        int size = 3;
        String[] info = new String[size];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Arrays.fill(info, sdf.format(new Date()));
        mView.showInfo(info);
    }

    @Override
    public void checkHandover() {
        int handoverDay = HandoverHelper.mustHandover();
        if (handoverDay != -1) {
            mView.tipHandover();
        }
    }

    @Override
    public void goShopCart() {
        if (HandoverHelper.mustHandover() == 0) {
            mView.mustHandover();
            return;
        }
        //初始化流水单信息
        TradeHelper.initSale();
        mView.goShopChartActivity();
    }

    @Override
    public void goHandover() {
        switch (HandoverHelper.canHandover()) {
            case 1:
                mView.goHandoverActivity();
                break;
            case 0:
                mView.hasNoTrade();
                break;
            case -1:
                mView.showOfflineTip();
                break;
            default:
                break;
        }
    }

    @Override
    public void getOutOrder() {
        if (HandoverHelper.mustHandover() == 0) {
            mView.mustHandover();
            return;
        }
        mView.goOrderOutActivity();
    }

    @Override
    public void logout() {
        //清除登录信息
        ZgParams.clearCurrentInfo();

        mView.logout();
    }

    @Override
    public void onDestory() {
        SunmiPayHelper.getInstance().disconnectPayService();
        if (mView != null) {
            mView = null;
        }
    }

}
