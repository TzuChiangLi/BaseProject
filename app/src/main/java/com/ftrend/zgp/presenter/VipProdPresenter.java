package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.api.PayContract;
import com.ftrend.zgp.api.VipProdContract;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Product_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.VipProdHelper;
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
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.sunmi.VipCardData;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */
public class VipProdPresenter implements VipProdContract.VipProdPresenter {
    private static final String TAG = "VipProdPresenter";
    private Handler mHandler = new Handler();
    private VipProdContract.VipProdView mView;
    private boolean isPay = false;

    private VipProdPresenter(VipProdContract.VipProdView mView) {
        this.mView = mView;
        EventBus.getDefault().register(this);
    }

    public static VipProdPresenter createPresenter(VipProdContract.VipProdView mView) {
        return new VipProdPresenter(mView);
    }

    @Override
    public void initVipProd() {
        String prodCode = ZgParams.getVipProd();
        if (!TextUtils.isEmpty(ZgParams.getVipProd())) {
            Product prod = SQLite.select().from(Product.class).where(Product_Table.prodCode.eq(prodCode)).querySingle();
            mView.setVipProd(prod != null ? String.format("%s-%s", prodCode, prod.getProdName()) : "重新设置刷卡商品");
        } else {
            List<Product> mProdList = TradeHelper.loadProduct(null, null, 0, 10);
            if (mProdList.size() > 0) {
                Product prod = mProdList.get(0);
                mView.setVipProd(prod != null ? String.format("%s-%s", prodCode, prod.getProdName()) : "重新设置刷卡商品");
            } else {
                mView.setVipProd("暂未设置刷卡商品");
            }
        }
    }

    @Override
    public void showProdDialog() {
        // TODO: 2020-10-29 增加分页支持
        List<Product> mProdList = TradeHelper.loadProduct(null, null, 0, 100);
        for (Product product : mProdList) {
            product.setSelect(false);
        }
        mView.showProdDialog(mProdList);
    }

    @Override
    public List<Product> searchDepProdList(String key, List<Product> prodList) {
        if (!prodList.isEmpty()) {
            return RtnHelper.searchDepProdList(key, prodList);
        }
        return null;
    }

    @Override
    public void setVipProd(Product prod) {
        ZgParams.saveAppParams(String.format(Locale.CHINA, "vipProd_%s", ZgParams.getCurrentDep().getDepCode()),
                prod.getProdCode());
        ZgParams.loadParams();
        mView.setVipProd(String.format("%s-%s", prod.getProdCode(), prod.getProdName()));
    }

    @Override
    public void pay(double total) {
        if (total == 0) {
            mView.show("请设置消费金额");
            return;
        }
        //判断是否已经设置刷卡商品
        String prodCode = ZgParams.getVipProd();
        if (TextUtils.isEmpty(prodCode)) {
            mView.show("尚未设置刷卡商品");
            return;
        }
        Product prod = SQLite.select().from(Product.class).where(Product_Table.prodCode.eq(prodCode))
                .querySingle();
        if (prod == null) {
            mView.show("请重新选择刷卡商品");
            return;
        }
        //商品价格修改
        prod.setPrice(total);
        //创建Trade
        VipProdHelper.initSale();
        //添加商品
        VipProdHelper.initProdList(prod);
        //刷卡
        readCard(true);
    }

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

