package com.ftrend.zgp.presenter;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 支付P层
 *
 * @author LZQ
 */
public class PayPresenter implements Contract.PayPresenter, HttpCallBack {
    private Contract.PayView mView;

    private PayPresenter(Contract.PayView mView) {
        this.mView = mView;
    }

    public static PayPresenter createPresenter(Contract.PayView mView) {
        return new PayPresenter(mView);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body) {

    }

    @Override
    public void onFailed(String errorCode, String errorMessage) {

    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void initPayWay() {
        List<Menu.MenuList> payWays = new ArrayList<>();
        payWays.add(new Menu.MenuList(R.drawable.alipay, "支付宝"));
        payWays.add(new Menu.MenuList(R.drawable.wechat, "微信支付"));
        payWays.add(new Menu.MenuList(R.drawable.card, "储值卡"));
        payWays.add(new Menu.MenuList(R.drawable.money, "现金"));
        mView.showPayway(payWays);
    }

    @Override
    public void paySuccess(String lsNo, float amount, int payWay) {
        //付款成功
        //更新交易流水表
        SQLite.update(Trade.class)
                .set(Trade_Table.status.eq("2"), Trade_Table.tradeTime.eq(new Date()))
                .where(Trade_Table.lsNo.is(lsNo))
                .async()
                .execute(); // non-UI blocking

        //更新交易支付表
        TradePay tradePay = new TradePay();
        tradePay.setLsNo(lsNo);
        tradePay.setPayTypeCode(String.valueOf(payWay));
        tradePay.setAmount(amount);
        tradePay.setPayTime(LogUtil.getDateTime());
//        tradePay.setPayCode();
        tradePay.insert();

        //流水号写入上传队列
        TradeUploadQueue queue = new TradeUploadQueue(ZgParams.getCurrentDep().getDepCode(), lsNo);
        queue.insert();
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
