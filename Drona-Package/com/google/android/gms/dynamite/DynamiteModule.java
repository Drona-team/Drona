package com.google.android.gms.dynamite;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.CrashUtils;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.ObjectWrapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.concurrent.GuardedBy;

@KeepForSdk
public final class DynamiteModule
{
  @KeepForSdk
  public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION = new MXParser();
  @KeepForSdk
  public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING = new UnsignedInteger();
  @KeepForSdk
  public static final VersionPolicy PREFER_HIGHEST_OR_REMOTE_VERSION = new SolidColor();
  @KeepForSdk
  public static final VersionPolicy PREFER_LOCAL;
  @KeepForSdk
  public static final VersionPolicy PREFER_REMOTE;
  @GuardedBy("DynamiteModule.class")
  private static Boolean zzif;
  @GuardedBy("DynamiteModule.class")
  private static Notification zzig;
  @GuardedBy("DynamiteModule.class")
  private static IMAXSIncomingFileTransferService zzih;
  @GuardedBy("DynamiteModule.class")
  private static String zzii;
  @GuardedBy("DynamiteModule.class")
  private static int zzij;
  private static final ThreadLocal<zza> zzik = new ThreadLocal();
  private static final DynamiteModule.VersionPolicy.zza zzil = new ByteArrayBuffer();
  private static final VersionPolicy zzim = new ConfigurationManager();
  private final Context zzin;
  
  static
  {
    PREFER_REMOTE = new Macro();
    PREFER_LOCAL = new NameFilter();
  }
  
  private DynamiteModule(Context paramContext)
  {
    zzin = ((Context)Preconditions.checkNotNull(paramContext));
  }
  
