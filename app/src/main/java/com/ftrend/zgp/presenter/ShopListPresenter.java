package com.ftrend.zgp.presenter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.api.ShopListContract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.FormatHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.StringInputCallback;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.sunmi.VipCardData;
import com.sunmi.pay.hardware.aidl.AidlConstants;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListPresenter implements ShopListContract.ShopListPresenter {
    private static final String TAG = "ShopListPresenter";
    private ShopListContract.ShopListView mView;

    private ShopListPresenter(ShopListContract.ShopListView mView) {
        this.mView = mView;
    }

    public static ShopListPresenter createPresenter(ShopListContract.ShopListView mView) {
        return new ShopListPresenter(mView);
    }

    @Override
    public void refreshTrade() {
        TradeHelper.setTradeStatus(TradeHelper.TRADE_STATUS_CANCELLED);
        TradeHelper.saveVipInfo();
        //清理数据
        TradeHelper.clear();
        TradeHelper.clearVip();
        //初始化数据
        TradeHelper.initSale();
    }

    @Override
    public void checkCancelTradeRight() {
        if (UserRightsHelper.hasRights(UserRightsHelper.CANCEL_TRADE)) {
            MessageUtil.question("是否取消当前交易？", new MessageUtil.MessageBoxYesNoListener() {
                @Override
                public void onYes() {
                    LogUtil.u(TAG, "购物车", "取消交易");
                    setTradeStatus(TradeHelper.TRADE_STATUS_CANCELLED);
                }

                @Override
                public void onNo() {
                    MessageUtil.show("已放弃当前操作");
                }
            });
        } else {
            LogUtil.u(TAG, "购物车", "无权限取消交易");
            MessageUtil.showError("无此操作权限！");
        }
    }

    @Override
    public void showVipInfo() {
        if (TradeHelper.vip != null) {
            mView.showVipInfoOnline();
            return;
        }
        if (!TextUtils.isEmpty(TradeHelper.getTrade().getVipCode())) {
            //未结、未挂起的单据有会员优惠的信息，但是vip是null
            if (ZgParams.isIsOnline()) {
                LogUtil.u(TAG, "购物车", "在线查询会员优惠");
                RestSubscribe.getInstance().queryVipInfo(TradeHelper.getTrade().getVipCode(), new RestCallback(regHandler));
            } else {
                LogUtil.u(TAG, "购物车", "离线模式保存会员信息");
                VipInfo vipInfo = TradeHelper.vip();
                vipInfo.setCardCode(TradeHelper.getTrade().getCardCode());
                vipInfo.setVipCode(TradeHelper.getTrade().getVipCode());
                vipInfo.setVipGrade(TradeHelper.getTrade().getVipGrade());
                mView.showVipInfoOffline(vipInfo);
            }
        }
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            VipInfo vipInfo = TradeHelper.vip();
            vipInfo.setVipName(body.getString("vipName"));
            vipInfo.setVipCode(body.getString("vipCode"));
            vipInfo.setVipDscRate(body.getDouble("vipDscRate"));
            vipInfo.setVipGrade(body.getString("vipGrade"));
            vipInfo.setVipPriceType(body.getDouble("vipPriceType"));
            vipInfo.setRateRule(body.getDouble("rateRule"));
            vipInfo.setForceDsc(body.getString("forceDsc"));
            vipInfo.setCardCode(body.getString("cardCode"));
            vipInfo.setDscProdIsDsc(body.getString("dscProdIsDsc"));
            //保存会员信息
            TradeHelper.saveVip();
            //刷新界面
            Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_VIP_INFO, vipInfo);
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            mView.showError(errorCode + errorMsg);
        }
    };

    @Override
    public void checkProdForDsc(int index) {
        TradeProd prod = TradeHelper.getProdList().get(index);
        if (prod.isForDsc()) {
            mView.showSingleDscDialog(index);
        } else {
            mView.showNoRightDscDialog("该商品不允许优惠");
        }
    }

    @Override
    public void initShopList() {
        //加载商品列表
        for (TradeProd prod :
                TradeHelper.getProdList()) {
            prod.setSelect(false);
        }
        mView.showTradeProd(TradeHelper.getProdList());
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void setTradeStatus(String status) {
        TradeHelper.setTradeStatus(status);
        TradeHelper.saveVipInfo();
        mView.returnHomeActivity(TradeHelper.convertTradeStatus(status));
        TradeHelper.clear();
        //完成后清理vip信息
        TradeHelper.clearVip();
    }

    @Override
    public void checkInputNum(int index, double changeAmount) {
        if ("1".equals(ZgParams.getInputNum())) {
            //手动输入
            mView.showInputNumDialog(index);
        } else {
            //直接修改
            changeAmount(index, changeAmount);
        }
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        if (changeAmount < 0) {
            changeAmount = TradeHelper.getProdList().get(index).getAmount() - 1 == 0 ? 0 : changeAmount;
        }
        if (TradeHelper.changeAmount(index, changeAmount) > 0) {
            updateTradeInfo();
            mView.updateTradeProd(index);
        }
    }

    @Override
    public void coverAmount(int index, double changeAmount) {
        if (TradeHelper.coverAmount(index, changeAmount) > 0) {
            updateTradeInfo();
            mView.updateTradeProd(index);
        }
    }

    @Override
    public void updateTradeInfo() {
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void updateTradeList(int index, TradeProd tradeProd) {
        mView.updateTradeProd(index);
    }

    @Override
    public void checkDelProdRight(int index) {
        if (UserRightsHelper.hasRights(UserRightsHelper.CANCEL_PROD)) {
            mView.hasDelProdRight(index);
        } else {
            mView.showError("无行清权限");
        }
    }

    /**
     * @param index 索引
     */
    @Override
    public void delTradeProd(int index) {
        if (TradeHelper.delProduct(index)) {
            mView.delTradeProd(index);
            updateTradeInfo();
            if (TradeHelper.getProdList().size() == 0) {
                mView.confirmEmptyTrade();
            }
        }
    }

    @Override
    public void vipInput(final Context context) {
        if (!SunmiPayHelper.getInstance().serviceAvailable()) {
            LogUtil.e("刷卡服务不可用，请手动输入会员信息");
            // 切换到手机号输入界面
            vipMobileInput(context);
        } else {
            MessageUtil.waitBegin("请刷卡...", new MessageUtil.MessageBoxCancelListener() {
                @Override
                public boolean onCancel() {
                    // 取消刷卡
                    SunmiPayHelper.getInstance().cancelReadCard();
                    // 切换到手机号输入界面
                    vipMobileInput(context);
                    return true;
                }
            });
            SunmiPayHelper.getInstance().readCard(new SunmiPayHelper.ReadCardCallback() {
                @Override
                public void onSuccess(VipCardData data) {
                    Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_VIPCARD_SUCCESS);
                    queryVipInfo(data.getCardCode(), data.getCardType());
                }

                @Override
                public void onError(String msg) {
                    Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_VIPCARD_FAILE, msg);
                }
            });
        }
    }

    /**
     * 输入会员手机号查询会员信息
     *
     * @param context
     */
    private void vipMobileInput(final Context context) {
        InputPanel.showVipMobile(context, new StringInputCallback() {
            @Override
            public void onOk(String value) {
                queryVipInfo(value, null);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public String validate(String value) {
                if ("SCAN".equals(value)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mView.goScan();
                        }
                    }, 150);
                    return null;
                }
                if (!FormatHelper.checkPhoneNoFormat(value)) {
                    return "手机号无效";
                }
                return null;
            }
        });
    }

    /**
     * 查询会员信息并计算会员优惠
     *
     * @param code 手机号或会员卡号
     */
    @Override
    public void queryVipInfo(String code, AidlConstants.CardType cardType) {
        String type = "";
        if (cardType == AidlConstants.CardType.MIFARE) {
            type = "1";
        } else if (cardType == AidlConstants.CardType.MAGNETIC) {
            type = "2";
        }
        if (ZgParams.isIsOnline()) {
            //在线查询会员信息
            RestSubscribe.getInstance().queryVipInfo(code, type, new RestCallback(new RestResultHandler() {
                @Override
                public void onSuccess(RestBodyMap body) {
                    if (body != null) {
                        VipInfo vipInfo = TradeHelper.vip();
                        vipInfo.setVipName(body.getString("vipName"));
                        vipInfo.setVipCode(body.getString("vipCode"));
                        vipInfo.setVipDscRate(body.getDouble("vipDscRate"));
                        vipInfo.setVipGrade(body.getString("vipGrade"));
                        vipInfo.setVipPriceType(body.getDouble("vipPriceType"));
                        vipInfo.setRateRule(body.getDouble("rateRule"));
                        vipInfo.setForceDsc(body.getString("forceDsc"));
                        vipInfo.setCardCode(body.getString("cardCode"));
                        vipInfo.setDscProdIsDsc(body.getString("dscProdIsDsc"));
                        //保存会员信息到流水
                        TradeHelper.saveVip();
                        //刷新会员优惠
                        DscHelper.saveVipDsc();
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_VIP_INFO, vipInfo);
                        MessageUtil.show("会员设置成功");
                    } else {
                        MessageUtil.showError("查询会员信息失败：返回结果为空");
                    }
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    MessageUtil.error(errorCode, errorMsg);
                }
            }));
        } else {
            MessageUtil.showWarning("当前为单机模式，无法查询会员信息");
            //保存vipCode（@后加卡类型，用于后台解析卡号并查询会员信息）
            TradeHelper.saveVipCodeOffline(code + (TextUtils.isEmpty(type) ? "" : "@" + type));
            mView.showVipInfoOffline(code);
        }
    }

    /**
     * @param prodCode 商品编码，可能不唯一
     * @param barCode  商品条码，可能为空
     */
    @Override
    public void getProdPriceFlag(String prodCode, String barCode, int index) {
        //如果条码不为空，即查条码
        if (TextUtils.isEmpty(barCode)) {
            if (TradeHelper.getPriceFlagByBarCode(barCode)) {
                mView.showPriceChangeDialog(index);
            } else {
                mView.showNoRightDscDialog("该商品不允许改价");
            }
        } else {
            if (TradeHelper.getPriceFlagByProdCode(prodCode)) {
                mView.showPriceChangeDialog(index);
            } else {
                mView.showNoRightDscDialog("该商品不允许改价");
            }
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
            System.gc();
        }
    }
}
