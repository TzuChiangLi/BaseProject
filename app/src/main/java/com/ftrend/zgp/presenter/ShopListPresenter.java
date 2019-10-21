package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;

import java.util.Map;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListPresenter implements Contract.ShopListPresenter {
    private Contract.ShopListView mView;

    private ShopListPresenter(Contract.ShopListView mView) {
        this.mView = mView;
    }

    public static ShopListPresenter createPresenter(Contract.ShopListView mView) {
        return new ShopListPresenter(mView);
    }

    @Override
    public void checkCancelTradeRight() {
        if (UserRightsHelper.hasRights(UserRightsHelper.CANCEL_TRADE)) {
            MessageUtil.question("是否取消当前交易？", new MessageUtil.MessageBoxYesNoListener() {
                @Override
                public void onYes() {
                    setTradeStatus(TradeHelper.TRADE_STATUS_CANCELLED);
                }

                @Override
                public void onNo() {
                    MessageUtil.show("已放弃当前操作");
                }
            });
        } else {
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
                RestSubscribe.getInstance().queryVipInfo(TradeHelper.getTrade().getVipCode(), new RestCallback(regHandler));
            } else {
                mView.showVipInfoOffline();
            }
        }
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            VipInfo vipInfo = TradeHelper.vip();
            vipInfo.setVipName(body.get("vipName").toString());
            vipInfo.setVipCode(body.get("vipCode").toString());
            vipInfo.setVipDscRate(Double.parseDouble(body.get("vipDscRate").toString()));
            vipInfo.setVipGrade(body.get("vipGrade").toString());
            vipInfo.setVipPriceType(Double.parseDouble(body.get("vipPriceType").toString()));
            vipInfo.setRateRule(Double.parseDouble(body.get("rateRule").toString()));
            vipInfo.setForceDsc(body.get("forceDsc").toString());
            vipInfo.setCardCode(body.get("cardCode").toString());
            vipInfo.setDscProdIsDsc(body.get("dscProdIsDsc").toString());
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
        if (TradeHelper.checkForDsc(index)) {
            mView.showSingleDscDialog(index);
        } else {
            mView.showNoRightDscDialog("该商品无优惠");
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
    public void changeAmount(int index, double changeAmount) {
        if (changeAmount < 0) {
            changeAmount = TradeHelper.getProdList().get(index).getAmount() - 1 == 0 ? 0 : -1;
        }
        if (TradeHelper.changeAmount(index, changeAmount) > 0) {
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
        if (UserRightsHelper.hasRights(UserRightsHelper.CANCEL_PROD)) {
            TradeHelper.delProduct(index);
            mView.delTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("无行清权限");
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
        }
    }

}
