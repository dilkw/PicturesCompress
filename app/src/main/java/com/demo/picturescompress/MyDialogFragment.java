package com.demo.picturescompress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.tinify.Tinify;

import java.util.Objects;

public class MyDialogFragment extends DialogFragment {

    private MaterialButton enterBtn, dialogCloseBtn;

    private EditText apiKeyEditText;

    public void setMyDialogListener(MyDialogListener myDialogListener) {
        this.myDialogListener = myDialogListener;
    }

    private MyDialogListener myDialogListener;

    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment", "onCreate: ");
        //LayoutInflater inflater = getLayoutInflater().inflate(R.layout.input_key,)
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("Fragment", "onCreateDialog: ");
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        myDialogListener = (MyDialogListener) getActivity();
        View view = inflater.inflate(R.layout.input_key,null);
        builder.setView(view);
        builder.show();
        enterBtn = view.findViewById(R.id.key_enter);
        dialogCloseBtn = view.findViewById(R.id.dialog_closeBtn);
        apiKeyEditText = view.findViewById(R.id.key_editText);
        progressBar = view.findViewById(R.id.progressBar);
        apiKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (! apiKeyEditText.getText().toString().trim().isEmpty()) {
                    enterBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiKeyEditText.clearFocus();
                enterBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //"2HtdPC0mHzClTzrmZyh2Jmf161mKdxqb"
                        try {
                            Tinify.setKey(apiKeyEditText.getText().toString());
                            Tinify.validate();
                        }catch (Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"API_KEY错误",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                            return;
                        }
                        Log.d("TAG", "run: api_key验证");
                        myDialogListener.enterOnClicked(MyDialogFragment.this);
                    }
                }).start();
            }
        });
        return builder.create();
    }

    public interface MyDialogListener {
        public void enterOnClicked(DialogFragment dialogFragment);
    }

    public String getApiKey() {
        return apiKeyEditText.getText().toString().trim();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myDialogListener = null;
        Log.d("Fragment", "onDestroyView: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Fragment", "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment", "onResume: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment", "onDestroy: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Fragment", "onPause: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment", "onStart: ");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("Fragment", "onAttach: ");
    }

}
