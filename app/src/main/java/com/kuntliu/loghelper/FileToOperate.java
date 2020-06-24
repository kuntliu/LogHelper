package com.kuntliu.loghelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.List;

class FileToOperate {


    static File searchSelectedFile(File[] Arr_Files, String fileNameClicked){
        //获取选择的文件
        File SelectedFile = null;
        for (File f : Arr_Files){
            if (f.getName().equals(fileNameClicked)){
                SelectedFile = f;
                break;
            }
        }
        return SelectedFile;
    }

//    根据position删除对应的数据源
    static void deleteFile(File file, List<LogFile> loglist, int position, FileAdapter madapter, Context context){
        if (file != null && file.exists()) {
            boolean isSuccessDeleteFile = file.delete();           //删除文件
            loglist.remove(loglist.get(position));              //删除loglist对应的数据源
            madapter.notifyDataSetChanged();                    //刷新适配器
//            Log.d("isSuccessDeleteFile", String.valueOf(isSuccessDeleteFile));

        }else {
            Toast.makeText(context ,"删除失败，文件不存在", Toast.LENGTH_LONG).show();
        }
    }
    static void shareFile(File SelectedFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity");//通过QQ传给我的电脑
        //适配7.0版本以下的Android系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.kuntliu.loghelper.fileprovider", SelectedFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(SelectedFile));
            context.startActivity(intent);
        }
    }
}
