package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.RtnContract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.task.RtnLsDownloadTask;

import java.text.SimpleDateFormat;
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
            case "8":
                //储值卡
                MessageUtil.showError("储值卡退款功能未实现");
                break;
            default:
                //默认按收钱吧处理
                MessageUtil.showError("收钱吧退款功能未实现");
/*                //判断收钱吧
                //TODO 2019年11月14日10:28:02 收钱吧退款流程暂时注释
                if (appPayType.contains("SQB")) {
                    if (RtnHelper.pay(appPayType, "")) {
                        if (RtnHelper.rtn()) {
                            mView.showSuccess("退货成功");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mView.finish();
                                }
                            }, 1500);
                        } else {
                            mView.showError("保存退货失败");
                        }
                    }
//                    SqbPayHelper.refundBySn(RtnHelper.getRtnTrade(), RtnHelper.getRtnSn(), new SqbPayHelper.PayResultCallback() {
//                        @Override
//                        public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
//                            if (isSuccess) {
//                                // TODO: 2019/10/26 微信支付账号长度超过后台数据库对应字段长度，暂时先不记录支付账号
//                                if (RtnHelper.pay(payType, "")) {
//                                    if (RtnHelper.rtn()) {
//                                        mView.showSuccess("退货成功");
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                mView.finish();
//                                            }
//                                        }, 1500);
//                                    } else {
//                                        mView.showError("保存退货失败");
//                                    }
//                                }
//                            } else {
//                                mView.showError(errMsg);
//                            }
//                        }
//                    });
                }*/
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
            case "0":
                //现金
                return R.drawable.money;
            case "8":
                //储值卡
                return R.drawable.card;
            default:
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