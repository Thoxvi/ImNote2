package thoxvi.imnote2.BaseDatas.Note;

import java.util.Date;

import thoxvi.imnote2.Utils.StringUtil;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class Note implements INoteBiz, INoteBO {
    public static final int NOTE_STATUS_LIVE = 0;
    public static final int NOTE_STATUS_DEAD = 1;

    public static final String CLASS_NAME = Note.class.getName();
    public static final String CLASS_INDEX = "Index";
    public static final String CLASS_ID = "ID";
    public static final String CLASS_TITLE = "Title";
    public static final String CLASS_CONTENT = "Content";

    public static final String TABLE_NAME = "Note";
    public static final String TABLE_ITEM_ID = "ID";
    public static final String TABLE_ITEM_STATUS = "Status";
    public static final String TABLE_ITEM_TITLE = "Title";
    public static final String TABLE_ITEM_CONTENT = "Content";
    public static final String TABLE_ITEM_TIME = "Time";

    public static final String TABLE_ITEM_INDEX = "mIndex";


    private long mID;
    private int mStatus;
    private String mTitle = "";
    private String mContent = "";
    private long mTime = 0;

    public Note(long id, String title, String content, int status, long time) {
        mID = id;
        mTitle = title;
        mContent = content;
        mTime = time;
        mStatus = status;
    }


    public Note(long id, String title, String content) {
        mID = id;
        mTitle = title;
        mContent = content;
        mTime = (new Date()).getTime();
        mStatus = NOTE_STATUS_LIVE;
    }

    public Note(long id) {
        mID = id;
        mTitle = "";
        mContent = "";
        mTime = (new Date()).getTime();
        mStatus = NOTE_STATUS_LIVE;
    }

    @Override
    public long getID() {
        return mID;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getContent() {
        return mContent;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public void killNote() {
        mStatus = NOTE_STATUS_DEAD;
    }

    @Override
    public void reliveNote() {
        mStatus = NOTE_STATUS_LIVE;
    }

    @Override
    public String getEasyInfo() {
        String info;
        if (mTitle.isEmpty()) {
            info = mContent;
        } else {
            info = mTitle;
        }
        return StringUtil.sliteString(info, 8);
    }

    @Override
    public String getCompleteInfo() {
        if (!mTitle.isEmpty() && mContent.isEmpty()) return mTitle;
        if (!mTitle.isEmpty() && !mContent.isEmpty()) return mTitle + "\n" + mContent;
        if (mTitle.isEmpty()) return mContent;
        return "";
    }
}
