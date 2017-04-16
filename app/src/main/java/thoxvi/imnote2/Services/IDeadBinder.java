package thoxvi.imnote2.Services;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public interface IDeadBinder extends IDataServiceBinder {
    void deleteAll();
    void delete(int index, long id);
    void relive(int index);
}
