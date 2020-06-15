package com.kuntliu.loghelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

class MySimpleDateFormat {
    String transFormTime(Long time_long){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/M/d HH:mm", Locale.getDefault());
        return sd.format(time_long);
    }
}
