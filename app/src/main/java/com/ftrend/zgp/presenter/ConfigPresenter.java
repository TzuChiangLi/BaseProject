package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.api.ConfigContract;
import com.ftrend.zgp.model.Config;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.HttpUtil;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.SqbConfig;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.printer.PrintConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class ConfigPresenter implements ConfigContract.ConfigPresenter {
    private static final String TAG = "ConfigPresenter";
    private ConfigContract.ConfigView mView;
    private List<Config> mCfgList = new ArrayList<>();

    private ConfigPresenter(ConfigContract.ConfigView mView) {
        this.mView = mView;
    }

    public static ConfigPresenter createPresenter(ConfigContract.ConfigView mView) {
        return new ConfigPresenter(mView);
    }

    @Override
    public void loadCfgItem() {
        //设置界面
        List<Config> mCfgList = new ArrayList<>();

        Config cfg = new Config();
        cfg.setItemType(Config.TYPE_TITLE);
        cfg.setText("打印设置");
        mCfgList.add(cfg);

        cfg = new Config();
        cfg.setItemType(Config.NORMAL_SWB);
        cfg.setText("结算成功自动打印交易小票");
        //获取打印设置状态
        cfg.setOn(ZgParams.isPrintBill());
        mCfgList.add(cfg);
        //支付方式
        cfg = new Config();
        cfg.setItemType(Config.TYPE_TITLE);
        cfg.setText("选择支付方式");
        mCfgList.add(cfg);

        //收钱吧激活码如果是空的，那客户端就没激活过，会报空指针异常导致崩溃
        //此处加上激活码判空，以此判断收钱吧是否失败并加上是否响应点击的锁
        SqbConfig config = ZgParams.getSqbConfig();
        boolean flag = !TextUtils.isEmpty(config.getActivateCode()) && SqbPayHelper.isActivated();
        cfg = new Config();
        cfg.setLock(flag);
        cfg.setErr(!flag);
        cfg.setItemType(Config.NORMAL_SWB);
        cfg.setText("收钱吧");
        cfg.setOn("1".equals(String.valueOf(ZgParams.getPayType()[0])));
        mCfgList.add(cfg);
        cfg = new Config();
        cfg.setItemType(Config.NORMAL_SWB);
        cfg.setText("储值卡");
        cfg.setOn("1".equals(String.valueOf(ZgParams.getPayType()[1])));
        mCfgList.add(cfg);
        cfg = new Config();
        cfg.setItemType(Config.NORMAL_SWB);
        cfg.setText("现金");
        cfg.setOn("1".equals(String.valueOf(ZgParams.getPayType()[2])));
        mCfgList.add(cfg);

        cfg = new Config();
        cfg.setItemType(Config.TYPE_TITLE);
        cfg.setText("服务器地址");
        mCfgList.add(cfg);

        cfg = new Config();
        cfg.setItemType(Config.NORMAL_MOD);
        cfg.setText(ZgParams.getServerUrl());
        mCfgList.add(cfg);

        cfg = new Config();
        cfg.setItemType(Config.TYPE_TITLE);
        cfg.setText("其他");
        mCfgList.add(cfg);

        cfg = new Config();
        cfg.setItemType(Config.NORMAL_MULTI);
        cfg.setText("关于");
        mCfgList.add(cfg);

        this.mCfgList = mCfgList;
        //更新界面
        mView.initCfgItem(mCfgList);
    }

    @Override
    public void config(int position) {
        if (position == mCfgList.size() - 1) {
            mView.goIntroActivity();
        }
    }

    @Override
    public void changeServerUrl(final int position, final String url) {
        final String oldURL = ZgParams.getServerUrl();
        try {
            HttpUtil.reset(url);
        } catch (Exception e) {
            mView.showError("服务地址无效");
            LogUtil.e(e.getMessage());
        }
        String posCode = ZgParams.getPosCode();
        String userCode = ZgParams.getCurrentUser().getUserCode();
        try {
            RestSubscribe.getInstance().ping(posCode, userCode, new HttpCallBack<String>() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String body) {
                    ZgParams.saveAppParams("ServerUrl", url);
                    mCfgList.get(position).setText(url);
                    mView.show("修改成功");
                    mView.updateConfig(position);
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    mView.showError(String.format("%s（%s）", errorMsg, errorCode));
                    HttpUtil.reset(oldURL);
                }

                @Override
                public void onHttpError(int errorCode, String errorMsg) {
                    mView.showError(String.format("%s（%s）", errorMsg, errorCode));
                    HttpUtil.reset(oldURL);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MessageUtil.waitEnd();
                    MessageUtil.error("网络通讯异常。请检查服务器地址及注册信息是否正确");
                }
            }, 500);
        }
    }

    @Override
    public void print(boolean flag) {
        PrintConfig printConfig = ZgParams.getPrinterConfig();
        printConfig.setPrintTrade(flag);
        if (ZgParams.saveAppParams("printBill", flag ? "True" : "False")) {
            //如果关闭打印小票，那么设置中打印储值卡存根同时关闭
            mCfgList.get(1).setOn(flag);
            ZgParams.loadParams();
            mView.show("参数已保存");
            LogUtil.u(TAG, "打印设置", flag ? "开启" : "关闭");
        } else {
            LogUtil.u(TAG, "打印设置", "参数写入失败");
            mView.show("参数写入失败");
        }
    }


    @Override
    public void payType(boolean isChecked, int position) {
        StringBuilder result = new StringBuilder();
        String[] payType = ZgParams.getPayType();
        payType[position] = isChecked ? "1" : "0";
        for (String s : payType) {
            result.append(s);
        }
        if ("000".equals(result.toString())) {
            mView.show("请至少选择一项支付方式");
            payType[position] = isChecked ? "0" : "1";
            if (position == 0) {
                mCfgList.get(3).setOn(true);
                mView.updateConfig(3);
            } else if (position == 1) {
                mCfgList.get(4).setOn(true);
                mView.updateConfig(4);
            } else if (position == 2) {
                mCfgList.get(5).setOn(true);
                mView.updateConfig(5);
            }
        } else {
            ZgParams.saveAppParams("payType", result.toString());
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
