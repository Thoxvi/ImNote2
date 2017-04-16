package thoxvi.imnote2.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.IDeadBinder;
import thoxvi.imnote2.Utils.StringUtil;
import thoxvi.imnote2.Utils.UiUtil;

/**
 * Created by Thoxvi on 2017/3/2.
 */

public class DeadAdpter extends RecyclerView.Adapter<DeadAdpter.MyViewHolder> {
    private Context context;
    private IDeadBinder mDeadBinder;
    private FloatingActionButton fab;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DeadAdpter(Context context, IDeadBinder mDeadBinder,FloatingActionButton fab) {
        this.context=context;
        this.mDeadBinder = mDeadBinder;
        this.fab = fab;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.dead_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final INoteBO note = mDeadBinder.getNotes().get(position);
        String content = StringUtil.sliteString(note.getContent(), 32);

        holder.title.setText(note.getTitle());
        holder.content.setText(content);
        holder.time.setText(mSimpleDateFormat.format(note.getTime()));
        holder.relive.setOnClickListener(v -> {
            mDeadBinder.relive(position);
            UiUtil.showSnackbar(fab,"已恢复\""+((INoteBiz)note).getEasyInfo()+"\"","好的",null);
        });
    }

    @Override
    public int getItemCount() {
        return mDeadBinder.getNotes().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView title;
        TextView time;
        Button relive;
        View v;

        MyViewHolder(View itemView) {
            super(itemView);
            v=itemView;
            title = (TextView) itemView.findViewById(R.id.dead_item_title);
            content = (TextView) itemView.findViewById(R.id.dead_item_content);
            time = (TextView) itemView.findViewById(R.id.dead_item_time);
            relive = (Button) itemView.findViewById(R.id.dead_item_relive);
        }
    }

}
