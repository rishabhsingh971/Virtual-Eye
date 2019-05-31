package com.example.controller.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class CAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    final TaskListener taskListener;
    WeakReference<Context> contextRef;
    ProgressDialog pd;


    public interface TaskListener {
        void onFinished(Object result);
    }
    CAsyncTask(Context context, TaskListener taskListener) {
        this.contextRef = new WeakReference<>(context);
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(contextRef.get(), "Loading", "Please wait...");

    }

    @Override
    protected void onPostExecute(Result result) {
        if(pd!=null) {
            pd.dismiss();
        }
        if(taskListener!=null) {
            taskListener.onFinished(result);
        }
    }
}
