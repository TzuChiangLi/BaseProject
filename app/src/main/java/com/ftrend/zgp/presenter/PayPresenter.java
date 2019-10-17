package com.ftrend.zgp.presenter;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.TradeHelper;
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
        payWays.add(new Menu.MenuList(R.drawable.shouqianba, "收钱吧"));
        payWays.add(new Menu.MenuList(R.drawable.card, "储值卡"));
        payWays.add(new Menu.MenuList(R.drawable.money, "现金"));
        mView.showPayway(payWays);
        mView.showTradeInfo(TradeHelper.getTradeTotal());
    }

    @Override
    public void payByShouQian(String value) {
        mView.waitPayResult();
        SqbPayHelper.pay(value, new SqbPayHelper.PayResultCallback() {
            @Override
            public void onResult(boolean isDone, boolean isSuccess, String payType, String payCode, String errMsg) {
                if (isDone && isSuccess) {
                    TradeHelper.pay(payType, payCode);
                    //插入交易流水队列
                    TradeHelper.uploadTradeQueue();
                    TradeHelper.clearVip();
                    mView.paySuccess();
                } else if (isDone && !isSuccess) {
                    mView.payFail("支付失败：" + errMsg);
                } else {
                    // TODO: 2019/10/17 中间状态，开始轮询支付订单状态
                }
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
