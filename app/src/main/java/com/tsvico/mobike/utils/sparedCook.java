package com.tsvico.mobike.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class sparedCook {
    /***********
     * 把cookie写入和读取到shared
     * @param context
     * @param key
     * @param value
     */
    public static void saveCookie(Context context,String key,String value){
        //获得SharedPreferences的实例 sp_name是文件名
        SharedPreferences sp = context.getSharedPreferences("mobike", Context.MODE_PRIVATE);
        //获得Editor 实例
        SharedPreferences.Editor editor = sp.edit();
        //以key-value形式保存数据
        editor.putString(key, value);
        //apply()是异步写入数据
        editor.apply();
        //commit()是同步写入数据
        //editor.commit();

    }
    public static String getCookie(Context context,String key){
        //获得SharedPreferences的实例
        SharedPreferences sp = context.getSharedPreferences("mobike", Context.MODE_PRIVATE);
        //通过key值获取到相应的data，如果没取到，则返回后面的默认值
        String data = sp.getString(key, "defaultValue");
        return data;
    }
    /**
     * 移除数据
     */
    private static void removeUserInfo(Context context,String key){
        SharedPreferences userInfo = context.getSharedPreferences("mobike", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清空数据
     */
    private static void clearUserInfo(Context context){
        SharedPreferences userInfo = context.getSharedPreferences("mobike", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.clear();
        editor.commit();
    }
}