    @Override
    public void readCard(boolean isPay) {
        this.isPay = isPay;
        //参数初始化
        payCardCode[0] = "";
        payCardType[0] = "";
        payDataSign[0] = "";
        payCardBalance[0] = 0.00;
        payRequestTime[0] = 0;
        if (!SunmiPayHelper.getInstance().serviceAvailable()) {
            MessageUtil.showError("刷卡服务不可用！");
            return;
        }
        LogUtil.u(TAG, "会员卡消费结算", "刷卡支付：读卡");
        mView.cardPayWait("请刷卡...");
        SunmiPayHelper.getInstance().readCard(new SunmiPayHelper.ReadCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                payCardCode[0] = data.getCardCode();
                if (data.getCardType() == AidlConstants.CardType.MIFARE) {
                    payCardType[0] = "1";
                    cardData.copy(data);//记录卡信息，用于IC卡支付
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
     */
    private void requestCardQueryResult() {
        // 30秒超时
        if (System.currentTimeMillis() - payRequestTime[0] > 30 * 1000) {
            //在线查询会员信息
            RestSubscribe.getInstance().queryVipInfo(payCardCode[0], payCardType[0], new RestCallback(new RestResultHandler() {
                @Override
                public void onSuccess(RestBodyMap body) {
                    mView.cardPayTimeout("通讯超时，是否重试？");
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    mView.cardPayFail(errorCode, errorMsg);
                }
            }));
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
                boolean needPass;
                payCardCode[0] = body.getString("cardCode");
                if ("1".equals(payCardType[0])) {//IC卡，以卡内信息为准
                    payCardBalance[0] = cardData.getMoney();
                    needPass = !TextUtils.isEmpty(cardData.getVipPwdDecrypted());
                } else {
                    payCardBalance[0] = body.getDouble("balance");
                    needPass = body.getBool("needPass");
                }
                if (isPay) {
                    if (payCardBalance[0] < VipProdHelper.getTrade().getTotal()) {
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
                    //仅读卡
                    VipInfo vipInfo = new VipInfo();
                    vipInfo.setCardCode(body.getString("cardCode"));
                    vipInfo.setBalance(payCardBalance[0]);
                    vipInfo.setCardType("1".equals(payCardType[0]) ? "IC卡" : "磁卡");
                    vipInfo.setVipName(body.getString("vipName"));
                    vipInfo.setVipCode(body.getString("vipCode"));
                    vipInfo.setVipScore(body.getString("score"));
                    mView.setVipInfo(vipInfo);
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
        mView.cardPayWait("请再次刷卡...");
        final double total = VipProdHelper.getTrade().getTotal();
        VipCardData updateData = new VipCardData(cardData);
        updateData.setMoney(total * -1);//扣减余额
        SunmiPayHelper.getInstance().writeCard(updateData, VipProdHelper.getTrade().getLsNo(), new SunmiPayHelper.WriteCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                paySuccess(PayType.PAYTYPE_ICCARD, total, data.getCardCode(), data.getMoney());
                mView.cardPaySuccess("支付成功！");
            }

            @Override
            public void onError(String msg) {
                mView.cardPayFail(msg);
            }
        });
    }

    /**
     * 磁卡支付，调用后台服务完成支付
     */
    private void doMagCardPay() {
        mView.cardPayWait("卡支付处理中...");
        Trade trade = VipProdHelper.getTrade();
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
                double total = VipProdHelper.getTrade().getTotal();
                double balance = payCardBalance[0] - total;
                paySuccess(PayType.PAYTYPE_PREPAID, total, payCardCode[0], balance);
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
    public boolean cardPayCancel() {
        if (SunmiPayHelper.getInstance().isReading()) {
            //取消读卡
            SunmiPayHelper.getInstance().cancelReadCard();
            return true;
        } else if (SunmiPayHelper.getInstance().isWriting()) {
            //取消写卡
            SunmiPayHelper.getInstance().isWriting();
            return true;
        } else {
            //支付过程暂不支持取消
            return false;
        }
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
    public boolean paySuccess(String appPayType, double value, String payCode, double balance) {
        //付款成功
        try {
            //完成支付
            if (VipProdHelper.pay(appPayType, payCode, balance)) {
                if (!ZgParams.isPrintBill()) {
                    return true;
                }
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
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
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
                    Trade trade = VipProdHelper.getTrade();
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
        TradeHelper.calcVipScore(VipProdHelper.getTrade(), VipProdHelper.getProdList(),
                VipProdHelper.getPay(), callback);
    }

    private void printLs() {
        //生成数据，执行打印命令
        PrinterHelper.print(ZgParams.getPrintBillBak(), PrintFormat.printVipProd());
        //如果系统参数允许，并且支付方式是IC卡或者磁卡的情况下，需要打印储值卡存根联
        if (ZgParams.isPrnCounterFoil()) {
            String payTypeCode = VipProdHelper.getPay().getPayTypeCode();
            if (payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_ICCARD) || payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_PREPAID)) {
                PrinterHelper.print(1, PrintFormat.printCard(PrintFormat.isVipProd));
            }
        }
    }

    /**
     * 延迟发送事件消息
     *
     * @param msgId
     */
    private void postMessage(final int msgId) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Event.sendEvent(Event.TARGET_VIP_PROD, msgId);
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessage(Event event) {
        if (event.getTarget() != Event.TARGET_VIP_PROD) {
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


    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        EventBus.getDefault().unregister(this);
    }
}

//        if (ZgParams.isIsOnline()) {
//            //在线查询会员信息
//            RestSubscribe.getInstance().queryVipInfo(cardCode, cardType, new RestCallback(new RestResultHandler() {
//                @Override
//                public void onSuccess(RestBodyMap body) {
//                    LogUtil.d("----queryVipInfo:" + body.keySet());
//                    if (body != null) {
//                        if (!"1".equals(cardType)) {
//                            //磁卡需要显示接口返回的余额
//                            vipInfo.setBalance(Double.parseDouble(body.getString("balance")));
//                        }
//                        vipInfo.setVipName(body.getString("vipName"));
//                        vipInfo.setVipCode(body.getString("vipCode"));
//                        vipInfo.setVipScore(body.getString("vipScore"));
//                    } else {
//                        MessageUtil.showError("查询会员信息失败：返回结果为空");
//                        return;
//                    }
//                    mView.setVipInfo(vipInfo);
//                }
//
//                @Override
//                public void onFailed(String errorCode, String errorMsg) {
//                    MessageUtil.error(errorCode, errorMsg);
//                    mView.setVipInfo(vipInfo);
//                }
//            }));
//        } else {
//            mView.show("当前处于离线状态无法获取更多信息");
//
//        }