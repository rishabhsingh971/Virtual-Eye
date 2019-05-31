package com.example.controller.app;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class HelpActivity extends AppCompatActivity {

    CTextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView tvOcr = findViewById(R.id.tv_hlp_ocr);
//         Set up the Text To Speech engine.
        tts = new CTextToSpeech(this.getApplicationContext());
        fun(R.id.tv_hlp_curr, R.id.tv_hlp_ocr, R.id.tv_hlp_trans, R.id.tv_hlp_txt_man);

    }

    private void fun(int ...resIds) {
        for(int id:resIds) {
            ((TextView)findViewById(id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tts.speak(((TextView)v).getText().toString());
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tts!=null) {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(tts!=null) {
//            tts.shutdown();;
//        }
    }
}
