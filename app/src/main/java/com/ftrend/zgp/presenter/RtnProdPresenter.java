package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.RtnContract;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.task.RtnLsDownloadTask;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnContract.RtnProdPresenter {
    private RtnContract.RtnProdView mView;
    private RtnLsDownloadTask task;

    private RtnProdPresenter(RtnContract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(RtnContract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }


    @Override
    public void getTradeByLsNo(final String lsNo) {
        String lsNoLite;
        if (TextUtils.isEmpty(lsNo)) {
            mView.showError("请输入流水单号");
        } else {
            //输入的小票流水，需要取出实际流水号
            lsNoLite = lsNo.length() > 8 ? lsNo.substring(8) : lsNo;
            //先获取本地流水单
            if (RtnHelper.initRtnLocal(lsNoLite)) {
                LogUtil.d("----查本地");
                //获取支付方式
                if (RtnHelper.getProdList().isEmpty()) {
                    mView.showError("该笔交易内无商品");
                    return;
                }
                String appPayType = RtnHelper.getPay().getAppPayType();
                mView.existTrade(RtnHelper.getProdList());
                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(RtnHelper.getTrade().getTradeTime())
                        , lsNo, TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                updateTradeInfo();
            } else {
                //本地无此流水，开始联网查询
                if (ZgParams.isIsOnline()) {
                    //网络有数据
                    LogUtil.d("----查后台");
                    if (lsNo.length() < 16) {
                        mView.showError("本地无数据\n请输入完整流水号获取后台数据");
                        return;
                    }
                    RtnLsDownloadTask.taskStart(lsNo, new OperateCallback() {
                        @Override
                        public void onSuccess(Map<String, Object> data) {
                            if (RtnHelper.getProdList().isEmpty()) {
                                mView.showError("该笔交易内无商品");
                                return;
                            }
                            //有此流水
                            if (RtnHelper.initRtnOnline()) {
                                String appPayType = RtnHelper.getPay().getAppPayType();
                                mView.showTradeFlag(RtnHelper.getTrade().getRtnFlag().equals(RtnHelper.TRADE_FLAG_RTN));
                                mView.existTrade(RtnHelper.getProdList());
                                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType), payTypeImgRes(appPayType));
                                mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(RtnHelper.getTrade().getTradeTime())
                                        , lsNo, TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                                updateTradeInfo();
                            }
                        }

                        @Override
                        public void onError(String code, String msg) {
                            mView.showError(TextUtils.isEmpty(code) ? String.format("%s=", msg) : String.format("%s(%s)", msg, code));
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
        mView.showTradeTotal(RtnHelper.getTrade().getTotal());
        //获取退货流水金额
        mView.showRtnTotal(RtnHelper.getRtnTrade().getTotal());
    }

    @Override
    public void rtnTrade() {
        //判断支付方式
        switch (RtnHelper.getPay().getAppPayType()) {
            case "0":
            case "1":
                //现金
                if (RtnHelper.pay("1", 0)) {
                    if (RtnHelper.rtn()) {
                        mView.showSuccess("退货成功");
                    } else {
                        mView.showError("退货失败");
                    }
                }
                break;
            case "2":
                //支付宝
                break;
            case "3":
                //微信
                break;
            case "4":
                //储值卡
                break;
            case "5":
                //收钱吧
                break;
            default:
                break;
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
        RtnHelper.clearAllData();
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