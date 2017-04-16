package thoxvi.imnote2.Services;

import android.app.Activity;
import android.view.View;

import thoxvi.imnote2.Activities.BaseActivity;
import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public interface IClipBinder {
    void startClipListener();
    void stopClipListener();
    void quickInsert(boolean useSystem);
    boolean isClipListenerStart();
    boolean isClipDataInNotes();
    IClipBinder setActivity(BaseActivity activity);
    String getClipData();
}
