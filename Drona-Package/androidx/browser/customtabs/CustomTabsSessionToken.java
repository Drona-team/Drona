package androidx.browser.customtabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback;
import android.support.customtabs.ICustomTabsCallback.Stub;
import android.util.Log;
import androidx.core.package_4.BundleCompat;

public class CustomTabsSessionToken
{
  private static final String TAG = "CustomTabsSessionToken";
  private final CustomTabsCallback mCallback;
  final ICustomTabsCallback mCallbackBinder;
  
  CustomTabsSessionToken(ICustomTabsCallback paramICustomTabsCallback)
  {
    mCallbackBinder = paramICustomTabsCallback;
    mCallback = new CustomTabsCallback()
    {
      public void extraCallback(String paramAnonymousString, Bundle paramAnonymousBundle)
      {
        ICustomTabsCallback localICustomTabsCallback = mCallbackBinder;
        try
        {
          localICustomTabsCallback.extraCallback(paramAnonymousString, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          for (;;) {}
        }
        Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
      }
      
      public void onMessageChannelReady(Bundle paramAnonymousBundle)
      {
        ICustomTabsCallback localICustomTabsCallback = mCallbackBinder;
        try
        {
          localICustomTabsCallback.onMessageChannelReady(paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousBundle)
        {
          for (;;) {}
        }
        Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
      }
      
      public void onNavigationEvent(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        ICustomTabsCallback localICustomTabsCallback = mCallbackBinder;
        try
        {
          localICustomTabsCallback.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousBundle)
        {
          for (;;) {}
        }
        Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
      }
      
      public void onPostMessage(String paramAnonymousString, Bundle paramAnonymousBundle)
      {
        ICustomTabsCallback localICustomTabsCallback = mCallbackBinder;
        try
        {
          localICustomTabsCallback.onPostMessage(paramAnonymousString, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          for (;;) {}
        }
        Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
      }
      
      public void onRelationshipValidationResult(int paramAnonymousInt, Uri paramAnonymousUri, boolean paramAnonymousBoolean, Bundle paramAnonymousBundle)
      {
        ICustomTabsCallback localICustomTabsCallback = mCallbackBinder;
        try
        {
          localICustomTabsCallback.onRelationshipValidationResult(paramAnonymousInt, paramAnonymousUri, paramAnonymousBoolean, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousUri)
        {
          for (;;) {}
        }
        Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
      }
    };
  }
  
  public static CustomTabsSessionToken createMockSessionTokenForTesting()
  {
    return new CustomTabsSessionToken(new MockCallback());
  }
  
  public static CustomTabsSessionToken getSessionTokenFromIntent(Intent paramIntent)
  {
    paramIntent = BundleCompat.getBinder(paramIntent.getExtras(), "android.support.customtabs.extra.SESSION");
    if (paramIntent == null) {
      return null;
    }
    return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(paramIntent));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CustomTabsSessionToken)) {
      return false;
    }
    return ((CustomTabsSessionToken)paramObject).getCallbackBinder().equals(mCallbackBinder.asBinder());
  }
  
  public CustomTabsCallback getCallback()
  {
    return mCallback;
  }
  
  IBinder getCallbackBinder()
  {
    return mCallbackBinder.asBinder();
  }
  
  public int hashCode()
  {
    return getCallbackBinder().hashCode();
  }
  
  public boolean isAssociatedWith(CustomTabsSession paramCustomTabsSession)
  {
    return paramCustomTabsSession.getBinder().equals(mCallbackBinder);
  }
  
  static class MockCallback
    extends ICustomTabsCallback.Stub
  {
    MockCallback() {}
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public void extraCallback(String paramString, Bundle paramBundle) {}
    
    public void onMessageChannelReady(Bundle paramBundle) {}
    
    public void onNavigationEvent(int paramInt, Bundle paramBundle) {}
    
    public void onPostMessage(String paramString, Bundle paramBundle) {}
    
    public void onRelationshipValidationResult(int paramInt, Uri paramUri, boolean paramBoolean, Bundle paramBundle) {}
  }
}
