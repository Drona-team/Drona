package com.facebook.common.references;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class CloseableReference<T>
  implements Cloneable, Closeable
{
  private static final ResourceReleaser<Closeable> DEFAULT_CLOSEABLE_RELEASER = new ResourceReleaser()
  {
    public void release(Closeable paramAnonymousCloseable)
    {
      try
      {
        Closeables.close(paramAnonymousCloseable, true);
        return;
      }
      catch (IOException paramAnonymousCloseable) {}
    }
  };
  private static final LeakHandler DEFAULT_LEAK_HANDLER = new LeakHandler()
  {
    public void reportLeak(SharedReference paramAnonymousSharedReference, Throwable paramAnonymousThrowable)
    {
      FLog.w(CloseableReference.keyType, "Finalized without closing: %x %x (type = %s)", new Object[] { Integer.valueOf(System.identityHashCode(this)), Integer.valueOf(System.identityHashCode(paramAnonymousSharedReference)), paramAnonymousSharedReference.getValue().getClass().getName() });
    }
    
    public boolean requiresStacktrace()
    {
      return false;
    }
  };
  private static Class<CloseableReference> keyType = CloseableReference.class;
  @GuardedBy("this")
  private boolean mIsClosed = false;
  private final LeakHandler mLeakHandler;
  private final SharedReference<T> mSharedReference;
  @Nullable
  private final Throwable mStacktrace;
  
  private CloseableReference(SharedReference paramSharedReference, LeakHandler paramLeakHandler, Throwable paramThrowable)
  {
    mSharedReference = ((SharedReference)Preconditions.checkNotNull(paramSharedReference));
    paramSharedReference.addReference();
    mLeakHandler = paramLeakHandler;
    mStacktrace = paramThrowable;
  }
  
  private CloseableReference(Object paramObject, ResourceReleaser paramResourceReleaser, LeakHandler paramLeakHandler, Throwable paramThrowable)
  {
    mSharedReference = new SharedReference(paramObject, paramResourceReleaser);
    mLeakHandler = paramLeakHandler;
    mStacktrace = paramThrowable;
  }
  
  public static CloseableReference cloneOrNull(CloseableReference paramCloseableReference)
  {
    if (paramCloseableReference != null) {
      return paramCloseableReference.cloneOrNull();
    }
    return null;
  }
  
  public static List cloneOrNull(Collection paramCollection)
  {
    if (paramCollection == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      localArrayList.add(cloneOrNull((CloseableReference)paramCollection.next()));
    }
    return localArrayList;
  }
  
  public static void closeSafely(CloseableReference paramCloseableReference)
  {
    if (paramCloseableReference != null) {
      paramCloseableReference.close();
    }
  }
  
  public static void closeSafely(Iterable paramIterable)
  {
    if (paramIterable != null)
    {
      paramIterable = paramIterable.iterator();
      while (paramIterable.hasNext()) {
        closeSafely((CloseableReference)paramIterable.next());
      }
    }
  }
  
  public static boolean isValid(CloseableReference paramCloseableReference)
  {
    return (paramCloseableReference != null) && (paramCloseableReference.isValid());
  }
  
  public static CloseableReference loadClass(Closeable paramCloseable, LeakHandler paramLeakHandler)
  {
    Throwable localThrowable = null;
    if (paramCloseable == null) {
      return null;
    }
    ResourceReleaser localResourceReleaser = DEFAULT_CLOSEABLE_RELEASER;
    if (paramLeakHandler.requiresStacktrace()) {
      localThrowable = new Throwable();
    }
    return new CloseableReference(paramCloseable, localResourceReleaser, paramLeakHandler, localThrowable);
  }
  
  public static CloseableReference loadClass(Object paramObject, ResourceReleaser paramResourceReleaser, LeakHandler paramLeakHandler)
  {
    Throwable localThrowable = null;
    if (paramObject == null) {
      return null;
    }
    if (paramLeakHandler.requiresStacktrace()) {
      localThrowable = new Throwable();
    }
    return new CloseableReference(paramObject, paramResourceReleaser, paramLeakHandler, localThrowable);
  }
  
  public static CloseableReference of(Closeable paramCloseable)
  {
    return of(paramCloseable, DEFAULT_CLOSEABLE_RELEASER);
  }
  
  public static CloseableReference of(Object paramObject, ResourceReleaser paramResourceReleaser)
  {
    return loadClass(paramObject, paramResourceReleaser, DEFAULT_LEAK_HANDLER);
  }
  
  public CloseableReference clone()
  {
    try
    {
      Preconditions.checkState(isValid());
      CloseableReference localCloseableReference = new CloseableReference(mSharedReference, mLeakHandler, mStacktrace);
      return localCloseableReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public CloseableReference cloneOrNull()
  {
    try
    {
      if (isValid())
      {
        CloseableReference localCloseableReference = clone();
        return localCloseableReference;
      }
      return null;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void close()
  {
    try
    {
      if (mIsClosed) {
        return;
      }
      mIsClosed = true;
      mSharedReference.deleteReference();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  protected void finalize()
    throws Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 53	com/facebook/common/references/CloseableReference:mIsClosed	Z
    //   6: ifeq +10 -> 16
    //   9: aload_0
    //   10: monitorexit
    //   11: aload_0
    //   12: invokespecial 166	java/lang/Object:finalize	()V
    //   15: return
    //   16: aload_0
    //   17: monitorexit
    //   18: aload_0
    //   19: getfield 68	com/facebook/common/references/CloseableReference:mLeakHandler	Lcom/facebook/common/references/CloseableReference$LeakHandler;
    //   22: aload_0
    //   23: getfield 63	com/facebook/common/references/CloseableReference:mSharedReference	Lcom/facebook/common/references/SharedReference;
    //   26: aload_0
    //   27: getfield 70	com/facebook/common/references/CloseableReference:mStacktrace	Ljava/lang/Throwable;
    //   30: invokeinterface 170 3 0
    //   35: aload_0
    //   36: invokevirtual 120	com/facebook/common/references/CloseableReference:close	()V
    //   39: aload_0
    //   40: invokespecial 166	java/lang/Object:finalize	()V
    //   43: return
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    //   49: astore_1
    //   50: aload_0
    //   51: invokespecial 166	java/lang/Object:finalize	()V
    //   54: aload_1
    //   55: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	CloseableReference
    //   44	4	1	localThrowable1	Throwable
    //   49	6	1	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   2	11	44	java/lang/Throwable
    //   16	18	44	java/lang/Throwable
    //   45	47	44	java/lang/Throwable
    //   0	2	49	java/lang/Throwable
    //   18	39	49	java/lang/Throwable
    //   47	49	49	java/lang/Throwable
  }
  
  public Object get()
  {
    try
    {
      Preconditions.checkState(mIsClosed ^ true);
      Object localObject = mSharedReference.getValue();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public SharedReference getUnderlyingReferenceTestOnly()
  {
    try
    {
      SharedReference localSharedReference = mSharedReference;
      return localSharedReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getValueHash()
  {
    if (isValid()) {
      return System.identityHashCode(mSharedReference.getValue());
    }
    return 0;
  }
  
  public boolean isValid()
  {
    try
    {
      boolean bool = mIsClosed;
      return bool ^ true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static abstract interface LeakHandler
  {
    public abstract void reportLeak(SharedReference paramSharedReference, Throwable paramThrowable);
    
    public abstract boolean requiresStacktrace();
  }
}
