package com.kuntliu.loghelper;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class CopyProgressBarDialog {
    static View showCopyPrigressBar(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);


        AlertDialog dialog = builder.create();
        dialog.setTitle("正在复制");
        dialog.show();
        return view;
    }
}
