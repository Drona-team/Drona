package androidx.browser.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback;
import android.support.customtabs.ICustomTabsService;
import android.widget.RemoteViews;
import java.util.List;

public final class CustomTabsSession
{
  private static final String TAG = "CustomTabsSession";
  private final ICustomTabsCallback mCallback;
  private final ComponentName mComponentName;
  private final Object mLock = new Object();
  private final ICustomTabsService mService;
  
  CustomTabsSession(ICustomTabsService paramICustomTabsService, ICustomTabsCallback paramICustomTabsCallback, ComponentName paramComponentName)
  {
    mService = paramICustomTabsService;
    mCallback = paramICustomTabsCallback;
    mComponentName = paramComponentName;
  }
  
  public static CustomTabsSession createMockSessionForTesting(ComponentName paramComponentName)
  {
    return new CustomTabsSession(null, new CustomTabsSessionToken.MockCallback(), paramComponentName);
  }
  
  IBinder getBinder()
  {
    return mCallback.asBinder();
  }
  
  ComponentName getComponentName()
  {
    return mComponentName;
  }
  
  public boolean mayLaunchUrl(Uri paramUri, Bundle paramBundle, List paramList)
  {
    ICustomTabsService localICustomTabsService = mService;
    ICustomTabsCallback localICustomTabsCallback = mCallback;
    try
    {
      boolean bool = localICustomTabsService.mayLaunchUrl(localICustomTabsCallback, paramUri, paramBundle, paramList);
      return bool;
    }
    catch (RemoteException paramUri)
    {
      for (;;) {}
    }
    return false;
  }
  
  public int postMessage(String paramString, Bundle paramBundle)
  {
    Object localObject = mLock;
    ICustomTabsService localICustomTabsService = mService;
    ICustomTabsCallback localICustomTabsCallback = mCallback;
    try
    {
      int i = localICustomTabsService.postMessage(localICustomTabsCallback, paramString, paramBundle);
      return i;
    }
    catch (Throwable paramString)
    {
      break label48;
      return -2;
      throw paramString;
    }
    catch (RemoteException paramString)
    {
      label48:
      for (;;) {}
    }
  }
  
  public boolean requestPostMessageChannel(Uri paramUri)
  {
    ICustomTabsService localICustomTabsService = mService;
    ICustomTabsCallback localICustomTabsCallback = mCallback;
    try
    {
      boolean bool = localICustomTabsService.requestPostMessageChannel(localICustomTabsCallback, paramUri);
      return bool;
    }
    catch (RemoteException paramUri)
    {
      for (;;) {}
    }
    return false;
  }
  
  public boolean setActionButton(Bitmap paramBitmap, String paramString)
  {
    Object localObject = new Bundle();
    ((Bundle)localObject).putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    ((BaseBundle)localObject).putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    paramBitmap = new Bundle();
    paramBitmap.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", (Bundle)localObject);
    paramString = mService;
    localObject = mCallback;
    try
    {
      boolean bool = paramString.updateVisuals((ICustomTabsCallback)localObject, paramBitmap);
      return bool;
    }
    catch (RemoteException paramBitmap)
    {
      for (;;) {}
    }
    return false;
  }
  
  public boolean setSecondaryToolbarViews(RemoteViews paramRemoteViews, int[] paramArrayOfInt, PendingIntent paramPendingIntent)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", paramRemoteViews);
    localBundle.putIntArray("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", paramArrayOfInt);
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", paramPendingIntent);
    paramRemoteViews = mService;
    paramArrayOfInt = mCallback;
    try
    {
      boolean bool = paramRemoteViews.updateVisuals(paramArrayOfInt, localBundle);
      return bool;
    }
    catch (RemoteException paramRemoteViews)
    {
      for (;;) {}
    }
    return false;
  }
  
  public boolean setToolbarItem(int paramInt, Bitmap paramBitmap, String paramString)
  {
    Object localObject = new Bundle();
    ((BaseBundle)localObject).putInt("android.support.customtabs.customaction.ID", paramInt);
    ((Bundle)localObject).putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    ((BaseBundle)localObject).putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    paramBitmap = new Bundle();
    paramBitmap.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", (Bundle)localObject);
    paramString = mService;
    localObject = mCallback;
    try
    {
      boolean bool = paramString.updateVisuals((ICustomTabsCallback)localObject, paramBitmap);
      return bool;
    }
    catch (RemoteException paramBitmap)
    {
      for (;;) {}
    }
    return false;
  }
  
  public boolean validateRelationship(int paramInt, Uri paramUri, Bundle paramBundle)
  {
    ICustomTabsService localICustomTabsService;
    ICustomTabsCallback localICustomTabsCallback;
    if (paramInt >= 1)
    {
      if (paramInt > 2) {
        return false;
      }
      localICustomTabsService = mService;
      localICustomTabsCallback = mCallback;
    }
    try
    {
      boolean bool = localICustomTabsService.validateRelationship(localICustomTabsCallback, paramInt, paramUri, paramBundle);
      return bool;
    }
    catch (RemoteException paramUri) {}
    return false;
    return false;
  }
}
