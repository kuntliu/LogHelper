package com.kuntliu.loghelper;

public class LogFile {
    private int file_image;
    private String file_name;
    private String file_size;
    private String file_time_create;

    public LogFile(int file_image, String file_name, String file_size, String file_time_create) {
        this.file_image = file_image;
        this.file_name = file_name;
        this.file_size = file_size;
        this.file_time_create = file_time_create;
    }

    public int getFile_image() {
        return file_image;
    }

    public void setFile_image(int file_image) {
        this.file_image = file_image;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_time_create() {
        return file_time_create;
    }

    public void setFile_time_create(String file_time_create) {
        this.file_time_create = file_time_create;
    }

}
