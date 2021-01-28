package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kuntliu.loghelper.R;

class MyConfirmCopyDialog {

    private static AlertDialog dialog;

    static void showConfirmCopyDialog(Context context, String filename, String filesize, String filepath, final AlertDialogBtnClickListener
            alertDialogBtnClickListener){
        View view = LayoutInflater.from(context).inflate(R.layout.mydialog_confirm_copy, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();

        TextView tv_filename = view.findViewById(R.id.comfirm_copyFileName);
        TextView tv_filesize = view.findViewById(R.id.comfirm_copyFileSize);
        TextView tv_filepath = view.findViewById(R.id.comfirm_copyFilePath);
        tv_filename.setText(filename);
        tv_filesize.setText(filesize);
        tv_filepath.setText(filepath);

        Button btn_cancel = view.findViewById(R.id.btn_cancle);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBtnClickListener.clickCancel();
                dialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                alertDialogBtnClickListener.clickConfirm();

            }
        });
        //给dialog的弹框添加动画
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.Dialog_Anim);
        }
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        }
        dialog.setTitle("目标文件");
        dialog.show();
    }

    static void dismissConfirmCopyDialog(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }


    interface AlertDialogBtnClickListener {
        void clickCancel();
        void clickConfirm();
    }
}
