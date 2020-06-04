package com.kuntliu.loghelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String path_SdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    String path_west = Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"Android"+File.separator+"data"+File.separator+"com.activision.callofduty.shooter"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
    String path_garena = Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"Android"+File.separator+"data"+File.separator+"com.garena.game.codm"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
    String path_korea = Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"Android"+File.separator+"data"+File.separator+"com.tencent.tmgp.kr.codm"+File.separator+"cache"+File.separator+"Cache"+File.separator+"Log"+File.separator ;
    private AlertDialog alertDialog;
    String[] permissions = new String[]
            {Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.WRITE_EXTERNAL_STORAGE};    //需要申请的权限
    List<String> permissions_rejected = new ArrayList<>();//保存未授予权限
    int PERMISSION_CODE = 1000;
    int SETTING_CODE = 1001;
    File file;

    List<LogFile> loglist = null;
    File[] LogFiles = null;
    FileAdapter madapter;
    ListView mylistview;
    PopupMenu popup;
    String FileSize_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermission();
        }
        Spinner spinner = findViewById(R.id.spinner_item);
        Resources resources = getResources();
        String[] arr_path = resources.getStringArray(R.array.arr_path);
        //踩坑描述：android.R.layout.simple_list_item_1是系统提供的下拉列表样式，也可以自定义
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , arr_path);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mylistview = findViewById(R.id.log_list);

        Button button = (Button)findViewById(R.id.obbButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ObbActivity.class);
                startActivity(intent);
            }
        });

    }

    //申请权限
    private void requestPermission() {
        permissions_rejected.clear();
        //判断是否有权限
        for (int i = 0; i < permissions.length; i++){
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissions_rejected.add(permissions[i]);
                Log.d("Permissions_rejected", permissions_rejected.toString());
            }
        }
        if (!permissions_rejected.isEmpty()){
            ActivityCompat.requestPermissions(this, permissions_rejected.toArray(new String[permissions_rejected.size()]), PERMISSION_CODE);
        }
        else {
            Log.d("HadPermissionCheck","ALLPERMISSION");
        }
    }

    //权限窗口，用户操作的结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasRejectPermission = false;
        if (PERMISSION_CODE == requestCode){
            for (int i = 0; i < grantResults.length; i++){
                //用户选择“允许”
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.d(permissions[i], "onRequestPermissionsResult:ture ");
                }if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    hasRejectPermission = true;
                }
            }
            if(hasRejectPermission){
                showDialogAndGotoSetting();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTING_CODE){       //从设置返回后再次进行权限判断
            requestPermission();
        }
    }


    private void showDialogAndGotoSetting(){
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示")
                    .setMessage("缺少权限")
                    .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertDialog != null) {//先关闭弹窗再跳转
                                alertDialog.dismiss();
                            }
                            gotoSetting();
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            System.exit(0);
                        }
                    });
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示")
                .setMessage("缺少权限")
                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void gotoSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTING_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Toast.makeText(MainActivity.this, "Current Version:1.0 \n Developed by v_kuntliu", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void GetAllFile(final String path) throws IOException {

        loglist = new ArrayList<>();   //初始化数据
        Log.d("filepath", path);
        file = new File(path);
        LogFiles =  file.listFiles();
        int logiconID = getResources().getIdentifier("icon_file","drawable","com.kuntliu.loghelper");//需要传入资源id
        if(LogFiles != null && file.exists()){
            for(File f : LogFiles) {
                Log.d("Filelist", f.toString());
                if (f == null){
                    Toast.makeText(this, "当前目录为空", Toast.LENGTH_SHORT).show();
                }
                if (f.isFile() && !f.getName().startsWith(".")) {    //只需要文件并且过滤“.”开头的隐藏文件
                    FileSizeTransform fs = new FileSizeTransform();
                    FileSize_str= fs.Tansform(f.length());      //文件大小单位转换
                    LogFile log = new LogFile(logiconID, f.getName(), FileSize_str, new SimpleDateFormat("yyyy/M/d H:m").format(f.lastModified()));
                    Log.d("fileName:fileSize", f.getName()+":"+ f.length());
                    loglist.add(log);
                }
            }
            Collections.sort(loglist, new Comparator<LogFile>() {
                @Override
                public int compare(LogFile f1, LogFile f2) {
                    if (f1.getFile_name().compareTo(f2.getFile_name()) == 0){
                        return 0;
                    }else
                        return f1.getFile_name().compareTo(f2.getFile_name());
                    }
                });
            madapter = new FileAdapter(MainActivity.this, loglist);
            mylistview.setAdapter(madapter);
            mylistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final String fileNameClicked = String.valueOf(loglist.get(position).getFile_name());   //通过item的id使用getFile_name()获取要操作的文件名
                    Log.d("ItemClicked", fileNameClicked);
                    popup = new PopupMenu(MainActivity.this, view);
                    getMenuInflater().inflate(R.menu.menu_clicklist, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //获取选择的文件
                            File SelectedFile = null;
                            for (File f : LogFiles){
                                if (f.getName().equals(fileNameClicked)){
                                    SelectedFile = f;
                                    break;
                                }
                            }
                            switch (item.getItemId()){
                                case R.id.menu_delete:
                                    if (SelectedFile != null && SelectedFile.exists()) {
                                        boolean isSuccessDeleteFile = SelectedFile.delete();           //删除文件
                                        loglist.remove(loglist.get(position));              //删除loglist对应的数据源
                                        madapter.notifyDataSetChanged();                    //刷新适配器
                                        Log.d("isSuccessDeleteFile", String.valueOf(isSuccessDeleteFile));
                                        break;
                                    }else {
                                        Toast.makeText(MainActivity.this ,"删除失败，文件不存在", Toast.LENGTH_LONG).show();
                                    }
                                case R.id.menu_detail:
                                    Toast.makeText(MainActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.menu_share:
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity");//传给我的电脑
                                    //适配7.0版本以下的Android系统
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(MainActivity.this, "com.kuntliu.loghelper.fileprovider", SelectedFile));
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(intent);
                                    }else {
                                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(SelectedFile));
                                        startActivity(intent);
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return false;
                }
            });
        }else {
            loglist.clear();
            madapter = new FileAdapter(MainActivity.this, loglist);
            mylistview.setAdapter(madapter);
            Toast.makeText(MainActivity.this, "当前目录不存在", Toast.LENGTH_SHORT).show();
            Log.d("IsNofile", "ture");
        }
    }
    private void fileSizeTransform(){

    }

    private static void copyFile(File source, File desc)throws IOException{
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(desc).getChannel();
        } finally {
            assert inputChannel != null;
            assert outputChannel != null;
            inputChannel.close();
            outputChannel.close();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        int itemId_selected = parent.getSelectedItemPosition();
            switch (position){
                case 1:
                    try {
                        GetAllFile(path_garena);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        GetAllFile(path_korea);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        GetAllFile(path_SdcardRoot);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        GetAllFile(path_west);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        Log.d("ItemSelected",  parent.getSelectedItem().toString());
        Log.d("ItemSelectedPosition", String.valueOf(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
