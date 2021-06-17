package com.kuntliu.loghelper.myadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuntliu.loghelper.MyFile;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.Transform;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyViewHolder> {
    private final List<MyFile> list;
    private final Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener monItemClickListener) {
        this.mOnItemClickListener = monItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public MyRecycleViewAdapter(List<MyFile> list, Context context) {
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_fileName.setText(list.get(position).getFile_name());
        holder.tv_fileSize.setText(Transform.transformSize(list.get(position).getFile_size()));
        holder.tv_fileCreateTime.setText(Transform.transFormTime(list.get(position).getFile_time_create()));
        holder.tv_fileIcon.setImageDrawable(list.get(position).getFile_icon());
        holder.tv_apkVersion.setText(list.get(position).getFile_apk_version());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "onLongClick: "+ mOnItemLongClickListener);
                mOnItemLongClickListener.onItemLongClick(view, position);
                return true;    //需要返回true，否则在长按松开后，会触发点击事件
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
        TextView tv_apkVersion;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fileName = itemView.findViewById(R.id.file_name);
            tv_fileSize = itemView.findViewById(R.id.file_size);
            tv_fileCreateTime =itemView.findViewById(R.id.file_time_create);
            tv_fileIcon = itemView.findViewById(R.id.file_icon);
            tv_apkVersion = itemView.findViewById(R.id.apkFile_version);
        }
    }


}
