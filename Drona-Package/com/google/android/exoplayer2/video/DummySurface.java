package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EGLSurfaceTexture;
import com.google.android.exoplayer2.util.Util;

@TargetApi(17)
public final class DummySurface
  extends Surface
{
  private static final String EXTENSION_PROTECTED_CONTENT = "EGL_EXT_protected_content";
  private static final String EXTENSION_SURFACELESS_CONTEXT = "EGL_KHR_surfaceless_context";
  private static final String PAGE_KEY = "DummySurface";
  private static int secureMode;
  private static boolean secureModeInitialized;
  public final boolean secure;
  private final DummySurfaceThread thread;
  private boolean threadReleased;
  
  private DummySurface(DummySurfaceThread paramDummySurfaceThread, SurfaceTexture paramSurfaceTexture, boolean paramBoolean)
  {
    super(paramSurfaceTexture);
    thread = paramDummySurfaceThread;
    secure = paramBoolean;
  }
  
  private static void assertApiLevel17OrHigher()
  {
    if (Util.SDK_INT >= 17) {
      return;
    }
    throw new UnsupportedOperationException("Unsupported prior to API level 17");
  }
  
  private static int getSecureModeV24(Context paramContext)
  {
    if (Util.SDK_INT < 26)
    {
      if ("samsung".equals(Util.MANUFACTURER)) {
        break label95;
      }
      if ("XT1650".equals(Util.MODEL)) {
        return 0;
      }
    }
    if ((Util.SDK_INT < 26) && (!paramContext.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance"))) {
      return 0;
    }
    paramContext = EGL14.eglQueryString(EGL14.eglGetDisplay(0), 12373);
    if (paramContext == null) {
      return 0;
    }
    if (!paramContext.contains("EGL_EXT_protected_content")) {
      return 0;
    }
    if (paramContext.contains("EGL_KHR_surfaceless_context")) {
      return 1;
    }
    return 2;
    label95:
    return 0;
  }
  
  public static boolean isSecureSupported(Context paramContext)
  {
    try
    {
      boolean bool2 = secureModeInitialized;
      boolean bool1 = true;
      if (!bool2)
      {
        if (Util.SDK_INT < 24) {
          i = 0;
        } else {
          i = getSecureModeV24(paramContext);
        }
        secureMode = i;
        secureModeInitialized = true;
      }
      int i = secureMode;
      if (i == 0) {
        bool1 = false;
      }
      return bool1;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static DummySurface newInstanceV17(Context paramContext, boolean paramBoolean)
  {
    assertApiLevel17OrHigher();
    int i = 0;
    boolean bool;
    if ((paramBoolean) && (!isSecureSupported(paramContext))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkState(bool);
    paramContext = new DummySurfaceThread();
    if (paramBoolean) {
      i = secureMode;
    }
    return paramContext.init(i);
  }
  
  public void release()
  {
    super.release();
    DummySurfaceThread localDummySurfaceThread = thread;
    try
    {
      if (!threadReleased)
      {
        thread.release();
        threadReleased = true;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static class DummySurfaceThread
    extends HandlerThread
    implements Handler.Callback
  {
    private static final int MSG_INIT = 1;
    private static final int MSG_RELEASE = 2;
    private EGLSurfaceTexture eglSurfaceTexture;
    private Handler handler;
    @Nullable
    private Error initError;
    @Nullable
    private RuntimeException initException;
    @Nullable
    private DummySurface surface;
    
    public DummySurfaceThread()
    {
      super();
    }
    
    private void initInternal(int paramInt)
    {
      Assertions.checkNotNull(eglSurfaceTexture);
      eglSurfaceTexture.init(paramInt);
      SurfaceTexture localSurfaceTexture = eglSurfaceTexture.getSurfaceTexture();
      boolean bool;
      if (paramInt != 0) {
        bool = true;
      } else {
        bool = false;
      }
      surface = new DummySurface(this, localSurfaceTexture, bool, null);
    }
    
    private void releaseInternal()
    {
      Assertions.checkNotNull(eglSurfaceTexture);
      eglSurfaceTexture.release();
    }
    
    /* Error */
    public boolean handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 74	android/os/Message:what	I
      //   4: lookupswitch	default:+28->32, 1:+67->71, 2:+33->37
      //   32: goto +3 -> 35
      //   35: iconst_1
      //   36: ireturn
      //   37: aload_0
      //   38: invokespecial 76	com/google/android/exoplayer2/video/DummySurface$DummySurfaceThread:releaseInternal	()V
      //   41: aload_0
      //   42: invokevirtual 80	android/os/HandlerThread:quit	()Z
      //   45: pop
      //   46: iconst_1
      //   47: ireturn
      //   48: astore_1
      //   49: goto +15 -> 64
      //   52: astore_1
      //   53: ldc 82
      //   55: ldc 84
      //   57: aload_1
      //   58: invokestatic 90	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   61: goto -20 -> 41
      //   64: aload_0
      //   65: invokevirtual 80	android/os/HandlerThread:quit	()Z
      //   68: pop
      //   69: aload_1
      //   70: athrow
      //   71: aload_0
      //   72: aload_1
      //   73: getfield 93	android/os/Message:arg1	I
      //   76: invokespecial 95	com/google/android/exoplayer2/video/DummySurface$DummySurfaceThread:initInternal	(I)V
      //   79: aload_0
      //   80: monitorenter
      //   81: aload_0
      //   82: invokevirtual 100	java/lang/Object:notify	()V
      //   85: aload_0
      //   86: monitorexit
      //   87: iconst_1
      //   88: ireturn
      //   89: astore_1
      //   90: aload_0
      //   91: monitorexit
      //   92: aload_1
      //   93: athrow
      //   94: astore_1
      //   95: goto +61 -> 156
      //   98: astore_1
      //   99: ldc 82
      //   101: ldc 102
      //   103: aload_1
      //   104: invokestatic 90	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   107: aload_0
      //   108: aload_1
      //   109: putfield 104	com/google/android/exoplayer2/video/DummySurface$DummySurfaceThread:initError	Ljava/lang/Error;
      //   112: aload_0
      //   113: monitorenter
      //   114: aload_0
      //   115: invokevirtual 100	java/lang/Object:notify	()V
      //   118: aload_0
      //   119: monitorexit
      //   120: iconst_1
      //   121: ireturn
      //   122: astore_1
      //   123: aload_0
      //   124: monitorexit
      //   125: aload_1
      //   126: athrow
      //   127: astore_1
      //   128: ldc 82
      //   130: ldc 102
      //   132: aload_1
      //   133: invokestatic 90	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   136: aload_0
      //   137: aload_1
      //   138: putfield 106	com/google/android/exoplayer2/video/DummySurface$DummySurfaceThread:initException	Ljava/lang/RuntimeException;
      //   141: aload_0
      //   142: monitorenter
      //   143: aload_0
      //   144: invokevirtual 100	java/lang/Object:notify	()V
      //   147: aload_0
      //   148: monitorexit
      //   149: iconst_1
      //   150: ireturn
      //   151: astore_1
      //   152: aload_0
      //   153: monitorexit
      //   154: aload_1
      //   155: athrow
      //   156: aload_0
      //   157: monitorenter
      //   158: aload_0
      //   159: invokevirtual 100	java/lang/Object:notify	()V
      //   162: aload_0
      //   163: monitorexit
      //   164: aload_1
      //   165: athrow
      //   166: astore_1
      //   167: aload_0
      //   168: monitorexit
      //   169: aload_1
      //   170: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	171	0	this	DummySurfaceThread
      //   0	171	1	paramMessage	Message
      // Exception table:
      //   from	to	target	type
      //   53	61	48	java/lang/Throwable
      //   37	41	52	java/lang/Throwable
      //   81	87	89	java/lang/Throwable
      //   90	92	89	java/lang/Throwable
      //   71	79	94	java/lang/Throwable
      //   99	112	94	java/lang/Throwable
      //   128	141	94	java/lang/Throwable
      //   71	79	98	java/lang/Error
      //   114	120	122	java/lang/Throwable
      //   123	125	122	java/lang/Throwable
      //   71	79	127	java/lang/RuntimeException
      //   143	149	151	java/lang/Throwable
      //   152	154	151	java/lang/Throwable
      //   158	164	166	java/lang/Throwable
      //   167	169	166	java/lang/Throwable
    }
    
    public DummySurface init(int paramInt)
    {
      start();
      handler = new Handler(getLooper(), this);
      Object localObject = handler;
      DummySurfaceThread localDummySurfaceThread = this;
      eglSurfaceTexture = new EGLSurfaceTexture((Handler)localObject);
      for (;;)
      {
        try
        {
          localObject = handler;
          int i = 0;
          ((Handler)localObject).obtainMessage(1, paramInt, 0).sendToTarget();
          paramInt = i;
          if ((surface == null) && (initException == null))
          {
            localObject = initError;
            if (localObject != null) {
              break;
            }
          }
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
        try
        {
          localDummySurfaceThread.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          continue;
        }
        paramInt = 1;
      }
      if (paramInt != 0) {
        Thread.currentThread().interrupt();
      }
      if (initException == null)
      {
        if (initError == null) {
          return (DummySurface)Assertions.checkNotNull(surface);
        }
        throw initError;
      }
      throw initException;
    }
    
    public void release()
    {
      Assertions.checkNotNull(handler);
      handler.sendEmptyMessage(2);
    }
  }
}