  /* Error */
  public static int create(Context paramContext, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 120	com/google/android/gms/dynamite/DynamiteModule:zzif	Ljava/lang/Boolean;
    //   6: astore 6
    //   8: aload 6
    //   10: astore 5
    //   12: aload 6
    //   14: ifnonnull +294 -> 308
    //   17: aload_0
    //   18: invokevirtual 124	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   21: invokevirtual 128	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   24: astore 5
    //   26: aload 5
    //   28: ldc 6
    //   30: invokevirtual 134	java/lang/Class:getName	()Ljava/lang/String;
    //   33: invokevirtual 140	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   36: astore 6
    //   38: aload 6
    //   40: ldc -114
    //   42: invokevirtual 146	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   45: astore 5
    //   47: aload 6
    //   49: monitorenter
    //   50: aload 5
    //   52: aconst_null
    //   53: invokevirtual 151	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   56: checkcast 136	java/lang/ClassLoader
    //   59: astore 7
    //   61: aload 7
    //   63: ifnull +32 -> 95
    //   66: aload 7
    //   68: invokestatic 154	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   71: if_acmpne +11 -> 82
    //   74: getstatic 159	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   77: astore 5
    //   79: goto +149 -> 228
    //   82: aload 7
    //   84: invokestatic 163	com/google/android/gms/dynamite/DynamiteModule:getInstance	(Ljava/lang/ClassLoader;)V
    //   87: getstatic 166	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   90: astore 5
    //   92: goto +136 -> 228
    //   95: ldc -88
    //   97: aload_0
    //   98: invokevirtual 124	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   101: invokevirtual 171	android/content/Context:getPackageName	()Ljava/lang/String;
    //   104: invokevirtual 177	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   107: ifeq +20 -> 127
    //   110: aload 5
    //   112: aconst_null
    //   113: invokestatic 154	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   116: invokevirtual 181	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   119: getstatic 159	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   122: astore 5
    //   124: goto +104 -> 228
    //   127: aload_0
    //   128: aload_1
    //   129: iload_2
    //   130: invokestatic 183	com/google/android/gms/dynamite/DynamiteModule:get	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   133: istore_3
    //   134: getstatic 185	com/google/android/gms/dynamite/DynamiteModule:zzii	Ljava/lang/String;
    //   137: ifnull +69 -> 206
    //   140: getstatic 185	com/google/android/gms/dynamite/DynamiteModule:zzii	Ljava/lang/String;
    //   143: astore 7
    //   145: aload 7
    //   147: invokevirtual 189	java/lang/String:isEmpty	()Z
    //   150: istore 4
    //   152: iload 4
    //   154: ifeq +6 -> 160
    //   157: goto +49 -> 206
    //   160: getstatic 185	com/google/android/gms/dynamite/DynamiteModule:zzii	Ljava/lang/String;
    //   163: astore 7
    //   165: new 191	com/google/android/gms/dynamite/Loader
    //   168: dup
    //   169: aload 7
    //   171: invokestatic 154	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   174: invokespecial 194	com/google/android/gms/dynamite/Loader:<init>	(Ljava/lang/String;Ljava/lang/ClassLoader;)V
    //   177: astore 7
    //   179: aload 7
    //   181: invokestatic 163	com/google/android/gms/dynamite/DynamiteModule:getInstance	(Ljava/lang/ClassLoader;)V
    //   184: aload 5
    //   186: aconst_null
    //   187: aload 7
    //   189: invokevirtual 181	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   192: getstatic 166	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   195: putstatic 120	com/google/android/gms/dynamite/DynamiteModule:zzif	Ljava/lang/Boolean;
    //   198: aload 6
    //   200: monitorexit
    //   201: ldc 2
    //   203: monitorexit
    //   204: iload_3
    //   205: ireturn
    //   206: aload 6
    //   208: monitorexit
    //   209: ldc 2
    //   211: monitorexit
    //   212: iload_3
    //   213: ireturn
    //   214: aload 5
    //   216: aconst_null
    //   217: invokestatic 154	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   220: invokevirtual 181	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   223: getstatic 159	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   226: astore 5
    //   228: aload 6
    //   230: monitorexit
    //   231: goto +72 -> 303
    //   234: astore 5
    //   236: aload 6
    //   238: monitorexit
    //   239: aload 5
    //   241: athrow
    //   242: astore 5
    //   244: aload 5
    //   246: invokestatic 198	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   249: astore 5
    //   251: new 200	java/lang/StringBuilder
    //   254: dup
    //   255: aload 5
    //   257: invokestatic 198	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   260: invokevirtual 204	java/lang/String:length	()I
    //   263: bipush 30
    //   265: iadd
    //   266: invokespecial 207	java/lang/StringBuilder:<init>	(I)V
    //   269: astore 6
    //   271: aload 6
    //   273: ldc -47
    //   275: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   278: pop
    //   279: aload 6
    //   281: aload 5
    //   283: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: pop
    //   287: ldc -41
    //   289: aload 6
    //   291: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   294: invokestatic 224	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   297: pop
    //   298: getstatic 159	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   301: astore 5
    //   303: aload 5
    //   305: putstatic 120	com/google/android/gms/dynamite/DynamiteModule:zzif	Ljava/lang/Boolean;
    //   308: ldc 2
    //   310: monitorexit
    //   311: aload 5
    //   313: invokevirtual 227	java/lang/Boolean:booleanValue	()Z
    //   316: istore 4
    //   318: iload 4
    //   320: ifeq +59 -> 379
    //   323: aload_0
    //   324: aload_1
    //   325: iload_2
    //   326: invokestatic 183	com/google/android/gms/dynamite/DynamiteModule:get	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   329: istore_3
    //   330: iload_3
    //   331: ireturn
    //   332: astore_1
    //   333: aload_1
    //   334: invokevirtual 232	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   337: invokestatic 198	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   340: astore_1
    //   341: aload_1
    //   342: invokevirtual 204	java/lang/String:length	()I
    //   345: istore_3
    //   346: iload_3
    //   347: ifeq +13 -> 360
    //   350: ldc -22
    //   352: aload_1
    //   353: invokevirtual 238	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   356: astore_1
    //   357: goto +13 -> 370
    //   360: new 173	java/lang/String
    //   363: dup
    //   364: ldc -22
    //   366: invokespecial 241	java/lang/String:<init>	(Ljava/lang/String;)V
    //   369: astore_1
    //   370: ldc -41
    //   372: aload_1
    //   373: invokestatic 224	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   376: pop
    //   377: iconst_0
    //   378: ireturn
    //   379: aload_0
    //   380: aload_1
    //   381: iload_2
    //   382: invokestatic 244	com/google/android/gms/dynamite/DynamiteModule:execute	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   385: istore_3
    //   386: iload_3
    //   387: ireturn
    //   388: astore_1
    //   389: ldc 2
    //   391: monitorexit
    //   392: aload_1
    //   393: athrow
    //   394: astore_1
    //   395: aload_0
    //   396: aload_1
    //   397: invokestatic 250	com/google/android/gms/common/util/CrashUtils:addDynamiteErrorToDropBox	(Landroid/content/Context;Ljava/lang/Throwable;)Z
    //   400: pop
    //   401: aload_1
    //   402: athrow
    //   403: astore 5
    //   405: goto -318 -> 87
    //   408: astore 7
    //   410: goto -196 -> 214
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	413	0	paramContext	Context
    //   0	413	1	paramString	String
    //   0	413	2	paramBoolean	boolean
    //   133	254	3	i	int
    //   150	169	4	bool	boolean
    //   10	217	5	localObject1	Object
    //   234	6	5	localThrowable	Throwable
    //   242	3	5	localClassNotFoundException	ClassNotFoundException
    //   249	63	5	localObject2	Object
    //   403	1	5	localLoadingException1	LoadingException
    //   6	284	6	localObject3	Object
    //   59	129	7	localObject4	Object
    //   408	1	7	localLoadingException2	LoadingException
    // Exception table:
    //   from	to	target	type
    //   50	61	234	java/lang/Throwable
    //   66	79	234	java/lang/Throwable
    //   82	87	234	java/lang/Throwable
    //   87	92	234	java/lang/Throwable
    //   95	124	234	java/lang/Throwable
    //   127	134	234	java/lang/Throwable
    //   134	145	234	java/lang/Throwable
    //   145	152	234	java/lang/Throwable
    //   160	165	234	java/lang/Throwable
    //   165	192	234	java/lang/Throwable
    //   192	201	234	java/lang/Throwable
    //   206	209	234	java/lang/Throwable
    //   214	228	234	java/lang/Throwable
    //   228	231	234	java/lang/Throwable
    //   236	239	234	java/lang/Throwable
    //   17	26	242	java/lang/ClassNotFoundException
    //   17	26	242	java/lang/IllegalAccessException
    //   17	26	242	java/lang/NoSuchFieldException
    //   26	47	242	java/lang/ClassNotFoundException
    //   26	47	242	java/lang/IllegalAccessException
    //   26	47	242	java/lang/NoSuchFieldException
    //   239	242	242	java/lang/ClassNotFoundException
    //   239	242	242	java/lang/IllegalAccessException
    //   239	242	242	java/lang/NoSuchFieldException
    //   323	330	332	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   3	8	388	java/lang/Throwable
    //   17	26	388	java/lang/Throwable
    //   26	47	388	java/lang/Throwable
    //   47	50	388	java/lang/Throwable
    //   201	204	388	java/lang/Throwable
    //   209	212	388	java/lang/Throwable
    //   239	242	388	java/lang/Throwable
    //   244	303	388	java/lang/Throwable
    //   303	308	388	java/lang/Throwable
    //   308	311	388	java/lang/Throwable
    //   389	392	388	java/lang/Throwable
    //   0	3	394	java/lang/Throwable
    //   311	318	394	java/lang/Throwable
    //   323	330	394	java/lang/Throwable
    //   333	346	394	java/lang/Throwable
    //   350	357	394	java/lang/Throwable
    //   360	370	394	java/lang/Throwable
    //   370	377	394	java/lang/Throwable
    //   379	386	394	java/lang/Throwable
    //   392	394	394	java/lang/Throwable
    //   82	87	403	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   127	134	408	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   145	152	408	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   165	192	408	com/google/android/gms/dynamite/DynamiteModule$LoadingException
  }
  
