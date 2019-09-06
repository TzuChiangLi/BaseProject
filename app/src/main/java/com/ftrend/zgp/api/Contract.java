package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.User;

import java.util.List;

/**
 * 接口类
 *
 * @author liziqiang@ftrend.cn
 */
public interface Contract {

    /**********************************Activity************************************/


    interface RegisterPresenter {

        void register(String url, String posCode, String regCode);

        /**
         * 销毁，防止泄露
         */
        void onDestory();

    }

    interface RegisterView extends BaseView<Contract.RegisterPresenter> {

        /**
         * 注册成功
         */
        void registerSuccess();

        /**
         * 错误原因回调
         *
         * @param error 错误原因
         */
        void registerError(String error);

    }

    interface InitPresenter {
        void startAnimator();

        /**
         * 开始同步数据
         */
        void startInitData();

        /**
         * 停止同步
         */
        void stopInitData();

        /**
         * 完成同步
         */
        void finishInitData();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface InitView extends BaseView<Contract.InitPresenter> {

        /**
         * 开始同步数据
         */
        void startUpdate();

        /**
         * 同步数据进度
         *
         * @param progress 进度
         */
        void updateProgress(int progress);

        /**
         * 停止动画
         */
        void stopUpdate();

        /**
         * 完成同步
         *
         * @param posCode 机器编号
         * @param dep     可登录专柜
         * @param user    可登录用户
         */
        void finishUpdate(String posCode, String dep, String user);
    }


    interface LoginPresenter {
        /**
         * 初始化可登录专柜数据
         */
        void initDepData(Context context);

        /**
         * 初始化可登录用户数据
         */
        void initUserData();

        /**
         * 验证用户信息
         */
        void checkUserInfo(String userCode, String userPwd, String depCode);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface LoginView extends BaseView<Contract.LoginPresenter> {
        /**
         * 返回专柜信息
         *
         * @param depData 专柜信息
         */
        void setDepData(List<Dep> depData);

        /**
         * 返回可登录用户信息
         *
         * @param userData 可登录用户
         */
        void setUserData(List<User> userData);

        /**
         * 登录失败
         */
        void loginFailed(String failedMsg);

        /**
         * 登录成功
         */
        void loginSuccess(User user, Dep dep);

    }


    interface HomePresenter {
        /**
         * 创建界面菜单的数据
         */
        void initMenuList();

        /**
         * 设置用户名、专柜号、当前日期
         */
        void setInfo();


        /**
         * 跳转收银-选择商品界面
         */
        void goShopCart();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface HomeView extends BaseView<Contract.HomePresenter> {
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
         * 跳转到收银选择商品界面
         *
         * @param lsNo 流水单号
         */
        void goShopChartActivity(String lsNo);
    }

    interface ShopCartPresenter {
        /**
         * 加载商品信息
         */
        void initProdList();

        /**
         * 加载本次流水单号中的购物车信息
         *
         * @param lsNo 本次流水单号
         */
        void initOrderInfo(String lsNo);

        /**
         * 筛选商品
         *
         * @param key 筛选关键字
         */
        void searchProdList(String key);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         * @param lsNo       流水单号
         */
        void addToShopCart(DepProduct depProduct, String lsNo);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    /**
     *
     */
    interface ShopCartView extends BaseView<Contract.ShopCartPresenter> {
        /**
         * 设置商品类别
         *
         * @param clsList 分类名称
         */
        void setClsList(List<DepCls> clsList);

        /**
         * 设置商品
         *
         * @param prodList 商品列表
         */
        void setProdList(List<DepProduct> prodList);

        /**
         * 返回过滤筛选后的商品列表
         *
         * @param prodList 商品列表
         */
        void updateProdList(List<DepProduct> prodList);


        /**
         * 更新界面购物车的数量
         *
         * @param num 购物车内的数量
         */
        void updateTradeProd(long num, float price);
    }

    interface ShopListPresenter {
        /**
         * 显示此时购物车内的所有商品
         *
         * @param lsNo 流水单号
         */
        void initShopList(String lsNo);

        /**
         * 设置交易状态
         *
         * @param lsNo   流水单号
         * @param status 交易状态
         */
        void setTradeStatus(String lsNo, int status);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface ShopListView extends BaseView<Contract.ShopListPresenter> {
        /**
         * 显示流水单内商品
         *
         * @param prodList 购物车商品信息
         */
        void showTradeProd(List<TradeProd> prodList);

        /**
         * 返回界面
         */
        void returnHomeActivity();

    }


    interface PayPresenter {
        /**
         * 初始化界面
         */
        void initPayWay();

        /**
         * 交易完成
         */
        void paySuccess(String lsNo, float amount, int payWay);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface PayView extends BaseView<Contract.PayPresenter> {
        /**
         * 界面
         *
         * @param payWay 图标、文字
         */
        void showPayway(List<Menu.MenuList> payWay);
    }
}
