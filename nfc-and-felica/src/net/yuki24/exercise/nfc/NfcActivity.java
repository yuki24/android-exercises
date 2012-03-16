package net.yuki24.exercise.nfc;

import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.felica.lib.FeliCaLib.IDm;
import com.felicanetworks.mfc.PushIntentSegment;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

public class NfcActivity extends Activity {
	@Override 
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = getIntent();
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(tag != null) {
			byte[] idm = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

			try {
				Uri uri = Uri.parse("market://details?id=com.main.typograffit");
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				PushIntentSegment segment = new PushIntentSegment(i);
				PushCommand pushCommand = new PushCommand(new IDm(idm), segment);
				FeliCaLib.execute(tag, pushCommand);
			} catch (FeliCaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}