package thoxvi.imnote2.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class UiUtil {

    public static void showAlertDialog(Context context, String title, String message, Runnable okTodo, Runnable cancelTodo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (!title.isEmpty()) dialog.setTitle(title);
        else dialog.setTitle("提醒");
        if (!message.isEmpty()) dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (okTodo != null) {
                            okTodo.run();
                        }
                    }
                }
        );

        //如果为Null则不显示，为空Runnable则显示但是没效果
        if (cancelTodo != null) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTodo.run();
                }
            });
        }

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelTodo != null) {
                    cancelTodo.run();
                }
            }
        });
        dialog.show();
    }

    public static void showSystemAlertDialog(Activity activity, String title, String message, Runnable okTodo, Runnable cancelTodo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        if (!title.isEmpty()) dialog.setTitle(title);
        else dialog.setTitle("提醒");
        if (!message.isEmpty()) dialog.setMessage(message);

        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (okTodo != null) {
                    okTodo.run();
                }
            }
        });
        if (cancelTodo != null) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTodo.run();
                }
            });
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelTodo != null) {
                    cancelTodo.run();
                }
            }
        });
        AlertDialog mDialog = dialog.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    public static void showProgressDialog(Context context, String title, String message, Runnable runTodo) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (runTodo != null) {
            runTodo.run();
        }
        progressDialog.dismiss();

    }

    public static void showSnackbar(View v, String message, final String actionText, final Runnable actionTodo) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                .setAction(actionText, view -> {
                    if (actionTodo != null) {
                        actionTodo.run();
                    }
                }).show();
    }

    //多处理
    public static class SelectStrAndRun {
        String mInfo;
        Runnable mTodo;

        public SelectStrAndRun(String info, Runnable todo) {
            //为空处理
            if (info != null && !info.isEmpty()) mInfo = info;
            else mInfo = "";

            if (todo != null) mTodo = todo;
            else mTodo = () -> {};
        }
    }

    public static void showSelectAlertDialog(Context context, String title, List<SelectStrAndRun> mSelectList) {
        List<String> mInfoList = new ArrayList<>();
        final List<Runnable> mTodoList = new ArrayList<>();

        for (SelectStrAndRun select : mSelectList) {
            mInfoList.add(select.mInfo);
            mTodoList.add(select.mTodo);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
        builder.setItems(mInfoList.toArray(new String[0]), (dialogInterface, i) -> mTodoList.get(i).run()).show();
    }

    public static void showSystemSelectAlertDialog(Activity activity, String title, List<SelectStrAndRun> mSelectList) {
        List<String> mInfoList = new ArrayList<>();
        final List<Runnable> mTodoList = new ArrayList<>();

        for (SelectStrAndRun select : mSelectList) {
            mInfoList.add(select.mInfo);
            mTodoList.add(select.mTodo);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
        builder.setItems(mInfoList.toArray(new String[0]), (dialogInterface, i) -> mTodoList.get(i).run());

        AlertDialog mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    public static void showPopupMenu(Context context, View v, List<SelectStrAndRun> mSelectList) {
        PopupMenu menu = new PopupMenu(context, v);
        final List<Runnable> mTodoList = new ArrayList<>();

        for (SelectStrAndRun select : mSelectList) {
            menu.getMenu().add(select.mInfo);
            mTodoList.add(select.mTodo);
        }
        menu.setOnMenuItemClickListener(item -> {
            mTodoList.get(item.getItemId()).run();
            return true;
        });
        menu.show();

    }
}
