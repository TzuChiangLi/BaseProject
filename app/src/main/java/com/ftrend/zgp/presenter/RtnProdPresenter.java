package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
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
        LogUtil.d("----lsNo:" + lsNo);
        if (TextUtils.isEmpty(lsNo)) {
            mView.showError("请输入流水单号");
        } else {
            //先获取本地流水单
            if (RtnHelper.initRtnSale(lsNo)) {
                LogUtil.d("----trade is not empty");
                //获取支付方式
                String appPayType = RtnHelper.getPay().getAppPayType();
                mView.existTrade(RtnHelper.getProdList());
                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(RtnHelper.getTrade().getTradeTime())
                        , lsNo, TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                updateTradeInfo();
            } else {
                LogUtil.d("----trade is null");
                if (true) {
                    //有此流水
                } else {
                    //本地无此流水，开始联网查询
                    if (ZgParams.isIsOnline()) {
                        //网络有数据
                        RtnLsDownloadTask.taskStart(lsNo, new OperateCallback() {
                            @Override
                            public void onSuccess(Map<String, Object> data) {
                                //有此流水
                                String appPayType = TradeHelper.getPay().getAppPayType();
                                List<TradeProd> prodList = TradeHelper.getProdList();
                                LogUtil.d("----prodList.size:" + prodList.size());
                                mView.existTrade(prodList);
                                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(TradeHelper.getTrade().getTradeTime())
                                        , lsNo, TradeHelper.getCashierByUserCode(TradeHelper.getTrade().getCashier()));
                                updateTradeInfo();
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
    }


    @Override
    public void updateTradeInfo() {
        //获取销售流水金额
        mView.showTradeTotal(RtnHelper.getTrade().getTotal());
        //获取退货流水金额
        mView.showRtnTotal(RtnHelper.getRtnTrade().getTotal());
    }

    @Override
    public void rtnTrade() {
        LogUtil.d("----退货");
        if (RtnHelper.rtn()) {
            mView.showSuccess("退货成功");
            LogUtil.d("----退货成功");
        } else {
            LogUtil.d("----退货失败");
            mView.showError("退货失败");
        }
    }

    @Override
    public void changePrice(int index, double price) {
        if (price > (RtnHelper.getProdList().get(index).getTotal()
                / RtnHelper.getProdList().get(index).getAmount())) {
            mView.showError("价格已超过销价");
            return;
        }
        if (price == 0) {
            mView.showError("输入价格为0");
            return;
        }
        if (RtnHelper.changeRtnPrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("改价未成功");
        }
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        //仅修改临时数据，不修改数据库内数据
        RtnHelper.rtnChangeAmount(index, changeAmount);
        //更新列表界面
        mView.updateTradeProd(index);
        //更新底部信息
        updateTradeInfo();
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