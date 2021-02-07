package com.kuntliu.loghelper;

import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.kuntliu.loghelper.arraylistsort.ArrayListSort;
import com.kuntliu.loghelper.myadapter.MyRecycleViewApater;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileToOperate {
    private File file;

    public List<LogFile> getFileList(String path, File[] arrFiles, Context context, TextView tv)  {
        List<LogFile> fileList = new ArrayList<>();   //初始化数据
        arrFiles = getFileArr(path);
        //判断path目录是否存在
        if (file.exists()){
            if (arrFiles == null || arrFiles.length == 0){  //踩坑描述：要先判断arrFiles是否为null，然后再判断后面的length == 0，否侧可能会出现空指针
                fileList.clear();
                tv.setVisibility(View.VISIBLE);
                tv.setText("当前目录为空");
//                Toast.makeText(this, "当前目录为空", Toast.LENGTH_SHORT).show();
            }else {
                for (File f : arrFiles) {
                    tv.setVisibility(View.GONE);
//                Log.d("FileList", f.toString());
                    if (f.isFile() && !f.getName().startsWith(".")) {    //过滤：“.”开头的隐藏文件和path目录下的文件夹
                        String fileSize_str = FileSizeTransform.Tansform(f.length());      //获取文件大小并且进行显示单位转换
                        String time_str = MySimpleDateFormat.transFormTime(f.lastModified());    //获取文件最后修改时间并且进行时间格式转换

                        String fileType_str = FileToOperate.getFileSuffix(f);//获取文件后缀，string类型
                        int iconResourcesId = FileToOperate.getFileIconResourceId(fileType_str, context);//根据文件后缀去使用icon图标

                        LogFile log = new LogFile(iconResourcesId, f.getName(), fileSize_str, time_str);
//                    Log.d("fileName:fileSize", f.getName()+":"+ f.length());
                        fileList.add(log);
                    }
                }
                fileList = new ArrayListSort().stringSore(fileList);   //对集合内元素进行排序

//                MyRecycleViewApater adapter = new MyRecycleViewApater(fileList, this);
//                recycleview.setAdapter(adapter);
//                madapter = new FileAdapter(MainActivity.this, loglist);
//                mylistview.setAdapter(madapter);
//                mylistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                        final String fileNameClicked = loglist.get(position).getFile_name();   //通过item的id使用getFile_name()获取要操作的文件名
//                        Log.d("ItemClicked", fileNameClicked);
//                        popup = new PopupMenu(MainActivity.this, view);
//                        getMenuInflater().inflate(R.menu.menu_clicklist, popup.getMenu());
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                File SelectedFile = FileToOperate.searchSelectedFile(Arr_Files, fileNameClicked);    //获取选择的文件
//                                switch (item.getItemId()) {
//                                    case R.id.menu_delete:
//                                        FileToOperate.deleteFile(SelectedFile, loglist, position, madapter, MainActivity.this);
//                                        break;
//                                    case R.id.menu_detail:
//                                        String FileSize = FileSizeTransform.Tansform(SelectedFile.length());
//                                        MyFileDetailInfoDialog.showFileDetailInfoDialog(MainActivity.this, SelectedFile.getName(), FileSize, SelectedFile.getAbsolutePath().replace(SelectedFile.getName(), ""));
//                                        break;
//                                    case R.id.menu_share:
//                                        FileToOperate.shareFile(SelectedFile, MainActivity.this);
//                                        break;
//                                }
//                                return false;
//                            }
//                        });
//                        popup.show();
//                        return false;
//                    }
//                });
            }
        }else {
            fileList.clear();
            tv.setVisibility(View.VISIBLE);
            tv.setText("当前目录不存在");
//            Toast.makeText(MainActivity.this, "当前目录不存在", Toast.LENGTH_SHORT).show();
//            Log.d("IsNofile", "ture");
        }
        return fileList;
    }

    //获取path目录下的文件（数组类型）
    public File[] getFileArr(String path) {
        file = new File(path);
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

    //获取文件后缀
    public static String getFileSuffix(File file){
        String fileName = file.getName();
        if (fileName.endsWith(".apk")){
            return "apk";
        }else if(fileName.endsWith(".txt")){
            return "txt";
        }else if(fileName.endsWith(".log")){
            return "log";
        }else if(fileName.endsWith(".obb")){
            return "obb";
        }else
            return "";
    }

    //根据文件后缀获取对应类型文件的icon资源id
    public static int getFileIconResourceId(String fileSuffix, Context context) {
        if (fileSuffix.equals("txt") || fileSuffix.equals("log")){
            return context.getResources().getIdentifier("ic_file_txt","drawable","com.kuntliu.loghelper");
        }else if(fileSuffix.equals("obb")){
            return context.getResources().getIdentifier("ic_file_obb","drawable","com.kuntliu.loghelper");
        }else{
            return context.getResources().getIdentifier("ic_file_unknown","drawable","com.kuntliu.loghelper");
        }
    }


    //根据position删除对应的数据源
    public static void deleteFile(File file, List<LogFile> loglist, int position, MyRecycleViewApater madapter, Context context){
        if (file != null && file.exists()) {
            boolean isSuccessDeleteFile = file.delete();           //删除文件
            loglist.remove(loglist.get(position));              //删除loglist对应的数据源
            madapter.notifyDataSetChanged();                    //刷新适配器
//            Log.d("isSuccessDeleteFile", String.valueOf(isSuccessDeleteFile));

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
}
