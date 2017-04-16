package thoxvi.imnote2.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.BaseDatas.Note.Note;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.DataBaseService;
import thoxvi.imnote2.Services.IClipBinder;
import thoxvi.imnote2.Services.ICommandManagerBinder;
import thoxvi.imnote2.Services.IDeadBinder;
import thoxvi.imnote2.Services.ILiveBinder;
import thoxvi.imnote2.Utils.StringUtil;
import thoxvi.imnote2.Utils.UiUtil;

public class EditActivity extends BaseActivity implements TextWatcher {
    private ILiveBinder mLiveBinder;
    private IDeadBinder mDeadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mLiveBinder = ((ICommandManagerBinder) service).getLiveBinder();
            mDeadBinder = ((ICommandManagerBinder) service).getDeadBinder();

            //是否已经存在判定
            IClipBinder mClipBinder = ((ICommandManagerBinder) service).getClipBinder();
            if (mClipBinder.isClipDataInNotes()) return;

            //是否新建判定
            if (note == null || note.getID() >= 0) return;

            //是否拿到实例判定
            if (mTitle == null || mContent == null) return;

            //是否为分享判定
            if (!mTitle.getEditText().getText().toString().isEmpty()) return;
            if (!mContent.getEditText().getText().toString().isEmpty()) return;
            final String clipData = mClipBinder.getClipData();
            if (!clipData.isEmpty()) {
                UiUtil.showAlertDialog(EditActivity.this, "提示", "检测到剪贴板存在信息:\n\"" + StringUtil.sliteString(clipData, 8) + "\"\n是否快速添加？\n(长按\"+\"按钮可选择类型并快速添加)",  new Runnable() {
                    @Override
                    public void run() {
                        if (clipData.length() <= 8) {
                            mTitle.getEditText().setText(clipData);
                            mContent.getEditText().requestFocus();
                        } else {
                            mContent.getEditText().setText(clipData);
                        }
                    }
                }, ()->{});
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private TextInputLayout mTitle;
    private TextInputLayout mContent;

    private INoteBO note;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("编辑");
        initServiceConnection();

        Intent i = getIntent();
        String title = i.getStringExtra(Note.CLASS_TITLE);
        if (title == null) title = "";
        String content = i.getStringExtra(Note.CLASS_CONTENT);
        if (content == null) content = "";
        long id = i.getLongExtra(Note.CLASS_ID, -1);
        index = i.getIntExtra(Note.CLASS_INDEX, -1);

        note = new Note(
                id,
                title,
                content
        );

        mTitle = (TextInputLayout) findViewById(R.id.activity_edit_title);
        mContent = (TextInputLayout) findViewById(R.id.activity_edit_content);

        String type = i.getType();
        String action = i.getAction();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (Tmper.TEXT_PLAIN.equals(type)) {
                setTitle("快速记录");
                String s = i.getStringExtra(Intent.EXTRA_TEXT);
                if (s.length() <= 8) {
                    mTitle.getEditText().setText(s);
                    mContent.requestFocus();
                } else {
                    mContent.getEditText().setText(s);
                    mTitle.requestFocus();
                }
            }
        } else {
            mTitle.getEditText().setText(note.getTitle());
            mContent.getEditText().setText(note.getContent());
            if (!note.getTitle().isEmpty()) {
                mContent.getEditText().requestFocus();
                mContent.getEditText().setSelection(note.getContent().length());
            }
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mTitle.getEditText().addTextChangedListener(this);
        mContent.getEditText().addTextChangedListener(this);


        findViewById(R.id.activity_edit_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                finishAfterTransition();
            }
        });
    }

    private void initServiceConnection() {
        Intent i = new Intent(this, DataBaseService.class);
        bindService(i, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
        unbindService(connection);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            saveData();
//            finish();
//        }
//        return false;
//    }

    private void saveData() {
        note.setTitle(mTitle.getEditText().getText().toString());
        note.setContent(mContent.getEditText().getText().toString());
        note.setTime((new Date()).getTime());

        //服务未运行不保存
        if (mLiveBinder == null) {
            Toast.makeText(this, "数据库服务未打开！", Toast.LENGTH_SHORT).show();
            return;
        }

        //为空删除
        if (note.getTitle().isEmpty() && note.getContent().isEmpty()) {
            if (index == -1) return;
            mLiveBinder.delete(index);
            mDeadBinder.delete(0, note.getID());
            index = -1;
            return;
        }


        // 保存
        if (note.getID() >= 0 && index >= 0) {
            mLiveBinder.update(index, note);
            // TODO: 2017/3/12 更新置顶设置
//            index = 0;
        } else {
            note = mLiveBinder.insert(note);
            index = 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (note.getID() >= 0) {
            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        saveData();
    }
}
