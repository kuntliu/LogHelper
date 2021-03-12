package com.kuntliu.loghelper.myadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuntliu.loghelper.PrivateTabs;
import com.kuntliu.loghelper.R;

import java.util.List;

public class PrivateTabsAdapter extends RecyclerView.Adapter<PrivateTabsAdapter.MyViewHolder> {

    private final Context context;
    private final List<PrivateTabs> myData;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public PrivateTabsAdapter(Context context, List<PrivateTabs> myData) {
        this.context = context;
        this.myData = myData;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener){
        this.onItemLongClickListener = mOnItemLongClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(View v, int position);
    }
    public interface OnItemLongClickListener{
        void OnItemLongClick(View v, int position);
    }

    @NonNull
    @Override
    public PrivateTabsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_private_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateTabsAdapter.MyViewHolder holder, final int position) {
        holder.tv_tabs.setText(myData.get(position).getTabs());
        holder.tv_paths.setText(myData.get(position).getPaths());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.OnItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemLongClickListener.OnItemLongClick(view, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_tabs;
        TextView tv_paths;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tabs = itemView.findViewById(R.id.tv_private_tab);
            tv_paths = itemView.findViewById(R.id.tv_private_path);
        }
    }
}
