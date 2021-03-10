package com.kuntliu.loghelper;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.preference.ListPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kuntliu.loghelper.myadapter.FragmentAdapter;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> myTab_defalut = new ArrayList<>();
    private final ArrayList<String> myPath_defalut = new ArrayList<>();

    private final List<TabFragment> tabFragmentList = new ArrayList<>();


    private AlertDialog alertDialog;
    String[] permissions = new String[]
            {Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.WRITE_EXTERNAL_STORAGE};    //需要申请的权限
    List<String> permissions_rejected = new ArrayList<>();//保存未授予权限
    int PERMISSION_CODE = 1000;
    int SETTING_CODE = 1001;


    TabLayout tab_version;
    ViewPager viewPager;
    FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        tab_version.setupWithViewPager(viewPager,false);

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

//已弃用，改用tab
//        Spinner spinner = findViewById(R.id.spinner_item);
//        Resources resources = getResources();
//        String[] arr_path = resources.getStringArray(R.array.arr_path);
//        //踩坑描述：android.R.layout.simple_list_item_1是系统提供的下拉列表样式，也可以自定义
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr_path);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        Button button = findViewById(R.id.obbButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ObbActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tab_version = findViewById(R.id.tab_version);
        viewPager = findViewById(R.id.viewPage_file);
        viewPager.setOffscreenPageLimit(5);    //默认情况下，viewPager会加载相邻的1页，这里设置为5

        tab_version.setTabMode(TabLayout.MODE_SCROLLABLE);
        tab_version.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    private void initData() {
        //默认的目录和路径
        FileToOperate.setDefalutTabAndPath(myTab_defalut, myPath_defalut);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun){
            editor.putBoolean("isFirstRun", false);
            editor.apply();

            MyPreferences.setSharePreferencesListData("myTabs", myTab_defalut, this);
            MyPreferences.setSharePreferencesListData("myPaths", myPath_defalut, this);

            for (int i=0; i<myTab_defalut.size(); i++ ){
                tabFragmentList.add(TabFragment.newInstance(myTab_defalut.get(i), myPath_defalut.get(i)));
            }

            //第一次启动应用使用默认的Tab目录
            adapter = new FragmentAdapter(getSupportFragmentManager(), myTab_defalut, tabFragmentList);
        }else {
            //读取配置中的Tab目录和对应的Path
            ArrayList<String> myTabsFromPreferences = MyPreferences.getSharePreferencesListData("myTabs", this);
            ArrayList<String> myPathsFromPreferences = MyPreferences.getSharePreferencesListData("myPaths", this);

            for (int i=0; i<myTabsFromPreferences.size(); i++ ){
                tabFragmentList.add(TabFragment.newInstance(myTabsFromPreferences.get(i), myPathsFromPreferences.get(i)));
            }
            adapter = new FragmentAdapter(getSupportFragmentManager(), myTabsFromPreferences, tabFragmentList);
        }
        viewPager.setAdapter(adapter);
    }

    //申请权限
    private void requestPermission() {
        permissions_rejected.clear();
        //判断是否有权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_rejected.add(permission);
                Log.d("Permissions_rejected", permissions_rejected.toString());
            }
        }
        if (!permissions_rejected.isEmpty()){
            ActivityCompat.requestPermissions(this, permissions_rejected.toArray(new String[0]), PERMISSION_CODE);
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
//                    getAllFile(path_west);
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
            Toast.makeText(MainActivity.this, "Current Version:1.1 \n Developed by v_kuntliu", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_add_version) {
            Toast.makeText(MainActivity.this, "这里是添加逻辑", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.action_setting){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
