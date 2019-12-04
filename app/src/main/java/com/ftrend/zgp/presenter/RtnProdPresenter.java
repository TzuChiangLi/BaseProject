package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.RtnProdContract;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
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
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.sunmi.VipCardData;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sunmi.pay.hardware.aidl.AidlConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnProdContract.RtnProdPresenter {
    private RtnProdContract.RtnProdView mView;

    private RtnProdPresenter(RtnProdContract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(RtnProdContract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }

    @Override
    public void showRtnProdDialog() {
        RtnHelper.initSale();
        List<DepProduct> mProdList = SQLite.select().from(DepProduct.class)
                .where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                //3-注销；2-暂停销售
                .and(DepProduct_Table.prodStatus.notIn("2", "3"))
                //季节销售商品
                .and(OperatorGroup.clause(DepProduct_Table.season.eq("0000"))
                        .or(DepProduct_Table.season.like(ShopCartPresenter.makeSeanFilter())))
                .queryList();
        for (DepProduct product : mProdList) {
            product.setSelect(false);
        }
        mView.showRtnProdDialog(mProdList);
    }

    @Override
    public void updateRtnProdList() {
        mView.initProdList(RtnHelper.getRtnProdList());
        updateTradeInfo();
    }

    @Override
    public void delRtnProd(int index) {
        if (RtnHelper.delProduct(index)) {
            mView.delTradeProd(index);
        }
        updateTradeInfo();
    }

    @Override
    public void searchProdByScan(String code, List<DepProduct> depProducts) {
        boolean hasFlag = false;
        for (int i = 0; i < depProducts.size(); i++) {
            if (code.equals(depProducts.get(i).getProdCode()) ||
                    code.equals(depProducts.get(i).getBarCode())) {
                hasFlag = true;
                mView.setScanProdPosition(i);
                addRtnProd(depProducts.get(i));
                break;
            }
            if (i == depProducts.size() && hasFlag == false) {
                mView.showError("无此商品");
            }
        }
    }


    @Override
    public List<DepProduct> searchDepProdList(String key, List<DepProduct> depProdList) {
        if (!depProdList.isEmpty()) {
            return RtnHelper.searchDepProdList(key, depProdList);
        }
        return null;
    }

    @Override
    public boolean addRtnProd(DepProduct depProduct) {
        if (!RtnHelper.addProduct(depProduct)) {
            LogUtil.e("向数据库添加商品失败");
            return false;
        } else {
            updateTradeInfo();
            return true;
        }
    }


    @Override
    public void updateTradeInfo() {
        //获取退货流水金额
        mView.showRtnTotal(RtnHelper.getRtnTotal());
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
                                LogUtil.u("不按单退货", "现金退货成功");
                                mView.finish();
                            }
                        });
                    } else {
                        LogUtil.u("不按单退货", "现金退货失败");
                        MessageUtil.error("退货失败");
                    }
                }
                break;
            case "7":
                //IC卡
                doIcCardPay();
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
        TradeProd prod = RtnHelper.getRtnProdList().get(index);
        if (price > (prod.getTotal() / prod.getAmount())) {
            mView.showError("退货单价不能大于原销售单价");
            return;
        }
        if (price == 0) {
            mView.showError("退货单价应大于0");
            return;
        }
        if (RtnHelper.changeRtnProdPrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("退货单价修改失败");
        }
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        //仅修改临时数据，不修改数据库内数据
        if (RtnHelper.changeRtnProdAmount(index, changeAmount)) {
            //更新列表界面
            mView.updateTradeProd(index);
            //更新底部信息
            updateTradeInfo();
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
        updateData.setCardCode("");//不限制卡号。原卡号：RtnHelper.getPay().getPayCode()
        updateData.setMoney(RtnHelper.getRtnTrade().getTotal());//回充余额（此时流水金额还是正数）
        SunmiPayHelper.getInstance().writeCard(updateData, new SunmiPayHelper.WriteCardCallback() {
            @Override
            public void onSuccess(VipCardData data) {
                if (RtnHelper.pay(PayType.PAYTYPE_ICCARD, data.getCardCode())) {//保存实际写入的卡号
                    if (RtnHelper.rtn()) {
                        MessageUtil.waitSuccesss("储值卡退款成功", new MessageUtil.MessageBoxOkListener() {
                            @Override
                            public void onOk() {
                                LogUtil.u("不按单退货", "储值卡退款成功");
                                mView.finish();
                            }
                        });
                    } else {
                        LogUtil.u("不按单退货", "储值卡退款失败");
                        MessageUtil.waitError("储值卡退款失败", null);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                MessageUtil.waitError(msg, null);
            }
        });
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
                                    LogUtil.u("不按单退货", "收钱吧退款失败");
                                    mView.finish();
                                }
                            });
                        } else {
                            LogUtil.u("不按单退货", "收钱吧退款失败");
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