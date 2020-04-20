package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.RtnProdContract;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Product_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import static com.ftrend.zgp.presenter.ShopCartPresenter.makeSeanFilter;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnProdContract.RtnProdPresenter {
    private static final String TAG = "RtnProdPresenter";
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
        List<DepProduct> mTempList = SQLite.select(DepProduct_Table.prodCode).from(DepProduct.class).where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();
        List<String> mStrList = new ArrayList<>();
        for (DepProduct prod : mTempList) {
            mStrList.add(prod.getProdCode());
        }
        List<Product> mProdList = SQLite.select().from(Product.class)
                .where(Product_Table.prodCode.in(mStrList))
                .and(Product_Table.prodStatus.withTable().notIn("2", "3"))
                //季节销售商品
                .and(OperatorGroup.clause(Product_Table.season.withTable().eq("0000"))
                        .or(Product_Table.season.withTable().like(makeSeanFilter())))
                .queryList();
        for (Product product : mProdList) {
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
            updateTradeInfo();
        }
    }

    @Override
    public void searchProdByScan(String code, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            if (code.equals(products.get(i).getProdCode()) ||
                    code.equals(products.get(i).getBarCode())) {
                mView.setScanProdPosition(i);
                addRtnProd(products.get(i));
                break;
            }
            if (i == products.size() - 1) {
                mView.showError("无此商品");
            }
        }
    }


    @Override
    public List<Product> searchDepProdList(String key, List<Product> depProdList) {
        if (!depProdList.isEmpty()) {
            return RtnHelper.searchDepProdList(key, depProdList);
        }
        return null;
    }

    @Override
    public void addRtnProd(Product prod) {
        addRtnProd(prod, prod.getPrice());
    }

    @Override
    public void addRtnProd(Product prod, double price) {
        if (prod.getForSaleRet() == 1) {
            //不允许退货
            mView.showError("该商品不允许退货");
        } else {
            prod.setPrice(price);
            if (!RtnHelper.addProduct(prod)) {
                LogUtil.e("向数据库添加商品失败");
            } else {
                updateTradeInfo();
            }
        }
    }


    @Override
    public void updateTradeInfo() {
        //获取退货流水金额
        mView.showRtnTotal(RtnHelper.getRtnTotal());
    }

    @Override
    public void changePrice(int index, double price) {
        TradeProd prod = RtnHelper.getRtnProdList().get(index);
        Product product = RtnHelper.getProduct(prod.getProdCode());
        if ((price > (prod.getTotal() / prod.getAmount())) && product.getPrice() != 0) {
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
    public void checkInputNum(int index, double changeAmount) {
        if ("1".equals(ZgParams.getInputNum())) {
            mView.showInputNumDialog(index);
        } else {
            changeAmount(index, changeAmount);
        }
    }

    @Override
    public void coverAmount(int index, double changeAmount) {
        if (RtnHelper.coverRtnProdAmount(index, changeAmount)) {
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