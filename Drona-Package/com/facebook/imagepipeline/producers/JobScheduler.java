package com.facebook.imagepipeline.producers;

import android.os.SystemClock;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.image.EncodedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

public class JobScheduler
{
  static final String QUEUE_TIME_KEY = "queueTime";
  private final Runnable mDoJobRunnable;
  @VisibleForTesting
  @GuardedBy("this")
  EncodedImage mEncodedImage;
  private final Executor mExecutor;
  private final JobRunnable mJobRunnable;
  @VisibleForTesting
  @GuardedBy("this")
  long mJobStartTime;
  @VisibleForTesting
  @GuardedBy("this")
  JobState mJobState;
  @VisibleForTesting
  @GuardedBy("this")
  long mJobSubmitTime;
  private final int mMinimumJobIntervalMs;
  @VisibleForTesting
  @GuardedBy("this")
  int mStatus;
  private final Runnable mSubmitJobRunnable;
  
  public JobScheduler(Executor paramExecutor, JobRunnable paramJobRunnable, int paramInt)
  {
    mExecutor = paramExecutor;
    mJobRunnable = paramJobRunnable;
    mMinimumJobIntervalMs = paramInt;
    mDoJobRunnable = new Runnable()
    {
      public void run()
      {
        JobScheduler.this.doJob();
      }
    };
    mSubmitJobRunnable = new Runnable()
    {
      public void run()
      {
        JobScheduler.this.submitJob();
      }
    };
    mEncodedImage = null;
    mStatus = 0;
    mJobState = JobState.IDLE;
    mJobSubmitTime = 0L;
    mJobStartTime = 0L;
  }
  
  /* Error */
  private void doJob()
  {
    // Byte code:
    //   0: invokestatic 93	android/os/SystemClock:uptimeMillis	()J
    //   3: lstore_2
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield 65	com/facebook/imagepipeline/producers/JobScheduler:mEncodedImage	Lcom/facebook/imagepipeline/image/EncodedImage;
    //   10: astore 5
    //   12: aload_0
    //   13: getfield 67	com/facebook/imagepipeline/producers/JobScheduler:mStatus	I
    //   16: istore_1
    //   17: aload_0
    //   18: aconst_null
    //   19: putfield 65	com/facebook/imagepipeline/producers/JobScheduler:mEncodedImage	Lcom/facebook/imagepipeline/image/EncodedImage;
    //   22: aload_0
    //   23: iconst_0
    //   24: putfield 67	com/facebook/imagepipeline/producers/JobScheduler:mStatus	I
    //   27: aload_0
    //   28: getstatic 96	com/facebook/imagepipeline/producers/JobScheduler$JobState:RUNNING	Lcom/facebook/imagepipeline/producers/JobScheduler$JobState;
    //   31: putfield 72	com/facebook/imagepipeline/producers/JobScheduler:mJobState	Lcom/facebook/imagepipeline/producers/JobScheduler$JobState;
    //   34: aload_0
    //   35: lload_2
    //   36: putfield 76	com/facebook/imagepipeline/producers/JobScheduler:mJobStartTime	J
    //   39: aload_0
    //   40: monitorexit
    //   41: aload 5
    //   43: iload_1
    //   44: invokestatic 100	com/facebook/imagepipeline/producers/JobScheduler:shouldProcess	(Lcom/facebook/imagepipeline/image/EncodedImage;I)Z
    //   47: istore 4
    //   49: iload 4
    //   51: ifeq +15 -> 66
    //   54: aload_0
    //   55: getfield 53	com/facebook/imagepipeline/producers/JobScheduler:mJobRunnable	Lcom/facebook/imagepipeline/producers/JobScheduler$JobRunnable;
    //   58: aload 5
    //   60: iload_1
    //   61: invokeinterface 104 3 0
    //   66: aload 5
    //   68: invokestatic 110	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
    //   71: aload_0
    //   72: invokespecial 113	com/facebook/imagepipeline/producers/JobScheduler:onJobFinished	()V
    //   75: return
    //   76: astore 6
    //   78: aload 5
    //   80: invokestatic 110	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
    //   83: aload_0
    //   84: invokespecial 113	com/facebook/imagepipeline/producers/JobScheduler:onJobFinished	()V
    //   87: aload 6
    //   89: athrow
    //   90: astore 5
    //   92: aload_0
    //   93: monitorexit
    //   94: aload 5
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	JobScheduler
    //   16	45	1	i	int
    //   3	33	2	l	long
    //   47	3	4	bool	boolean
    //   10	69	5	localEncodedImage	EncodedImage
    //   90	5	5	localThrowable1	Throwable
    //   76	12	6	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   41	49	76	java/lang/Throwable
    //   54	66	76	java/lang/Throwable
    //   6	41	90	java/lang/Throwable
    //   92	94	90	java/lang/Throwable
  }
  
