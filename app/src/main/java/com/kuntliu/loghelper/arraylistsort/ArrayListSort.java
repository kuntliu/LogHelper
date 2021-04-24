package com.kuntliu.loghelper.arraylistsort;

import com.kuntliu.loghelper.LogFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ArrayListSort {

    public List<LogFile> fileSort(List<LogFile> fileList, int sortByWhat, int sortType){

        if (sortByWhat == 1 && sortType == 0){
            //按照文件时间排序,新->旧
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f2.getFile_time_create().compareTo(f1.getFile_time_create());
                }
            });
        }
        else if (sortByWhat == 1 && sortType == 1){
            //按照文件时间排序,旧->新
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f1.getFile_time_create().compareTo(f2.getFile_time_create());
                }
            });
        }else if(sortByWhat == 2 && sortType == 0){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f1.getFile_name().compareTo(f2.getFile_name());
                }
            });
        }else if(sortByWhat == 2 && sortType == 1){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f2.getFile_name().compareTo(f1.getFile_name());
                }
            });
        }else if(sortByWhat == 3 && sortType == 0){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f1.getFile_size().compareTo(f2.getFile_size());
                }
            });
        }else if(sortByWhat == 3 && sortType == 1){
            Collections.sort(fileList, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    return f2.getFile_size().compareTo(f1.getFile_size());
                }
            });
        }
        return fileList;
    }
}
