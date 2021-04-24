package com.kuntliu.loghelper;



import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kuntliu.loghelper.mydialog.CopyProgressBarDialog;
import com.kuntliu.loghelper.mydialog.MyConfirmCopyDialog;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class ObbFile {


    public void copyObbFile(final File selectedObbFile, String obbFileNameCilcked, final Context context){

        String copyFileDescPath = getSelectedObbFileDescPath(obbFileNameCilcked, context);  //获取已选择的obb文件要复制的目标路径
        if (!copyFileDescPath.equals("")) {
            final File descFile = new File(copyFileDescPath + selectedObbFile.getName());  //完整的目标文件对象

            if (!isExisted_DirCopyFileDescPath(copyFileDescPath)) {
                //如果复制的目标目录不存在就先创建目录
                mkCopyFileDir(copyFileDescPath);
            }
            //执行复制操作前需要判断目标目录的obb文件是否已存在
            if (!copyDescFile_isExisted(obbFileNameCilcked, context)) {
                String copyFileSize = Transform.transformSize(selectedObbFile.length());

                MyConfirmCopyDialog.showConfirmCopyDialog(context, selectedObbFile.getName(), copyFileSize, copyFileDescPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
                    @Override
                    public void clickCancel() {
                    }
                    @Override
                    public void clickConfirm() {
                        //复制文件前先判断剩余空间是否足够，为了保险，继续预留300M的空间来进行判断
                        if (getFreespace() - 300 * 1024 * 1024 > selectedObbFile.length()) {

                            View view = CopyProgressBarDialog.showCopyProgressBar(context, selectedObbFile.getName());
                            final TextView tv_precent = view.findViewById(R.id.CopyPrecent);

                            final ProgressBar progressBar = view.findViewById(R.id.CopyProgressbar);
                            final Handler handler = new Handler(Looper.getMainLooper());

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long transforSize = 0;
                                    int Progress;
                                    FileChannel inputChannel = null;
                                    FileChannel outputChannel = null;
                                    try {
                                        inputChannel = new FileInputStream(selectedObbFile).getChannel();
                                        outputChannel = new FileOutputStream(descFile).getChannel();

                                        ByteBuffer buffer = ByteBuffer.allocate(4096);

                                        while (inputChannel.read(buffer) != -1) {
                                            buffer.flip();
                                            transforSize += outputChannel.write(buffer);
                                            Progress = (int) (transforSize * 100 / selectedObbFile.length());
//                                                Log.d("CopyProgress", "CopyProgress"+Progress);
                                            final int finalProgress = Progress;
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_precent.setText(String.format(finalProgress + "%s", context.getResources().getString(R.string.text_precent)));
                                                    progressBar.setProgress(finalProgress);
                                                }
                                            });
                                            buffer.clear();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (inputChannel != null) {
                                            try {
                                                inputChannel.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (outputChannel != null) {
                                            try {
                                                outputChannel.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    CopyProgressBarDialog.dismissCopyProgressBar();
                                }
                            }).start();
                        } else {
                            MyConfirmCopyDialog.dismissConfirmCopyDialog();
                            Toast.makeText(context, "存储空间不足，请清理后尝试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(context, "目标目录已存在该obb文件", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //获取存储的剩余空间
    private long getFreespace(){
        //        Log.d("getFreespace", "getFreespace: "+FileSizeTransform.Tansform(Freespace));
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    //判断目录是否存在
    private boolean isExisted_DirCopyFileDescPath(String path){
        File file = new File(path);
        return file.exists();
    }
    //如果目录不存在就创建目标目录
    private void mkCopyFileDir(String path){
        boolean dir_Existed = isExisted_DirCopyFileDescPath(path);
        File file = new File(path);
        if (!dir_Existed) {
            boolean isSuccess = file.mkdirs();
//            Log.d("DirisSuccess", "DirisSuccess"+isSuccess);
        }
    }

    //获取已选择的obb文件要复制的目标路径
    private String getSelectedObbFileDescPath(String fileName, Context context) {
        String descPath = "";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (fileName != null) {
            //通过正则表达式去获取obb路径
//            String pattern = "(com).\\w+(.+(?=.obb))";
            String pattern = MyPreferences.getSharePreferencesStringData("private_obb_re", context);
            Log.d(TAG, "pattern: "+pattern);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(fileName);
            Log.d(TAG, "getSelectedObbFileDescPath: "+m);
            if (m.find()){
                descPath = sdPath +
                        File.separator + "Android" + File.separator + "obb" + File.separator + m.group() + File.separator;
                Log.d(TAG, "getSelectedObbFileDescPath: "+descPath);
            }else {
                Toast.makeText(context, "获取目标目录失败,请在设置中修改正则表达式后重试", Toast.LENGTH_SHORT).show();
            }
        }
        return descPath;
    }

    //根据要复制的文件名去判断复制的目标目录下是否已存在obb文件
    private boolean copyDescFile_isExisted(String fileNameClicked, Context context){
        boolean isExisted = false;
        File[] obbDirfile;
        String path = getSelectedObbFileDescPath(fileNameClicked, context);
        File file1 = new File(path);
        obbDirfile = file1.listFiles();
        if (obbDirfile != null) {
            for (File f : obbDirfile) {
                if (f.getName().equals(fileNameClicked)){
                    isExisted = true;
                }
            }
        }
        return isExisted;
    }
}



