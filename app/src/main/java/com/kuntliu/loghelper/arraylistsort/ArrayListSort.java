package com.kuntliu.loghelper.arraylistsort;

import com.kuntliu.loghelper.LogFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ArrayListSort {
    public List<LogFile> stringSore(List<LogFile> fileList, int sortType){

        //按照文件名ASCI码排序
        if (sortType == 0){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    if (f1.getFile_name().compareTo(f2.getFile_name()) == 0) {
                        return 0;
                    } else
                        return f1.getFile_name().compareTo(f2.getFile_name());
                }
            });
        }
        //按照文件时间排序
        else if (sortType == 1){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    if (f1.getFile_time_create().compareTo(f2.getFile_time_create()) == 0) {
                        return 0;
                    } else
                        return f1.getFile_name().compareTo(f2.getFile_name());
                }
            });
        }
        return fileList;
    }
}
