package com.example.controller.app;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

//TODO : may add FPS or Preview size too
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Switch swc_autofocus, swc_flash, swc_multicolumn;
    Spinner spr_ocr_lang;
    int ocrLangPos;
    PreferenceManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new PreferenceManager(getApplicationContext());
        setContentView(R.layout.activity_settings);
        swc_autofocus = findViewById(R.id.swc_autofocus);
        swc_flash = findViewById(R.id.swc_flash);
        swc_multicolumn = findViewById(R.id.swc_multicolumn);
        spr_ocr_lang = findViewById(R.id.spr_lang);
        /*// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_ocr_lang, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spr_ocr_lang.setAdapter(adapter);*/
        getSettings();
        swc_autofocus.setOnClickListener(this);
        swc_flash.setOnClickListener(this);
        swc_multicolumn.setOnClickListener(this);
        String[] ocrLangCodes = getResources().getStringArray(R.array.arr_ocr_lang_code);
        final CTextToSpeech tts = new CTextToSpeech(getApplicationContext(), ocrLangCodes[ocrLangPos]);
        spr_ocr_lang.setSelection(ocrLangPos, true);
        spr_ocr_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO : check if tts lang available

                if(tts.isLangAvailable(ocrLangCodes[position])) {
                    ocrLangPos = position;
                    manager.setOcrLangPos(position);
                } else {
                    MessageUtil.toast(getApplicationContext(), "Sorry! This language is not installed.");
                    spr_ocr_lang.setSelection(ocrLangPos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSettings() {
        swc_autofocus.setChecked(manager.getAutofocus());
        swc_flash.setChecked(manager.getFlash());
        swc_multicolumn.setChecked(manager.getMulticolumn());
        spr_ocr_lang.setSelection(manager.getOcrLangPos());
        ocrLangPos = manager.getOcrLangPos();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swc_autofocus:
                manager.setAutoFocus(swc_autofocus.isChecked());
                break;
            case R.id.swc_flash:
                manager.setFlash(swc_flash.isChecked());
                break;
            case R.id.swc_multicolumn:
                manager.setMulticolumn(swc_multicolumn.isChecked());
                break;
        }
    }
}
