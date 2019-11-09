package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.User;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.util.List;

/**
 * 接口类
 *
 * @author liziqiang@ftrend.cn
 */
public interface Contract {

    /**********************************Activity************************************/

    interface OrderOutPresenter {
        /**
         * 初始化界面数据
         */
        void initView();

        /**
         * 取单操作
         *
         * @param lsNo 流水单号
         * @return -1 - 购物车不为空，0 - 取单成功， 1 - 取单失败
         */
        int doOrderOut(String lsNo);

        /**
         * 销毁，防止泄露
         */
        void onDestory();

    }

    interface OrderOutView extends BaseView<Contract.OrderOutPresenter> {
        /**
         * 初始化界面列表数据
         *
         * @param tradeList 数据
         */
        void initOutOrder(List<Trade> tradeList);

    }

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
        /**
         * 开始动画
         */
        void startAnimator();

        /**
         * 开始同步数据
         *
         * @param step 步骤：1 - 下载基础数据；2 - 下载实时流水
         */
        void startInitData(int step);

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
         * @param step     步骤：1 - 下载基础数据；2 - 下载实时流水
         * @param progress 进度
         */
        void updateProgress(int step, int progress);

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
        void goAsyncTask();

        /**
         * 注销登录
         */
        void logout();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface HomeView extends BaseView<Contract.HomePresenter> {
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
        void tipHandover();


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
         * 执行数据同步
         */
        void doAsyncTask();

        /**
         * 注销登录
         */
        void logout();

    }

    interface ShopCartPresenter {
        /**
         * 刷新交易信息
         */
        void refreshTrade();

        /**
         * 加载商品信息
         */
        void initProdList();

        /**
         * 加载本次流水单号中的购物车信息
         */
        void initOrderInfo();

        /**
         * 刷新购物车信息
         */
        void updateOrderInfo();

        /**
         * 筛选商品
         *
         * @param key 筛选关键字
         */
        void searchProdList(String... key);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         */
        void addToShopCart(DepProduct depProduct);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         * @param price      价格
         */
        void addToShopCart(DepProduct depProduct, double price);

        /**
         * 设置交易状态
         *
         * @param status 交易状态
         */
        void setTradeStatus(String status);

        /**
         * 取消改价操作，购物车已添加的商品回滚
         *
         * @param index 索引
         */
        void cancelPriceChange(int index);

        /**
         * 更新交易信息
         */
        void updateTradeInfo();

        /**
         * 通过扫码识别并定位商品
         *
         * @param code     识别码
         * @param prodList 商品列表
         */
        void searchProdByScan(String code, List<DepProduct> prodList);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface ShopCartView extends BaseView<Contract.ShopCartPresenter> {
        /**
         * 弹窗显示错误信息文本
         *
         * @param error 错误信息文本
         */
        void showError(String error);

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
        void updateTradeProd(double num, double price);


        /**
         * 返回界面
         *
         * @param statusResult 状态结果
         */
        void returnHomeActivity(String statusResult);

        /**
         * 定位商品
         *
         * @param index 索引
         */
        void setScanProdPosition(int index);

        /**
         * 扫码的商品不存在
         */
        void noScanProdPosition();

        /**
         * 撤销商品添加
         *
         * @param index 索引
         */
        void cancelAddProduct(int index);

        /**
         * 刷新信息
         */
        void updateOrderInfo();
    }

    interface ShopListPresenter {
        /**
         * 刷新交易信息
         */
        void refreshTrade();

        /**
         * 检查取消交易权限
         */
        void checkCancelTradeRight();

        /**
         * 检查是否拥有会员优惠权限并展示界面
         */
        void showVipInfo();

        /**
         * 商品是否允许优惠,弹出相应提示
         *
         * @param index 索引
         */
        void checkProdForDsc(int index);

        /**
         * 显示购物车内的所有商品
         */
        void initShopList();

        /**
         * 设置交易状态
         *
         * @param status 交易状态
         */
        void setTradeStatus(String status);


        /**
         * 更改商品数量
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void changeAmount(int index, double changeAmount);

        /**
         * 更新交易信息
         */
        void updateTradeInfo();

        /**
         * 更新列表数据
         *
         * @param index     索引
         * @param tradeProd 修改的数据
         */
        void updateTradeList(int index, TradeProd tradeProd);

        /**
         * 检查行情权限
         *
         * @param index 索引
         */
        void checkDelProdRight(int index);

        /**
         * 行清商品
         *
         * @param index 索引
         */
        void delTradeProd(int index);

        /**
         * 会员输入（刷卡或者输入手机号）
         */
        void vipInput(final Context context);

