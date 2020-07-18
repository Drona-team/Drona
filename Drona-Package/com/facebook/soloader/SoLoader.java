package com.facebook.soloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.BaseDexClassLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SoLoader
{
  static final boolean DEBUG = false;
  static final String PAGE_KEY = "SoLoader";
  public static final int SOLOADER_ALLOW_ASYNC_INIT = 2;
  public static final int SOLOADER_DISABLE_BACKUP_SOSOURCE = 8;
  public static final int SOLOADER_ENABLE_EXOPACKAGE = 1;
  public static final int SOLOADER_LOOK_IN_ZIP = 4;
  private static final String SO_STORE_NAME_MAIN = "lib-main";
  private static final String SO_STORE_NAME_SPLIT = "lib-";
  static final boolean SYSTRACE_LIBRARY_LOADING;
  @Nullable
  @GuardedBy("sSoSourcesLock")
  private static ApplicationSoSource sApplicationSoSource;
  @Nullable
  @GuardedBy("sSoSourcesLock")
  private static UnpackingSoSource[] sBackupSoSources;
  @GuardedBy("sSoSourcesLock")
  private static int sFlags;
  private static final Set<String> sLoadedAndMergedLibraries;
  @GuardedBy("SoLoader.class")
  private static final HashSet<String> sLoadedLibraries;
  @GuardedBy("SoLoader.class")
  private static final Map<String, Object> sLoadingLibraries;
  @Nullable
  static SoFileLoader sSoFileLoader;
  @Nullable
  @GuardedBy("sSoSourcesLock")
  private static SoSource[] sSoSources;
  private static final ReentrantReadWriteLock sSoSourcesLock = new ReentrantReadWriteLock();
  private static int sSoSourcesVersion;
  @Nullable
  private static SystemLoadLibraryWrapper sSystemLoadLibraryWrapper;
  
  static
  {
    sSoSources = null;
    boolean bool = false;
    sSoSourcesVersion = 0;
    sLoadedLibraries = new HashSet();
    sLoadingLibraries = new HashMap();
    sLoadedAndMergedLibraries = Collections.newSetFromMap(new ConcurrentHashMap());
    sSystemLoadLibraryWrapper = null;
    try
    {
      int i = Build.VERSION.SDK_INT;
      if (i >= 18) {
        bool = true;
      }
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
      for (;;) {}
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      for (;;) {}
    }
    SYSTRACE_LIBRARY_LOADING = bool;
  }
  
  public SoLoader() {}
  
  public static boolean areSoSourcesAbisSupported()
  {
    sSoSourcesLock.readLock().lock();
    try
    {
      Object localObject1 = sSoSources;
      if (localObject1 == null)
      {
        sSoSourcesLock.readLock().unlock();
        return false;
      }
      Object localObject2 = SysUtil.getSupportedAbis();
      int i = 0;
      for (;;)
      {
        int j = sSoSources.length;
        if (i >= j) {
          break;
        }
        localObject1 = sSoSources[i].getSoSourceAbis();
        j = 0;
        for (;;)
        {
          int k = localObject1.length;
          if (j >= k) {
            break;
          }
          k = 0;
          boolean bool = false;
          for (;;)
          {
            int m = localObject2.length;
            if ((k >= m) || (bool)) {
              break;
            }
            bool = localObject1[j].equals(localObject2[k]);
            k += 1;
          }
          if (!bool)
          {
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("abi not supported: ");
            ((StringBuilder)localObject2).append(localObject1[j]);
            Log.e("SoLoader", ((StringBuilder)localObject2).toString());
            sSoSourcesLock.readLock().unlock();
            return false;
          }
          j += 1;
        }
        i += 1;
      }
      sSoSourcesLock.readLock().unlock();
      return true;
    }
    catch (Throwable localThrowable)
    {
      sSoSourcesLock.readLock().unlock();
      throw localThrowable;
    }
  }
  
  private static void assertInitialized()
  {
    sSoSourcesLock.readLock().lock();
    try
    {
      SoSource[] arrayOfSoSource = sSoSources;
      if (arrayOfSoSource != null)
      {
        sSoSourcesLock.readLock().unlock();
        return;
      }
      throw new RuntimeException("SoLoader.init() not yet called");
    }
    catch (Throwable localThrowable)
    {
      sSoSourcesLock.readLock().unlock();
      throw localThrowable;
    }
  }
  
  public static void deinitForTest()
  {
    setSoSources(null);
  }
  
  private static void doLoadLibraryBySoName(String paramString, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
    throws IOException
  {
    sSoSourcesLock.readLock().lock();
    try
    {
      Object localObject = sSoSources;
      if (localObject != null)
      {
        sSoSourcesLock.readLock().unlock();
        int m;
        if (paramThreadPolicy == null)
        {
          paramThreadPolicy = StrictMode.allowThreadDiskReads();
          m = 1;
        }
        else
        {
          m = 0;
        }
        if (SYSTRACE_LIBRARY_LOADING)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("SoLoader.loadLibrary[");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append("]");
          Api18TraceUtils.beginTraceSection(((StringBuilder)localObject).toString());
        }
        int j = 0;
        for (;;)
        {
          int k = j;
          try
          {
            sSoSourcesLock.readLock().lock();
            k = j;
            int i1 = sSoSourcesVersion;
            int n = 0;
            int i;
            for (;;)
            {
              i = j;
              if (j == 0) {
                try
                {
                  k = sSoSources.length;
                  i = j;
                  if (n < k)
                  {
                    i = sSoSources[n].loadLibrary(paramString, paramInt, paramThreadPolicy);
                    k = i;
                    if (i == 3) {
                      try
                      {
                        localObject = sBackupSoSources;
                        if (localObject != null)
                        {
                          localObject = new StringBuilder();
                          ((StringBuilder)localObject).append("Trying backup SoSource for ");
                          ((StringBuilder)localObject).append(paramString);
                          Log.d("SoLoader", ((StringBuilder)localObject).toString());
                          localObject = sBackupSoSources;
                          int i2 = localObject.length;
                          n = 0;
                          for (;;)
                          {
                            j = k;
                            if (n >= i2) {
                              break;
                            }
                            str = localObject[n];
                            str.prepare(paramString);
                            j = str.loadLibrary(paramString, paramInt, paramThreadPolicy);
                            if (j == 1) {
                              break;
                            }
                            n += 1;
                          }
                          i = j;
                        }
                      }
                      catch (Throwable localThrowable1)
                      {
                        j = i;
                        break label302;
                      }
                    }
                    n += 1;
                    j = i;
                  }
                }
                catch (Throwable localThrowable2)
                {
                  label302:
                  k = j;
                  sSoSourcesLock.readLock().unlock();
                  k = j;
                  throw localThrowable2;
                }
              }
            }
            k = i;
            sSoSourcesLock.readLock().unlock();
            if (((paramInt & 0x2) == 2) && (i == 0))
            {
              k = i;
              sSoSourcesLock.writeLock().lock();
              try
              {
                ApplicationSoSource localApplicationSoSource = sApplicationSoSource;
                if (localApplicationSoSource != null)
                {
                  boolean bool = sApplicationSoSource.checkAndMaybeUpdate();
                  if (bool)
                  {
                    j = sSoSourcesVersion;
                    sSoSourcesVersion = j + 1;
                  }
                }
                j = sSoSourcesVersion;
                if (j != i1) {
                  j = 1;
                } else {
                  j = 0;
                }
                k = i;
                sSoSourcesLock.writeLock().unlock();
                k = j;
              }
              catch (Throwable localThrowable3)
              {
                k = i;
                sSoSourcesLock.writeLock().unlock();
                k = i;
                throw localThrowable3;
              }
            }
            k = 0;
            j = i;
            if (k == 0)
            {
              if (SYSTRACE_LIBRARY_LOADING) {
                Api18TraceUtils.endSection();
              }
              if (m != 0) {
                StrictMode.setThreadPolicy(paramThreadPolicy);
              }
              if ((i == 0) || (i == 3))
              {
                paramThreadPolicy = new StringBuilder();
                paramThreadPolicy.append("couldn't find DSO to load: ");
                paramThreadPolicy.append(paramString);
                paramString = paramThreadPolicy.toString();
                Log.e("SoLoader", paramString);
                throw new UnsatisfiedLinkError(paramString);
              }
            }
          }
          catch (Throwable localThrowable4)
          {
            if (SYSTRACE_LIBRARY_LOADING) {
              Api18TraceUtils.endSection();
            }
            if (m != 0) {
              StrictMode.setThreadPolicy(paramThreadPolicy);
            }
            if (k != 0)
            {
              if (k != 3) {
                return;
              }
              break label567;
              return;
            }
            label567:
            paramThreadPolicy = new StringBuilder();
            paramThreadPolicy.append("couldn't find DSO to load: ");
            paramThreadPolicy.append(paramString);
            String str = paramThreadPolicy.toString();
            paramThreadPolicy = localThrowable4.getMessage();
            paramString = paramThreadPolicy;
            if (paramThreadPolicy == null) {
              paramString = localThrowable4.toString();
            }
            paramThreadPolicy = new StringBuilder();
            paramThreadPolicy.append(str);
            paramThreadPolicy.append(" caused by: ");
            paramThreadPolicy.append(paramString);
            paramString = paramThreadPolicy.toString();
            Log.e("SoLoader", paramString);
            throw new UnsatisfiedLinkError(paramString);
          }
        }
      }
      paramThreadPolicy = new StringBuilder();
      paramThreadPolicy.append("Could not load: ");
      paramThreadPolicy.append(paramString);
      paramThreadPolicy.append(" because no SO source exists");
      Log.e("SoLoader", paramThreadPolicy.toString());
      paramThreadPolicy = new StringBuilder();
      paramThreadPolicy.append("couldn't find DSO to load: ");
      paramThreadPolicy.append(paramString);
      throw new UnsatisfiedLinkError(paramThreadPolicy.toString());
    }
    catch (Throwable paramString)
    {
      sSoSourcesLock.readLock().unlock();
      throw paramString;
    }
  }
  
  private static Method getNativeLoadRuntimeMethod()
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      if (Build.VERSION.SDK_INT > 27) {
        return null;
      }
      try
      {
        Method localMethod = Runtime.class.getDeclaredMethod("nativeLoad", new Class[] { String.class, ClassLoader.class, String.class });
        localMethod.setAccessible(true);
        return localMethod;
      }
      catch (NoSuchMethodException|SecurityException localNoSuchMethodException)
      {
        Log.w("SoLoader", "Cannot get nativeLoad method", localNoSuchMethodException);
      }
    }
    return null;
  }
  
  public static void init(Context paramContext, int paramInt)
    throws IOException
  {
    init(paramContext, paramInt, null);
  }
  
  private static void init(Context paramContext, int paramInt, SoFileLoader paramSoFileLoader)
    throws IOException
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskWrites();
    try
    {
      initSoLoader(paramSoFileLoader);
      initSoSources(paramContext, paramInt, paramSoFileLoader);
      StrictMode.setThreadPolicy(localThreadPolicy);
      return;
    }
    catch (Throwable paramContext)
    {
      StrictMode.setThreadPolicy(localThreadPolicy);
      throw paramContext;
    }
  }
  
  public static void init(Context paramContext, boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  private static void initSoLoader(final SoFileLoader paramSoFileLoader)
  {
    final Runtime localRuntime;
    final Method localMethod;
    boolean bool;
    if (paramSoFileLoader != null)
    {
      try
      {
        sSoFileLoader = paramSoFileLoader;
        return;
      }
      catch (Throwable paramSoFileLoader) {}
    }
    else
    {
      localRuntime = Runtime.getRuntime();
      localMethod = getNativeLoadRuntimeMethod();
      if (localMethod == null) {
        break label74;
      }
      bool = true;
      if (!bool) {
        break label79;
      }
      paramSoFileLoader = Api14Utils.getClassLoaderLdLoadLibrary();
    }
    for (;;)
    {
      sSoFileLoader = new SoFileLoader()
      {
        /* Error */
        private String getLibHash(String paramAnonymousString)
        {
          // Byte code:
          //   0: new 48	java/io/File
          //   3: dup
          //   4: aload_1
          //   5: invokespecial 51	java/io/File:<init>	(Ljava/lang/String;)V
          //   8: astore_1
          //   9: ldc 53
          //   11: invokestatic 59	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
          //   14: astore_3
          //   15: new 61	java/io/FileInputStream
          //   18: dup
          //   19: aload_1
          //   20: invokespecial 64	java/io/FileInputStream:<init>	(Ljava/io/File;)V
          //   23: astore_1
          //   24: sipush 4096
          //   27: newarray byte
          //   29: astore 4
          //   31: aload_1
          //   32: aload 4
          //   34: invokevirtual 70	java/io/InputStream:read	([B)I
          //   37: istore_2
          //   38: iload_2
          //   39: ifle +14 -> 53
          //   42: aload_3
          //   43: aload 4
          //   45: iconst_0
          //   46: iload_2
          //   47: invokevirtual 74	java/security/MessageDigest:update	([BII)V
          //   50: goto -19 -> 31
          //   53: ldc 76
          //   55: iconst_1
          //   56: anewarray 4	java/lang/Object
          //   59: dup
          //   60: iconst_0
          //   61: new 78	java/math/BigInteger
          //   64: dup
          //   65: iconst_1
          //   66: aload_3
          //   67: invokevirtual 82	java/security/MessageDigest:digest	()[B
          //   70: invokespecial 85	java/math/BigInteger:<init>	(I[B)V
          //   73: aastore
          //   74: invokestatic 91	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
          //   77: astore_3
          //   78: aload_1
          //   79: invokevirtual 94	java/io/InputStream:close	()V
          //   82: aload_3
          //   83: areturn
          //   84: astore_3
          //   85: goto +8 -> 93
          //   88: astore 4
          //   90: aload 4
          //   92: athrow
          //   93: aload 4
          //   95: ifnull +20 -> 115
          //   98: aload_1
          //   99: invokevirtual 94	java/io/InputStream:close	()V
          //   102: goto +17 -> 119
          //   105: astore_1
          //   106: aload 4
          //   108: aload_1
          //   109: invokevirtual 98	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
          //   112: goto +7 -> 119
          //   115: aload_1
          //   116: invokevirtual 94	java/io/InputStream:close	()V
          //   119: aload_3
          //   120: athrow
          //   121: astore_1
          //   122: aload_1
          //   123: checkcast 44	java/security/NoSuchAlgorithmException
          //   126: invokevirtual 102	java/security/NoSuchAlgorithmException:toString	()Ljava/lang/String;
          //   129: areturn
          //   130: astore_1
          //   131: aload_1
          //   132: checkcast 42	java/lang/SecurityException
          //   135: invokevirtual 103	java/lang/SecurityException:toString	()Ljava/lang/String;
          //   138: areturn
          //   139: astore_1
          //   140: aload_1
          //   141: checkcast 40	java/io/IOException
          //   144: invokevirtual 104	java/io/IOException:toString	()Ljava/lang/String;
          //   147: areturn
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	148	0	this	1
          //   0	148	1	paramAnonymousString	String
          //   37	10	2	i	int
          //   14	69	3	localObject	Object
          //   84	36	3	localThrowable1	Throwable
          //   29	15	4	arrayOfByte	byte[]
          //   88	19	4	localThrowable2	Throwable
          // Exception table:
          //   from	to	target	type
          //   90	93	84	java/lang/Throwable
          //   24	31	88	java/lang/Throwable
          //   31	38	88	java/lang/Throwable
          //   42	50	88	java/lang/Throwable
          //   53	78	88	java/lang/Throwable
          //   98	102	105	java/lang/Throwable
          //   0	15	121	java/security/NoSuchAlgorithmException
          //   15	24	121	java/security/NoSuchAlgorithmException
          //   78	82	121	java/security/NoSuchAlgorithmException
          //   106	112	121	java/security/NoSuchAlgorithmException
          //   115	119	121	java/security/NoSuchAlgorithmException
          //   119	121	121	java/security/NoSuchAlgorithmException
          //   0	15	130	java/lang/SecurityException
          //   15	24	130	java/lang/SecurityException
          //   78	82	130	java/lang/SecurityException
          //   106	112	130	java/lang/SecurityException
          //   115	119	130	java/lang/SecurityException
          //   119	121	130	java/lang/SecurityException
          //   0	15	139	java/io/IOException
          //   15	24	139	java/io/IOException
          //   78	82	139	java/io/IOException
          //   106	112	139	java/io/IOException
          //   115	119	139	java/io/IOException
          //   119	121	139	java/io/IOException
        }
        
        public void load(String paramAnonymousString, int paramAnonymousInt)
        {
          if (val$hasNativeLoadMethod)
          {
            if ((paramAnonymousInt & 0x4) == 4) {
              paramAnonymousInt = 1;
            } else {
              paramAnonymousInt = 0;
            }
            Object localObject1;
            if (paramAnonymousInt != 0) {
              localObject1 = paramSoFileLoader;
            } else {
              localObject1 = val$localLdLibraryPathNoZips;
            }
            Object localObject6 = null;
            Object localObject4 = null;
            Object localObject3 = localObject1;
            Object localObject2 = localObject6;
            try
            {
              Object localObject7 = localRuntime;
              localObject3 = localObject1;
              localObject2 = localObject6;
              localObject2 = localObject1;
              localObject3 = localObject4;
              try
              {
                localObject4 = (String)localMethod.invoke(localRuntime, new Object[] { paramAnonymousString, SoLoader.class.getClassLoader(), localObject1 });
                if (localObject4 == null) {}
                try
                {
                  if (localObject4 == null) {
                    return;
                  }
                  localObject2 = new StringBuilder();
                  ((StringBuilder)localObject2).append("Error when loading lib: ");
                  ((StringBuilder)localObject2).append((String)localObject4);
                  ((StringBuilder)localObject2).append(" lib hash: ");
                  ((StringBuilder)localObject2).append(getLibHash(paramAnonymousString));
                  ((StringBuilder)localObject2).append(" search path is ");
                  ((StringBuilder)localObject2).append((String)localObject1);
                  Log.e("SoLoader", ((StringBuilder)localObject2).toString());
                  return;
                }
                catch (Throwable localThrowable3) {}
                throw new UnsatisfiedLinkError((String)localObject4);
              }
              catch (Throwable localThrowable4)
              {
                localObject4 = localObject3;
                localObject1 = localObject2;
              }
              localObject2 = localObject1;
              localObject3 = localObject4;
              localObject3 = localObject1;
              localObject2 = localObject4;
              Object localObject5;
              System.load(paramAnonymousString);
            }
            catch (Throwable localThrowable2)
            {
              try
              {
                throw localThrowable4;
              }
              catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException localIllegalAccessException)
              {
                localObject3 = localObject1;
                localObject2 = localThrowable2;
                localObject7 = new StringBuilder();
                localObject3 = localObject1;
                localObject2 = localThrowable2;
                ((StringBuilder)localObject7).append("Error: Cannot load ");
                localObject3 = localObject1;
                localObject2 = localThrowable2;
                ((StringBuilder)localObject7).append(paramAnonymousString);
                localObject3 = localObject1;
                localObject2 = localThrowable2;
                localObject5 = ((StringBuilder)localObject7).toString();
                try
                {
                  throw new RuntimeException((String)localObject5, localIllegalAccessException);
                }
                catch (Throwable localThrowable1)
                {
                  localObject2 = localObject5;
                }
              }
              localThrowable2 = localThrowable2;
              localObject1 = localObject3;
              localObject3 = localThrowable2;
              if (localObject2 != null)
              {
                localObject5 = new StringBuilder();
                ((StringBuilder)localObject5).append("Error when loading lib: ");
                ((StringBuilder)localObject5).append((String)localObject2);
                ((StringBuilder)localObject5).append(" lib hash: ");
                ((StringBuilder)localObject5).append(getLibHash(paramAnonymousString));
                ((StringBuilder)localObject5).append(" search path is ");
                ((StringBuilder)localObject5).append((String)localObject1);
                Log.e("SoLoader", ((StringBuilder)localObject5).toString());
              }
              throw localThrowable1;
            }
          }
        }
      };
      return;
      throw paramSoFileLoader;
      label74:
      bool = false;
      break;
      label79:
      paramSoFileLoader = null;
    }
  }
  
  private static void initSoSources(Context paramContext, int paramInt, SoFileLoader paramSoFileLoader)
    throws IOException
  {
    sSoSourcesLock.writeLock().lock();
    try
    {
      paramSoFileLoader = sSoSources;
      if (paramSoFileLoader == null)
      {
        Log.d("SoLoader", "init start");
        sFlags = paramInt;
        ArrayList localArrayList = new ArrayList();
        Object localObject1 = System.getenv("LD_LIBRARY_PATH");
        paramSoFileLoader = (SoFileLoader)localObject1;
        if (localObject1 == null) {
          paramSoFileLoader = "/vendor/lib:/system/lib";
        }
        paramSoFileLoader = paramSoFileLoader.split(":");
        int i = 0;
        for (;;)
        {
          j = paramSoFileLoader.length;
          if (i >= j) {
            break;
          }
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("adding system library source: ");
          ((StringBuilder)localObject1).append(paramSoFileLoader[i]);
          Log.d("SoLoader", ((StringBuilder)localObject1).toString());
          localArrayList.add(new DirectorySoSource(new File(paramSoFileLoader[i]), 2));
          i += 1;
        }
        if (paramContext != null) {
          if ((paramInt & 0x1) != 0)
          {
            sBackupSoSources = null;
            Log.d("SoLoader", "adding exo package source: lib-main");
            localArrayList.add(0, new ExoSoSource(paramContext, "lib-main"));
          }
          else
          {
            paramSoFileLoader = paramContext.getApplicationInfo();
            paramInt = flags;
            if ((paramInt & 0x1) != 0)
            {
              paramInt = flags;
              if ((paramInt & 0x80) == 0)
              {
                paramInt = 1;
                break label226;
              }
            }
            paramInt = 0;
            label226:
            if (paramInt != 0)
            {
              paramInt = 0;
            }
            else
            {
              paramInt = Build.VERSION.SDK_INT;
              if (paramInt <= 17) {
                paramInt = 1;
              } else {
                paramInt = 0;
              }
              sApplicationSoSource = new ApplicationSoSource(paramContext, paramInt);
              paramSoFileLoader = new StringBuilder();
              paramSoFileLoader.append("adding application source: ");
              paramSoFileLoader.append(sApplicationSoSource.toString());
              Log.d("SoLoader", paramSoFileLoader.toString());
              localArrayList.add(0, sApplicationSoSource);
              paramInt = 1;
            }
            i = sFlags;
            if ((i & 0x8) != 0)
            {
              sBackupSoSources = null;
            }
            else
            {
              localObject1 = new File(getApplicationInfosourceDir);
              paramSoFileLoader = new ArrayList();
              localObject1 = new ApkSoSource(paramContext, (File)localObject1, "lib-main", paramInt);
              paramSoFileLoader.add(localObject1);
              Object localObject2 = new StringBuilder();
              ((StringBuilder)localObject2).append("adding backup source from : ");
              ((StringBuilder)localObject2).append(((DirectorySoSource)localObject1).toString());
              Log.d("SoLoader", ((StringBuilder)localObject2).toString());
              i = Build.VERSION.SDK_INT;
              if (i >= 21)
              {
                localObject1 = getApplicationInfosplitSourceDirs;
                if (localObject1 != null)
                {
                  Log.d("SoLoader", "adding backup sources from split apks");
                  localObject1 = getApplicationInfosplitSourceDirs;
                  int k = localObject1.length;
                  j = 0;
                  i = 0;
                  while (j < k)
                  {
                    localObject2 = new File(localObject1[j]);
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("lib-");
                    localStringBuilder.append(i);
                    localObject2 = new ApkSoSource(paramContext, (File)localObject2, localStringBuilder.toString(), paramInt);
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("adding backup source: ");
                    localStringBuilder.append(((DirectorySoSource)localObject2).toString());
                    Log.d("SoLoader", localStringBuilder.toString());
                    paramSoFileLoader.add(localObject2);
                    j += 1;
                    i += 1;
                  }
                }
              }
              sBackupSoSources = (UnpackingSoSource[])paramSoFileLoader.toArray(new UnpackingSoSource[paramSoFileLoader.size()]);
              localArrayList.addAll(0, paramSoFileLoader);
            }
          }
        }
        paramContext = (SoSource[])localArrayList.toArray(new SoSource[localArrayList.size()]);
        int j = makePrepareFlags();
        for (paramInt = paramContext.length;; paramInt = i)
        {
          i = paramInt - 1;
          if (paramInt <= 0) {
            break;
          }
          paramSoFileLoader = new StringBuilder();
          paramSoFileLoader.append("Preparing SO source: ");
          paramSoFileLoader.append(paramContext[i]);
          Log.d("SoLoader", paramSoFileLoader.toString());
          paramContext[i].prepare(j);
        }
        sSoSources = paramContext;
        paramInt = sSoSourcesVersion;
        sSoSourcesVersion = paramInt + 1;
        paramContext = new StringBuilder();
        paramContext.append("init finish: ");
        paramContext.append(sSoSources.length);
        paramContext.append(" SO sources prepared");
        Log.d("SoLoader", paramContext.toString());
      }
      Log.d("SoLoader", "init exiting");
      sSoSourcesLock.writeLock().unlock();
      return;
    }
    catch (Throwable paramContext)
    {
      Log.d("SoLoader", "init exiting");
      sSoSourcesLock.writeLock().unlock();
      throw paramContext;
    }
  }
  
  public static boolean loadLibrary(String paramString)
  {
    return loadLibrary(paramString, 0);
  }
  
  public static boolean loadLibrary(String paramString, int paramInt)
    throws UnsatisfiedLinkError
  {
    sSoSourcesLock.readLock().lock();
    try
    {
      Object localObject = sSoSources;
      if (localObject == null)
      {
        boolean bool = "http://www.android.com/".equals(System.getProperty("java.vendor.url"));
        if (bool) {
          assertInitialized();
        } else {
          try
          {
            bool = sLoadedLibraries.contains(paramString) ^ true;
            if (bool) {
              if (sSystemLoadLibraryWrapper != null) {
                sSystemLoadLibraryWrapper.loadLibrary(paramString);
              } else {
                System.loadLibrary(paramString);
              }
            }
            sSoSourcesLock.readLock().unlock();
            return bool;
          }
          catch (Throwable paramString)
          {
            throw paramString;
          }
        }
      }
      sSoSourcesLock.readLock().unlock();
      String str = MergedSoMapping.mapLibName(paramString);
      if (str != null) {
        localObject = str;
      } else {
        localObject = paramString;
      }
      return loadLibraryBySoName(System.mapLibraryName((String)localObject), paramString, str, paramInt | 0x2, null);
    }
    catch (Throwable paramString)
    {
      sSoSourcesLock.readLock().unlock();
      throw paramString;
    }
  }
  
  static void loadLibraryBySoName(String paramString, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
  {
    loadLibraryBySoName(paramString, null, null, paramInt, paramThreadPolicy);
  }
  
  /* Error */
  private static boolean loadLibraryBySoName(String paramString1, String paramString2, String paramString3, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 469	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   4: istore 8
    //   6: iconst_0
    //   7: istore 7
    //   9: iload 8
    //   11: ifne +17 -> 28
    //   14: getstatic 101	com/facebook/soloader/SoLoader:sLoadedAndMergedLibraries	Ljava/util/Set;
    //   17: aload_1
    //   18: invokeinterface 472 2 0
    //   23: ifeq +5 -> 28
    //   26: iconst_0
    //   27: ireturn
    //   28: ldc 2
    //   30: monitorenter
    //   31: getstatic 85	com/facebook/soloader/SoLoader:sLoadedLibraries	Ljava/util/HashSet;
    //   34: aload_0
    //   35: invokevirtual 446	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   38: ifeq +457 -> 495
    //   41: aload_2
    //   42: ifnonnull +8 -> 50
    //   45: ldc 2
    //   47: monitorexit
    //   48: iconst_0
    //   49: ireturn
    //   50: iconst_1
    //   51: istore 5
    //   53: goto +3 -> 56
    //   56: getstatic 90	com/facebook/soloader/SoLoader:sLoadingLibraries	Ljava/util/Map;
    //   59: aload_0
    //   60: invokeinterface 477 2 0
    //   65: ifeq +17 -> 82
    //   68: getstatic 90	com/facebook/soloader/SoLoader:sLoadingLibraries	Ljava/util/Map;
    //   71: aload_0
    //   72: invokeinterface 481 2 0
    //   77: astore 9
    //   79: goto +24 -> 103
    //   82: new 4	java/lang/Object
    //   85: dup
    //   86: invokespecial 112	java/lang/Object:<init>	()V
    //   89: astore 9
    //   91: getstatic 90	com/facebook/soloader/SoLoader:sLoadingLibraries	Ljava/util/Map;
    //   94: aload_0
    //   95: aload 9
    //   97: invokeinterface 485 3 0
    //   102: pop
    //   103: ldc 2
    //   105: monitorexit
    //   106: aload 9
    //   108: monitorenter
    //   109: iload 5
    //   111: istore 6
    //   113: iload 5
    //   115: ifne +200 -> 315
    //   118: ldc 2
    //   120: monitorenter
    //   121: getstatic 85	com/facebook/soloader/SoLoader:sLoadedLibraries	Ljava/util/HashSet;
    //   124: aload_0
    //   125: invokevirtual 446	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   128: ifeq +18 -> 146
    //   131: aload_2
    //   132: ifnonnull +11 -> 143
    //   135: ldc 2
    //   137: monitorexit
    //   138: aload 9
    //   140: monitorexit
    //   141: iconst_0
    //   142: ireturn
    //   143: iconst_1
    //   144: istore 5
    //   146: ldc 2
    //   148: monitorexit
    //   149: iload 5
    //   151: istore 6
    //   153: iload 5
    //   155: ifne +160 -> 315
    //   158: new 147	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   165: astore 10
    //   167: aload 10
    //   169: ldc_w 487
    //   172: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: pop
    //   176: aload 10
    //   178: aload_0
    //   179: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: pop
    //   183: ldc 20
    //   185: aload 10
    //   187: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: invokestatic 207	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   193: pop
    //   194: aload_0
    //   195: iload_3
    //   196: aload 4
    //   198: invokestatic 489	com/facebook/soloader/SoLoader:doLoadLibraryBySoName	(Ljava/lang/String;ILandroid/os/StrictMode$ThreadPolicy;)V
    //   201: ldc 2
    //   203: monitorenter
    //   204: new 147	java/lang/StringBuilder
    //   207: dup
    //   208: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   211: astore 4
    //   213: aload 4
    //   215: ldc_w 491
    //   218: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: pop
    //   222: aload 4
    //   224: aload_0
    //   225: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: pop
    //   229: ldc 20
    //   231: aload 4
    //   233: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   236: invokestatic 207	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   239: pop
    //   240: getstatic 85	com/facebook/soloader/SoLoader:sLoadedLibraries	Ljava/util/HashSet;
    //   243: aload_0
    //   244: invokevirtual 492	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   247: pop
    //   248: ldc 2
    //   250: monitorexit
    //   251: iload 5
    //   253: istore 6
    //   255: goto +60 -> 315
    //   258: astore_0
    //   259: ldc 2
    //   261: monitorexit
    //   262: aload_0
    //   263: athrow
    //   264: astore_0
    //   265: aload_0
    //   266: invokevirtual 241	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   269: astore_1
    //   270: aload_1
    //   271: ifnull +26 -> 297
    //   274: aload_1
    //   275: ldc_w 494
    //   278: invokevirtual 496	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   281: istore 8
    //   283: iload 8
    //   285: ifeq +12 -> 297
    //   288: new 11	com/facebook/soloader/SoLoader$WrongAbiError
    //   291: dup
    //   292: aload_0
    //   293: invokespecial 499	com/facebook/soloader/SoLoader$WrongAbiError:<init>	(Ljava/lang/Throwable;)V
    //   296: athrow
    //   297: aload_0
    //   298: athrow
    //   299: astore_0
    //   300: new 167	java/lang/RuntimeException
    //   303: dup
    //   304: aload_0
    //   305: invokespecial 500	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   308: athrow
    //   309: astore_0
    //   310: ldc 2
    //   312: monitorexit
    //   313: aload_0
    //   314: athrow
    //   315: iload 7
    //   317: istore_3
    //   318: aload_1
    //   319: invokestatic 469	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   322: ifne +20 -> 342
    //   325: iload 7
    //   327: istore_3
    //   328: getstatic 101	com/facebook/soloader/SoLoader:sLoadedAndMergedLibraries	Ljava/util/Set;
    //   331: aload_1
    //   332: invokeinterface 472 2 0
    //   337: ifeq +5 -> 342
    //   340: iconst_1
    //   341: istore_3
    //   342: aload_2
    //   343: ifnull +133 -> 476
    //   346: iload_3
    //   347: ifne +129 -> 476
    //   350: getstatic 110	com/facebook/soloader/SoLoader:SYSTRACE_LIBRARY_LOADING	Z
    //   353: ifeq +39 -> 392
    //   356: new 147	java/lang/StringBuilder
    //   359: dup
    //   360: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   363: astore_2
    //   364: aload_2
    //   365: ldc_w 502
    //   368: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: pop
    //   372: aload_2
    //   373: aload_1
    //   374: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   377: pop
    //   378: aload_2
    //   379: ldc -65
    //   381: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: pop
    //   385: aload_2
    //   386: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   389: invokestatic 196	com/facebook/soloader/Api18TraceUtils:beginTraceSection	(Ljava/lang/String;)V
    //   392: new 147	java/lang/StringBuilder
    //   395: dup
    //   396: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   399: astore_2
    //   400: aload_2
    //   401: ldc_w 504
    //   404: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   407: pop
    //   408: aload_2
    //   409: aload_1
    //   410: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: pop
    //   414: aload_2
    //   415: ldc_w 506
    //   418: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   421: pop
    //   422: aload_2
    //   423: aload_0
    //   424: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   427: pop
    //   428: ldc 20
    //   430: aload_2
    //   431: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   434: invokestatic 207	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   437: pop
    //   438: aload_1
    //   439: invokestatic 509	com/facebook/soloader/MergedSoMapping:invokeJniOnload	(Ljava/lang/String;)V
    //   442: getstatic 101	com/facebook/soloader/SoLoader:sLoadedAndMergedLibraries	Ljava/util/Set;
    //   445: aload_1
    //   446: invokeinterface 510 2 0
    //   451: pop
    //   452: getstatic 110	com/facebook/soloader/SoLoader:SYSTRACE_LIBRARY_LOADING	Z
    //   455: ifeq +21 -> 476
    //   458: invokestatic 231	com/facebook/soloader/Api18TraceUtils:endSection	()V
    //   461: goto +15 -> 476
    //   464: astore_0
    //   465: getstatic 110	com/facebook/soloader/SoLoader:SYSTRACE_LIBRARY_LOADING	Z
    //   468: ifeq +6 -> 474
    //   471: invokestatic 231	com/facebook/soloader/Api18TraceUtils:endSection	()V
    //   474: aload_0
    //   475: athrow
    //   476: aload 9
    //   478: monitorexit
    //   479: iload 6
    //   481: iconst_1
    //   482: ixor
    //   483: ireturn
    //   484: aload 9
    //   486: monitorexit
    //   487: aload_0
    //   488: athrow
    //   489: astore_0
    //   490: ldc 2
    //   492: monitorexit
    //   493: aload_0
    //   494: athrow
    //   495: iconst_0
    //   496: istore 5
    //   498: goto -442 -> 56
    //   501: astore_0
    //   502: goto -18 -> 484
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	505	0	paramString1	String
    //   0	505	1	paramString2	String
    //   0	505	2	paramString3	String
    //   0	505	3	paramInt	int
    //   0	505	4	paramThreadPolicy	StrictMode.ThreadPolicy
    //   51	446	5	i	int
    //   111	372	6	j	int
    //   7	319	7	k	int
    //   4	280	8	bool	boolean
    //   77	408	9	localObject	Object
    //   165	21	10	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   204	251	258	java/lang/Throwable
    //   259	262	258	java/lang/Throwable
    //   158	201	264	java/lang/UnsatisfiedLinkError
    //   158	201	299	java/io/IOException
    //   121	131	309	java/lang/Throwable
    //   135	138	309	java/lang/Throwable
    //   146	149	309	java/lang/Throwable
    //   310	313	309	java/lang/Throwable
    //   392	452	464	java/lang/Throwable
    //   31	41	489	java/lang/Throwable
    //   45	48	489	java/lang/Throwable
    //   56	79	489	java/lang/Throwable
    //   82	103	489	java/lang/Throwable
    //   103	106	489	java/lang/Throwable
    //   490	493	489	java/lang/Throwable
    //   118	121	501	java/lang/Throwable
    //   138	141	501	java/lang/Throwable
    //   158	201	501	java/lang/Throwable
    //   201	204	501	java/lang/Throwable
    //   262	264	501	java/lang/Throwable
    //   265	270	501	java/lang/Throwable
    //   274	283	501	java/lang/Throwable
    //   288	297	501	java/lang/Throwable
    //   297	299	501	java/lang/Throwable
    //   300	309	501	java/lang/Throwable
    //   313	315	501	java/lang/Throwable
    //   318	325	501	java/lang/Throwable
    //   328	340	501	java/lang/Throwable
    //   350	392	501	java/lang/Throwable
    //   452	461	501	java/lang/Throwable
    //   465	474	501	java/lang/Throwable
    //   474	476	501	java/lang/Throwable
    //   476	479	501	java/lang/Throwable
    //   484	487	501	java/lang/Throwable
  }
  
  public static String makeLdLibraryPath()
  {
    sSoSourcesLock.readLock().lock();
    try
    {
      assertInitialized();
      Log.d("SoLoader", "makeLdLibraryPath");
      Object localObject1 = new ArrayList();
      Object localObject2 = sSoSources;
      int i = 0;
      for (;;)
      {
        int j = localObject2.length;
        if (i >= j) {
          break;
        }
        localObject2[i].addToLdLibraryPath((Collection)localObject1);
        i += 1;
      }
      localObject1 = TextUtils.join(":", (Iterable)localObject1);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("makeLdLibraryPath final path: ");
      ((StringBuilder)localObject2).append((String)localObject1);
      Log.d("SoLoader", ((StringBuilder)localObject2).toString());
      sSoSourcesLock.readLock().unlock();
      return localObject1;
    }
    catch (Throwable localThrowable)
    {
      sSoSourcesLock.readLock().unlock();
      throw localThrowable;
    }
  }
  
  public static String makeNonZipPath(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = paramString.split(":");
    ArrayList localArrayList = new ArrayList(paramString.length);
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramString[i];
      if (!localObject.contains("!")) {
        localArrayList.add(localObject);
      }
      i += 1;
    }
    return TextUtils.join(":", localArrayList);
  }
  
  private static int makePrepareFlags()
  {
    sSoSourcesLock.writeLock().lock();
    try
    {
      int i = sFlags;
      if ((i & 0x2) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      sSoSourcesLock.writeLock().unlock();
      return i;
    }
    catch (Throwable localThrowable)
    {
      sSoSourcesLock.writeLock().unlock();
      throw localThrowable;
    }
  }
  
  public static void prependSoSource(SoSource paramSoSource)
    throws IOException
  {
    sSoSourcesLock.writeLock().lock();
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Prepending to SO sources: ");
      ((StringBuilder)localObject).append(paramSoSource);
      Log.d("SoLoader", ((StringBuilder)localObject).toString());
      assertInitialized();
      paramSoSource.prepare(makePrepareFlags());
      int i = sSoSources.length;
      localObject = new SoSource[i + 1];
      localObject[0] = paramSoSource;
      System.arraycopy(sSoSources, 0, localObject, 1, sSoSources.length);
      sSoSources = (SoSource[])localObject;
      i = sSoSourcesVersion;
      sSoSourcesVersion = i + 1;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Prepended to SO sources: ");
      ((StringBuilder)localObject).append(paramSoSource);
      Log.d("SoLoader", ((StringBuilder)localObject).toString());
      sSoSourcesLock.writeLock().unlock();
      return;
    }
    catch (Throwable paramSoSource)
    {
      sSoSourcesLock.writeLock().unlock();
      throw paramSoSource;
    }
  }
  
  static void resetStatus()
  {
    try
    {
      sLoadedLibraries.clear();
      sLoadingLibraries.clear();
      sSoFileLoader = null;
      setSoSources(null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static void setInTestMode()
  {
    setSoSources(new SoSource[] { new NoopSoSource() });
  }
  
  static void setSoFileLoader(SoFileLoader paramSoFileLoader)
  {
    sSoFileLoader = paramSoFileLoader;
  }
  
  static void setSoSources(SoSource[] paramArrayOfSoSource)
  {
    sSoSourcesLock.writeLock().lock();
    try
    {
      sSoSources = paramArrayOfSoSource;
      int i = sSoSourcesVersion;
      sSoSourcesVersion = i + 1;
      sSoSourcesLock.writeLock().unlock();
      return;
    }
    catch (Throwable paramArrayOfSoSource)
    {
      sSoSourcesLock.writeLock().unlock();
      throw paramArrayOfSoSource;
    }
  }
  
  public static void setSystemLoadLibraryWrapper(SystemLoadLibraryWrapper paramSystemLoadLibraryWrapper)
  {
    sSystemLoadLibraryWrapper = paramSystemLoadLibraryWrapper;
  }
  
  public static File unpackLibraryAndDependencies(String paramString)
    throws UnsatisfiedLinkError
  {
    
    try
    {
      paramString = unpackLibraryBySoName(System.mapLibraryName(paramString));
      return paramString;
    }
    catch (IOException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  static File unpackLibraryBySoName(String paramString)
    throws IOException
  {
    sSoSourcesLock.readLock().lock();
    int i = 0;
    try
    {
      for (;;)
      {
        int j = sSoSources.length;
        if (i >= j) {
          break;
        }
        File localFile = sSoSources[i].unpackLibrary(paramString);
        if (localFile != null)
        {
          sSoSourcesLock.readLock().unlock();
          return localFile;
        }
        i += 1;
      }
      sSoSourcesLock.readLock().unlock();
      throw new FileNotFoundException(paramString);
    }
    catch (Throwable paramString)
    {
      sSoSourcesLock.readLock().unlock();
      throw paramString;
    }
  }
  
  @TargetApi(14)
  @DoNotOptimize
  private static class Api14Utils
  {
    private Api14Utils() {}
    
    public static String getClassLoaderLdLoadLibrary()
    {
      Object localObject1 = SoLoader.class.getClassLoader();
      if ((localObject1 instanceof BaseDexClassLoader))
      {
        localObject1 = (BaseDexClassLoader)localObject1;
        try
        {
          localObject2 = BaseDexClassLoader.class.getMethod("getLdLibraryPath", new Class[0]);
          localObject1 = ((Method)localObject2).invoke(localObject1, new Object[0]);
          return (String)localObject1;
        }
        catch (Exception localException)
        {
          throw new RuntimeException("Cannot call getLdLibraryPath", localException);
        }
      }
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("ClassLoader ");
      ((StringBuilder)localObject2).append(localException.getClass().getName());
      ((StringBuilder)localObject2).append(" should be of type BaseDexClassLoader");
      throw new IllegalStateException(((StringBuilder)localObject2).toString());
    }
  }
  
  public static final class WrongAbiError
    extends UnsatisfiedLinkError
  {
    WrongAbiError(Throwable paramThrowable)
    {
      super();
      initCause(paramThrowable);
    }
  }
}
