package com.kuntliu.loghelper.mydocumentfile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.kuntliu.loghelper.FileToOperate;
import com.kuntliu.loghelper.MyFile;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.arraylistsort.ArrayListSort;
import com.kuntliu.loghelper.mypermission.PermissionManager;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class MyDocumentFile {


    public static List<MyFile> getDocumentFileList(DocumentFile[] documentFileArr, boolean isNeedUseDoc, Context context) {
//        long start_time = System.currentTimeMillis();
        List<MyFile> fileList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 30 && isNeedUseDoc) {
            if (documentFileArr != null) {
                for (DocumentFile df : documentFileArr) {
                    String documentFileName = df.getName();
                    if (df.isFile() && !documentFileName.startsWith(".")) {    //过滤：“.”开头的隐藏文件和path目录下的文件夹
                        //判断如果文件是apk就获取版本号
                        String apk_version = "";
                        if (df.getName().endsWith(".apk")) {
                            apk_version = " - 版本 " + FileToOperate.getApkVersion(df.getUri().getPath(), context);
                            if (apk_version.equals(" - ")) {
                                apk_version = "";
                            }
                        }
                        MyFile myFile = new MyFile(getDocumentFileDrawable(df, context), documentFileName, df.length(), df.lastModified(), apk_version);
                        fileList.add(myFile);
                        Log.d(TAG, "getDocumentFileList: "+fileList);
                    }
                }
                //对集合内元素进行排序
                fileList = new ArrayListSort().fileSort(fileList, MyPreferences.getSharePreferencesSortData("sort_setting", context)[0], MyPreferences.getSharePreferencesSortData("sort_setting", context)[1]);
            }
        }
//        long end_time = System.currentTimeMillis();
//        Log.d(TAG, "getDocumentFileList: dotime "+(end_time-start_time));
        return fileList;
    }

    public static boolean checkIsNeedDocument(String path){
        boolean isNeedUseDocument = false;
        String path_contain_data = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android/data/";
        String path_contain_obb = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android/obb/";
        Log.d(TAG, "checkIsNeedDocument: path_contain_data "+path_contain_data);
        Log.d(TAG, "checkIsNeedDocument: path_contain_obb "+path_contain_obb);
        if (path.startsWith(path_contain_data) || path.startsWith(path_contain_obb) && Build.VERSION.SDK_INT >= 30){
            isNeedUseDocument = true;
        }
        Log.d(TAG, "checkIsNeedDocument: isNeedUseDoc "+isNeedUseDocument);
        return isNeedUseDocument;
    }

    public static String[] getDatadirItemPath(String path){
        Log.d(TAG, "getItemPath: "+ Arrays.toString(path.replace("/storage/emulated/0/Android/data/", "").split(File.separator)));
        return path.replace("/storage/emulated/0/Android/data/", "").split(File.separator);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static DocumentFile[] getdestDocumentFileArr(DocumentFile documentFile, String path, Context context) {
        long start_time = System.currentTimeMillis();
        Log.d(TAG, "getdestDocumentFileArr:path " + path);
        String[] pathArr = new String[0];
        if (path.startsWith("/storage/emulated/0/Android/data/")){
            pathArr = path.replace("/storage/emulated/0/Android/data/", "").split(File.separator);
            if (pathArr[0].equals("")) {
                documentFile = getDataDirDocumentFile(context);
                if (documentFile != null) {
                    return documentFile.listFiles();
                }
            }
        }else if (path.startsWith("/storage/emulated/0/Android/obb/")){
            pathArr = path.replace("/storage/emulated/0/Android/obb/", "").split(File.separator);
                if (pathArr[0].equals("")) {
                    documentFile = getObbDirDocumentFile(context);
                    if (documentFile != null) {
                        return documentFile.listFiles();
                    }
                }
        }
        //通过循环递归去寻找目标子目录的documentFile
        for (String s : pathArr) {
            Log.d(TAG, "getdestDocumentFileArr: ForString " + s);
            documentFile = getItemDirDocumentFile(documentFile, s);
        }
        if (documentFile != null) {
            for (DocumentFile h : documentFile.listFiles()) {
                Log.d(TAG, "getdestpathDocument: FileList: " + h.getName());
            }
            long end_time = System.currentTimeMillis();
            Log.d(TAG, "getdestDocumentFileArr: DoTime " + (end_time - start_time));
            return documentFile.listFiles();
        }
        //特殊处理如果传入的path仅仅是data或obb目录的时候，就直接获取data或obb目录的documentFile，而无需继续遍历寻找目标子目录的documentFile
        return null;
    }

    public static DocumentFile getItemDirDocumentFile(DocumentFile documentFile, String itemDirName){
        if (documentFile != null) {
            for (DocumentFile d : documentFile.listFiles()) {
                if (d.isDirectory() && d.getName().equals(itemDirName)) {
                    return d;
                }
            }
        }
        return null;
    }


    public static DocumentFile getDataDirDocumentFile(Context context) {
        SharedPreferences sp = context.getSharedPreferences("DirPermission", Context.MODE_PRIVATE);
        String uri_str = sp.getString("dataUriTree", "");
        if (!TextUtils.isEmpty(uri_str)){
            long start_time = System.currentTimeMillis();
//        if (path.endsWith("/")) {
//            path = path.substring(0, path.length() - 1);
//        }
//        String path2 = path.replace("/storage/emulated/0/Android/data", "").replace("/", "%2F");
//        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A");
//        Log.d(TAG, "getDataDirDoucmentFile: path2 "+path2);
            DocumentFile dataDocumentFile = DocumentFile.fromTreeUri(context, Uri.parse(uri_str));
            long end_time = System.currentTimeMillis();
            Log.d(TAG, "getDataDirDocumentFile: dotime "+(end_time-start_time));
            Log.d(TAG, "getDataDirDocumentFile: isSuccGetDataDocumentFile" + dataDocumentFile);
            return dataDocumentFile;
        }else {
            if (Build.VERSION.SDK_INT >= 30){
                PermissionManager.showDataPermissionTips((Activity)context);
            }
            return null;
        }
    }

    //增加一个方法去判断是否需要获得data/obb的权限放在主线程调用，然后从getObb和Data中移除这部分代码

    public static DocumentFile getObbDirDocumentFile(Context context) {
        SharedPreferences sp = context.getSharedPreferences("DirPermission", Context.MODE_PRIVATE);
        String uri_str = sp.getString("obbUriTree", "");
        if (!TextUtils.isEmpty(uri_str)){
//        if (path.endsWith("/")) {
//            path = path.substring(0, path.length() - 1);
//        }
//        String path2 = path.replace("/storage/emulated/0/Android/data", "").replace("/", "%2F");
//        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A");
//        Log.d(TAG, "getDataDirDoucmentFile: path2 "+path2);
            DocumentFile obbDocumentFile = DocumentFile.fromTreeUri(context, Uri.parse(uri_str));
            Log.d(TAG, "getObbDirDocumentFile: isSuccGetObbDocumentFile" + obbDocumentFile);
            return obbDocumentFile;
        }else {
            if (Build.VERSION.SDK_INT >= 30){
                PermissionManager.showObbPermissionTips((Activity)context);
            }
            return null;
        }
    }

    //判断文件类型并且返回对应的文件icon资源
    public static Drawable getDocumentFileDrawable(DocumentFile documentFile, Context context){
        String fileName = documentFile.getName();
        String filePath = documentFile.getUri().getPath();
        Log.d(TAG, "getDocumentFileDrawable: filepath"+filePath);
        if (fileName.endsWith(".apk")){
            return FileToOperate.getApkIcon(filePath ,context);
        }else if(fileName.endsWith(".txt")){
            return ContextCompat.getDrawable(context, R.drawable.ic_file_txt);
        }else if(fileName.endsWith(".log")){
            return ContextCompat.getDrawable(context, R.drawable.ic_file_txt);
        }else if(fileName.endsWith(".obb")){
            return ContextCompat.getDrawable(context, R.drawable.ic_file_obb);
        }else
            return ContextCompat.getDrawable(context, R.drawable.ic_file_unknown);
    }

}