        /**
         * 查询专柜商品信息表中该商品的改价权限
         *
         * @param prodCode 商品编码，可能不唯一
         * @param barCode  商品条码，可能为空
         * @param index    商品索引
         */
        void getProdPriceFlag(String prodCode, String barCode, int index);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface ShopListView extends BaseView<Contract.ShopListPresenter> {

        /**
         * 展示会员信息
         */
        void showVipInfoOnline();

        /**
         * 检测到交易已有会员优惠，但是此时离线
         */
        void showVipInfoOffline();

        /**
         * 弹窗显示错误信息文本
         *
         * @param error 错误信息文本
         */
        void showError(String error);

        /**
         * 显示流水单内商品
         *
         * @param prodList 购物车商品信息
         */
        void showTradeProd(List<TradeProd> prodList);

        /**
         * 更新合计金额
         */
        void updateTotal(double total);

        /**
         * 更新购物车总商品数
         */
        void updateCount(double count);

        /**
         * 更新界面 - 行清
         *
         * @param index 索引
         */
        void delTradeProd(int index);

        /**
         * 加减更改
         *
         * @param index 索引
         */
        void updateTradeProd(int index);

        /**
         * 返回界面
         *
         * @param status 更改状态
         */
        void returnHomeActivity(String status);

        /**
         * 是否可以改价
         *
         * @param index 索引
         */
        void showPriceChangeDialog(int index);

        /**
         * 单项优惠
         *
         * @param index 索引
         */
        void showSingleDscDialog(int index);

        /**
         * 单项优惠
         *
         * @param msg 文本
         */
        void showNoRightDscDialog(String msg);

        /**
         * 允许行清
         *
         * @param index 索引
         */
        void hasDelProdRight(int index);

    }

    interface PayPresenter {
        //TODO 测试打印
        void getPrintData(SunmiPrinterService service);

        /**
         * 初始化界面
         */
        void initPayWay();

        /**
         * 收钱吧
         *
         * @param value 扫码结果
         */
        void payByShouQian(String value);

        /**
         * 交易完成
         *
         * @param appPayType APP支付方式
         */
        boolean paySuccess(String appPayType);

        /**
         * 交易完成
         *
         * @param appPayType APP支付方式
         * @param value      实际支付金额
         */
        boolean paySuccess(String appPayType, double value);

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

        /**
         * 显示应收款
         *
         * @param total 订单总金额
         */
        void showTradeInfo(double total);

        /**
         * 等待付款结果
         */
        void waitPayResult();

        /**
         * 支付成功
         */
        void paySuccess();

        /**
         * 支付失败
         *
         * @param msg 错误信息
         */
        void payFail(String msg);

        /**
         * 显示错误
         *
         * @param msg 错误信息
         */
        void showError(String msg);
    }

    interface HandoverPresenter {
        /**
         * 初始化界面
         */
        void initView();

        /**
         * 交班
         */
        void doHandover();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface HandoverView extends BaseView<Contract.HandoverPresenter> {
        /**
         * 采用列表的方式显示交班信息
         *
         * @param recordList 交班信息
         */
        void showHandoverRecord(List<HandoverRecord> recordList);


        /**
         * 交班成功并返回
         */
        void success();

        /**
         * 单机模式不允许交班
         */
        void showOfflineTip();

    }

    interface RtnProdPresenter {
        /**
         * 获取流水
         *
         * @param lsNo 流水号
         */
        void getTradeByLsNo(String lsNo) throws CloneNotSupportedException;


        /**
         * 更新信息
         */
        void updateTradeInfo();

        /**
         * 退货
         */
        void rtnTrade();

        /**
         * 改商品价格
         *
         * @param index 商品索引
         * @param price 商品退货改价
         */
        void changePrice(int index, double price);


        /**
         * 更改商品数量
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void changeAmount(int index, double changeAmount);


        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface RtnProdView extends BaseView<Contract.RtnProdPresenter> {
        /**
         * 加减更改
         *
         * @param index 索引
         */
        void updateTradeProd(int index);

        /**
         * 存在此流水并显示
         *
         * @param data 流水内商品信息
         */
        void existTrade(List<TradeProd> data);

        /**
         * @param tradeTotal 订单金额
         */
        void showTradeTotal(double tradeTotal);

        /**
         * @param rtnTotal 实退金额
         */
        void showRtnTotal(double rtnTotal);

        /**
         * 支付方式类型
         *
         * @param payTypeName 名称
         * @param img         图片
         */
        void showPayTypeName(String payTypeName, int img);

        /**
         * @param info 交易时间，流水号，收款员
         */
        void showTradeInfo(String... info);

        /**
         * 异常信息
         *
         * @param msg 文本
         */
        void showError(String msg);

        /**
         * 成功信息
         *
         * @param msg 文本
         */
        void showSuccess(String msg);
    }

}
