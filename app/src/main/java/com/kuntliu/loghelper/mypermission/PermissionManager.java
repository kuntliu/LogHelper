package com.kuntliu.loghelper.mypermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.kuntliu.loghelper.BuildConfig;
import com.kuntliu.loghelper.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static AlertDialog alertDialog;

    static String[] permissions = new String[]
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};    //需要申请的权限
    static List<String> permissions_rejected = new ArrayList<>();//保存未授予权限
    static int PERMISSION_CODE = 1000;
    static int REQUEST_CODE_FOR_DATA_DIR = 1001;
    static int REQUEST_CODE_FOR_OBB_DIR = 1002;


    //申请常规的读写权限，Android11的data权限除外
    public static boolean getNormalPermission(Activity context) {
        permissions_rejected.clear();
        //判断是否有权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_rejected.add(permission);
//                Log.d("Permissions_rejected", permissions_rejected.toString());
            }
        }
        if (!permissions_rejected.isEmpty()){
            ActivityCompat.requestPermissions(context, permissions_rejected.toArray(new String[0]), PERMISSION_CODE);
        }else {
            return true;
        }
        return false;
    }


    //
    public static void getDataPermission(Activity context){
        if (Build.VERSION.SDK_INT >= 30){
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
            context.startActivity(intent);
            Toast.makeText(context, "点击下方的按钮进行授权data目录的访问", Toast.LENGTH_LONG).show();
            toRequestDataPermission(context, REQUEST_CODE_FOR_DATA_DIR);
        }
    }

    public static void getObbPermission(Activity context){
        if (Build.VERSION.SDK_INT >= 30){
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
            context.startActivity(intent);
            Toast.makeText(context, "点击下方的按钮进行授权obb目录的访问", Toast.LENGTH_LONG).show();
            toRequestObbPermission(context, REQUEST_CODE_FOR_OBB_DIR);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void toRequestDataPermission(Activity context, int REQUEST_CODE_FOR_DATA_DIR) {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        Log.d("startForRoot", "startForRoot:uri "+uri);
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (documentFile != null){
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
            context.startActivityForResult(intent, REQUEST_CODE_FOR_DATA_DIR);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void toRequestObbPermission(Activity context, int REQUEST_CODE_FOR_OBB_DIR) {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb");
        Log.d("startForRoot", "startForRoot:uri "+uri);
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (documentFile != null){
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
            context.startActivityForResult(intent, REQUEST_CODE_FOR_OBB_DIR);
        }
    }

    public static void showDialogAndGotoSetting(final Context context){
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示")
                    .setMessage("缺少读写文件的权限，需要获取权限后才能正常使用哦")
                    .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            gotoSetting(context);
                            System.exit(0);
                            //重启应用
//                            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("REBOOT","reboot");
//                            context.startActivity(intent);
                        }
                     })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            System.exit(0);
                        }
            });
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
    private static void gotoSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void showDataPermissionTips(final Activity context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionManager.getDataPermission(context);
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                    System.exit(0);
                }
            }
        });
        //dialog的实例化必须等待builder设置完之后
        android.app.AlertDialog dialog = builder.create();
        dialog.setMessage("由于Android 11及以上的系统的分区存储机制，需要手动授权data目录的访问才能继续data目录的访问");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void showObbPermissionTips(final Activity context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionManager.getObbPermission(context);
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                    System.exit(0);
                }
            }
        });
        //dialog的实例化必须等待builder设置完之后
        android.app.AlertDialog dialog = builder.create();
        dialog.setMessage("检测到您已设置obb目录，由于Android 11及以上的系统的分区存储机制，需要手动授权obb目录的访问才能继续obb目录的访问");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void showAllFilePermissionTips(final Context context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setPositiveButton("去允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                Toast.makeText(context, "点击开关以获取访问所有文件权限", Toast.LENGTH_LONG).show();
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                    System.exit(0);
                }
            }
        });
        //dialog的实例化必须等待builder设置完之后
        android.app.AlertDialog dialog = builder.create();
        dialog.setMessage("检测到未获取访问所有文件权限，需要允许后才能访问文件的访问哦");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void showReqInstallPermissionTips(final Context context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Uri uri = Uri.parse("package:"+ context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.setMessage("由于Android 11及以上的系统的分区存储机制，obb目录受限，需要授权安装权限后才能继续对obb文件的操作");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }
}

