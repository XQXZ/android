package com.sdutacm.imooc_wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bummer on 2017/8/16.
 */

public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mLineHeight; //减少精度的丢失
    private int MAX_LINE = 20;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;    //引入棋子的图片
    private Bitmap mBlackPiece;

    //引入比例,让棋子的大小是行高的1/4
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;
    //白棋子先手,当前轮到白棋
    private boolean mIsWhite = true;
    //存储棋子的集合
    private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private ArrayList<Point> firstStep = new ArrayList<>();
    private ArrayList<Point> secondStep = new ArrayList<>();
    private ArrayList<Point> thirdStep = new ArrayList<>();
    private ArrayList<Point> forthStep = new ArrayList<>();
    private ArrayList<Point> fifthStep = new ArrayList<>();
    private ArrayList<Point> sixthStep = new ArrayList<>();

    private ArrayList<Point> firstStepTemp = new ArrayList<>();
    private ArrayList<Point> secondStepTemp = new ArrayList<>();
    private ArrayList<Point> thirdStepTemp = new ArrayList<>();
    private ArrayList<Point> forthStepTemp = new ArrayList<>();
    private ArrayList<Point> fifthStepTemp = new ArrayList<>();
    private ArrayList<Point> sixthStepTemp = new ArrayList<>();
    
    private boolean mIsGameOver;  //判断游戏是否结束

    private boolean OPEN_AI_MODE = false;
    private boolean mIsWhiteRobot = false; //默认黑棋先行
    private boolean Key = false;

    private boolean mIsWhiteWinner; //判断白子是否为赢家,值为真就是白子为赢家,值为false,黑子为赢家

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置背景为透明红色
        setBackgroundColor(0xffcccccc);
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setDither(true); //绘制棋子
        mPaint.setStyle(Paint.Style.STROKE); //画线

        //初始化棋子图片
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b2);

    }

    //自定义测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(heightSize, widthSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    /**
     * 当你的宽高确定了，发生改变了，会回调这个方法，
     * 对尺寸变量进行初始化
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE; //获得行高

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        //修改图的尺寸
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    //编写点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //游戏结束,对点击事件不感兴趣
        if (mIsGameOver) return false;

        //对点击事件感兴趣,表明自己的态度
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            //将x,y封装成一个point
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            //判断当前这个点是否已经有棋子
            if (mIsWhite) {
                //判断是白棋子
                if (OPEN_AI_MODE) {
                    if (!mIsWhiteRobot) {
                        mWhiteArray.add(p);
                    } else {
                        //黑棋人工智能
                       // blackFirst();
                       // whiteFirst();
                        clearQueue();
                        if (mIsWhiteRobot) { //如果当前应当白棋先走
                            whiteFirst();
                        } else {
                            blackFirst();
                        }
                    }
                } else {
                    mWhiteArray.add(p);
                }

            } else if (!mIsWhite) {
                if (OPEN_AI_MODE) {
                    if (mIsWhiteRobot) {
                        mBlackArray.add(p);
                    } else {
                        //白棋人工智能
                     //   whiteFirst();
                       // blackFirst();
                        clearQueue();
                        if (!mIsWhiteRobot) { //如果当前应当白棋先走
                            whiteFirst();
                        } else {
                            blackFirst();
                        }
                    }
                } else {
                    mBlackArray.add(p);
                }
            }
            //请求重新绘制
            invalidate();
            //改变一下mIsWhite的值
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private void whiteFirst() {
        clearQueue();
        for (Point point : mWhiteArray) {
            int x = point.x;
            int y = point.y;
            whiteFirstCheckHorizontalStategy(x, y, mWhiteArray); //传入当前棋子的位置, 白棋集合,黑棋集合
            whiteFirstcheckVerticalStategy(x, y, mWhiteArray);
            whiteFirstcheckLeftDiagonalStategy(x, y, mWhiteArray);
            whiteFirstcheckRightDiagonalStategy(x, y, mWhiteArray);

        }
        Point ppp = getPoint();
        mWhiteArray.add(ppp);
    }

    private void blackFirst() {
        clearQueue();
        for (Point point : mBlackArray) {
            int x = point.x;
            int y = point.y;
            blackFirstCheckHorizontalStategy(x, y, mBlackArray); //传入当前棋子的位置, 白棋集合,黑棋集合
            blackFirstcheckVerticalStategy(x, y, mBlackArray);
            blackFirstcheckLeftDiagonalStategy(x, y, mBlackArray);
            blackFirstcheckRightDiagonalStategy(x, y, mBlackArray);
        }
        Point ppp = getPoint();
        mBlackArray.add(ppp);
    }


    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;

        }
        return false;
    }

    /**
     * 判断x,y位置是否横向有相邻的五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
             return true;
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        //绘制白子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        //绘制黑子
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            //绘制横向——————————————————————————————————————————
            int startX = (int) (lineHeight / 2); //起始坐标
            int endX = (int) (w - lineHeight / 2); //终点坐标

            int y = (int) ((0.5 + i) * lineHeight);  //y的坐标、
            //绘制横向——————————————————————————————————————————
            canvas.drawLine(startX, y, endX, y, mPaint);
            //绘制纵向——————————————————————————————————————————
            canvas.drawLine(y, startX, y, endX, mPaint);

        }
    }

    //白子 黑子棋盘 定义Key
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WRITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    /**
     * 对棋子的位置进行存储
     *
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WRITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    public void start() {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsGameOver = false;
        OPEN_AI_MODE = false;
        mIsWhiteRobot = false; //默认黑棋先行
        Key = false;
        clearQueue();
        clearQueueTemp();
        invalidate();
    }

    /**
     * 悔棋
     */
    public void takeBack() {
        if (mBlackArray.size() > 0 && mWhiteArray.size() > 0) {
            if (!mIsGameOver) {
                mWhiteArray.remove(mWhiteArray.size() - 1);
                mBlackArray.remove(mBlackArray.size() - 1);
                invalidate();
            } else {
                Toast.makeText(getContext(), "游戏已经结束,再来一局吧!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "当前无路可退了呢,亲", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提示功能
     */
    public void hint() {

        if (!mIsGameOver) {
            hintSmartStep(mWhiteArray, mBlackArray);
        } else {
            Toast.makeText(getContext(), "游戏已经结束,再来一局吧!", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearQueue() {
        fifthStep.clear();
        sixthStep.clear();
        forthStep.clear();
        thirdStep.clear();
        secondStep.clear();
        firstStep.clear();
    }
    public void clearQueueTemp() {
        fifthStepTemp.clear();
        sixthStepTemp.clear();
        forthStepTemp.clear();
        thirdStepTemp.clear();
        secondStepTemp.clear();
        firstStepTemp.clear();
    }

    public Point getPoint() {
        if (firstStep.size() > 0) {
            return firstStep.get(firstStep.size() - 1);
        } else if (secondStep.size() > 0) {
            return secondStep.get(secondStep.size() - 1);
        } else if (thirdStep.size() > 0) {
            return thirdStep.get(thirdStep.size() - 1);
        } else if (forthStep.size() > 0) {
            return forthStep.get(forthStep.size() - 1);
        } else if (fifthStep.size() > 0) {
            return fifthStep.get(fifthStep.size() - 1);
        } else if (sixthStep.size() > 0) {
            return sixthStep.get(sixthStep.size() - 1);
        }
        return null;
    }

    /**
     * 提示走下一步
     */
    private void hintSmartStep(List<Point> wPoints, List<Point> bPoints) {
        if (mIsWhite) { //如果当前应当白棋先走
            whiteFirst();
        } else {
            blackFirst();
        }
        mIsWhite = !mIsWhite;
        invalidate();
    }

    public void ai() {
        if (!mIsGameOver) {
            if (!Key) { //确定哪一方是电脑
                Key = true;
                if (mIsWhite) {
                    mIsWhiteRobot = true;
                    Log.d("mIsWhiteRobot", "mIsWhiteRobot is the " + mIsWhiteRobot);
                } else {
                    mIsWhiteRobot = false;
                    Log.d("mIsWhiteRobot", "mIsWhiteRobot is the " + mIsWhiteRobot);
                }
                OPEN_AI_MODE = true;
                String firstRobot;
                if(mIsWhiteRobot){
                    firstRobot = "白棋";
                }else {
                    firstRobot = "黑棋";
                }
                Toast.makeText(getContext(),"人机对战已经打开,电脑执"+firstRobot,Toast.LENGTH_SHORT).show();
            }else if(OPEN_AI_MODE){
                OPEN_AI_MODE = !OPEN_AI_MODE;
                Toast.makeText(getContext(),"关闭人机对战",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "游戏已经结束,再来一局吧!", Toast.LENGTH_SHORT).show();
        }
    }

    private void whiteFirstcheckRightDiagonalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y - i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x - i, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x - i, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y - i)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y - i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y + i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x + i, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y + i)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y + i)));
                        break;
                    }
                }
            }

        }
    }

    private void blackFirstcheckRightDiagonalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y - i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x - i, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x - i, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y - i)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y - i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y + i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x + i, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y + i)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y + i)));
                        break;
                    }
                }
            }

        }
    }

    private void blackFirstcheckLeftDiagonalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y + i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x - i, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x - i, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y + i)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y + i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y - i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x + i, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y - i)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y - i)));
                        break;
                    }
                }
            }

        }
    }

    private void whiteFirstcheckLeftDiagonalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y + i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x - i, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x - i, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y + i)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y + i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y - i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x + i, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y - i)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y - i)));
                        break;
                    }
                }
            }

        }
    }

    private void blackFirstcheckVerticalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y - i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y - i)));
                    } else {
                        if (!points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y - i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y + i))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y + i)));
                    } else {
                        if (!points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y + i)));
                        break;
                    }
                }
            }

        }

    }

    private void whiteFirstcheckVerticalStategy(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y - i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x, y - i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x, y - i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x, y - i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x, y - i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x, y - i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x, y - i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y - i)));
                    } else {
                        if (!points.contains(new Point(x, y - i - 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y - i)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x, y + i))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x, y + i))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x, y + i));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x, y + i)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x, y + i));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x, y + i)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x, y + i));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y + i)));
                    } else {
                        if (!points.contains(new Point(x, y + i + 1))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x, y + i)));
                        break;
                    }
                }
            }

        }


    }

    private void blackFirstCheckHorizontalStategy(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x - i, y))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x - i, y));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y))) { //一直是白棋
                count++;
            } else {
                if (mWhiteArray.contains(new Point(x + i, y))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y)));
                        break;
                    }
                }
            }

        }

    }

    private void whiteFirstCheckHorizontalStategy(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x - i, y))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x - i, y))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 2
                            secondStep.add(new Point(new Point(x - i, y)));
                        } else
                            //等级 1
                            firstStep.add(new Point(x - i, y));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x - i, y));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x - i, y)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x - i, y));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y)));
                    } else {
                        if (!points.contains(new Point(x - i - 1, y))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x - i, y)));
                        break;
                    }
                }
            }


        }
        count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE-1; i++) {
            if (points.contains(new Point(x + i, y))) { //一直是白棋
                count++;
            } else {
                if (mBlackArray.contains(new Point(x + i, y))) { //遇到黑棋,结束遍历
                    break;
                } else {  //点Piont(x-i,y)为空
                    if (count == 3) {  //已经有四棋子在一块了
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 1
                            firstStep.add(new Point(x + i, y));
                        } else
                            //等级 2
                            secondStep.add(new Point(new Point(x + i, y)));
                    } else if (count == 2) { //已经有三棋子在一块了
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 3
                            thirdStep.add(new Point(x + i, y));
                        } else
                            //等级 4
                            forthStep.add(new Point(new Point(x + i, y)));
                    } else if (count == 1) {
                        if (points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //等级 5
                            fifthStep.add(new Point(x + i, y));
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y)));
                    } else {
                        if (!points.contains(new Point(x + i + 1, y))) { //隔了一个空白,空白对面还是自己人
                            //遇到黑子或者撞墙,结束
                            break;
                        } else
                            //等级 6
                            sixthStep.add(new Point(new Point(x + i, y)));
                        break;
                    }
                }
            }

        }
    }

    /**
     * 恢复棋盘
     *
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WRITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_WRITE_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
