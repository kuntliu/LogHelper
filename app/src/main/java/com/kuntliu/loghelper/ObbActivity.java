package com.kuntliu.loghelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mydialog.CopyProgressBarDialog;
import com.kuntliu.loghelper.mydialog.MyConfirmCopyDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ObbActivity extends AppCompatActivity {
    String path_SdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    List<LogFile> obbFilesList = new ArrayList<>();
    File[] sdCardRootFiles;

    String fileSize_str;
    String fileTime_str;
    String fileName_str;

    File selectedObbFile;
    String copyFileDescPath;

    RecyclerView obblistview;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obb);



        Toolbar toolbar = findViewById(R.id.toolbar_obb);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        obblistview = findViewById(R.id.rv_obb_file);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        obblistview.setLayoutManager(linearLayoutManager);

        sdCardRootFiles = FileToOperate.getFileArr(path_SdcardRoot);
        if(sdCardRootFiles != null) {
            for (File f : sdCardRootFiles) {
                //过滤掉文件夹，并且找到obb文件
                fileName_str = f.getName();
                if (f.isFile() && fileName_str.endsWith(".obb") || fileName_str.endsWith("apk")) {
                    fileSize_str = FileSizeTransform.Tansform(f.length());
                    fileTime_str = MySimpleDateFormat.transFormTime(f.lastModified());

                    String apk_version = "";
                    if (fileName_str.endsWith(".apk")){
                        apk_version = " - 版本 " + FileToOperate.getApkVersion(f.getAbsolutePath(), ObbActivity.this);
                        if(apk_version.equals(" - ")){
                            apk_version = "";
                        }
                    }
                    LogFile obbfile = new LogFile(FileToOperate.getFileDrawable(f, ObbActivity.this), fileName_str, fileSize_str, fileTime_str, apk_version);
                    obbFilesList.add(obbfile);
                }
            }
            if (obbFilesList.size() == 0){
                Toast.makeText(ObbActivity.this, "根目录无obb和apk文件", Toast.LENGTH_SHORT).show();
            }
            MyRecycleViewAdapter madapter = new MyRecycleViewAdapter(obbFilesList,ObbActivity.this);
            obblistview.setAdapter(madapter);
            madapter.setOnItemClickListener(new MyRecycleViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    FileToOperate.installAPK(FileToOperate.searchSelectedFile(sdCardRootFiles, obbFilesList.get(position).getFile_name()), ObbActivity.this);
                }
            });
