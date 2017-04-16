package thoxvi.imnote2.Fragments;

import thoxvi.imnote2.Services.ICommandManagerBinder;

/**
 * Created by Thoxvi on 2017/3/9.
 */

public interface BinderHaveTo<T> {
    void setBinder(ICommandManagerBinder mManagerBinder);
}
