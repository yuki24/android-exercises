package net.yuki24.c2dm;

import net.yuki24.c2dm.util.C2DMessaging;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class C2dmActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void registerAccount(View v) {
        EditText acct = (EditText) findViewById(R.id.account);
        C2DMessaging.register(this, acct.getText().toString());
    }

    public void unregisterAccount(View v) {
        C2DMessaging.unregister(this);
    }
}