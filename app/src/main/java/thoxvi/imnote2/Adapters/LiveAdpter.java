package thoxvi.imnote2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.Fragments.Loader;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.ILiveBinder;
import thoxvi.imnote2.Services.IRemindBinder;
import thoxvi.imnote2.Utils.StringUtil;
import thoxvi.imnote2.Utils.UiUtil;

/**
 * Created by Thoxvi on 2017/3/2.
 */

public class LiveAdpter extends RecyclerView.Adapter<LiveAdpter.MyViewHolder> {
    private Context context;
    private IRemindBinder mRemindBinder;
    private ILiveBinder mLiveBinder;
    private Loader mLoader;
    private FloatingActionButton fab;

    private boolean isRun = false;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LiveAdpter(Context context, Loader loader, ILiveBinder mLiveBinder, IRemindBinder remindBinder, FloatingActionButton fab) {
        this.context = context;
        this.mLoader = loader;
        this.mLiveBinder = mLiveBinder;
        this.mRemindBinder = remindBinder;
        this.fab = fab;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.live_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final INoteBO note = mLiveBinder.getNotes().get(position);
        String content = StringUtil.sliteString(note.getContent(), 250);

        holder.title.setText(note.getTitle());
        holder.content.setText(content);
        holder.time.setText(mSimpleDateFormat.format(note.getTime()));
        holder.remind.setOnClickListener(v -> {
            if (!mRemindBinder.areNotificationsEnabled()) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
                context.startActivity(intent);
                Toast.makeText(context, "请给Note通知权限！以便于显示提醒！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isRun) return;
            isRun = true;
            new Thread(() -> {
                mRemindBinder.remindNote(note);
                isRun = false;
                UiUtil.showSnackbar(fab, "已提醒\"" + ((INoteBiz) note).getEasyInfo() + "\"", "好的", null);
            }).start();
        });

        holder.v.setOnClickListener(v -> mLoader.loadEdit(position, note));

//        holder.top.setOnClickListener(v -> {
//            mLiveBinder.moveTop(position);
//            UiUtil.showSnackbar(fab, "已将\"" + ((INoteBiz) note).getEasyInfo() + "\"置顶", "好的", null);
//        });
        holder.v.setOnLongClickListener(v -> {
            List<UiUtil.SelectStrAndRun> selects = new ArrayList<>();
            selects.add(new UiUtil.SelectStrAndRun("分享", () -> {
                List<UiUtil.SelectStrAndRun> copySelects = new ArrayList<>();
                copySelects.add(new UiUtil.SelectStrAndRun("可读式分享", () -> {
                    String text = ((INoteBiz) note).getCompleteInfo();
                    shareString(text);
                }));
                copySelects.add(new UiUtil.SelectStrAndRun("可读转Base64分享", () -> {
                    String text = StringUtil.stringToBase64(((INoteBiz) note).getCompleteInfo());
                    shareString(text);
                }));
                copySelects.add(new UiUtil.SelectStrAndRun("对象到Base64分享", () -> {
                    String text = StringUtil.objToJson(note);
                    text = StringUtil.stringToBase64(text);
                    shareString(text);
                }));
                UiUtil.showSelectAlertDialog(context, "分享", copySelects);
            }));
            selects.add(new UiUtil.SelectStrAndRun("复制", () -> {
                List<UiUtil.SelectStrAndRun> copySelects = new ArrayList<>();
                copySelects.add(new UiUtil.SelectStrAndRun("可读式复制", () -> {
                    String text = ((INoteBiz) note).getCompleteInfo();
                    StringUtil.setClipString(context, text);
                    UiUtil.showSnackbar(fab, "已复制\"" + StringUtil.sliteString(text, 10) + "\"", "好的", null);
                }));
                copySelects.add(new UiUtil.SelectStrAndRun("可读转Base64", () -> {
                    String text = StringUtil.stringToBase64(((INoteBiz) note).getCompleteInfo());
                    StringUtil.setClipString(context, text);
                    UiUtil.showSnackbar(fab, "已复制\"" + StringUtil.sliteString(text, 10) + "\"", "好的", null);
                }));
                copySelects.add(new UiUtil.SelectStrAndRun("对象到Base64", () -> {
                    String text = StringUtil.objToJson(note);
                    text = StringUtil.stringToBase64(text);
                    StringUtil.setClipString(context, text);
                    UiUtil.showSnackbar(fab, "已复制\"" + StringUtil.sliteString(text, 10) + "\"", "好的", null);
                }));
                UiUtil.showSelectAlertDialog(context, "复制", copySelects);
            }));
            selects.add(new UiUtil.SelectStrAndRun("置顶", () -> {
                mLiveBinder.moveTop(position);
                UiUtil.showSnackbar(fab, "已将\"" + ((INoteBiz) note).getEasyInfo() + "\"置顶", "好的", null);
            }));
            UiUtil.showSelectAlertDialog(context, "更多", selects);


            return true;
        });

//        holder.more.setOnClickListener(v -> {
//            List<UiUtil.SelectStrAndRun> mSelects = new ArrayList<>();
//            mSelects.add(new UiUtil.SelectStrAndRun("置顶", () -> {
//                mLiveBinder.moveTop(position);
//                UiUtil.showSnackbar(fab, "已将\"" + ((INoteBiz) note).getEasyInfo() + "\"置顶", "好的", null);
//            }));
//            UiUtil.showPopupMenu(context, v, mSelects);
//        });

    }

    @Override
    public int getItemCount() {
        return mLiveBinder.getNotes().size();
    }

    private void shareString(String s) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, s);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView title;
        TextView time;
        Button remind;
        Button top;
//        ImageButton more;

        View v;

        MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.live_item_title);
            content = (TextView) itemView.findViewById(R.id.live_item_content);
            time = (TextView) itemView.findViewById(R.id.live_item_time);
            remind = (Button) itemView.findViewById(R.id.live_item_remind);
            top = (Button) itemView.findViewById(R.id.live_item_top);
            top.setVisibility(View.GONE);
//            more = (ImageButton) itemView.findViewById(R.id.live_item_more);

            v = itemView;
        }
    }

}
