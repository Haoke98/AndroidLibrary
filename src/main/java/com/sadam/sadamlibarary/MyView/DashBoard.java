package com.sadam.sadamlibarary.MyView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.text.DecimalFormat;

/**
 * 圆圈
 */
public class DashBoard extends View {
    private final static String TAG = "DashBoard";

    /**
     * View的宽高
     */
    private int mWidth;
    private int mHeight;

    /**
     * 指针指向的实时值
     * 画出实时值的画笔
     */
    private float mRealTimeValue = 7;
    private Paint realTimeValuePaint;
    private int mRealTimeTextSize = 250;
    private int mRealTimeValueColor = Color.LTGRAY;
    private int mRealTimeValueDistanse = 300;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");
    /**
     * 标题 可以自定义一些文字显示在 仪表盘的上面
     */
    private String title = "DashBoard";
    private Paint titlePaint;
    private int titileColor = Color.CYAN;
    private int titleTextSize = 150;
    private int titleDistanse = 100;
    /**
     * 小刻度数目
     * 每两个小刻度之间的角度
     * 大刻度数目
     * 每两个时大刻度之间的角度
     * 每两个大刻度之间的小刻度的数目
     */
    private int sliceCount = 126;
    private float valueOfPerSlice = 1.0f;
    private float degressPerSlice;
    private int bigSliceCount = 10;
    private float degressPerBigSlice = 360.0f / (float) bigSliceCount;
    private int smallSliceCountInOneBigSlice;
    /**
     * 外圆圈画笔
     * 圆心坐标
     * 外圆圈的半径
     */
    private Paint circlePaint;
    private int circleColor = Color.BLUE;
    private float circleX, circleY;
    private float mRealTimeValueX = getCircleX();
    private float titleX = getCircleX();
    private float circleRadius;
    private float mRealTimeValueY = getCircleY() + getCircleRadius() + 20;
    private float titleY = getCircleY() - getCircleRadius() - 20;
    /**
     * 小刻度画笔
     * 小刻度颜色
     * 小刻度长度
     */
    private Paint slicePaint;
    private int sliceColor = Color.RED;
    private int lenOfSlice = 30;
    /**
     * 大刻度画笔
     * 大刻度颜色
     * 大刻度长度
     */
    private Paint bigSlicePaint;
    private int bigSliceColor = Color.YELLOW;
    private int lenOfBigSlice = 60;
    /**
     * 小(普通)数字画笔
     * 小数字颜色
     * 小数字文字大小
     */
    private Paint numberPaint;
    private int numColor = Color.GREEN;
    private int numTextSize = 30;
    /**
     * 大数字画笔
     */
    private Paint bigNumPaint;
    private int bigNumColor = Color.RED;
    private int bigNumTextSize = 2 * numTextSize;
    /**
     * 指针画笔
     * 指针的颜色
     * 指针的长度，按大外圈半径的倍数算
     */
    private Paint pointerPaint;
    private int pointerColor = Color.BLACK;
    private float lenOfPointerBy = 0.5f;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public DashBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*注意：在这里不能直接调用父类View类的getWidth()方法，因为它返回的是就这个我们想要的mWidth的值，可我们还没给它赋值*/
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mWidth = windowManager.getDefaultDisplay().getWidth();
        mHeight = windowManager.getDefaultDisplay().getHeight();
        /*初始化用到的所有画笔*/
        initPaint();
    }

    private float getCircleRadius() {
        return circleRadius;
    }

    private float getCircleX() {
        return circleX;
    }

    private float getCircleY() {
        return circleY;
    }

    private int getSliceCount() {
        return sliceCount;
    }

    private int getBigSliceCount() {
        return bigSliceCount;
    }

    public void setBigSliceCount(int bigSliceCount) {
        this.bigSliceCount = bigSliceCount;
        this.degressPerBigSlice = 360.0f / (float) bigSliceCount;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }
