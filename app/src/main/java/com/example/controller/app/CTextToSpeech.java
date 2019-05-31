package com.example.controller.app;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class CTextToSpeech {
    private static final String TAG = CTextToSpeech.class.getSimpleName();
    private static TextToSpeech tts;

    public CTextToSpeech(Context context, String langCode) {
        TextToSpeech.OnInitListener listener =
                status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        Log.e("OnInitListener", LogUtil.prependCallLocation(context.getString(R.string.tts_init_success)));
                        try {
                            if(langCode==null || langCode.isEmpty()) {
                                tts.setLanguage(Locale.getDefault());
                            } else {
                                int s1 = tts.setLanguage(Locale.forLanguageTag(langCode));
//                                int s2 = tts.setLanguage(new Locale(langCode));
//                                Log.e(TAG, LogUtil.prependCallLocation("CTextToSpeech: " + s1 + " " + s2));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, LogUtil.prependCallLocation("onInit: Error"),e);
                            tts.setLanguage(Locale.getDefault());
                        }
                    } else {
                        Log.e("OnInitListener", LogUtil.prependCallLocation(context.getString(R.string.tts_init_failed)));
                    }
                };
        tts = new TextToSpeech(context, listener);
    }

    CTextToSpeech(Context context) {
        this(context, Locale.getDefault().getISO3Language());
    }

    public void speak(CharSequence text) {
        if (Build.VERSION.SDK_INT >= 21) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DEFAULT");
        } else {
            tts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void stop() {
        if(tts!=null) {
            tts.stop();
        }
    }
    public void shutdown() {
        if(tts!=null) {
            tts.shutdown();
        }
    }
    public boolean isLangAvailable(Locale locale) {
        Log.e(TAG, LogUtil.prependCallLocation("isLangAvailable: "+tts.isLanguageAvailable(locale))+ " "+locale.toString());
        int status =  tts.isLanguageAvailable(locale);
        return status==TextToSpeech.LANG_AVAILABLE;
    }

//    public boolean isLangAvailable(String langCode) {
//        return isLangAvailable(new Locale(langCode));
//    }
    public boolean isLangAvailable(String langCode) {
        return isLangAvailable(Locale.forLanguageTag(langCode));
    }

    public boolean setLanguage(String langCode) {
        if(langCode==null) {
            return setDefault();
        }
        int status = tts.setLanguage(new Locale(langCode));
        return status == TextToSpeech.LANG_AVAILABLE;
    }

    public boolean setDefault() {
        return TextToSpeech.LANG_AVAILABLE == tts.setLanguage(Locale.getDefault());
    }
}
