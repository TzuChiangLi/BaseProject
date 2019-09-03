package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.http.RestResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListPresenter implements Contract.ShopListPresenter, HttpCallBack {
    private Contract.ShopListView mView;

    private ShopListPresenter(Contract.ShopListView mView) {
        this.mView = mView;
    }

    public static ShopListPresenter createPresenter(Contract.ShopListView mView) {
        return new ShopListPresenter(mView);
    }


    @Override
    public void initShopList(String lsNo) {
        mView.showTradeProd(SQLite.select().from(TradeProd.class).where(TradeProd_Table.lsNo.eq(lsNo)).queryList());
    }

    @Override
    public void setTradeStatus(String lsNo, int status) {
        switch (status) {
            case 0:
                //未结
                break;
            case 1:
                //挂起
                break;
            case 2:
                //已结
                break;
            case 3:
                //取消
                SQLite.update(Trade.class)
                        .set(Trade_Table.status.eq(String.valueOf(status)))
                        .where(Trade_Table.lsNo.is(lsNo))
                        .async()
                        .execute(); // non-UI blocking

                SQLite.delete(TradeProd.class)
                        .where(TradeProd_Table.lsNo.eq(lsNo))
                        .async()
                        .execute();
                mView.returnHomeActivity();
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


}
