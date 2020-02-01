package com.sadam.sadamlibarary.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.sadam.sadamlibarary.AppInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StaticUtils {
    private static final byte MAXFLOOR = 3;

    public static String getCodeInfo(Throwable throwable) {
        String s = "";
        int i = 0;
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            if (i == 0) {
                i++;
                continue;
            }
            s += stackTraceElement.toString() + "" + "\n";
            if (i++ == MAXFLOOR) {
                break;
            }
        }
        return s + ":";
    }

    /**
     * 通过系统默认的浏览器浏览网页
     *
     * @param url 要打开的网址
     */
    public static void openWebsiteOnSystemWebBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivities(new Intent[]{intent});
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void UpgradeAlert(String message, final Context context, AppInfo appInfo, final String url_for_upgrade) {
        int index1 = message.indexOf("|");
        int index2 = message.indexOf("/");
        long newVersionCode = Long.valueOf(message.substring(0, index1));
        String newVersionName = message.substring(index1 + 1, index2);
        String text = message.substring(index2 + 1);
        System.out.println(text);
        if (appInfo.getVersionCode() < newVersionCode) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("更新通知");
            dialog.setMessage("有内容更新......\nV" + appInfo.getVersionName() + "  -->  V" + newVersionName + "\n" + text + "\n要不要更新？");
            dialog.setCancelable(true);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openWebsiteOnSystemWebBrowser(context, url_for_upgrade);
                }
            });
            dialog.setNegativeButton("下次吧", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }
    }

    @SuppressLint("LongLogTag")
    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        Log.e("<StaticUtils:isLightColor>", "the darkness of this color is " + darkness + "");
        return darkness < 0.5;
    }

    /**
     * 通过反射机制获取Field的get，set方法
     *
     * @param objectClass
     * @param field
     * @param SetOrGet
     * @return 返回Method    通过  Method.ivoke(Object,args)方法来实现
     */
    private static Method getDeclaredSetGetMethod(Class objectClass, Field field, String SetOrGet) {
        StringBuffer sb = new StringBuffer();
        sb.append(SetOrGet);
        sb.append(field.getName().substring(0, 1).toUpperCase());
        sb.append(field.getName().substring(1));
        try {
            return objectClass.getDeclaredMethod(sb.toString(), field.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射机制获取Field的set方法
     *
     * @param objectClass
     * @param field
     * @return
     */
    public static Method getDeclaredSetMethod(Class objectClass, Field field) {
        return getDeclaredSetGetMethod(objectClass, field, "set");
    }

    /**
     * 通过反射机制获取Field的get方法
     *
     * @param objectClass
     * @param field
     * @return
     */
    public static Method getDeclaredGetMethod(Class objectClass, Field field) {
        return getDeclaredSetGetMethod(objectClass, field, "get");
    }

    /**
     * 调整图片大小
     *
     * @param bitmap 源
     * @param dst_w  输出宽度
     * @param dst_h  输出高度
     * @return
     */
    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }


    public final static float map(float value, float minValue, float maxValue, float newMinValue, float newMaxValue) {
        float oldInterValSize = maxValue - minValue;
        float newIntervalSize = newMaxValue - newMinValue;
        float realValue = value - minValue;
        float percentage = realValue / oldInterValSize;
        float newValue = newMinValue + percentage * newIntervalSize;
        return newValue;
    }

    public static void play(Context context, MediaPlayer mediaPlayer, int resources) {
        mediaPlayer.reset();
        Uri uri = Uri.parse("android.resource://com.sadam.bluetoothcontroler/" + resources);
        try {
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mediaPlayer.setDisplay(surfaceView.getHolder());
        mediaPlayer.start();
    }

    public static void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        stopMediaPlayer(mediaPlayer);
        mediaPlayer.release();
    }

    public static void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private static void play(Context context, MediaPlayer mediaPlayer, SurfaceView surfaceView, int resources) {
        mediaPlayer.reset();
        Uri uri = Uri.parse("android.resource://com.sadam.bluetoothcontroler/" + resources);
        try {
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setDisplay(surfaceView.getHolder());
        mediaPlayer.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void UpgradeAlert(String message, final Context context, AppInfo appInfo) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            int newVersionCode = jsonObject.getInt("versionCode");
            String newVersionName = jsonObject.getString("versionName");
            String messages = jsonObject.getString("messages");
            final String download_url = jsonObject.getString("download_url");
            if (appInfo.getVersionCode() < newVersionCode) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("更新通知");
                dialog.setMessage("有内容更新......\nV" + appInfo.getVersionName() + "  -->  V" + newVersionName + "\n" + messages + "\n要不要更新？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openWebsiteOnSystemWebBrowser(context, download_url);
                    }
                });
                dialog.setNegativeButton("下次吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
