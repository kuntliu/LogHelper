package com.kuntliu.loghelper.mypreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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

    public static void deleteSharePreferenceListData(String key, int position, Context context){
        ArrayList<String> dataList = getSharePreferencesListData(key, context);
        dataList.remove(position);
        setSharePreferencesListData(key, dataList, context);
    }

    public static ArrayList<String> getSharePreferencesListData(String key, Context context){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> dataList = new ArrayList<>(Arrays.asList(spf.getString(key, "").split("#")));
        Log.d(TAG, "getSharePreferencesArrData: "+ dataList);
        return dataList;
    }

    public static String getSharePreferencesStringData(String key, String defValue, Context context){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        return spf.getString(key, defValue);
    }

    public static void updateSharePreferencesListData(String key, String strToUpdate, int position, Context context){
        ArrayList<String> dataList = getSharePreferencesListData(key, context);
        dataList.set(position, strToUpdate);
        setSharePreferencesListData(key, dataList, context);
    }

    public static int[] getSharePreferencesSortData(String key, Context context){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String[] sortData_str = spf.getString(key, "1,0").split(",");
        int[] sortData_int = new int[sortData_str.length];
        for (int i=0; i<sortData_str.length; i++){
            sortData_int[i] = Integer.parseInt(sortData_str[i]);
        }
        return sortData_int;
    }

    public static int SharePreferenceSize(Context context){
        return getSharePreferencesListData("myTabs", context).size();
    }

}
