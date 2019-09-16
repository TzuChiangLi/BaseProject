package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.utils.TradeHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListPresenter implements Contract.ShopListPresenter {
    private Contract.ShopListView mView;

    private ShopListPresenter(Contract.ShopListView mView) {
        this.mView = mView;
    }

    public static ShopListPresenter createPresenter(Contract.ShopListView mView) {
        return new ShopListPresenter(mView);
    }


    @Override
    public void initShopList(String lsNo) {
        List<TradeProd> tradeProdList = SQLite.select().from(TradeProd.class).where(TradeProd_Table.lsNo.eq(lsNo)).queryList();
        for (TradeProd t : tradeProdList) {
            t.setSelect(false);
        }
        mView.showTradeProd(tradeProdList);
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
                TradeHelper.cancelTrade();
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


}
