package com.kuntliu.loghelper;

import android.annotation.SuppressLint;
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

    public FileAdapter(Context context, List<LogFile> list){
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
        @SuppressLint({"ViewHolder", "InflateParams"}) View layout = mInflater.inflate(R.layout.item_list, null);

        ImageView logImage = layout.findViewById(R.id.file_image);
        TextView logName = layout.findViewById(R.id.file_name);
        TextView logSize = layout.findViewById(R.id.file_size);

        LogFile log = list.get(position);
        assert log != null;
        logImage.setImageResource(log.getFile_image());
        logName.setText(log.getFile_name());
        logSize.setText(log.getFile_size());
        return layout;
    }
}
