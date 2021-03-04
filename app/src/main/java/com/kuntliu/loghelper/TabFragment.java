package com.kuntliu.loghelper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuntliu.loghelper.myadapter.MyRecycleViewApater;
import com.kuntliu.loghelper.mydialog.BottomMenuDialog;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;


public class TabFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_empty_tips;

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        String tab = getArguments().getString("myTab");
        String path = getArguments().getString("myPath");


        final File[] fileArr = FileToOperate.getFileArr(path);
        final List<LogFile> fileList = FileToOperate.getFileList(path, fileArr, getContext(), tv_empty_tips);




        final MyRecycleViewApater adapter = new MyRecycleViewApater(fileList, getContext());
        recyclerView.setAdapter(adapter);


        //使用系统默认的删除添加动画
//        recyclerView.setItemAnimator(new DefaultItemAnimator());



        adapter.setOnItemClickListener(new MyRecycleViewApater.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: "+position);
                //如果点击的是APK文件则调用安装器进行安装
                File selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                FileToOperate.installAPK(selectedFile,  getContext());
            }
        });
        adapter.setOnItemLongClickListener(new MyRecycleViewApater.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                BottomMenuDialog bmd = new BottomMenuDialog();
                File selectedFile = FileToOperate.searchSelectedFile(fileArr, fileList.get(position).getFile_name());
                bmd.showBottomMenu(selectedFile, fileList, getContext(), adapter, position);
            }
        });
    }
}
