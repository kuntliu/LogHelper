package com.kuntliu.loghelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kuntliu.loghelper.myadapter.MyselfDirAdapter;
import com.kuntliu.loghelper.mydialog.EditTabsDialog;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.util.ArrayList;

public class PrivateDirActivity extends AppCompatActivity {

    ArrayList<PrivateTabs> list = new ArrayList<>();
    ArrayList<String> myTabsToShow;
    ArrayList<String> myPathsToShow;
    MyselfDirAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_dir);


        Toolbar toolbar = findViewById(R.id.toolbar_dir_act);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);//自定义的toolbar需要调用此函数才能获得ActionBar的效果

        myTabsToShow = MyPreferences.getSharePreferencesListData("myTabs", this);
        myPathsToShow = MyPreferences.getSharePreferencesListData("myPaths", this);

        for (int i=0; i<myTabsToShow.size(); i++){
            PrivateTabs privateTabs = new PrivateTabs(myTabsToShow.get(i), myPathsToShow.get(i));
            list.add(privateTabs);
        }

        RecyclerView rv_privateDir = findViewById(R.id.rv_private_dir);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_privateDir.setLayoutManager(linearLayoutManager);

        adapter = new MyselfDirAdapter(this,list);
        rv_privateDir.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyselfDirAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                EditTabsDialog.ShowEditTabsDialog(PrivateDirActivity.this, position, adapter, list);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_set_deafalut_dir, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set_defalut) {
            list.clear();
            myTabsToShow.clear();
            myPathsToShow.clear();
            FileToOperate.setDefalutTabAndPath(myTabsToShow, myPathsToShow);
            MyPreferences.setSharePreferencesListData("myTabs", myTabsToShow, this);
            MyPreferences.setSharePreferencesListData("myPaths", myPathsToShow, this);
            for (int i=0; i<myTabsToShow.size(); i++){
                PrivateTabs privateTabs = new PrivateTabs(myTabsToShow.get(i), myPathsToShow.get(i));
                list.add(privateTabs);
            }
            adapter.notifyDataSetChanged();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}