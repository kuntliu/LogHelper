package com.kuntliu.loghelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ObbActivity extends AppCompatActivity {
    String path_SdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    List<LogFile> ObbFiles;
    File[] obbfiles;

    FileAdapter madapter;
    String FileSize_str;
    String Time_str;
    ListView obblistview = null;

    File SelectedObbFile;
    String CopyFileDescPath;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obb);
        obblistview = findViewById(R.id.item_obb);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        TextView tv_CopyRate = findViewById(R.id.CopyRate);
//        final TextView tv_CopyPrecent = findViewById(R.id.CopyPrecent);

        ObbFiles = new ArrayList<>();
        File file = new File(path_SdcardRoot);
        obbfiles = file.listFiles();
        final int logiconID = getResources().getIdentifier("icon_file","drawable","com.kuntliu.loghelper");//需要传入资源id


        if(obbfiles != null) {
            for (File f : obbfiles) {
                //过滤掉文件夹，并且找到obb文件
                if (f.isFile() && f.getName().endsWith(".obb") || f.getName().endsWith("apk")) {
                    FileSize_str = FileSizeTransform.Tansform(f.length());
                    Time_str = MySimpleDateFormat.transFormTime(f.lastModified());
                    LogFile obbfile = new LogFile(logiconID, f.getName(), FileSize_str, Time_str);
                    ObbFiles.add(obbfile);
                }
            }
            if (ObbFiles.size() == 0){
                Toast.makeText(ObbActivity.this, "根目录无obb和apk文件", Toast.LENGTH_SHORT).show();
            }
            madapter = new FileAdapter(ObbActivity.this, ObbFiles);
            obblistview.setAdapter(madapter);
            obblistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    String ObbFileNameCilcked = ObbFiles.get(position).getFile_name();
                    SelectedObbFile = FileToOperate.searchSelectedFile(obbfiles, ObbFileNameCilcked);

                    final PopupMenu popup = new PopupMenu(ObbActivity.this, view);
                    getMenuInflater().inflate(R.menu.menu_delete_obb, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.action_delete_obb) {
                                FileToOperate.deleteFile(SelectedObbFile, ObbFiles, position, madapter, ObbActivity.this);
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return true;
                }
            });



            obblistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String ObbFileNameCilcked = ObbFiles.get(i).getFile_name();
                    SelectedObbFile = FileToOperate.searchSelectedFile(obbfiles, ObbFileNameCilcked);

                    //如果点击的是APK文件则调用安装器进行安装
                    InstallAPK(SelectedObbFile);

                    //如果点击的是obb文件则进入复制文件流程
                    if (SelectedObbFile.getName().endsWith(".obb")) {
                        CopyFileDescPath = GetSelectedObbFileDescPath(SelectedObbFile);  //获取已选择的obb文件要复制的目标路径
                        if (CopyFileDescPath != null) {
                            final File descFile = new File(CopyFileDescPath + SelectedObbFile.getName());  //完整的目标文件对象


                            if (!isExisted_DirCopyFileDescPath(CopyFileDescPath)) {
                                //如果复制的目标目录不存在就先创建目录
                                mkCopyFileDirs(CopyFileDescPath);
                            }
                            //执行复制操作前需要判断目标目录的文件是否已存在
                            if (!Existed_CopeDescFile(SelectedObbFile, ObbFileNameCilcked)) {
                                String copyFileSize = FileSizeTransform.Tansform(SelectedObbFile.length());

                                MyConfirmCopyDialog.showConfirmCopyDialog(ObbActivity.this, SelectedObbFile.getName(), copyFileSize, CopyFileDescPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
                                    @Override
                                    public void clickCancel() {
                                    }

                                    @Override
                                    public void clickConfirm() {
                                        //复制文件前先判断剩余空间是否足够，为了保险，继续预留300M的空间来进行判断
                                        if (GetFreespace() - 300 * 1024 * 1024 > SelectedObbFile.length()) {
                                            View view = CopyProgressBarDialog.showCopyProgressBar(ObbActivity.this, SelectedObbFile.getName());
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
                                                        inputChannel = new FileInputStream(SelectedObbFile).getChannel();
                                                        outputChannel = new FileOutputStream(descFile).getChannel();

                                                        ByteBuffer buffer = ByteBuffer.allocate(4096);

                                                        while (inputChannel.read(buffer) != -1) {
                                                            buffer.flip();
                                                            TansforSize += outputChannel.write(buffer);
                                                            Progress = (int) (TansforSize * 100 / SelectedObbFile.length());
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
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
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
                }
            });
        }
    }

    //获取存储的剩余空间
    private static long GetFreespace(){
        //        Log.d("GetFreespace", "GetFreespace: "+FileSizeTransform.Tansform(Freespace));
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    //判断目录是否存在
    private boolean isExisted_DirCopyFileDescPath(String path){
        File file = new File(path);
        return file.exists();
    }
    //如果目录不存在就创建目标目录
    private void mkCopyFileDirs(String path){
        boolean Dir_Existed = isExisted_DirCopyFileDescPath(path);
        File file = new File(path);
        if (!Dir_Existed) {
            boolean isSuccess = file.mkdirs();
//            Log.d("DirisSuccess", "DirisSuccess"+isSuccess);
        }
    }

    //获取已选择的obb文件要复制的目标路径
    private String GetSelectedObbFileDescPath(File file) {
        String CopyFileDescPath = null;
        if (file != null) {
            if (file.getName().contains("com.activision.callofduty.shooter")) {
                CopyFileDescPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.activision.callofduty.shooter" + File.separator;
            } else if (file.getName().contains("com.garena.game.codm")) {
                CopyFileDescPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.garena.game.codm" + File.separator;
            } else if (file.getName().contains("com.tencent.tmgp.kr.codm")) {
                CopyFileDescPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.tencent.tmgp.kr.codm" + File.separator;
            } else if (file.getName().contains("com.vng.codmvn")) {
                CopyFileDescPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Android" + File.separator + "obb" + File.separator + "com.vng.codmvn" + File.separator;
            } else {
                Toast.makeText(ObbActivity.this, "获取复制目标目录失败", Toast.LENGTH_SHORT).show();
            }
        }
        return CopyFileDescPath;
    }

    //判断复制的目标文件是否已存在
    private boolean Existed_CopeDescFile(File file, String FileNameClicked){
        boolean isExisted = false;
        File[] obbDirfile;
        String path = GetSelectedObbFileDescPath(file);
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

    private void InstallAPK(File file){
        if (file.getName().endsWith(".apk")){
            Log.d("ObbActivity", "InstallAPK: here");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Uri uri = FileProvider.getUriForFile(ObbActivity.this, "com.kuntliu.loghelper.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            ObbActivity.this.startActivity(intent);
        }
    }

}



