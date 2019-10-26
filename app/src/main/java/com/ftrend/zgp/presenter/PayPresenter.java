package com.ftrend.zgp.presenter;

import android.os.RemoteException;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.ftrend.zgp.view.PayActivity;
import com.sunmi.peripheral.printer.SunmiPrinterService;

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
    public void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        //TODO 2019年10月18日10:56:56 生成数据，执行打印命令
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
//                    TradeHelper.pay(payType, payCode);
                    TradeHelper.pay(payType, "");
                    //插入交易流水队列
                    TradeHelper.uploadTradeQueue();
                    TradeHelper.clearVip();
                    mView.paySuccess();
                } else {
                    mView.payFail(errMsg);
                }
            }
        });
    }

    @Override
    public boolean paySuccess(String appPayType) {
        return paySuccess(appPayType, 0);
    }

    @Override
    public boolean paySuccess(String appPayType, double value) {
        //付款成功
        //更新交易流水表
        try {
            //完成支付
            if (TradeHelper.pay(appPayType, value)) {
                //插入交易流水队列
                TradeHelper.uploadTradeQueue();
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
    }

}
