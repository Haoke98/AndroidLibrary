package com.sadam.sadamlibarary.MyView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import static com.sadam.sadamlibarary.Utils.StaticUtils.map;


/**
 * 因为是ImageView类的子类，所以是一个组建，可以添加到  Layout.XML  文件当中
 */
@SuppressLint("AppCompatCustomView")
public class BubbleView extends ImageView implements View.OnTouchListener {
    public static float SCREENWIDTH;
    public static float SCREENHEIGHT;
    /**
     * 用于储存Bubble对象的数组，是弹性可变数组，不用定义大小；非常方便
     */
    static ArrayList<Bubble> bubbleList;
    static boolean isStopped = false;
    private Random rand = new Random();
    /**
     * 定义气泡的默认大小
     */
    private int size = 50;
    /**
     * 定义默认的延迟时间，跟刷新率有关,33毫秒，1000/33 = 30 fps,帧间延迟33毫秒 ，30fps指 每一秒过30帧动画
     * private int delay = 33;       --->  30fps
     * private int delay = 16;       --->  62fps
     */
    private int delay = 16;
    /**
     * Piant对象相当于Android屏幕上的画笔对象
     */
    private Paint myPaint = new Paint();
    /**
     * 操作线程
     */
    private Handler handler = new Handler();
    /**
     * 操作线程的接口Runnable,Runnable接口提供方法run()，它决定当线程运行时该做什么
     */
    private Runnable runable = new Runnable() {
        @Override
        public void run() {
            for (Bubble b : bubbleList) {
//            调用Bubble对象的update方法来更新bubbleList中每个气泡的下x和y坐标
                if (isStopped) {
//                    b.update();
                }
            }
//        用于清屏，  执行清屏 并 调用方法onDraw()重绘视图
            invalidate();
        }
    };


    /**
     * 给出构造方法
     *
     * @param context      当把这个BubbleView添加到 Layout.xml 文件上时，系统自动 调用该参数，并且把Layout.xml文件的Context作为参数传给它
     * @param attributeSet
     */
    public BubbleView(Context context, AttributeSet attributeSet) {
//        把参数传递给父类ImageView的构造函数，设置应用和绘画屏幕
        super(context, attributeSet);
        bubbleList = new ArrayList<Bubble>();
//        在构造函数中添加触摸事件监听器   ,让java将触摸事件交给这个BubbleView对象的onTouch()方法去处理
//        setOnTouchListener(this);
        bubbleList.add(new Bubble(50, 50, 50));


//        display = (Display) context.getSystemService(Context.DISPLAY_SERVICE) getWindowManager().getDefaultDisplay();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        SCREENWIDTH = windowManager.getDefaultDisplay().getWidth();

        SCREENHEIGHT = windowManager.getDefaultDisplay().getHeight();
    }

    /**
     * @param newX           新的x坐标
     * @param Xmin           能提供的新x坐标的最小值
     * @param Xmax           能提供的新x坐标的最大值
     * @param newY           新的y坐标
     * @param Ymin           能提供的新y坐标的最小值
     * @param Ymax           能提供的新y坐标的最大值
     * @param newDiameter    新的气球直径
     * @param newDiameterMin 能提供的新气球直径的最小值
     * @param newDiameterMax 能提供的新气球直径的最大值
     * @param newAlfa        新的透明度
     * @param newAlfaMin     能提供的新透明度的最小值
     * @param newAlfaMax     能提供的新透明度的最大值
     */
    public static void update(int newX, int Xmin, int Xmax, int newY, int Ymin, int Ymax, int newDiameter, int newDiameterMin, int newDiameterMax, int newAlfa, int newAlfaMin, int newAlfaMax) {
        if (bubbleList.size() > 0) {
            float x = map(newX, Xmin, Xmax, 0, SCREENWIDTH);
            float y = map(newY, Ymin, Ymax, 0, SCREENHEIGHT);
            float size = map(newDiameter, newDiameterMin, newDiameterMax, 0, Math.min(SCREENWIDTH, SCREENHEIGHT));
            int alfa = (int) map(newAlfa, newAlfaMin, newAlfaMax, 0, 255);


            Bubble bubble = bubbleList.get(0);
            String s = "x:" + x + " y:" + y + "  diametere:" + size + " w:" + SCREENWIDTH + " H:" + SCREENHEIGHT;
            Log.d("data", s);
            bubble.update(x, y, size, alfa);
        } else {

        }
    }

    public static void update(int newX, int Xmin, int Xmax, int newY, int Ymin, int Ymax, int newDiameter, int newDiameterMin, int newDiameterMax) {
        if (bubbleList.size() > 0) {
            float x = map(newX, Xmin, Xmax, 0, SCREENWIDTH);
            float y = map(newY, Ymin, Ymax, 0, SCREENHEIGHT);
            float size = map(newDiameter, newDiameterMin, newDiameterMax, 0, Math.min(SCREENWIDTH, SCREENHEIGHT));
            Bubble bubble = bubbleList.get(0);
            String s = "x:" + x + " y:" + y + "  diametere:" + size + " w:" + SCREENWIDTH + " H:" + SCREENHEIGHT;
            Log.d("data", s);
            bubble.update(x, y, size);
        } else {

        }
    }

