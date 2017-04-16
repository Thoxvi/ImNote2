package thoxvi.imnote2.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import thoxvi.imnote2.R;
import thoxvi.imnote2.Utils.AppInfo;

public class About extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView)findViewById(R.id.about_xianyu)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        findViewById(R.id.about_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQQ();
            }
        });
        findViewById(R.id.about_outlook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendErroEmail();
            }
        });
        setTitle("关于");
        ((TextView)findViewById(R.id.about_version)).setText(getString(R.string.appVersion)+ AppInfo.APP_VERSION);
    }




    private void sendErroEmail() {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:Thoxvi@OutLook.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "Bug反馈");
        data.putExtra(Intent.EXTRA_TEXT,
                "--------系统信息--------\n" +
                        "手机品牌：" + Build.BRAND + "\n" +
                        "系统版本：" + Build.MODEL + "\n" +
                        "SDK版本：" + Build.VERSION.SDK + "\n" +
                        "Android版本：" + Build.VERSION.RELEASE + "\n"
        );
        startActivity(data);
    }
    private void sendQQ(){
        String url="mqqwpa://im/chat?chat_type=wpa&uin=1041972872";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

}
