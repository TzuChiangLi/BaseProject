package com.ftrend.progressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

/**
 * 自定义圆形进度条控件
 *
 * @author liziqiang@ftrend.cn
 */
public class ProgressView extends View {
    //设置默认最大进度
    public int max = 100;
    //设置默认初始化进度
    public int progress = 0;
    //设置View默认的大小
    private int mDefaultWidth = dp2px(60);
    private int mDefaultPadding = dp2px(10);

    // 定义设置进度圆的默认半径
    private int mRadius = mDefaultWidth / 2;
    //圆环的默认宽度
    private int mProgressBarHeight = dp2px(5);
    //声明初始化一个画笔
    private Paint mPaint = new Paint();
    //设置未加载进度的默认颜色
    private int mUnReachedBarColor = 0xffe6e6e6;
    //设置已加载进度的默认颜色
    private int mReachedBarColor = 0xff89cc99;

    //测量后的实际view的大小
    private int mMeasureWidth;
    private int mMeasureHeight;
    private RectF mRectF;
    private int MODE_MASK = 0xc000000;


    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //当宽高为match_parent时，传来的模式为EXACTLY
        //当宽高为wrap_content时，传来的面膜是为AT_MOST（未确定）
        //未确定时，需要重新计算他的宽高属性
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {


        }
        if (getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {


        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取显示进度的文字指示
        String text = progress + "%";
        //获取显示进度的文字的宽与高
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.save();

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        canvas.drawCircle(190, 200, 150, mPaint);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        canvas.drawCircle(190, 200, 100, mPaint);
    }

    /**
     * 获取模式，一共有三种模式
     * MeasureSpec.AT_MOST
     * MeasureSpec.EXACTYLY
     * MeasureSpec.UNSPECIFIED
     *
     * @param measureSpec 输入父类建议的测量值
     * @return 返回模式
     */
    private int getMode(int measureSpec) {
        return (measureSpec & MODE_MASK);
    }

    /**
     * 获取size值
     *
     * @param measureSpec 输入父类建议的测量值
     * @return 返回真正的值
     */
    private int getSize(int measureSpec) {
        return (measureSpec & ~MODE_MASK);
    }

}
