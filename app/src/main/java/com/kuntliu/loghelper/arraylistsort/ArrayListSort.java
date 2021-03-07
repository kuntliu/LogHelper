package com.kuntliu.loghelper.arraylistsort;

import com.kuntliu.loghelper.LogFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ArrayListSort {
    public final int SORT_BY_NAME = 0;
    public final int SORT_BY_TIME = 1;


    public final int SORT_TYPE_UP = 0;
    public final int SORT_TYPE_DOWN = 1;

    public List<LogFile> fileSort(List<LogFile> fileList, int sortByWhat, int sortType){

        //按照文件名字典顺序排序,正序
        if (sortByWhat == 0 && sortType == 0){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f1.getFile_name().compareTo(f2.getFile_name());
                }
            });
        //按照文件名字典顺序排序,逆序
        }else if (sortByWhat == 0 && sortType == 1){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f2.getFile_name().compareTo(f1.getFile_name());
                }
            });
        }
        //按照文件时间排序,正序
        else if (sortByWhat == 1 && sortType == 0){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f1.getFile_time_create().compareTo(f2.getFile_time_create());
                }
            });
        }
        //按照文件时间排序,逆序
        else if (sortByWhat == 1 && sortType == 1){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {return f1.getFile_time_create().compareTo(f2.getFile_time_create());
                }
            });
        }
        return fileList;
    }
}
