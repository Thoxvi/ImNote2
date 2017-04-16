package thoxvi.imnote2.Services;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public interface ILiveBinder extends IDataServiceBinder{
    void update(int index, INoteBO note);
    void delete(int index);
    void relive(INoteBO note);

    void moveTop(int index);

}
