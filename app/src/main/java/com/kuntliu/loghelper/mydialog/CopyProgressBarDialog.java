package com.kuntliu.loghelper.mydialog;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kuntliu.loghelper.R;

import org.w3c.dom.Text;

import java.io.File;

public class CopyProgressBarDialog {

    private static AlertDialog dialog;

    public static View showCopyProgressBar(Context context, String filename){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        TextView tv_filename = view.findViewById(R.id.CopyingFilename);
        tv_filename.setText(filename);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();
        dialog.setTitle("正在复制...");
        dialog.setCancelable(false);
        //dialog.setCancelable(false);
        //dialog弹出后会点击屏幕或物理返回键，dialog不消失
        //dialog.setCanceledOnTouchOutside(false);
        //dialog弹出后，点击屏幕dialog不消失；点击物理返回键dialog消失

        Window window = dialog.getWindow();
        if (window != null){
            window.setWindowAnimations(R.style.Dialog_Anim);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.show();
        return view;
    }

    public static void dismissCopyProgressBar(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
