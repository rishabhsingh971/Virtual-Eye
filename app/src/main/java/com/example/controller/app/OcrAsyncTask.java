package com.example.controller.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;

public class OcrAsyncTask extends CAsyncTask<Uri, Void, String[]> {
    private static final String TAG = OcrAsyncTask.class.getSimpleName();
    private TextRecognizer detector;

    OcrAsyncTask(Context context, TaskListener taskListener) {
        super(context, taskListener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        detector = new TextRecognizer.Builder(contextRef.get()).build();
    }

    @Override
    protected String[] doInBackground(Uri... uris) {
        try {
            boolean multi = new PreferenceManager(contextRef.get()).getMulticolumn();
            StringBuilder printText = new StringBuilder();
            StringBuilder speakText = new StringBuilder();
            for(Uri uri: uris) {
                Bitmap bitmap = BitmapUtil.decodeBitmapUri(contextRef.get(), uri);
                Log.e(TAG, LogUtil.prependCallLocation("doInBackground: " + bitmap));
                if (detector.isOperational() && bitmap != null) {
                    Log.e(TAG, LogUtil.prependCallLocation("fun: detector operational"));
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlockSparseArray = detector.detect(frame);
                    speakText.append(TextBlockUtil.getString(textBlockSparseArray, multi))
                            .append("\n");
                    if(multi) {
                        printText.append(TextBlockUtil.getString(textBlockSparseArray, false))
                                .append("\n");
                    }
                }
            }
            return new String[]{printText.toString(), speakText.toString(), null };
        } catch (FileNotFoundException e) {
            Log.e(TAG, LogUtil.prependCallLocation("doInBackground: "), e);
        }
        return null;
    }
}
