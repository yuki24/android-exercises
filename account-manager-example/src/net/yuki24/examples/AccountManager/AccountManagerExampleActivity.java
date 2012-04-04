package net.yuki24.examples.AccountManager;

import java.util.ArrayList;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

public class AccountManagerExampleActivity extends AccountAuthenticatorActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ArrayList<String> accountsInfo = new ArrayList<String>();
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccounts();
		for (Account account : accounts) {
			String name = account.name;
			String type = account.type;
			int describeContents = account.describeContents();
			int hashCode = account.hashCode();

			accountsInfo.add("name = " + name +
							 "\ntype = " + type +
							 "\ndescribeContents = " + describeContents +
							 "\nhashCode = " + hashCode);
		}
		String[] result = new String[accountsInfo.size()];
		accountsInfo.toArray(result);

        String username = "hoge";
        String password = "fuga";
        String accountType = "net.yuki24.examples";

        // This is the magic that addes the account to the Android Account Manager
        final Account account = new Account(username, accountType);
        manager.addAccountExplicitly(account, password, null);

        // Now we tell our caller, could be the Andreid Account Manager or even our own application
        // that the process was successful
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
        this.setAccountAuthenticatorResult(intent.getExtras());
        this.setResult(RESULT_OK, intent);
        this.finish();
	}
}