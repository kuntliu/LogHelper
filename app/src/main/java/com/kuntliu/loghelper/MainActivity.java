package com.kuntliu.loghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kuntliu.loghelper.myadapter.FragmentAdapter;
import com.kuntliu.loghelper.mypermission.PermissionManager;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> myTab_defalut = new ArrayList<>();
    private final ArrayList<String> myPath_defalut = new ArrayList<>();
    private final List<TabFragment> tabFragmentList = new ArrayList<>();

    TabLayout tab_version;
    ViewPager viewPager;
    FragmentAdapter adapter;

    static int PERMISSION_CODE = 1000;
    int REQUEST_CODE_FOR_DIR = 1002;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean isGetNormalPermission = PermissionManager.getNormalPermission(MainActivity.this);
            //获得权限了之后去初始化数据
            if (isGetNormalPermission){
                if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()){
                    Log.d(TAG, "run: isGetDataPermission "+ Environment.isExternalStorageManager());
                    //Android11如果还没授予data权限，先显示授权data的提示框
                    PermissionManager.showDataPermissionTips(MainActivity.this);
                }else {
                    //主线程初始化view，新开子线程去初始化数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    }).start();
                }
            }
        }
        tab_version.setupWithViewPager(viewPager,false);



//        FileToOperate.getFiles(FileToOperate.getDoucmentFile(MainActivity.this, FileToOperate.path_west));

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
    //默认的Activity生命周期模式下，onRestart()会在点击返回键或者从后台切回应用时被调用
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
        viewPager.setOffscreenPageLimit(MyPreferences.SharePreferenceSize(MainActivity.this));    //默认情况下，viewPager会加载相邻的1页，这里设置为自动获取tab的数量

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

    //权限窗口，用户操作的结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasRejectPermission = false;
        if (requestCode == PERMISSION_CODE){
            for (int grantResult : grantResults) {
                //用户选择“拒绝”
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    hasRejectPermission = true;
                    break;
                }
            }
            if(hasRejectPermission){
                PermissionManager.showDialogAndGotoSetting(MainActivity.this);
            }else {
                //拿到全部全限后就开始初始化数据
                if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()){
                    PermissionManager.showDataPermissionTips(MainActivity.this);
                }else {
                    initData();
                }
            }
        }
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
            Toast.makeText(MainActivity.this, "Current Version:1.2 \n Developed by v_kuntliu", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.action_setting){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //权限申请的结果回调
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_FOR_DIR && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
            SharedPreferences sharedPreferences = getSharedPreferences("DirPermission", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("dataUriTree", uri.toString());
            editor.apply();
//            if (!TextUtils.isEmpty(sharedPreferences.getString("dataUriTree", ""))){
//                initData();
//            }
        }
    }
}
