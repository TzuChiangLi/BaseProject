package com.ftrend.progressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.ftrend.library.R;

/**
 * 自定义圆形进度条控件
 *
 * @author liziqiang@ftrend.cn
 */
public class ProgressView extends FrameLayout {
    private Context mContext;
    private int progress;
    public static int maxProgress = 100;
    public int FLAG = -1;
    private ImageView mBigestCircle;
    private ImageView mBigCircle;
    private ImageView mMidCircle;
    private ImageView mSmallCircle;
    private ImageView mSmallestCircle;

    private Animation animation_bigest;
    private Animation animation_big;
    private Animation animation_mid;
    private Animation animation_small;
    private Animation animation_smallest;

    private ObjectAnimator scaleXImg;
    private ObjectAnimator scaleYImg;
    private ObjectAnimator alphaImg;


    private TextView mProgressTv;
    private String defualtText;
    private int originColor = -1, finishColor = -1, errorColor = -1;

    public ProgressView(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        //获取属性
        defualtText = typedArray.getString(R.styleable.ProgressView_defualtText);
        originColor = typedArray.getColor(R.styleable.ProgressView_originColor, getResources().getColor(R.color.progress_load_blue));
        finishColor = typedArray.getColor(R.styleable.ProgressView_finishColor, getResources().getColor(R.color.progress_load_orange));
        errorColor = typedArray.getColor(R.styleable.ProgressView_errorColor, getResources().getColor(R.color.progress_load_red));
        //回收变量
        typedArray.recycle();
        initView();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    public void initView() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.CENTER;
        lp.width = ConvertUtils.dp2px(140);
        lp.height = ConvertUtils.dp2px(140);
        mBigestCircle = new ImageView(mContext);
        mBigCircle = new ImageView(mContext);
        mMidCircle = new ImageView(mContext);
        mSmallCircle = new ImageView(mContext);
        mSmallestCircle = new ImageView(mContext);

        mProgressTv = new TextView(mContext);
        mProgressTv.setText(defualtText == null ? "0%" : defualtText);
        mProgressTv.setTextColor(Color.WHITE);
        mProgressTv.setTypeface(Typeface.DEFAULT_BOLD);
        mProgressTv.setGravity(Gravity.CENTER);
        mProgressTv.setTextSize(16);
        mProgressTv.setLayoutParams(lp);

        mBigestCircle.setLayoutParams(lp);
        mBigCircle.setLayoutParams(lp);
        mMidCircle.setLayoutParams(lp);
        mSmallCircle.setLayoutParams(lp);
        mSmallestCircle.setLayoutParams(lp);

        mBigestCircle.setBackgroundResource(R.drawable.progress_load_view);
        mBigCircle.setBackgroundResource(R.drawable.progress_load_view);
        mMidCircle.setBackgroundResource(R.drawable.progress_load_view);
        mSmallCircle.setBackgroundResource(R.drawable.progress_load_view);
        mSmallestCircle.setBackgroundResource(R.drawable.progress_load_view);

        if (originColor != -1) {
            changeColor(originColor, mBigestCircle);
            changeColor(originColor, mBigCircle);
            changeColor(originColor, mMidCircle);
            changeColor(originColor, mSmallCircle);
            changeColor(originColor, mSmallestCircle);
        }

        initViewScale(mBigestCircle);
        initViewScale(mBigCircle);
        initViewScale(mMidCircle);
        initViewScale(mSmallCircle);
        initViewScale(mSmallestCircle);

        addView(mBigestCircle);
        addView(mBigCircle);
        addView(mMidCircle);
        addView(mSmallCircle);
        addView(mSmallestCircle);
        addView(mProgressTv);
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        mProgressTv.setText(String.format("%d%%", progress));
        mProgressTv.postInvalidate();
    }

