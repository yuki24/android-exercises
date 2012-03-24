package net.yuki24.ContactExample.core;

import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

public class ContactBuilder {
	private ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
	private int rawContactInsertIndex = ops.size();

	public ContentProviderResult[] apply(ContentResolver resolver) throws RemoteException, OperationApplicationException {
		return resolver.applyBatch(ContactsContract.AUTHORITY, ops);
	}

	public void assignAccount() {
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
			.withValue(RawContacts.ACCOUNT_TYPE, null)
			.withValue(RawContacts.ACCOUNT_NAME, null)
			.build());
	}

	public void assignAccount(String accountType, String accountName) {
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
			.withValue(RawContacts.ACCOUNT_TYPE, accountType)
			.withValue(RawContacts.ACCOUNT_NAME, accountName)
			.build());
	}

	public void assignPhoneNumber(String phoneNumber) {
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
			.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
			.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
			.withValue(Phone.NUMBER, phoneNumber)
			.build());
	}

	public void assignEmail(String email) {
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
			.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
			.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
			.withValue(Email.DATA, email)
			.build());
	}

	public void assignDisplayName(String displayName) {
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
			.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
			.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
			.withValue(StructuredName.DISPLAY_NAME, displayName)
			.build());
	}

	public void assignFullName(String firstName, String lastName) {
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
			.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
			.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
			.withValue(StructuredName.DISPLAY_NAME, buildFullname(firstName, lastName))
			.withValue(StructuredName.GIVEN_NAME, firstName)
			.withValue(StructuredName.FAMILY_NAME, lastName)
			.build());
	}

	public void assignFullName(String firstName, String middleName, String lastName) {
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
			.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
			.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
			.withValue(StructuredName.DISPLAY_NAME, buildFullname(firstName, middleName, lastName))
			.withValue(StructuredName.GIVEN_NAME, firstName)
			.withValue(StructuredName.MIDDLE_NAME, middleName)
			.withValue(StructuredName.FAMILY_NAME, lastName)
			.build());
	}

	private String buildFullname(String firstName, String lastName) {
		return (new StringBuilder()).append(firstName).append(" ").append(lastName).toString();
	}

	private String buildFullname(String firstName, String middleName, String lastName) {
		return (new StringBuilder()).append(firstName).append(" ").append(middleName).append(" ").append(lastName).toString();
	}
}
