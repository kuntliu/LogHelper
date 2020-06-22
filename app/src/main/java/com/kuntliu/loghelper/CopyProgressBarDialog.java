package com.kuntliu.loghelper;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class CopyProgressBarDialog {

    private static AlertDialog dialog;

    static View showCopyProgressBar(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();
        if (dialog != null) {
            dialog.setTitle("正在复制...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        return view;
    }
    static void dismissCopyProGressBar(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
