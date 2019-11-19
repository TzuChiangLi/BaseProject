package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.ftrend.zgp.R;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author liziqiang@ftrend.cn
 */

public class RtnProdDialog extends BottomPopupView {
    @BindView(R.id.rtn_dialog_rl)
    RelativeLayout mDialog;
    private Context mContext;

    @Override
    protected int getImplLayoutId() {
        return R.layout.rtn_prod_dialog;
    }

    public RtnProdDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mDialog.getLayoutParams();
        layoutParams.height = ScreenUtils.getAppScreenHeight()/15*14;
        mDialog.setLayoutParams(layoutParams);
    }
}
