package com.kuntliu.loghelper;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.kuntliu.loghelper.arraylistsort.ArrayListSort;
import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FileToOperate {

    private static final int SORT_BY_TIME = 1;     //根据时间排序
    private static final int SORT_BY_SIZE = 2;     //根据大小排序
    private static final int SORT_BY_NAME = 3;     //根据名称排序
    private static final int ORDER_POSITIVE = 0;   //正序
    private static final int ORDER_REVERSE = 1;    //逆序

    //设置默认的初始Tab名称和Tab对应的路径
    public static void setDefalutTabAndPath(ArrayList<String> TabList, ArrayList<String> PathList){
        String path_SdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path_west = path_SdcardRoot+
                File.separator+"Android"+File.separator+"data"+File.separator+"com.activision.callofduty.shooter"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
        String path_cn = path_SdcardRoot+
                File.separator+"Android"+File.separator+"data"+File.separator+"com.tencent.tmgp.cod"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
        String path_garena = path_SdcardRoot+
                File.separator+"Android"+File.separator+"data"+File.separator+"com.garena.game.codm"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
        String path_korea = path_SdcardRoot+
                File.separator+"Android"+File.separator+"data"+File.separator+"com.tencent.tmgp.kr.codm"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
        String path_vng = path_SdcardRoot+
                File.separator+"Android"+File.separator+"data"+File.separator+"com.vng.codmvn"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;

        TabList.add("主目录");
        TabList.add("西方");
        TabList.add("国服");
        TabList.add("GARENA");
        TabList.add("韩国");
        TabList.add("VNG");
        PathList.add(path_SdcardRoot);
        PathList.add(path_west);
        PathList.add(path_cn);
        PathList.add(path_garena);
        PathList.add(path_korea);
        PathList.add(path_vng);
    }

    //获取文件列表，数据形式为list
    public static List<LogFile> getFileList(String path, File[] arrFiles, Context context, String filterConditon, Boolean isSdCardroot)  {
        List<LogFile> fileList = new ArrayList<>();   //初始化数据
        //判断path目录是否存在
        if (getFileArr(path) != null){
                for (File f : arrFiles) {
//                Log.d("FileList", f.toString());
                    if (f.isFile() && !f.getName().startsWith(".")) {    //过滤：“.”开头的隐藏文件和path目录下的文件夹
                        //判断如果文件是apk就获取版本号
                        String apk_version = "";
                        if (f.getName().endsWith(".apk")){
                            apk_version = " - 版本 " + getApkVersion(f.getAbsolutePath(), context);
                            if (apk_version.equals(" - ")){
                                apk_version = "";
                            }
                        }
                        if (isSdCardroot && filterConditon.equals( "show_apk_obb")){
                            if (f.getName().endsWith("apk") || f.getName().endsWith("obb")){
                                LogFile log = new LogFile(getFileDrawable(f, context), f.getName(), f.length(), f.lastModified(), apk_version);
//                    Log.d("fileName:fileSize", f.getName()+":"+ f.length());
                                fileList.add(log);
                            }
                        }else {
                            LogFile log = new LogFile(getFileDrawable(f, context), f.getName(), f.length(), f.lastModified(), apk_version);
                            fileList.add(log);
                        }
                    }
                }
                //对集合内元素进行排序
                fileList = new ArrayListSort().fileSort(fileList, MyPreferences.getSharePreferencesSortData("sort_setting", context)[0], MyPreferences.getSharePreferencesSortData("sort_setting", context)[1]);
            }
        return fileList;
    }


    //根据list和file[]正确判断当前文件列表状态
    public static void tvSwitch(List<LogFile> list, File[] arrFiles, TextView tv){
        if (list.size() == 0){
            tv.setVisibility(View.VISIBLE);
            tv.setText("当前目录为空");
        }if (arrFiles == null){
            tv.setVisibility(View.VISIBLE);
            tv.setText("当前目录不存在");
        }if (list.size() != 0 && arrFiles != null) {
            tv.setVisibility(View.GONE);
        }
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

    //根据position删除对应的数据源并刷新适配器
    public static void deleteFile(File file, List<LogFile> loglist, int position, MyRecycleViewAdapter madapter, Context context){
        if (file != null && file.exists()) {
            boolean isSuccessDeleteFile = file.delete();         //删除文件
            loglist.remove(loglist.get(position));              //删除loglist对应的数据源
            madapter.notifyItemRemoved(position);               //播放删除动画
            madapter.notifyItemRangeChanged(position, loglist.size());  //解决删除文件后list的position发生变化的问题，对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
//            Log.d("isSuccessDeleteFile", String.valueOf(isSuccessDeleteFile));
//            Log.d(TAG, "deleteFile: "+position);
        }else {
            Toast.makeText(context ,"删除失败，文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    //判断系统是否已安装QQ
    public static boolean isInstallQQ(Context context){
        boolean isInstall = false;
        PackageManager pm = context.getPackageManager();
        //获取手机内已安装应用的列表
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        Log.d(TAG, "getAppList: "+packageInfoList);
        for (PackageInfo p : packageInfoList){
            if (p.packageName.equals("com.tencent.mobileqq")) {
                isInstall = true;
                break;
            }
        }
        return isInstall;
    }

    //通过QQ-“发送我的电脑”分享（发送）文件
    public static void shareFile(File selectedFile, Context context) {
        if (isInstallQQ(context)){
            if (selectedFile.exists()){
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity");//通过QQ传给我的电脑
                //适配7.0版本以上的Android系统,需要使用内容提供器
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.kuntliu.loghelper.fileprovider", selectedFile));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }else {
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selectedFile));
                    context.startActivity(intent);
                }
            }else {
                Toast.makeText(context, "文件不存在", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(context, "未安装QQ", Toast.LENGTH_LONG).show();
        }
    }

    //通过apk的路径获取apk安装包的图标
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

    //通过apk的路径获取apk安装包的版本号
    public static String getApkVersion(String path, Context context){
        String version = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
        if (pi != null){
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

}
