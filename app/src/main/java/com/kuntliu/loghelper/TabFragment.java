package com.kuntliu.loghelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mydialog.BottomMenuDialog;
import com.kuntliu.loghelper.mydocumentfile.MyDocumentFile;
import com.kuntliu.loghelper.mypermission.PermissionManager;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;


public class TabFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_empty_tips;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<MyFile> fileList;
    File[] fileArr;
    DocumentFile[] documentFileArr;
    DocumentFile dataDirDocumentFile;
    DocumentFile selectedDocFile;
    File selectedFile;

    String path;
    int tabPosition;
    String filterCondition;
    boolean isNeedUseDoc;
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
        loadingTabData();
    }


    //页签数据加载准备工作
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
        isNeedUseDoc = MyDocumentFile.checkIsNeedDocument(path);
        dataDirDocumentFile = MyDocumentFile.getDataDirDocumentFile(context);

        SharedPreferences sp = context.getSharedPreferences("DirPermission", Context.MODE_PRIVATE);
        String obb_uri_str = sp.getString("obbUriTree", "");
        String path_contain_obb = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android/obb/";
        if (isNeedUseDoc && TextUtils.isEmpty(obb_uri_str) && path.startsWith(path_contain_obb)){
            PermissionManager.showObbPermissionTips((Activity)context);
        }
    }

    //开始加载对应页签内的数据
    private void loadingTabData(){
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 30 && isNeedUseDoc){
                    Log.d(TAG, "onStart: doDocumentFileMethod");
                    if (dataDirDocumentFile != null){
                        documentFileArr = MyDocumentFile.getdestDocumentFileArr(dataDirDocumentFile, path, context);
                    }
                    fileList = MyDocumentFile.getDocumentFileList(documentFileArr, isNeedUseDoc, context);
                }else{
                    Log.d(TAG, "onStart: doFileMethod");
                    fileArr = FileToOperate.getFileArr(path);
                    fileList = FileToOperate.getFileList(path, fileArr, getContext(), filterCondition, isSdCardRoot);
                }
                //完成数据加载去通知ui线程进行更新界面
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyRecycleViewAdapter(fileList, context);
                        recyclerView.setAdapter(adapter);
                        //根据fileList判断，显示对应的提示
                        FileToOperate.tvSwitch(path, fileList, fileArr, documentFileArr, isNeedUseDoc, tv_empty_tips);

                        adapter.setOnItemClickListener(new MyRecycleViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d(TAG, "onItemClick: position "+position);
                                if (Build.VERSION.SDK_INT >=30 && isNeedUseDoc){
                                    selectedDocFile = FileToOperate.searchSelectedDocFile(documentFileArr, fileList.get(position).getFile_name(), context);
                                    if (selectedDocFile != null && selectedDocFile.getName().endsWith(".obb")){
                                        Toast.makeText(context, "由于Android 11及以上的系统限制，暂不支持在data和obb目录下obb文件的操作", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                                    if (selectedFile.getName().endsWith(".obb")){
                                        if (Build.VERSION.SDK_INT >= 30 && !context.getPackageManager().canRequestPackageInstalls()){
                                            PermissionManager.showReqInstallPermissionTips(context);
                                        }else {
                                            ObbFile obbFile = new ObbFile();
                                            Log.d(TAG, "onItemClick: selectedDocFile"+selectedDocFile);
                                            obbFile.copyObbFile(selectedFile, fileList, position, context, adapter);
                                        }
                                    }
                                }
                                Log.d(TAG, "OnStar:selectedFile "+selectedFile);
                                Log.d(TAG, "OnStar:selectedDocFile "+selectedDocFile);
                                //如果点击的是APK文件则调用安装器进行安装
                                FileToOperate.installAPK(selectedFile, selectedDocFile, isNeedUseDoc, getContext());
                            }
                        });
                        adapter.setOnItemLongClickListener(new MyRecycleViewAdapter.OnItemLongClickListener() {
                            @Override
                            public void onItemLongClick(View view, int position) {
                                BottomMenuDialog bmd = new BottomMenuDialog();
                                if (Build.VERSION.SDK_INT >=30 && isNeedUseDoc){
                                    selectedDocFile = FileToOperate.searchSelectedDocFile(documentFileArr, fileList.get(position).getFile_name(), context);
                                }else {
                                    selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                                }
                                if (selectedDocFile != null || selectedFile != null){
                                    bmd.showBottomMenu(selectedFile, selectedDocFile, isNeedUseDoc, fileList, context, adapter, position, tv_empty_tips);
                                }
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
                });

            }
        }).start();
    }




    //每个Tab的刷新功能
    private void toRefresh(){
        if (isNeedUseDoc && documentFileArr == null && !Environment.isExternalStorageManager()){
            PermissionManager.showDataPermissionTips((Activity) context);
        }
        fileList.clear();
        path = MyPreferences.getSharePreferencesListData("myPaths", context).get(tabPosition);
        Log.d(TAG, "toRefresh: RefreshPath "+path);
        if (Build.VERSION.SDK_INT >= 30 && isNeedUseDoc){
            Log.d(TAG, "onStart: doDocumentFileMethod");
            documentFileArr = MyDocumentFile.getdestDocumentFileArr(dataDirDocumentFile, path, context);
            fileList.addAll(MyDocumentFile.getDocumentFileList(documentFileArr, isNeedUseDoc, context));
        }else{
            Log.d(TAG, "onStart: doFileMethod");
            fileArr = FileToOperate.getFileArr(path);
            fileList.addAll(FileToOperate.getFileList(path, fileArr, context, filterCondition, isSdCardRoot));  //notifyDataSetChanged要生效的话，就必须对fileList进行操作，重新赋值是不行的
        }
        adapter.notifyDataSetChanged();
        FileToOperate.tvSwitch(path, fileList, fileArr, documentFileArr, isNeedUseDoc, tv_empty_tips);
    }
}
