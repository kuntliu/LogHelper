package com.kuntliu.loghelper;

import android.annotation.SuppressLint;

import java.util.Locale;

public class FileSizeTransform {
    public static String Tansform(long FileSize){
        String FileSize_str;
        double FileSize_db;
        if (FileSize >= Math.pow(2, 30)){
            FileSize_db = FileSize/Math.pow(2, 30);
            FileSize_str = String.format(Locale.getDefault(),"%.2f", FileSize_db) + " GB";
        }else if (FileSize >= Math.pow(2, 20) && FileSize < Math.pow(2, 30)){
            FileSize_db = FileSize/Math.pow(2, 20);
            FileSize_str = String.format(Locale.getDefault(),"%.2f", FileSize_db) + " MB";
        }else if(FileSize >= Math.pow(2, 10) && FileSize < Math.pow(2, 20)){
            FileSize_db = FileSize/Math.pow(2, 10);
            FileSize_str = String.format(Locale.getDefault(),"%.2f", FileSize_db) + " KB";
        }else {
            FileSize_str = FileSize + " B";
        }
        return FileSize_str;
    }
}
