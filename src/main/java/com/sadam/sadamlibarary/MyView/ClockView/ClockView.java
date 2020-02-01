package com.sadam.sadamlibarary.MyView.ClockView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 圆圈
 */
public class ClockView extends View {
    private final static String TAG = "CircularView";

    /**
     * View的宽高
     */
    private int mWidth;
    private int mHeight;

    private int second;
    private double minute = 30;
    private double hour = 7;
    /**
     * 分钟刻度数目
     * 每两个分钟刻度之间的角度
     * 时钟刻度数目
     * 每两个时钟刻度之间的角度
     */
    private int minuteScaleCount = 60;
    private int degressPerMinute = 360 / minuteScaleCount;
    private int hourScaleCount = 24;
    private int degressPerHour = 360 / hourScaleCount;
    private int bigSliceCount = 60;
//    private int smallSliceCountInOneBigSlice = smallSliceBigCount
    /**
     * 外圆圈画笔
     * 圆心坐标
     * 外圆圈的半径
     */
    private Paint circlePaint;
    private int circleColor = Color.BLUE;
    private float circleX, circleY;
    private float circleRadius;
    /**
     * 分钟刻度画笔
     */
    private Paint minuteScalePaint;
    private int minuteScaleColor = Color.RED;
    /**
     * 时钟刻度画笔
     */
    private Paint hourScalePaint;
    private int hourScaleColor = Color.YELLOW;
    /**
     * 数字画笔
     */
    private Paint numberPaint;
    private int numColor = Color.GREEN;
    private int numTextSize = 30;
    /**
     * 时针画笔
     * 时针的颜色
     * 时针的长度，按大外圈半径的倍数算
     */
    private Paint hourPointerPaint;
    private int hourPointerColor = Color.BLACK;
    private float lenOfHourPointerBy = 0.5f;
    /**
     * 分钟指针画笔
     * 分针的颜色
     * 分针的长度，按大外圈的半径的倍数
     */
    private Paint minutePointerPaint;
    private int minutePointerColor = Color.MAGENTA;
    private float lenOfMinutePointerBy = 0.3f;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                /*清屏，并重新调用onDraw()方法*/
                invalidate();
            }
        }
    };
    /**
     * 画布，从onDraw()方法哪里提取
     * 为后期的动态画指针作铺垫
     */
    private Canvas canvas;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*注意：在这里不能直接调用父类View类的getWidth()方法，因为它返回的是就这个我们想要的mWidth的值，可我们还没给它赋值*/
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mWidth = windowManager.getDefaultDisplay().getWidth();
        mHeight = windowManager.getDefaultDisplay().getHeight();
        /*初始化用到的所有画笔*/
        initPaint();
    }

    public void setMinute(int minute) {
        this.minute = minute;
        invalidate();
    }

    public void setHour(float hour) {
        this.hour = hour;
        invalidate();
    }

    public void setHourMinute(float hour, int minute) {
        setMinute(minute);
        setHour(hour);
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
        /*分钟刻度的画笔*/
        minuteScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minuteScalePaint.setColor(minuteScaleColor);
        minuteScalePaint.setStrokeWidth(15);
        /*时钟刻度画笔*/
        hourScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourScalePaint.setColor(hourScaleColor);
        hourScalePaint.setStrokeWidth(20);
        /*数字的画笔*/
        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(numColor);
        numberPaint.setStrokeWidth(5);
        numberPaint.setTextSize(numTextSize);
        /*分针的画笔*/
        minutePointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minutePointerPaint.setColor(minutePointerColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(15);
        /*时针的画笔*/
        hourPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourPointerPaint.setColor(hourPointerColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*提取画布*/
        this.canvas = canvas;
        /*画外圆*/
        canvas.drawCircle(circleX, circleY, circleRadius, circlePaint);
        drawStripe(canvas);
        drawPointer(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        mWidth = getMeasuredWidth();
//        mHeight = getMinimumHeight();
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
    public void drawStripe(Canvas canvas) {
        for (int i = 0; i < hourScaleCount; i++) {
            String degree = String.valueOf(i);
            /*区分整点和非整点*/
            if (i == 0 || i == 6 || i == 12 || i == 18) {

                canvas.drawLine(circleX, circleY - circleX, circleX, circleY - circleX + 60, hourScalePaint);

                canvas.drawText(degree, circleX - numberPaint.measureText(degree), circleY - circleX + 90, numberPaint);

            } else {
                canvas.drawLine(circleX, circleY - circleX, circleX, circleY - circleX + 30, minuteScalePaint);
                canvas.drawText(degree, circleX - numberPaint.measureText(degree), circleY - circleX + 60, numberPaint);
            }
            /*通过旋转画布，简化坐标运算*/
            /*因为 360/24 = 15度  */
            canvas.rotate(degressPerHour, circleX, circleY);
        }
    }

    public void showPointer() {
        this.drawPointer(this.canvas);
        Log.d(TAG, "CircularView");
        invalidate();
    }

    /**
     * 画指针
     *
     * @param canvas 画布，需要的是 系统传给onDraw()的画布
     */
    public void drawPointer(Canvas canvas) {
        /*把极坐标转换成直角坐标系*/
        /*因为安卓系统的坐标系与数学坐标系不同,安卓坐标系的x轴相反,所以加上270调整方向*/
        canvas.translate(circleX, circleY);
        float hourX = (float) Math.cos(Math.toRadians(hour * degressPerHour + 270)) * circleRadius * lenOfHourPointerBy;
        float hourY = (float) Math.sin(Math.toRadians(hour * degressPerHour + 270)) * circleRadius * lenOfHourPointerBy;
        float minuteX = (float) Math.cos(Math.toRadians(minute * degressPerMinute + 270)) * circleRadius * lenOfMinutePointerBy;
        float minuteY = (float) Math.sin(Math.toRadians(minute * degressPerMinute + 270)) * circleRadius * lenOfMinutePointerBy;
        Log.d(TAG, hourX + " " + hourY + " " + minuteX + " " + minuteY);
        canvas.drawLine(0, 0, hourX, hourY, hourPointerPaint);
        canvas.drawLine(0, 0, minuteX, minuteY, minutePointerPaint);
    }
//    public void drawPointer(Canvas canvas) {
//        /*把已经画好的画布保存下来，后面恢复它与新画合并*/
//        canvas.save();
//        /*聚焦到画布的最最中心*/
//        canvas.translate(mWidth / 2, mHeight / 2);
//        /*startX=0,startY=0说明从画布的当前位置（即mWidth/2,mHeight/2)开始朝向（mWidth/2+100,mHeight/2+100)的点画一段线段，长度为  根号下(100*100+100*100)约141  )*/
//        canvas.drawLine(0, 0, 100, 100, hourPointerPaint);
//        canvas.drawLine(0, 0, 100, 200, minutePointerPaint);
//        /*恢复之前保存好的图像，使得新旧图像合并*/
//        canvas.restore();
//    }

}
