package com.kuntliu.loghelper.mydialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.kuntliu.loghelper.FileSizeTransform;
import com.kuntliu.loghelper.FileToOperate;
import com.kuntliu.loghelper.LogFile;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.myadapter.MyRecycleViewApater;

import java.io.File;
import java.util.List;

public class BottomMenuDialog {

    public void showBottomDialog(final File file, final List<LogFile> list, final Context context, final MyRecycleViewApater apater, final int position){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_menu, null);
        Button btn_sent = view.findViewById(R.id.btn_sent);
        Button btn_delete = view.findViewById(R.id.btn_delete);
        Button btn_detail = view.findViewById(R.id.btn_detail);

        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileToOperate.shareFile(file, context);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileToOperate.deleteFile(file, list, position, apater, context);
            }
        });

        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFileDetailInfoDialog.showFileDetailInfoDialog(context, file.getName(), FileSizeTransform.Tansform(file.length()), file.getPath());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.getWindow().setWindowAnimations(R.style.Dialog_Anim);
        dialog.show();

    }
}
