package com.bita.smscontrol.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveItem {
    public static final String DEVICE_PHONE ="device_phone";
    public static final String OPERATOR_PHONE ="operator_phone";
    public static final String COUNT_UNREAD_SMS="count_unread_sms";



    public static SharedPreferences getSP(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences.Editor getEdit(Context context){
        return getSP(context).edit();
    }

    public static void setItem(Context context,String key,String value){
        getEdit(context).putString(key,value).apply();
    }

    public static String getItem(Context context,String key,String defaultVal){
        String v = getSP(context).getString(key,"");
        if (v.equals("") || v.isEmpty()){
            return defaultVal.trim();
        }
        return v.trim();
    }

}

