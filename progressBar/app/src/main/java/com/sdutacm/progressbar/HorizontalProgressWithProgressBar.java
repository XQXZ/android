package com.sdutacm.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by bummer on 2017/8/3.
 */

public class HorizontalProgressWithProgressBar extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10; //SP
    private static final int DEFAULT_TEXT_COLOR = 0Xfffc00d1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2; //dp
    private static final int DEFAULT_TEXT_OFFSET = 10; //dp


    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnReachColor = DEFAULT_COLOR_UNREACH;
    protected int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mReachHeight = DEFAULT_HEIGHT_REACH;
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();

    private int mRealWidth;

    public HorizontalProgressWithProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int mTextSize) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttras(attrs);
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void obtainStyledAttras(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressWithProgressBar);
        mTextSize = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_text_size,
                mTextSize);
        mTextColor = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_text_color,
                mTextColor);
        mTextOffset = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_text_offset,
                mTextOffset);
        mUnReachColor = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_unreach_color,
                mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_unreach_height,
                mUnReachColor);
        mReachColor = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_reach_color,
                mReachColor);
        mReachHeight = (int) ta.getDimension(
                R.styleable.HorizontalProgressWithProgressBar_progress_reach_height,
                mReachHeight);

        ta.recycle();

        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal,height);  //确定了控件的宽度和高度

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight(); //获取测量的值
    }

    public HorizontalProgressWithProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if(mode == MeasureSpec.EXACTLY){ //给的是精确值,直接返回精确值
            result = size;
        }else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent()); //字体高度

            //测量控件的高度
            result = getPaddingTop()  //上边距
                    + getPaddingBottom() //下边距
                    + Math.max(Math.max(mReachHeight,mUnReachHeight),
                    //求mReachHeight,mUnReachHeight，
                    // textHeight三者的最大值
                    Math.abs(textHeight));
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(getPaddingLeft(),getHeight()/2);

        boolean noNeedUnRech = false;  //判断右边进度条是否要绘制
        //draw reach bar
        float radio = getProgress()*1.0f/getMax();
        String text = getProgress() +"%";
        int textWidth = (int) mPaint.measureText(text); //测量文本的宽度
        float progressX = radio*mRealWidth;
        if(progressX + textWidth > mRealWidth){
            progressX = mRealWidth - textWidth;
            noNeedUnRech = true;
        }


        float endX = radio*mRealWidth - mTextOffset /2;
        if(endX > 0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX,0,mPaint);
        }
        //draw Text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent()+mPaint.ascent())/2);
        canvas.drawText(text,progressX,y,mPaint);

        //draw unreach bar
        if(!noNeedUnRech){ //需要绘制
            float start = progressX + mTextOffset /2 +textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }


        canvas.restore();
    }

    //一个参数构造方法
    public HorizontalProgressWithProgressBar(Context context) {
        super(context, null);
    }

    //两个参数构造方法
    public HorizontalProgressWithProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                getResources().getDisplayMetrics());
    }

}
