package com.ftrend.zgp.utils.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.lxj.xpopup.core.CenterPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 模态弹窗构建工具
 *
 * @author LZQ
 */
public class DialogBuilder extends CenterPopupView {
    @BindView(R.id.dialog_img_state)
    ImageView mStateImg;
    @BindView(R.id.dialog_tv_title)
    TextView mTitleTv;
    @BindView(R.id.dialog_tv_msg)
    TextView mMsgTv;
    @BindView(R.id.dialog_ll_btn_left)
    Button mLeftBtn;
    @BindView(R.id.dialog_ll_btn_right)
    Button mRightBtn;
    @BindView(R.id.dialog_ll_btn)
    LinearLayout mBtnLayout;
    @BindView(R.id.dialog_divide_line)
    View mHorizontalLineView;
    @BindView(R.id.dialog_ll_btn_line)
    View mVerticalLineView;
    private Context context;
    private String title, content, leftBtn, rightBtn;

    private onBtnClickListener mOnClickListener;
    /**
     * 0:提示，1：警告，2：错误，3：询问
     */
    private int dialogType = 0;
    /**
     * 按钮数量
     */
    private int btnNum = 0;

    /**
     * 默认显示两个按钮
     *
     * @param context 控制上下文
     */
    public DialogBuilder(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 传入按钮的数量
     *
     * @param context 控制上下文
     * @param btnNum  按钮数量
     */
    public DialogBuilder(Context context, int btnNum) {
        super(context);
        this.context = context;
        this.btnNum = btnNum;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        switch (dialogType) {
            case 0:
                mStateImg.setImageResource(R.drawable.dialog_state_tip);
                mTitleTv.setText("提示");
                break;
            case 1:
                mStateImg.setImageResource(R.drawable.dialog_state_warning);
                mTitleTv.setText("警告");
                break;
            case 2:
                mStateImg.setImageResource(R.drawable.dialog_state_error);
                mTitleTv.setText("错误");
                break;
            case 3:
                mStateImg.setImageResource(R.drawable.dialog_state_ask);
                mTitleTv.setText("询问");
                break;
        }
        switch (btnNum) {
            case 0:
                mBtnLayout.setVisibility(GONE);
                mHorizontalLineView.setVisibility(GONE);
                break;
            case 1:
                mRightBtn.setVisibility(GONE);
                mVerticalLineView.setVisibility(GONE);
                break;
            case 2:
                break;
        }
        if (!TextUtils.isEmpty(title)) {
            mTitleTv.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            mMsgTv.setText(content);
        }
        if (btnNum != 0) {
            if (!TextUtils.isEmpty(leftBtn)) {
                mLeftBtn.setText(leftBtn);
            }
            if (!TextUtils.isEmpty(rightBtn)) {
                mRightBtn.setText(rightBtn);
            }
        }
    }

    @OnClick(R.id.dialog_ll_btn_left)
    public void onLeftBtnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onLeftBtnClick(mLeftBtn);
        }
    }

    @OnClick(R.id.dialog_ll_btn_right)
    public void onRightBtnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onRightBtnClick(mRightBtn);
        }
    }

    public interface onBtnClickListener {
        void onLeftBtnClick(View v);

        void onRightBtnClick(View v);
    }

    public void setOnClickListener(onBtnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    //region 设置显示的内容
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLeftBtn(String leftBtn) {
        this.leftBtn = leftBtn;
    }

    public void setRightBtn(String rightBtn) {
        this.rightBtn = rightBtn;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public void setBtnNum(int btnNum) {
        this.btnNum = btnNum;
    }

    public Context getBuilderContext() {
        return context;
    }


    //endregion
}
