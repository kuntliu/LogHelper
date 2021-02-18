package com.kuntliu.loghelper.myadapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuntliu.loghelper.PrivateTabs;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.mydialog.EditTabsDialog;

import java.util.List;

public class MyselfDirAdapter extends RecyclerView.Adapter<MyselfDirAdapter.MyViewHolder> {

    private final Context context;
    private final List<PrivateTabs> myData;
    private OnItemClickListener mOnItemClickListener;

    public MyselfDirAdapter(Context context, List<PrivateTabs> myData) {
        this.context = context;
        this.myData = myData;
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(View v, int position);
    }

    @NonNull
    @Override
    public MyselfDirAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_private_dir_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyselfDirAdapter.MyViewHolder holder, final int position) {
        holder.tv_tabs.setText(myData.get(position).getTabs());
        holder.tv_paths.setText(myData.get(position).getPaths());
        holder.btn_deleteDirs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.OnItemClick(v, position);
                return false;
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
        Button btn_deleteDirs;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tabs = itemView.findViewById(R.id.tv_private_dir_tab);
            tv_paths = itemView.findViewById(R.id.tv_private_dir_path);
            btn_deleteDirs = itemView.findViewById(R.id.btn_delete_dirs);
        }
    }



}
