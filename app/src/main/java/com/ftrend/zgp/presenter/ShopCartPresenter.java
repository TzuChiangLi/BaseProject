package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements Contract.ShopCartPresenter {
    private Contract.ShopCartView mView;
    private List<DepProduct> mProdList = new ArrayList<>();

    private ShopCartPresenter(Contract.ShopCartView mView) {
        this.mView = mView;
    }

    public static ShopCartPresenter createPresenter(Contract.ShopCartView mView) {
        return new ShopCartPresenter(mView);
    }


    @Override
    public void refreshTrade() {
        TradeHelper.initSale();
    }

    /**
     * 生成季节查询条件
     *
     * @return
     */
    private String makeSeanFilter() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month <= 3) {
            return "1___";
        } else if (month <= 6) {
            return "_1__";
        } else if (month <= 9) {
            return "__1_";
        } else {
            return "___1";
        }
    }

    @Override
    public void initProdList() {
        List<DepCls> clsList = SQLite.select().from(DepCls.class)
                .where(DepCls_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .queryList();
        //注意：状态为注销、暂停销售的商品，以及季节商品在商品列表不显示；但已经添加到购物车的不受影响
        mProdList = SQLite.select().from(DepProduct.class)
                .where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                //3-注销；2-暂停销售
                .and(DepProduct_Table.prodStatus.notIn("2", "3"))
                //季节销售商品
                .and(OperatorGroup.clause(DepProduct_Table.season.eq("0000"))
                        .or(DepProduct_Table.season.like(makeSeanFilter())))
                .queryList();
        for (DepProduct product : mProdList) {
            product.setSelect(false);
        }
        mView.setProdList(mProdList);
        if (ZgParams.isShowCls(ZgParams.getCurrentDep().getDepCode())) {
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
        //key[0]==clsCode
        //key[1]==输入关键字
        if (!TextUtils.isEmpty(key[0])) {
            if ("all".equals(key[0]) && TextUtils.isEmpty(key[1])) {
                mView.updateProdList(mProdList);
                return;
            }
            List<DepProduct> fliterList = new ArrayList<>();
            if (mProdList.size() != 0 || mProdList != null) {
                for (DepProduct depProduct : mProdList) {
                    if (!"all".equals(key[0])) {
                        //有类别编码
                        if (!TextUtils.isEmpty(depProduct.getClsCode())) {
                            //该分类下
                            if (depProduct.getClsCode().contains(key[0])) {
                                //符合关键词筛选
                                if (!TextUtils.isEmpty(depProduct.getProdCode())) {
                                    if (depProduct.getProdCode().contains(key[1])) {
                                        fliterList.add(depProduct);
                                        continue;
                                    }
                                }
                                if (!TextUtils.isEmpty(depProduct.getProdName())) {
                                    if (depProduct.getProdName().contains(key[1])) {
                                        fliterList.add(depProduct);
                                        continue;
                                    }
                                }
                                if (!TextUtils.isEmpty(depProduct.getBarCode())) {
                                    if (depProduct.getBarCode().contains(key[1])) {
                                        fliterList.add(depProduct);
                                    }
                                }
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(depProduct.getProdCode())) {
                            if (depProduct.getProdCode().contains(key[1])) {
                                fliterList.add(depProduct);
                                continue;
                            }
                        }
                        if (!TextUtils.isEmpty(depProduct.getProdName())) {
                            if (depProduct.getProdName().contains(key[1])) {
                                fliterList.add(depProduct);
                                continue;
                            }
                        }
                        if (!TextUtils.isEmpty(depProduct.getBarCode())) {
                            if (depProduct.getBarCode().contains(key[1])) {
                                fliterList.add(depProduct);
                            }
                        }
                    }
                }
            }
            mView.updateProdList(fliterList);
        } else {
            //防止意外情况
            mView.updateProdList(mProdList);
        }
    }

    @Override
    public void addToShopCart(DepProduct depProduct) {
        addToShopCart(depProduct, depProduct.getPrice());
    }

    @Override
    public void addToShopCart(DepProduct depProduct, double price) {
        if (TradeHelper.addProduct(depProduct) == -1) {
            LogUtil.e("向数据库添加商品失败");
        } else {
            if (TradeHelper.vip != null) {
                //如果会员已登录且有会员信息
                DscHelper.saveVipProdDsc(TradeHelper.getProdList().size() - 1);
            } else {
                //如果单据未结，此时退出重新进入，此时vip==null
                if (ZgParams.isIsOnline()) {
                    RestSubscribe.getInstance().queryVipInfo(TradeHelper.getTrade().getVipCode(), new RestCallback(regHandler));
                } else {
                    mView.showError("无法获取会员优惠信息");
                }
            }
            TradeHelper.priceChangeInShopCart(price);
            updateTradeInfo();
        }
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            VipInfo vipInfo = TradeHelper.vip();
            vipInfo.setVipName(body.get("vipName").toString());
            vipInfo.setVipCode(body.get("vipCode").toString());
            vipInfo.setVipDscRate(Double.parseDouble(body.get("vipDscRate").toString()));
            vipInfo.setVipGrade(body.get("vipGrade").toString());
            vipInfo.setVipPriceType(Double.parseDouble(body.get("vipPriceType").toString()));
            vipInfo.setRateRule(Double.parseDouble(body.get("rateRule").toString()));
            vipInfo.setForceDsc(body.get("forceDsc").toString());
            vipInfo.setCardCode(body.get("cardCode").toString());
            vipInfo.setDscProdIsDsc(body.get("dscProdIsDsc").toString());
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
        double count = TradeHelper.getTradeCount();
        mView.updateTradeProd(count, price);
    }

    @Override
    public void searchProdByScan(String code, List<DepProduct> prodList) {
        boolean hasFlag = false;
        for (int i = 0; i < prodList.size(); i++) {
            if (code.equals(prodList.get(i).getProdCode()) ||
                    code.equals(prodList.get(i).getBarCode())) {
                hasFlag = true;
                mView.setScanProdPosition(i);
                addToShopCart(prodList.get(i));
                break;
            }
            if (i == prodList.size() && hasFlag == false) {
                mView.noScanProdPosition();
            }
        }
    }


    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
