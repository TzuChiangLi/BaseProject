package com.ftrend.zgp.presenter;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 主界面P层----所有业务逻辑在此
 *
 * @author liziqiang@ftrend.cn
 */
public class HomePresenter implements Contract.HomePresenter, HttpCallBack {
    private Contract.HomeView mView;


    public HomePresenter(Contract.HomeView mView) {
        this.mView = mView;
    }

    public static HomePresenter createPresenter(Contract.HomeView mView) {
        return new HomePresenter(mView);
    }


    @Override
    public void initMenuList() {
        List<Menu> menuList = new ArrayList<>();
        List<Menu.MenuList> childList = new ArrayList<>();
        String[] menuName = {"收银", "取单", "退货", "交班", "交班报表", "交易统计", "流水查询", "数据同步", "操作指南", "参数设置"
                , "修改密码", "注销登录"};
        int[] menuImg = {R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb, R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb,
                R.drawable.jy_sy, R.drawable.jy_qd, R.drawable.jy_th, R.drawable.jy_jb};
        for (int i = 0; i < 4; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("交易", childList));
        childList = new ArrayList<>();
        for (int i = 4; i < 7; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("报表查询", childList));
        childList = new ArrayList<>();
        for (int i = 7; i < menuName.length; i++) {
            childList.add(new Menu.MenuList(menuImg[i], menuName[i]));
        }
        menuList.add(new Menu("系统功能", childList));
        mView.setMenuList(menuList);

    }

    @Override
    public void setInfo() {
        int size = 3;
        String[] info = new String[size];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Arrays.fill(info, sdf.format(new Date()));
        mView.showInfo(info);
    }

    @Override
    public void goShopCart() {
        //先判断TradeProd表内的流水号在Trade表里的Status状态
        //如果是取消，就创建新单子
        TradeHelper.initSale();
        mView.goShopChartActivity(TradeHelper.getTrade().getLsNo());
        //三位POS号+五位流水号
//        String maxLsNo = SQLite.select(TradeProd_Table.lsNo).from(TradeProd.class)
//                .where(TradeProd_Table.id.eq(id))
//                .querySingle().getLsNo();
//        LogUtil.d("----maxLsNo:"+maxLsNo);
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


    /**
     * 创建流水单号
     *
     * @return 流水单号
     */
    private String createLsNo() {
        String max = "99999";
        long count = SQLite.select(count(Trade_Table.id)).from(Trade.class).count();
        String maxLsNo = "";
        if (count != 0) {
            FlowCursor cursor = SQLite.select(Method.max(Trade_Table.id)).from(Trade.class).query();
            cursor.moveToFirst();
            int id = cursor.getIntOrDefault(0);
            cursor.close();
            maxLsNo = SQLite.select(Trade_Table.lsNo).distinct().from(Trade.class).where(Trade_Table.id.eq(id)).querySingle().getLsNo();
            String status = SQLite.select(Trade_Table.status).from(Trade.class).where(Trade_Table.lsNo.eq(maxLsNo)).querySingle().getStatus();
            if (!status.equals("0")) {
                String tmp = maxLsNo.substring(3, 7);
                String pos = maxLsNo.substring(0, 2);
                if (tmp.equals(max)) {
                    tmp = "00000";
                } else {
                    tmp = padLeft(Integer.valueOf(tmp) + 1);
                }
                maxLsNo = pos + tmp;
            }
        } else {
            maxLsNo = "00100000";
        }
        return maxLsNo;
    }

    /**
     * 字符补齐
     *
     * @param inStr 输入文本
     * @return 补齐文本
     */
    private String padLeft(int inStr) {
        String outStr = "";
        String temp = String.valueOf(inStr);
        int size = temp.length();
        if (size == 1) {
            outStr = "0000" + temp;
        } else if (size == 2) {
            outStr = "000" + temp;
        } else if (size == 3) {
            outStr = "00" + temp;
        } else if (size == 4) {
            outStr = "0" + temp;
        } else if (size == 5) {
            outStr = temp;
        }

        return outStr;
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
