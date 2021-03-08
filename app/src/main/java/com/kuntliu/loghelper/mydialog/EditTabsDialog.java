package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuntliu.loghelper.PrivateTabs;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.myadapter.MyselfDirAdapter;
import com.kuntliu.loghelper.mypreferences.MyPreferences;


import java.util.ArrayList;

public class EditTabsDialog {

    public static void ShowEditTabsDialog(final Context context, final int position, final MyselfDirAdapter adapter, final ArrayList<PrivateTabs> list){
        View view  = LayoutInflater.from(context).inflate(R.layout.dialog_edit_dirs, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        final EditText edit_tabs = view.findViewById(R.id.et_tabs);
        final EditText edit_paths = view.findViewById(R.id.et_paths);
//        ArrayList<String> myTabs = MyPreferences.getSharePreferencesListData("myTabs",context);
//        ArrayList<String> myPaths = MyPreferences.getSharePreferencesListData("myPaths",context);
//
//        tv_tabs_edit.setText(myTabs.get(position));
//        tv_paths_edit.setText(myPaths.get(position));
        edit_tabs.setText(list.get(position).getTabs());
        edit_paths.setText(list.get(position).getPaths());




        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //存储edit框内的data到Preference
                MyPreferences.updateSharePreferencesListData("myTabs", edit_tabs.getText().toString(), position, context);
                MyPreferences.updateSharePreferencesListData("myPaths", edit_paths.getText().toString(), position, context);

                //更新数据源
                PrivateTabs privateTabs = new PrivateTabs(edit_tabs.getText().toString(), edit_paths.getText().toString());
                list.set(position, privateTabs);
                adapter.notifyDataSetChanged();

                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });


        dialog.show();

    }
}
