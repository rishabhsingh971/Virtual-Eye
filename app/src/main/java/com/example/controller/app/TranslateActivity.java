package com.example.controller.app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TranslateActivity.class.getSimpleName();
    EditText edtSrc;
    Button btnTrans;
    Spinner sprTransLang;
    TranslateAsyncTask translateAsyncTask;
    PreferenceManager manager;
    private String transLangCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        manager = new PreferenceManager(getApplicationContext());
        edtSrc = findViewById(R.id.edt_src);
//        setTheme(R.style.AppTheme_Dark);
        sprTransLang = findViewById(R.id.spr_trans_lang);
        /*// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_trans_lang, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sprTransLang.setAdapter(adapter);*/
        btnTrans = findViewById(R.id.btn_trans);
        btnTrans.setOnClickListener(this);
        String[] transLangCodes = getResources().getStringArray(R.array.arr_trans_lang_code);
        int transLangPos = manager.getTransLangPos();
        transLangCode = transLangCodes[transLangPos];
        sprTransLang.setSelection(transLangPos, true);

        String data = getIntent().getStringExtra(TextManagerActivity.tagPrintText);
        if(data!=null && !data.isEmpty()) {
            edtSrc.setText(data);
        }
        sprTransLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    transLangCode = transLangCodes[position];
                    manager.setTransLangPos(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, LogUtil.prependCallLocation("onClick: "));
        switch (v.getId()) {
            case R.id.btn_trans:
                String srcText = edtSrc.getText().toString();
                if (!srcText.isEmpty()) {
                    translateAsyncTask = new TranslateAsyncTask(this, new TranslateAsyncTask.TaskListener() {
                        @Override
                        public void onFinished(Object result) {
                            Log.e(TAG, LogUtil.prependCallLocation("onFinished: "+result));
                            Intent intent = new Intent(getApplicationContext(), TextManagerActivity.class);
                            intent.putExtra(TextManagerActivity.tagPrintText, (String) result);
                            intent.putExtra(TextManagerActivity.tagLangCode, transLangCode);
                            startActivity(intent);
                        }
                    });
                    translateAsyncTask.execute(srcText, transLangCode);

                }
            break;
        }
    }

    private static class TranslateAsyncTask extends CAsyncTask<String, Void, String> {
        private static final String TAG = TranslateAsyncTask.class.getSimpleName();

        TranslateAsyncTask(Context context, TaskListener taskListener) {
            super(context, taskListener);
            Log.e(TAG, LogUtil.prependCallLocation("TranslateAsyncTask: "));
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e(TAG, LogUtil.prependCallLocation("doInBackground: "));
            String result = null;
            try {
                result = Translate.translate(params[0], params[1]);
                Log.e(TAG, LogUtil.prependCallLocation("doInBackground: translation "+ result));
//                Log.e(TAG, LogUtil.prependCallLocation("doInBackground: get language "+Translate.getLanguages()));
            } catch (Exception e) {
                Log.e(TAG, LogUtil.prependCallLocation("doInBackground: Error " + e));
                e.printStackTrace();
            }
            return result;
        }

    }
}
