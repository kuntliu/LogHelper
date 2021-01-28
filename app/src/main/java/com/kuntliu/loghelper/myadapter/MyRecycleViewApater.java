package com.kuntliu.loghelper.myadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuntliu.loghelper.LogFile;
import com.kuntliu.loghelper.R;

import java.util.List;

public class MyRecycleViewApater extends RecyclerView.Adapter<MyRecycleViewApater.MyViewHolder> {
    private List<LogFile> list;
    private Context context;
    private OnItemClickListener OnItemClickListener;


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.OnItemClickListener = onItemClickListener;
    }

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tv_fileName.setText(list.get(position).getFile_name());
        holder.tv_fileSize.setText(list.get(position).getFile_size());
        holder.tv_fileCreateTime.setText(list.get(position).getFile_time_create());
        holder.tv_fileIcon.setImageResource(list.get(position).getFile_icon());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_fileName;
        TextView tv_fileSize;
        TextView tv_fileCreateTime;
        ImageView tv_fileIcon;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fileName = itemView.findViewById(R.id.file_name);
            tv_fileSize = itemView.findViewById(R.id.file_size);
            tv_fileCreateTime =itemView.findViewById(R.id.file_time_create);
            tv_fileIcon = itemView.findViewById(R.id.file_icon);
        }
    }


}
