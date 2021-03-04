package com.kuntliu.loghelper;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.kuntliu.loghelper.arraylistsort.ArrayListSort;
import com.kuntliu.loghelper.myadapter.MyRecycleViewApater;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FileToOperate {
    public File file;

    public static List<LogFile> getFileList(String path, File[] arrFiles, Context context, TextView tv_empty_tips)  {
        List<LogFile> fileList = new ArrayList<>();   //初始化数据
        //判断path目录是否存在
        if (getFileArr(path) != null){
            if (arrFiles.length == 0){  //踩坑描述：要先判断arrFiles是否为null，然后再判断后面的length == 0，否侧可能会出现空指针
                fileList.clear();
                tv_empty_tips.setVisibility(View.VISIBLE);
                tv_empty_tips.setText("当前目录为空");
//                Toast.makeText(this, "当前目录为空", Toast.LENGTH_SHORT).show();
            }else {
                for (File f : arrFiles) {
                    tv_empty_tips.setVisibility(View.GONE);
//                Log.d("FileList", f.toString());
                    if (f.isFile() && !f.getName().startsWith(".")) {    //过滤：“.”开头的隐藏文件和path目录下的文件夹
                        String fileSize_str = FileSizeTransform.Tansform(f.length());      //获取文件大小并且进行显示单位转换
                        String time_str = MySimpleDateFormat.transFormTime(f.lastModified());    //获取文件最后修改时间并且进行时间格式转换
                        //判断如果文件是apk就获取版本号
                        String apk_version = "";
                        if (f.getName().endsWith(".apk")){
                            apk_version = " - 版本 " + getApkVersion(f.getAbsolutePath(), context);
                            if (apk_version.equals(" - ")){
                                apk_version = "";
                            }
                        }
                        LogFile log = new LogFile(getFileDrawable(f, context), f.getName(), fileSize_str, time_str, apk_version);

//                    Log.d("fileName:fileSize", f.getName()+":"+ f.length());
                        fileList.add(log);
                    }
                }
                fileList = new ArrayListSort().stringSore(fileList, 1);   //对集合内元素进行排序
            }
        }else {
            fileList.clear();
            tv_empty_tips.setVisibility(View.VISIBLE);
            tv_empty_tips.setText("当前目录不存在");
//            Toast.makeText(MainActivity.this, "当前目录不存在", Toast.LENGTH_SHORT).show();
//            Log.d("IsNofile", "ture");
        }
        return fileList;
    }


    //获取path目录下的文件（数组类型）
    public static File[] getFileArr(String path) {
        File file = new File(path);
        return file.listFiles();
    }

    //根据文件名获取list中选择的文件对象
    public static File searchSelectedFile(File[] arrFiles, String fileNameClicked){
        File selectedFile = null;
        for (File f : arrFiles){
            if (f.getName().equals(fileNameClicked)){
                selectedFile = f;
                break;
            }
        }
        return selectedFile;
    }

    //判断文件类型并且返回对应的文件icon资源
    public static Drawable getFileDrawable(File file, Context context){
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        if (fileName.endsWith(".apk")){
            return getApkIcon(filePath ,context);
        }else if(fileName.endsWith(".txt")){
            return context.getDrawable(R.drawable.ic_file_txt);
        }else if(fileName.endsWith(".log")){
            return context.getDrawable(R.drawable.ic_file_txt);
        }else if(fileName.endsWith(".obb")){
            return context.getDrawable(R.drawable.ic_file_obb);
        }else
            return context.getDrawable(R.drawable.ic_file_unknown);
    }



    //根据position删除对应的数据源
    public static void deleteFile(File file, List<LogFile> loglist, int position, MyRecycleViewApater madapter, Context context){
        if (file != null && file.exists()) {
            boolean isSuccessDeleteFile = file.delete();           //删除文件
            loglist.remove(loglist.get(position));              //删除loglist对应的数据源
            madapter.notifyItemRemoved(position);      //播放删除动画
            madapter.notifyItemRangeChanged(position, loglist.size());  //解决删除文件后list的position发生变化的问题，对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
//            Log.d("isSuccessDeleteFile", String.valueOf(isSuccessDeleteFile));
//            Log.d(TAG, "deleteFile: "+position);
        }else {
            Toast.makeText(context ,"删除失败，文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    //通过QQ上我的电脑分享（发送）文件
    public static void shareFile(File selectedFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity");//通过QQ传给我的电脑
        //适配7.0版本以下的Android系统,需要使用内容提供器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.kuntliu.loghelper.fileprovider", selectedFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selectedFile));
            context.startActivity(intent);
        }
    }

    //通过apk的路径获取包信息
    private static Drawable getApkIcon(String path, Context context){
        Drawable icon = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

//        Log.d("getFileDrawable", "getFileDrawable: "+path);
        if (pi != null) {
            ApplicationInfo application = pi.applicationInfo;
            application.sourceDir = path;
            application.publicSourceDir = path; //如果不加这2句，那么获取的将会是android默认的icon，而不是应用的icon
            icon = pm.getApplicationIcon(application);
        }
        return icon;
    }

    public static String getApkVersion(String path, Context context){
        String version = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
        if (pi != null){
            ApplicationInfo application = pi.applicationInfo;
            version = pi.versionName;
        }
        return version;
    }


    //识别并安装apk文件
    public static void installAPK(File file, Context context){
        if (file.getName().endsWith(".apk")){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Uri uri = FileProvider.getUriForFile(context, "com.kuntliu.loghelper.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        }
    }


    public void getLogcatData(){

    }
}
