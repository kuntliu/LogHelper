package com.kuntliu.loghelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.kuntliu.loghelper.myadapter.PrivateTabsAdapter;
import com.kuntliu.loghelper.mydialog.EditTabsDialog;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.util.ArrayList;

public class PrivateTabActivity extends AppCompatActivity {

    ArrayList<PrivateTabs> tabList = new ArrayList<>();
    ArrayList<String> myTabsToShow;
    ArrayList<String> myPathsToShow;
    PrivateTabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_tab);

        Toolbar toolbar = findViewById(R.id.toolbar_private_tab);
        setSupportActionBar(toolbar);//自定义的toolbar需要调用此函数才能获得ActionBar的效果
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.d("PrivateTabAct", "NavigationOnClick: ");
            }
        });

        myTabsToShow = MyPreferences.getSharePreferencesListData("myTabs", this);
        myPathsToShow = MyPreferences.getSharePreferencesListData("myPaths", this);

        for (int i=0; i<myTabsToShow.size(); i++){
            PrivateTabs privateTabs = new PrivateTabs(myTabsToShow.get(i), myPathsToShow.get(i));
            tabList.add(privateTabs);
        }

        RecyclerView rv_privateTab = findViewById(R.id.rv_private_tab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_privateTab.setLayoutManager(linearLayoutManager);
//        rv_privateTab.addItemDecoration(new DividerItemDecoration(PrivateTabActivity.this, DividerItemDecoration.VERTICAL));
        rv_privateTab.addItemDecoration(new com.kuntliu.loghelper.recycleviewitemdivline.DividerItemDecoration());

        adapter = new PrivateTabsAdapter(this, tabList);
        rv_privateTab.setAdapter(adapter);

        adapter.setOnItemClickListener(new PrivateTabsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                EditTabsDialog.ShowEditTabsDialog(PrivateTabActivity.this, position, adapter, tabList);
            }
        });
        adapter.setOnItemLongClickListener(new PrivateTabsAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View v, final int position) {
                PopupWindow pw = new PopupWindow();

                String[] tabMenu = getResources().getStringArray(R.array.delete_private_tab);
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PrivateTabActivity.this, R.layout.support_simple_spinner_dropdown_item, tabMenu);
//                spinner.setAdapter(arrayAdapter);
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        if (i == 0) {
//                            MyPreferences.deleteSharePreferenceListData("myTabs", position, PrivateTabActivity.this);
//                            MyPreferences.deleteSharePreferenceListData("myPaths", position, PrivateTabActivity.this);
//                            tabList.remove(position);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
            }
        });
    }

    //创建右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_set_deafalut_tab, menu);
        return true;
    }

    //对应的菜单被点击，执行对应的逻辑
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Tab名称和对应的path恢复默认
        if (id == R.id.action_set_defalut) {
            showConfirmDeleteTabDialog(PrivateTabActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showConfirmDeleteTabDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tabList.clear();
                myTabsToShow.clear();
                myPathsToShow.clear();
                FileToOperate.setDefalutTabAndPath(myTabsToShow, myPathsToShow);
                MyPreferences.setSharePreferencesListData("myTabs", myTabsToShow, context);
                MyPreferences.setSharePreferencesListData("myPaths", myPathsToShow, context);
                for (int i=0; i<myTabsToShow.size(); i++){
                    PrivateTabs privateTabs = new PrivateTabs(myTabsToShow.get(i), myPathsToShow.get(i));
                    tabList.add(privateTabs);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setMessage("是否恢复默认Tab目录?");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

}