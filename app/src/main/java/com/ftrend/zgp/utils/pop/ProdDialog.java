package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */

public class ProdDialog extends BottomPopupView implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rtn_dialog_rl)
    RelativeLayout mDialog;
    @BindView(R.id.rtn_dialog_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rtn_dialog_edt)
    ClearEditText mEdt;
    @BindView(R.id.rtn_dialog_title)
    TextView mTitleTv;
    private Context mContext;
    private ShopAdapter<Product> mAdapter;
    private onDialogCallBack callBack;
    private ProdType mProdType;
    private Handler mHandler = new Handler();

    @Override
    protected int getImplLayoutId() {
        return R.layout.rtn_prod_dialog;
    }

    public ProdDialog(@NonNull Context context, onDialogCallBack callBack) {
        this(context, callBack, ProdType.RtnProd);
    }

    public ProdDialog(@NonNull Context context, onDialogCallBack callBack, ProdType mProdType) {
        super(context);
        this.mContext = context;
        this.callBack = callBack;
        this.mProdType = mProdType;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        switch (mProdType) {
            case VipProd:
                mTitleTv.setText("选择刷卡商品");
                mAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item_normal, null, 10);
                break;
            case RtnProd:
            default:
                mAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item_normal, null, 7);
                break;
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mDialog.getLayoutParams();
        layoutParams.height = ScreenUtils.getAppScreenHeight() / 20 * 17;
        mDialog.setLayoutParams(layoutParams);
        mEdt.addTextChangedListener(watcher);
        mAdapter.setOnItemClickListener(this);
        callBack.onLoad(mRecyclerView, mAdapter);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            callBack.onSearch(mEdt.getText().toString(), mAdapter);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick(R.id.rtn_dialog_img_hide)
    public void onHide() {
        if (ClickUtil.onceClick()) {
            return;
        }
        dismiss();
    }

    @Override
    protected void onDismiss() {
        KeyboardUtils.hideSoftInput(this);
        super.onDismiss();
        callBack.onClose();
        mHandler = null;
    }

    @OnClick(R.id.rtn_dialog_img_scan)
    public void onScan() {
        if (ClickUtil.onceClick()) {
            return;
        }
        callBack.onScan();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 300);
    }


    /**
     * @param callBack 导入回调实例
     */
    public void setCallBack(onDialogCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (ClickUtil.onceClick()) {
            return;
        }
        callBack.onItemClick(mRecyclerView, mAdapter, position);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 300);
    }

    public enum ProdType {
        RtnProd,
        VipProd
    }


    /**
     * 弹窗回调
     */
    public interface onDialogCallBack {

        /**
         * @param view
         * @param adapter
         */
        void onLoad(RecyclerView view, ShopAdapter<Product> adapter);

        /**
         * @param view     列表
         * @param adapter  适配器
         * @param position 索引
         */
        void onItemClick(RecyclerView view, ShopAdapter<Product> adapter, int position);

        /**
         * 点击摄像头扫描按钮
         */
        void onScan();

        /**
         * 关闭
         */
        void onClose();

        /**
         * 筛选
         */
        void onSearch(String key, ShopAdapter<Product> adapter);

    }
}
