package com.ftrend.zgp.presenter;

import android.content.Context;

import com.blankj.utilcode.util.DeviceUtils;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.HomeContract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.ftrend.zgp.utils.task.LsUploadThread;
import com.ftrend.zgp.utils.task.ServerWatcherThread;
import com.wosai.upay.common.DebugConfig;
import com.wosai.upay.common.UpayTask;
import com.wosai.upay.http.Env;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 主界面P层----所有业务逻辑在此
 *
 * @author liziqiang@ftrend.cn
 */
public class HomePresenter implements HomeContract.HomePresenter {
    private HomeContract.HomeView mView;


    private HomePresenter(HomeContract.HomeView mView) {
        this.mView = mView;
    }

    public static HomePresenter createPresenter(HomeContract.HomeView mView) {
        return new HomePresenter(mView);
    }


    @Override
    public void initSunmiPaySdk() {
        LogUtil.d("----设备厂商：" + DeviceUtils.getManufacturer());
        LogUtil.d("----设备型号：" + DeviceUtils.getModel());
        if (DeviceUtils.getManufacturer().contains("SUNMI")) {
            SunmiPayHelper.getInstance().connectPayService();
        }
    }

    @Override
    public void initSqbSdk(Context context) {
        DebugConfig.setDebug(false);//默认为非调试模式,如果需要调试,请设置为 true,打印和保存相关日志
        UpayTask.getInstance().initUpay(context, ZgParams.getSqbConfig().isPlaySound(), Env.UrlType.PRO);
        SqbPayHelper.activate(new SqbPayHelper.PayResultCallback() {
            @Override
            public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                if (!isSuccess) {
                    MessageUtil.showError("收钱吧客户端激活失败");
                }
            }
        });
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
        String[] menuName = {
                "收银", "取单", "退货", "交班",
                "交班报表", "交易统计", "流水查询", "数据同步",
                "操作指南", "参数设置", "修改密码", "注销登录"/*, "测试退款"*/
        };
        int[] menuImg = {
                R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb,
                R.drawable.bbcx_jbbb, R.drawable.bbcx_jytj, R.drawable.bbcx_lscx, R.drawable.xtgn_sjtb,
                R.drawable.xtgn_czzn, R.drawable.xtgn_cssz, R.drawable.xtgn_xgmm, R.drawable.xtgn_zxdl/*, R.drawable.xtgn_zxdl*/
        };
        for (int i = 0; i < 4; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i], i == 1 ? TradeHelper.getHangUpCount() : 0));
        }
        menuList.add(new Menu("交易", childList));
        childList = new ArrayList<>();
        for (int i = 4; i < 7; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i], 0));
        }
        menuList.add(new Menu("报表查询", childList));
        childList = new ArrayList<>();
        for (int i = 7; i < menuName.length; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i], 0));
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
        if (handoverDay >= 0) {
            mView.tipHandover(handoverDay);
        }
    }

    @Override
    public void goShopCart() {
        if (HandoverHelper.mustHandover() == 0) {
            mView.mustHandover();
            return;
        }
        if (!UserRightsHelper.hasRights(UserRightsHelper.SALE)) {
            mView.showError("无此权限");
            return;
        }
        LogUtil.u("点击收银", "进入收银界面");
        //初始化流水单信息
        TradeHelper.initSale();
        mView.goShopChartActivity();

    }

    @Override
    public void goHandover() {
        LogUtil.u("点击交班", "进入交班界面");
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
    public void goRtnProd() {
        if (HandoverHelper.mustHandover() == 0) {
            mView.mustHandover();
            return;
        }
        if (!UserRightsHelper.hasRights(UserRightsHelper.REFUND)) {
            mView.showError("无此权限");
            return;
        }
        LogUtil.u("点击退货", "进入退货界面");
        mView.goRtnProdActivity();
    }

    @Override
    public void getOutOrder() {
        if (HandoverHelper.mustHandover() == 0) {
            mView.mustHandover();
            return;
        }
        LogUtil.u("点击取单", "进入取单界面");
        if (TradeHelper.outOrderCount()) {
            mView.goOrderOutActivity();
        } else {
            mView.hasNoHangUpTrade();
        }
    }

    @Override
    public void doDataTrans() {
        LogUtil.u("点击数据同步", "进行数据同步");
        final String waitMsg = "正在同步数据，请稍候...";
        MessageUtil.waitBegin(waitMsg, new MessageUtil.MessageBoxCancelListener() {
            @Override
            public boolean onCancel() {
                DataDownloadTask.taskCancel();
                return false;
            }
        });
        DataDownloadTask.taskStart(true, new DataDownloadTask.ProgressHandler() {
            @Override
            public void handleProgress(int percent, boolean isFailed, String msg) {
                System.out.println(String.format(Locale.getDefault(), "数据下载进度：%d%% %s", percent, msg));
                String custMsg = String.format(Locale.getDefault(), waitMsg + "(%d%%)", percent);
                MessageUtil.waitUpdate(custMsg);
                if (percent >= 100) {
                    //专柜、用户信息可能已更新，重新登录
                    MessageUtil.waitSuccesss("数据同步已完成，请重新登录", new MessageUtil.MessageBoxOkListener() {
                        @Override
                        public void onOk() {
                            LogUtil.u("数据同步", "数据同步成功");
                            logout();
                        }
                    });
                } else if (isFailed) {
                    LogUtil.u("数据同步", String.format("数据同步失败：%s", msg));
                    MessageUtil.waitError("数据同步失败：" + msg, null);
                }
            }
        });
    }

    @Override
    public void goHandoverReport() {
        LogUtil.u("点击交班报表", "进入交班报表界面");
        mView.goHandoverReportActivity();
    }

    @Override
    public void goTradeReport() {
        if (!ZgParams.isIsOnline()) {
            mView.showError("单机模式无法查询交易统计！");
            LogUtil.u("点击交易查询", "单机模式无法查询交易统计");
            return;
        }
        if (!UserRightsHelper.hasRights(UserRightsHelper.HISTORY_REPORT)) {
            mView.showError("无此权限");
            LogUtil.u("点击交易查询", "无此权限");
            return;
        }
        LogUtil.u("点击交易查询", "进入交易查询");
        mView.goTradeReportActivity();
    }

    @Override
    public void goTradeQuery() {
        LogUtil.u("点击流水查询", "进入流水查询");
        mView.goTradeQueryActivity();
    }

    @Override
    public void goConfigSetting() {
        LogUtil.u("点击参数设置", "进入参数设置界面");
        mView.goConfigActivity();
    }

    @Override
    public void goPwdChange() {
        if (!ZgParams.isIsOnline()) {
            mView.showError("单机模式无法修改密码！");
            return;
        }
        LogUtil.u("点击修改密码", "进入修改密码界面");
        mView.goPwdChangeActivity();
    }

    @Override
    public void logout() {
        LogUtil.u("点击注销", "注销当前用户");
        //清理当前信息
        ZgParams.clearCurrentInfo();
        //清理当前交易未处理的所有信息
        TradeHelper.clear();
        //重新读取信息
        ZgParams.loadParams();
        //界面注销
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
