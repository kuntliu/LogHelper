package com.kuntliu.loghelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;
import com.kuntliu.loghelper.mydialog.BottomMenuDialog;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;


public class TabFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_empty_tips;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<LogFile> fileList;

    private MyRecycleViewAdapter adapter;

    static TabFragment newInstance(String tab, String path) {
        Bundle args = new Bundle();
//        args.putString("myTab", tab);
        args.putString("myPath", path);
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
//        String tab = getArguments().getString("myTab");
        final String path = getArguments().getString("myPath");

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (spf.getString("show_type_values", "").equals("show_all")){

        }

        final File[] fileArr = FileToOperate.getFileArr(path);
        fileList = FileToOperate.getFileList(path, fileArr, getContext(), tv_empty_tips);


        adapter = new MyRecycleViewAdapter(fileList, getContext());
        recyclerView.setAdapter(adapter);
        //根据fileList判断，显示对应的提示
        FileToOperate.tvSwitch(fileList, fileArr, tv_empty_tips);

        adapter.setOnItemClickListener(new MyRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: "+position);
                File selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                if (selectedFile.getName().endsWith(".obb")){
                    ObbFile obbFile = new ObbFile();
                    obbFile.copyObbFile(selectedFile, fileList.get(position).getFile_name(), getContext());
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
                bmd.showBottomMenu(selectedFile, fileList, getContext(), adapter, position, tv_empty_tips);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fileList.clear();
                final File[] fileArr_refresh = FileToOperate.getFileArr(path);
                fileList.addAll(FileToOperate.getFileList(path, fileArr_refresh, getContext(), tv_empty_tips));  //notifyDataSetChanged要生效的话，就必须对fileList进行操作，重新赋值是不行的
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                if (fileList.size() == 0){
                    tv_empty_tips.setVisibility(View.VISIBLE);
                    tv_empty_tips.setText("当前目录为空");
                }else {
                    tv_empty_tips.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
