package com.facebook.common.references;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.concurrent.GuardedBy;

@VisibleForTesting
public class SharedReference<T>
{
  @GuardedBy("itself")
  private static final Map<Object, Integer> sLiveObjects = new IdentityHashMap();
  @GuardedBy("this")
  private int mRefCount;
  private final ResourceReleaser<T> mResourceReleaser;
  @GuardedBy("this")
  private T mValue;
  
  public SharedReference(Object paramObject, ResourceReleaser paramResourceReleaser)
  {
    mValue = Preconditions.checkNotNull(paramObject);
    mResourceReleaser = ((ResourceReleaser)Preconditions.checkNotNull(paramResourceReleaser));
    mRefCount = 1;
    addLiveReference(paramObject);
  }
  
  private static void addLiveReference(Object paramObject)
  {
    Map localMap = sLiveObjects;
    try
    {
      Integer localInteger = (Integer)sLiveObjects.get(paramObject);
      if (localInteger == null) {
        sLiveObjects.put(paramObject, Integer.valueOf(1));
      } else {
        sLiveObjects.put(paramObject, Integer.valueOf(localInteger.intValue() + 1));
      }
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private int decreaseRefCount()
  {
    for (;;)
    {
      try
      {
        ensureValid();
        if (mRefCount > 0)
        {
          bool = true;
          Preconditions.checkArgument(bool);
          mRefCount -= 1;
          int i = mRefCount;
          return i;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  private void ensureValid()
  {
    if (isValid(this)) {
      return;
    }
    throw new NullReferenceException();
  }
  
  public static boolean isValid(SharedReference paramSharedReference)
  {
    return (paramSharedReference != null) && (paramSharedReference.isValid());
  }
  
  private static void removeLiveReference(Object paramObject)
  {
    Map localMap = sLiveObjects;
    try
    {
      Integer localInteger = (Integer)sLiveObjects.get(paramObject);
      if (localInteger == null) {
        FLog.wtf("SharedReference", "No entry in sLiveObjects for value of type %s", new Object[] { paramObject.getClass() });
      } else if (localInteger.intValue() == 1) {
        sLiveObjects.remove(paramObject);
      } else {
        sLiveObjects.put(paramObject, Integer.valueOf(localInteger.intValue() - 1));
      }
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void addReference()
  {
    try
    {
      ensureValid();
      mRefCount += 1;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean addReferenceIfValid()
  {
    try
    {
      if (isValid())
      {
        addReference();
        return true;
      }
      return false;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void deleteReference()
  {
    if (decreaseRefCount() == 0) {
      try
      {
        Object localObject = mValue;
        mValue = null;
        mResourceReleaser.release(localObject);
        removeLiveReference(localObject);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  public int getRefCountTestOnly()
  {
    try
    {
      int i = mRefCount;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object getValue()
  {
    try
    {
      Object localObject = mValue;
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isValid()
  {
    try
    {
      int i = mRefCount;
      boolean bool;
      if (i > 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static class NullReferenceException
    extends RuntimeException
  {
    public NullReferenceException()
    {
      super();
    }
  }
}
