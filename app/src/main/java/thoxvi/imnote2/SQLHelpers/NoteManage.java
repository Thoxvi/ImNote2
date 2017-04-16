package thoxvi.imnote2.SQLHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.BaseDatas.Note.Note;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class NoteManage implements DataBaseComonder {
    private DBHelper dbHelper;

    public NoteManage(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public INoteBO insert(INoteBO note) {
        String title = note.getTitle();
        String content = note.getContent();
        int status = Note.NOTE_STATUS_LIVE;
        long time = (new Date()).getTime();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(Note.TABLE_ITEM_TITLE, title);
        v.put(Note.TABLE_ITEM_CONTENT, content);
        v.put(Note.TABLE_ITEM_TIME, time);
        v.put(Note.TABLE_ITEM_STATUS, status);

        v.put(Note.TABLE_ITEM_INDEX, 0);

        long id = db.insert(Note.TABLE_NAME, null, v);
        db.close();

        return new Note(id, title, content, status, time);
    }

    @Override
    public void delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Note.TABLE_NAME, " " + Note.TABLE_ITEM_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
    }

    @Override
    public void update(INoteBO note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(Note.TABLE_ITEM_TITLE, note.getTitle());
        v.put(Note.TABLE_ITEM_CONTENT, note.getContent());
        v.put(Note.TABLE_ITEM_TIME, note.getTime());
        v.put(Note.TABLE_ITEM_STATUS, note.getStatus());

        v.put(Note.TABLE_ITEM_INDEX, 0);

        db.update(Note.TABLE_NAME, v, " " + Note.TABLE_ITEM_ID + "=? ", new String[]{String.valueOf(note.getID())});
        db.close();
    }

    @Override
    public INoteBO killNote(INoteBO note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Note.TABLE_ITEM_STATUS, Note.NOTE_STATUS_DEAD);
        db.update(Note.TABLE_NAME, v, " " + Note.TABLE_ITEM_ID + "=? ", new String[]{String.valueOf(note.getID())});
        db.close();
        ((INoteBiz) note).killNote();
        return note;
    }

    @Override
    public INoteBO reliveNote(INoteBO note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Note.TABLE_ITEM_STATUS, Note.NOTE_STATUS_LIVE);
        db.update(Note.TABLE_NAME, v, " " + Note.TABLE_ITEM_ID + "=? ", new String[]{String.valueOf(note.getID())});
        db.close();
        ((INoteBiz) note).reliveNote();
        return note;
    }

    @Override
    public List<INoteBO> getNotesAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " +
                Note.TABLE_ITEM_ID + "," +
                Note.TABLE_ITEM_TITLE + "," +
                Note.TABLE_ITEM_CONTENT + "," +
                Note.TABLE_ITEM_TIME + "," +
                Note.TABLE_ITEM_STATUS + " FROM " +
                Note.TABLE_NAME + " order by " +
                Note.TABLE_ITEM_INDEX + ";";

        ArrayList<INoteBO> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                notes.add(new Note(
                                cursor.getLong(cursor.getColumnIndex(Note.TABLE_ITEM_ID)),
                                cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_TITLE)),
                                cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_CONTENT)),
                                cursor.getInt(cursor.getColumnIndex(Note.TABLE_ITEM_STATUS)),
                                cursor.getLong(cursor.getColumnIndex(Note.TABLE_ITEM_TIME))
                        )
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public List<INoteBO> getNotesByStatus(int status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " +
                Note.TABLE_ITEM_ID + "," +
                Note.TABLE_ITEM_TITLE + "," +
                Note.TABLE_ITEM_CONTENT + "," +
                Note.TABLE_ITEM_TIME + " FROM " +
                Note.TABLE_NAME + " WHERE " +
                Note.TABLE_ITEM_STATUS + "=? order by " +
                Note.TABLE_ITEM_INDEX + ";";

        ArrayList<INoteBO> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(status)});
        if (cursor.moveToFirst()) {
            do {
                notes.add(new Note(
                                cursor.getLong(cursor.getColumnIndex(Note.TABLE_ITEM_ID)),
                                cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_TITLE)),
                                cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_CONTENT)),
                                status,
                                cursor.getLong(cursor.getColumnIndex(Note.TABLE_ITEM_TIME))
                        )
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public void sortList(List<INoteBO> notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues v = new ContentValues();
            try {
                for (int i = 0; i < notes.size(); i++) {
                    v.put(Note.TABLE_ITEM_INDEX, i);
                    db.update(Note.TABLE_NAME, v, " " + Note.TABLE_ITEM_ID + "=? ", new String[]{String.valueOf(notes.get(i).getID())});
                }
            } finally {
                v.clear();
            }
        } catch (Exception e) {
        } finally {
            db.close();
        }
    }

    @Override
    public INoteBO getByID(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " +
                Note.TABLE_ITEM_ID + "," +
                Note.TABLE_ITEM_TITLE + "," +
                Note.TABLE_ITEM_CONTENT + "," +
                Note.TABLE_ITEM_TIME + "," +
                Note.TABLE_ITEM_STATUS + " FROM " +
                Note.TABLE_NAME + " WHERE " +
                Note.TABLE_ITEM_ID + "=?";

        INoteBO note = new Note(id);

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            note.setTitle(cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndex(Note.TABLE_ITEM_CONTENT)));
            note.setStatus(cursor.getInt(cursor.getColumnIndex(Note.TABLE_ITEM_STATUS)));
            note.setTime(cursor.getLong(cursor.getColumnIndex(Note.TABLE_ITEM_TIME)));
        }
        cursor.close();
        db.close();
        return note;
    }

}
