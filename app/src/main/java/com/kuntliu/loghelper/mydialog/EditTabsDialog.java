package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.mypreferences.MyPreferences;


import java.util.ArrayList;

public class EditTabsDialog {

    public static void ShowEditTabsDialog(final Context context, final int position){
        View view  = LayoutInflater.from(context).inflate(R.layout.dialog_edit_dirs, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        final EditText tv_tabs_edit = view.findViewById(R.id.et_tabs);
        final EditText tv_paths_edit = view.findViewById(R.id.et_paths);
        ArrayList<String> myTabs = MyPreferences.getSharePreferencesListData("myTabs",context);
        ArrayList<String> myPaths = MyPreferences.getSharePreferencesListData("myPaths",context);

        tv_tabs_edit.setText(myTabs.get(position));
        tv_paths_edit.setText(myPaths.get(position));


        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreferences.updateSharePreferencesListData("myTabs", tv_tabs_edit.getText().toString(), position, context);
                MyPreferences.updateSharePreferencesListData("myPaths", tv_paths_edit.getText().toString(), position, context);
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
