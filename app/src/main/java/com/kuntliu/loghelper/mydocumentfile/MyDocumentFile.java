package com.kuntliu.loghelper.mydocumentfile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.kuntliu.loghelper.FileToOperate;
import com.kuntliu.loghelper.LogFile;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.arraylistsort.ArrayListSort;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyDocumentFile {


    public static List<LogFile> getDocumentFileList(String path, boolean isNeedUseDoc, Context context) {
        List<LogFile> fileList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 30 && isNeedUseDoc) {
            DocumentFile[] documentFilesArr = getdestDocumentFileArr(getDataDirDocumentFile(context, path), getDatadirItemPath(path));
            if (documentFilesArr != null) {
                for (DocumentFile df : documentFilesArr) {
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
                        LogFile log = new LogFile(getDocumentFileDrawable(df, context), documentFileName, df.length(), df.lastModified(), apk_version);
                        fileList.add(log);
                        Log.d(TAG, "getDocumentFileList: "+fileList);
                    }
                }
                //对集合内元素进行排序
                fileList = new ArrayListSort().fileSort(fileList, MyPreferences.getSharePreferencesSortData("sort_setting", context)[0], MyPreferences.getSharePreferencesSortData("sort_setting", context)[1]);
            }
        }
        return fileList;
    }

    public static boolean checkIsNeedDocument(String path){
        boolean isNeedUseDocument = false;
        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android/data/";
        Log.d(TAG, "checkIsNeedDocument: path2 "+path2);
        if (path.startsWith(path2) && Build.VERSION.SDK_INT >= 30){
            isNeedUseDocument = true;
        }
        Log.d(TAG, "checkIsNeedDocument: isNeedUsaDoc "+isNeedUseDocument);
        return isNeedUseDocument;
    }

    public static String[] getDatadirItemPath(String path){
//        Log.d(TAG, "getItemPath: "+ Arrays.toString(path.replace("/storage/emulated/0/", "").split(File.separator)));
        return path.replace("/storage/emulated/0/Android/data/", "").split(File.separator);
    }

    //递归调用，一层层目录进去找到对象目录的listFiles()
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static DocumentFile[] getdestDocumentFileArr(DocumentFile documentFile, String[] path) {
//            Log.d(TAG, "getdestDocumentFileArr:path " + Arrays.toString(path));
//            for (String s : path) {
//                for (DocumentFile d : documentFile.listFiles()) {
//                    if (d.isDirectory() && d.getName().equals(s)) {
//                        getdestDocumentFileArr(d, path);
//                    }
//                }
//           }
        for (DocumentFile d: documentFile.listFiles()){
            if (d.isDirectory() && d.getName().equals("com.activision.callofduty.shooter")){
                Log.d(TAG, "com.activision.callofduty.shooter"+d.listFiles());
                for (DocumentFile e:d.listFiles()){
                    if (e.isDirectory() && e.getName().equals("cache")){
                        Log.d(TAG, "cache"+e.listFiles());
                        for (DocumentFile f:e.listFiles()){
                            if (f.isDirectory() && f.getName().equals("Cache")){

                                Log.d(TAG, "Cache"+e.listFiles());
                                for (DocumentFile g:f.listFiles()){
                                    if (g.isDirectory() && g.getName().equals("Log")){

                                        Log.d(TAG, "Log"+e.listFiles());
                                        for (DocumentFile h: g.listFiles()){
                                            Log.d(TAG, "getdestpathDocument: logFileList： "+h.getName());
                                        }
                                        return g.listFiles();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return documentFile.listFiles();
    }

    //转换至uriTree的路径
    public static DocumentFile getDataDirDocumentFile(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/Android/data", "").replace("/", "%2F");

        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2);
        Log.d(TAG, "getDataDirDoucmentFile: path2 "+path2);

        DocumentFile d = DocumentFile.fromTreeUri(context, uri);



        //        if (d != null){
//           //        d = DocumentFile.fromFile(new File(path));
//            Log.d(TAG, "getDataDirDoucmentFile: isDir "+d.isDirectory() );
//            Log.d(TAG, "getDataDirDoucmentFile: isCanread "+d.canRead() );
//            Log.d(TAG, "getDataDirDoucmentFile: Name "+d.getName() );
            for (DocumentFile e :d.listFiles()){
                Log.d(TAG, "getDataDirDoucmentFile: getDestDirFileList "+e.getName());
            }
//        }
        return d;
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
