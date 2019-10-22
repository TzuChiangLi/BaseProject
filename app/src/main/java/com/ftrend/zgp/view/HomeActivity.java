package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.MenuAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.presenter.HomePresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.pop.StringInputCallback;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 主界面V层----本层不处理任何数据，只负责调用、接收并显示
 *
 * @author liziqiang@ftrend.cn
 */
public class HomeActivity extends BaseActivity implements Contract.HomeView, MenuAdapter.OnMenuClickListener {
    @BindView(R.id.home_rv_menu)
    RecyclerView mRecyclerView;
    @BindView(R.id.home_tv_date)
    TextView mDateTv;
    @BindView(R.id.home_tv_user)
    TextView mUserTv;
    @BindView(R.id.home_tv_depname)
    TextView mDepTv;
    @BindView(R.id.home_img_online_status)
    ImageView mNetImg;
    private Contract.HomePresenter mPresenter;
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
        mPresenter.initSqbSdk();
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
    public void setPresenter(Contract.HomePresenter presenter) {
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
    }

    @Override
    public void mustHandover() {
        MessageUtil.showWarning("您已经长时间未交班，功能使用受限。请先交班。");
    }


    @Override
    public void tipHandover() {
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
            case "退货":
                mPresenter.goRtnProd();
                break;
            case "注销登录":
                mPresenter.logout();
                break;
            case "取单":
                mPresenter.getOutOrder();
                break;
            case "交班报表":
                MessageUtil.waitBegin("请稍等", null);
                break;
            case "交易统计":
                MessageUtil.showSuccess();
                break;
            case "流水查询":
                MessageUtil.showWarning("警告");
                break;
            case "数据同步":
                // TODO: 2019/10/12 重构：移动到presenter类
                final String waitMsg = "正在同步数据，请稍候...";
                MessageUtil.waitBegin(waitMsg, new MessageUtil.MessageBoxCancelListener() {
                    @Override
                    public boolean onCancel() {
                        DataDownloadTask.taskCancel();
                        return false;
                    }
                });
                DataDownloadTask.taskStart(true, new DataDownloadTask.ProgressHandler() {
                    @Override
                    public void handleProgress(int percent, boolean isFailed, String msg) {
                        System.out.println(String.format(Locale.getDefault(), "数据下载进度：%d%% %s", percent, msg));
                        String custMsg = String.format(Locale.getDefault(), waitMsg + "(%d%%)", percent);
                        MessageUtil.waitUpdate(custMsg);
                        if (percent >= 100) {
                            MessageUtil.waitEnd();
                            MessageUtil.showSuccess("数据同步已完成");
                        } else if (isFailed) {
                            MessageUtil.waitEnd();
                            MessageUtil.showError("数据同步失败：" + msg);
                        }
                    }
                });
                break;
            case "操作指南":
                MessageUtil.error("操作指南");
                break;
            case "参数设置":
                MessageUtil.warning("参数设置");
                break;
            case "修改密码":
                MessageUtil.question("修改密码", new MessageUtil.MessageBoxYesNoListener() {
                    @Override
                    public void onYes() {
                    }

                    @Override
                    public void onNo() {
                    }
                });
                break;
            case "测试退款":
                // 此功能仅用于测试
                if (!ZgParams.getSqbConfig().isDemoMode()) {
                    return;
                }
                String msg = "请输入支付记录中的商家订单号：";
                MessageUtil.showInput(HomeActivity.this, msg, new StringInputCallback() {
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
                break;
            default:
                LogUtil.e("无此功能");
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
        Intent intent = new Intent(HomeActivity.this, RtnProdActivity.class);
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
    public void doAsyncTask() {

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
}
