package com.ftrend.zgp.presenter;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.pay.PayCallBack;
import com.ftrend.zgp.utils.pay.SqbPayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付P层
 *
 * @author liziqiang@ftrend.cn
 */
public class PayPresenter implements Contract.PayPresenter {
    private Contract.PayView mView;

    private PayPresenter(Contract.PayView mView) {
        this.mView = mView;
    }

    public static PayPresenter createPresenter(Contract.PayView mView) {
        return new PayPresenter(mView);
    }


    @Override
    public void initPayWay() {
        List<Menu.MenuList> payWays = new ArrayList<>();
        payWays.add(new Menu.MenuList(R.drawable.shouqian, "收钱吧"));
        payWays.add(new Menu.MenuList(R.drawable.card, "储值卡"));
        payWays.add(new Menu.MenuList(R.drawable.money, "现金"));
        mView.showPayway(payWays);
        mView.showTradeInfo(TradeHelper.getTradeTotal());
    }

    @Override
    public void payByShouQian(String value) {
        mView.waitPayResult();
        SqbPayHelper.pay(value, new PayCallBack() {
            @Override
            public void isDone() {
                LogUtil.d("----isDone");
            }

            @Override
            public void isSuccesss() {
                LogUtil.d("----isSuccess");
                //TODO 2019年10月16日16:46:45 pay方法的参数.
                mView.paySuccess();
                //数据库操作异常：Attempt to invoke virtual method 'java.lang.String com.ftrend.zgp.model.TradePay.getLsNo()' on a null object reference
//                if (TradeHelper.pay(TradeHelper.APP_PAY_TYPE_SHOUQIANBA, TradeHelper.getTrade().getTotal(), 0, "42")) {
//                    mView.paySuccess();
//                } else {
//                    mView.showError("数据库写入错误");
//                }
            }

            @Override
            public void isFailed() {
                LogUtil.d("----isFailed");
            }
        });
    }

    @Override
    public boolean paySuccess(String appPayType) {
        //付款成功
        //更新交易流水表
        try {
            //完成支付
            if (TradeHelper.pay(appPayType)) {
                //插入交易流水队列
                TradeHelper.uploadTradeQueue();
                TradeHelper.clearVip();
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
    }

}
