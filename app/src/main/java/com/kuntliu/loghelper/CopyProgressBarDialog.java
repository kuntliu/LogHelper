package com.kuntliu.loghelper;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

class CopyProgressBarDialog {

    private static AlertDialog dialog;

    static View showCopyProgressBar(Context context, String filename){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        TextView tv_filename = view.findViewById(R.id.CopyingFilename);
        tv_filename.setText(filename);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();
        dialog.setTitle("正在复制...");
        dialog.setCancelable(false);
        //总结：默认情况下，setCancelable和setCanceledOnTouchOutside都是可以取消掉dialog的，如果设置setCancelable为false，这个时候急救都不可以取消dialog

        Window window = dialog.getWindow();
        if (window != null){
            window.setWindowAnimations(R.style.Dialog_Anim);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.show();
        return view;
    }


    static void dismissCopyProgressBar(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
