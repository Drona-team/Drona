package androidx.browser.customtabs;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback;
import android.support.customtabs.IPostMessageService.Stub;

public class PostMessageService
  extends Service
{
  private IPostMessageService.Stub mBinder = new IPostMessageService.Stub()
  {
    public void onMessageChannelReady(ICustomTabsCallback paramAnonymousICustomTabsCallback, Bundle paramAnonymousBundle)
      throws RemoteException
    {
      paramAnonymousICustomTabsCallback.onMessageChannelReady(paramAnonymousBundle);
    }
    
    public void onPostMessage(ICustomTabsCallback paramAnonymousICustomTabsCallback, String paramAnonymousString, Bundle paramAnonymousBundle)
      throws RemoteException
    {
      paramAnonymousICustomTabsCallback.onPostMessage(paramAnonymousString, paramAnonymousBundle);
    }
  };
  
  public PostMessageService() {}
  
  public IBinder onBind(Intent paramIntent)
  {
    return mBinder;
  }
}
