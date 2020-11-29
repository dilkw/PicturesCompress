package com.demo.picturescompress;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tinify.Source;
import com.tinify.Tinify;

import java.io.File;
import java.io.IOException;

import leakcanary.AppWatcher;
import leakcanary.ObjectWatcher;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.MyDialogListener {

    static private int RESULT_LOAD_IMAGE = 1;

    private TextView startSizeTextView, endSizeTextView, timesTextView, apiKeyTextView;

    private MaterialButton selectBtn, keyEditBtn, compressBtn;

    private ImageView imageView;

    private String filePath;

    private Source source = null;

    private int number = 0;

    private MyDialogFragment myDialogFragment;

    ViewPager viewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppWatcher.INSTANCE.getObjectWatcher().watch(this,"Activity");
        imageView = findViewById(R.id.imageView);
        startSizeTextView = findViewById(R.id.startSize);
        endSizeTextView = findViewById(R.id.endSize);
        timesTextView = findViewById(R.id.times);
        apiKeyTextView = findViewById(R.id.apiKey_textView);
        compressBtn = findViewById(R.id.compress_button);
        selectBtn = findViewById(R.id.select_button);
        keyEditBtn = findViewById(R.id.apiKey_edit);
        keyEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogFragment = new MyDialogFragment();
                ObjectWatcher objectWatcher = AppWatcher.INSTANCE.getObjectWatcher();
                objectWatcher.watch(myDialogFragment,"myDialogFragment");
//                RefWatcher refWatcher = MyApplication.getRefWatcher(MainActivity.this);
//                if (refWatcher != null) {
//                    Log.d("TAG", "onClick: notNull");
//                    refWatcher.watch(myDialogFragment);
//                }
                myDialogFragment.show(getSupportFragmentManager(),"MyDialogFragment");
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImg();
            }
        });

        compressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "compressBtn onClick:开始压缩 ");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            source = Tinify.fromFile(filePath);
                            source.toFile(filePath);
                            final int length = source.result().size();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("TAG", "compressBtn onClick:完成 ");
                                    //Toast.makeText(getApplicationContext(),"压缩成功",Toast.LENGTH_SHORT).show();
                                    endSizeTextView.setText(length / 1024 + "KB");
                                    timesTextView.setText(++number + "/500");
                                }
                            });

                        } catch (IOException e) {
                            Log.d("TAG", "compressBtn onClick:失败 ");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        apiKeyTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (apiKeyTextView.getText().toString().trim().isEmpty()) {
                    selectBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void selectImg() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
//            paths = uri.getPathSegments();
            String[] paths = {MediaStore.Images.Media.DATA};
            Log.d("TAG", "onActivityResult: path=------" + uri.getPath());
            imageView.setImageURI(uri);
            selectBtn.setEnabled(true);
            Cursor cursor = getContentResolver().query(uri, paths, null, null, null);
            assert cursor != null;
            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(column_index);
                File file = new File(filePath);
                startSizeTextView.setText(file.length() / 1024 + "KB");
            }
            Log.d("TAG", "onActivityResult: " + filePath);
            cursor.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (myDialogFragment != null) {
            Log.d("TAG", "onDestroy: ");
            myDialogFragment = null;
        }
        super.onDestroy();
    }

    @Override
    public void enterOnClicked(DialogFragment dialogFragment) {
        number = Tinify.compressionCount();
        timesTextView.setText(number + "/500");
        apiKeyTextView.setText(myDialogFragment.getApiKey());
        if (myDialogFragment != null) {
            myDialogFragment.dismiss();
        }
    }
}