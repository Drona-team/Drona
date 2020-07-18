package com.google.android.exoplayer2.upstream;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public final class Loader
  implements LoaderErrorThrower
{
  private static final int ACTION_TYPE_DONT_RETRY = 2;
  private static final int ACTION_TYPE_DONT_RETRY_FATAL = 3;
  private static final int ACTION_TYPE_RETRY = 0;
  private static final int ACTION_TYPE_RETRY_AND_RESET_ERROR_COUNT = 1;
  public static final LoadErrorAction DONT_RETRY = new LoadErrorAction(2, -9223372036854775807L, null);
  public static final LoadErrorAction DONT_RETRY_FATAL = new LoadErrorAction(3, -9223372036854775807L, null);
  public static final LoadErrorAction RETRY = createRetryAction(false, -9223372036854775807L);
  public static final LoadErrorAction RETRY_RESET_ERROR_COUNT = createRetryAction(true, -9223372036854775807L);
  private LoadTask<? extends Loadable> currentTask;
  private final ExecutorService downloadExecutorService;
  private IOException fatalError;
  
  public Loader(String paramString)
  {
    downloadExecutorService = Util.newSingleThreadExecutor(paramString);
  }
  
  public static LoadErrorAction createRetryAction(boolean paramBoolean, long paramLong)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void cancelLoading()
  {
    currentTask.cancel(false);
  }
  
  public boolean isLoading()
  {
    return currentTask != null;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    maybeThrowError(Integer.MIN_VALUE);
  }
  
  public void maybeThrowError(int paramInt)
    throws IOException
  {
    if (fatalError == null)
    {
      if (currentTask != null)
      {
        LoadTask localLoadTask = currentTask;
        int i = paramInt;
        if (paramInt == Integer.MIN_VALUE) {
          i = currentTask.defaultMinRetryCount;
        }
        localLoadTask.maybeThrowError(i);
      }
    }
    else {
      throw fatalError;
    }
  }
  
  public void release()
  {
    release(null);
  }
  
  public void release(ReleaseCallback paramReleaseCallback)
  {
    if (currentTask != null) {
      currentTask.cancel(true);
    }
    if (paramReleaseCallback != null) {
      downloadExecutorService.execute(new ReleaseTask(paramReleaseCallback));
    }
    downloadExecutorService.shutdown();
  }
  
  public long startLoading(Loadable paramLoadable, Callback paramCallback, int paramInt)
  {
    Looper localLooper = Looper.myLooper();
    boolean bool;
    if (localLooper != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    fatalError = null;
    long l = SystemClock.elapsedRealtime();
    new LoadTask(localLooper, paramLoadable, paramCallback, paramInt, l).start(0L);
    return l;
  }
  
  public static abstract interface Callback<T extends Loader.Loadable>
  {
    public abstract void onLoadCanceled(Loader.Loadable paramLoadable, long paramLong1, long paramLong2, boolean paramBoolean);
    
    public abstract void onLoadCompleted(Loader.Loadable paramLoadable, long paramLong1, long paramLong2);
    
    public abstract Loader.LoadErrorAction onLoadError(Loader.Loadable paramLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt);
  }
  
  public static final class LoadErrorAction
  {
    private final long retryDelayMillis;
    private final int type;
    
    private LoadErrorAction(int paramInt, long paramLong)
    {
      type = paramInt;
      retryDelayMillis = paramLong;
    }
    
    public boolean isRetry()
    {
      if (type != 0) {
        return type == 1;
      }
      return true;
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private final class LoadTask<T extends Loader.Loadable>
    extends Handler
    implements Runnable
  {
    private static final int MSG_CANCEL = 1;
    private static final int MSG_END_OF_SOURCE = 2;
    private static final int MSG_FATAL_ERROR = 4;
    private static final int MSG_IO_EXCEPTION = 3;
    private static final int MSG_START = 0;
    private static final String TAG = "LoadTask";
    @Nullable
    private Loader.Callback<T> callback;
    private volatile boolean canceled;
    private IOException currentError;
    public final int defaultMinRetryCount;
    private int errorCount;
    private volatile Thread executorThread;
    private final T loadable;
    private volatile boolean released;
    private final long startTimeMs;
    
    public LoadTask(Looper paramLooper, Loader.Loadable paramLoadable, Loader.Callback paramCallback, int paramInt, long paramLong)
    {
      super();
      loadable = paramLoadable;
      callback = paramCallback;
      defaultMinRetryCount = paramInt;
      startTimeMs = paramLong;
    }
    
    private void execute()
    {
      currentError = null;
      downloadExecutorService.execute(currentTask);
    }
    
    private void finish()
    {
      Loader.access$102(Loader.this, null);
    }
    
    private long getRetryDelayMillis()
    {
      return Math.min((errorCount - 1) * 1000, 5000);
    }
    
    public void cancel(boolean paramBoolean)
    {
      released = paramBoolean;
      currentError = null;
      if (hasMessages(0))
      {
        removeMessages(0);
        if (!paramBoolean) {
          sendEmptyMessage(1);
        }
      }
      else
      {
        canceled = true;
        loadable.cancelLoad();
        if (executorThread != null) {
          executorThread.interrupt();
        }
      }
      if (paramBoolean)
      {
        finish();
        long l = SystemClock.elapsedRealtime();
        callback.onLoadCanceled(loadable, l, l - startTimeMs, true);
        callback = null;
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (released) {
        return;
      }
      if (what == 0)
      {
        execute();
        return;
      }
      if (what != 4)
      {
        finish();
        long l1 = SystemClock.elapsedRealtime();
        long l2 = l1 - startTimeMs;
        if (canceled)
        {
          callback.onLoadCanceled(loadable, l1, l2, false);
          return;
        }
        switch (what)
        {
        default: 
          return;
        case 3: 
          currentError = ((IOException)obj);
          errorCount += 1;
          paramMessage = callback.onLoadError(loadable, l1, l2, currentError, errorCount);
          if (type == 3)
          {
            Loader.access$202(Loader.this, currentError);
            return;
          }
          if (type == 2) {
            return;
          }
          if (type == 1) {
            errorCount = 1;
          }
          if (retryDelayMillis != -9223372036854775807L) {
            l1 = retryDelayMillis;
          } else {
            l1 = getRetryDelayMillis();
          }
          start(l1);
          return;
        case 2: 
          try
          {
            callback.onLoadCompleted(loadable, l1, l2);
            return;
          }
          catch (RuntimeException paramMessage)
          {
            Log.e("LoadTask", "Unexpected exception handling load completed", paramMessage);
            Loader.access$202(Loader.this, new Loader.UnexpectedLoaderException(paramMessage));
            return;
          }
        }
        callback.onLoadCanceled(loadable, l1, l2, false);
        return;
      }
      throw ((Error)obj);
    }
    
    public void maybeThrowError(int paramInt)
      throws IOException
    {
      if (currentError != null)
      {
        if (errorCount <= paramInt) {
          return;
        }
        throw currentError;
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: invokestatic 214	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   3: astore_2
      //   4: aload_0
      //   5: aload_2
      //   6: putfield 119	com/google/android/exoplayer2/upstream/Loader$LoadTask:executorThread	Ljava/lang/Thread;
      //   9: aload_0
      //   10: getfield 112	com/google/android/exoplayer2/upstream/Loader$LoadTask:canceled	Z
      //   13: istore_1
      //   14: iload_1
      //   15: ifne +63 -> 78
      //   18: new 216	java/lang/StringBuilder
      //   21: dup
      //   22: invokespecial 218	java/lang/StringBuilder:<init>	()V
      //   25: astore_2
      //   26: aload_2
      //   27: ldc -36
      //   29: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   32: pop
      //   33: aload_0
      //   34: getfield 56	com/google/android/exoplayer2/upstream/Loader$LoadTask:loadable	Lcom/google/android/exoplayer2/upstream/Loader$Loadable;
      //   37: astore_3
      //   38: aload_2
      //   39: aload_3
      //   40: invokevirtual 230	java/lang/Object:getClass	()Ljava/lang/Class;
      //   43: invokevirtual 236	java/lang/Class:getSimpleName	()Ljava/lang/String;
      //   46: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   49: pop
      //   50: aload_2
      //   51: invokevirtual 239	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   54: invokestatic 245	com/google/android/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
      //   57: aload_0
      //   58: getfield 56	com/google/android/exoplayer2/upstream/Loader$LoadTask:loadable	Lcom/google/android/exoplayer2/upstream/Loader$Loadable;
      //   61: invokeinterface 248 1 0
      //   66: invokestatic 251	com/google/android/exoplayer2/util/TraceUtil:endSection	()V
      //   69: goto +9 -> 78
      //   72: astore_2
      //   73: invokestatic 251	com/google/android/exoplayer2/util/TraceUtil:endSection	()V
      //   76: aload_2
      //   77: athrow
      //   78: aload_0
      //   79: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   82: istore_1
      //   83: iload_1
      //   84: ifne +152 -> 236
      //   87: aload_0
      //   88: iconst_2
      //   89: invokevirtual 110	android/os/Handler:sendEmptyMessage	(I)Z
      //   92: pop
      //   93: return
      //   94: astore_2
      //   95: ldc 27
      //   97: ldc -3
      //   99: aload_2
      //   100: invokestatic 192	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   103: aload_0
      //   104: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   107: ifne +12 -> 119
      //   110: aload_0
      //   111: iconst_4
      //   112: aload_2
      //   113: invokevirtual 257	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   116: invokevirtual 260	android/os/Message:sendToTarget	()V
      //   119: aload_2
      //   120: athrow
      //   121: astore_2
      //   122: ldc 27
      //   124: ldc_w 262
      //   127: aload_2
      //   128: invokestatic 192	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   131: aload_0
      //   132: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   135: ifne +101 -> 236
      //   138: aload_0
      //   139: iconst_3
      //   140: new 194	com/google/android/exoplayer2/upstream/Loader$UnexpectedLoaderException
      //   143: dup
      //   144: aload_2
      //   145: invokespecial 197	com/google/android/exoplayer2/upstream/Loader$UnexpectedLoaderException:<init>	(Ljava/lang/Throwable;)V
      //   148: invokevirtual 257	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   151: invokevirtual 260	android/os/Message:sendToTarget	()V
      //   154: return
      //   155: astore_2
      //   156: ldc 27
      //   158: ldc_w 264
      //   161: aload_2
      //   162: invokestatic 192	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   165: aload_0
      //   166: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   169: ifne +67 -> 236
      //   172: aload_0
      //   173: iconst_3
      //   174: new 194	com/google/android/exoplayer2/upstream/Loader$UnexpectedLoaderException
      //   177: dup
      //   178: aload_2
      //   179: invokespecial 197	com/google/android/exoplayer2/upstream/Loader$UnexpectedLoaderException:<init>	(Ljava/lang/Throwable;)V
      //   182: invokevirtual 257	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   185: invokevirtual 260	android/os/Message:sendToTarget	()V
      //   188: return
      //   189: aload_0
      //   190: getfield 112	com/google/android/exoplayer2/upstream/Loader$LoadTask:canceled	Z
      //   193: invokestatic 269	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
      //   196: aload_0
      //   197: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   200: ifne +36 -> 236
      //   203: aload_0
      //   204: iconst_2
      //   205: invokevirtual 110	android/os/Handler:sendEmptyMessage	(I)Z
      //   208: pop
      //   209: return
      //   210: astore_2
      //   211: aload_0
      //   212: getfield 99	com/google/android/exoplayer2/upstream/Loader$LoadTask:released	Z
      //   215: ifne +21 -> 236
      //   218: aload_0
      //   219: iconst_3
      //   220: aload_2
      //   221: invokevirtual 257	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   224: invokevirtual 260	android/os/Message:sendToTarget	()V
      //   227: return
      //   228: astore_2
      //   229: goto -40 -> 189
      //   232: astore_2
      //   233: goto -44 -> 189
      //   236: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	237	0	this	LoadTask
      //   13	71	1	bool	boolean
      //   3	48	2	localObject	Object
      //   72	5	2	localThrowable	Throwable
      //   94	26	2	localError	Error
      //   121	24	2	localOutOfMemoryError	OutOfMemoryError
      //   155	24	2	localException	Exception
      //   210	11	2	localIOException	IOException
      //   228	1	2	localInterruptedException1	InterruptedException
      //   232	1	2	localInterruptedException2	InterruptedException
      //   37	3	3	localLoadable	Loader.Loadable
      // Exception table:
      //   from	to	target	type
      //   57	66	72	java/lang/Throwable
      //   0	4	94	java/lang/Error
      //   4	14	94	java/lang/Error
      //   18	33	94	java/lang/Error
      //   38	57	94	java/lang/Error
      //   66	69	94	java/lang/Error
      //   73	78	94	java/lang/Error
      //   78	83	94	java/lang/Error
      //   87	93	94	java/lang/Error
      //   0	4	121	java/lang/OutOfMemoryError
      //   18	33	121	java/lang/OutOfMemoryError
      //   38	57	121	java/lang/OutOfMemoryError
      //   66	69	121	java/lang/OutOfMemoryError
      //   73	78	121	java/lang/OutOfMemoryError
      //   87	93	121	java/lang/OutOfMemoryError
      //   0	4	155	java/lang/Exception
      //   18	33	155	java/lang/Exception
      //   38	57	155	java/lang/Exception
      //   66	69	155	java/lang/Exception
      //   73	78	155	java/lang/Exception
      //   87	93	155	java/lang/Exception
      //   0	4	210	java/io/IOException
      //   18	33	210	java/io/IOException
      //   38	57	210	java/io/IOException
      //   66	69	210	java/io/IOException
      //   73	78	210	java/io/IOException
      //   87	93	210	java/io/IOException
      //   0	4	228	java/lang/InterruptedException
      //   18	33	228	java/lang/InterruptedException
      //   38	57	228	java/lang/InterruptedException
      //   66	69	232	java/lang/InterruptedException
      //   73	78	232	java/lang/InterruptedException
      //   87	93	232	java/lang/InterruptedException
    }
    
    public void start(long paramLong)
    {
      boolean bool;
      if (currentTask == null) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkState(bool);
      Loader.access$102(Loader.this, this);
      if (paramLong > 0L)
      {
        sendEmptyMessageDelayed(0, paramLong);
        return;
      }
      execute();
    }
  }
  
  public static abstract interface Loadable
  {
    public abstract void cancelLoad();
    
    public abstract void load()
      throws IOException, InterruptedException;
  }
  
  public static abstract interface ReleaseCallback
  {
    public abstract void onLoaderReleased();
  }
  
  private static final class ReleaseTask
    implements Runnable
  {
    private final Loader.ReleaseCallback callback;
    
    public ReleaseTask(Loader.ReleaseCallback paramReleaseCallback)
    {
      callback = paramReleaseCallback;
    }
    
    public void run()
    {
      callback.onLoaderReleased();
    }
  }
  
  public static final class UnexpectedLoaderException
    extends IOException
  {
    public UnexpectedLoaderException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}
