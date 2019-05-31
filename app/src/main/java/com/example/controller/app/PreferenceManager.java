package com.example.controller.app;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String KEY_AUTOFOCUS = "autofocus";
    private static final String KEY_FLASH = "flash";
    private static final String KEY_OCR_LANG = "ocr_lang";
//    public static final String KEY_TRANS_SRC_LANG = "src_lang";
    public static final String KEY_TRANS_TGT_LANG = "tgt_lang";
    private static final String KEY_MULTICOLUMN = "key_multicomun";
    private static SharedPreferences sharedPreferences;



    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("SP_BA_VE", Context.MODE_PRIVATE);
    }

//    public static void init(Context context) {
//        sharedPreferences = context.getSharedPreferences("SP_BA_VE", Context.MODE_PRIVATE);
//    }
    private boolean getBoolean(String key, boolean defVal) {
        return sharedPreferences.getBoolean(key, defVal);
    }
    private int getInt(String key, int defVal) {
        return sharedPreferences.getInt(key, defVal);
    }

    public String getString(String key, String defVal) {
        return sharedPreferences.getString(key, defVal);
    }
    private void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
    private void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }
    private void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }
    public boolean getAutofocus() {
        return getBoolean(KEY_AUTOFOCUS, true);
    }

    public boolean getFlash() {
        return getBoolean(KEY_FLASH, true);
    }

    public boolean getMulticolumn() {
        return getBoolean(KEY_MULTICOLUMN, false);
    }

    public int getOcrLangPos() {
        return getInt(KEY_OCR_LANG, 0);
    }

    public void setOcrLangPos(int position) {
        putInt(KEY_OCR_LANG, position);
    }

    public void setAutoFocus(boolean checked) {
        putBoolean(KEY_AUTOFOCUS, checked);
    }
    public void setFlash(boolean checked) {
        putBoolean(KEY_FLASH, checked);
    }

    public void setMulticolumn(boolean checked) {
        putBoolean(KEY_MULTICOLUMN, checked);
    }

    public int getTransLangPos() {
        return getInt(KEY_TRANS_TGT_LANG, 0);
    }

    public void setTransLangPos(int transLangPos) {
        putInt(KEY_TRANS_TGT_LANG, transLangPos);
    }
}
