package thoxvi.imnote2.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import thoxvi.imnote2.Fragments.BinderHaveTo;
import thoxvi.imnote2.Fragments.DeadFragment;
import thoxvi.imnote2.Fragments.LiveFragment;
import thoxvi.imnote2.R;
import thoxvi.imnote2.Services.DataBaseService;
import thoxvi.imnote2.Services.IClipBinder;
import thoxvi.imnote2.Services.ICommandManagerBinder;
import thoxvi.imnote2.Services.IRemindBinder;
import thoxvi.imnote2.Utils.PicUtil;
import thoxvi.imnote2.Utils.UiUtil;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int OVERLAY_PERMISSION_REQ_CODE = -1;

    private IClipBinder mClipBinder;
    private IRemindBinder mRemindBinder;
    private ICommandManagerBinder mCommandManagerBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCommandManagerBinder = (ICommandManagerBinder) service;

            mClipBinder = mCommandManagerBinder.getClipBinder().setActivity(MainActivity.this);

            mRemindBinder = mCommandManagerBinder.getRemindBinder();

            replaceFragment(mLiveFragment);

            fromNotificationRemind();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private BinderHaveTo<Fragment> mLiveFragment = new LiveFragment();
    private BinderHaveTo<Fragment> mDeadFragment = new DeadFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initServiceConnection();
        initUI();
    }

    private void initServiceConnection() {
        Intent i = new Intent(this, DataBaseService.class);
        bindService(i, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_right, menu);

        ((ImageView) findViewById(R.id.main_header_timeimage)).setImageResource(PicUtil.getTimePic());
        findViewById(R.id.main_header_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/6 登录功能
                Toast.makeText(MainActivity.this, "功能尚未完成！", Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                // TODO: 2017/3/6 设置功能
                Toast.makeText(MainActivity.this, "功能尚未完成！", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.action_remindAll:
//                remindAll();
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_live:
                replaceFragment(mLiveFragment);
                break;
            case R.id.nav_dead:
                replaceFragment(mDeadFragment);
                break;
            case R.id.nav_achievement:
                // TODO: 2017/3/6 成就功能
                Toast.makeText(MainActivity.this, "功能尚未完成！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_remind:
                if (!mRemindBinder.areNotificationsEnabled()) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    startActivity(intent);
                    Toast.makeText(this, "请给Note通知权限！以便于显示提醒！", Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(MainActivity.this, "已提醒！", Toast.LENGTH_SHORT).show();
                remindAll();
                break;
            case R.id.nav_cliplistener:
                if(Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        Toast.makeText(this, "请给Note悬浮窗权限！以便于判断监听数据类型！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        break;
                    }
                }else{
                    Toast.makeText(this, "系统不支持此功能！", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (!mClipBinder.isClipListenerStart()) {
                    mClipBinder.startClipListener();
                    item.setTitle(this.getString(R.string.StopClipListener));
                    item.setIcon(R.mipmap.ico_uncliplistener);
                } else {
                    mClipBinder.stopClipListener();
                    item.setTitle(this.getString(R.string.StartClipListener));
                    item.setIcon(R.mipmap.ico_cliplistener);
                }
                break;
            case R.id.nav_about:
                startAboutActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mClipBinder.isClipListenerStart()) {
            if (isClilReminded()) {
                goLauncher();
                return false;
            }
            UiUtil.showAlertDialog(this, "提醒", "正在监听剪贴板！确定要关闭吗？\n如果想在后台监听的话\n请不要清理掉此程序！", new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    goLauncher();
                }
            });
            setClilReminded(true);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        View fab = findViewById(R.id.list_fab);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                UiUtil.showSnackbar(fab, "授权失败，无法监听剪贴板！", "好的", null);
            } else {
                UiUtil.showSnackbar(fab, "授权成功，请再次点击监听剪贴板！", "好的", null);
            }
        }
    }

    private void remindAll() {
        mRemindBinder.remindLiveNotes();
    }

    private void initUI() {
        setTitle(getString(R.string.live));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            private boolean isLoaded = false;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (isLoaded) return;
                //获得菜单项目实例，并修改标题
                isLoaded = true;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isLoaded = false;
            }
        });

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void startAboutActivity() {
        startActivity(new Intent(this, About.class));
    }

    private void replaceFragment(final BinderHaveTo<? extends Fragment> fragment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t = fm.beginTransaction();
                fragment.setBinder(mCommandManagerBinder);
                t.replace(R.id.main_fragment, (Fragment) fragment);
                t.commit();
            }
        }).start();

    }

    private void goLauncher() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
        Toast.makeText(MainActivity.this, "正在后台监听剪贴板！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        fromNotificationRemind();
    }

    private void fromNotificationRemind() {
        long id = getIntent().getLongExtra(IRemindBinder.FROM_REMIND_NOTIFICATION, -1);
        getIntent().putExtra(IRemindBinder.FROM_REMIND_NOTIFICATION, -1);
        if (id >= 0) {
            if (mRemindBinder != null) {
                mRemindBinder.remindNote(this, id);
            }
        }
    }
}
