package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.PayContract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.view.PayActivity;
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
    private PayContract.View mView;

    private PayPresenter(PayContract.View mView) {
        this.mView = mView;
        EventBus.getDefault().register(this);
    }

    public static PayPresenter createPresenter(PayContract.View mView) {
        return new PayPresenter(mView);
    }


    @Override
    public void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        //生成数据，执行打印命令
        PrinterHelper.print(PrintFormat.printFormat());
    }

    @Override
    public void initPayWay() {
        List<Menu.MenuList> payWays = new ArrayList<>();
        payWays.add(new Menu.MenuList(R.drawable.shouqianba, "收钱吧"));
        payWays.add(new Menu.MenuList(R.drawable.card, "储值卡"));
        payWays.add(new Menu.MenuList(R.drawable.money, "现金"));
        mView.showPayway(payWays);
        mView.showTradeInfo(TradeHelper.getTradeTotal());
    }

    @Override
    public void payByShouQian(String value) {
        mView.waitPayResult();
        SqbPayHelper.pay(TradeHelper.getTrade(), value, new SqbPayHelper.PayResultCallback() {
            @Override
            public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                if (isSuccess) {
                    // TODO: 2019/10/26 微信支付账号长度超过后台数据库对应字段长度，暂时先不记录支付账号
                    paySuccess(payType, TradeHelper.getTrade().getTotal(), "");
                    mView.paySuccess();
                } else {
                    mView.payFail(errMsg);
                }
            }
        });
    }

    @Override
    public boolean paySuccess(String appPayType, double value, String payCode) {
        //付款成功
        //更新交易流水表
        try {
            //计算找零
            double change = 0;
            if (appPayType.equals(PayType.PAYTYPE_CASH)) {
                change = value - TradeHelper.getTradeTotal();
            }
            //完成支付
            if (TradeHelper.pay(appPayType, value, change, payCode)) {
                TradeHelper.clearVip();
                PrinterHelper.initPrinter(PayActivity.mContext, new PrinterHelper.PrintInitCallBack() {
                    @Override
                    public void onSuccess(SunmiPrinterService service) throws RemoteException {
                        getPrintData(service);
                    }

                    @Override
                    public void onFailed() {
                        MessageUtil.showError("打印机出现故障，请检查");
                    }
                });
                return true;
            } else {
                return false;
            }
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

    /**
     * 储值卡号
     */
    private final String[] payCardCode = {""};
    /**
     * 储值卡类型：1-IC卡，2-磁卡
     */
    private final String[] payCardType = {""};
    /**
     * 请求数据标识，用于轮询请求结果
     */
    private final String[] payDataSign = {""};
    /**
     * 卡余额
     */
    private final double[] payCardBalance = {0.00};
    /**
     * 请求发起时间
     */
    private final long[] payRequestTime = {0};

    @Override
    public void cardPay() {
        //参数初始化
        payCardType[0] = "";
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

        mView.cardPayWait("请刷卡...");
        SunmiPayHelper.getInstance().readCard(new SunmiPayHelper.ReadCardCallback() {
            @Override
            public void onSuccess(String cardNo, AidlConstants.CardType cardType) {
                payCardCode[0] = cardNo;
                if (cardType == AidlConstants.CardType.MIFARE) {
                    payCardType[0] = "1";
                } else if (cardType == AidlConstants.CardType.MAGNETIC) {
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
     */
    private void cardQuery() {
        mView.cardPayWait("卡信息校验中...");
        RestSubscribe.getInstance().payCardInfoRequest(payCardCode[0], payCardType[0], new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                payDataSign[0] = body.get("dataSign").toString();
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
            public void onSuccess(Map<String, Object> body) {
                payCardCode[0] = body.get("cardCode").toString();
                payCardBalance[0] = Double.parseDouble(body.get("balance").toString());
                if (payCardBalance[0] < TradeHelper.getTradeTotal()) {
                    mView.cardPayFail("卡余额不足！");
                    return;
                }
                boolean needPass = Boolean.parseBoolean(body.get("needPass").toString());
                if (needPass) {
                    //需要支付密码
                    postMessage(PayContract.MSG_CARD_PASSWORD);
                } else {
                    //无需支付密码
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

    @Override
    public void cardPayPass(String pwd) {
        mView.cardPayWait("正在校验支付密码...");
        RestSubscribe.getInstance().vipCardPwdValidate(payCardCode[0], pwd, new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
//                cardPay();
                postMessage(PayContract.MSG_CARD_PAY_REQUEST);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                MessageUtil.showError("支付密码校验失败，请重新输入！");
                mView.cardPayPassword();
            }
        }));
    }

    /**
     * IC卡支付，直接更新卡内余额
     */
    private void doIcCardPay() {
        // TODO: 2019/11/12 实现IC卡支付，直接写卡
    }

    /**
     * 磁卡支付，调用后台服务完成支付
     */
    private void doMagCardPay() {
        mView.cardPayWait("卡支付处理中...");
        Trade trade = TradeHelper.getTrade();
        RestSubscribe.getInstance().payCardRequest(
                ZgParams.getPosCode(),
                trade.getLsNo(),
                CommonUtil.dateToYyyyMmDd(new Date()),
                ZgParams.getCurrentUser().getUserCode(),
                payCardCode[0],
                trade.getTotal(),
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(Map<String, Object> body) {
                        payDataSign[0] = body.get("dataSign").toString();
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
            public void onSuccess(Map<String, Object> body) {
                paySuccess(PayType.PAYTYPE_PREPAID, TradeHelper.getTradeTotal(), payCardCode[0]);
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
        if (TextUtils.isEmpty(payCardCode[0])) {
            // 取消刷卡
            SunmiPayHelper.getInstance().cancelReadCard();
            return true;
        } else {
            //支付过程暂不支持取消
            return false;
        }
    }

    /**
     * 延迟发送事件消息
     *
     * @param msgId
     */
    private void postMessage(final int msgId) {
        new Handler().postDelayed(new Runnable() {
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
                if (payCardType[0].equals("1")) {//1-IC卡
                    doIcCardPay();
                } else {
                    doMagCardPay();
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
