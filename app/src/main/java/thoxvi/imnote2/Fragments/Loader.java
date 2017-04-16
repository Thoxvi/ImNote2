package thoxvi.imnote2.Fragments;

import android.view.View;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/2.
 */

public interface Loader {
    void loadEdit(int index, INoteBO note);
    void loadNew();
}
