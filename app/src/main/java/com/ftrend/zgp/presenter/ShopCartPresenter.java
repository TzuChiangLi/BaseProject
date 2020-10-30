package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.ShopCartContract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements ShopCartContract.ShopCartPresenter {
    private static final String TAG = "ShopCartPresenter";
    private ShopCartContract.ShopCartView mView;
    private List<Product> mProdList = new ArrayList<>();

    //查询参数
    private int mPageSize = 20;//每页行数
    private int mPage = 0;//当前页
    private String mClassCode = null;//当前分类
    private String mQueryStr = null;//查询字符串，用于扫码或模糊查询

    private ShopCartPresenter(ShopCartContract.ShopCartView mView) {
        this.mView = mView;
    }

    public static ShopCartPresenter createPresenter(ShopCartContract.ShopCartView mView) {
        return new ShopCartPresenter(mView);
    }

    @Override
    public void refreshTrade() {
        TradeHelper.initSale();
    }

    @Override
    public void initProdList() {
        mProdList = new ArrayList<>();
        mView.setProdList(mProdList);
        loadPage();
        if (ZgParams.isShowCls(ZgParams.getCurrentDep().getDepCode())) {
            List<DepCls> clsList;
            if (!"0".equalsIgnoreCase(ZgParams.getCurrentDep().getDepCode())) {
                clsList = SQLite.select().from(DepCls.class)
                        .queryList();
            } else {
                clsList = SQLite.select().from(DepCls.class)
                        .where(DepCls_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                        .queryList();
            }
            for (DepCls cls : clsList) {
                cls.setSelect(false);
            }
            DepCls depCls = new DepCls();
            depCls.setClsName("全部类别");
            depCls.setDepCode("all");
            depCls.setClsCode("all");
            depCls.setSelect(true);

            clsList.add(0, depCls);
            mView.setClsList(clsList);
        }
    }

    @Override
    public void loadMoreProd() {
        mPage++;
        loadPage();
    }

    private void loadPage() {
        mProdList = TradeHelper.loadProduct(mClassCode, mQueryStr, mPage, mPageSize);
        for (Product product : mProdList) {
            product.setSelect(false);
        }
        //page = 0，更新商品列表；否则，追加到商品列表
        if (mPage == 0) {
            mView.updateProdList(mProdList);
        } else {
            mView.appendProdList(mProdList);
        }
    }

    @Override
    public void initOrderInfo() {
        mView.updateTradeProd(TradeHelper.getTradeCount(), TradeHelper.getTradeTotal());
    }

    @Override
    public void updateOrderInfo() {
        mView.updateOrderInfo();
        mView.updateTradeProd(TradeHelper.getTradeCount(), TradeHelper.getTradeTotal());
    }


    @Override
    public void searchProdList(String... key) {
        mClassCode = key[0];
        mQueryStr = key[1];
        mPage = 0;
        loadPage();
    }

    @Override
    public void addToShopCart(Product product) {
        addToShopCart(product, product.getPrice());
    }

    @Override
    public void addToShopCart(Product product, double price) {
        if (TradeHelper.addProduct(product) == -1) {
            LogUtil.e("向数据库添加商品失败");
        } else {
            TradeHelper.priceChangeInShopCart(price);
            if (TradeHelper.vip != null) {
                //如果会员已登录且有会员信息
                DscHelper.saveVipProdDsc(TradeHelper.getProdList().size() - 1);
            } else {
                //如果单据未结，此时退出重新进入，此时vip==null
                if (ZgParams.isIsOnline()) {
                    RestSubscribe.getInstance().queryVipInfo(TradeHelper.getTrade().getVipCode(), new RestCallback(regHandler));
                } else {
                    if (TradeHelper.vip != null) {
                        mView.showError("无法获取会员优惠信息");
                    }
                }
            }
            updateTradeInfo();
        }
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            VipInfo vipInfo = TradeHelper.vip();
            vipInfo.setVipName(body.getString("vipName"));
            vipInfo.setVipCode(body.getString("vipCode"));
            vipInfo.setVipDscRate(body.getDouble("vipDscRate"));
            vipInfo.setVipGrade(body.getString("vipGrade"));
            vipInfo.setVipPriceType(body.getDouble("vipPriceType"));
            vipInfo.setRateRule(body.getDouble("rateRule"));
            vipInfo.setForceDsc(body.getString("forceDsc"));
            vipInfo.setCardCode(body.getString("cardCode"));
            vipInfo.setDscProdIsDsc(body.getString("dscProdIsDsc"));
            //保存会员信息
            TradeHelper.saveVip();
            //会员优惠
            DscHelper.saveVipProdDsc(TradeHelper.getProdList().size() - 1);
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
        }
    };

    @Override
    public void setTradeStatus(String status) {
        TradeHelper.setTradeStatus(status);
        TradeHelper.saveVipInfo();
        mView.returnHomeActivity(TradeHelper.convertTradeStatus(status));
        TradeHelper.clear();
        TradeHelper.clearVip();

    }

    @Override
    public void cancelPriceChange(int index) {
        TradeHelper.rollackPriceChangeInShopCart();
        mView.cancelAddProduct(index);
        updateTradeInfo();
    }

    @Override
    public void updateTradeInfo() {
        double price = TradeHelper.getTradeTotal();
        long count = TradeHelper.getTradeCount();
        mView.updateTradeProd(count, price);
    }

    @Override
    public void searchProdByScan(String code, List<Product> prodList) {
        searchProdList(null, code);
        if (mProdList.size() > 0) {
            mView.setScanProdPosition(0);
        } else {
            mView.noScanProdPosition();
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
            mProdList = null;
            System.gc();
        }
    }
}