  private static int execute(Context paramContext, String paramString, boolean paramBoolean)
  {
    Notification localNotification = getInstance(paramContext);
    if (localNotification == null) {
      return 0;
    }
    try
    {
      int i = localNotification.zzak();
      if (i >= 2)
      {
        i = localNotification.setTime(ObjectWrapper.wrap(paramContext), paramString, paramBoolean);
        return i;
      }
      Log.w("DynamiteModule", "IDynamite loader version < 2, falling back to getModuleVersion2");
      i = localNotification.speak(ObjectWrapper.wrap(paramContext), paramString, paramBoolean);
      return i;
    }
    catch (RemoteException paramContext)
    {
      paramContext = String.valueOf(paramContext.getMessage());
      if (paramContext.length() != 0) {
        paramContext = "Failed to retrieve remote module version: ".concat(paramContext);
      } else {
        paramContext = new String("Failed to retrieve remote module version: ");
      }
      Log.w("DynamiteModule", paramContext);
    }
    return 0;
  }
  
  private static DynamiteModule execute(Context paramContext, String paramString, int paramInt)
    throws DynamiteModule.LoadingException
  {
    for (;;)
    {
      try
      {
        try
        {
          localObject = zzif;
          if (localObject == null) {}
        }
        catch (Throwable paramString)
        {
          Object localObject;
          boolean bool;
          int i;
          throw paramString;
        }
      }
      catch (Throwable paramString)
      {
        CrashUtils.addDynamiteErrorToDropBox(paramContext, paramString);
        throw new LoadingException("Failed to load remote module.", paramString, null);
      }
      try
      {
        bool = ((Boolean)localObject).booleanValue();
        if (bool)
        {
          paramString = get(paramContext, paramString, paramInt);
          return paramString;
        }
        i = String.valueOf(paramString).length();
        localObject = new StringBuilder(i + 51);
        ((StringBuilder)localObject).append("Selected remote version of ");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append(", version >= ");
        ((StringBuilder)localObject).append(paramInt);
        Log.i("DynamiteModule", ((StringBuilder)localObject).toString());
        localObject = getInstance(paramContext);
        if (localObject != null)
        {
          i = ((Notification)localObject).zzak();
          if (i >= 2)
          {
            paramString = ((Notification)localObject).setTime(ObjectWrapper.wrap(paramContext), paramString, paramInt);
          }
          else
          {
            Log.w("DynamiteModule", "Dynamite loader version < 2, falling back to createModuleContext");
            paramString = ((Notification)localObject).getChat(ObjectWrapper.wrap(paramContext), paramString, paramInt);
          }
          localObject = ObjectWrapper.unwrap(paramString);
          if (localObject != null)
          {
            paramString = ObjectWrapper.unwrap(paramString);
            paramString = (Context)paramString;
            paramString = new DynamiteModule(paramString);
            return paramString;
          }
          paramString = new LoadingException("Failed to load remote module.", null);
          throw paramString;
        }
        paramString = new LoadingException("Failed to create IDynamiteLoader.", null);
        throw paramString;
      }
      catch (LoadingException paramContext)
      {
        throw paramContext;
      }
      catch (RemoteException paramContext)
      {
        throw new LoadingException("Failed to load remote module.", paramContext, null);
      }
    }
    paramString = new LoadingException("Failed to determine which loading route to use.", null);
    throw paramString;
  }
  
