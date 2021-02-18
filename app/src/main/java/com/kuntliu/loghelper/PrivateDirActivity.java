package com.kuntliu.loghelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kuntliu.loghelper.myadapter.MyselfDirAdapter;
import com.kuntliu.loghelper.mydialog.EditTabsDialog;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PrivateDirActivity extends AppCompatActivity {

    ArrayList<PrivateTabs> list = new ArrayList<>();

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

        ArrayList<String> myTabsToShow = MyPreferences.getSharePreferencesArrData("myTabs", this);
        ArrayList<String> myPathsToShow = MyPreferences.getSharePreferencesArrData("myPaths", this);

        for (int i=0; i<myTabsToShow.size(); i++){
            PrivateTabs privateTabs = new PrivateTabs(myTabsToShow.get(i), myPathsToShow.get(i));
            list.add(privateTabs);
        }

        RecyclerView rv_privateDir = findViewById(R.id.rv_private_dir);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_privateDir.setLayoutManager(linearLayoutManager);

        MyselfDirAdapter adapter = new MyselfDirAdapter(this,list);
        rv_privateDir.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyselfDirAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                EditTabsDialog.ShowEditTabsDialog(PrivateDirActivity.this);
            }
        });



    }
}