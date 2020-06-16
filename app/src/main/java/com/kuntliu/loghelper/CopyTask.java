package com.kuntliu.loghelper;

import android.os.AsyncTask;

public class CopyTask extends AsyncTask<Void, Integer, Boolean> {

    @Override                                     //学习记录
    protected void onPreExecute() {             //线程的准备工作，一般用于弹出UI对话框
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {       //线程的执行
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {        //线程的执行进度,需要在doInBackground中使用publishProgress(Progress... values)来执行此方法
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {          //线程的执行结果
        super.onPostExecute(result);
    }
}
