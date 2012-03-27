package net.yuki24.examples.AccountManager.core;

import net.yuki24.examples.AccountManager.authenticator.ExampleAccountAuthenticator;
import android.app.Service;  
import android.content.Intent;
import android.os.IBinder;

public class ExampleAuthenticationService extends Service {
  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return new ExampleAccountAuthenticator(this).getIBinder();
  }
}