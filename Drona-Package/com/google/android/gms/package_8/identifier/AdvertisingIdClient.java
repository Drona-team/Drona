package com.google.android.gms.package_8.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.android.gms.common.BlockingServiceConnection;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.annotation.KeepForSdkWithMembers;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads_identifier.zze;
import com.google.android.gms.internal.ads_identifier.zzf;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.GuardedBy;

@ParametersAreNonnullByDefault
@KeepForSdk
public class AdvertisingIdClient
{
  @Nullable
  @GuardedBy("this")
  private BlockingServiceConnection data;
  @GuardedBy("this")
  private final Context mContext;
  private final boolean mRoot;
  private final Object next = new Object();
  @Nullable
  @GuardedBy("mAutoDisconnectTaskLock")
  private zza out;
  private final long state;
  @Nullable
  @GuardedBy("this")
  private zze title;
  @GuardedBy("this")
  private boolean type;
  
  public AdvertisingIdClient(Context paramContext)
  {
    this(paramContext, 30000L, false, false);
  }
  
  private AdvertisingIdClient(Context paramContext, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    Preconditions.checkNotNull(paramContext);
    Context localContext = paramContext;
    if (paramBoolean1)
    {
      localContext = paramContext.getApplicationContext();
      if (localContext == null) {
        localContext = paramContext;
      }
    }
    mContext = localContext;
    type = false;
    state = paramLong;
    mRoot = paramBoolean2;
  }
  
  private final boolean add(Info paramInfo, boolean paramBoolean, float paramFloat, long paramLong, String paramString, Throwable paramThrowable)
  {
    if (Math.random() > paramFloat) {
      return false;
    }
    HashMap localHashMap = new HashMap();
    String str;
    if (paramBoolean) {
      str = "1";
    } else {
      str = "0";
    }
    localHashMap.put("app_context", str);
    if (paramInfo != null)
    {
      if (paramInfo.isLimitAdTrackingEnabled()) {
        str = "1";
      } else {
        str = "0";
      }
      localHashMap.put("limit_ad_tracking", str);
    }
    if ((paramInfo != null) && (paramInfo.getId() != null)) {
      localHashMap.put("ad_id_size", Integer.toString(paramInfo.getId().length()));
    }
    if (paramThrowable != null) {
      localHashMap.put("error", paramThrowable.getClass().getName());
    }
    if ((paramString != null) && (!paramString.isEmpty())) {
      localHashMap.put("experiment_id", paramString);
    }
    localHashMap.put("tag", "AdvertisingIdClient");
    localHashMap.put("time_spent", Long.toString(paramLong));
    new SqlTileWriter.1(this, localHashMap).start();
    return true;
  }
  
