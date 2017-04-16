package thoxvi.imnote2.Fragments;

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

import thoxvi.imnote2.Adapters.DeadAdpter;
import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.INoteBiz;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.ICommandManagerBinder;
import thoxvi.imnote2.Services.IDeadBinder;
import thoxvi.imnote2.Utils.UiUtil;

/**
 * Created by Thoxvi on 2017/3/6.
 */

public class DeadFragment extends Fragment implements BinderHaveTo<Fragment> {
    private IDeadBinder mDeadBinder;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fab;

    @Override
    public void setBinder(ICommandManagerBinder mManagerBinder) {
        mDeadBinder = mManagerBinder.getDeadBinder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_list, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        getActivity().setTitle(getString(R.string.dead));
        fab = (FloatingActionButton) getActivity().findViewById(R.id.list_fab);
        fab.setImageResource(R.mipmap.ico_deleteall);

        final RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.main_list_recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final RecyclerView.Adapter mAdapter;
        mRecyclerView.setAdapter(mAdapter = new DeadAdpter(getActivity(), mDeadBinder, fab));
        if (mDeadBinder != null) {
            mDeadBinder.setAdapter(mAdapter);
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final INoteBO note = mDeadBinder.getNotes().get(position);
                UiUtil.showAlertDialog(getActivity(), "提示", "确定删除吗？\n操作无法恢复！",  new Runnable() {
                    @Override
                    public void run() {
                        mDeadBinder.delete(position, note.getID());
                        showRemoveData((INoteBiz) note);
                    }
                }, () -> mAdapter.notifyItemChanged(position));

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            private void showRemoveData(INoteBiz note) {
                UiUtil.showSnackbar(fab, "已彻底删除\"" + note.getEasyInfo() + "\"", "好的", null);
            }
        }
        );
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.main_list_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorGreen, R.color.colorBlue, R.color.colorRed);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDeadBinder.getNewNotes(new Runnable() {
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

        fab.setOnClickListener(v1 -> {
            if (mDeadBinder.getNotes().size() == 0) {
                UiUtil.showSnackbar(fab, "回收站是空的！", "好的", null);
                return;
            }
            UiUtil.showAlertDialog(getActivity(), "提示", "是否清空回收站？\n此操作不可逆！\n(长按\"清空\"按钮可直接清除)", this::killAll, () -> {});
            mAdapter.notifyDataSetChanged();
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDeadBinder.getNotes().size() == 0) {
                    UiUtil.showSnackbar(fab, "回收站是空的！", "好的", null);
                    return true;
                }
                killAll();
                return true;
            }
        });
    }


    private void killAll() {
        UiUtil.showProgressDialog(getActivity(), "请稍候", "正在删除中…", () -> {
            mDeadBinder.deleteAll();
            UiUtil.showSnackbar(fab, "已清空回收站", "好的", null);
        });
    }


}
