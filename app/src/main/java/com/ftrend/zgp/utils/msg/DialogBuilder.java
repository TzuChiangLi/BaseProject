package com.ftrend.zgp.utils.msg;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 模态弹窗构建工具
 *
 * @author liziqiang@ftrend.cn
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
    private OnBtnClickListener mOnClickListener;


    /**
     * 0:提示，1：警告，2：错误，3：询问
     */
    private DialogType dialogType = DialogType.info;
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
        this(context, 0);
    }

    /**
     * 传入按钮的数量
     *
     * @param context 控制上下文
     * @param btnNum  按钮数量
     */
    public DialogBuilder(Context context, int btnNum) {
        super(context);
        if (context == null) {
            this.context = BaseActivity.mContext;
        } else {
            this.context = context;
        }
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
        initDialogType(dialogType);

        initBtnNum(btnNum);
    }

    private void initDialogType(DialogType dialogType) {
        switch (dialogType) {
            case info:
                mStateImg.setImageResource(R.drawable.dialog_state_tip);
                mTitleTv.setText("提示");
                break;
            case warning:
                mStateImg.setImageResource(R.drawable.dialog_state_warning);
                mTitleTv.setText("警告");
                break;
            case error:
                mStateImg.setImageResource(R.drawable.dialog_state_error);
                mTitleTv.setText("错误");
                break;
            case question:
                mStateImg.setImageResource(R.drawable.dialog_state_ask);
                mTitleTv.setText("确认");
                break;
            case wait:
                mStateImg.setImageResource(R.drawable.dialog_state_wait);
                mTitleTv.setText("等待");
                break;
            default:
                break;
        }
    }

    private void initBtnNum(int btnNum) {
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
            default:
                break;
        }
    }

    /**
     * 更新提示消息
     *
     * @param newMsg
     */
    public void updateMsg(String newMsg) {
        if (mMsgTv == null) {
            return;
        }
        mMsgTv.setText(newMsg);
    }

    /**
     * 更新提示消息
     */
    public void updateSucccess(String msg) {
        if (mMsgTv == null) {
            return;
        }
        mTitleTv.setText("成功");
        mStateImg.setImageResource(R.drawable.dialog_state_tip);
        if (!TextUtils.isEmpty(msg)) {
            mMsgTv.setText(msg);
        }
        mLeftBtn.setText("确认");
    }

    /**
     * 更新提示消息
     */
    public void updateError(String msg) {
        if (mMsgTv == null) {
            return;
        }
        mTitleTv.setText("错误");
        mStateImg.setImageResource(R.drawable.dialog_state_error);
        if (!TextUtils.isEmpty(msg)) {
            mMsgTv.setText(msg);
        }
        mLeftBtn.setText("返回");
    }


    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @OnClick(R.id.dialog_ll_btn_left)
    public void onLeftBtnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onLeftBtnClick(this);
        }
    }

    @OnClick(R.id.dialog_ll_btn_right)
    public void onRightBtnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onRightBtnClick(this);
        }
    }

    /**
     * 弹窗按键监听
     */
    public interface OnBtnClickListener {
        /**
         * 左按钮监听
         *
         * @param v button的view
         */
        void onLeftBtnClick(BasePopupView v);

        /**
         * 右按钮监听
         *
         * @param v button的view
         */
        void onRightBtnClick(BasePopupView v);
    }

    public void setOnClickListener(OnBtnClickListener onClickListener) {
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

    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
    }

    public void setBtnNum(int btnNum) {
        this.btnNum = btnNum;
    }

    //endregion

    /**
     * 对话框类型定义
     */
    public enum DialogType {
        /**
         * 信息弹窗
         */
        info,
        /**
         * 警告弹窗
         */
        warning,
        /**
         * 错误弹窗
         */
        error,
        /**
         * 询问弹窗
         */
        question,
        /**
         * 等待提示框
         */
        wait
    }

}
