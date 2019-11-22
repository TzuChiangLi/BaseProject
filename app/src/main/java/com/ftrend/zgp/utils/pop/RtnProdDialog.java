package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */

public class RtnProdDialog extends BottomPopupView {
    @BindView(R.id.rtn_dialog_rl)
    RelativeLayout mDialog;
    @BindView(R.id.rtn_dialog_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rtn_dialog_btn_finish)
    Button mBtn;
    @BindView(R.id.rtn_dialog_edt)
    ClearEditText mEdt;
    private Context mContext;
    private ShopAdapter<DepProduct> mAdapter;
    private onDialogCallBack callBack;

    @Override
    protected int getImplLayoutId() {
        return R.layout.rtn_prod_dialog;
    }

    public RtnProdDialog(@NonNull Context context, onDialogCallBack callBack) {
        super(context);
        mContext = context;
        this.callBack = callBack;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        mAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item_normal, null, 7);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mDialog.getLayoutParams();
        layoutParams.height = ScreenUtils.getAppScreenHeight() / 20 * 17;
        mDialog.setLayoutParams(layoutParams);
        mEdt.addTextChangedListener(watcher);
        callBack.onStart(mBtn);
        callBack.onLoad(mRecyclerView, mAdapter, mBtn);
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

    @OnClick(R.id.rtn_dialog_btn_finish)
    public void onFinish() {
        if (ClickUtil.onceClick()) {
            return;
        }
        dismiss();
    }

    @OnClick(R.id.rtn_dialog_img_hide)
    public void onHide() {
        if (ClickUtil.onceClick()) {
            return;
        }
        dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        callBack.onClose();
    }

    @OnClick(R.id.rtn_dialog_img_scan)
    public void onScan() {
        if (ClickUtil.onceClick()) {
            return;
        }
        callBack.onScan();
    }


    /**
     * @param callBack 导入回调实例
     */
    public void setCallBack(onDialogCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 弹窗回调
     */
    public interface onDialogCallBack {

        /**
         * @param btn 底部按钮
         */
        void onStart(Button btn);

        /**
         * @param view
         * @param adapter
         */
        void onLoad(RecyclerView view, ShopAdapter<DepProduct> adapter, Button btn);

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
        void onSearch(String key, ShopAdapter<DepProduct> adapter);

    }
}
