package thoxvi.imnote2.Services;

/**
 * Created by Thoxvi on 2017/3/9.
 */

public interface ICommandManagerBinder {
    ILiveBinder getLiveBinder();

    IDeadBinder getDeadBinder();

    IRemindBinder getRemindBinder();

    IClipBinder getClipBinder();
}
