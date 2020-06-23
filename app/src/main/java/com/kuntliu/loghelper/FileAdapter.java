package com.kuntliu.loghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class FileAdapter extends BaseAdapter {
    private List<LogFile>  list;
    private LayoutInflater mInflater;

    FileAdapter(Context context, List<LogFile> list){
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_list, parent, false);
        }else {
            view = convertView;
        }
        ImageView logImage = view.findViewById(R.id.file_image);
        TextView logName = view.findViewById(R.id.file_name);
        TextView logSize = view.findViewById(R.id.file_size);
        TextView logCreateTime = view.findViewById(R.id.file_time_create);
        LogFile log = list.get(position);
        logImage.setImageResource(log.getFile_image());
        logName.setText(log.getFile_name());
        logSize.setText(log.getFile_size());
        logCreateTime.setText(log.getFile_time_create());
        return view;
    }
}
