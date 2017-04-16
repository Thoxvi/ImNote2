package thoxvi.imnote2.SQLHelpers;

import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/2.
 */

public interface DataBaseComonder {
    INoteBO insert(INoteBO note);

    void delete(long id);

    void update(INoteBO note);
    INoteBO killNote(INoteBO note);
    INoteBO reliveNote(INoteBO note);
    void sortList(List<INoteBO> notes);

    INoteBO getByID(long id);
    List<INoteBO> getNotesAll();
    List<INoteBO> getNotesByStatus(int status);
}
