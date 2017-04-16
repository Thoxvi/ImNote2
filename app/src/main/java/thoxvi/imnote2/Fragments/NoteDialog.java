package thoxvi.imnote2.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import thoxvi.imnote2.Activities.Tmper;
import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.Note;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.DataBaseService;
import thoxvi.imnote2.Services.ICommandManagerBinder;
import thoxvi.imnote2.Services.ILiveBinder;

/**
 * Created by Thoxv on 2017/3/6.
 */

public class NoteDialog extends DialogFragment implements TextWatcher {
    private ILiveBinder mLiveBinder;
    private EditText mTitle;
    private EditText mContent;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLiveBinder = ((ICommandManagerBinder) service).getLiveBinder();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initServiceConnection();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_edit, null);

        mTitle = ((TextInputLayout) v.findViewById(R.id.fragment_edit_title)).getEditText();
        mContent = ((TextInputLayout) v.findViewById(R.id.fragment_edit_content)).getEditText();

        Bundle b = getArguments();
        String s = b.getString(Tmper.TEXT_PLAIN, "");

        if (mTitle != null && mContent != null) {
            if (s.length() <= 8) {
                mTitle.setText(s);
                mContent.requestFocus();
            } else {
                mContent.setText(s);
                mTitle.requestFocus();
            }
        }

        builder.setView(v).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mTitle == null || mContent == null) {
                    getActivity().finish();
                    return;
                }
                saveData();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
                //取消就取消
            }
        }).setTitle("快速记录");

        return builder.create();
    }

    private void saveData() {
        //为空不保存
        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        if (title.isEmpty() && content.isEmpty()) return;
        //服务未运行不保存
        if (mLiveBinder == null) {
            Toast.makeText(getActivity(), "数据库服务未打开！", Toast.LENGTH_SHORT).show();
        } else {
            INoteBO note = mLiveBinder.insert(new Note(-1, title, content));
            Toast.makeText(getActivity(), "记录成功！", Toast.LENGTH_SHORT).show();
        }

    }

    private void initServiceConnection() {
        Intent i = new Intent(getActivity(), DataBaseService.class);
        getActivity().bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(connection);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().
                postDelayed(new Runnable() {
                    public void run() {
                        InputMethodManager inManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }, 80);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        saveData();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
