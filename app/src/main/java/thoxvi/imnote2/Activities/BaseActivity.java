package thoxvi.imnote2.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import thoxvi.imnote2.R;

/**
 * Created by Thoxvi on 2017/3/12.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar问题
//        setTheme(getDiyTheme());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isEnable=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isEnable=false;
    }

    protected int getDiyTheme() {
        // TODO: 2017/3/12 读取主题并放回
        return R.style.BlueTheme;
    }



    private boolean isClilReminded = false;
    private boolean isEnable=false;

    public boolean isClilReminded() {
        return isClilReminded;
    }

    public void setClilReminded(boolean clilReminded) {
        isClilReminded = clilReminded;
    }

    public boolean isEnable() {
        return isEnable;
    }
}