//    /**
//     * 分钟指针画笔
//     * 分针的颜色
//     * 分针的长度，按大外圈的半径的倍数
//     */
//    private Paint minutePointerPaint;
//    private int minutePointerColor = Color.MAGENTA;
//    private float lenOfMinutePointerBy = 0.3f;

    public void setTitleRealTimeValue(String title, float mRealTimeValue) {
        this.mRealTimeValue = mRealTimeValue / valueOfPerSlice;
        this.title = title;
        invalidate();
    }

    public void setSliceCount(int sliceCount, float valueOfPerSlice) {
        this.sliceCount = sliceCount;
        this.degressPerSlice = 360.0f / (float) getSliceCount();
        this.smallSliceCountInOneBigSlice = getSliceCount() / getBigSliceCount();
        this.valueOfPerSlice = valueOfPerSlice;
        invalidate();
    }

    public void setmRealTimeValue(float mRealTimeValue) {
        this.mRealTimeValue = mRealTimeValue / valueOfPerSlice;
        invalidate();
    }


    /**
     * 初始化要用到的所有画笔
     */
    private void initPaint() {
        /*初始化circlePaint（），外圆圈的画笔*/
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(15);
        /*小刻度的画笔*/
        slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slicePaint.setColor(sliceColor);
        slicePaint.setStrokeWidth(15);
        /*大刻度画笔*/
        bigSlicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigSlicePaint.setColor(bigSliceColor);
        bigSlicePaint.setStrokeWidth(20);
        /*小数字的画笔*/
        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(numColor);
        numberPaint.setStrokeWidth(5);
        numberPaint.setTextSize(numTextSize);
        /*大数字画笔*/
        bigNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigNumPaint.setColor(bigNumColor);
        bigNumPaint.setTextSize(bigNumTextSize);
        bigNumPaint.setStrokeWidth(5);
        /*指针的画笔*/
        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setColor(pointerColor);
        pointerPaint.setStrokeWidth(15);
        /*实时实际值的画笔*/
        realTimeValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        realTimeValuePaint.setColor(mRealTimeValueColor);
        realTimeValuePaint.setTextSize(mRealTimeTextSize);
        realTimeValuePaint.setStrokeWidth(10);
        /*标题的画笔*/
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(titileColor);
        titlePaint.setTextSize(titleTextSize);
        titlePaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*画外圆*/
        canvas.drawCircle(circleX, circleY, circleRadius, circlePaint);
        drawSlice(canvas);
        drawPointer(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth < mHeight) {
            /*圆的半径为View的宽度的一般再减9，防止贴边*/
            circleRadius = mWidth / 2 - 9;
        } else {
            circleRadius = mHeight / 2 - 9;
        }
        circleX = mWidth / 2;
        circleY = mHeight / 2;
    }

    /**
     * 画刻度
     * 在onDraw()内被调用才能实现，即需要 系统给onDraw()函数的画布cnavas
     */
    private void drawSlice(Canvas canvas) {
        for (int i = 0; i < sliceCount; i++) {
            /*区分整点和非整点*/
            if (i % smallSliceCountInOneBigSlice == 0) {
                String degree = String.valueOf((i * valueOfPerSlice));
                canvas.drawLine(circleX, circleY - circleRadius, circleX, circleY - circleRadius + lenOfBigSlice, bigSlicePaint);

                canvas.drawText(degree, circleX - bigNumPaint.measureText(degree) / 2, circleY - circleX + lenOfBigSlice + bigNumPaint.measureText(degree), bigNumPaint);

            } else {
                String degree = String.valueOf((int) (i * valueOfPerSlice));
                canvas.drawLine(circleX, circleY - circleRadius, circleX, circleY - circleRadius + lenOfSlice, slicePaint);
                canvas.drawText(degree, circleX - numberPaint.measureText(degree) / 2, circleY - circleRadius + lenOfSlice + numberPaint.measureText(degree), numberPaint);
            }
            /*通过旋转画布，简化坐标运算*/
            /*因为 360/24 = 15度  */               /*是让画布旋转,每旋转 degressPerSlice时 小刻度对应的位置 挨个转到外圆圈最顶点*/
            canvas.rotate(degressPerSlice, circleX, circleY);
        }
    }


    /**
     * 画指针
     *
     * @param canvas 画布，需要的是 系统传给onDraw()的画布
     */
    private void drawPointer(Canvas canvas) {
        /*画表头*/
        canvas.drawText(title, circleX - titlePaint.measureText(title) / 2, circleY - circleRadius - titleDistanse, titlePaint);
        /*画实时实际值*/

        String strValue = decimalFormat.format(mRealTimeValue * valueOfPerSlice);
        canvas.drawText(strValue, circleX - realTimeValuePaint.measureText(strValue) / 2, circleY + circleRadius + mRealTimeValueDistanse, realTimeValuePaint);

        /*把极坐标转换成直角坐标系*/
        /*因为安卓系统的坐标系与数学坐标系不同,安卓坐标系的x轴相反,所以加上270调整方向*/
        float stopX = (float) Math.cos(Math.toRadians(mRealTimeValue * degressPerSlice + 270)) * circleRadius * lenOfPointerBy;
        float stopY = (float) Math.sin(Math.toRadians(mRealTimeValue * degressPerSlice + 270)) * circleRadius * lenOfPointerBy;

        /*聚焦到画布的最最中心*/
        canvas.translate(circleX, circleY);
        canvas.drawLine(0, 0, stopX, stopY, pointerPaint);
    }
//    public void drawPointer(Canvas canvas) {
//        /*把已经画好的画布保存下来，后面恢复它与新画合并*/
//        canvas.save();
//
//        canvas.translate(mWidth / 2, mHeight / 2);
//        /*startX=0,startY=0说明从画布的当前位置（即mWidth/2,mHeight/2)开始朝向（mWidth/2+100,mHeight/2+100)的点画一段线段，长度为  根号下(100*100+100*100)约141  )*/
//        canvas.drawLine(0, 0, 100, 100, pointerPaint);
//        canvas.drawLine(0, 0, 100, 200, minutePointerPaint);
//        /*恢复之前保存好的图像，使得新旧图像合并*/
//        canvas.restore();
//    }

}