    public void start() {
        FLAG = 0;
        animation_bigest = AnimationUtils.loadAnimation(mContext, R.anim.progress_load_anim);
        animation_big = AnimationUtils.loadAnimation(mContext, R.anim.progress_load_anim);
        animation_mid = AnimationUtils.loadAnimation(mContext, R.anim.progress_load_anim);
        animation_small = AnimationUtils.loadAnimation(mContext, R.anim.progress_load_anim);
        animation_smallest = AnimationUtils.loadAnimation(mContext, R.anim.progress_load_anim);

        animation_bigest.setRepeatMode(Animation.INFINITE);
        animation_big.setRepeatMode(Animation.INFINITE);
        animation_mid.setRepeatMode(Animation.INFINITE);
        animation_small.setRepeatMode(Animation.INFINITE);
        animation_smallest.setRepeatMode(Animation.INFINITE);

        if (mBigestCircle.getScaleX() != 1.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(beforeBeginScaleX(mBigestCircle), beforeBeginScaleY(mBigestCircle),
                    beforeBeginScaleX(mBigCircle), beforeBeginScaleY(mBigCircle),
                    beforeBeginScaleX(mMidCircle), beforeBeginScaleY(mMidCircle),
                    beforeBeginScaleX(mSmallestCircle), beforeBeginScaleY(mSmallestCircle),
                    beforeBeginScaleX(mSmallCircle), beforeBeginScaleY(mSmallCircle));
            animatorSet.setDuration(800).start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBigestCircle.startAnimation(animation_bigest);

                    animation_big.setStartOffset(300);
                    mBigCircle.startAnimation(animation_big);

                    animation_mid.setStartOffset(600);
                    mMidCircle.startAnimation(animation_mid);

                    animation_small.setStartOffset(900);
                    mSmallCircle.startAnimation(animation_small);

                    animation_smallest.setStartOffset(1200);
                    mSmallCircle.startAnimation(animation_smallest);

                }
            });
        } else {
            mBigestCircle.startAnimation(animation_bigest);

            animation_big.setStartOffset(300);
            mBigCircle.startAnimation(animation_big);

            animation_mid.setStartOffset(600);
            mMidCircle.startAnimation(animation_mid);

            animation_small.setStartOffset(900);
            mSmallCircle.startAnimation(animation_small);

            animation_smallest.setStartOffset(1200);
            mSmallCircle.startAnimation(animation_smallest);

        }
    }


    /**
     * @param finish 是否完成，true表示完成，false表示中断
     */
    public void restore(final boolean finish) {
        FLAG = finish ? 1 : -1;
        AnimatorSet bigest = finishAnimator(mBigestCircle);
        AnimatorSet big = finishAnimator(mBigCircle);
        AnimatorSet mid = finishAnimator(mMidCircle);
        AnimatorSet small = finishAnimator(mSmallCircle);
        AnimatorSet smallest = finishAnimator(mSmallestCircle);
        if (!finish) {
            changeColor(errorColor, mBigestCircle);
            changeColor(errorColor, mBigCircle);
            changeColor(errorColor, mMidCircle);
            changeColor(errorColor, mSmallCircle);
            changeColor(errorColor, mSmallestCircle);
            mProgressTv.setText("取消");
        }

        bigest.start();
        big.start();
        mid.start();
        small.start();
        smallest.start();
        smallest.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimatorSet animatorSet = hide(ProgressView.this);
                        animatorSet.start();
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (finish) {
                                    //动画消失后移除本View
                                    changeColor(finishColor, mBigestCircle);
                                    changeColor(finishColor, mBigCircle);
                                    changeColor(finishColor, mMidCircle);
                                    changeColor(finishColor, mSmallCircle);
                                    changeColor(finishColor, mSmallestCircle);
                                    mProgressTv.setText("进入系统");
                                    ObjectAnimator.ofFloat(ProgressView.this, "alpha", 0f, 1f).setDuration(500).start();
                                } else {
                                    changeColor(originColor, mBigestCircle);
                                    changeColor(originColor, mBigCircle);
                                    changeColor(originColor, mMidCircle);
                                    changeColor(originColor, mSmallCircle);
                                    changeColor(originColor, mSmallestCircle);
                                    mProgressTv.setText("开始");
                                    ObjectAnimator.ofFloat(ProgressView.this, "alpha", 0f, 1f).setDuration(500).start();
                                }
                            }
                        });
                    }
                }, 200);
            }

        });


    }


    private void changeColor(int color, View view) {
        GradientDrawable myGrad = (GradientDrawable) view.getBackground();
        myGrad.setColor(color);
    }

    private AnimatorSet hide(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 0f);
        animatorSet.playTogether(alpha);
        animatorSet.setDuration(500);
        return animatorSet;
    }

    private AnimatorSet finishAnimator(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        scaleXImg = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0.5f);
        scaleYImg = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleX(), 0.5f);
        alphaImg = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 1f);
        animatorSet.playTogether(scaleXImg, scaleYImg, alphaImg);
        animatorSet.setDuration(500);
        return animatorSet;
    }


    private ObjectAnimator beforeBeginScaleX(View view) {
        return ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1.1f, 1f);
    }

    private ObjectAnimator beforeBeginScaleY(View view) {
        return ObjectAnimator.ofFloat(view, "scaleY", view.getScaleX(), 1.1f, 1f);
    }


    private void initViewScale(View view) {
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
    }


    //    /**
//     * 获取模式，一共有三种模式
//     * MeasureSpec.AT_MOST
//     * MeasureSpec.EXACTYLY
//     * MeasureSpec.UNSPECIFIED
//     *
//     * @param measureSpec 输入父类建议的测量值
//     * @return 返回模式
//     */
//    private int getMode(int measureSpec) {
//        return (measureSpec & MODE_MASK);
//    }
//
//    /**
//     * 获取size值
//     *
//     * @param measureSpec 输入父类建议的测量值
//     * @return 返回真正的值
//     */
//    private int getSize(int measureSpec) {
//        return (measureSpec & ~MODE_MASK);
//    }

}
