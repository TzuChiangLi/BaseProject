package com.ftrend.zgp;

import android.os.Build;

import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.Dep_Table;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Product_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Locale;

/**
 * 优惠计算单元测试
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/28
 */
@RunWith(RobolectricTestRunner.class)
//@Config(minSdk = Build.VERSION_CODES.LOLLIPOP, maxSdk = Build.VERSION_CODES.N_MR1)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class DscHelperUnitTest {

    /**
     * 初始化运行环境
     */
    @Before
    public void init() {
        FlowManager.init(RuntimeEnvironment.systemContext);// 使用@BeforeClass注解时，systemContext为null
        System.out.println("正在导入数据...");
        TestDataImporter.importAll();
        System.out.println("数据导入完成。");
        ZgParams.loadParams();
    }

    @After
    public void finalize() {
        FlowManager.close();
    }

    @Test
    public void simpleTest() {
        assert initUserAndDep("063", "2037");
        System.out.println(ZgParams.getPosCode());
        System.out.println(ZgParams.getCurrentUser().getUserName());
        System.out.println(ZgParams.getCurrentDep().getDepName());
    }

    /**
     * 测试：所有商品允许优惠
     */
    @Test
    public void fullDscTest() {
        assert initUserAndDep("063", "2037");
        initCartWithFullDsc();

        double dscRate = 10.0;
        doWholeDsc(dscRate);

        outputProdTotal();
        outputTradeTotal();

        double prodTotal = getProdTotal();
        double tradeTotal = TradeHelper.getTradeTotal();
        double dscTotal = TradeHelper.getTrade().getDscTotal();
        assert doubleEq(prodTotal, tradeTotal + dscTotal);
        assert doubleEq(dscTotal, prodTotal * dscRate / 100.0);
        System.out.println("fullDscTest PASS");
    }

    /**
     * 测试：部分商品允许优惠
     */
    @Test
    public void halfDscTest() {
        assert initUserAndDep("063", "2037");
        initCartWithHalfDsc();

        double dscRate = 10.0;
        doWholeDsc(dscRate);

        outputProdTotal();
        outputTradeTotal();

        double prodTotal = getProdTotal();
        double tradeTotal = TradeHelper.getTradeTotal();
        double dscTotal = TradeHelper.getTrade().getDscTotal();
        assert doubleEq(prodTotal, tradeTotal + dscTotal);
        assert doubleLt(dscTotal, prodTotal * dscRate / 100.0);
        System.out.println("halfDscTest PASS");
    }

    /**
     * 测试：所有商品不允许优惠
     */
    @Test
    public void noDscTest() {
        assert initUserAndDep("063", "2009");
        initCartWithNoDsc();

        double dscRate = 10.0;
        doWholeDsc(dscRate);

        outputProdTotal();
        outputTradeTotal();

        double prodTotal = getProdTotal();
        double tradeTotal = TradeHelper.getTradeTotal();
        double dscTotal = TradeHelper.getTrade().getDscTotal();
        assert doubleEq(prodTotal, tradeTotal + dscTotal);
        assert doubleEq(dscTotal, 0);
        System.out.println("noDscTest PASS");
    }

    /**
     * 设置当前登录的用户和专柜
     *
     * @param userCode
     * @param depCode
     * @return
     */
    private boolean initUserAndDep(String userCode, String depCode) {
        User user = SQLite.select().from(User.class)
                .where(User_Table.userCode.eq(userCode))
                .querySingle();
        if (user == null) {
            System.out.println("用户不存在！");
            return false;
        }
        Dep dep = SQLite.select().from(Dep.class)
                .where(Dep_Table.depCode.eq(depCode))
                .querySingle();
        if (dep == null) {
            System.out.println("专柜不存在！");
            return false;
        }
        ZgParams.saveCurrentInfo(user, dep);
        return true;
    }

    /**
     * 清除购物车：挂起未结流水
     */
    private void clearCart() {
        SQLite.update(Trade.class)
                .set(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_HANGUP))
                .where(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_NOTPAY))
                .execute();
    }

    /**
     * 添加指定商品到购物车
     *
     * @param prodCode
     */
    private void addToCart(String prodCode) {
        Product product = SQLite.select().from(Product.class)
                .where(Product_Table.prodCode.eq(prodCode))
                .querySingle();
        TradeHelper.addProduct(product);
    }

    /**
     * 设置购物车：全部商品支持优惠
     */
    private void initCartWithFullDsc() {
        clearCart();
        TradeHelper.initSale();
        addToCart("X73319Y");
        addToCart("X73319Z");
        addToCart("X74123A");
        addToCart("X74123B");
        TradeHelper.recalcTotal();
    }

    /**
     * 设置购物车：部分商品支持优惠
     */
    private void initCartWithHalfDsc() {
        clearCart();
        TradeHelper.initSale();
        // 这里的商品属于两个不同的专柜
        addToCart("X73319Y");
        addToCart("X73319Z");
        addToCart("A1AA74508");
        addToCart("A1AA83501");
        TradeHelper.recalcTotal();
    }

    /**
     * 设置购物车：全部商品不支持优惠
     */
    private void initCartWithNoDsc() {
        clearCart();
        TradeHelper.initSale();
        addToCart("9A0DD7301");
        addToCart("A1AA74507");
        addToCart("A1AA74508");
        addToCart("A1AA83501");
        TradeHelper.recalcTotal();
    }

    /**
     * 执行整单优惠
     *
     * @param dscRate 折扣率
     */
    private void doWholeDsc(double dscRate) {
        DscHelper.beginWholeDsc();
        DscHelper.wholeDscByRate(dscRate);
        DscHelper.commitWholeDsc();
        /*double dsc = DscHelper.getWholeDsc(10);
        DscHelper.commitWholeDsc(dsc);*/
    }

    /**
     * 获取商品总价
     *
     * @return
     */
    private double getProdTotal() {
        List<TradeProd> prodList = TradeHelper.getProdList();
        double total = 0;
        for (TradeProd prod : prodList) {
            total += prod.getAmount() * prod.getPrice();
        }
        return total;
    }

    private boolean doubleEq(double value1, double value2) {
        return Math.abs(value1 - value2) < 0.01;
    }

    private boolean doubleLt(double value1, double value2) {
        return value1 - value2 < 0.00;
    }

    private void outputTradeTotal() {
        Trade trade = TradeHelper.getTrade();
        outputValue("合计应收", trade.getTotal());
        outputValue("优惠合计", trade.getDscTotal());
    }

    private void outputProdTotal() {
        List<TradeProd> prodList = TradeHelper.getProdList();
        double total = 0;
        double manuDsc = 0;
        for (TradeProd prod : prodList) {
            total += prod.getAmount() * prod.getPrice();
            manuDsc += prod.getManuDsc();
        }
        outputValue("商品总价", total);
        outputValue("商品手工优惠合计", manuDsc);
        outputValue("商品应收合计", total - manuDsc);
    }

    private void outputValue(String msg, double value) {
        System.out.println(String.format(Locale.CHINA, msg + ": %.2f", value));
    }
}
