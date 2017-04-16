package thoxvi.imnote2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import thoxvi.imnote2.Fragments.NoteDialog;

public class Tmper extends BaseActivity {
    public static final  String TEXT_PLAIN="text/plain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.01f;
        lp.dimAmount=0.0f;
        getWindow().setAttributes(lp);


        Intent i = getIntent();
        String type = i.getType();
        String action = i.getAction();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (TEXT_PLAIN.equals(type)) {
                String s = i.getStringExtra(Intent.EXTRA_TEXT);
                if (!s.isEmpty()) {
                    NoteDialog mNoteDialog=new NoteDialog();
                    mNoteDialog.setCancelable(false);

                    Bundle b=new Bundle();
                    b.putString(TEXT_PLAIN,s);
                    mNoteDialog.setArguments(b);
                    mNoteDialog.show(getFragmentManager(),"");
                }
            }
        }
    }


}
