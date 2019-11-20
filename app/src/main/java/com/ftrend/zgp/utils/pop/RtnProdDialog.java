package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mDialog.getLayoutParams();
        layoutParams.height = ScreenUtils.getAppScreenHeight() / 20 * 19;
        mDialog.setLayoutParams(layoutParams);
        callBack.onStart(mBtn);
        callBack.onLoadProd(mRecyclerView, mAdapter, mBtn);
    }

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
        callBack.onScanClick();
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
        void onLoadProd(RecyclerView view, ShopAdapter<DepProduct> adapter, Button btn);

        /**
         * 点击摄像头扫描按钮
         */
        void onScanClick();

        /**
         * 关闭
         */
        void onClose();

        /**
         * 筛选
         */
        void onSearch(ShopAdapter<DepProduct> adapter);

    }
}
