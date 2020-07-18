package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.ICancelToken;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.Releasable;
import com.google.android.gms.common.package_6.ResultTransform;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.TransformedResult;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.base.zap;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@KeepForSdk
@KeepName
public abstract class BasePendingResult<R extends com.google.android.gms.common.api.Result>
  extends com.google.android.gms.common.api.PendingResult<R>
{
  static final ThreadLocal<Boolean> zadn = new CycleDetectingLockFactory.1();
  @KeepName
  private com.google.android.gms.common.api.internal.BasePendingResult.zaa mResultGuardian;
  private Status mStatus;
  private R zacj;
  private final Object zado = new Object();
  private final com.google.android.gms.common.api.internal.BasePendingResult.CallbackHandler<R> zadp;
  private final WeakReference<com.google.android.gms.common.api.GoogleApiClient> zadq;
  private final CountDownLatch zadr = new CountDownLatch(1);
  private final ArrayList<com.google.android.gms.common.api.PendingResult.StatusListener> zads = new ArrayList();
  private com.google.android.gms.common.api.ResultCallback<? super R> zadt;
  private final AtomicReference<com.google.android.gms.common.api.internal.zacs> zadu = new AtomicReference();
  private volatile boolean zadv;
  private boolean zadw;
  private boolean zadx;
  private ICancelToken zady;
  private volatile com.google.android.gms.common.api.internal.zacm<R> zadz;
  private boolean zaea = false;
  
  BasePendingResult()
  {
    zadp = new CallbackHandler(Looper.getMainLooper());
    zadq = new WeakReference(null);
  }
  
  protected BasePendingResult(Looper paramLooper)
  {
    zadp = new CallbackHandler(paramLooper);
    zadq = new WeakReference(null);
  }
  
  protected BasePendingResult(com.google.android.gms.common.package_6.GoogleApiClient paramGoogleApiClient)
  {
    Looper localLooper;
    if (paramGoogleApiClient != null) {
      localLooper = paramGoogleApiClient.getLooper();
    } else {
      localLooper = Looper.getMainLooper();
    }
    zadp = new CallbackHandler(localLooper);
    zadq = new WeakReference(paramGoogleApiClient);
  }
  
  protected BasePendingResult(CallbackHandler paramCallbackHandler)
  {
    zadp = ((CallbackHandler)Preconditions.checkNotNull(paramCallbackHandler, "CallbackHandler must not be null"));
    zadq = new WeakReference(null);
  }
  
  public static void clear(com.google.android.gms.common.package_6.Result paramResult)
  {
    if ((paramResult instanceof Releasable)) {
      try
      {
        ((Releasable)paramResult).release();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        paramResult = String.valueOf(paramResult);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramResult).length() + 18);
        localStringBuilder.append("Unable to release ");
        localStringBuilder.append(paramResult);
        Log.w("BasePendingResult", localStringBuilder.toString(), localRuntimeException);
      }
    }
  }
  
  private final void close(com.google.android.gms.common.package_6.Result paramResult)
  {
    zacj = paramResult;
    zady = null;
    zadr.countDown();
    mStatus = zacj.getStatus();
    if (zadw)
    {
      zadt = null;
    }
    else if (zadt == null)
    {
      if ((zacj instanceof Releasable)) {
        mResultGuardian = new zaa(null);
      }
    }
    else
    {
      zadp.removeMessages(2);
      zadp.doToast(zadt, iterator());
    }
    paramResult = (ArrayList)zads;
    int j = paramResult.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = paramResult.get(i);
      i += 1;
      ((com.google.android.gms.common.package_6.PendingResult.StatusListener)localObject).onComplete(mStatus);
    }
    zads.clear();
  }
  
  private final com.google.android.gms.common.package_6.Result iterator()
  {
    Object localObject = zado;
    try
    {
      Preconditions.checkState(zadv ^ true, "Result has already been consumed.");
      Preconditions.checkState(isReady(), "Result is not ready.");
      com.google.android.gms.common.package_6.Result localResult = zacj;
      zacj = null;
      zadt = null;
      zadv = true;
      localObject = (zacs)zadu.getAndSet(null);
      if (localObject != null)
      {
        ((zacs)localObject).andNot(this);
        return localResult;
      }
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
    return localThrowable;
  }
  
  public final void addStatusListener(com.google.android.gms.common.package_6.PendingResult.StatusListener paramStatusListener)
  {
    boolean bool;
    if (paramStatusListener != null) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "Callback cannot be null.");
    Object localObject = zado;
    try
    {
      if (isReady()) {
        paramStatusListener.onComplete(mStatus);
      } else {
        zads.add(paramStatusListener);
      }
      return;
    }
    catch (Throwable paramStatusListener)
    {
      throw paramStatusListener;
    }
  }
  
  public final com.google.android.gms.common.package_6.Result await()
  {
    Preconditions.checkNotMainThread("await must not be called on the UI thread");
    boolean bool2 = zadv;
    boolean bool1 = true;
    Preconditions.checkState(bool2 ^ true, "Result has already been consumed");
    if (zadz != null) {
      bool1 = false;
    }
    Preconditions.checkState(bool1, "Cannot await if then() has been called.");
    CountDownLatch localCountDownLatch = zadr;
    try
    {
      localCountDownLatch.await();
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
    await(Status.RESULT_INTERRUPTED);
    Preconditions.checkState(isReady(), "Result is not ready.");
    return iterator();
  }
  
  public final com.google.android.gms.common.package_6.Result await(long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramLong > 0L) {
      Preconditions.checkNotMainThread("await must not be called on the UI thread when time is greater than zero.");
    }
    boolean bool2 = zadv;
    boolean bool1 = true;
    Preconditions.checkState(bool2 ^ true, "Result has already been consumed.");
    if (zadz != null) {
      bool1 = false;
    }
    Preconditions.checkState(bool1, "Cannot await if then() has been called.");
    CountDownLatch localCountDownLatch = zadr;
    try
    {
      bool1 = localCountDownLatch.await(paramLong, paramTimeUnit);
      if (bool1) {
        break label90;
      }
      paramTimeUnit = Status.RESULT_TIMEOUT;
      await(paramTimeUnit);
    }
    catch (InterruptedException paramTimeUnit)
    {
      for (;;) {}
    }
    await(Status.RESULT_INTERRUPTED);
    label90:
    Preconditions.checkState(isReady(), "Result is not ready.");
    return iterator();
  }
  
  public final void await(Status paramStatus)
  {
    Object localObject = zado;
    try
    {
      if (!isReady())
      {
        setResult(createFailedResult(paramStatus));
        zadx = true;
      }
      return;
    }
    catch (Throwable paramStatus)
    {
      throw paramStatus;
    }
  }
  
  /* Error */
  public void cancel()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 67	com/google/android/gms/common/package_6/internal/BasePendingResult:zado	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 177	com/google/android/gms/common/package_6/internal/BasePendingResult:zadw	Z
    //   11: ifne +59 -> 70
    //   14: aload_0
    //   15: getfield 216	com/google/android/gms/common/package_6/internal/BasePendingResult:zadv	Z
    //   18: ifeq +6 -> 24
    //   21: goto +49 -> 70
    //   24: aload_0
    //   25: getfield 164	com/google/android/gms/common/package_6/internal/BasePendingResult:zady	Lcom/google/android/gms/common/internal/ICancelToken;
    //   28: astore_2
    //   29: aload_2
    //   30: ifnull +14 -> 44
    //   33: aload_0
    //   34: getfield 164	com/google/android/gms/common/package_6/internal/BasePendingResult:zady	Lcom/google/android/gms/common/internal/ICancelToken;
    //   37: astore_2
    //   38: aload_2
    //   39: invokeinterface 300 1 0
    //   44: aload_0
    //   45: getfield 162	com/google/android/gms/common/package_6/internal/BasePendingResult:zacj	Lcom/google/android/gms/common/package_6/Result;
    //   48: invokestatic 302	com/google/android/gms/common/package_6/internal/BasePendingResult:clear	(Lcom/google/android/gms/common/package_6/Result;)V
    //   51: aload_0
    //   52: iconst_1
    //   53: putfield 177	com/google/android/gms/common/package_6/internal/BasePendingResult:zadw	Z
    //   56: aload_0
    //   57: aload_0
    //   58: getstatic 305	com/google/android/gms/common/package_6/Status:RESULT_CANCELED	Lcom/google/android/gms/common/package_6/Status;
    //   61: invokevirtual 288	com/google/android/gms/common/package_6/internal/BasePendingResult:createFailedResult	(Lcom/google/android/gms/common/package_6/Status;)Lcom/google/android/gms/common/package_6/Result;
    //   64: invokespecial 307	com/google/android/gms/common/package_6/internal/BasePendingResult:close	(Lcom/google/android/gms/common/package_6/Result;)V
    //   67: aload_1
    //   68: monitorexit
    //   69: return
    //   70: aload_1
    //   71: monitorexit
    //   72: return
    //   73: astore_2
    //   74: aload_1
    //   75: monitorexit
    //   76: aload_2
    //   77: athrow
    //   78: astore_2
    //   79: goto -35 -> 44
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	this	BasePendingResult
    //   4	71	1	localObject	Object
    //   28	11	2	localICancelToken	ICancelToken
    //   73	4	2	localThrowable	Throwable
    //   78	1	2	localRemoteException	android.os.RemoteException
    // Exception table:
    //   from	to	target	type
    //   7	21	73	java/lang/Throwable
    //   24	29	73	java/lang/Throwable
    //   38	44	73	java/lang/Throwable
    //   44	69	73	java/lang/Throwable
    //   70	72	73	java/lang/Throwable
    //   74	76	73	java/lang/Throwable
    //   38	44	78	android/os/RemoteException
  }
  
  public final boolean compareAndSet()
  {
    Object localObject = zado;
    try
    {
      if (((com.google.android.gms.common.package_6.GoogleApiClient)zadq.get() == null) || (!zaea)) {
        cancel();
      }
      boolean bool = isCanceled();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected abstract com.google.android.gms.common.package_6.Result createFailedResult(Status paramStatus);
  
  public final Integer getValue()
  {
    return null;
  }
  
  public boolean isCanceled()
  {
    Object localObject = zado;
    try
    {
      boolean bool = zadw;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final boolean isReady()
  {
    return zadr.getCount() == 0L;
  }
  
  public final void put()
  {
    boolean bool;
    if ((!zaea) && (!((Boolean)zadn.get()).booleanValue())) {
      bool = false;
    } else {
      bool = true;
    }
    zaea = bool;
  }
  
  public final void remove(zacs paramZacs)
  {
    zadu.set(paramZacs);
  }
  
  protected final void setCancelToken(ICancelToken paramICancelToken)
  {
    Object localObject = zado;
    try
    {
      zady = paramICancelToken;
      return;
    }
    catch (Throwable paramICancelToken)
    {
      throw paramICancelToken;
    }
  }
  
  public final void setResult(com.google.android.gms.common.package_6.Result paramResult)
  {
    Object localObject = zado;
    try
    {
      if ((!zadx) && (!zadw))
      {
        isReady();
        Preconditions.checkState(isReady() ^ true, "Results have already been set");
        Preconditions.checkState(zadv ^ true, "Result has already been consumed");
        close(paramResult);
        return;
      }
      clear(paramResult);
      return;
    }
    catch (Throwable paramResult)
    {
      throw paramResult;
    }
  }
  
  public final void setResultCallback(com.google.android.gms.common.package_6.ResultCallback paramResultCallback)
  {
    Object localObject = zado;
    boolean bool1;
    if (paramResultCallback == null)
    {
      try
      {
        zadt = null;
        return;
      }
      catch (Throwable paramResultCallback) {}
    }
    else
    {
      boolean bool2 = zadv;
      bool1 = true;
      Preconditions.checkState(bool2 ^ true, "Result has already been consumed.");
      if (zadz != null) {
        break label105;
      }
    }
    for (;;)
    {
      Preconditions.checkState(bool1, "Cannot set callbacks if then() has been called.");
      if (isCanceled()) {
        return;
      }
      if (isReady()) {
        zadp.doToast(paramResultCallback, iterator());
      } else {
        zadt = paramResultCallback;
      }
      return;
      throw paramResultCallback;
      label105:
      bool1 = false;
    }
  }
  
  public final void setResultCallback(com.google.android.gms.common.package_6.ResultCallback paramResultCallback, long paramLong, TimeUnit paramTimeUnit)
  {
    Object localObject = zado;
    boolean bool1;
    if (paramResultCallback == null)
    {
      try
      {
        zadt = null;
        return;
      }
      catch (Throwable paramResultCallback) {}
    }
    else
    {
      boolean bool2 = zadv;
      bool1 = true;
      Preconditions.checkState(bool2 ^ true, "Result has already been consumed.");
      if (zadz != null) {
        break label133;
      }
    }
    for (;;)
    {
      Preconditions.checkState(bool1, "Cannot set callbacks if then() has been called.");
      if (isCanceled()) {
        return;
      }
      if (isReady())
      {
        zadp.doToast(paramResultCallback, iterator());
      }
      else
      {
        zadt = paramResultCallback;
        paramResultCallback = zadp;
        paramLong = paramTimeUnit.toMillis(paramLong);
        paramResultCallback.sendMessageDelayed(paramResultCallback.obtainMessage(2, this), paramLong);
      }
      return;
      throw paramResultCallback;
      label133:
      bool1 = false;
    }
  }
  
  public TransformedResult then(ResultTransform paramResultTransform)
  {
    Preconditions.checkState(zadv ^ true, "Result has already been consumed.");
    Object localObject = zado;
    for (;;)
    {
      try
      {
        zacm localZacm = zadz;
        boolean bool2 = false;
        if (localZacm == null)
        {
          bool1 = true;
          Preconditions.checkState(bool1, "Cannot call then() twice.");
          bool1 = bool2;
          if (zadt == null) {
            bool1 = true;
          }
          Preconditions.checkState(bool1, "Cannot call then() if callbacks are set.");
          Preconditions.checkState(zadw ^ true, "Cannot call then() if result was canceled.");
          zaea = true;
          zadz = new zacm(zadq);
          paramResultTransform = zadz.then(paramResultTransform);
          if (isReady()) {
            zadp.doToast(zadz, iterator());
          } else {
            zadt = zadz;
          }
          return paramResultTransform;
        }
      }
      catch (Throwable paramResultTransform)
      {
        throw paramResultTransform;
      }
      boolean bool1 = false;
    }
  }
  
  @VisibleForTesting
  public class CallbackHandler<R extends com.google.android.gms.common.api.Result>
    extends zap
  {
    public CallbackHandler()
    {
      this();
    }
    
    public CallbackHandler()
    {
      super();
    }
    
    public final void doToast(com.google.android.gms.common.package_6.ResultCallback paramResultCallback, com.google.android.gms.common.package_6.Result paramResult)
    {
      sendMessage(obtainMessage(1, new Pair(paramResultCallback, paramResult)));
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (what)
      {
      default: 
        int i = what;
        paramMessage = new StringBuilder(45);
        paramMessage.append("Don't know how to handle message: ");
        paramMessage.append(i);
        Log.wtf("BasePendingResult", paramMessage.toString(), new Exception());
        return;
      case 2: 
        ((BasePendingResult)obj).await(Status.RESULT_TIMEOUT);
        return;
      }
      Object localObject = (Pair)obj;
      paramMessage = (com.google.android.gms.common.package_6.ResultCallback)first;
      localObject = (com.google.android.gms.common.package_6.Result)second;
      try
      {
        paramMessage.onResult((com.google.android.gms.common.package_6.Result)localObject);
        return;
      }
      catch (RuntimeException paramMessage)
      {
        BasePendingResult.clear((com.google.android.gms.common.package_6.Result)localObject);
        throw paramMessage;
      }
    }
  }
  
  final class zaa
  {
    private zaa() {}
    
    protected final void finalize()
      throws Throwable
    {
      BasePendingResult.clear(BasePendingResult.readMessage(BasePendingResult.this));
      super.finalize();
    }
  }
}
