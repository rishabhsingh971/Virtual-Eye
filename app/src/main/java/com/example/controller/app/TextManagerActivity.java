package com.example.controller.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

public class TextManagerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String tagPrintText = "result1";
    public static final String tagSpeakText = "result2";
    public static final String tagLangCode = "result3";
    private static final String TAG = TextManagerActivity.class.getSimpleName();
//    private TextView tvResult;
    private EditText etResult;
    CTextToSpeech tts;
    String speakText;
    private String langCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, LogUtil.prependCallLocation("onCreate: "));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);
        Button btn_save, btn_share, btn_copy, btn_trans, btn_edit, btn_speak;
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_copy = (Button) findViewById(R.id.btn_copy);
        btn_copy.setOnClickListener(this);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
        btn_trans = (Button) findViewById(R.id.btn_trans);
        btn_trans.setOnClickListener(this);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        btn_speak = (Button) findViewById(R.id.btn_speak);
        btn_speak.setOnClickListener(this);
//        tvResult = (TextView) findViewById(R.id.tv_result);
        etResult = (EditText) findViewById(R.id.et_result);
        etResult.setFocusableInTouchMode(false);
//        tvResult.setOnClickListener(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
            String printText = intent.getStringExtra(tagPrintText);
            Log.e(TAG, "onCreate: "+printText );
            if (printText != null && !printText.isEmpty()) {
                etResult.setText(printText);
                speakText = printText;
            }
            Log.e(TAG, "onCreate: "+speakText );
            String speakText = intent.getStringExtra(tagSpeakText);
            if (speakText != null && !speakText.isEmpty()) {
                this.speakText = speakText;
                if(printText==null) {
                    etResult.setText(speakText);
                }
            }
            String langCode = intent.getStringExtra(tagLangCode);
            if(langCode!=null) {
                this.langCode = langCode;
            }
        }
        // Set up the Text To Speech engine.
        tts = new CTextToSpeech(getApplicationContext(), langCode);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                Button btn = ((Button) v);
                if(btn.getText().equals(getString(R.string.str_done))) {
//                    tvResult.setText(etResult.getText());
//                    etResult.setVisibility(View.GONE);
//                    tvResult.setVisibility(View.VISIBLE);
                    v.requestFocus();
                    etResult.setCursorVisible(false);
                    etResult.setFocusableInTouchMode(false);
                    btn.setText(R.string.str_edit);
                } else {
//                    tvResult.setVisibility(View.GONE);
//                    etResult.setText(tvResult.getText());
//                    etResult.setVisibility(View.VISIBLE)
                    etResult.setCursorVisible(true);
                    etResult.setFocusableInTouchMode(true);
                    btn.setText(R.string.str_done);
                }
                break;
            case R.id.btn_copy:// Gets a handle to the clipboard service.
                Log.e(TAG, LogUtil.prependCallLocation("onClick: copy"));
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                if (etResult.getText().length() != 0) {
                    ClipData clip = ClipData.newPlainText("result", etResult.getText());
                    Objects.requireNonNull(clipboard).setPrimaryClip(clip);
                } else {
                    Log.e(TAG, LogUtil.prependCallLocation("onClick: Nothing to Copy"));
                }

                break;
            case R.id.btn_save:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: save"));
                String result = etResult.getText().toString();
                if (result.length() != 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String filename = formatter.format(new Date())+".txt";
                    File mFile = new File(Objects.requireNonNull(this.getExternalFilesDir(null)), filename);
                    FileOutputStream outputStream;

                    try {
//                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

                        outputStream = new FileOutputStream(mFile);

                        outputStream.write(result.getBytes());
                        Log.e(TAG, LogUtil.prependCallLocation("onClick: saved " + filename + " " + outputStream.getFD()));
                        MessageUtil.toast(getApplicationContext(), "Saved "+filename + " in "+getExternalFilesDir(null));
                        outputStream.close();
                    } catch (Exception e) {
                        Log.e(TAG, LogUtil.prependCallLocation("onClick: Error " + e.getMessage()));
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, LogUtil.prependCallLocation("onClick: nothing to save"));
                }
                break;
            case R.id.btn_share:
                //Share text:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: Share "));
                result = etResult.getText().toString();

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, result);
                startActivity(Intent.createChooser(intent2, "Share via"));
                break;
            case R.id.btn_trans:
                Log.e(TAG, LogUtil.prependCallLocation("onClick: Trans "));
                Intent intent = new Intent(getApplicationContext(), TranslateActivity.class);
                intent.putExtra(tagPrintText, etResult.getText().toString());
                finish();
                startActivity(intent);
                break;
            case R.id.btn_speak:
            case R.id.et_result:
                if(langCode!=null) {
                    if (!tts.isLangAvailable(langCode)) {
                        Locale locale = new Locale(langCode);
                        MessageUtil.toast(getApplicationContext(), "Sorry! " + locale.getDisplayLanguage() + " Language not installed. Trying Default Language.");
                        tts.setDefault();
                    }
                    tts.setLanguage(langCode);
                }
                tts.speak(speakText);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (tts != null) {
//            tts.shutdown();
//        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            etResult.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            OcrAsyncTask ocrAsyncTask = new OcrAsyncTask(getApplicationContext(), new CAsyncTask.TaskListener() {
                @Override
                public void onFinished(Object result) {
                    Log.e(TAG, "onFinished: " + result);
                    if (result == null) {
                        Log.e(TAG, LogUtil.prependCallLocation("onFinished: Text could not be read from given image"));
                        MessageUtil.toast(getApplicationContext(), R.string.str_no_text_in_image);
                        return;
                    }
                    String[] results = (String[]) result;
                    if (results.length == 3) {
                        etResult.setText(results[0]);
                        speakText = results[1];
                        langCode = results[2];
//                    if(!tts.isLangAvailable(langCode)) {
//                        MessageUtil.toast(getApplicationContext(), "Ocr language not installed");
//                    }

                    }
                }
            });
            ocrAsyncTask.execute(imageUri);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            Uri[] uris = (Uri[]) imageUris.toArray();
            OcrAsyncTask ocrAsyncTask = new OcrAsyncTask(getApplicationContext(), new CAsyncTask.TaskListener() {
                @Override
                public void onFinished(Object result) {
                    Log.e(TAG, "onFinished: " + result);
                    if (result == null) {
                        Log.e(TAG, LogUtil.prependCallLocation("onFinished: Text could not be read from given image"));
                        MessageUtil.toast(getApplicationContext(), R.string.str_no_text_in_image);
                        return;
                    }
                    String[] results = (String[]) result;
                    if (results.length == 3) {
                        etResult.setText(results[0]);
                        speakText = results[1];
                        langCode = results[2];
//                    if(!tts.isLangAvailable(langCode)) {
//                        MessageUtil.toast(getApplicationContext(), "Ocr language not installed");
//                    }

                    }
                }
            });
            ocrAsyncTask.execute(uris);
        }
    }
}
