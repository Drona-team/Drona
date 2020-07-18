package com.google.android.gms.package_8;

import android.os.RemoteException;
import androidx.annotation.Nullable;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.ads.zzaar;
import com.google.android.gms.internal.ads.zzacc;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzbad;
import javax.annotation.concurrent.GuardedBy;

@zzard
public final class VideoController
{
  @KeepForSdk
  public static final int PLAYBACK_STATE_ENDED = 3;
  @KeepForSdk
  public static final int PLAYBACK_STATE_PAUSED = 2;
  @KeepForSdk
  public static final int PLAYBACK_STATE_PLAYING = 1;
  @KeepForSdk
  public static final int PLAYBACK_STATE_READY = 5;
  @KeepForSdk
  public static final int PLAYBACK_STATE_UNKNOWN = 0;
  private final Object lock = new Object();
  @Nullable
  @GuardedBy("lock")
  private zzaar zzaav;
  @Nullable
  @GuardedBy("lock")
  private VideoLifecycleCallbacks zzaaw;
  
  public VideoController() {}
  
  /* Error */
  public final float getAspectRatio()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/google/android/gms/package_8/VideoController:lock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   11: ifnonnull +7 -> 18
    //   14: aload_2
    //   15: monitorexit
    //   16: fconst_0
    //   17: freturn
    //   18: aload_0
    //   19: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   22: astore_3
    //   23: aload_3
    //   24: invokeinterface 47 1 0
    //   29: fstore_1
    //   30: aload_2
    //   31: monitorexit
    //   32: fload_1
    //   33: freturn
    //   34: astore_3
    //   35: ldc 49
    //   37: aload_3
    //   38: invokestatic 55	com/google/android/gms/internal/ads/zzbad:zzc	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   41: aload_2
    //   42: monitorexit
    //   43: fconst_0
    //   44: freturn
    //   45: astore_3
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VideoController
    //   29	4	1	f	float
    //   4	43	2	localObject	Object
    //   22	2	3	localZzaar	zzaar
    //   34	4	3	localRemoteException	RemoteException
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   23	30	34	android/os/RemoteException
    //   7	16	45	java/lang/Throwable
    //   23	30	45	java/lang/Throwable
    //   30	32	45	java/lang/Throwable
    //   35	43	45	java/lang/Throwable
    //   46	48	45	java/lang/Throwable
  }
  
  /* Error */
  public final int getPlaybackState()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/google/android/gms/package_8/VideoController:lock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   11: ifnonnull +7 -> 18
    //   14: aload_2
    //   15: monitorexit
    //   16: iconst_0
    //   17: ireturn
    //   18: aload_0
    //   19: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   22: astore_3
    //   23: aload_3
    //   24: invokeinterface 59 1 0
    //   29: istore_1
    //   30: aload_2
    //   31: monitorexit
    //   32: iload_1
    //   33: ireturn
    //   34: astore_3
    //   35: ldc 61
    //   37: aload_3
    //   38: invokestatic 55	com/google/android/gms/internal/ads/zzbad:zzc	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   41: aload_2
    //   42: monitorexit
    //   43: iconst_0
    //   44: ireturn
    //   45: astore_3
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VideoController
    //   29	4	1	i	int
    //   4	43	2	localObject	Object
    //   22	2	3	localZzaar	zzaar
    //   34	4	3	localRemoteException	RemoteException
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   23	30	34	android/os/RemoteException
    //   7	16	45	java/lang/Throwable
    //   23	30	45	java/lang/Throwable
    //   30	32	45	java/lang/Throwable
    //   35	43	45	java/lang/Throwable
    //   46	48	45	java/lang/Throwable
  }
  
  public final VideoLifecycleCallbacks getVideoLifecycleCallbacks()
  {
    Object localObject = lock;
    try
    {
      VideoLifecycleCallbacks localVideoLifecycleCallbacks = zzaaw;
      return localVideoLifecycleCallbacks;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final boolean hasVideoContent()
  {
    Object localObject = lock;
    for (;;)
    {
      try
      {
        if (zzaav != null)
        {
          bool = true;
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  /* Error */
  public final boolean isClickToExpandEnabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/google/android/gms/package_8/VideoController:lock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   11: ifnonnull +7 -> 18
    //   14: aload_2
    //   15: monitorexit
    //   16: iconst_0
    //   17: ireturn
    //   18: aload_0
    //   19: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   22: astore_3
    //   23: aload_3
    //   24: invokeinterface 70 1 0
    //   29: istore_1
    //   30: aload_2
    //   31: monitorexit
    //   32: iload_1
    //   33: ireturn
    //   34: astore_3
    //   35: ldc 72
    //   37: aload_3
    //   38: invokestatic 55	com/google/android/gms/internal/ads/zzbad:zzc	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   41: aload_2
    //   42: monitorexit
    //   43: iconst_0
    //   44: ireturn
    //   45: astore_3
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VideoController
    //   29	4	1	bool	boolean
    //   4	43	2	localObject	Object
    //   22	2	3	localZzaar	zzaar
    //   34	4	3	localRemoteException	RemoteException
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   23	30	34	android/os/RemoteException
    //   7	16	45	java/lang/Throwable
    //   23	30	45	java/lang/Throwable
    //   30	32	45	java/lang/Throwable
    //   35	43	45	java/lang/Throwable
    //   46	48	45	java/lang/Throwable
  }
  
  /* Error */
  public final boolean isCustomControlsEnabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/google/android/gms/package_8/VideoController:lock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   11: ifnonnull +7 -> 18
    //   14: aload_2
    //   15: monitorexit
    //   16: iconst_0
    //   17: ireturn
    //   18: aload_0
    //   19: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   22: astore_3
    //   23: aload_3
    //   24: invokeinterface 75 1 0
    //   29: istore_1
    //   30: aload_2
    //   31: monitorexit
    //   32: iload_1
    //   33: ireturn
    //   34: astore_3
    //   35: ldc 77
    //   37: aload_3
    //   38: invokestatic 55	com/google/android/gms/internal/ads/zzbad:zzc	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   41: aload_2
    //   42: monitorexit
    //   43: iconst_0
    //   44: ireturn
    //   45: astore_3
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VideoController
    //   29	4	1	bool	boolean
    //   4	43	2	localObject	Object
    //   22	2	3	localZzaar	zzaar
    //   34	4	3	localRemoteException	RemoteException
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   23	30	34	android/os/RemoteException
    //   7	16	45	java/lang/Throwable
    //   23	30	45	java/lang/Throwable
    //   30	32	45	java/lang/Throwable
    //   35	43	45	java/lang/Throwable
    //   46	48	45	java/lang/Throwable
  }
  
  /* Error */
  public final boolean isMuted()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/google/android/gms/package_8/VideoController:lock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   11: ifnonnull +7 -> 18
    //   14: aload_2
    //   15: monitorexit
    //   16: iconst_1
    //   17: ireturn
    //   18: aload_0
    //   19: getfield 43	com/google/android/gms/package_8/VideoController:zzaav	Lcom/google/android/gms/internal/ads/zzaar;
    //   22: astore_3
    //   23: aload_3
    //   24: invokeinterface 80 1 0
    //   29: istore_1
    //   30: aload_2
    //   31: monitorexit
    //   32: iload_1
    //   33: ireturn
    //   34: astore_3
    //   35: ldc 82
    //   37: aload_3
    //   38: invokestatic 55	com/google/android/gms/internal/ads/zzbad:zzc	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   41: aload_2
    //   42: monitorexit
    //   43: iconst_1
    //   44: ireturn
    //   45: astore_3
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VideoController
    //   29	4	1	bool	boolean
    //   4	43	2	localObject	Object
    //   22	2	3	localZzaar	zzaar
    //   34	4	3	localRemoteException	RemoteException
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   23	30	34	android/os/RemoteException
    //   7	16	45	java/lang/Throwable
    //   23	30	45	java/lang/Throwable
    //   30	32	45	java/lang/Throwable
    //   35	43	45	java/lang/Throwable
    //   46	48	45	java/lang/Throwable
  }
  
  public final void mute(boolean paramBoolean)
  {
    Object localObject = lock;
    try
    {
      if (zzaav == null) {
        return;
      }
      zzaar localZzaar = zzaav;
      try
      {
        localZzaar.mute(paramBoolean);
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zzc("Unable to call mute on video controller.", localRemoteException);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void pause()
  {
    Object localObject = lock;
    try
    {
      if (zzaav == null) {
        return;
      }
      zzaar localZzaar = zzaav;
      try
      {
        localZzaar.pause();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zzc("Unable to call pause on video controller.", localRemoteException);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void play()
  {
    Object localObject = lock;
    try
    {
      if (zzaav == null) {
        return;
      }
      zzaar localZzaar = zzaav;
      try
      {
        localZzaar.play();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zzc("Unable to call play on video controller.", localRemoteException);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void setDisplayMode(zzaar paramZzaar)
  {
    Object localObject = lock;
    try
    {
      zzaav = paramZzaar;
      if (zzaaw != null) {
        setVideoLifecycleCallbacks(zzaaw);
      }
      return;
    }
    catch (Throwable paramZzaar)
    {
      throw paramZzaar;
    }
  }
  
  public final void setVideoLifecycleCallbacks(VideoLifecycleCallbacks paramVideoLifecycleCallbacks)
  {
    Preconditions.checkNotNull(paramVideoLifecycleCallbacks, "VideoLifecycleCallbacks may not be null.");
    Object localObject = lock;
    try
    {
      zzaaw = paramVideoLifecycleCallbacks;
      if (zzaav == null) {
        return;
      }
      zzaar localZzaar = zzaav;
      try
      {
        localZzaar.zza(new zzacc(paramVideoLifecycleCallbacks));
      }
      catch (RemoteException paramVideoLifecycleCallbacks)
      {
        zzbad.zzc("Unable to call setVideoLifecycleCallbacks on video controller.", paramVideoLifecycleCallbacks);
      }
      return;
    }
    catch (Throwable paramVideoLifecycleCallbacks)
    {
      throw paramVideoLifecycleCallbacks;
    }
  }
  
  public final zzaar zzdh()
  {
    Object localObject = lock;
    try
    {
      zzaar localZzaar = zzaav;
      return localZzaar;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public class VideoLifecycleCallbacks
  {
    public VideoLifecycleCallbacks() {}
    
    public void onVideoEnd() {}
    
    public void onVideoMute(boolean paramBoolean) {}
    
    public void onVideoPause() {}
    
    public void onVideoPlay() {}
    
    public void onVideoStart() {}
  }
}
