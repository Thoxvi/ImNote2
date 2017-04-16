package thoxvi.imnote2.Services;

import android.app.Activity;

import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public interface IRemindBinder {
    final String FROM_REMIND_NOTIFICATION="FROM_REMIND_NOTIFICATION";

    void remindLiveNotes();
    void remindNote(Activity activity,long id);
    void remindNote(INoteBO note);
    void cancelRemind(long id);
    boolean areNotificationsEnabled();
//    void remindNote(List<INoteBO> notes);
}
