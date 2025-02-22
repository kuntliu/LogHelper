package com.kuntliu.loghelper;



import android.content.Context;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mydialog.CopyProgressBarDialog;
import com.kuntliu.loghelper.mydialog.MyConfirmCopyDialog;
import com.kuntliu.loghelper.mydocumentfile.MyDocumentFile;
import com.kuntliu.loghelper.mypermission.PermissionManager;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class ObbFile {


    public void copyObbFile(final File selectedObbFile, final List<MyFile> filelist, final int position, final Context context, final MyRecycleViewAdapter adapter){
        String obbFileNameCilcked = filelist.get(position).getFile_name();
        final DocumentFile documentFile = MyDocumentFile.getDataDirDocumentFile(context);

        final String copyFileDestPath = getSelectedObbFileDescPath(obbFileNameCilcked, context);  //获取已选择的obb文件要复制的目标路径
        Log.d(TAG, "copyObbFile: "+copyFileDestPath);
        if (!copyFileDestPath.equals("")) {
            final File destFile = new File(copyFileDestPath + selectedObbFile.getName());  //完整的目标文件对象

            Log.d(TAG, "destFile: "+destFile.getName());

            if (!isExisted_DirCopyFileDescPath(copyFileDestPath)) {
                //如果复制的目标目录不存在就先创建目录
                mkCopyFileDir(copyFileDestPath);
            }
            //执行复制操作前需要判断目标目录的obb文件是否已存在
            if (!copyDescFile_isExisted(obbFileNameCilcked, context)) {
                String copyFileSize = Transform.transformSize(selectedObbFile.length());
                final Handler handler = new Handler(Looper.getMainLooper());
                MyConfirmCopyDialog.showConfirmCopyDialog(context, selectedObbFile.getName(), copyFileSize, copyFileDestPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
                    @Override
                    public void clickCancel() {
                    }
                    @Override
                    public void clickMove() {
                        if (getFreespace() - 300 * 1024 * 1024 > selectedObbFile.length()) {
                            CopyProgressBarDialog.showCircleProgressBar(context);   //显示耗时的转菊花效果
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //renameTo()在不同的机器上可能表现不一样，部分机器的文件移动可能比较耗时，需要开启子线程进行操作
                                    boolean isSuccessMove = selectedObbFile.renameTo(destFile);
                                    Log.d(TAG, "selectedObbFile: " + selectedObbFile + "\ndescFile: " + destFile);
                                    Log.d(TAG, "fileMoveResult: " + isSuccessMove);
                                    if (isSuccessMove) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                filelist.remove(position);
                                                adapter.notifyItemRangeRemoved(position, filelist.size() - position);
                                                Toast.makeText(context, "obb文件移动完成(10001)", Toast.LENGTH_LONG).show();
                                                CopyProgressBarDialog.dismissCopyProgressBar();  //文件移动完成后关闭耗时的转菊花效果
                                            }
                                        });
                                    } else {
                                        //移动用renameTo方法失败的话就用File.move方法，如果还是移动失败，就用复制文件后删除源文件进行移动
                                        //由于Files.copy()需要至少sdk为26及以上才能使用，因此增加下面的判断
                                        if (Build.VERSION.SDK_INT >= 26) {
                                            final Path sourcePath = selectedObbFile.toPath();
                                            Path destPath = destFile.toPath();
                                            try {
//                                              测试时发现move方法在部分机器不会生效，更稳妥的方法是使用copy()
                                                Path targrtPath_copy = Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                                                Log.d(TAG, "Files.copy: targrtPath " + targrtPath_copy);
                                                Log.d(TAG, "Files.copy: destPath " + destPath);
                                                if (targrtPath_copy.equals(destPath)) {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //Toast不能在子线程中使用，否则会抛异常，需要调用handler.post()通知主线程进行Toast的显示
                                                            Toast.makeText(context, "obb文件移动完成(10002)", Toast.LENGTH_LONG).show();
                                                            //移动完成后删除源文件
                                                            FileToOperate.deleteFile(selectedObbFile, documentFile, filelist, position, adapter, MyDocumentFile.checkIsNeedDocument(selectedObbFile.getPath()), context);
                                                            CopyProgressBarDialog.dismissCopyProgressBar();
                                                        }
                                                    });
                                                } else {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(context, "obb文件移动失败(10002)", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "obb文件移动失败(10001)", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            }).start();
                        }else {
                            Toast.makeText(context, "存储空间不足，请清理后尝试", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void clickConfirm() {
//                        复制文件前先判断剩余空间是否足够，为了保险，继续预留300M的空间来进行判断
                        if (getFreespace() - 300 * 1024 * 1024 > selectedObbFile.length()) {
                            //获取dialog的view
                            final View view = CopyProgressBarDialog.showCopyProgressBar(context, selectedObbFile.getName());
                            final TextView tv_precent = view.findViewById(R.id.CopyPrecent);
                            final ProgressBar progressBar = view.findViewById(R.id.CopyProgressbar);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long transformSize = 0;
                                    int progress;
                                    FileChannel inputChannel = null;
                                    FileChannel outputChannel = null;
                                    try {
                                        inputChannel = new FileInputStream(selectedObbFile).getChannel();
                                        outputChannel = new FileOutputStream(destFile).getChannel();

                                        ByteBuffer buffer = ByteBuffer.allocate(4096);

                                        while (inputChannel.read(buffer) != -1) {
                                            buffer.flip();
                                            transformSize += outputChannel.write(buffer);
                                            progress = (int) (transformSize * 100 / selectedObbFile.length());
//                                                Log.d("CopyProgress", "CopyProgress"+Progress);
                                            final int finalProgress = progress;
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

    private void copyFile(File sourceFile, Context context, Uri uri) {
        if (uri == null) {
            return;
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            os = context.getContentResolver().openOutputStream(uri);
            if (os == null) {
                return;
            }
            int read = 0;
            if (sourceFile.exists()) { // 文件存在时
                try {
                    is = new FileInputStream(sourceFile); // 读入原文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        if ((read = is.read(buffer)) == -1) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        os.write(buffer, 0, read);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.d("dirIsSuccessful", "dirIsSuccessful "+isSuccess);
        }
    }

    //获取已选择的obb文件要复制的目标路径
    private String getSelectedObbFileDescPath(String fileName, Context context) {
        String descPath = "";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (fileName != null) {
            //通过正则表达式去获取obb路径
//            String pattern = "(com).\\w+(.+(?=.obb))";
            String pattern = MyPreferences.getSharePreferencesStringData("private_obb_re", "(com).\\w+(.+(?=.obb))", context);
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
        File[] obbDirfile;
        String path = getSelectedObbFileDescPath(fileNameClicked, context);
        File file1 = new File(path);
        obbDirfile = file1.listFiles();
        if (obbDirfile != null) {
            for (File f : obbDirfile) {
                if (f.getName().equals(fileNameClicked)){
                    return true;
                }
            }
        }
        return false;
    }

}



