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

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kuntliu.loghelper.myadapter.FragmentAdapter;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //获得权限了之后去初始化数据
            if (getPermission()){
                initData();
            }
        }
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


//          已弃用Spinner，改用TabLayout
//          整体的实现逻辑：首次启动将写入tab和对应path配置，保存在本地，非首次启动就去读取已保存的配置，根据tab的position去获取对应的path显示该路径下的文件
//        Spinner spinner = findViewById(R.id.spinner_item);
//        Resources resources = getResources();
//        String[] arr_path = resources.getStringArray(R.array.arr_path);
//        //踩坑描述：android.R.layout.simple_list_item_1是系统提供的下拉列表样式，也可以自定义
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr_path);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

    }

    //通过重写onRestart重新加载tab名称
    //默认的Activity生命周期模式下，onRestart()会在点击返回或者从后台切回应用时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        tabFragmentList.clear();
        ArrayList<String> myTabsFromPreferences = MyPreferences.getSharePreferencesListData("myTabs", MainActivity.this);
        Log.d("MainActivity", "onRestart: "+myTabsFromPreferences);
        for (int i=0; i<myTabsFromPreferences.size(); i++ ){
            tabFragmentList.add(TabFragment.newInstance(i));
        }
        adapter = new FragmentAdapter(getSupportFragmentManager(), myTabsFromPreferences, tabFragmentList);
        viewPager.setAdapter(adapter);
    }



    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tab_version = findViewById(R.id.tab_version);
        viewPager = findViewById(R.id.viewPage_file);
        viewPager.setOffscreenPageLimit(MyPreferences.SharePregerenceSize(MainActivity.this));    //默认情况下，viewPager会加载相邻的1页，这里设置为5

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
                tabFragmentList.add(TabFragment.newInstance( i));
            }

            //第一次启动应用使用默认的Tab目录
            adapter = new FragmentAdapter(getSupportFragmentManager(), myTab_defalut, tabFragmentList);
        }else {
            //非第一次启动应用就读取配置中的Tab目录和对应的Path
            ArrayList<String> myTabsFromPreferences = MyPreferences.getSharePreferencesListData("myTabs", this);
//            ArrayList<String> myPathsFromPreferences = MyPreferences.getSharePreferencesListData("myPaths", this);

            for (int i=0; i<myTabsFromPreferences.size(); i++ ){
                tabFragmentList.add(TabFragment.newInstance(i));
            }
            adapter = new FragmentAdapter(getSupportFragmentManager(), myTabsFromPreferences, tabFragmentList);
        }
        viewPager.setAdapter(adapter);
    }

    //申请权限
    private boolean getPermission() {
        permissions_rejected.clear();
        boolean isGetAllPermissions = false;
        //判断是否有权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_rejected.add(permission);
//                Log.d("Permissions_rejected", permissions_rejected.toString());
            }
        }
        if (!permissions_rejected.isEmpty()){
            ActivityCompat.requestPermissions(this, permissions_rejected.toArray(new String[0]), PERMISSION_CODE);
        }else {
            isGetAllPermissions = true;
        }
        return isGetAllPermissions;
    }

    //权限窗口，用户操作的结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasRejectPermission = false;
        if (requestCode == PERMISSION_CODE){
            for (int grantResult : grantResults) {
                //用户选择“允许”
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    hasRejectPermission = true;
                    break;
                }
            }
            if(hasRejectPermission){
                showDialogAndGotoSetting();
            }else {
                //拿到全部全限后就开始初始化数据
                initData();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTING_CODE){       //从设置返回后再次进行权限判断
            getPermission();
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
