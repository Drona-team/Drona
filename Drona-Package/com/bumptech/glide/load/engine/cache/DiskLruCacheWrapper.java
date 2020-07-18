package com.bumptech.glide.load.engine.cache;

import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.disklrucache.DiskLruCache.Value;
import com.bumptech.glide.load.Key;
import java.io.File;
import java.io.IOException;

public class DiskLruCacheWrapper
  implements DiskCache
{
  private static final int APP_VERSION = 1;
  private static final String TAG = "DiskLruCacheWrapper";
  private static final int VALUE_COUNT = 1;
  private static DiskLruCacheWrapper wrapper;
  private final File directory;
  private DiskLruCache diskLruCache;
  private final long maxSize;
  private final SafeKeyGenerator safeKeyGenerator;
  private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();
  
  protected DiskLruCacheWrapper(File paramFile, long paramLong)
  {
    directory = paramFile;
    maxSize = paramLong;
    safeKeyGenerator = new SafeKeyGenerator();
  }
  
  public static DiskCache create(File paramFile, long paramLong)
  {
    return new DiskLruCacheWrapper(paramFile, paramLong);
  }
  
  public static DiskCache get(File paramFile, long paramLong)
  {
    try
    {
      if (wrapper == null) {
        wrapper = new DiskLruCacheWrapper(paramFile, paramLong);
      }
      paramFile = wrapper;
      return paramFile;
    }
    catch (Throwable paramFile)
    {
      throw paramFile;
    }
  }
  
  private DiskLruCache getDiskCache()
    throws IOException
  {
    try
    {
      if (diskLruCache == null) {
        diskLruCache = DiskLruCache.open(directory, 1, 1, maxSize);
      }
      DiskLruCache localDiskLruCache = diskLruCache;
      return localDiskLruCache;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void resetDiskCache()
  {
    try
    {
      diskLruCache = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 72	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:getDiskCache	()Lcom/bumptech/glide/disklrucache/DiskLruCache;
    //   6: invokevirtual 75	com/bumptech/glide/disklrucache/DiskLruCache:delete	()V
    //   9: aload_0
    //   10: invokespecial 77	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:resetDiskCache	()V
    //   13: goto +29 -> 42
    //   16: astore_1
    //   17: goto +28 -> 45
    //   20: astore_1
    //   21: ldc 13
    //   23: iconst_5
    //   24: invokestatic 83	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   27: ifeq -18 -> 9
    //   30: ldc 13
    //   32: ldc 85
    //   34: aload_1
    //   35: invokestatic 89	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   38: pop
    //   39: goto -30 -> 9
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: aload_0
    //   46: invokespecial 77	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:resetDiskCache	()V
    //   49: aload_1
    //   50: athrow
    //   51: astore_1
    //   52: aload_0
    //   53: monitorexit
    //   54: aload_1
    //   55: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	DiskLruCacheWrapper
    //   16	1	1	localThrowable1	Throwable
    //   20	30	1	localIOException	IOException
    //   51	4	1	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   2	9	16	java/lang/Throwable
    //   21	39	16	java/lang/Throwable
    //   2	9	20	java/io/IOException
    //   9	13	51	java/lang/Throwable
    //   45	51	51	java/lang/Throwable
  }
  
  public void delete(Key paramKey)
  {
    paramKey = safeKeyGenerator.getSafeKey(paramKey);
    try
    {
      getDiskCache().remove(paramKey);
      return;
    }
    catch (IOException paramKey)
    {
      if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
        Log.w("DiskLruCacheWrapper", "Unable to delete from disk cache", paramKey);
      }
    }
  }
  
  public File get(Key paramKey)
  {
    String str = safeKeyGenerator.getSafeKey(paramKey);
    if (Log.isLoggable("DiskLruCacheWrapper", 2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Get: Obtained: ");
      localStringBuilder.append(str);
      localStringBuilder.append(" for for Key: ");
      localStringBuilder.append(paramKey);
      Log.v("DiskLruCacheWrapper", localStringBuilder.toString());
    }
    try
    {
      paramKey = getDiskCache().get(str);
      if (paramKey != null)
      {
        paramKey = paramKey.getFile(0);
        return paramKey;
      }
    }
    catch (IOException paramKey)
    {
      if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
        Log.w("DiskLruCacheWrapper", "Unable to get from disk cache", paramKey);
      }
    }
    return null;
  }
  
  /* Error */
  public void put(Key paramKey, DiskCache.Writer paramWriter)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 45	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:safeKeyGenerator	Lcom/bumptech/glide/load/engine/cache/SafeKeyGenerator;
    //   4: aload_1
    //   5: invokevirtual 94	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:getSafeKey	(Lcom/bumptech/glide/load/Key;)Ljava/lang/String;
    //   8: astore 4
    //   10: aload_0
    //   11: getfield 36	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:writeLocker	Lcom/bumptech/glide/load/engine/cache/DiskCacheWriteLocker;
    //   14: aload 4
    //   16: invokevirtual 140	com/bumptech/glide/load/engine/cache/DiskCacheWriteLocker:acquire	(Ljava/lang/String;)V
    //   19: ldc 13
    //   21: iconst_2
    //   22: invokestatic 83	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   25: istore_3
    //   26: iload_3
    //   27: ifeq +54 -> 81
    //   30: new 103	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 104	java/lang/StringBuilder:<init>	()V
    //   37: astore 5
    //   39: aload 5
    //   41: ldc -114
    //   43: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: pop
    //   47: aload 5
    //   49: aload 4
    //   51: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: pop
    //   55: aload 5
    //   57: ldc 112
    //   59: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: pop
    //   63: aload 5
    //   65: aload_1
    //   66: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   69: pop
    //   70: ldc 13
    //   72: aload 5
    //   74: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   77: invokestatic 123	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   80: pop
    //   81: aload_0
    //   82: invokespecial 72	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:getDiskCache	()Lcom/bumptech/glide/disklrucache/DiskLruCache;
    //   85: astore_1
    //   86: aload_1
    //   87: aload 4
    //   89: invokevirtual 126	com/bumptech/glide/disklrucache/DiskLruCache:get	(Ljava/lang/String;)Lcom/bumptech/glide/disklrucache/DiskLruCache$Value;
    //   92: astore 5
    //   94: aload 5
    //   96: ifnull +13 -> 109
    //   99: aload_0
    //   100: getfield 36	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:writeLocker	Lcom/bumptech/glide/load/engine/cache/DiskCacheWriteLocker;
    //   103: aload 4
    //   105: invokevirtual 145	com/bumptech/glide/load/engine/cache/DiskCacheWriteLocker:release	(Ljava/lang/String;)V
    //   108: return
    //   109: aload_1
    //   110: aload 4
    //   112: invokevirtual 149	com/bumptech/glide/disklrucache/DiskLruCache:edit	(Ljava/lang/String;)Lcom/bumptech/glide/disklrucache/DiskLruCache$Editor;
    //   115: astore_1
    //   116: aload_1
    //   117: ifnull +37 -> 154
    //   120: aload_2
    //   121: aload_1
    //   122: iconst_0
    //   123: invokevirtual 152	com/bumptech/glide/disklrucache/DiskLruCache$Editor:getFile	(I)Ljava/io/File;
    //   126: invokeinterface 158 2 0
    //   131: istore_3
    //   132: iload_3
    //   133: ifeq +7 -> 140
    //   136: aload_1
    //   137: invokevirtual 161	com/bumptech/glide/disklrucache/DiskLruCache$Editor:commit	()V
    //   140: aload_1
    //   141: invokevirtual 164	com/bumptech/glide/disklrucache/DiskLruCache$Editor:abortUnlessCommitted	()V
    //   144: goto +67 -> 211
    //   147: astore_2
    //   148: aload_1
    //   149: invokevirtual 164	com/bumptech/glide/disklrucache/DiskLruCache$Editor:abortUnlessCommitted	()V
    //   152: aload_2
    //   153: athrow
    //   154: new 103	java/lang/StringBuilder
    //   157: dup
    //   158: invokespecial 104	java/lang/StringBuilder:<init>	()V
    //   161: astore_1
    //   162: aload_1
    //   163: ldc -90
    //   165: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: pop
    //   169: aload_1
    //   170: aload 4
    //   172: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: pop
    //   176: new 168	java/lang/IllegalStateException
    //   179: dup
    //   180: aload_1
    //   181: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokespecial 170	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   187: astore_1
    //   188: aload_1
    //   189: athrow
    //   190: astore_1
    //   191: ldc 13
    //   193: iconst_5
    //   194: invokestatic 83	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   197: istore_3
    //   198: iload_3
    //   199: ifeq +12 -> 211
    //   202: ldc 13
    //   204: ldc -84
    //   206: aload_1
    //   207: invokestatic 89	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   210: pop
    //   211: aload_0
    //   212: getfield 36	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:writeLocker	Lcom/bumptech/glide/load/engine/cache/DiskCacheWriteLocker;
    //   215: aload 4
    //   217: invokevirtual 145	com/bumptech/glide/load/engine/cache/DiskCacheWriteLocker:release	(Ljava/lang/String;)V
    //   220: return
    //   221: astore_1
    //   222: aload_0
    //   223: getfield 36	com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper:writeLocker	Lcom/bumptech/glide/load/engine/cache/DiskCacheWriteLocker;
    //   226: aload 4
    //   228: invokevirtual 145	com/bumptech/glide/load/engine/cache/DiskCacheWriteLocker:release	(Ljava/lang/String;)V
    //   231: aload_1
    //   232: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	this	DiskLruCacheWrapper
    //   0	233	1	paramKey	Key
    //   0	233	2	paramWriter	DiskCache.Writer
    //   25	174	3	bool	boolean
    //   8	219	4	str	String
    //   37	58	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   120	132	147	java/lang/Throwable
    //   136	140	147	java/lang/Throwable
    //   81	94	190	java/io/IOException
    //   109	116	190	java/io/IOException
    //   140	144	190	java/io/IOException
    //   148	154	190	java/io/IOException
    //   154	188	190	java/io/IOException
    //   19	26	221	java/lang/Throwable
    //   30	81	221	java/lang/Throwable
    //   81	94	221	java/lang/Throwable
    //   109	116	221	java/lang/Throwable
    //   140	144	221	java/lang/Throwable
    //   148	154	221	java/lang/Throwable
    //   154	188	221	java/lang/Throwable
    //   188	190	221	java/lang/Throwable
    //   191	198	221	java/lang/Throwable
    //   202	211	221	java/lang/Throwable
  }
}
