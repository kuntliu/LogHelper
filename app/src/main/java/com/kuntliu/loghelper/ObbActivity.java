package com.kuntliu.loghelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SNIHostName;

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

        FileSizeTransform fileSizeTransform = new FileSizeTransform();
        MySimpleDateFormat sdf = new MySimpleDateFormat();

        if(obbfiles != null) {
            for (File f : obbfiles) {
                if (f.isFile() && f.getName().endsWith(".obb")) {

                    FileSize_str = fileSizeTransform.Tansform(f.length());
                    Time_str = sdf.transFormTime(f.lastModified());

                    LogFile obbfile = new LogFile(logiconID, f.getName(), FileSize_str, Time_str);
                    ObbFiles.add(obbfile);
                }
            }


            final FileToOperate fto = new FileToOperate();
            madapter = new FileAdapter(ObbActivity.this, ObbFiles);
            obblistview.setAdapter(madapter);
            obblistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String ObbFileNameCilcked = ObbFiles.get(i).getFile_name();
                    SelectedObbFile = fto.searchSelectedFile(obbfiles, ObbFileNameCilcked);

                    CopyFileDescPath = GetSelectedObbFileDescPath(SelectedObbFile);  //获取已选择的obb文件要复制的目标路径
                    final File descFile = new File(CopyFileDescPath + SelectedObbFile.getName());  //完整的目标文件对象

                    boolean Existed = isExisted_DirCopyFileDescPath(CopyFileDescPath);
                    if (!Existed) {
                        //如果复制的目标目录不存在就先创建目录
                        mkCopyFileDirs(CopyFileDescPath);
                    }
                    //执行复制操作前需要判断目标目录的文件是否已存在
                    if (!Existed_CopeDescFile(SelectedObbFile, ObbFileNameCilcked)){
                        FileSizeTransform fst = new FileSizeTransform();
                        String copyFileSize = fst.Tansform(SelectedObbFile.length());

                        MyConfirmCopyDialog.showConfirmCopyDialog(ObbActivity.this, SelectedObbFile.getName(), copyFileSize, CopyFileDescPath, new MyConfirmCopyDialog.AlertDialogBtnClickListener() {
                            @Override
                            public void clickCancel() {
                            }

                            @Override
                            public void clickConfirm() {

                                View view = CopyProgressBarDialog.showCopyPrigressBar(ObbActivity.this);
                                final TextView tv_precent = (TextView)view.findViewById(R.id.CopyPrecent);
                                final Handler handler = new Handler();


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        long TansforSize = 0;
                                        int Progress = 0;
                                        FileChannel inputChannel = null;
                                        FileChannel outputChannel = null;
                                        try {
                                            inputChannel = new FileInputStream(SelectedObbFile).getChannel();
                                            outputChannel = new FileOutputStream(descFile).getChannel();

                                            Log.d("inputChannel11", String.valueOf(inputChannel.size()));
                                            Log.d("outputChannel11", String.valueOf(outputChannel.size()));

                                            ByteBuffer buff = ByteBuffer.allocate(4096);

                                            while (inputChannel.read(buff) != -1){
                                                buff.flip();
                                                TansforSize += outputChannel.write(buff);
                                                Progress = (int)(TansforSize * 100/SelectedObbFile.length());
                                                Log.d("jisuan", "fixxxx"+Progress);
                                                final int finalProgress = Progress;
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        tv_precent.setText(finalProgress);
                                                    }
                                                });
                                                buff.clear();
                                            }

                                            Log.d("inputChannel22", String.valueOf(inputChannel.size()));
                                            Log.d("outputChannel22", String.valueOf(outputChannel.size()));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            if (inputChannel != null){
                                                try {
                                                    inputChannel.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }else if (outputChannel != null){
                                                try {
                                                    outputChannel.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }).start();
                            }
                        });
                    }else {
                        Toast.makeText(ObbActivity.this, "目标目录已存在该obb文件", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "根目录没有obb文件", Toast.LENGTH_SHORT).show();
        }
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
        }
    }
    //获取已选择的obb文件要复制的目标路径
    private String GetSelectedObbFileDescPath(File file) {
        String CopyFileDescPath = "";
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
}



