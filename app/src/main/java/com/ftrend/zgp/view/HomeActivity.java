package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftrend.zgp.App;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.MenuAdapter;
import com.ftrend.zgp.api.HomeContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayResult;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.presenter.HomePresenter;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.sunmi.SunmiPayHelper;
import com.ftrend.zgp.utils.sunmi.VipCardData;
import com.gyf.immersionbar.ImmersionBar;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sunmi.pay.hardware.aidl.AidlConstants;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 主界面V层----本层不处理任何数据，只负责调用、接收并显示
 *
 * @author liziqiang@ftrend.cn
 */
public class HomeActivity extends BaseActivity implements HomeContract.HomeView, MenuAdapter.OnMenuClickListener {
    @BindView(R.id.home_rv_menu)
    RecyclerView mRecyclerView;
    @BindView(R.id.home_tv_date)
    TextView mDateTv;
    @BindView(R.id.home_tv_user)
    TextView mUserTv;
    @BindView(R.id.home_tv_depname)
    TextView mDepTv;
    @BindView(R.id.home_tv_pos_code)
    TextView mPosCodeTv;
    @BindView(R.id.home_img_online_status)
    ImageView mNetImg;
    private HomeContract.HomePresenter mPresenter;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void initData() {
        mPresenter.initMenuList();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
        //设置界面信息
        mPresenter.setInfo();
        //启动线程
        mPresenter.initServerThread();
        //初始化商米支付SDK
        mPresenter.initSunmiPaySdk();
        //初始化收钱吧SDK
        mPresenter.initSqbSdk(this);
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mNetImg.setImageResource(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.home_activity;
    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        if (presenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
        mPresenter = presenter;
    }


    @Override
    public void showError(String msg) {
        MessageUtil.showError(msg);
    }

    @Override
    public void setMenuList(List<Menu> menuList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMenuAdapter = new MenuAdapter(this, R.layout.home_rv_menu_item, menuList);
        mRecyclerView.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnMenuClickListener(this);
    }

    /**
     * 设置主界面需要显示的几个信息
     *
     * @param info 数组信息
     */
    @Override
    public void showInfo(String... info) {
        mDateTv.setText(info[0]);
        mUserTv.setText(ZgParams.getCurrentUser().getUserName());
        mDepTv.setText(ZgParams.getCurrentDep().getDepName());
        mPosCodeTv.setText(ZgParams.getPosCode());
    }

    @Override
    public void mustHandover() {
        MessageUtil.error("您已经长时间未交班，功能使用受限。请先交班。",
                new MessageUtil.MessageBoxOkListener() {
                    @Override
                    public void onOk() {
                        mPresenter.goHandover();
                    }
                });
    }

    @Override
    public void tipHandover(int days) {
        MessageUtil.showWarning(String.format(Locale.CHINA,
                "您已经%d天未交班，请及时交班，避免功能使用受限。", days));
    }

    @Override
    public void onMenuClick(View view, int position) {
        if (ClickUtil.onceClick()) {
            return;
        }
        switch ((String) view.getTag()) {
            case "收银":
                mPresenter.goShopCart();
                break;
            case "交班":
                mPresenter.goHandover();
                break;
            case "注销登录":
                mPresenter.logout();
                break;
            case "取单":
                mPresenter.getOutOrder();
                break;
            case "数据同步":
                mPresenter.doDataTrans();
                break;
            case "参数设置":
                mPresenter.goConfigSetting();
                break;
            case "退货":
                mPresenter.goRtnProd();
                break;
            case "交易统计":
                mPresenter.goTradeReport();
                break;
            case "交班报表":
                mPresenter.goHandoverReport();
                break;
            case "流水查询":
                mPresenter.goTradeQuery();
                break;
            case "操作指南":
                TradeHelper.getPaidLs("90200033");
                // 此功能仅用于测试
                if (!CommonUtil.debugMode(App.getContext())) {
                    return;
                }
                if (!SunmiPayHelper.getInstance().serviceAvailable()) {
                    MessageUtil.showError("刷卡服务不可用");
                    return;
                }
                MessageUtil.question("请选择写卡还是读卡", "读卡", "写卡", new MessageUtil.MessageBoxYesNoListener() {
                    @Override
                    public void onYes() {
                        //读卡
                        MessageUtil.waitBegin("测试功能：读取IC卡\n请刷卡...", new MessageUtil.MessageBoxCancelListener() {
                            @Override
                            public boolean onCancel() {
                                SunmiPayHelper.getInstance().cancelReadCard();
                                return true;
                            }
                        });
                        SunmiPayHelper.getInstance().readCard(new SunmiPayHelper.ReadCardCallback() {
                            @Override
                            public void onSuccess(VipCardData data) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("卡号：" + data.getCardCode() + "\n");
                                sb.append("余额：" + data.getMoney() + "\n");
                                sb.append("密码：" + data.getVipPwdDecrypted());
                                MessageUtil.waitSuccesss(sb.toString(), null);
                            }

                            @Override
                            public void onError(String msg) {
                                MessageUtil.waitError(msg, null);
                            }
                        });
                    }

                    @Override
                    public void onNo() {
                        //写卡
                        VipCardData data = new VipCardData(AidlConstants.CardType.MIFARE);
                        data.setCardCode("2");
                        data.setMoney(10000.0);
                        data.setVipPwd("7AUAL;;?.");// //123456
                        MessageUtil.waitBegin("测试功能：初始化IC卡\n请刷卡...", new MessageUtil.MessageBoxCancelListener() {
                            @Override
                            public boolean onCancel() {
                                SunmiPayHelper.getInstance().cancelWriteCard();
                                return true;
                            }
                        });
                        SunmiPayHelper.getInstance().initCard(data, new SunmiPayHelper.WriteCardCallback() {
                            @Override
                            public void onSuccess(VipCardData data1) {
                                MessageUtil.waitSuccesss("写卡成功", null);
                            }

                            @Override
                            public void onError(String msg) {
                                MessageUtil.waitError("写卡失败", null);
                            }
                        });
                    }
                });
                break;
            /*case "测试退款":
                // 此功能仅用于测试
                if (!CommonUtil.debugMode(App.getContext())) {
                    return;
                }
                String msg = "请输入支付记录中的商家订单号：";
                InputPanel.showInput(HomeActivity.this, msg, new StringInputCallback() {
                    @Override
                    public void onOk(String value) {
                        Trade trade = new Trade();
                        trade.setLsNo("TEST");
                        SqbPayHelper.refundBySn(trade, value, new SqbPayHelper.PayResultCallback() {
                            @Override
                            public void onResult(boolean isSuccess, String payType, String payCode, String errMsg) {
                                if (isSuccess) {
                                    MessageUtil.showSuccess("退款成功");
                                } else {
                                    MessageUtil.showError("退款失败");
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        MessageUtil.show("已取消退款");
                    }

                    @Override
                    public String validate(String value) {
                        return null;
                    }
                });
                break;*/
            case "修改密码":
                mPresenter.goPwdChange();
                break;
            default:
                MessageUtil.showError("此功能暂不可用");
                break;
        }
    }

    @Override
    public void goShopChartActivity() {
        Intent intent = new Intent(HomeActivity.this, ShopCartActivity.class);
        startActivity(intent);
    }

    @Override
    public void goHandoverActivity() {
        Intent intent = new Intent(HomeActivity.this, HandoverActivity.class);
        startActivity(intent);
    }

    @Override
    public void goOrderOutActivity() {
        Intent intent = new Intent(HomeActivity.this, OrderOutActivity.class);
        startActivity(intent);
    }

    @Override
    public void goRtnProdActivity() {
        Intent intent = new Intent(HomeActivity.this, RtnTradeActivity.class);
        startActivity(intent);
    }

    @Override
    public void goHandoverReportActivity() {
        Intent intent = new Intent(HomeActivity.this, HandoverActivity.class);
        intent.putExtra("isReport", true);
        startActivity(intent);
    }

    @Override
    public void goTradeReportActivity() {
        Intent intent = new Intent(HomeActivity.this, TradeReportActivity.class);
        startActivity(intent);
    }

    @Override
    public void goTradeQueryActivity() {
        Intent intent = new Intent(HomeActivity.this, TradeQueryActivity.class);
        startActivity(intent);
    }

    @Override
    public void goConfigActivity() {
        Intent intent = new Intent(HomeActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    @Override
    public void goPwdChangeActivity() {
        Intent intent = new Intent(HomeActivity.this, PwdChangeActivity.class);
        startActivity(intent);
    }


    @Override
    public void hasNoHangUpTrade() {
        MessageUtil.showWarning("当前无挂单流水");
    }

    @Override
    public void hasNoTrade() {
        MessageUtil.showWarning("当前没有交易流水！");
    }

    @Override
    public void showOfflineTip() {
        MessageUtil.showWarning("单机模式不能交班");
    }

    @Override
    public void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.initMenuList();
    }

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mNetImg == null) {
            mNetImg = findViewById(R.id.home_img_online_status);
        }
        mNetImg.setImageResource(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setCurrentModule() {
        LogUtil.setCurrentModule("首页");
    }

    @Override
    public void onBackPressed() {
        MessageUtil.question("是否要退出本程序？", new MessageUtil.MessageBoxYesNoListener() {
            @Override
            public void onYes() {
                finish();
            }

            @Override
            public void onNo() {

            }
        });
    }
}