  private void enqueueJob(long paramLong)
  {
    if (paramLong > 0L)
    {
      JobStartExecutorSupplier.createTimer().schedule(mSubmitJobRunnable, paramLong, TimeUnit.MILLISECONDS);
      return;
    }
    mSubmitJobRunnable.run();
  }
  
  private void onJobFinished()
  {
    long l2 = SystemClock.uptimeMillis();
    try
    {
      long l1;
      int i;
      if (mJobState == JobState.RUNNING_AND_PENDING)
      {
        l1 = Math.max(mJobStartTime + mMinimumJobIntervalMs, l2);
        i = 1;
        mJobSubmitTime = l2;
        mJobState = JobState.QUEUED;
      }
      else
      {
        mJobState = JobState.IDLE;
        l1 = 0L;
        i = 0;
      }
      if (i != 0)
      {
        enqueueJob(l1 - l2);
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static boolean shouldProcess(EncodedImage paramEncodedImage, int paramInt)
  {
    return (BaseConsumer.isLast(paramInt)) || (BaseConsumer.statusHasFlag(paramInt, 4)) || (EncodedImage.isValid(paramEncodedImage));
  }
  
  private void submitJob()
  {
    mExecutor.execute(mDoJobRunnable);
  }
  
  public void clearJob()
  {
    try
    {
      EncodedImage localEncodedImage = mEncodedImage;
      mEncodedImage = null;
      mStatus = 0;
      EncodedImage.closeSafely(localEncodedImage);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getQueuedTime()
  {
    try
    {
      long l1 = mJobStartTime;
      long l2 = mJobSubmitTime;
      return l1 - l2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean scheduleJob()
  {
    long l2 = SystemClock.uptimeMillis();
    try
    {
      boolean bool = shouldProcess(mEncodedImage, mStatus);
      i = 0;
      if (!bool) {
        return false;
      }
      switch (3.$SwitchMap$com$facebook$imagepipeline$producers$JobScheduler$JobState[mJobState.ordinal()])
      {
      case 3: 
        mJobState = JobState.RUNNING_AND_PENDING;
      }
    }
    catch (Throwable localThrowable)
    {
      int i;
      throw localThrowable;
    }
    long l1 = Math.max(mJobStartTime + mMinimumJobIntervalMs, l2);
    mJobSubmitTime = l2;
    mJobState = JobState.QUEUED;
    i = 1;
    for (;;)
    {
      if (i != 0)
      {
        enqueueJob(l1 - l2);
        return true;
      }
      return true;
      l1 = 0L;
    }
  }
  
  public boolean updateJob(EncodedImage paramEncodedImage, int paramInt)
  {
    if (!shouldProcess(paramEncodedImage, paramInt)) {
      return false;
    }
    try
    {
      EncodedImage localEncodedImage = mEncodedImage;
      mEncodedImage = EncodedImage.cloneOrNull(paramEncodedImage);
      mStatus = paramInt;
      EncodedImage.closeSafely(localEncodedImage);
      return true;
    }
    catch (Throwable paramEncodedImage)
    {
      throw paramEncodedImage;
    }
  }
  
  public static abstract interface JobRunnable
  {
    public abstract void remainder(EncodedImage paramEncodedImage, int paramInt);
  }
  
  @VisibleForTesting
  static class JobStartExecutorSupplier
  {
    private static ScheduledExecutorService sJobStarterExecutor;
    
    JobStartExecutorSupplier() {}
    
    static ScheduledExecutorService createTimer()
    {
      if (sJobStarterExecutor == null) {
        sJobStarterExecutor = Executors.newSingleThreadScheduledExecutor();
      }
      return sJobStarterExecutor;
    }
  }
  
  @VisibleForTesting
  static enum JobState
  {
    IDLE,  QUEUED,  RUNNING,  RUNNING_AND_PENDING;
  }
}
