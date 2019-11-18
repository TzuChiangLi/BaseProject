package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.RtnContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.task.RtnLsDownloadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnContract.RtnProdPresenter {
    private RtnContract.RtnProdView mView;

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
            mView.showError("请输入流水号");
        } else if (lsNo.length() != 8 && lsNo.length() != 16) {
            mView.showError("流水号长度不正确");
        } else {
            //输入的小票流水，需要取出实际流水号
            lsNoLite = lsNo.length() > 8 ? lsNo.substring(8) : lsNo;
            //先获取本地流水单
            if (RtnHelper.initRtnLocal(lsNoLite)) {
                //获取支付方式
                if (RtnHelper.getProdList().isEmpty()) {
                    mView.showError("该笔交易内无商品");
                    return;
                }
                String appPayType = RtnHelper.getPay().getAppPayType();
                mView.existTrade(RtnHelper.getProdList());
                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType, RtnHelper.getTrade().getDepCode()),
                        payTypeImgRes(appPayType));
                mView.showTradeInfo(formatTradeTime(), RtnHelper.getTrade().getFullLsNo(),
                        TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                updateTradeInfo();
            } else {
                //本地无此流水，开始联网查询
                if (ZgParams.isIsOnline()) {
                    //网络有数据
                    if (lsNo.length() < 16) {
                        mView.showError("未找到对应的实时流水\n请输入完整流水号查询历史流水");
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
                                mView.existTrade(RtnHelper.getProdList());
                                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType, RtnHelper.getTrade().getDepCode()),
                                        payTypeImgRes(appPayType));
                                mView.showTradeInfo(formatTradeTime(), lsNo,
                                        TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                                updateTradeInfo();
                            } else {
                                mView.showError("退货流水初始化失败");
                            }
                        }

                        @Override
                        public void onError(String code, String msg) {
                            mView.showError(TextUtils.isEmpty(code) ? String.format("%s", msg) : String.format("%s(%s)", msg, code));
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
        if (RtnHelper.getTrade().getRtnFlag().equals(RtnHelper.TRADE_FLAG_RTN) ||
                (RtnHelper.getTrade().getTradeFlag().equals(TradeHelper.TRADE_FLAG_REFUND))) {
            return;
        }
        //判断支付方式
        String appPayType = RtnHelper.getPay().getAppPayType();
        switch (appPayType) {
            case "0":
                //现金
                if (RtnHelper.pay(appPayType, 0)) {
                    if (RtnHelper.rtn()) {
                        MessageUtil.info("退货成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                mView.finish();
                            }
                        });
                    } else {
                        MessageUtil.error("退货失败");
                    }
                }
                break;
            case "7":
                //IC卡
                MessageUtil.showError("IC卡退款功能未实现");
                break;
            case "8":
                //储值卡
                doMagCardPay();
                break;
            default:
                //默认按收钱吧处理
                doSqbPay();
                break;
        }
    }

    @Override
    public void changePrice(int index, double price) {
        TradeProd prod = RtnHelper.getProdList().get(index);
        if (price > (prod.getTotal() / prod.getAmount())) {
            mView.showError("退货单价不能大于原销售单价");
            return;
        }
        if (price == 0) {
            mView.showError("退货单价应大于0");
            return;
        }
        if (RtnHelper.changeRtnPrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("退货单价修改失败");
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

    /**
     * @param appPayType 支付方式代码
     * @return 图片资源
     */
    private int payTypeImgRes(String appPayType) {
        switch (appPayType) {
            case PayType.PAYTYPE_CASH://现金
                return R.drawable.money;
            case PayType.PAYTYPE_ICCARD: //IC卡
            case PayType.PAYTYPE_PREPAID://储值卡
                return R.drawable.card;
            default:// 其他都认为是收钱吧
                if (appPayType.startsWith("SQB_")) {
                    return R.drawable.shouqianba;
                }
                return R.drawable.money;
        }
    }

    /**
     * 格式化交易时间
     *
     * @return
     */
    private String formatTradeTime() {
        return new SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.CHINA)
                .format(RtnHelper.getTrade().getTradeTime());
    }

    //region 储值卡退款

    /**
     * 请求数据标识，用于轮询请求结果
     */
    private final String[] payDataSign = {""};

    /**
     * 请求发起时间
     */
    private final long[] payRequestTime = {0};

    /**
     * 磁卡支付，调用后台服务完成支付
     */
    private void doMagCardPay() {
        MessageUtil.waitBegin("储值卡退款处理中...", new MessageUtil.MessageBoxCancelListener() {
            @Override
            public boolean onCancel() {
                return false;//支付过程无法取消
            }
        });
        Trade trade = RtnHelper.getRtnTrade();
        RestSubscribe.getInstance().payCardRequest(
                ZgParams.getPosCode(),
                trade.getLsNo(),
                CommonUtil.dateToYyyyMmDd(new Date()),
                ZgParams.getCurrentUser().getUserCode(),
                RtnHelper.getPay().getPayCode(),
                trade.getTotal() * -1,
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(Map<String, Object> body) {
                        payDataSign[0] = body.get("dataSign").toString();
                        payRequestTime[0] = System.currentTimeMillis();
                        requestCardPayResult();
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {
                        MessageUtil.waitError(errorCode, errorMsg, null);
                    }
                }));
    }

    /**
     * 轮询支付结果
     */
    private void requestCardPayResult() {
        // 30秒超时
        if (System.currentTimeMillis() - payRequestTime[0] > 30 * 1000) {
            MessageUtil.waitError("通讯超时，请稍后重试", null);
            return;
        }

        //延迟500毫秒再查询
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RestSubscribe.getInstance().payCard(payDataSign[0], new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                if (RtnHelper.pay(PayType.PAYTYPE_PREPAID, RtnHelper.getPay().getPayCode())) {
                    if (RtnHelper.rtn()) {
                        MessageUtil.waitSuccesss("储值卡退款成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                mView.finish();
                            }
                        });
                    } else {
                        MessageUtil.waitError("储值卡退款失败", null);
                    }
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                if (errorCode.endsWith("70")) {//70-处理中
                    requestCardPayResult();
                } else {
                    MessageUtil.waitError("储值卡退款失败", null);
                }
            }
        }));
    }
    //endregion

    //region 收钱吧退款
    private void doSqbPay() {
        String clientSn = RtnHelper.getTrade().getSqbPayClientSn();
        SqbPayHelper.refundByClientSn(RtnHelper.getRtnTrade(), clientSn, new SqbPayHelper.PayResultCallback() {
            @Override
            public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                if (isSuccess) {
                    if (RtnHelper.pay(payType, "")) {
                        if (RtnHelper.rtn()) {
                            MessageUtil.waitSuccesss("退款成功", new MessageUtil.MessageBoxOkListener() {
                                @Override
                                public void onOk() {
                                    mView.finish();
                                }
                            });
                        } else {
                            MessageUtil.waitError("退款失败", null);
                        }
                    }
                } else {
                    MessageUtil.error(errMsg);
                }
            }
        });
    }
    //endregion
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