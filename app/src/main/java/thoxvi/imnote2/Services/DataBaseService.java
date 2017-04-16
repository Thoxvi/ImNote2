package thoxvi.imnote2.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import thoxvi.imnote2.Activities.BaseActivity;
import thoxvi.imnote2.Activities.MainActivity;
import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.BaseDatas.Note.Note;
import thoxvi.imnote2.R;
import thoxvi.imnote2.SQLHelpers.DataBaseComonder;
import thoxvi.imnote2.SQLHelpers.NoteManage;
import thoxvi.imnote2.Utils.StringUtil;
import thoxvi.imnote2.Utils.UiUtil;

public class DataBaseService extends Service {
    private DataBaseComonder mNoteManage;

    private LiveBinder mLiveBinder = new LiveBinder();
    private DeadBinder mDeadBinder = new DeadBinder();
    private ClipBinder mClipBinder = new ClipBinder();
    private RemindBinder mRemindBinder = new RemindBinder();

    private CommandManagerBinder mCommandManagerBinder = new CommandManagerBinder();

    public void onCreate() {
        super.onCreate();
        mNoteManage = new NoteManage(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mCommandManagerBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClipBinder.isClipListenerStart()) {
            mClipBinder.stopClipListener();
        }
        ((LiveBinder) mLiveBinder).sortList();
        ((DeadBinder) mDeadBinder).sortList();
    }

    private List<INoteBO> getAllNotes() {
        List<INoteBO> notes = new ArrayList<>();
        notes.addAll(mLiveBinder.getNewNotes());
        notes.addAll(mDeadBinder.getNotes());
        return notes;
    }

    private class CommandManagerBinder extends Binder implements ICommandManagerBinder {
        @Override
        public ILiveBinder getLiveBinder() {
            return mLiveBinder;
        }

        @Override
        public IDeadBinder getDeadBinder() {
            return mDeadBinder;
        }

        @Override
        public IRemindBinder getRemindBinder() {
            return mRemindBinder;
        }

        @Override
        public IClipBinder getClipBinder() {
            return mClipBinder;
        }
    }

    private class LiveBinder implements ILiveBinder {
        private List<INoteBO> notes;
        private RecyclerView.Adapter mAdapter;

        @Override
        public INoteBO insert(INoteBO note) {
            note = mNoteManage.insert(note);
            if (notes != null) {
                notes.add(0, note);
                insertUI(0);
                updateAllUI();
            }
            return note;
        }

        @Override
        public void delete(int index) {
            mDeadBinder.insert(notes.get(index));
            notes.remove(index);
            removeUI(index);
        }

        @Override
        public void relive(INoteBO note) {
            note = mNoteManage.reliveNote(note);
            notes.add(0, note);
            insertUI(0);
            updateAllUI();
        }

        INoteBO getNoteByID(long id) {
            for (INoteBO note : getNotes()) {
                if (note.getID() == id) {
                    return note;
                }
            }
            return null;
        }

        @Override
        public void moveTop(int index) {
            INoteBO note = notes.get(index);
            notes.remove(index);
            notes.add(0, note);
            moveUI(index);
        }

        @Override
        public void update(int index, INoteBO note) {
            mNoteManage.update(note);
            notes.set(index, note);
            updateUI(index);
            // TODO: 2017/3/12 更新移动到顶端设置
//            notes.remove(index);
//            notes.add(0, note);
//            moveUI(index);
        }

        @Override
        public List<INoteBO> getNotes() {
            if (notes == null) {
                return getNewNotes();
            } else {
                return notes;
            }
        }

        @Override
        public List<INoteBO> getNewNotes() {
            notes = mNoteManage.getNotesByStatus(Note.NOTE_STATUS_LIVE);
            return notes;
        }

        @Override
        public void getNewNotes(Runnable doneTodo) {
            //开线程记得start()!
            new Thread(() -> {
                sortList();
                notes = mNoteManage.getNotesByStatus(Note.NOTE_STATUS_LIVE);
                if (doneTodo == null) return;
                doneTodo.run();
            }).start();
        }

        @Override
        public void setAdapter(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
        }

        void sortList() {
            if (notes != null && mNoteManage != null) {
                mNoteManage.sortList(notes);
            }
        }

        private void removeUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemRemoved(index);
            mAdapter.notifyItemRangeChanged(index, mAdapter.getItemCount());

        }

