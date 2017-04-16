package thoxvi.imnote2.Fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import thoxvi.imnote2.Activities.EditActivity;
import thoxvi.imnote2.Adapters.LiveAdpter;
import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.BaseDatas.Note.Note;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.IClipBinder;
import thoxvi.imnote2.Services.ICommandManagerBinder;
import thoxvi.imnote2.Services.IDeadBinder;
import thoxvi.imnote2.Services.ILiveBinder;
import thoxvi.imnote2.Services.IRemindBinder;
import thoxvi.imnote2.Utils.StringUtil;
import thoxvi.imnote2.Utils.UiUtil;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public class LiveFragment extends Fragment implements Loader, BinderHaveTo<Fragment> {
    private ILiveBinder mLiveBinder;
    private IDeadBinder mDeadBinder;
    private IRemindBinder mRemindBinder;
    private IClipBinder mClipBinder;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fab;

    @Override
    public void setBinder(ICommandManagerBinder mManagerBinder) {
        mRemindBinder = mManagerBinder.getRemindBinder();
        mLiveBinder = mManagerBinder.getLiveBinder();
        mDeadBinder = mManagerBinder.getDeadBinder();
        mClipBinder = mManagerBinder.getClipBinder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_list, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        getActivity().setTitle(getString(R.string.live));

        fab = (FloatingActionButton) getActivity().findViewById(R.id.list_fab);
        fab.setImageResource(R.mipmap.ico_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNew();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClipBinder.quickInsert(false);
                return true;
            }
        });

        final RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.main_list_recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final RecyclerView.Adapter mAdapter;
        mRecyclerView.setAdapter(mAdapter = new LiveAdpter(getActivity(), this, mLiveBinder, mRemindBinder, fab));
        if (mLiveBinder != null) {
            mLiveBinder.setAdapter(mAdapter);
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            private INoteBO tmpnote = null;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
//                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
//                if (fromPosition < toPosition) {
//                    //分别把中间所有的item的位置重新交换
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(mLiveBinder.getNotes(), i, i + 1);
//                    }
//                } else {
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(mLiveBinder.getNotes(), i, i - 1);
//                    }
//                }
//                mAdapter.notifyItemMoved(fromPosition, toPosition);
//                //返回true表示执行拖动
//                return true;
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                INoteBO note = mLiveBinder.getNotes().get(position);
                UiUtil.showSnackbar(fab, "\"" + ((INoteBiz)note).getEasyInfo() + "\"已放入回收站", "好的",null);
//                showRemoveData((INoteBiz) note);
//                tmpnote = note;
                mLiveBinder.delete(position);
                mRemindBinder.cancelRemind(note.getID());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            private void showRemoveData(INoteBiz note) {
                UiUtil.showSnackbar(fab, "\"" + note.getEasyInfo() + "\"已放入回收站", "撤销", () -> {
//                    if(tmpnote==null)return;
                    mDeadBinder.relive(0);
//                    tmpnote = null;
                });
            }
        }
        );
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.main_list_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorGreen, R.color.colorBlue, R.color.colorRed);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLiveBinder.getNewNotes(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public void loadEdit(int index, INoteBO note) {
        Intent i = new Intent(getActivity(), EditActivity.class);
        i.putExtra(Note.CLASS_ID, note.getID());
        i.putExtra(Note.CLASS_INDEX, index);
        i.putExtra(Note.CLASS_TITLE, note.getTitle());
        i.putExtra(Note.CLASS_CONTENT, note.getContent());
        startActivity(i);
//        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(getActivity(), v, "TRANSITION_ACTIVITY_EDIT").toBundle());
    }

    @Override
    public void loadNew() {
        Intent i = new Intent(getActivity(), EditActivity.class);
        startActivity(i);
//        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(getActivity(), v, "TRANSITION_ACTIVITY_EDIT").toBundle());
    }

}
