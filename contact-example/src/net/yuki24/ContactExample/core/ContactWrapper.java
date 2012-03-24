package net.yuki24.ContactExample.core;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactWrapper {

	public static void all(ContentResolver cResolver) {
        Cursor cursor = cResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        boolean hoge = cursor.moveToFirst();
        if (hoge) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                switch (type) {
                    case Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        break;
                    case Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                    }
            }
            phones.close();

            Cursor emails = cResolver.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null);
            while (emails.moveToNext()) {
                String email = emails.getString(emails.getColumnIndex(Email.DATA));
                int type = emails.getInt(emails.getColumnIndex(Phone.TYPE));
                switch (type) {
                    case Email.TYPE_HOME:
                        // do something with the Home email here...
                        break;
                    case Email.TYPE_WORK:
                        // do something with the Work email here...
                        break;
                }
            }
            emails.close();
        }
        cursor.close();
	}
}
