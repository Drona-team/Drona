package com.facebook.imagepipeline.cache;

import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.WriterCallback;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferedDiskCache
{
  private static final Class<?> TAG = BufferedDiskCache.class;
  private final FileCache mFileCache;
  private final ImageCacheStatsTracker mImageCacheStatsTracker;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  private final PooledByteStreams mPooledByteStreams;
  private final Executor mReadExecutor;
  private final StagingArea mStagingArea;
  private final Executor mWriteExecutor;
  
  public BufferedDiskCache(FileCache paramFileCache, PooledByteBufferFactory paramPooledByteBufferFactory, PooledByteStreams paramPooledByteStreams, Executor paramExecutor1, Executor paramExecutor2, ImageCacheStatsTracker paramImageCacheStatsTracker)
  {
    mFileCache = paramFileCache;
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
    mPooledByteStreams = paramPooledByteStreams;
    mReadExecutor = paramExecutor1;
    mWriteExecutor = paramExecutor2;
    mImageCacheStatsTracker = paramImageCacheStatsTracker;
    mStagingArea = StagingArea.getInstance();
  }
  
  private boolean checkInStagingAreaAndFileCache(CacheKey paramCacheKey)
  {
    Object localObject = mStagingArea.get(paramCacheKey);
    if (localObject != null)
    {
      ((EncodedImage)localObject).close();
      FLog.v(TAG, "Found image for %s in staging area", paramCacheKey.getUriString());
      mImageCacheStatsTracker.onStagingAreaHit(paramCacheKey);
      return true;
    }
    FLog.v(TAG, "Did not find image for %s in staging area", paramCacheKey.getUriString());
    mImageCacheStatsTracker.onStagingAreaMiss();
    localObject = mFileCache;
    try
    {
      boolean bool = ((FileCache)localObject).hasKey(paramCacheKey);
      return bool;
    }
    catch (Exception paramCacheKey)
    {
      for (;;) {}
    }
    return false;
  }
  
  private Task containsAsync(final CacheKey paramCacheKey)
  {
    try
    {
      Object localObject = new Callable()
      {
        public Boolean call()
          throws Exception
        {
          return Boolean.valueOf(BufferedDiskCache.this.checkInStagingAreaAndFileCache(paramCacheKey));
        }
      };
      Executor localExecutor = mReadExecutor;
      localObject = Task.call((Callable)localObject, localExecutor);
      return localObject;
    }
    catch (Exception localException)
    {
      FLog.w(TAG, localException, "Failed to schedule disk-cache read for %s", new Object[] { paramCacheKey.getUriString() });
      return Task.forError(localException);
    }
  }
  
  private Task foundPinnedImage(CacheKey paramCacheKey, EncodedImage paramEncodedImage)
  {
    FLog.v(TAG, "Found image for %s in staging area", paramCacheKey.getUriString());
    mImageCacheStatsTracker.onStagingAreaHit(paramCacheKey);
    return Task.forResult(paramEncodedImage);
  }
  
  private Task getAsync(final CacheKey paramCacheKey, final AtomicBoolean paramAtomicBoolean)
  {
    try
    {
      paramAtomicBoolean = new Callable()
      {
        /* Error */
        public EncodedImage call()
          throws Exception
        {
          // Byte code:
          //   0: invokestatic 42	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
          //   3: istore_1
          //   4: iload_1
          //   5: ifeq +8 -> 13
          //   8: ldc 44
          //   10: invokestatic 48	com/facebook/imagepipeline/systrace/FrescoSystrace:beginSection	(Ljava/lang/String;)V
          //   13: aload_0
          //   14: getfield 24	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$isCancelled	Ljava/util/concurrent/atomic/AtomicBoolean;
          //   17: invokevirtual 53	java/util/concurrent/atomic/AtomicBoolean:get	()Z
          //   20: istore_1
          //   21: iload_1
          //   22: ifne +199 -> 221
          //   25: aload_0
          //   26: getfield 22	com/facebook/imagepipeline/cache/BufferedDiskCache$2:this$0	Lcom/facebook/imagepipeline/cache/BufferedDiskCache;
          //   29: invokestatic 57	com/facebook/imagepipeline/cache/BufferedDiskCache:access$100	(Lcom/facebook/imagepipeline/cache/BufferedDiskCache;)Lcom/facebook/imagepipeline/cache/StagingArea;
          //   32: aload_0
          //   33: getfield 26	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$key	Lcom/facebook/cache/common/CacheKey;
          //   36: invokevirtual 62	com/facebook/imagepipeline/cache/StagingArea:get	(Lcom/facebook/cache/common/CacheKey;)Lcom/facebook/imagepipeline/image/EncodedImage;
          //   39: astore_3
          //   40: aload_3
          //   41: astore_2
          //   42: aload_3
          //   43: ifnull +39 -> 82
          //   46: invokestatic 66	com/facebook/imagepipeline/cache/BufferedDiskCache:access$200	()Ljava/lang/Class;
          //   49: ldc 68
          //   51: aload_0
          //   52: getfield 26	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$key	Lcom/facebook/cache/common/CacheKey;
          //   55: invokeinterface 74 1 0
          //   60: invokestatic 80	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
          //   63: aload_0
          //   64: getfield 22	com/facebook/imagepipeline/cache/BufferedDiskCache$2:this$0	Lcom/facebook/imagepipeline/cache/BufferedDiskCache;
          //   67: invokestatic 84	com/facebook/imagepipeline/cache/BufferedDiskCache:access$300	(Lcom/facebook/imagepipeline/cache/BufferedDiskCache;)Lcom/facebook/imagepipeline/cache/ImageCacheStatsTracker;
          //   70: aload_0
          //   71: getfield 26	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$key	Lcom/facebook/cache/common/CacheKey;
          //   74: invokeinterface 90 2 0
          //   79: goto +81 -> 160
          //   82: invokestatic 66	com/facebook/imagepipeline/cache/BufferedDiskCache:access$200	()Ljava/lang/Class;
          //   85: ldc 92
          //   87: aload_0
          //   88: getfield 26	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$key	Lcom/facebook/cache/common/CacheKey;
          //   91: invokeinterface 74 1 0
          //   96: invokestatic 80	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
          //   99: aload_0
          //   100: getfield 22	com/facebook/imagepipeline/cache/BufferedDiskCache$2:this$0	Lcom/facebook/imagepipeline/cache/BufferedDiskCache;
          //   103: invokestatic 84	com/facebook/imagepipeline/cache/BufferedDiskCache:access$300	(Lcom/facebook/imagepipeline/cache/BufferedDiskCache;)Lcom/facebook/imagepipeline/cache/ImageCacheStatsTracker;
          //   106: invokeinterface 95 1 0
          //   111: aload_0
          //   112: getfield 22	com/facebook/imagepipeline/cache/BufferedDiskCache$2:this$0	Lcom/facebook/imagepipeline/cache/BufferedDiskCache;
          //   115: astore_2
          //   116: aload_0
          //   117: getfield 26	com/facebook/imagepipeline/cache/BufferedDiskCache$2:val$key	Lcom/facebook/cache/common/CacheKey;
          //   120: astore_3
          //   121: aload_2
          //   122: aload_3
          //   123: invokestatic 99	com/facebook/imagepipeline/cache/BufferedDiskCache:access$400	(Lcom/facebook/imagepipeline/cache/BufferedDiskCache;Lcom/facebook/cache/common/CacheKey;)Lcom/facebook/common/memory/PooledByteBuffer;
          //   126: astore_2
          //   127: aload_2
          //   128: ifnonnull +14 -> 142
          //   131: invokestatic 42	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
          //   134: ifeq +123 -> 257
          //   137: invokestatic 102	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
          //   140: aconst_null
          //   141: areturn
          //   142: aload_2
          //   143: invokestatic 108	com/facebook/common/references/CloseableReference:of	(Ljava/io/Closeable;)Lcom/facebook/common/references/CloseableReference;
          //   146: astore_3
          //   147: new 110	com/facebook/imagepipeline/image/EncodedImage
          //   150: dup
          //   151: aload_3
          //   152: invokespecial 113	com/facebook/imagepipeline/image/EncodedImage:<init>	(Lcom/facebook/common/references/CloseableReference;)V
          //   155: astore_2
          //   156: aload_3
          //   157: invokestatic 116	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
          //   160: invokestatic 121	java/lang/Thread:interrupted	()Z
          //   163: istore_1
          //   164: iload_1
          //   165: ifeq +27 -> 192
          //   168: invokestatic 66	com/facebook/imagepipeline/cache/BufferedDiskCache:access$200	()Ljava/lang/Class;
          //   171: ldc 123
          //   173: invokestatic 126	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;)V
          //   176: aload_2
          //   177: ifnull +7 -> 184
          //   180: aload_2
          //   181: invokevirtual 129	com/facebook/imagepipeline/image/EncodedImage:close	()V
          //   184: new 131	java/lang/InterruptedException
          //   187: dup
          //   188: invokespecial 132	java/lang/InterruptedException:<init>	()V
          //   191: athrow
          //   192: invokestatic 42	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
          //   195: ifeq +64 -> 259
          //   198: invokestatic 102	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
          //   201: aload_2
          //   202: areturn
          //   203: astore_2
          //   204: aload_3
          //   205: invokestatic 116	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
          //   208: aload_2
          //   209: athrow
          //   210: invokestatic 42	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
          //   213: ifeq +48 -> 261
          //   216: invokestatic 102	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
          //   219: aconst_null
          //   220: areturn
          //   221: new 134	java/util/concurrent/CancellationException
          //   224: dup
          //   225: invokespecial 135	java/util/concurrent/CancellationException:<init>	()V
          //   228: athrow
          //   229: astore_2
          //   230: invokestatic 42	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
          //   233: ifeq +6 -> 239
          //   236: invokestatic 102	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
          //   239: aload_2
          //   240: athrow
          //   241: astore_2
          //   242: goto -32 -> 210
          //   245: astore_2
          //   246: goto -36 -> 210
          //   249: astore_2
          //   250: goto -40 -> 210
          //   253: astore_2
          //   254: goto -44 -> 210
          //   257: aconst_null
          //   258: areturn
          //   259: aload_2
          //   260: areturn
          //   261: aconst_null
          //   262: areturn
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	263	0	this	2
          //   3	162	1	bool	boolean
          //   41	161	2	localObject1	Object
          //   203	6	2	localThrowable1	Throwable
          //   229	11	2	localThrowable2	Throwable
          //   241	1	2	localException1	Exception
          //   245	1	2	localException2	Exception
          //   249	1	2	localException3	Exception
          //   253	7	2	localException4	Exception
          //   39	166	3	localObject2	Object
          // Exception table:
          //   from	to	target	type
          //   147	156	203	java/lang/Throwable
          //   0	4	229	java/lang/Throwable
          //   8	13	229	java/lang/Throwable
          //   13	21	229	java/lang/Throwable
          //   25	40	229	java/lang/Throwable
          //   46	79	229	java/lang/Throwable
          //   82	111	229	java/lang/Throwable
          //   121	127	229	java/lang/Throwable
          //   142	147	229	java/lang/Throwable
          //   156	160	229	java/lang/Throwable
          //   160	164	229	java/lang/Throwable
          //   168	176	229	java/lang/Throwable
          //   180	184	229	java/lang/Throwable
          //   184	192	229	java/lang/Throwable
          //   204	210	229	java/lang/Throwable
          //   221	229	229	java/lang/Throwable
          //   121	127	241	java/lang/Exception
          //   142	147	245	java/lang/Exception
          //   156	160	249	java/lang/Exception
          //   204	210	253	java/lang/Exception
        }
      };
      Executor localExecutor = mReadExecutor;
      paramAtomicBoolean = Task.call(paramAtomicBoolean, localExecutor);
      return paramAtomicBoolean;
    }
    catch (Exception paramAtomicBoolean)
    {
      FLog.w(TAG, paramAtomicBoolean, "Failed to schedule disk-cache read for %s", new Object[] { paramCacheKey.getUriString() });
    }
    return Task.forError(paramAtomicBoolean);
  }
  
  /* Error */
  private com.facebook.common.memory.PooledByteBuffer readFromDiskCache(CacheKey paramCacheKey)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 36	com/facebook/imagepipeline/cache/BufferedDiskCache:TAG	Ljava/lang/Class;
    //   3: astore_2
    //   4: aload_2
    //   5: ldc -87
    //   7: aload_1
    //   8: invokeinterface 111 1 0
    //   13: invokestatic 117	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
    //   16: aload_0
    //   17: getfield 43	com/facebook/imagepipeline/cache/BufferedDiskCache:mFileCache	Lcom/facebook/cache/disk/FileCache;
    //   20: astore_2
    //   21: aload_2
    //   22: aload_1
    //   23: invokeinterface 173 2 0
    //   28: astore_2
    //   29: aload_2
    //   30: ifnonnull +32 -> 62
    //   33: getstatic 36	com/facebook/imagepipeline/cache/BufferedDiskCache:TAG	Ljava/lang/Class;
    //   36: astore_2
    //   37: aload_2
    //   38: ldc -81
    //   40: aload_1
    //   41: invokeinterface 111 1 0
    //   46: invokestatic 117	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
    //   49: aload_0
    //   50: getfield 53	com/facebook/imagepipeline/cache/BufferedDiskCache:mImageCacheStatsTracker	Lcom/facebook/imagepipeline/cache/ImageCacheStatsTracker;
    //   53: astore_2
    //   54: aload_2
    //   55: invokeinterface 178 1 0
    //   60: aconst_null
    //   61: areturn
    //   62: getstatic 36	com/facebook/imagepipeline/cache/BufferedDiskCache:TAG	Ljava/lang/Class;
    //   65: astore_3
    //   66: aload_3
    //   67: ldc -76
    //   69: aload_1
    //   70: invokeinterface 111 1 0
    //   75: invokestatic 117	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
    //   78: aload_0
    //   79: getfield 53	com/facebook/imagepipeline/cache/BufferedDiskCache:mImageCacheStatsTracker	Lcom/facebook/imagepipeline/cache/ImageCacheStatsTracker;
    //   82: astore_3
    //   83: aload_3
    //   84: aload_1
    //   85: invokeinterface 183 2 0
    //   90: aload_2
    //   91: invokeinterface 189 1 0
    //   96: astore_3
    //   97: aload_0
    //   98: getfield 45	com/facebook/imagepipeline/cache/BufferedDiskCache:mPooledByteBufferFactory	Lcom/facebook/common/memory/PooledByteBufferFactory;
    //   101: aload_3
    //   102: aload_2
    //   103: invokeinterface 193 1 0
    //   108: l2i
    //   109: invokeinterface 199 3 0
    //   114: astore_2
    //   115: aload_3
    //   116: invokevirtual 202	java/io/InputStream:close	()V
    //   119: getstatic 36	com/facebook/imagepipeline/cache/BufferedDiskCache:TAG	Ljava/lang/Class;
    //   122: astore_3
    //   123: aload_3
    //   124: ldc -52
    //   126: aload_1
    //   127: invokeinterface 111 1 0
    //   132: invokestatic 117	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V
    //   135: aload_2
    //   136: areturn
    //   137: astore_2
    //   138: aload_3
    //   139: invokevirtual 202	java/io/InputStream:close	()V
    //   142: aload_2
    //   143: athrow
    //   144: astore_2
    //   145: getstatic 36	com/facebook/imagepipeline/cache/BufferedDiskCache:TAG	Ljava/lang/Class;
    //   148: aload_2
    //   149: ldc -50
    //   151: iconst_1
    //   152: anewarray 4	java/lang/Object
    //   155: dup
    //   156: iconst_0
    //   157: aload_1
    //   158: invokeinterface 111 1 0
    //   163: aastore
    //   164: invokestatic 150	com/facebook/common/logging/FLog:w	(Ljava/lang/Class;Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)V
    //   167: aload_0
    //   168: getfield 53	com/facebook/imagepipeline/cache/BufferedDiskCache:mImageCacheStatsTracker	Lcom/facebook/imagepipeline/cache/ImageCacheStatsTracker;
    //   171: invokeinterface 209 1 0
    //   176: aload_2
    //   177: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	this	BufferedDiskCache
    //   0	178	1	paramCacheKey	CacheKey
    //   3	133	2	localObject1	Object
    //   137	6	2	localThrowable	Throwable
    //   144	33	2	localIOException	IOException
    //   65	74	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   97	115	137	java/lang/Throwable
    //   4	16	144	java/io/IOException
    //   21	29	144	java/io/IOException
    //   37	49	144	java/io/IOException
    //   54	60	144	java/io/IOException
    //   66	78	144	java/io/IOException
    //   83	97	144	java/io/IOException
    //   115	119	144	java/io/IOException
    //   123	135	144	java/io/IOException
    //   138	144	144	java/io/IOException
  }
  
  private void writeToDiskCache(CacheKey paramCacheKey, final EncodedImage paramEncodedImage)
  {
    FLog.v(TAG, "About to write to disk-cache for key %s", paramCacheKey.getUriString());
    FileCache localFileCache = mFileCache;
    try
    {
      localFileCache.insert(paramCacheKey, new WriterCallback()
      {
        public void write(OutputStream paramAnonymousOutputStream)
          throws IOException
        {
          mPooledByteStreams.copy(paramEncodedImage.getInputStream(), paramAnonymousOutputStream);
        }
      });
      paramEncodedImage = TAG;
      FLog.v(paramEncodedImage, "Successful disk-cache write for key %s", paramCacheKey.getUriString());
      return;
    }
    catch (IOException paramEncodedImage)
    {
      FLog.w(TAG, paramEncodedImage, "Failed to write to disk-cache for key %s", new Object[] { paramCacheKey.getUriString() });
    }
  }
  
  public Task addTask(CacheKey paramCacheKey, AtomicBoolean paramAtomicBoolean)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("BufferedDiskCache#get");
      }
      EncodedImage localEncodedImage = mStagingArea.get(paramCacheKey);
      if (localEncodedImage != null)
      {
        paramAtomicBoolean = foundPinnedImage(paramCacheKey, localEncodedImage);
        paramCacheKey = paramAtomicBoolean;
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return paramAtomicBoolean;
        }
      }
      else
      {
        paramAtomicBoolean = getAsync(paramCacheKey, paramAtomicBoolean);
        paramCacheKey = paramAtomicBoolean;
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return paramAtomicBoolean;
        }
      }
    }
    catch (Throwable paramCacheKey)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramCacheKey;
    }
    return paramCacheKey;
  }
  
  public Task clearAll()
  {
    mStagingArea.clearAll();
    try
    {
      Object localObject = new Callable()
      {
        public Void call()
          throws Exception
        {
          mStagingArea.clearAll();
          mFileCache.clearAll();
          return null;
        }
      };
      Executor localExecutor = mWriteExecutor;
      localObject = Task.call((Callable)localObject, localExecutor);
      return localObject;
    }
    catch (Exception localException)
    {
      FLog.w(TAG, localException, "Failed to schedule disk-cache clear", new Object[0]);
      return Task.forError(localException);
    }
  }
  
  public Task contains(CacheKey paramCacheKey)
  {
    if (containsSync(paramCacheKey)) {
      return Task.forResult(Boolean.valueOf(true));
    }
    return containsAsync(paramCacheKey);
  }
  
  public boolean containsSync(CacheKey paramCacheKey)
  {
    return (mStagingArea.containsKey(paramCacheKey)) || (mFileCache.hasKeySync(paramCacheKey));
  }
  
  public boolean diskCheckSync(CacheKey paramCacheKey)
  {
    if (containsSync(paramCacheKey)) {
      return true;
    }
    return checkInStagingAreaAndFileCache(paramCacheKey);
  }
  
  public long getSize()
  {
    return mFileCache.getSize();
  }
  
  public Task remove(final CacheKey paramCacheKey)
  {
    Preconditions.checkNotNull(paramCacheKey);
    mStagingArea.remove(paramCacheKey);
    try
    {
      Object localObject = new Callable()
      {
        public Void call()
          throws Exception
        {
          try
          {
            boolean bool = FrescoSystrace.isTracing();
            if (bool) {
              FrescoSystrace.beginSection("BufferedDiskCache#remove");
            }
            mStagingArea.remove(paramCacheKey);
            mFileCache.remove(paramCacheKey);
            if (FrescoSystrace.isTracing()) {
              FrescoSystrace.endSection();
            }
            return null;
          }
          catch (Throwable localThrowable)
          {
            if (FrescoSystrace.isTracing()) {
              FrescoSystrace.endSection();
            }
            throw localThrowable;
          }
        }
      };
      Executor localExecutor = mWriteExecutor;
      localObject = Task.call((Callable)localObject, localExecutor);
      return localObject;
    }
    catch (Exception localException)
    {
      FLog.w(TAG, localException, "Failed to schedule disk-cache remove for %s", new Object[] { paramCacheKey.getUriString() });
      return Task.forError(localException);
    }
  }
  
  public void startListening(final CacheKey paramCacheKey, EncodedImage paramEncodedImage)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("BufferedDiskCache#put");
      }
      Preconditions.checkNotNull(paramCacheKey);
      Preconditions.checkArgument(EncodedImage.isValid(paramEncodedImage));
      mStagingArea.put(paramCacheKey, paramEncodedImage);
      final EncodedImage localEncodedImage = EncodedImage.cloneOrNull(paramEncodedImage);
      Executor localExecutor = mWriteExecutor;
      try
      {
        localExecutor.execute(new Runnable()
        {
          public void run()
          {
            try
            {
              boolean bool = FrescoSystrace.isTracing();
              if (bool) {
                FrescoSystrace.beginSection("BufferedDiskCache#putAsync");
              }
              BufferedDiskCache.this.writeToDiskCache(paramCacheKey, localEncodedImage);
              mStagingArea.remove(paramCacheKey, localEncodedImage);
              EncodedImage.closeSafely(localEncodedImage);
              if (FrescoSystrace.isTracing())
              {
                FrescoSystrace.endSection();
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              mStagingArea.remove(paramCacheKey, localEncodedImage);
              EncodedImage.closeSafely(localEncodedImage);
              if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
              }
              throw localThrowable;
            }
          }
        });
      }
      catch (Exception localException)
      {
        FLog.w(TAG, localException, "Failed to schedule disk-cache write for %s", new Object[] { paramCacheKey.getUriString() });
        mStagingArea.remove(paramCacheKey, paramEncodedImage);
        EncodedImage.closeSafely(localEncodedImage);
      }
      if (FrescoSystrace.isTracing())
      {
        FrescoSystrace.endSection();
        return;
      }
    }
    catch (Throwable paramCacheKey)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramCacheKey;
    }
  }
}
