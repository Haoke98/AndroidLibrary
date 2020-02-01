package com.sadam.sadamlibarary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Tools {


    /**通过反射机制获取Field的get方法
     * @param objectClass
     * @param field
     * @return
     */
    public static Method getDeclaredGetMethod(Class objectClass,Field field){
        return getDeclaredSetGetMethod(objectClass,field,"get");
    }

    /**通过反射机制获取Field的set方法
     * @param objectClass
     * @param field
     * @return
     */
    public static Method getDeclaredSetMethod(Class objectClass,Field field){
        return getDeclaredSetGetMethod(objectClass,field,"set");
    }

    /**通过反射机制获取Field的get，set方法
     * @param objectClass
     * @param field
     * @param SetOrGet
     * @return  返回Method    通过  Method.ivoke(Object,args)方法来实现
     */
    private static Method getDeclaredSetGetMethod(Class objectClass, Field field, String SetOrGet){
        StringBuffer sb = new StringBuffer();
        sb.append(SetOrGet);
        sb.append(field.getName().substring(0,1).toUpperCase());
        sb.append(field.getName().substring(1));
        try {
            return objectClass.getDeclaredMethod(sb.toString(),field.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        Log.e(Tools.class.getSimpleName(), "the darkness of this color is " + darkness + "");
        return darkness < 0.5;
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
                        Tools.openWebsiteOnSystemWebBrowser(context, download_url);
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
