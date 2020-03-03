package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.PayContract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.event.Event;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付P层
 *
 * @author liziqiang@ftrend.cn
 */
public class PayPresenter implements PayContract.Presenter {
    private static final String TAG = "PayPresenter";
    private PayContract.View mView;
    private boolean isSale;

    private PayPresenter(PayContract.View mView) {
        this.mView = mView;
        EventBus.getDefault().register(this);
    }

    public static PayPresenter createPresenter(PayContract.View mView) {
        return new PayPresenter(mView);
    }


    @Override
    public void setTradeType(boolean isSale) {
        this.isSale = isSale;
    }

    private void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        final OperateCallback callback = new OperateCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                LogUtil.d("----data.keyset:"+data.keySet());
                // 实时积分计算成功，保存实时积分用于打印
                if (data != null) {
                    Trade trade = isSale ? TradeHelper.getTrade() : RtnHelper.getRtnTrade();
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
        // 计算实时积分
        if (isSale) {
            TradeHelper.calcVipScore(callback);
        } else {
            TradeHelper.calcVipScore(RtnHelper.getRtnTrade(), RtnHelper.getRtnProdList(),
                    RtnHelper.getRtnPay(), callback);
        }
    }

    private void printLs() {
        //生成数据，执行打印命令
        PrinterHelper.print(ZgParams.getPrintBillBak(), isSale ? PrintFormat.printSale() : PrintFormat.printRtn());
        //如果系统参数允许，并且支付方式是IC卡或者磁卡的情况下，需要打印储值卡存根联
        if (ZgParams.isPrnCounterFoil()) {
            String payTypeCode = isSale ? TradeHelper.getPay().getPayTypeCode() : RtnHelper.getRtnPay().getPayTypeCode();
            if (payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_ICCARD) || payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_PREPAID)) {
                PrinterHelper.print(1, PrintFormat.printCard(isSale ? PrintFormat.isSale : PrintFormat.isRtn));
            }
        }
    }

    @Override
    public void initPayWay() {
        List<Menu.MenuList> payWays = new ArrayList<>();
        String[] payType = ZgParams.getPayType();
        if ("1".equals(String.valueOf(payType[0]))) {
            payWays.add(new Menu.MenuList(R.drawable.shouqianba, "收钱吧"));
        }
        if ("1".equals(String.valueOf(payType[1]))) {
            payWays.add(new Menu.MenuList(R.drawable.card, "储值卡"));
        }
        if ("1".equals(String.valueOf(payType[2]))) {
            payWays.add(new Menu.MenuList(R.drawable.money, "现金"));
        }
        mView.showPayway(payWays);
        mView.showTradeInfo(isSale ? TradeHelper.getTradeTotal() : RtnHelper.getRtnTotal());
    }

    /**
     * @param sn 收钱吧订单号
     */
    @Override
    public void payByShouQian(final String sn) {
        LogUtil.u(TAG, "结算", "收钱吧支付");
        mView.waitPayResult();
        //网络不可用等情况，收钱吧SDK返回比较快，可能导致错误消息比等待提示先出现，界面一直显示等待提示。
        // 这里延迟100毫秒，确保先显示等待提示再调用SDK方法。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSale) {
                    SqbPayHelper.pay(TradeHelper.getTrade(), sn, new SqbPayHelper.PayResultCallback() {
                        @Override
                        public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                            if (isSuccess) {
                                // TODO: 2019/10/26 微信支付账号长度超过后台数据库对应字段长度，暂时先不记录支付账号
                                paySuccess(payType, TradeHelper.getTrade().getTotal(), "");
                                mView.paySuccess();
                                LogUtil.u(TAG, "结算", "结算成功");
                            } else {
                                mView.payFail(errMsg);
                            }
                        }
                    });
                } else {
                    SqbPayHelper.refundBySn(RtnHelper.getRtnTrade(), sn, new SqbPayHelper.PayResultCallback() {
                        @Override
                        public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                            if (isSuccess) {
                                paySuccess(payType, RtnHelper.getRtnTrade().getTotal(), "");
                                mView.paySuccess();
                                LogUtil.u(TAG, "结算", "结算成功");
                            } else {
                                mView.payFail(errMsg);
                            }
                        }
                    });
                }
            }
        }, 100);

    }

    @Override
    public boolean paySuccess(String appPayType, double value, String payCode) {
        return paySuccess(appPayType, value, payCode, 0);
    }

    @Override
    public boolean paySuccess(String appPayType, double value, String payCode, double balance) {
        //付款成功
        try {
            if (isSale) {
                //计算找零
                double change = 0;
                if (appPayType.equals(PayType.PAYTYPE_CASH)) {
                    change = value - TradeHelper.getTradeTotal();
                }
                //完成支付
                if (!TradeHelper.pay(appPayType, value, change, payCode, balance)) {
                    return false;
                }
                TradeHelper.clearVip();
            } else {
                // TODO: 2020/1/9 以下两个事务合并成一个
                if (!RtnHelper.pay(appPayType, value, 0, payCode, balance)) {
                    return false;
                }
                if (!RtnHelper.rtn()) {
                    return false;
                }
            }
            if (!ZgParams.isPrintBill()) {
                return true;
            }
            PrinterHelper.initPrinter(new PrinterHelper.PrintInitCallBack() {
                @Override
                public void onSuccess(SunmiPrinterService service) {
                    LogUtil.u(TAG, "打印机初始化", "打印机初始化成功");
                    getPrintData(service);
                }

                @Override
                public void onFailed() {
                    LogUtil.u(TAG, "打印机初始化", "打印机初始化失败");
                    MessageUtil.showError("打印机出现故障，请检查");
                }
            });
            return true;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }


    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        EventBus.getDefault().unregister(this);
    }

    //region 储值卡支付

    // 储值卡号
    private final String[] payCardCode = {""};
    // IC卡信息：用于IC卡支付
    private final VipCardData cardData = new VipCardData(AidlConstants.CardType.MIFARE);
    // 储值卡类型：1-IC卡，2-磁卡
    private final String[] payCardType = {""};
    // 请求数据标识，用于轮询请求结果
    private final String[] payDataSign = {""};
    // 卡余额
    private final double[] payCardBalance = {0.00};
    // 请求发起时间
    private final long[] payRequestTime = {0};

    /**
     * ERR:A03---cardPay
     */
    @Override
    public void cardPay() {
        //参数初始化
        payCardCode[0] = "";
        payCardType[0] = "";
        payDataSign[0] = "";
        payCardBalance[0] = 0.00;
        payRequestTime[0] = 0;
        if (!SunmiPayHelper.getInstance().serviceAvailable()) {
            MessageUtil.showError("刷卡服务不可用！");
            //手工输入卡号
            postMessage(PayContract.MSG_CARD_CODE_INPUT);
            return;
        }
        LogUtil.u(TAG, "结算", "刷卡支付");
        mView.cardPayWait("请刷卡...");
        SunmiPayHelper.getInstance().readCard(new SunmiPayHelper.ReadCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                payCardCode[0] = data.getCardCode();
                if (data.getCardType() == AidlConstants.CardType.MIFARE) {
                    payCardType[0] = "1";
                    cardData.copy(data);//记录卡信息，用于IC卡支付
                    if (!isSale) {
                        doIcCardPay();
                        return;
                    }
                } else if (data.getCardType() == AidlConstants.CardType.MAGNETIC) {
                    payCardType[0] = "2";
                } else {
                    mView.cardPayFail("无效卡");
                    return;
                }
                //读卡成功，查询卡信息
                postMessage(PayContract.MSG_CARD_QUERY_REQUEST);
            }

            @Override
            public void onError(String msg) {
                mView.cardPayFail(msg);
            }
        });
    }

    @Override
    public void cardPay(String cardCode) {
        payCardCode[0] = cardCode;
        payCardType[0] = "2";
        postMessage(PayContract.MSG_CARD_QUERY_REQUEST);
    }

    /**
     * 查询卡信息
     * ERR:A04---cardQuery
     */
    private void cardQuery() {
        mView.cardPayWait("卡信息校验中...");
        RestSubscribe.getInstance().payCardInfoRequest(payCardCode[0], payCardType[0], new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(RestBodyMap body) {
                payDataSign[0] = body.getString("dataSign");
                payRequestTime[0] = System.currentTimeMillis();
                postMessage(PayContract.MSG_CARD_QUERY_RESULT);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                mView.cardPayFail(errorCode, errorMsg);
            }
        }));
    }

    /**
     * 轮询会员卡信息查询结果
     * ERR:A05---requestCardQueryResult
     */
    private void requestCardQueryResult() {
        // 30秒超时
        if (System.currentTimeMillis() - payRequestTime[0] > 30 * 1000) {
            mView.cardPayTimeout("通讯超时，是否重试？");
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
                if (isSale) {
                    payCardCode[0] = body.getString("cardCode");
                    boolean needPass;
                    if ("1".equals(payCardType[0])) {//IC卡，以卡内信息为准
                        payCardBalance[0] = cardData.getMoney();
                        needPass = !TextUtils.isEmpty(cardData.getVipPwdDecrypted());
                    } else {
                        payCardBalance[0] = body.getDouble("balance");
                        needPass = body.getBool("needPass");
                    }

                    if (payCardBalance[0] < TradeHelper.getTradeTotal()) {
                        mView.cardPayFail("卡余额不足！");
                        return;
                    }
                    if (needPass) {
                        //需要支付密码
                        postMessage(PayContract.MSG_CARD_PASSWORD);
                    } else {
                        //无需支付密码
                        postMessage(PayContract.MSG_CARD_PAY_REQUEST);
                    }
                } else {
                    payCardCode[0] = body.getString("cardCode");
                    if ("1".equals(payCardType[0])) {//IC卡，以卡内信息为准
                        payCardBalance[0] = cardData.getMoney();
                    } else {
                        payCardBalance[0] = body.getDouble("balance");
                    }
                    //退款无需支付密码
                    postMessage(PayContract.MSG_CARD_PAY_REQUEST);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                if (errorCode.endsWith("50")) {//50-查询中
                    postMessage(PayContract.MSG_CARD_QUERY_RESULT);
                } else {
                    mView.cardPayFail(errorCode, errorMsg);
                }
            }
        }));
    }

    /**
     * ERR:A06---cardPayPass
     *
     * @param pwd 密码
     */
    @Override
    public void cardPayPass(String pwd) {
        if ("1".equals(payCardType[0])) {
            //IC卡，以卡内信息为准
            if (cardData.getVipPwdDecrypted().equals(pwd)) {
                postMessage(PayContract.MSG_CARD_PAY_REQUEST);
            } else {
                MessageUtil.showError("支付密码校验失败，请重新输入！");
                mView.cardPayPassword();
            }
        } else {
            mView.cardPayWait("正在校验支付密码...");
            RestSubscribe.getInstance().vipCardPwdValidate(payCardCode[0], pwd,
                    new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(RestBodyMap body) {
                            postMessage(PayContract.MSG_CARD_PAY_REQUEST);
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            MessageUtil.showError("支付密码校验失败，请重新输入！");
                            mView.cardPayPassword();
                        }
                    }));
        }
    }

    /**
     * IC卡支付，直接更新卡内余额
     */
    private void doIcCardPay() {
        if (isSale) {
            mView.cardPayWait("请再次刷卡...");
            VipCardData updateData = new VipCardData(cardData);
            updateData.setMoney(isSale ? TradeHelper.getTrade().getTotal() * -1 : RtnHelper.getRtnTrade().getTotal() * -1);//扣减余额
            String lsNo = isSale ? TradeHelper.getTrade().getLsNo() : RtnHelper.getRtnTrade().getLsNo();
            SunmiPayHelper.getInstance().writeCard(updateData, lsNo, new SunmiPayHelper.WriteCardCallback() {
                @Override
                public void onSuccess(VipCardData data) {
                    paySuccess(PayType.PAYTYPE_ICCARD, TradeHelper.getTrade().getTotal(), data.getCardCode(), data.getMoney());
                    mView.cardPaySuccess("支付成功！");
                }

                @Override
                public void onError(String msg) {
                    mView.cardPayFail(msg);
                }
            });
        } else {
            VipCardData updateData = new VipCardData(AidlConstants.CardType.MIFARE);
            //不限制卡号。原卡号：RtnHelper.getPay().getPayCode()
            updateData.setCardCode("");
            //回充余额（此时流水金额还是正数）
            updateData.setMoney(RtnHelper.getRtnTrade().getTotal());
            SunmiPayHelper.getInstance().writeCard(updateData, RtnHelper.getRtnTrade().getLsNo(), new SunmiPayHelper.WriteCardCallback() {
                @Override
                public void onSuccess(VipCardData data) {
                    if (paySuccess(PayType.PAYTYPE_ICCARD, RtnHelper.getRtnTrade().getTotal(), data.getCardCode(), data.getMoney())) {
                        mView.cardPaySuccess("IC卡退款成功");
                    } else {
                        LogUtil.u(TAG, "不按单退货", "IC卡退款失败");
                        MessageUtil.waitError("IC卡退款失败", null);
                    }
                }

                @Override
                public void onError(String msg) {
                    LogUtil.u(TAG, "不按单退货", "IC卡退款失败");
                    MessageUtil.waitError(msg, null);
                }
            });
        }
    }

    /**
     * 磁卡支付，调用后台服务完成支付
     */
    private void doMagCardPay() {
        //ERR:A08---doMagCardPay
        mView.cardPayWait("卡支付处理中...");
        Trade trade = isSale ? TradeHelper.getTrade() : RtnHelper.getRtnTrade();
        RestSubscribe.getInstance().payCardRequest(
                ZgParams.getPosCode(),
                trade.getLsNo(),
                CommonUtil.dateToYyyyMmDd(new Date()),
                ZgParams.getCurrentUser().getUserCode(),
                payCardCode[0],
                trade.getTotal(),
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(RestBodyMap body) {
                        payDataSign[0] = body.getString("dataSign");
                        payRequestTime[0] = System.currentTimeMillis();
                        postMessage(PayContract.MSG_CARD_PAY_RESULT);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {
                        mView.cardPayFail(errorCode, errorMsg);
                    }
                }));
    }

    /**
     * 轮询支付结果
     */
    private void requestCardPayResult() {
        //ERR:A09---requestCardPayResult
        // 30秒超时
        if (System.currentTimeMillis() - payRequestTime[0] > 30 * 1000) {
            mView.cardPayTimeout("通讯超时，是否重试？");
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
                if (isSale) {
                    double balance = payCardBalance[0] - TradeHelper.getTradeTotal();
                    paySuccess(PayType.PAYTYPE_PREPAID, TradeHelper.getTradeTotal(), payCardCode[0], balance);
                } else {
                    double balance = payCardBalance[0] + RtnHelper.getRtnTotal();
                    paySuccess(PayType.PAYTYPE_PREPAID, RtnHelper.getRtnTotal(), payCardCode[0], balance);
                }
                mView.cardPaySuccess("支付成功！");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                if (errorCode.endsWith("70")) {//70-处理中
                    postMessage(PayContract.MSG_CARD_PAY_RESULT);
                } else {
                    mView.cardPayFail(errorCode, errorMsg);
                }
            }
        }));
    }

    @Override
    public void cardPayRetry() {
        if (payCardBalance[0] > 0) {
            //余额>0，说明已经查过卡信息
            postMessage(PayContract.MSG_CARD_PAY_REQUEST);
        } else {
            postMessage(PayContract.MSG_CARD_QUERY_REQUEST);
        }
    }

    @Override
    public boolean cardPayCancel() {
        if (SunmiPayHelper.getInstance().isReading()) {
            //取消读卡
            SunmiPayHelper.getInstance().cancelReadCard();
            return true;
        } else if (SunmiPayHelper.getInstance().isWriting()) {
            //取消写卡
            SunmiPayHelper.getInstance().cancelWriteCard();
            return true;
        } else {
            //支付过程暂不支持取消
            return false;
        }
    }

    private Handler mHandler = new Handler();

    /**
     * 延迟发送事件消息
     *
     * @param msgId 事件代码
     */
    private void postMessage(final int msgId) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Event.sendEvent(Event.TARGET_PAY, msgId);
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessage(Event event) {
        if (event.getTarget() != Event.TARGET_PAY) {
            return;
        }
        switch (event.getType()) {
            case PayContract.MSG_CARD_QUERY_REQUEST:
                cardQuery();
                break;
            case PayContract.MSG_CARD_QUERY_RESULT:
                requestCardQueryResult();
                break;
            case PayContract.MSG_CARD_PASSWORD:
                mView.cardPayPassword();
                break;
            case PayContract.MSG_CARD_PAY_REQUEST:
                if ("1".equals(payCardType[0])) {//1-IC卡
                    doIcCardPay();
                } else if ("2".equals(payCardType[0])) {//2-磁卡
                    doMagCardPay();
                } else {
                    mView.cardPayFail("支付失败");
                }
                break;
            case PayContract.MSG_CARD_PAY_RESULT:
                requestCardPayResult();
                break;
            default:
                break;
        }
    }

    //endregion

}