        private void insertUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemInserted(index);
        }

        private void moveUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemMoved(index, 0);
            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());

        }

        private void updateUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemChanged(index);
        }

        private void updateAllUI() {
            if (mAdapter == null) return;
            mAdapter.notifyDataSetChanged();
        }

    }

    private class DeadBinder implements IDeadBinder {
        private List<INoteBO> notes;
        private RecyclerView.Adapter mAdapter;

        public INoteBO insert(INoteBO note) {
            note = mNoteManage.killNote(note);
            if (notes != null) {
                notes.add(0, note);
                insertUI(0);
            }
            return note;
        }

        @Override
        public void deleteAll() {
            while (getNotes().size() != 0) {
                mNoteManage.delete(getNotes().get(0).getID());
                getNotes().remove(0);
                removeUI(0);
            }
        }

        @Override
        public void delete(int index, long id) {
            mNoteManage.delete(id);
            notes.remove(index);
            removeUI(index);
        }

        @Override
        public void relive(int index) {
            try {
                //点快了会死
                getNotes();
                mLiveBinder.relive(notes.get(index));
                notes.remove(index);
            } catch (IndexOutOfBoundsException e) {
            }
            removeUI(index);
        }

        @Override
        public List<INoteBO> getNotes() {
            if (notes == null) {
                return getNewNotes();
            } else {
                return notes;
            }

        }

        @Override
        public List<INoteBO> getNewNotes() {
            sortList();
            notes = mNoteManage.getNotesByStatus(Note.NOTE_STATUS_DEAD);
            return notes;
        }

        @Override
        public void getNewNotes(Runnable doneTodo) {
            new Thread(() -> {
                sortList();
                notes = mNoteManage.getNotesByStatus(Note.NOTE_STATUS_DEAD);

                if (doneTodo == null) return;
                doneTodo.run();
            }).start();
        }

        @Override
        public void setAdapter(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
        }

        void sortList() {
            if (notes != null && mNoteManage != null) {
                mNoteManage.sortList(notes);
            }
        }

        private void removeUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemRemoved(index);
            mAdapter.notifyItemRangeChanged(index, mAdapter.getItemCount());
        }

        private void insertUI(int index) {
            if (mAdapter == null) return;
            mAdapter.notifyItemInserted(index);
        }

    }

    private class RemindBinder implements IRemindBinder {
        private boolean isInRemindAll = false;

        @Override
        public void remindLiveNotes() {
            remindNote(mLiveBinder.getNotes());
        }

        @Override
        public void remindNote(Activity activity, long id) {
            INoteBO note = mLiveBinder.getNoteByID(id);
            if (note == null) return;
            UiUtil.showAlertDialog(activity, note.getTitle(), note.getContent(), ()->remindNote(note), null);
        }

        @Override
        public void remindNote(INoteBO note) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DataBaseService.this);
            //为空处理
            String title = note.getTitle().isEmpty() ? "提醒" : note.getTitle();
            //空就空
            String content = note.getContent();

            Intent intent = new Intent(DataBaseService.this, MainActivity.class);
            intent.putExtra(IRemindBinder.FROM_REMIND_NOTIFICATION, note.getID());

            mBuilder.setSmallIcon(R.mipmap.ic_only_remind)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setTicker("提醒")

                    .setAutoCancel(true)
                    .setOngoing(false)

                    .setContentIntent(PendingIntent.getActivity(DataBaseService.this, (int) note.getID(), intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ico_remind));
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify((int) note.getID(), mBuilder.build());
        }

        @Override
        public void cancelRemind(long id) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel((int) id);
        }

        @Override
        public boolean areNotificationsEnabled() {
            return (NotificationManagerCompat.from(DataBaseService.this)).areNotificationsEnabled();
        }

        private void remindNote(final List<INoteBO> notes) {
            if (isInRemindAll) {
                Toast.makeText(DataBaseService.this, "正在添加通知栏提醒！请稍候！", Toast.LENGTH_SHORT).show();
                return;
            }

            final List<INoteBO> tmpList = new ArrayList<>();
            for (int i = notes.size() - 1; i >= 0; i--) {
                tmpList.add(notes.get(i));
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    isInRemindAll = true;
                    for (INoteBO note : tmpList) {
                        remindNote(note);
                    }
                    isInRemindAll = false;
                }
            }).start();
        }

    }

    //使用quickInsert之前一定要SetActivity
    private class ClipBinder implements IClipBinder {
        private BaseActivity mActivity = null;
        private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                quickInsert(true);
            }
        };

        private boolean ClipListenerStatus = false;

        @Override
        public void startClipListener() {
            if (!ClipListenerStatus) {
                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
                Toast.makeText(DataBaseService.this, "已开启监听剪贴板！", Toast.LENGTH_SHORT).show();
                ClipListenerStatus = true;
            } else {
                Toast.makeText(DataBaseService.this, "已开启监听剪贴板！", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void stopClipListener() {
            if (ClipListenerStatus) {
                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
                ClipListenerStatus = false;
            }

            Toast.makeText(DataBaseService.this, "已关闭监听剪贴板！", Toast.LENGTH_SHORT).show();

        }

        @Override
        public boolean isClipListenerStart() {
            return ClipListenerStatus;
        }

        @Override
        public boolean isClipDataInNotes() {
            String s = getClipData();
            for (INoteBO note : getAllNotes()) {
                if (note.getTitle().equals(s) || note.getContent().equals(s)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getClipData() {
            ClipData data = ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getPrimaryClip();
            if (data == null) {
                return "";
            } else {
                return data.getItemAt(0).getText().toString();
            }
        }

        @Override
        public void quickInsert(boolean useSystem) {
            String s = getClipData();
            if (s.isEmpty()) return;
            if (mActivity == null) {
                return;
            }

            List<UiUtil.SelectStrAndRun> selects = new ArrayList<>();
            selects.add(new UiUtil.SelectStrAndRun("可读文本", new Runnable() {
                @Override
                public void run() {
                    insert(s);
                    Toast.makeText(DataBaseService.this, "快速添加\"" + StringUtil.sliteString(s, 8) + "\"成功！", Toast.LENGTH_SHORT).show();
                }
            }));
            if (StringUtil.maybeBase64ThenJson(s)) {
                selects.add(new UiUtil.SelectStrAndRun("Base64对象", new Runnable() {
                    @Override
                    public void run() {
                        String text = StringUtil.stringFromBase64(s);
                        INoteBO note = StringUtil.objFromJson(text, Note.class);
                        mLiveBinder.insert(note);
                        Toast.makeText(DataBaseService.this, "快速添加\"" + StringUtil.sliteString(((INoteBiz) note).getEasyInfo(), 8) + "\"成功！", Toast.LENGTH_SHORT).show();
                    }
                }));
            } else if (StringUtil.maybeBase64(s)) {
                selects.add(new UiUtil.SelectStrAndRun("Base64文本", new Runnable() {
                    @Override
                    public void run() {
                        String text = StringUtil.stringFromBase64(s);
                        insert(text);
                        Toast.makeText(DataBaseService.this, "快速添加\"" + StringUtil.sliteString(text, 8) + "\"成功！", Toast.LENGTH_SHORT).show();
                    }
                }));
            }

            selects.add(new UiUtil.SelectStrAndRun("先看看内容", new Runnable() {
                @Override
                public void run() {
                    String message = "作为可读文本：\n\n" + s;
                    if (StringUtil.maybeBase64ThenJson(s)) {
                        String text = StringUtil.stringFromBase64(s);
                        INoteBO note = StringUtil.objFromJson(text, Note.class);
                        message += "\n\n作为对象：\n\n" + note.getTitle() + "\n" + note.getContent();
                    } else if (StringUtil.maybeBase64(s)) {
                        String text = StringUtil.stringFromBase64(s);
                        message += "\n\n作为Base64解码：\n\n" + text;
                    }
                    UiUtil.showSystemAlertDialog(mActivity, "\"" + StringUtil.sliteString(s, 5) + "\"是…", message, () -> {
                        quickInsert(useSystem);
                    }, () -> {
                        quickInsert(useSystem);
                    });
                }
            }));

            selects.add(new UiUtil.SelectStrAndRun("不要添加",null));


            if (useSystem && !mActivity.isEnable()) {
                UiUtil.showSystemSelectAlertDialog(mActivity, "\"" + StringUtil.sliteString(s, 5) + "\"是一个…", selects);
            } else {
                UiUtil.showSelectAlertDialog(mActivity, "\"" + StringUtil.sliteString(s, 5) + "\"是一个…", selects);
            }

        }

        @Override
        public IClipBinder setActivity(BaseActivity mActivity) {
            this.mActivity = mActivity;
            return this;
        }

        private void insert(String s) {
            String title = "";
            String content = "";
            if (s.length() <= 8) title = s;
            else content = s;
            INoteBO note = new Note(-1, title, content);
            mLiveBinder.insert(note);
        }
    }
}
