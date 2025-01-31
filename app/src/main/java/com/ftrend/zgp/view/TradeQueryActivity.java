package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.TrdQryAdapter;
import com.ftrend.zgp.api.TrdQryContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.presenter.TrdQryPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class TradeQueryActivity extends BaseActivity implements TrdQryContract.TrdQryView, OnTitleBarListener {
    @BindView(R.id.trade_qry_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.textView_dep)
    TextView mDep;
    @BindView(R.id.trd_qry_prod_edt_trade)
    ClearEditText mEdt;
    @BindView(R.id.trade_qry_rv_list)
    RecyclerView mRecyclerView;
    private TrdQryContract.TrdQryPresenter mPresenter;
    private TrdQryAdapter mAdapter;
    private int page = 0;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.trade_qry_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setCurrentModule() {
        LogUtil.setCurrentModule("流水查询");
    }

    @Override
    protected int getLayoutID() {
        return R.layout.trade_query_activity;
    }

    @Override
    protected void initData() {
        mPresenter.loadTradeList(page);
    }

    @Override
    protected void initView() {
        mDep.setText("专柜：" + ZgParams.getCurrentDep().getDepName());

        if (mPresenter == null) {
            mPresenter = TrdQryPresenter.createPresenter(this);
        }
        mEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.search(mEdt.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void onLeftClick(View v) {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void showTradeList(List<Trade> trdList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TrdQryAdapter(R.layout.trade_qry_rv_item, trdList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.trade_qry_rv_item_btn_enter) {
                    MessageUtil.waitCircleProgress("加载中");
                    mPresenter.queryTradeProd(position);
                }
            }
        });
        //设置分页，上拉加载更多
        mAdapter.setOnLoadMoreListener(loadMoreListener, mRecyclerView);
        mAdapter.disableLoadMoreIfNotFullPage();
    }

    @Override
    public void loadMoreTrade(List<Trade> trdList) {
        mAdapter.addData(trdList);
        mAdapter.loadMoreComplete();
    }

    @Override
    public void loadMoreEnd() {
        mAdapter.loadMoreEnd();
    }


    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPresenter.addTradeData(++page);
                }
            }, 1000);
        }
    };


    @Override
    public void updateFilterTrade(List<Trade> trdList) {
        //过滤筛选
        if (!trdList.isEmpty()) {
            mAdapter.setNewData(trdList);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.setNewData(null);
            mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty,
                    (ViewGroup) mRecyclerView.getParent(), false));
            TextView mEmptyTv = mAdapter.getEmptyView().findViewById(R.id.rv_item_tv_empty);
            mEmptyTv.setText("无满足条件的流水");
        }
    }

    @Override
    public void setEnableLoadMore(boolean canLoadMore) {
        mAdapter.setEnableLoadMore(canLoadMore);
    }

    @Override
    public void goTradeProdActivity(String lsNo) {
        MessageUtil.waitEnd();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TradeQueryActivity.this, TradeProdActivity.class);
                startActivity(intent);
            }
        }, 300);

    }

    @Override
    public void showError(final String msg) {
        MessageUtil.waitEnd();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageUtil.show(msg);
            }
        }, 500);

    }

    @OnClick(R.id.trade_qry_btn_back)
    public void back() {
        finish();
    }

    @Override
    public void setPresenter(TrdQryContract.TrdQryPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