  private static int get(Context paramContext, String paramString, boolean paramBoolean)
    throws DynamiteModule.LoadingException
  {
    Object localObject1 = null;
    int i;
    try
    {
      Object localObject2 = paramContext.getContentResolver();
      if (paramBoolean) {
        paramContext = "api_force_staging";
      } else {
        paramContext = "api";
      }
      i = String.valueOf(paramContext).length();
      int j = String.valueOf(paramString).length();
      Object localObject3 = new StringBuilder(i + 42 + j);
      ((StringBuilder)localObject3).append("content://com.google.android.gms.chimera/");
      ((StringBuilder)localObject3).append(paramContext);
      ((StringBuilder)localObject3).append("/");
      ((StringBuilder)localObject3).append(paramString);
      paramContext = ((ContentResolver)localObject2).query(Uri.parse(((StringBuilder)localObject3).toString()), null, null, null, null);
      paramString = paramContext;
      if (paramContext != null) {
        try
        {
          paramBoolean = paramContext.moveToFirst();
          if (paramBoolean)
          {
            i = paramContext.getInt(0);
            localObject1 = paramString;
            if (i > 0) {
              try
              {
                zzii = paramContext.getString(2);
                j = paramContext.getColumnIndex("loaderVersion");
                if (j >= 0) {
                  zzij = paramContext.getInt(j);
                }
                localObject1 = zzik;
                localObject1 = ((ThreadLocal)localObject1).get();
                localObject2 = (zza)localObject1;
                localObject1 = paramString;
                if (localObject2 != null)
                {
                  localObject3 = zzio;
                  localObject1 = paramString;
                  if (localObject3 == null)
                  {
                    zzio = paramContext;
                    localObject1 = null;
                  }
                }
              }
              catch (Throwable paramString)
              {
                throw paramString;
              }
            }
            if (localObject1 == null) {
              return i;
            }
            ((Cursor)localObject1).close();
            return i;
          }
        }
        catch (Throwable paramString)
        {
          break label322;
        }
        catch (Exception paramString) {}
      }
      Log.w("DynamiteModule", "Failed to retrieve remote module version.");
      paramString = new LoadingException("Failed to connect to dynamite module ContentResolver.", null);
      throw paramString;
    }
    catch (Throwable paramString)
    {
      paramContext = (Context)localObject1;
    }
    catch (Exception paramString)
    {
      paramContext = null;
      try
      {
        paramBoolean = paramString instanceof LoadingException;
        if (paramBoolean) {
          throw paramString;
        }
        throw new LoadingException("V2 version check failed", paramString, null);
      }
      catch (Throwable paramString) {}
    }
    label322:
    if (paramContext != null) {
      paramContext.close();
    }
    throw paramString;
    return i;
  }
  
