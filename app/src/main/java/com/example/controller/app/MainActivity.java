package com.example.controller.app;

import android.content.Intent;
import android.os.Bundle;
//import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.controller.app.currency.ClassifierActivity;
import com.example.controller.app.ocr.OcrCaptureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static final boolean DEV_MODE = true;
    private static final int CHECK_TTS_CODE = 10;

    //    private GestureDetectorCompat mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, LogUtil.prependCallLocation("onCreate: "));
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, CHECK_TTS_CODE);
        super.onCreate(savedInstanceState);
        /*if (DEV_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }*/
        setContentView(R.layout.activity_main);
//        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        findViewAndSetThisAsOnClickListener(R.id.btn_ocr, R.id.btn_curr,R.id.btn_translate,R.id.btn_ocr_setting, R.id.btn_hlp);
    }

    private void findViewAndSetThisAsOnClickListener(@IdRes int ... viewIds) {
        for(int viewId : viewIds) {
            findViewById(viewId).setOnClickListener(this);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                CTextToSpeech tts = new CTextToSpeech(this);
                tts.stop();
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    @Override
    public void onClick(View view) {
        Log.e(TAG, LogUtil.prependCallLocation("onClick: "+view.getId()+" "+view.getContentDescription()));
        switch (view.getId()) {
            case R.id.btn_ocr:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: OCR button"));
                // launch Ocr capture activity.
                Intent intent = new Intent(this, OcrCaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_curr:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: Currency Button"));

                intent = new Intent(this, ClassifierActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_translate:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: "));
                intent = new Intent(this, TranslateActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_hlp:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: Help"));
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ocr_setting:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: OCR Settings"));
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
    }


/*    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        public final String TAG = MyGestureListener.class.getSimpleName();
        @Override
        public void onLongPress(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onLongPress: "));
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onSingleTapConfirmed: "));
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, LogUtil.prependCallLocation("onFling: "));
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onDoubleTap: "));
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onDown: "));
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onDoubleTapEvent: "));
            return super.onDoubleTapEvent(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.e(TAG, LogUtil.prependCallLocation("onShowPress: "));
            super.onShowPress(e);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

//        this.mDetector.onTouchEvent(event);
//        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.e(TAG, LogUtil.prependCallLocation("dispatchTouchEvent: Action Down"));
//        }else if(event.getAction() == MotionEvent.ACTION_UP) {
//            Log.e(TAG, LogUtil.prependCallLocation("dispatchTouchEvent: Action up"));
//        }else if(event.getAction() == MotionEvent.ACTION_MOVE) {
//            Log.e(TAG, LogUtil.prependCallLocation("dispatchTouchEvent: Action Move"));
//        } else {
//            Log.e(TAG, LogUtil.prependCallLocation("dispatchTouchEvent: "+event.toString()));
//        }

        return super.dispatchTouchEvent(event);
    }*/
}
