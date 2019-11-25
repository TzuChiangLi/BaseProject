package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Menu;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface HomeContract {
    interface HomePresenter {
        /**
         * 初始化商米支付SDK
         */
        void initSunmiPaySdk();

        /**
         * 初始化收钱吧SDK
         */
        void initSqbSdk(Context context);

        /**
         * 启动后台线程
         */
        void initServerThread();

        /**
         * 创建界面菜单的数据
         */
        void initMenuList();

        /**
         * 设置用户名、专柜号、当前日期
         */
        void setInfo();

        /**
         * 检查交班
         */
        void checkHandover();

        /**
         * 跳转收银-选择商品界面
         */
        void goShopCart();

        /**
         * 跳转交班界面
         */
        void goHandover();

        /**
         * 跳转到退货界面
         */
        void goRtnProd();

        /**
         * 跳转到取单界面
         */
        void getOutOrder();

        /**
         * 执行数据同步
         */
        void doDataTrans(final Context context);

        /**
         * 跳转到交班报表界面
         */
        void goHandoverReport();

        /**
         * 跳转到交易统计界面
         */
        void goTradeReport();

        /**
         * 跳转到流水查询界面
         */
        void goTradeQuery();

        /**
         * 注销登录
         */
        void logout();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface HomeView extends BaseView<HomeContract.HomePresenter> {
        /**
         * 显示错误
         *
         * @param msg 文本信息
         */
        void showError(String msg);

        /**
         * 返回数据显示界面
         *
         * @param menuList 数据
         */
        void setMenuList(List<Menu> menuList);

        /**
         * 设置用户名、专柜号、当前日期
         *
         * @param info 字符串数组
         */
        void showInfo(String... info);

        /**
         * 必须交班
         */
        void mustHandover();

        /**
         * 提示交班
         */
        void tipHandover(int days);

        /**
         * 跳转到收银选择商品界面
         */
        void goShopChartActivity();

        /**
         * 跳转到交班界面
         */
        void goHandoverActivity();

        /**
         * 跳转到取单界面
         */
        void goOrderOutActivity();

        /**
         * 跳转到退货界面
         */
        void goRtnProdActivity();

        /**
         * 跳转到交班报表界面
         */
        void goHandoverReportActivity();

        /**
         * 跳转到交易统计界面
         */
        void goTradeReportActivity();

        /**
         * 跳转到流水查询界面
         */
        void goTradeQueryActivity();

        /**
         * 无挂单流水
         */
        void hasNoHangUpTrade();

        /**
         * 没有交易流水，无法进入交班界面
         */
        void hasNoTrade();

        /**
         * 单机运行
         */
        void showOfflineTip();

        /**
         * 注销登录
         */
        void logout();

    }
}
