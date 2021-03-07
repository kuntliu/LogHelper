package com.kuntliu.loghelper.mydialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.kuntliu.loghelper.FileSizeTransform;
import com.kuntliu.loghelper.FileToOperate;
import com.kuntliu.loghelper.LogFile;
import com.kuntliu.loghelper.R;
import com.kuntliu.loghelper.myadapter.MyRecycleViewAdapter;

import java.io.File;
import java.util.List;

public class BottomMenuDialog {
    PopupWindow pw;

    public void showBottomMenu(final File file, final List<LogFile> list, final Context context, final MyRecycleViewAdapter apater, final int position){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_menu, null);
        Button btn_sent = view.findViewById(R.id.btn_sent);
        Button btn_delete = view.findViewById(R.id.btn_delete);
        Button btn_detail = view.findViewById(R.id.btn_detail);

        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileToOperate.shareFile(file, context);
                pwDismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileToOperate.deleteFile(file, list, position, apater, context);
                pw.dismiss();
            }
        });

        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFileDetailInfoDialog.showFileDetailInfoDialog(context, file.getName(), FileSizeTransform.Tansform(file.length()), file.getPath());
                pw.dismiss();
            }
        });


        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pw.setContentView(view);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);   //设置焦点，可以防止popupwindow存在时，点击窗口以外的地方不会触发点击位置的点击事件

        pw.setAnimationStyle(R.style.Dialog_Anim);
        pw.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_main,null), Gravity.BOTTOM, 0 ,0 );
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
//        dialog.setView(view);
//        dialog.getWindow().setWindowAnimations(R.style.Dialog_Anim);
//        dialog.show();

    }

    private void pwDismiss() {
        if (pw != null && pw.isShowing()){
            pw.dismiss();
        }
    }
}
