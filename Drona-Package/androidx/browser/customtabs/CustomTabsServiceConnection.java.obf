package androidx.browser.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.customtabs.ICustomTabsService;
import android.support.customtabs.ICustomTabsService.Stub;

public abstract class CustomTabsServiceConnection
  implements ServiceConnection
{
  public CustomTabsServiceConnection() {}
  
  public abstract void onCustomTabsServiceConnected(ComponentName paramComponentName, CustomTabsClient paramCustomTabsClient);
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    onCustomTabsServiceConnected(paramComponentName, new CustomTabsClient(ICustomTabsService.Stub.asInterface(paramIBinder), paramComponentName) {});
  }
}
