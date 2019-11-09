package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.task.RtnLsDownloadTask;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements Contract.RtnProdPresenter {
    private Contract.RtnProdView mView;
    private RtnLsDownloadTask task;

    private RtnProdPresenter(Contract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(Contract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }


    @Override
    public void getTradeByLsNo(final String lsNo) {
        if (TextUtils.isEmpty(lsNo)) {
            mView.showError("请输入流水单号");
        } else {
            if (TradeHelper.initRtnSale(lsNo)) {
                //有此流水
                String appPayType = TradeHelper.getPay().getAppPayType();
                List<TradeProd> prodList = TradeHelper.getProdList();
                mView.existTrade(prodList);
                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(TradeHelper.getTrade().getTradeTime())
                        , lsNo, TradeHelper.getCashierByUserCode(TradeHelper.getTrade().getCashier()));
                updateTradeInfo();
            } else {
                //本地无此流水，开始联网查询
                if (ZgParams.isIsOnline()) {
                    //网络有数据
                    RtnLsDownloadTask.taskStart(lsNo, new OperateCallback() {
                        @Override
                        public void onSuccess(Map<String, Object> data) {
                            if (TradeHelper.initRtnSale(String.format("%s", lsNo.substring(8)))) {
                                //有此流水
                                String appPayType = TradeHelper.getPay().getAppPayType();
                                List<TradeProd> prodList = TradeHelper.getProdList();
                                mView.existTrade(prodList);
                                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(TradeHelper.getTrade().getTradeTime())
                                        , lsNo, TradeHelper.getCashierByUserCode(TradeHelper.getTrade().getCashier()));
                                updateTradeInfo();
                            } else {
                                mView.showError("无此流水信息");
                            }
                        }

                        @Override
                        public void onError(String code, String msg) {
                            mView.showError(String.format("%s(%s)", msg, code));
                        }
                    });
                } else {
                    mView.showError("单机模式无法查询历史流水，请联机后重试");
                }

            }
        }
    }


    @Override
    public void updateTradeInfo() {
        //获取销售流水金额
        mView.showTradeTotal(TradeHelper.getTrade().getTotal());
        //获取退货流水金额
        mView.showRtnTotal(TradeHelper.getTrade().getRtnTotal());
    }

    @Override
    public void rtnTrade() {
        LogUtil.d("----退货");
        if (TradeHelper.rtn()) {
            mView.showSuccess("退货成功");
            LogUtil.d("----退货成功");
        } else {
            LogUtil.d("----退货失败");
            mView.showError("退货失败");
        }
    }

    @Override
    public void changePrice(int index, double price) {
        if (price > (TradeHelper.getProdList().get(index).getTotal()
                / TradeHelper.getProdList().get(index).getAmount())) {
            mView.showError("价格已超过销价");
            return;
        }
        if (TradeHelper.changeRtnPrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("改价未成功");
        }
    }


    @Override
    public void changeAmount(int index, double changeAmount) {
        double maxAmount = TradeHelper.getProdList().get(index).getAmount();
        double rtnAmount = TradeHelper.getProdList().get(index).getRtnAmount();
        LogUtil.d("----rtn/max:" + rtnAmount + "/" + maxAmount);
        if (rtnAmount + changeAmount < 0 || rtnAmount + changeAmount > maxAmount) {
            return;
        }
        //仅修改临时数据，不修改数据库内数据
        if (TradeHelper.doRtnChangeAmount(index, changeAmount) > 0) {
            //更新列表界面
            mView.updateTradeProd(index);
            //更新底部信息
            updateTradeInfo();
        } else {
            mView.showError("数据修改出现异常");
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }

    private int payTypeImgRes(String appPayType) {
        switch (appPayType) {
            case "1":
                return R.drawable.money;
            case "2":
                return R.drawable.alipay;
            case "3":
                return R.drawable.wechat;
            case "4":
                return R.drawable.card;
            case "5":
                return R.drawable.shouqianba;
            default:
                return R.drawable.money;
        }
    }

}
//                                RestSubscribe.getInstance().queryRefundLs(lsNo, new RestCallback(
//                                        new RestResultHandler() {
//                                            @Override
//                                            public void onSuccess(Map<String, Object> body) {
//                                                Map<String, Object> trade = (Map<String, Object>) body.get("trade");
//                                                List<Map<String, Object>> prod = (List<Map<String, Object>>) body.get("prod");
//                                                Map<String, Object> pay = (Map<String, Object>) body.get("pay");
//
//                                            }
//
//                                            @Override
//                                            public void onFailed(String errorCode, String errorMsg) {
//                                                mView.showError(errorMsg);
//                                            }
//                                        }));