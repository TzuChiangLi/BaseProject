package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.HelperContract;
import com.ftrend.zgp.model.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */
public class HelperPresenter implements HelperContract.HelperPresenter {
    private static final String TAG = "HelperPresenter";
    private HelperContract.HelperView mView;

    private HelperPresenter(HelperContract.HelperView mView) {
        this.mView = mView;
    }

    public static HelperPresenter createPresenter(HelperContract.HelperView mView) {
        return new HelperPresenter(mView);
    }


    @Override
    public void initHelper() {
        String[] questions = {
                "1、注册失败，提示“服务器请求失败...”",
                "2、注册失败，提示“机器号不存在或者验证码错误”",
                "3、初始化完成后，点击“进入系统”没反应",
                "4、密码输入正确，仍然登录失败",
                "5、进入主界面时，总是提示“收钱吧激活失败”",
                "6、选择收钱吧支付，扫码后顾客端无反应",
                "7、选择储值卡支付，提示“网络通讯异常”",
                "8、取单失败"
        };
        String[] answers = {
                "（1）检查服务器IP地址是否输入正确。\n" +
                        "（2）如果后台服务未“启用80端口转发”，服务器地址应包括端口号，如：192.168.1.20:8091。",
                "机器号和验证码可能不匹配。",
                "如果可登录专柜或用户未配置，是无法进入系统的。可以关闭APP，在后台配置完成后，重新运行APP，此时仍然进入初始化界面。",
                "如果登录密码从方象5000后台做了修改，可以退出APP重新进入，在联机状态下，APP会自动从后台同步密码。\n" +
                        "如果修改密码时，已经登录，可以使用“数据同步”功能从后台同步密码。",
                "登录成功进入主界面时，会尝试激活收钱吧客户端，已经激活的不会重复激活。如果此时设备无法连接外网（或者后台未配置激活码），会导致激活失败，并显示提示信息；" +
                        "切换到外网网络连接（或配置激活码并同步数据）后，重新登录会自动尝试激活，激活成功不会提示。",
                "收钱吧支付必须连接外网，请检查设备是否已经连接到外网。",
                "储值卡支付必须在联机模式下进行，请检查APP右上角网络连接图标是否为灰色（灰色表示单机模式，无法使用联机功能）。\n",
                "如果当前购物车中有商品，则无法完成取单操作。可以先将购物车中的交易挂起或取消。"
        };
        List<Helper> mList = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            mList.add(new Helper(questions[i], answers[i]));
        }
        mView.showHelper(mList);
    }

    @Override
    public void onDestroy() {
        if (mView != null) {
            mView = null;
        }
    }
}
