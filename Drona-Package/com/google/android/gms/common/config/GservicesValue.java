package com.google.android.gms.common.config;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.HashSet;
import javax.annotation.concurrent.GuardedBy;

@KeepForSdk
public abstract class GservicesValue<T>
{
  private static final Object sLock = new Object();
  private static zza zzbm = null;
  private static int zzbn = 0;
  private static Context zzbo;
  @GuardedBy("sLock")
  private static HashSet<String> zzbp;
  protected final String mKey;
  protected final T zzbq;
  private T zzbr = null;
  
  protected GservicesValue(String paramString, Object paramObject)
  {
    mKey = paramString;
    zzbq = paramObject;
  }
  
  private static boolean hasMoreUpdates()
  {
    Object localObject = sLock;
    try
    {
      return false;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static boolean isInitialized()
  {
    Object localObject = sLock;
    try
    {
      return false;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static GservicesValue value(String paramString, Float paramFloat)
  {
    return new PolynomialFunction(paramString, paramFloat);
  }
  
  public static GservicesValue value(String paramString, Integer paramInteger)
  {
    return new Scorer(paramString, paramInteger);
  }
  
  public static GservicesValue value(String paramString, Long paramLong)
  {
    return new Array2DRowRealMatrix(paramString, paramLong);
  }
  
  public static GservicesValue value(String paramString1, String paramString2)
  {
    return new RealVector(paramString1, paramString2);
  }
  
  public static GservicesValue value(String paramString, boolean paramBoolean)
  {
    return new ArrayRealVector(paramString, Boolean.valueOf(paramBoolean));
  }
  
  protected abstract Object add(String paramString);
  
  public final Object getBinderSafe()
  {
    return rewrite();
  }
  
  public void override(Object paramObject)
  {
    Log.w("GservicesValue", "GservicesValue.override(): test should probably call initForTests() first");
    zzbr = paramObject;
    paramObject = sLock;
    try
    {
      hasMoreUpdates();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void resetOverride()
  {
    zzbr = null;
  }
  
  /* Error */
  public final Object rewrite()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 42	com/google/android/gms/common/config/GservicesValue:zzbr	Ljava/lang/Object;
    //   4: ifnull +8 -> 12
    //   7: aload_0
    //   8: getfield 42	com/google/android/gms/common/config/GservicesValue:zzbr	Ljava/lang/Object;
    //   11: areturn
    //   12: invokestatic 117	android/os/StrictMode:allowThreadDiskReads	()Landroid/os/StrictMode$ThreadPolicy;
    //   15: astore_3
    //   16: getstatic 34	com/google/android/gms/common/config/GservicesValue:sLock	Ljava/lang/Object;
    //   19: astore 4
    //   21: aload 4
    //   23: monitorenter
    //   24: aload 4
    //   26: monitorexit
    //   27: getstatic 34	com/google/android/gms/common/config/GservicesValue:sLock	Ljava/lang/Object;
    //   30: astore 4
    //   32: aload 4
    //   34: monitorenter
    //   35: aconst_null
    //   36: putstatic 119	com/google/android/gms/common/config/GservicesValue:zzbp	Ljava/util/HashSet;
    //   39: aconst_null
    //   40: putstatic 121	com/google/android/gms/common/config/GservicesValue:zzbo	Landroid/content/Context;
    //   43: aload 4
    //   45: monitorexit
    //   46: aload_0
    //   47: getfield 44	com/google/android/gms/common/config/GservicesValue:mKey	Ljava/lang/String;
    //   50: astore 4
    //   52: aload_0
    //   53: aload 4
    //   55: invokevirtual 123	com/google/android/gms/common/config/GservicesValue:add	(Ljava/lang/String;)Ljava/lang/Object;
    //   58: astore 4
    //   60: aload_3
    //   61: invokestatic 127	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   64: aload 4
    //   66: areturn
    //   67: astore 4
    //   69: goto +37 -> 106
    //   72: invokestatic 133	android/os/Binder:clearCallingIdentity	()J
    //   75: lstore_1
    //   76: aload_0
    //   77: aload_0
    //   78: getfield 44	com/google/android/gms/common/config/GservicesValue:mKey	Ljava/lang/String;
    //   81: invokevirtual 123	com/google/android/gms/common/config/GservicesValue:add	(Ljava/lang/String;)Ljava/lang/Object;
    //   84: astore 4
    //   86: lload_1
    //   87: invokestatic 137	android/os/Binder:restoreCallingIdentity	(J)V
    //   90: aload_3
    //   91: invokestatic 127	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   94: aload 4
    //   96: areturn
    //   97: astore 4
    //   99: lload_1
    //   100: invokestatic 137	android/os/Binder:restoreCallingIdentity	(J)V
    //   103: aload 4
    //   105: athrow
    //   106: aload_3
    //   107: invokestatic 127	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   110: aload 4
    //   112: athrow
    //   113: astore_3
    //   114: aload 4
    //   116: monitorexit
    //   117: aload_3
    //   118: athrow
    //   119: astore_3
    //   120: aload 4
    //   122: monitorexit
    //   123: aload_3
    //   124: athrow
    //   125: astore 4
    //   127: goto -55 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	GservicesValue
    //   75	25	1	l	long
    //   15	92	3	localThreadPolicy	android.os.StrictMode.ThreadPolicy
    //   113	5	3	localThrowable1	Throwable
    //   119	5	3	localThrowable2	Throwable
    //   19	46	4	localObject1	Object
    //   67	1	4	localThrowable3	Throwable
    //   84	11	4	localObject2	Object
    //   97	24	4	localThrowable4	Throwable
    //   125	1	4	localSecurityException	SecurityException
    // Exception table:
    //   from	to	target	type
    //   52	60	67	java/lang/Throwable
    //   72	76	67	java/lang/Throwable
    //   86	90	67	java/lang/Throwable
    //   99	106	67	java/lang/Throwable
    //   76	86	97	java/lang/Throwable
    //   35	46	113	java/lang/Throwable
    //   114	117	113	java/lang/Throwable
    //   24	27	119	java/lang/Throwable
    //   120	123	119	java/lang/Throwable
    //   52	60	125	java/lang/SecurityException
  }
  
  private static abstract interface zza
  {
    public abstract Float convertTo(String paramString, Float paramFloat);
    
    public abstract Boolean getBooleanAttribute(String paramString, Boolean paramBoolean);
    
    public abstract Long getLong(String paramString, Long paramLong);
    
    public abstract String getString(String paramString1, String paramString2);
    
    public abstract Integer setDns1(String paramString, Integer paramInteger);
  }
}