  /* Error */
  private final void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 43	com/google/android/gms/package_8/identifier/AdvertisingIdClient:next	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   11: ifnull +22 -> 33
    //   14: aload_0
    //   15: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   18: getfield 158	com/google/android/gms/package_8/identifier/AdvertisingIdClient$zza:ready	Ljava/util/concurrent/CountDownLatch;
    //   21: invokevirtual 163	java/util/concurrent/CountDownLatch:countDown	()V
    //   24: aload_0
    //   25: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   28: astore_2
    //   29: aload_2
    //   30: invokevirtual 166	java/lang/Thread:join	()V
    //   33: aload_0
    //   34: getfield 61	com/google/android/gms/package_8/identifier/AdvertisingIdClient:state	J
    //   37: lconst_0
    //   38: lcmp
    //   39: ifle +19 -> 58
    //   42: aload_0
    //   43: new 8	com/google/android/gms/package_8/identifier/AdvertisingIdClient$zza
    //   46: dup
    //   47: aload_0
    //   48: aload_0
    //   49: getfield 61	com/google/android/gms/package_8/identifier/AdvertisingIdClient:state	J
    //   52: invokespecial 169	com/google/android/gms/package_8/identifier/AdvertisingIdClient$zza:<init>	(Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient;J)V
    //   55: putfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   58: aload_1
    //   59: monitorexit
    //   60: return
    //   61: astore_2
    //   62: aload_1
    //   63: monitorexit
    //   64: aload_2
    //   65: athrow
    //   66: astore_2
    //   67: goto -34 -> 33
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	this	AdvertisingIdClient
    //   4	59	1	localObject	Object
    //   28	2	2	localZza	zza
    //   61	4	2	localThrowable	Throwable
    //   66	1	2	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   7	24	61	java/lang/Throwable
    //   29	33	61	java/lang/Throwable
    //   33	58	61	java/lang/Throwable
    //   58	60	61	java/lang/Throwable
    //   62	64	61	java/lang/Throwable
    //   29	33	66	java/lang/InterruptedException
  }
  
  /* Error */
  public static Info getAdvertisingIdInfo(Context paramContext)
    throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    // Byte code:
    //   0: new 181	com/google/android/gms/package_8/identifier/AndroidPreferences
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 183	com/google/android/gms/package_8/identifier/AndroidPreferences:<init>	(Landroid/content/Context;)V
    //   8: astore 8
    //   10: aload 8
    //   12: ldc -71
    //   14: iconst_0
    //   15: invokevirtual 189	com/google/android/gms/package_8/identifier/AndroidPreferences:getBoolean	(Ljava/lang/String;Z)Z
    //   18: istore_2
    //   19: aload 8
    //   21: ldc -65
    //   23: fconst_0
    //   24: invokevirtual 195	com/google/android/gms/package_8/identifier/AndroidPreferences:getFloat	(Ljava/lang/String;F)F
    //   27: fstore_1
    //   28: aload 8
    //   30: ldc -59
    //   32: ldc -57
    //   34: invokevirtual 203	com/google/android/gms/package_8/identifier/AndroidPreferences:getString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   37: astore 7
    //   39: new 2	com/google/android/gms/package_8/identifier/AdvertisingIdClient
    //   42: dup
    //   43: aload_0
    //   44: ldc2_w 204
    //   47: iload_2
    //   48: aload 8
    //   50: ldc -49
    //   52: iconst_0
    //   53: invokevirtual 189	com/google/android/gms/package_8/identifier/AndroidPreferences:getBoolean	(Ljava/lang/String;Z)Z
    //   56: invokespecial 37	com/google/android/gms/package_8/identifier/AdvertisingIdClient:<init>	(Landroid/content/Context;JZZ)V
    //   59: astore_0
    //   60: invokestatic 213	android/os/SystemClock:elapsedRealtime	()J
    //   63: lstore_3
    //   64: aload_0
    //   65: iconst_0
    //   66: invokespecial 217	com/google/android/gms/package_8/identifier/AdvertisingIdClient:initialize	(Z)V
    //   69: aload_0
    //   70: invokevirtual 221	com/google/android/gms/package_8/identifier/AdvertisingIdClient:getInfo	()Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$Info;
    //   73: astore 8
    //   75: invokestatic 213	android/os/SystemClock:elapsedRealtime	()J
    //   78: lstore 5
    //   80: aload_0
    //   81: aload 8
    //   83: iload_2
    //   84: fload_1
    //   85: lload 5
    //   87: lload_3
    //   88: lsub
    //   89: aload 7
    //   91: aconst_null
    //   92: invokespecial 223	com/google/android/gms/package_8/identifier/AdvertisingIdClient:add	(Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$Info;ZFJLjava/lang/String;Ljava/lang/Throwable;)Z
    //   95: pop
    //   96: aload_0
    //   97: invokevirtual 226	com/google/android/gms/package_8/identifier/AdvertisingIdClient:finish	()V
    //   100: aload 8
    //   102: areturn
    //   103: astore 7
    //   105: goto +23 -> 128
    //   108: astore 8
    //   110: aload_0
    //   111: aconst_null
    //   112: iload_2
    //   113: fload_1
    //   114: ldc2_w 204
    //   117: aload 7
    //   119: aload 8
    //   121: invokespecial 223	com/google/android/gms/package_8/identifier/AdvertisingIdClient:add	(Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$Info;ZFJLjava/lang/String;Ljava/lang/Throwable;)Z
    //   124: pop
    //   125: aload 8
    //   127: athrow
    //   128: aload_0
    //   129: invokevirtual 226	com/google/android/gms/package_8/identifier/AdvertisingIdClient:finish	()V
    //   132: aload 7
    //   134: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	paramContext	Context
    //   27	87	1	f	float
    //   18	95	2	bool	boolean
    //   63	25	3	l1	long
    //   78	8	5	l2	long
    //   37	53	7	str	String
    //   103	30	7	localThrowable1	Throwable
    //   8	93	8	localObject	Object
    //   108	18	8	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   110	128	103	java/lang/Throwable
    //   60	80	108	java/lang/Throwable
    //   80	96	108	java/lang/Throwable
  }
  
  public static boolean getIsAdIdFakeForDebugLogging(Context paramContext)
    throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    AndroidPreferences localAndroidPreferences = new AndroidPreferences(paramContext);
    paramContext = new AdvertisingIdClient(paramContext, -1L, localAndroidPreferences.getBoolean("gads:ad_id_app_context:enabled", false), localAndroidPreferences.getBoolean("com.google.android.gms.ads.identifier.service.PERSISTENT_START", false));
    try
    {
      paramContext.initialize(false);
      boolean bool = paramContext.run();
      paramContext.finish();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      paramContext.finish();
      throw localThrowable;
    }
  }
  
  private static zze getSocket(Context paramContext, BlockingServiceConnection paramBlockingServiceConnection)
    throws IOException
  {
    paramContext = TimeUnit.MILLISECONDS;
    try
    {
      paramContext = zzf.zza(paramBlockingServiceConnection.getServiceWithTimeout(10000L, paramContext));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw new IOException(paramContext);
      throw new IOException("Interrupted exception");
    }
    catch (InterruptedException paramContext)
    {
      for (;;) {}
    }
  }
  
  private static BlockingServiceConnection initialize(Context paramContext, boolean paramBoolean)
    throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    try
    {
      paramContext.getPackageManager().getPackageInfo("com.android.vending", 0);
      int i = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(paramContext, 12451000);
      if ((i != 0) && (i != 2)) {
        throw new IOException("Google Play services not available");
      }
      if (paramBoolean) {
        localObject = "com.google.android.gms.ads.identifier.service.PERSISTENT_START";
      } else {
        localObject = "com.google.android.gms.ads.identifier.service.START";
      }
      BlockingServiceConnection localBlockingServiceConnection = new BlockingServiceConnection();
      Object localObject = new Intent((String)localObject);
      ((Intent)localObject).setPackage("com.google.android.gms");
      try
      {
        paramBoolean = ConnectionTracker.getInstance().bindService(paramContext, (Intent)localObject, localBlockingServiceConnection, 1);
        if (paramBoolean) {
          return localBlockingServiceConnection;
        }
        throw new IOException("Connection failure");
      }
      catch (Throwable paramContext)
      {
        throw new IOException(paramContext);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    throw new GooglePlayServicesNotAvailableException(9);
  }
  
  private final void initialize(boolean paramBoolean)
    throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    Preconditions.checkNotMainThread("Calling this from your main thread can lead to deadlock");
    try
    {
      if (type) {
        finish();
      }
      data = initialize(mContext, mRoot);
      title = getSocket(mContext, data);
      type = true;
      if (paramBoolean) {
        close();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  private final boolean run()
    throws IOException
  {
    // Byte code:
    //   0: ldc_w 320
    //   3: invokestatic 323	com/google/android/gms/common/internal/Preconditions:checkNotMainThread	(Ljava/lang/String;)V
    //   6: aload_0
    //   7: monitorenter
    //   8: aload_0
    //   9: getfield 59	com/google/android/gms/package_8/identifier/AdvertisingIdClient:type	Z
    //   12: ifne +86 -> 98
    //   15: aload_0
    //   16: getfield 43	com/google/android/gms/package_8/identifier/AdvertisingIdClient:next	Ljava/lang/Object;
    //   19: astore_2
    //   20: aload_2
    //   21: monitorenter
    //   22: aload_0
    //   23: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   26: ifnull +56 -> 82
    //   29: aload_0
    //   30: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   33: getfield 339	com/google/android/gms/package_8/identifier/AdvertisingIdClient$zza:value	Z
    //   36: ifeq +46 -> 82
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_0
    //   42: iconst_0
    //   43: invokespecial 217	com/google/android/gms/package_8/identifier/AdvertisingIdClient:initialize	(Z)V
    //   46: aload_0
    //   47: getfield 59	com/google/android/gms/package_8/identifier/AdvertisingIdClient:type	Z
    //   50: istore_1
    //   51: iload_1
    //   52: ifeq +6 -> 58
    //   55: goto +43 -> 98
    //   58: new 173	java/io/IOException
    //   61: dup
    //   62: ldc_w 341
    //   65: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   68: athrow
    //   69: astore_2
    //   70: new 173	java/io/IOException
    //   73: dup
    //   74: ldc_w 341
    //   77: aload_2
    //   78: invokespecial 344	java/io/IOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   81: athrow
    //   82: new 173	java/io/IOException
    //   85: dup
    //   86: ldc_w 346
    //   89: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   92: athrow
    //   93: astore_3
    //   94: aload_2
    //   95: monitorexit
    //   96: aload_3
    //   97: athrow
    //   98: aload_0
    //   99: getfield 327	com/google/android/gms/package_8/identifier/AdvertisingIdClient:data	Lcom/google/android/gms/common/BlockingServiceConnection;
    //   102: invokestatic 49	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   105: pop
    //   106: aload_0
    //   107: getfield 331	com/google/android/gms/package_8/identifier/AdvertisingIdClient:title	Lcom/google/android/gms/internal/ads_identifier/zze;
    //   110: invokestatic 49	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   113: pop
    //   114: aload_0
    //   115: getfield 331	com/google/android/gms/package_8/identifier/AdvertisingIdClient:title	Lcom/google/android/gms/internal/ads_identifier/zze;
    //   118: astore_2
    //   119: aload_2
    //   120: invokeinterface 351 1 0
    //   125: istore_1
    //   126: aload_0
    //   127: monitorexit
    //   128: aload_0
    //   129: invokespecial 333	com/google/android/gms/package_8/identifier/AdvertisingIdClient:close	()V
    //   132: iload_1
    //   133: ireturn
    //   134: astore_2
    //   135: ldc -126
    //   137: ldc_w 353
    //   140: aload_2
    //   141: invokestatic 359	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   144: pop
    //   145: new 173	java/io/IOException
    //   148: dup
    //   149: ldc_w 361
    //   152: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   155: athrow
    //   156: astore_2
    //   157: aload_0
    //   158: monitorexit
    //   159: aload_2
    //   160: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	161	0	this	AdvertisingIdClient
    //   50	83	1	bool	boolean
    //   19	21	2	localObject	Object
    //   69	26	2	localException	Exception
    //   118	2	2	localZze	zze
    //   134	7	2	localRemoteException	android.os.RemoteException
    //   156	4	2	localThrowable1	Throwable
    //   93	4	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   41	46	69	java/lang/Exception
    //   22	41	93	java/lang/Throwable
    //   82	93	93	java/lang/Throwable
    //   94	96	93	java/lang/Throwable
    //   119	126	134	android/os/RemoteException
    //   8	22	156	java/lang/Throwable
    //   41	46	156	java/lang/Throwable
    //   46	51	156	java/lang/Throwable
    //   58	69	156	java/lang/Throwable
    //   70	82	156	java/lang/Throwable
    //   96	98	156	java/lang/Throwable
    //   98	114	156	java/lang/Throwable
    //   119	126	156	java/lang/Throwable
    //   126	128	156	java/lang/Throwable
    //   135	156	156	java/lang/Throwable
    //   157	159	156	java/lang/Throwable
  }
  
  public static void setShouldSkipGmsCoreVersionCheck(boolean paramBoolean) {}
  
  protected void finalize()
    throws Throwable
  {
    finish();
    super.finalize();
  }
  
  public final void finish()
  {
    Preconditions.checkNotMainThread("Calling this from your main thread can lead to deadlock");
    try
    {
      if (mContext != null)
      {
        BlockingServiceConnection localBlockingServiceConnection = data;
        if (localBlockingServiceConnection != null)
        {
          try
          {
            if (type) {
              ConnectionTracker.getInstance().unbindService(mContext, data);
            }
          }
          catch (Throwable localThrowable1)
          {
            Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", localThrowable1);
          }
          type = false;
          title = null;
          data = null;
          return;
        }
      }
      return;
    }
    catch (Throwable localThrowable2)
    {
      throw localThrowable2;
    }
  }
  
  /* Error */
  public Info getInfo()
    throws IOException
  {
    // Byte code:
    //   0: ldc_w 320
    //   3: invokestatic 323	com/google/android/gms/common/internal/Preconditions:checkNotMainThread	(Ljava/lang/String;)V
    //   6: aload_0
    //   7: monitorenter
    //   8: aload_0
    //   9: getfield 59	com/google/android/gms/package_8/identifier/AdvertisingIdClient:type	Z
    //   12: ifne +86 -> 98
    //   15: aload_0
    //   16: getfield 43	com/google/android/gms/package_8/identifier/AdvertisingIdClient:next	Ljava/lang/Object;
    //   19: astore_2
    //   20: aload_2
    //   21: monitorenter
    //   22: aload_0
    //   23: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   26: ifnull +56 -> 82
    //   29: aload_0
    //   30: getfield 154	com/google/android/gms/package_8/identifier/AdvertisingIdClient:out	Lcom/google/android/gms/package_8/identifier/AdvertisingIdClient$zza;
    //   33: getfield 339	com/google/android/gms/package_8/identifier/AdvertisingIdClient$zza:value	Z
    //   36: ifeq +46 -> 82
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_0
    //   42: iconst_0
    //   43: invokespecial 217	com/google/android/gms/package_8/identifier/AdvertisingIdClient:initialize	(Z)V
    //   46: aload_0
    //   47: getfield 59	com/google/android/gms/package_8/identifier/AdvertisingIdClient:type	Z
    //   50: istore_1
    //   51: iload_1
    //   52: ifeq +6 -> 58
    //   55: goto +43 -> 98
    //   58: new 173	java/io/IOException
    //   61: dup
    //   62: ldc_w 341
    //   65: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   68: athrow
    //   69: astore_2
    //   70: new 173	java/io/IOException
    //   73: dup
    //   74: ldc_w 341
    //   77: aload_2
    //   78: invokespecial 344	java/io/IOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   81: athrow
    //   82: new 173	java/io/IOException
    //   85: dup
    //   86: ldc_w 346
    //   89: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   92: athrow
    //   93: astore_3
    //   94: aload_2
    //   95: monitorexit
    //   96: aload_3
    //   97: athrow
    //   98: aload_0
    //   99: getfield 327	com/google/android/gms/package_8/identifier/AdvertisingIdClient:data	Lcom/google/android/gms/common/BlockingServiceConnection;
    //   102: invokestatic 49	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   105: pop
    //   106: aload_0
    //   107: getfield 331	com/google/android/gms/package_8/identifier/AdvertisingIdClient:title	Lcom/google/android/gms/internal/ads_identifier/zze;
    //   110: invokestatic 49	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   113: pop
    //   114: aload_0
    //   115: getfield 331	com/google/android/gms/package_8/identifier/AdvertisingIdClient:title	Lcom/google/android/gms/internal/ads_identifier/zze;
    //   118: astore_2
    //   119: aload_2
    //   120: invokeinterface 372 1 0
    //   125: astore_2
    //   126: aload_0
    //   127: getfield 331	com/google/android/gms/package_8/identifier/AdvertisingIdClient:title	Lcom/google/android/gms/internal/ads_identifier/zze;
    //   130: astore_3
    //   131: new 6	com/google/android/gms/package_8/identifier/AdvertisingIdClient$Info
    //   134: dup
    //   135: aload_2
    //   136: aload_3
    //   137: iconst_1
    //   138: invokeinterface 376 2 0
    //   143: invokespecial 379	com/google/android/gms/package_8/identifier/AdvertisingIdClient$Info:<init>	(Ljava/lang/String;Z)V
    //   146: astore_2
    //   147: aload_0
    //   148: monitorexit
    //   149: aload_0
    //   150: invokespecial 333	com/google/android/gms/package_8/identifier/AdvertisingIdClient:close	()V
    //   153: aload_2
    //   154: areturn
    //   155: astore_2
    //   156: ldc -126
    //   158: ldc_w 353
    //   161: aload_2
    //   162: invokestatic 359	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   165: pop
    //   166: new 173	java/io/IOException
    //   169: dup
    //   170: ldc_w 361
    //   173: invokespecial 264	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   176: athrow
    //   177: astore_2
    //   178: aload_0
    //   179: monitorexit
    //   180: aload_2
    //   181: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	182	0	this	AdvertisingIdClient
    //   50	2	1	bool	boolean
    //   19	21	2	localObject1	Object
    //   69	26	2	localException	Exception
    //   118	36	2	localObject2	Object
    //   155	7	2	localRemoteException	android.os.RemoteException
    //   177	4	2	localThrowable1	Throwable
    //   93	4	3	localThrowable2	Throwable
    //   130	7	3	localZze	zze
    // Exception table:
    //   from	to	target	type
    //   41	46	69	java/lang/Exception
    //   22	41	93	java/lang/Throwable
    //   82	93	93	java/lang/Throwable
    //   94	96	93	java/lang/Throwable
    //   119	126	155	android/os/RemoteException
    //   131	147	155	android/os/RemoteException
    //   8	22	177	java/lang/Throwable
    //   41	46	177	java/lang/Throwable
    //   46	51	177	java/lang/Throwable
    //   58	69	177	java/lang/Throwable
    //   70	82	177	java/lang/Throwable
    //   96	98	177	java/lang/Throwable
    //   98	119	177	java/lang/Throwable
    //   119	126	177	java/lang/Throwable
    //   131	147	177	java/lang/Throwable
    //   147	149	177	java/lang/Throwable
    //   156	177	177	java/lang/Throwable
    //   178	180	177	java/lang/Throwable
  }
  
  public void start()
    throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    initialize(true);
  }
  
  @KeepForSdkWithMembers
  public final class Info
  {
    private final boolean mIsValid;
    
    public Info(boolean paramBoolean)
    {
      mIsValid = paramBoolean;
    }
    
    public final String getId()
    {
      return AdvertisingIdClient.this;
    }
    
    public final boolean isLimitAdTrackingEnabled()
    {
      return mIsValid;
    }
    
    public final String toString()
    {
      String str = AdvertisingIdClient.this;
      boolean bool = mIsValid;
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 7);
      localStringBuilder.append("{");
      localStringBuilder.append(str);
      localStringBuilder.append("}");
      localStringBuilder.append(bool);
      return localStringBuilder.toString();
    }
  }
  
  @VisibleForTesting
  final class zza
    extends Thread
  {
    private WeakReference<com.google.android.gms.ads.identifier.AdvertisingIdClient> instance;
    private long lock;
    CountDownLatch ready;
    boolean value;
    
    public zza(long paramLong)
    {
      instance = new WeakReference(this$1);
      lock = paramLong;
      ready = new CountDownLatch(1);
      value = false;
      start();
    }
    
    private final void disconnect()
    {
      AdvertisingIdClient localAdvertisingIdClient = (AdvertisingIdClient)instance.get();
      if (localAdvertisingIdClient != null)
      {
        localAdvertisingIdClient.finish();
        value = true;
      }
    }
    
    public final void run()
    {
      CountDownLatch localCountDownLatch = ready;
      long l = lock;
      TimeUnit localTimeUnit = TimeUnit.MILLISECONDS;
      try
      {
        boolean bool = localCountDownLatch.await(l, localTimeUnit);
        if (bool) {
          return;
        }
        disconnect();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
      disconnect();
      return;
    }
  }
}
