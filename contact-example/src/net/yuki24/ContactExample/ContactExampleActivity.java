package net.yuki24.ContactExample;

import net.yuki24.ContactExample.core.ContactBuilder;
import net.yuki24.ContactExample.core.ContactWrapper;
import android.app.Activity;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;

public class ContactExampleActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    	// load all contacts
    	ContactWrapper.all(getContentResolver()); 

    	// add a contact
        ContactBuilder cb = new ContactBuilder();
        cb.assignAccount();
        cb.assignFullName("Taro", "Yamada");
        cb.assignEmail("foo@example.com");
        cb.assignPhoneNumber("819012345678");

        try {
			cb.apply(getContentResolver());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}