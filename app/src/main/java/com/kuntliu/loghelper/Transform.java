package com.kuntliu.loghelper;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Transform {
    public static String transformSize(long FileSize){
        String fileSize_str;
        double fileSize_db;
        if (FileSize >= Math.pow(2, 30)){
            fileSize_db = FileSize/Math.pow(2, 30);
            fileSize_str = String.format(Locale.getDefault(),"%.2f", fileSize_db) + " GB";
        }else if (FileSize >= Math.pow(2, 20) && FileSize < Math.pow(2, 30)){
            fileSize_db = FileSize/Math.pow(2, 20);
            fileSize_str = String.format(Locale.getDefault(),"%.2f", fileSize_db) + " MB";
        }else if(FileSize >= Math.pow(2, 10) && FileSize < Math.pow(2, 20)){
            fileSize_db = FileSize/Math.pow(2, 10);
            fileSize_str = String.format(Locale.getDefault(),"%.2f", fileSize_db) + " KB";
        }else {
            fileSize_str = FileSize + " B";
        }
        return fileSize_str;
    }
    public static String transFormTime(Long time_long){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/M/d HH:mm", Locale.getDefault());
        return sd.format(time_long);
    }
}
