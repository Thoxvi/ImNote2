package thoxvi.imnote2.SQLHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import thoxvi.imnote2.BaseDatas.Note.Note;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ImNote.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STUDENT = "CREATE TABLE " +
                Note.TABLE_NAME + " (" +
                Note.TABLE_ITEM_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Note.TABLE_ITEM_TITLE + " TEXT," +
                Note.TABLE_ITEM_CONTENT + " TEXT," +
                Note.TABLE_ITEM_TIME + " INTEGER," +
                Note.TABLE_ITEM_INDEX + " INTEGER DEFAULT 0," +
                Note.TABLE_ITEM_STATUS + " INTEGER);";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //升级再用
    }
}
