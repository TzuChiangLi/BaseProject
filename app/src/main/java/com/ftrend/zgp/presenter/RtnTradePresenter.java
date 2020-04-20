package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.api.RtnTradeContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.sunmi.VipCardData;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnTradePresenter implements RtnTradeContract.RtnTradePresenter {
    private static final String TAG = "RtnTradePresenter";
    private RtnTradeContract.RtnTradeView mView;

    private RtnTradePresenter(RtnTradeContract.RtnTradeView mView) {
        this.mView = mView;
    }

    public static RtnTradePresenter createPresenter(RtnTradeContract.RtnTradeView mView) {
        return new RtnTradePresenter(mView);
    }


    /**
     * @param lsNo 流水号
     */
    @Override
    public void getTradeByLsNo(final String lsNo) {
        //先清理掉所有信息
        RtnHelper.clearAllData();
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
                        TradeHelper.payTypeImgRes(appPayType));
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
                    RestSubscribe.getInstance().queryRefundLs(lsNo, new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(RestBodyMap body) {
                            RestBodyMap trade = body.getMap("trade");
                            List<RestBodyMap> prodList = body.getMapList("prod");
                            RestBodyMap pay = body.getMap("pay");
                            if (trade == null || prodList == null || pay == null) {
                                mView.showError("流水数据异常");
                                return;
                            }
                            //不是当前专柜的销售流水不允许退货
                            if (!ZgParams.getCurrentDep().getDepCode().equals(trade.get("depCode"))) {
                                mView.showError("指定流水不存在");
                                return;
                            }
                            //保存到内存中
                            RtnHelper.saveLs(trade, prodList, pay);
                            //判断商品件数
                            if (RtnHelper.getProdList().isEmpty()) {
                                mView.showError("该笔交易内无商品");
                                return;
                            }
                            //有此流水
                            if (RtnHelper.initRtnOnline()) {
                                String appPayType = RtnHelper.getPay().getAppPayType();
                                mView.existTrade(RtnHelper.getProdList());
                                mView.showPayTypeName(TradeHelper.convertAppPayType(appPayType, RtnHelper.getTrade().getDepCode()),
                                        TradeHelper.payTypeImgRes(appPayType));
                                mView.showTradeInfo(formatTradeTime(), lsNo,
                                        TradeHelper.getCashierByUserCode(RtnHelper.getTrade().getCashier()));
                                updateTradeInfo();
                            } else {
                                mView.showError("退货流水初始化失败");
                            }
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            LogUtil.e("下载流水失败: " + errorCode + " - " + errorMsg);
                            mView.showError(errorMsg);
                        }
                    }));
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
            case PayType.PAYTYPE_CASH:
                //现金
                if (RtnHelper.pay(appPayType, 0)) {
                    if (RtnHelper.rtn()) {
                        //提前打印防止退出界面时内存内变量变null
                        printer();
                        MessageUtil.info("退货成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                LogUtil.u(TAG, "按单退货", "现金退货成功");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.returnHomeActivity();
                                    }
                                }, 300);
                            }
                        });
                    } else {
                        LogUtil.u(TAG, "按单退货", "现金退货失败");
                        MessageUtil.error("退货失败");
                    }
                }
                break;
            case PayType.PAYTYPE_ICCARD:
                //IC卡
                doIcCardPay();
                break;
            case PayType.PAYTYPE_PREPAID:
                //储值卡
                cardQuery();//后台支付接口未返回余额，这里先查询余额再退款
                break;
            default:
                //默认按收钱吧处理
                doSqbPay();
                break;
        }
    }

    @Override
    public void showInputPanel(int index) {
        if (!RtnHelper.getTrade().getTradeFlag().equals(TradeHelper.TRADE_FLAG_REFUND)) {
            mView.showInputPanel(index);
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
        if (RtnHelper.changeRtnTradePrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("退货单价修改失败");
        }
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        if (RtnHelper.isForSaleSet(RtnHelper.getProdList().get(index).getProdCode())) {
            //返回为true时不允许退货
            mView.showError("该商品不允许退货");
            return;
        }
        //仅修改临时数据，不修改数据库内数据
        RtnHelper.changeRtnTradeAmount(index, changeAmount);
        //更新列表界面
        mView.updateTradeProd(index);
        //更新底部信息
        updateTradeInfo();
    }

    @Override
    public void checkInputNum(int index, double changeAmount) {
        if ("1".equals(ZgParams.getInputNum())) {
            mView.showInputNumDialog(index);
        } else {
            changeAmount(index, changeAmount);
        }
    }


    @Override
    public void coverAmount(int index, double changeAmount) {
        if (RtnHelper.isForSaleSet(RtnHelper.getProdList().get(index).getProdCode())) {
            //返回为true时不允许退货
            mView.showError("该商品不允许退货");
            return;
        }
        //仅修改临时数据，不修改数据库内数据
        RtnHelper.coverRtnTradeAmount(index, changeAmount);
        //更新列表界面
        mView.updateTradeProd(index);
        //更新底部信息
        updateTradeInfo();
    }

    private void printer() {
        if (ZgParams.isPrintBill()) {
            PrinterHelper.initPrinter(new PrinterHelper.PrintInitCallBack() {
                @Override
                public void onSuccess(SunmiPrinterService service) {
                    getPrintData(service);
                }

                @Override
                public void onFailed() {
                    MessageUtil.showError("打印机出现故障，请检查");
                }
            });
        }
    }

    public void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        final OperateCallback callback = new OperateCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                //实时积分计算成功，保存实时积分用于打印
                if (data != null) {
                    Trade trade = RtnHelper.getRtnTrade();
                    trade.setCurrScore(String.valueOf(data.get("currScore")));
                    trade.setTotalScore(String.valueOf(data.get("totalScore")));
                }
                printLs();
            }

            @Override
            public void onError(String code, String msg) {
                //即使实时积分计算失败也打印（积分为0，不打印实时积分）
                printLs();
                LogUtil.u(TAG, "实时积分计算",
                        String.format("(%s)%s", code, msg));
            }
        };
        TradeHelper.calcVipScore(RtnHelper.getRtnTrade(), RtnHelper.getRtnProdList(),
                RtnHelper.getRtnPay(), callback);
    }

    private void printLs() {
        //生成数据，执行打印命令
        PrinterHelper.print(ZgParams.getPrintBillBak(), PrintFormat.printRtn());
        //如果系统参数允许，并且支付方式是IC卡或者磁卡的情况下，需要打印储值卡存根联
        if (ZgParams.isPrnCounterFoil()) {
            String payTypeCode = RtnHelper.getRtnPay().getPayTypeCode();
            if (payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_PREPAID) || payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_ICCARD)) {
                PrinterHelper.print(1, PrintFormat.printCard(PrintFormat.isRtn));
            }
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        RtnHelper.clearAllData();
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
     * 卡余额
     */
    private final double[] payCardBalance = {0.00};

    /**
     * 查询卡信息
     */
    private void cardQuery() {
        //mView.cardPayWait("卡信息校验中...");
        RestSubscribe.getInstance().payCardInfoRequest(RtnHelper.getPay().getPayCode(), "2", new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(RestBodyMap body) {
                payDataSign[0] = body.getString("dataSign");
                payRequestTime[0] = System.currentTimeMillis();
                requestCardQueryResult();
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                MessageUtil.waitError("储值卡退款失败", null);
            }
        }));
    }

    /**
     * 轮询会员卡信息查询结果
     */
    private void requestCardQueryResult() {
        // 30秒超时
        if (System.currentTimeMillis() - payRequestTime[0] > 30 * 1000) {
            //mView.cardPayTimeout("通讯超时，是否重试？");
            return;
        }

        //延迟500毫秒再查询
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RestSubscribe.getInstance().payCardInfo(payDataSign[0], new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(RestBodyMap body) {
                payCardBalance[0] = body.getDouble("balance");
                doMagCardPay();
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                if (errorCode.endsWith("50")) {//50-查询中
                    //postMessage(PayContract.MSG_CARD_QUERY_RESULT);
                } else {
                    MessageUtil.waitError("储值卡退款失败", null);
                }
            }
        }));
    }


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
                    public void onSuccess(RestBodyMap body) {
                        payDataSign[0] = body.getString("dataSign");
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
            public void onSuccess(RestBodyMap body) {
                double balance = payCardBalance[0] + RtnHelper.getRtnTrade().getTotal();
                if (RtnHelper.pay(PayType.PAYTYPE_PREPAID, RtnHelper.getPay().getPayCode(), balance)) {
                    if (RtnHelper.rtn()) {
                        MessageUtil.waitSuccesss("储值卡退款成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                printer();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.returnHomeActivity();
                                    }
                                }, 200);
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

    /**
     * IC卡支付，直接更新卡内余额
     */
    private void doIcCardPay() {
        MessageUtil.waitBegin("请刷卡...", new MessageUtil.MessageBoxCancelListener() {
            @Override
            public boolean onCancel() {
                SunmiPayHelper.getInstance().cancelWriteCard();
                return true;
            }
        });
        VipCardData updateData = new VipCardData(AidlConstants.CardType.MIFARE);
        //不限制卡号。原卡号：RtnHelper.getPay().getPayCode()
        updateData.setCardCode("");
        //回充余额（此时流水金额还是正数）
        updateData.setMoney(RtnHelper.getRtnTrade().getTotal());
        SunmiPayHelper.getInstance().writeCard(updateData, RtnHelper.getRtnTrade().getLsNo(), new SunmiPayHelper.WriteCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                if (RtnHelper.pay(PayType.PAYTYPE_ICCARD, data.getCardCode(), data.getMoney())) {//保存实际写入的卡号
                    if (RtnHelper.rtn()) {
                        MessageUtil.waitSuccesss("IC卡退款成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                LogUtil.u(TAG, "按单退货", "IC卡退款成功");
                                printer();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.returnHomeActivity();
                                    }
                                }, 200);
                            }
                        });
                    } else {
                        LogUtil.u(TAG, "按单退货", "IC卡退款失败");
                        MessageUtil.waitError("IC卡退款失败", null);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                LogUtil.u(TAG, "按单退货", "IC卡退款失败");
                MessageUtil.waitError(msg, null);
            }
        });
    }

    //endregion

    //region 收钱吧退款
    private void doSqbPay() {
        MessageUtil.waitBegin("退款处理中...", new MessageUtil.MessageBoxCancelListener() {
            @Override
            public boolean onCancel() {
                return false;//支付过程无法取消
            }
        });
        String clientSn = RtnHelper.getTrade().getSqbPayClientSn();
        SqbPayHelper.refundByClientSn(RtnHelper.getRtnTrade(), clientSn, new SqbPayHelper.PayResultCallback() {
            @Override
            public void onResult(boolean isSuccess, String payType, String payCode, final String errMsg) {
                if (isSuccess) {
                    if (RtnHelper.pay(RtnHelper.getPay().getAppPayType(), RtnHelper.getRtnTrade().getTotal(), 0, "")) {
                        if (RtnHelper.rtn()) {
                            MessageUtil.waitUpdate("退款成功", new MessageUtil.MessageBoxOkListener() {
                                @Override
                                public void onOk() {
                                    LogUtil.u(TAG, "按单退货", "收钱吧退款成功");
                                    printer();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mView.returnHomeActivity();
                                        }
                                    }, 200);
                                }
                            });
                        } else {
                            LogUtil.u(TAG, "按单退货", "收钱吧退款失败");
                            MessageUtil.waitError("退款失败", null);
                        }
                    }
                } else {
                    MessageUtil.waitEnd();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MessageUtil.error(errMsg);
                        }
                    }, 200);

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