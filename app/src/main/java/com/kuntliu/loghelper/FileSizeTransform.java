package com.kuntliu.loghelper;

import android.annotation.SuppressLint;

import java.util.Locale;

public class FileSizeTransform {
    public static String Tansform(long FileSize){
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
}