  private static DynamiteModule get(Context paramContext, String paramString, int paramInt)
    throws DynamiteModule.LoadingException, RemoteException
  {
    Object localObject1 = new StringBuilder(String.valueOf(paramString).length() + 51);
    ((StringBuilder)localObject1).append("Selected remote version of ");
    ((StringBuilder)localObject1).append(paramString);
    ((StringBuilder)localObject1).append(", version >= ");
    ((StringBuilder)localObject1).append(paramInt);
    Log.i("DynamiteModule", ((StringBuilder)localObject1).toString());
    try
    {
      localObject1 = zzih;
      if (localObject1 != null)
      {
        Object localObject2 = (zza)zzik.get();
        if ((localObject2 != null) && (zzio != null))
        {
          paramContext = paramContext.getApplicationContext();
          localObject2 = zzio;
          ObjectWrapper.wrap(null);
          if (zzaj().booleanValue())
          {
            Log.v("DynamiteModule", "Dynamite loader version >= 2, using loadModule2NoCrashUtils");
            paramContext = ((IMAXSIncomingFileTransferService)localObject1).getChat(ObjectWrapper.wrap(paramContext), paramString, paramInt, ObjectWrapper.wrap(localObject2));
          }
          else
          {
            Log.w("DynamiteModule", "Dynamite loader version < 2, falling back to loadModule2");
            paramContext = ((IMAXSIncomingFileTransferService)localObject1).createChat(ObjectWrapper.wrap(paramContext), paramString, paramInt, ObjectWrapper.wrap(localObject2));
          }
          paramContext = (Context)ObjectWrapper.unwrap(paramContext);
          if (paramContext != null) {
            return new DynamiteModule(paramContext);
          }
          throw new LoadingException("Failed to get module context", null);
        }
        throw new LoadingException("No result cursor", null);
      }
      throw new LoadingException("DynamiteLoaderV2 was not cached.", null);
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  /* Error */
  private static Notification getInstance(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 405	com/google/android/gms/dynamite/DynamiteModule:zzig	Lcom/google/android/gms/dynamite/Notification;
    //   6: ifnull +12 -> 18
    //   9: getstatic 405	com/google/android/gms/dynamite/DynamiteModule:zzig	Lcom/google/android/gms/dynamite/Notification;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: invokestatic 410	com/google/android/gms/common/GoogleApiAvailabilityLight:getInstance	()Lcom/google/android/gms/common/GoogleApiAvailabilityLight;
    //   21: aload_0
    //   22: invokevirtual 414	com/google/android/gms/common/GoogleApiAvailabilityLight:isGooglePlayServicesAvailable	(Landroid/content/Context;)I
    //   25: ifeq +8 -> 33
    //   28: ldc 2
    //   30: monitorexit
    //   31: aconst_null
    //   32: areturn
    //   33: aload_0
    //   34: ldc -88
    //   36: iconst_3
    //   37: invokevirtual 418	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   40: invokevirtual 128	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   43: ldc_w 420
    //   46: invokevirtual 140	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   49: invokevirtual 423	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   52: astore_0
    //   53: aload_0
    //   54: checkcast 425	android/os/IBinder
    //   57: astore_0
    //   58: aload_0
    //   59: ifnonnull +8 -> 67
    //   62: aconst_null
    //   63: astore_0
    //   64: goto +37 -> 101
    //   67: aload_0
    //   68: ldc_w 427
    //   71: invokeinterface 431 2 0
    //   76: astore_1
    //   77: aload_1
    //   78: instanceof 257
    //   81: ifeq +11 -> 92
    //   84: aload_1
    //   85: checkcast 257	com/google/android/gms/dynamite/Notification
    //   88: astore_0
    //   89: goto +12 -> 101
    //   92: new 433	com/google/android/gms/dynamite/Proxy
    //   95: dup
    //   96: aload_0
    //   97: invokespecial 436	com/google/android/gms/dynamite/Proxy:<init>	(Landroid/os/IBinder;)V
    //   100: astore_0
    //   101: aload_0
    //   102: ifnull +57 -> 159
    //   105: aload_0
    //   106: putstatic 405	com/google/android/gms/dynamite/DynamiteModule:zzig	Lcom/google/android/gms/dynamite/Notification;
    //   109: ldc 2
    //   111: monitorexit
    //   112: aload_0
    //   113: areturn
    //   114: astore_0
    //   115: aload_0
    //   116: invokevirtual 232	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   119: invokestatic 198	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   122: astore_0
    //   123: aload_0
    //   124: invokevirtual 204	java/lang/String:length	()I
    //   127: ifeq +14 -> 141
    //   130: ldc_w 438
    //   133: aload_0
    //   134: invokevirtual 238	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   137: astore_0
    //   138: goto +14 -> 152
    //   141: new 173	java/lang/String
    //   144: dup
    //   145: ldc_w 438
    //   148: invokespecial 241	java/lang/String:<init>	(Ljava/lang/String;)V
    //   151: astore_0
    //   152: ldc -41
    //   154: aload_0
    //   155: invokestatic 441	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   158: pop
    //   159: ldc 2
    //   161: monitorexit
    //   162: aconst_null
    //   163: areturn
    //   164: astore_0
    //   165: ldc 2
    //   167: monitorexit
    //   168: aload_0
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	paramContext	Context
    //   76	9	1	localIInterface	IInterface
    // Exception table:
    //   from	to	target	type
    //   33	53	114	java/lang/Exception
    //   67	77	114	java/lang/Exception
    //   92	101	114	java/lang/Exception
    //   3	16	164	java/lang/Throwable
    //   18	31	164	java/lang/Throwable
    //   33	53	164	java/lang/Throwable
    //   53	58	164	java/lang/Throwable
    //   67	77	164	java/lang/Throwable
    //   77	89	164	java/lang/Throwable
    //   92	101	164	java/lang/Throwable
    //   105	112	164	java/lang/Throwable
    //   115	138	164	java/lang/Throwable
    //   141	152	164	java/lang/Throwable
    //   152	159	164	java/lang/Throwable
    //   159	162	164	java/lang/Throwable
    //   165	168	164	java/lang/Throwable
  }
  
  private static void getInstance(ClassLoader paramClassLoader)
    throws DynamiteModule.LoadingException
  {
    try
    {
      paramClassLoader = paramClassLoader.loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2");
      paramClassLoader = paramClassLoader.getConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      paramClassLoader = (IBinder)paramClassLoader;
      if (paramClassLoader == null)
      {
        paramClassLoader = null;
      }
      else
      {
        IInterface localIInterface = paramClassLoader.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoaderV2");
        if ((localIInterface instanceof IMAXSIncomingFileTransferService)) {
          paramClassLoader = (IMAXSIncomingFileTransferService)localIInterface;
        } else {
          paramClassLoader = new IMAXSIncomingFileTransferService.Stub(paramClassLoader);
        }
      }
      zzih = paramClassLoader;
      return;
    }
    catch (ClassNotFoundException|IllegalAccessException|InstantiationException|InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new LoadingException("Failed to instantiate dynamite loader", paramClassLoader, null);
    }
  }
  
  public static int getLocalVersion(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getApplicationContext().getClassLoader();
      int i = String.valueOf(paramString).length();
      Object localObject = new StringBuilder(i + 61);
      ((StringBuilder)localObject).append("com.google.android.gms.dynamite.descriptors.");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(".ModuleDescriptor");
      localObject = paramContext.loadClass(((StringBuilder)localObject).toString());
      paramContext = ((Class)localObject).getDeclaredField("MODULE_ID");
      localObject = ((Class)localObject).getDeclaredField("MODULE_VERSION");
      boolean bool = paramContext.get(null).equals(paramString);
      if (!bool)
      {
        paramContext = String.valueOf(paramContext.get(null));
        i = String.valueOf(paramContext).length();
        int j = String.valueOf(paramString).length();
        localObject = new StringBuilder(i + 51 + j);
        ((StringBuilder)localObject).append("Module descriptor id '");
        ((StringBuilder)localObject).append(paramContext);
        ((StringBuilder)localObject).append("' didn't match expected id '");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("'");
        Log.e("DynamiteModule", ((StringBuilder)localObject).toString());
        return 0;
      }
      i = ((Field)localObject).getInt(null);
      return i;
    }
    catch (Exception paramContext)
    {
      paramContext = String.valueOf(paramContext.getMessage());
      if (paramContext.length() != 0) {
        paramContext = "Failed to load module descriptor class: ".concat(paramContext);
      } else {
        paramContext = new String("Failed to load module descriptor class: ");
      }
      Log.e("DynamiteModule", paramContext);
      return 0;
      paramContext = new StringBuilder(String.valueOf(paramString).length() + 45);
      paramContext.append("Local module descriptor class for ");
      paramContext.append(paramString);
      paramContext.append(" not found.");
      Log.w("DynamiteModule", paramContext.toString());
      return 0;
    }
    catch (ClassNotFoundException paramContext)
    {
      for (;;) {}
    }
  }
  
  public static int getRemoteVersion(Context paramContext, String paramString)
  {
    return create(paramContext, paramString, false);
  }
  
  public static DynamiteModule load(Context paramContext, VersionPolicy paramVersionPolicy, String paramString)
    throws DynamiteModule.LoadingException
  {
    zza localZza1 = (zza)zzik.get();
    zza localZza2 = new zza(null);
    zzik.set(localZza2);
    try
    {
      DynamiteModule.VersionPolicy.zzb localZzb = paramVersionPolicy.getValue(paramContext, paramString, zzil);
      int i = zzir;
      int j = zzis;
      int k = String.valueOf(paramString).length();
      int m = String.valueOf(paramString).length();
      Object localObject = new StringBuilder(k + 68 + m);
      ((StringBuilder)localObject).append("Considering local module ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(":");
      ((StringBuilder)localObject).append(i);
      ((StringBuilder)localObject).append(" and remote module ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(":");
      ((StringBuilder)localObject).append(j);
      Log.i("DynamiteModule", ((StringBuilder)localObject).toString());
      i = zzit;
      if (i != 0)
      {
        i = zzit;
        if (i == -1)
        {
          i = zzir;
          if (i == 0) {}
        }
        else
        {
          i = zzit;
          if (i == 1)
          {
            i = zzis;
            if (i == 0) {}
          }
          else
          {
            i = zzit;
            if (i == -1)
            {
              paramContext = loadData(paramContext, paramString);
              if (zzio != null) {
                zzio.close();
              }
              zzik.set(localZza1);
              return paramContext;
            }
            i = zzit;
            if (i == 1)
            {
              i = zzis;
              try
              {
                localObject = execute(paramContext, paramString, i);
                if (zzio != null) {
                  zzio.close();
                }
                zzik.set(localZza1);
                return localObject;
              }
              catch (LoadingException localLoadingException)
              {
                localObject = String.valueOf(localLoadingException.getMessage());
                i = ((String)localObject).length();
                if (i != 0) {
                  localObject = "Failed to load remote module: ".concat((String)localObject);
                } else {
                  localObject = new String("Failed to load remote module: ");
                }
                Log.w("DynamiteModule", (String)localObject);
                i = zzir;
                if (i != 0)
                {
                  i = getValuezzbzzir, 0)).zzit;
                  if (i == -1)
                  {
                    paramContext = loadData(paramContext, paramString);
                    if (zzio != null) {
                      zzio.close();
                    }
                    zzik.set(localZza1);
                    return paramContext;
                  }
                }
                throw new LoadingException("Remote load failed. No local fallback found.", localLoadingException, null);
              }
            }
            i = zzit;
            paramContext = new StringBuilder(47);
            paramContext.append("VersionPolicy returned invalid code:");
            paramContext.append(i);
            throw new LoadingException(paramContext.toString(), null);
          }
        }
      }
      i = zzir;
      j = zzis;
      paramContext = new StringBuilder(91);
      paramContext.append("No acceptable module found. Local version is ");
      paramContext.append(i);
      paramContext.append(" and remote version is ");
      paramContext.append(j);
      paramContext.append(".");
      throw new LoadingException(paramContext.toString(), null);
    }
    catch (Throwable paramContext)
    {
      if (zzio != null) {
        zzio.close();
      }
      zzik.set(localZza1);
      throw paramContext;
    }
  }
  
  private static DynamiteModule loadData(Context paramContext, String paramString)
  {
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {
      paramString = "Selected local version of ".concat(paramString);
    } else {
      paramString = new String("Selected local version of ");
    }
    Log.i("DynamiteModule", paramString);
    return new DynamiteModule(paramContext.getApplicationContext());
  }
  
  private static Boolean zzaj()
  {
    for (;;)
    {
      try
      {
        if (zzij >= 2)
        {
          bool = true;
          return Boolean.valueOf(bool);
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public final Context getModuleContext()
  {
    return zzin;
  }
  
  public final IBinder instantiate(String paramString)
    throws DynamiteModule.LoadingException
  {
    Object localObject = zzin;
    try
    {
      localObject = ((Context)localObject).getClassLoader().loadClass(paramString).newInstance();
      return (IBinder)localObject;
    }
    catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
    {
      paramString = String.valueOf(paramString);
      if (paramString.length() != 0) {
        paramString = "Failed to instantiate module class: ".concat(paramString);
      } else {
        paramString = new String("Failed to instantiate module class: ");
      }
      throw new LoadingException(paramString, localClassNotFoundException, null);
    }
  }
  
  @DynamiteApi
  public static class DynamiteLoaderClassLoader
  {
    @GuardedBy("DynamiteLoaderClassLoader.class")
    public static ClassLoader sClassLoader;
    
    public DynamiteLoaderClassLoader() {}
  }
  
  @KeepForSdk
  public static class LoadingException
    extends Exception
  {
    private LoadingException(String paramString)
    {
      super();
    }
    
    private LoadingException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  public static abstract interface VersionPolicy
  {
    public abstract zzb getValue(Context paramContext, String paramString, zza paramZza)
      throws DynamiteModule.LoadingException;
    
    public static abstract interface zza
    {
      public abstract int get(Context paramContext, String paramString, boolean paramBoolean)
        throws DynamiteModule.LoadingException;
      
      public abstract int getLocalVersion(Context paramContext, String paramString);
    }
    
    public static final class zzb
    {
      public int zzir = 0;
      public int zzis = 0;
      public int zzit = 0;
      
      public zzb() {}
    }
  }
  
  private static final class zza
  {
    public Cursor zzio;
    
    private zza() {}
  }
  
  private static final class zzb
    implements DynamiteModule.VersionPolicy.zza
  {
    private final int zzip;
    private final int zziq;
    
    public zzb(int paramInt1, int paramInt2)
    {
      zzip = paramInt1;
      zziq = 0;
    }
    
    public final int get(Context paramContext, String paramString, boolean paramBoolean)
    {
      return 0;
    }
    
    public final int getLocalVersion(Context paramContext, String paramString)
    {
      return zzip;
    }
  }
}
