package com.trendlab.aptex;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author: lookey
 * @date: 2021/5/25
 */
public class BinderViewTools {
    public static void bind(Activity activity){
        Class aClass = activity.getClass();
        try {
            Class<?> bindClass = Class.forName(aClass.getCanonicalName() + "_ViewBinding");//找到生成的相应的bind类工具
            Method method = bindClass.getMethod("bindView", aClass);
            method.invoke(bindClass.newInstance(),activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
