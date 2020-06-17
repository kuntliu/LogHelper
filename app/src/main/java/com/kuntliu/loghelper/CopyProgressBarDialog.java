package com.kuntliu.loghelper;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class CopyProgressBarDialog {
    static void showCopyPrigressBar(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        TextView tv_copyRate = view.findViewById(R.id.CopyRate);
        TextView tv_copyPrecent = view.findViewById(R.id.CopyPrecent);

        AlertDialog dialog = builder.create();
        dialog.setTitle("正在复制");


    }
}
