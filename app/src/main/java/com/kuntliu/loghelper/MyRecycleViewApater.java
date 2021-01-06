package com.kuntliu.loghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecycleViewApater extends RecyclerView.Adapter<MyRecycleViewApater.MyViewHolder> {
    private List<LogFile> list;
    private Context context;



    public MyRecycleViewApater(List<LogFile> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_fileName.setText(list.get(position).getFile_name());
        holder.tv_fileSize.setText(list.get(position).getFile_size());
        holder.tv_fileCreateTime.setText(list.get(position).getFile_time_create());
        holder.iv_fileImage.setImageResource(list.get(position).getFile_image());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_fileName;
        TextView tv_fileSize;
        TextView tv_fileCreateTime;
        ImageView iv_fileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fileName = (TextView)itemView.findViewById(R.id.file_name);
            tv_fileSize = (TextView)itemView.findViewById(R.id.file_size);
            tv_fileCreateTime = (TextView)itemView.findViewById(R.id.file_time_create);
            iv_fileImage = (ImageView)itemView.findViewById(R.id.file_image);
        }
    }
}
