package com.kuntliu.loghelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ObbActivity extends AppCompatActivity {
    String path_SdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    FileAdapter madapter;
    String FileSize_str;
    ListView obblistview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obb);
        obblistview = findViewById(R.id.item_obb);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        List<LogFile> ObbFiles = new ArrayList<>();
        File file = new File(path_SdcardRoot);
        File[] obbfiles = file.listFiles();
        int logiconID = getResources().getIdentifier("icon_file","drawable","com.kuntliu.loghelper");//需要传入资源id
        if(obbfiles != null) {
            for (File f : obbfiles) {
                if (f.isFile() && f.getName().endsWith(".obb")) {
                    FileSizeTransform fileSizeTransform = new FileSizeTransform();
                    FileSize_str = fileSizeTransform.Tansform(f.length());
                    LogFile obbfile = new LogFile(logiconID, f.getName(), FileSize_str, new SimpleDateFormat("yyyy/M/d H:m").format(f.lastModified()));
                    ObbFiles.add(obbfile);
                }
            }
            madapter = new FileAdapter(ObbActivity.this, ObbFiles);
            obblistview.setAdapter(madapter);
        }else {
            Toast.makeText(this, "根目录没有obb文件", Toast.LENGTH_SHORT).show();
        }
    }
}
