package com.kuntliu.loghelper.arraylistsort;

import com.kuntliu.loghelper.LogFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArrayListSort {
    public List<LogFile> stringSore(List<LogFile> fileList){
        Collections.sort(fileList, new Comparator<LogFile>() {
            @Override
            public int compare(LogFile f1, LogFile f2) {
                if (f1.getFile_name().compareTo(f2.getFile_name()) == 0) {
                    return 0;
                } else
                    return f1.getFile_name().compareTo(f2.getFile_name());
            }
        });
        return fileList;
    }
}
