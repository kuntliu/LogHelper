package com.kuntliu.loghelper.mydialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.kuntliu.loghelper.PrivateTabs;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.myadapter.PrivateTabsAdapter;
import com.kuntliu.loghelper.mypreferences.MyPreferences;

import java.util.ArrayList;

public class BottomMenuPopupWindow {

    private static PopupWindow pw;

    public static void showBottomPopWindow(final Context context, final int position, final ArrayList<PrivateTabs> list, final PrivateTabsAdapter adapter){
        View view = LayoutInflater.from(context).inflate(R.layout.private_tab_bottom_menu, null);
        Button btn_del_tab = view.findViewById(R.id.btn_delete_tab);
        btn_del_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0){
                    MyPreferences.deleteSharePreferenceListData("myTabs", position, context);
                    MyPreferences.deleteSharePreferenceListData("myPaths", position, context);
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, list.size());  //解决删除文件后list的position发生变化的问题，对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
                    pw.dismiss();
                }else {
                    Toast.makeText(context, "主目录不可删除哦", Toast.LENGTH_SHORT).show();
                    pw.dismiss();
                }
            }
        });

        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pw.setContentView(view);
        pw.setFocusable(true);
        pw.setOutsideTouchable(true);
        pw.setAnimationStyle(R.style.Dialog_Anim);
        pw.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_private_tab,null), Gravity.BOTTOM, 0 ,0 );
    }

}
