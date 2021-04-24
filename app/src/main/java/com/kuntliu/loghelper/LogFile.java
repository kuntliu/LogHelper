package com.kuntliu.loghelper;

import android.graphics.drawable.Drawable;

public class LogFile {
    private Drawable file_icon;
    private String file_name;
    private Long file_size;
    private Long file_time_create;
    private String file_apk_version;

    public LogFile(Drawable file_icon, String file_name, Long file_size, Long file_time_create, String file_apk_version) {
        this.file_icon = file_icon;
        this.file_name = file_name;
        this.file_size = file_size;
        this.file_time_create = file_time_create;
        this.file_apk_version = file_apk_version;
    }

    public Drawable getFile_icon() {
        return file_icon;
    }
    public void setFile_icon(Drawable file_icon) {
        this.file_icon = file_icon;
    }

    public String getFile_name() {
        return file_name;
    }
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Long getFile_size() {
        return file_size;
    }
    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public Long getFile_time_create() {
        return file_time_create;
    }
    public void setFile_time_create(Long file_time_create) {
        this.file_time_create = file_time_create;
    }

    public String getFile_apk_version() {
        return file_apk_version;
    }
    public void setFile_apk_version(String file_apk_version) {
        this.file_apk_version = file_apk_version;
    }

}
