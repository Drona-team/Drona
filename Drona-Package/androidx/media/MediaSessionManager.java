package androidx.media;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

public final class MediaSessionManager
{
  static final boolean DEBUG = Log.isLoggable("MediaSessionManager", 3);
  static final String PAGE_KEY = "MediaSessionManager";
  private static final Object sLock = new Object();
  private static volatile MediaSessionManager sSessionManager;
  MediaSessionManagerImpl mImpl;
  
  private MediaSessionManager(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 28)
    {
      mImpl = new MediaSessionManagerImplApi28(paramContext);
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      mImpl = new MediaSessionManagerImplApi21(paramContext);
      return;
    }
    mImpl = new MediaSessionManagerImplBase(paramContext);
  }
  
  public static MediaSessionManager getSessionManager(Context paramContext)
  {
    Object localObject1 = sSessionManager;
    if (localObject1 == null)
    {
      Object localObject2 = sLock;
      try
      {
        MediaSessionManager localMediaSessionManager = sSessionManager;
        localObject1 = localMediaSessionManager;
        if (localMediaSessionManager == null)
        {
          sSessionManager = new MediaSessionManager(paramContext.getApplicationContext());
          localObject1 = sSessionManager;
        }
        return localObject1;
      }
      catch (Throwable paramContext)
      {
        throw paramContext;
      }
    }
    return localObject1;
  }
  
  Context getContext()
  {
    return mImpl.getContext();
  }
  
  public boolean isTrustedForMediaControl(RemoteUserInfo paramRemoteUserInfo)
  {
    if (paramRemoteUserInfo != null) {
      return mImpl.isTrustedForMediaControl(mImpl);
    }
    throw new IllegalArgumentException("userInfo should not be null");
  }
  
  static abstract interface MediaSessionManagerImpl
  {
    public abstract Context getContext();
    
    public abstract boolean isTrustedForMediaControl(MediaSessionManager.RemoteUserInfoImpl paramRemoteUserInfoImpl);
  }
  
  public static final class RemoteUserInfo
  {
    public static final String LEGACY_CONTROLLER = "android.media.session.MediaController";
    MediaSessionManager.RemoteUserInfoImpl mImpl;
    
    public RemoteUserInfo(android.media.session.MediaSessionManager.RemoteUserInfo paramRemoteUserInfo)
    {
      mImpl = new MediaSessionManagerImplApi28.RemoteUserInfoImplApi28(paramRemoteUserInfo);
    }
    
    public RemoteUserInfo(String paramString, int paramInt1, int paramInt2)
    {
      if (Build.VERSION.SDK_INT >= 28)
      {
        mImpl = new MediaSessionManagerImplApi28.RemoteUserInfoImplApi28(paramString, paramInt1, paramInt2);
        return;
      }
      mImpl = new MediaSessionManagerImplBase.RemoteUserInfoImplBase(paramString, paramInt1, paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof RemoteUserInfo)) {
        return false;
      }
      return mImpl.equals(mImpl);
    }
    
    public String getPackageName()
    {
      return mImpl.getPackageName();
    }
    
    public int getPid()
    {
      return mImpl.getPid();
    }
    
    public int getUid()
    {
      return mImpl.getUid();
    }
    
    public int hashCode()
    {
      return mImpl.hashCode();
    }
  }
  
  static abstract interface RemoteUserInfoImpl
  {
    public abstract String getPackageName();
    
    public abstract int getPid();
    
    public abstract int getUid();
  }
}
