package thoxvi.imnote2.Services;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/6.
 */

interface IDataServiceBinder {
    INoteBO insert(INoteBO note);

    List<INoteBO> getNotes();
    List<INoteBO> getNewNotes();
    void getNewNotes(Runnable doneTodo);

    void setAdapter(RecyclerView.Adapter adapter);
}
