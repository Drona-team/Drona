package androidx.media;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.util.ObjectsCompat;

class MediaSessionManagerImplBase
  implements MediaSessionManager.MediaSessionManagerImpl
{
  private static final boolean DEBUG = MediaSessionManager.DEBUG;
  private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
  private static final String PAGE_KEY = "MediaSessionManager";
  private static final String PERMISSION_MEDIA_CONTENT_CONTROL = "android.permission.MEDIA_CONTENT_CONTROL";
  private static final String PERMISSION_STATUS_BAR_SERVICE = "android.permission.STATUS_BAR_SERVICE";
  ContentResolver mContentResolver;
  Context mContext;
  
  MediaSessionManagerImplBase(Context paramContext)
  {
    mContext = paramContext;
    mContentResolver = mContext.getContentResolver();
  }
  
  private boolean isPermissionGranted(MediaSessionManager.RemoteUserInfoImpl paramRemoteUserInfoImpl, String paramString)
  {
    if (paramRemoteUserInfoImpl.getPid() < 0)
    {
      if (mContext.getPackageManager().checkPermission(paramString, paramRemoteUserInfoImpl.getPackageName()) == 0) {
        return true;
      }
    }
    else if (mContext.checkPermission(paramString, paramRemoteUserInfoImpl.getPid(), paramRemoteUserInfoImpl.getUid()) == 0) {
      return true;
    }
    return false;
  }
  
  public Context getContext()
  {
    return mContext;
  }
  
  boolean isEnabledNotificationListener(MediaSessionManager.RemoteUserInfoImpl paramRemoteUserInfoImpl)
  {
    Object localObject = Settings.Secure.getString(mContentResolver, "enabled_notification_listeners");
    if (localObject != null)
    {
      localObject = ((String)localObject).split(":");
      int i = 0;
      while (i < localObject.length)
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(localObject[i]);
        if ((localComponentName != null) && (localComponentName.getPackageName().equals(paramRemoteUserInfoImpl.getPackageName()))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public boolean isTrustedForMediaControl(MediaSessionManager.RemoteUserInfoImpl paramRemoteUserInfoImpl)
  {
    Object localObject = mContext;
    try
    {
      localObject = ((Context)localObject).getPackageManager().getApplicationInfo(paramRemoteUserInfoImpl.getPackageName(), 0);
      if (uid != paramRemoteUserInfoImpl.getUid())
      {
        if (!DEBUG) {
          break label192;
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Package name ");
        ((StringBuilder)localObject).append(paramRemoteUserInfoImpl.getPackageName());
        ((StringBuilder)localObject).append(" doesn't match with the uid ");
        ((StringBuilder)localObject).append(paramRemoteUserInfoImpl.getUid());
        Log.d("MediaSessionManager", ((StringBuilder)localObject).toString());
        return false;
      }
      if ((!isPermissionGranted(paramRemoteUserInfoImpl, "android.permission.STATUS_BAR_SERVICE")) && (!isPermissionGranted(paramRemoteUserInfoImpl, "android.permission.MEDIA_CONTENT_CONTROL")) && (paramRemoteUserInfoImpl.getUid() != 1000) && (!isEnabledNotificationListener(paramRemoteUserInfoImpl))) {
        break label192;
      }
      return true;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Package ");
      ((StringBuilder)localObject).append(paramRemoteUserInfoImpl.getPackageName());
      ((StringBuilder)localObject).append(" doesn't exist");
      Log.d("MediaSessionManager", ((StringBuilder)localObject).toString());
      return false;
    }
    label192:
    return false;
  }
  
  static class RemoteUserInfoImplBase
    implements MediaSessionManager.RemoteUserInfoImpl
  {
    private String mPackageName;
    private int mPid;
    private int mUid;
    
    RemoteUserInfoImplBase(String paramString, int paramInt1, int paramInt2)
    {
      mPackageName = paramString;
      mPid = paramInt1;
      mUid = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof RemoteUserInfoImplBase)) {
        return false;
      }
      paramObject = (RemoteUserInfoImplBase)paramObject;
      return (TextUtils.equals(mPackageName, mPackageName)) && (mPid == mPid) && (mUid == mUid);
    }
    
    public String getPackageName()
    {
      return mPackageName;
    }
    
    public int getPid()
    {
      return mPid;
    }
    
    public int getUid()
    {
      return mUid;
    }
    
    public int hashCode()
    {
      return ObjectsCompat.hash(new Object[] { mPackageName, Integer.valueOf(mPid), Integer.valueOf(mUid) });
    }
  }
}