    public static void update(int newX, int Xmin, int Xmax, int newY, int Ymin, int Ymax) {
        if (bubbleList.size() > 0) {
            float x = map(newX, Xmin, Xmax, 0, SCREENWIDTH);
            float y = map(newY, Ymin, Ymax, 0, SCREENHEIGHT);
            float size = Math.min(SCREENWIDTH, SCREENHEIGHT) * 0.2f;
            Bubble bubble = bubbleList.get(0);
            String s = "x:" + x + " y:" + y + "  diametere:" + size + " w:" + SCREENWIDTH + " H:" + SCREENHEIGHT;
            Log.d("data", s);
            bubble.update(x, y, size);
        } else {

        }
    }

    /**
     * onDraw()告诉java当屏幕刷新时绘制什么，是跟Invalidate()方法有关
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        for (Bubble b : bubbleList) {
            b.draw(canvas);
        }
//        其中runable为接口Runable接口对象，把Handler对象handler与线程（Runable对象）runable关联起来，方法postDelay()向线程runable发送一条消息，让他33（delay的值）毫秒后再次运行
        handler.postDelayed(runable, delay);
    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
//        通过MotionEvent对象的getX(),getY()方法来获取单个触摸在屏幕上的位置
//        MotionEvent对象的getPointerCount()方法返回此刻在屏幕上触摸点的个数，屏幕上每个触摸点都按先后循序进行编号，所以通过循环访问,在把编号作为参数传递给方法getX(n)和getY(n)就能获取第n个触摸点的位置
        for (int n = 0; n < motionEvent.getPointerCount(); n++) {
            int x = (int) motionEvent.getX(n);
            int y = (int) motionEvent.getY();
            int s = rand.nextInt(size) + size;
            bubbleList.add(new Bubble(x, y, s));
        }
//        如果对触摸事件做全面的处理，则返回true;如果让Android去处理触摸事件（滚动or缩放等）应该返回false
        return true;
    }

    private class Bubble {
        /**
         * 定义常量最大默认速度，因为是常量所以都大写
         */
        private final int MAX_SPEED = 15;
        private float x;
        private float y;
        /**
         * 气球地直径
         */
        private float diametere;
        /**
         * 在Android中颜色都是编号数的所以颜色对应某一个数
         */
        private int color;
        private int xSpeed, ySpeed;

        /**
         * 定义构造函数
         *
         * @param newX        气泡在画布上的位置x坐标      单位：pxl(像素）
         * @param newY        气泡在画布上的位置y坐标      单位：pxl(像素）
         * @param newDiameter 气泡的大小，指的是直径长度  单位：pxl（像素）
         */
        public Bubble(int newX, int newY, int newDiameter) {
            x = newX;
            y = newY;
            diametere = newDiameter;
//            类Color的方法argb返回参数所对应的颜色的编号，参数分别是alpha,r,g,b 其中alpha是透明度
            color = Color.argb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
//            这算法 生成  [-MAXSPEED,MAXSPEED]之间生成随机数
            xSpeed = rand.nextInt(MAX_SPEED * 2) - MAX_SPEED;
            ySpeed = rand.nextInt(MAX_SPEED * 2) - MAX_SPEED;
//            为避免气泡呆在原地不动
            if (xSpeed == 0 && ySpeed == 0) {
                xSpeed = rand.nextInt(MAX_SPEED * 2) - MAX_SPEED;
                ySpeed = rand.nextInt(MAX_SPEED * 2) - MAX_SPEED;
            }
        }

        /**
         * Canvas canvas 相当于java.awt.Graphics   调用画笔的方法
         *
         * @param canvas 画布  是Canvas类的对象
         */
        public void draw(Canvas canvas) {
//            为画笔myPaint选颜色
            myPaint.setColor(color);
//           用画笔myPaint在画布（屏幕)上画, drawOval()是画椭圆的方法所以需要四个参数，   可以实现  当气泡碰到边框是变形  逐渐地缩小参数就行
            canvas.drawOval(x - diametere / 2, y - diametere / 2, x + diametere / 2, y + diametere / 2, myPaint);
        }

        /**
         * 更新气泡的坐标来实现移动动画 ， 气泡主动更改
         */
        public void updateNaturally() {
            x += xSpeed;
            y += ySpeed;
//            碰撞检测,getWidth()方法获取屏幕的右边缘值
            if (x - diametere / 2 <= 0 || x + diametere / 2 >= getWidth()) {
                xSpeed = -xSpeed;
            }
//            碰到边缘之后就反弹，碰撞前后的速度大小相等，方向相反,  靠经上边缘时减去size/2之后在比较来实现硬碰撞
            if (y - diametere / 2 <= 0 || y + diametere / 2 >= getHeight()) {
                ySpeed = -ySpeed;
            }
        }

        /**
         * 被动更改  气泡的各种属性
         *
         * @param newX         新的x坐标
         * @param newY         新的y坐标
         * @param newDiametere 新的大小
         * @param newAlfa      新的透明度 （0到255）
         */
        public void update(float newX, float newY, float newDiametere, int newAlfa) {
            this.x = newX;
            this.y = newY;
            this.diametere = newDiametere;
            this.color = Color.argb(newAlfa, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }

        public void update(float newX, float newY, float newDiametere) {
            update(newX, newY, newDiametere, 255);
        }

        public void update(float newX, float newY) {
            update(newX, newY, 100);
        }

    }
}