//            madapter.setOnItemLongClickListener(new MyRecycleViewAdapter.OnItemLongClickListener() {
//                @Override
//                public void onItemLongClick(View view, int position) {
//                    String obbFileNameCilcked = obbFilesList.get(position).getFile_name();
//
//                    selectedObbFile = FileToOperate.searchSelectedFile(sdCardRootFiles, obbFileNameCilcked);
//
//                    //如果点击的是obb文件则进入复制文件流程
//                    if (selectedObbFile.getName().endsWith(".obb")) {
//                        copyFileDescPath = getSelectedObbFileDescPath(selectedObbFile);  //获取已选择的obb文件要复制的目标路径
//                        if (copyFileDescPath != null) {
//                            final File descFile = new File(copyFileDescPath + selectedObbFile.getName());  //完整的目标文件对象
//
//
//                            if (!isExisted_DirCopyFileDescPath(copyFileDescPath)) {
//                                //如果复制的目标目录不存在就先创建目录
//                                mkCopyFileDirs(copyFileDescPath);
//                            }
//                            //执行复制操作前需要判断目标目录的文件是否已存在
//                            if (!copyDescFile_isExisted(selectedObbFile, obbFileNameCilcked)) {
//                                String copyFileSize = FileSizeTransform.Tansform(selectedObbFile.length());
//
//                                MyConfirmCopyDialog.showConfirmCopyDialog(ObbActivity.this, selectedObbFile.getName(), copyFileSize, copyFileDescPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
//                                    @Override
//                                    public void clickCancel() {
//                                    }
//
//                                    @Override
//                                    public void clickConfirm() {
//                                        //复制文件前先判断剩余空间是否足够，为了保险，继续预留300M的空间来进行判断
//                                        if (getFreespace() - 300 * 1024 * 1024 > selectedObbFile.length()) {
//
//                                            View view = CopyProgressBarDialog.showCopyProgressBar(ObbActivity.this, selectedObbFile.getName());
//                                            final TextView tv_precent = view.findViewById(R.id.CopyPrecent);
//
//                                            final ProgressBar progressBar = view.findViewById(R.id.CopyProgressbar);
//                                            final Handler handler = new Handler();
//
//                                            new Thread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    long TansforSize = 0;
//                                                    int Progress;
//                                                    FileChannel inputChannel = null;
//                                                    FileChannel outputChannel = null;
//                                                    try {
//                                                        inputChannel = new FileInputStream(selectedObbFile).getChannel();
//                                                        outputChannel = new FileOutputStream(descFile).getChannel();
//
//                                                        ByteBuffer buffer = ByteBuffer.allocate(4096);
//
//                                                        while (inputChannel.read(buffer) != -1) {
//                                                            buffer.flip();
//                                                            TansforSize += outputChannel.write(buffer);
//                                                            Progress = (int) (TansforSize * 100 / selectedObbFile.length());
////                                                Log.d("CopyProgress", "CopyProgress"+Progress);
//                                                            final int finalProgress = Progress;
//                                                            handler.post(new Runnable() {
//                                                                @Override
//                                                                public void run() {
//                                                                    tv_precent.setText(String.format(finalProgress + "%s", getResources().getString(R.string.text_precent)));
//                                                                    progressBar.setProgress(finalProgress);
//                                                                }
//                                                            });
//                                                            buffer.clear();
//                                                        }
//                                                    } catch (IOException e) {
//                                                        e.printStackTrace();
//                                                    } finally {
//                                                        if (inputChannel != null) {
//                                                            try {
//                                                                inputChannel.close();
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        } else if (outputChannel != null) {
//                                                            try {
//                                                                outputChannel.close();
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        }
//                                                    }
//                                                    CopyProgressBarDialog.dismissCopyProgressBar();
//                                                }
//                                            }).start();
//                                        } else {
//                                            MyConfirmCopyDialog.dismissConfirmCopyDialog();
//                                            Toast.makeText(ObbActivity.this, "存储空间不足，请清理后尝试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                            } else {
//                                Toast.makeText(ObbActivity.this, "目标目录已存在该obb文件", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }
//            });
        }
    }


    public void copyObbFile(final File selectedObbFile, String obbFileNameCilcked){

        copyFileDescPath = getSelectedObbFileDescPath(selectedObbFile);  //获取已选择的obb文件要复制的目标路径
        if (copyFileDescPath != null) {
            final File descFile = new File(copyFileDescPath + selectedObbFile.getName());  //完整的目标文件对象


            if (!isExisted_DirCopyFileDescPath(copyFileDescPath)) {
                //如果复制的目标目录不存在就先创建目录
                mkCopyFileDirs(copyFileDescPath);
            }
            //执行复制操作前需要判断目标目录的obb文件是否已存在
            if (!copyDescFile_isExisted(selectedObbFile, obbFileNameCilcked)) {
                String copyFileSize = FileSizeTransform.Tansform(selectedObbFile.length());

                MyConfirmCopyDialog.showConfirmCopyDialog(ObbActivity.this, selectedObbFile.getName(), copyFileSize, copyFileDescPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
                    @Override
                    public void clickCancel() {
                    }

                    @Override
                    public void clickConfirm() {
                        //复制文件前先判断剩余空间是否足够，为了保险，继续预留300M的空间来进行判断
                        if (getFreespace() - 300 * 1024 * 1024 > selectedObbFile.length()) {

                            View view = CopyProgressBarDialog.showCopyProgressBar(ObbActivity.this, selectedObbFile.getName());
                            final TextView tv_precent = view.findViewById(R.id.CopyPrecent);

                            final ProgressBar progressBar = view.findViewById(R.id.CopyProgressbar);
                            final Handler handler = new Handler();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long TansforSize = 0;
                                    int Progress;
                                    FileChannel inputChannel = null;
                                    FileChannel outputChannel = null;
                                    try {
                                        inputChannel = new FileInputStream(selectedObbFile).getChannel();
                                        outputChannel = new FileOutputStream(descFile).getChannel();

                                        ByteBuffer buffer = ByteBuffer.allocate(4096);

                                        while (inputChannel.read(buffer) != -1) {
                                            buffer.flip();
                                            TansforSize += outputChannel.write(buffer);
                                            Progress = (int) (TansforSize * 100 / selectedObbFile.length());
//                                                Log.d("CopyProgress", "CopyProgress"+Progress);
                                            final int finalProgress = Progress;
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_precent.setText(String.format(finalProgress + "%s", getResources().getString(R.string.text_precent)));
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
                            Toast.makeText(ObbActivity.this, "存储空间不足，请清理后尝试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(ObbActivity.this, "目标目录已存在该obb文件", Toast.LENGTH_SHORT).show();
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
    private void mkCopyFileDirs(String path){
        boolean dir_Existed = isExisted_DirCopyFileDescPath(path);
        File file = new File(path);
        if (!dir_Existed) {
            boolean isSuccess = file.mkdirs();
//            Log.d("DirisSuccess", "DirisSuccess"+isSuccess);
        }
    }

    //获取已选择的obb文件要复制的目标路径
    private String getSelectedObbFileDescPath(File file) {
        String descPath = "";
        if (file != null) {
            if (file.getName().contains("com.activision.callofduty.shooter")) {
                descPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.activision.callofduty.shooter" + File.separator;
            } else if (file.getName().contains("com.garena.game.codm")) {
                descPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.garena.game.codm" + File.separator;
            } else if (file.getName().contains("com.tencent.tmgp.kr.codm")) {
                descPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.tencent.tmgp.kr.codm" + File.separator;
            } else if (file.getName().contains("com.vng.codmvn")) {
                descPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.vng.codmvn" + File.separator;
            } else if (file.getName().contains("com.tencent.tmgp.cod")){
                descPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.tencent.tmgp.cod" + File.separator;
            }else {
                Toast.makeText(ObbActivity.this, "获取复制目标目录失败", Toast.LENGTH_SHORT).show();
            }
        }
        return descPath;
    }

    //根据要复制的文件名去判断复制的目标目录下是否已存在obb文件
    private boolean copyDescFile_isExisted(File file, String FileNameClicked){
        boolean isExisted = false;
        File[] obbDirfile;
        String path = getSelectedObbFileDescPath(file);
        file = new File(path);
        obbDirfile = file.listFiles();
        if (obbDirfile != null) {
            for (File f : obbDirfile) {
                if (f.getName().equals(FileNameClicked)){
                    isExisted = true;
                }
            }
        }
        return isExisted;
    }
}



