package com.kuntliu.loghelper.mypreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import androidx.preference.ListPreference;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class MyPreferences {

    public static void setSharePreferencesListData(String key,ArrayList<String> myDataToSet, Context context){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = spf.edit();

        StringBuilder data_str = new StringBuilder();
        if (myDataToSet !=null && myDataToSet.size() > 0){
            for (String s : myDataToSet){
                data_str.append(s);
                data_str.append("#");//由于保存数据的Set集合是不可重复并且无序的，因此使用字符串和#作为分隔符来存储数组类型的数据
            }
            data_str.deleteCharAt(data_str.lastIndexOf("#"));
        }
        Log.d(TAG, "setSharePreferencesArrData: "+data_str.toString());
        editor.putString(key, data_str.toString());
        editor.apply();
    }

    public static ArrayList<String> getSharePreferencesListData(String key, Context context){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> data_arr = new ArrayList<>(Arrays.asList(spf.getString(key, "").split("#")));
        Log.d(TAG, "getSharePreferencesArrData: "+ data_arr);
        return data_arr;
    }

    public static void updateSharePreferencesListData(String key, String datatoUpdata, int position, Context context){
        ArrayList<String> dataList = getSharePreferencesListData(key, context);
        dataList.set(position, datatoUpdata);
        setSharePreferencesListData(key, dataList, context);
    }


}
