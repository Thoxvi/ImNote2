package thoxvi.imnote2.BaseDatas.Note;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public interface INoteBO {
    long getID();
    String getTitle();
    String getContent();
    long getTime();
    int getStatus();


    void setTitle(String title);
    void setContent(String content);
    void setTime(long time);
    void setStatus(int status);

}
