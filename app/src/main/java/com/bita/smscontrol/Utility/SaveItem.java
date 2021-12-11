package com.bita.smscontrol.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveItem {
    public static final String DEVICE_PHONE ="device_phone";
    public static final String DEVICE_PHONE_ID ="device_phone_id";
    public static final String OPERATOR_PHONE ="operator_phone";
    public static final String COUNT_UNREAD_SMS="count_unread_sms";
    public static final String LAST_SMS ="last_sms";

    public static final String OPERATOR_NAME_1="operator_name_1";
    public static final String OPERATOR_TEL_1="operator_tel_1";

    public static final String OPERATOR_NAME_2="operator_name_2";
    public static final String OPERATOR_TEL_2="operator_tel_2";

    public static final String OPERATOR_NAME_3="operator_name_3";
    public static final String OPERATOR_TEL_3="operator_tel_3";

    public static final String DEVICE_NAME_1="device_name_1";
    public static final String DEVICE_TEL_1="device_tel_1";

    public static final String DEVICE_NAME_2="device_name_2";
    public static final String DEVICE_TEL_2="device_tel_2";

    public static final String DEVICE_NAME_3="device_name_3";
    public static final String DEVICE_TEL_3="device_tel_3";



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

