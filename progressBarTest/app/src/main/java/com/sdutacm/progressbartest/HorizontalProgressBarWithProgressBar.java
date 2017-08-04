package com.sdutacm.progressbartest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by bummer on 2017/8/4.
 */

public class HorizontalProgressBarWithProgressBar extends ProgressBar {

    private static final int DEFAULT_COLOR_REACH = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HRIGHT_UNREACH =2;   //dp
    private static final int DEFAULT_HEIGHT_REACH = 2;  //dp
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_TEXT_SIZE = 10;  //sp
    private static final int DEFAULT_TEXT_OFF_SET = 10;  //dp  文本偏移量


    //字体大小
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    //字体颜色
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    //未达到的进度条条的高度
    protected int mUnreachHeight = dp2px(DEFAULT_HRIGHT_UNREACH);
    //达到的进度条的高度
    protected int mReachHeight =dp2px(DEFAULT_HEIGHT_REACH);
    //达到的进度条颜色
    protected int mReachColor = DEFAULT_COLOR_REACH;
    //味道大进度条的颜色
    protected int mUnreachColor = DEFAULT_COLOR_UNREACH;
    //绘制进度偏移
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFF_SET);
    //判断是否绘制进度条文字
    protected  boolean mIfDrawText = true;
    //设置文字可见性
    protected static final int VISIABLE = 0;


    //绘制画笔
    protected Paint mPaint = new Paint();
    //获得最终宽度
    protected int mRealWidth;

    public HorizontalProgressBarWithProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置水平进度条边缘淡出淡进
        setHorizontalFadingEdgeEnabled(true);
        //设置样式属性
        obtainStyleAttributes(attrs);

        //设置画笔字体大小
        mPaint.setTextSize(mTextSize);
        //设置画笔颜色
        mPaint.setColor(mTextColor);

    }

    //获得样式属性
    private void obtainStyleAttributes(AttributeSet attrs) {
        //对自定义属性进行初始化操作
        final TypedArray attributes = getContext().obtainStyledAttributes(R.styleable.HorizontalProgressBarWithProgressBar);
        mTextColor = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_text_color,mTextColor);
        mTextSize = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_text_size,mTextSize);
        mReachColor = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_color_reach,mReachColor);
        mUnreachColor = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_color_unreach,mUnreachColor);
        mReachHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_height_reach,mReachHeight);
        mUnreachHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_height_unreach,mUnreachHeight);
        mTextOffset = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_text_offset,mTextOffset);
        int textVisiable = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithProgressBar_progress_text_visibility,VISIABLE);
        if(textVisiable ==VISIABLE){
            mIfDrawText = false;
        }
        attributes.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         *   获得高度的模式值
         */
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal,height); //确定控件的宽度和高度

    }
    //自测控件高度
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode != MeasureSpec.EXACTLY){  //自己测量尺寸
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());  //字体高度
            //测量控件的高度
            result = getPaddingTop() //上边距
            +getPaddingBottom() //下边距
            +Math.max(Math.max(mReachHeight,mUnreachHeight),Math.abs(textHeight));
            if(heightMode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
        }else { //给的是精确值，直接返回精确值
            result = size;
        }
        return result;
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //设置原点位置， 以（getPaddingLeft,getHeight/2）为原点(0,0)位置为原点
        canvas.translate(getPaddingLeft(),getHeight()/2);
        boolean Needbg = false;  //是否需要绘制进度条
        //设置当前进度和总进度的比值
        float radio = getProgress()*0.1f /getMax();
        //已达到的宽度  = 当前的进度比例*总宽度
        float progressBarX = (int)(radio*mRealWidth);
        //绘制文本
        String text = getProgress() + "%";

        //拿到字体的宽度和高度
        float textWidth = mPaint.measureText(text);
        float textHeight = mPaint.measureText(text);

        //如果达到最后，则未达到的进度条不需要绘制
        if(progressBarX + textWidth > mRealWidth){
            progressBarX = mRealWidth - textWidth;
            Needbg = true;
        }

        //绘制已达到的进度
        float endX = progressBarX - mTextOffset/2;
        if(endX > 0){
            //绘制一条线
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            //从（0，0） 开始绘制到 (endx,0)结束 //画笔为mPaint
            canvas.drawLine(0,0,endX,0,mPaint);
        }
        //绘制文本
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent()+mPaint.ascent())/2);
        canvas.drawText(text,progressBarX,y,mPaint);

        //绘制未达到的进度
        if(!Needbg){
            float start = progressBarX + mTextOffset/2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }


        canvas.restore();
    }


    public HorizontalProgressBarWithProgressBar(Context context) {
        super(context,null);
    }

    public HorizontalProgressBarWithProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0); //调用自身的三个参数的构造方法
    }

    //类型转换
    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal
                ,getResources().getDisplayMetrics());
    }
    //类型转换
    protected int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,
                getResources().getDisplayMetrics());
    }







    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w-getPaddingRight()-getPaddingLeft();
    }


}
