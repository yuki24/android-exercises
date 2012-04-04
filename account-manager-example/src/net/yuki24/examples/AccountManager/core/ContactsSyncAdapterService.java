package net.yuki24.examples.AccountManager.core;

import java.util.ArrayList;
import java.util.HashMap;
import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactsSyncAdapterService extends Service {
	private static final String TAG = "ContactsSyncAdapterService";
	private static SyncAdapterImpl sSyncAdapter = null;
	private static ContentResolver mContentResolver = null;
	 
	public ContactsSyncAdapterService() {
		super();
	}
	 
	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		private Context mContext;
	 
		public SyncAdapterImpl(Context context) {
			super(context, true);
			mContext = context;
		}
	 
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			try {
				ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 
	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}
	 
	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null) sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}
	 
	private static void performSync(Context context, Account account, Bundle extras, String authority,
									ContentProviderClient provider, SyncResult syncResult) throws OperationCanceledException {
		mContentResolver = context.getContentResolver();
		Log.i(TAG, "performSync: " + account.toString());

		//This is where the magic will happen!
		HashMap<String, Long> localContacts = new HashMap<String, Long>();
		mContentResolver = context.getContentResolver();

		// Load the local example contacts
		Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon()
								.appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name)
								.appendQueryParameter(RawContacts.ACCOUNT_TYPE, account.type)
								.build();
		Cursor c1 = mContentResolver.query(rawContactUri, new String[]{BaseColumns._ID, RawContacts.SYNC1}, null, null, null);
		while (c1.moveToNext()) { localContacts.put(c1.getString(1), c1.getLong(0)); }

		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		try {
			// Here comes the actual update of contacts
			if (!localContacts.containsKey("example"))
				addContact(account, "Account Example", "example");
			else
				updateContactStatus(operationList, localContacts.get("example"));

			if(operationList.size() > 0) mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addContact(Account account, String name, String username) {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		// Create our RawContact
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
		builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
		builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
		builder.withValue(RawContacts.SYNC1, username);
		operationList.add(builder.build());

		// Create a Data record of common type 'StructuredName' for our RawContact
		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(StructuredName.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		builder.withValue(StructuredName.DISPLAY_NAME, name);
		operationList.add(builder.build());

		// Create a Data record of phone number
		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(StructuredName.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		builder.withValue(Phone.NUMBER, "819012345678");
		operationList.add(builder.build());

		// Create a Data record of custom type to display a link to the sample profile
		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(Data.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, "vnd.android.cursor.item/vnd.net.yuki24.examples.profile");
		builder.withValue(Data.DATA2, "Yuki24 example Profile");
		builder.withValue(Data.DATA3, "View profile");
		builder.withValue(Data.DATA4, username);
		operationList.add(builder.build());

		try {
			mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateContactStatus(ArrayList<ContentProviderOperation> operationList, long rawContactId) {
		Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId);
		Uri entityUri = Uri.withAppendedPath(rawContactUri, Entity.CONTENT_DIRECTORY);
		Cursor c = mContentResolver.query(entityUri, new String[]{RawContacts.SOURCE_ID, Entity.DATA_ID, Entity.MIMETYPE, Entity.DATA1}, null, null, null);

		try {
			while(c.moveToNext()) {
				if (!c.isNull(1)) continue;

				String mimeType = c.getString(2);
				if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
					builder.withSelection(BaseColumns._ID + " = ?", new String[]{c.getString(1)});
					builder.withValue(Phone.NUMBER, "819087654321");
					operationList.add(builder.build());
				} else if (mimeType.equals("vnd.android.cursor.item/vnd.net.yuki24.examples.profile")) {
					// Log.i(TAG, "update own profile.");
				}
			}
		} finally {
			c.close();
		}
	}
}