package com.kuntliu.loghelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mydialog.BottomMenuDialog;
import com.kuntliu.loghelper.mydocumentfile.MyDocumentFile;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;


public class TabFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_empty_tips;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<LogFile> fileList;
    File[] fileArr;
    DocumentFile[] documentFileArr;
    String path;
    int tabPosition;
    String filterCondition;
    boolean isSdCardRoot = false;
    private MyRecycleViewAdapter adapter;
    Context context;

    static TabFragment newInstance(int tabPosition) {
        Bundle args = new Bundle();
//        args.putString("myTab", tab);
//        args.putString("myPath", path);
        args.putInt("tabPosition", tabPosition);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        recyclerView = view.findViewById(R.id.rv_file_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        tv_empty_tips = view.findViewById(R.id.tv_empty_tips);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        prePareTab();

        if (Build.VERSION.SDK_INT >= 30 && MyDocumentFile.checkIsNeedDocument(path)){
            Log.d(TAG, "onStart: doDocumentFileMethod");
            documentFileArr = MyDocumentFile.getdestDocumentFileArr(MyDocumentFile.getDataDirDocumentFile(context, path), MyDocumentFile.getDatadirItemPath(path));
            fileList = MyDocumentFile.getDocumentFileList(documentFileArr, MyDocumentFile.checkIsNeedDocument(path), context);
        }else{
            Log.d(TAG, "onStart: doFileMethod");
            fileArr = FileToOperate.getFileArr(path);
            fileList = FileToOperate.getFileList(path, fileArr, getContext(), filterCondition, isSdCardRoot);
        }

        adapter = new MyRecycleViewAdapter(fileList, context);
        recyclerView.setAdapter(adapter);
        //根据fileList判断，显示对应的提示
        FileToOperate.tvSwitch(path, fileList, fileArr, documentFileArr, tv_empty_tips);

        adapter.setOnItemClickListener(new MyRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: position "+position);
                File selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                Log.d(TAG, "onItemClick: selectedFile "+selectedFile);
                if (selectedFile.getName().endsWith(".obb")){
                    ObbFile obbFile = new ObbFile();
                    obbFile.copyObbFile(selectedFile, fileList, position, context, adapter);
                }
                //如果点击的是APK文件则调用安装器进行安装
                FileToOperate.installAPK(selectedFile,  getContext());
            }
        });
        adapter.setOnItemLongClickListener(new MyRecycleViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                BottomMenuDialog bmd = new BottomMenuDialog();
                File selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                bmd.showBottomMenu(selectedFile, fileList, context, adapter, position, tv_empty_tips);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void prePareTab(){
        if (getArguments() != null) {
            tabPosition = getArguments().getInt("tabPosition");
            Log.d(TAG, "onStart_Tabposition: "+tabPosition);
        }
        if (getContext() != null){
            context = getContext();
            path = MyPreferences.getSharePreferencesListData("myPaths", context).get(tabPosition);
            Log.d(TAG, "prePareTab: Currentpath "+path);
        }
        //判断当前tab是否是主目录
        if (tabPosition == 0){
            isSdCardRoot = true;
        }
        filterCondition = MyPreferences.getSharePreferencesStringData("show_type", "show_all", context);
    }

    private void toRefresh(){
        fileList.clear();
        path = MyPreferences.getSharePreferencesListData("myPaths", context).get(tabPosition);
        Log.d(TAG, "toRefresh: Refreshpath "+path);
        if (Build.VERSION.SDK_INT >= 30 && MyDocumentFile.checkIsNeedDocument(path)){
            Log.d(TAG, "onStart: doDocumentFileMethod");
            documentFileArr = MyDocumentFile.getdestDocumentFileArr(MyDocumentFile.getDataDirDocumentFile(context, path), MyDocumentFile.getDatadirItemPath(path));
            fileList.addAll(MyDocumentFile.getDocumentFileList(documentFileArr, MyDocumentFile.checkIsNeedDocument(path), context));
        }else{
            Log.d(TAG, "onStart: doFileMethod");
            fileArr = FileToOperate.getFileArr(path);
            fileList.addAll(FileToOperate.getFileList(path, fileArr, context, filterCondition, isSdCardRoot));  //notifyDataSetChanged要生效的话，就必须对fileList进行操作，重新赋值是不行的
        }
        adapter.notifyDataSetChanged();
        FileToOperate.tvSwitch(path, fileList, fileArr, documentFileArr, tv_empty_tips);
    }
}
