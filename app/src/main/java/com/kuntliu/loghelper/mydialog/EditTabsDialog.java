package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.kuntliu.loghelper.R;

public class EditTabsDialog {

    public static void ShowEditTabsDialog(Context context){
        View view  = LayoutInflater.from(context).inflate(R.layout.dialog_edit_dirs, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }
}
