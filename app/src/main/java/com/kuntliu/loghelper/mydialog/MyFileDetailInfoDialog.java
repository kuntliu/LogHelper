package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kuntliu.loghelper.R;

class MyFileDetailInfoDialog {
    static void showFileDetailInfoDialog(Context context, String FileName, String FileSize, String FilePath){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_detail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        TextView tv_filename = view.findViewById(R.id.detail_filename);
        TextView tv_filesize = view.findViewById(R.id.detail_filesize);
        TextView tv_filepath = view.findViewById(R.id.detail_filepath);
        tv_filename.setText(FileName);
        tv_filesize.setText(FileSize);
        tv_filepath.setText(FilePath);

        final AlertDialog dialog = builder.create();
        Button btn_confirm = view.findViewById(R.id.btn_single_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Window window = dialog.getWindow();
        if (window != null){
            window.setWindowAnimations(R.style.Dialog_Anim);
        }
        dialog.setTitle("详细信息");
        dialog.show();

    }
}
