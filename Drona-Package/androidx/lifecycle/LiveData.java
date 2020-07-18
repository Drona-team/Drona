package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.arch.core.internal.SafeIterableMap.IteratorWithAdditions;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class LiveData<T>
{
  static final Object NOT_SET = new Object();
  static final int START_VERSION = -1;
  int mActiveCount = 0;
  private volatile Object mData;
  final Object mDataLock = new Object();
  private boolean mDispatchInvalidated;
  private boolean mDispatchingValue;
  private SafeIterableMap<Observer<? super T>, LiveData<T>.ObserverWrapper> mObservers = new SafeIterableMap();
  volatile Object mPendingData = NOT_SET;
  private final Runnable mPostValueRunnable = new Runnable()
  {
    public void run()
    {
      Object localObject1 = mDataLock;
      try
      {
        Object localObject2 = mPendingData;
        mPendingData = LiveData.NOT_SET;
        setValue(localObject2);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  };
  private int mVersion;
  
  public LiveData()
  {
    mData = NOT_SET;
    mVersion = -1;
  }
  
  public LiveData(Object paramObject)
  {
    mData = paramObject;
    mVersion = 0;
  }
  
  static void assertMainThread(String paramString)
  {
    if (ArchTaskExecutor.getInstance().isMainThread()) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Cannot invoke ");
    localStringBuilder.append(paramString);
    localStringBuilder.append(" on a background thread");
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  private void considerNotify(ObserverWrapper paramObserverWrapper)
  {
    if (!mActive) {
      return;
    }
    if (!paramObserverWrapper.shouldBeActive())
    {
      paramObserverWrapper.activeStateChanged(false);
      return;
    }
    if (mLastVersion >= mVersion) {
      return;
    }
    mLastVersion = mVersion;
    mObserver.onChanged(mData);
  }
  
  void dispatchingValue(ObserverWrapper paramObserverWrapper)
  {
    if (mDispatchingValue)
    {
      mDispatchInvalidated = true;
      return;
    }
    mDispatchingValue = true;
    do
    {
      mDispatchInvalidated = false;
      ObserverWrapper localObserverWrapper;
      if (paramObserverWrapper != null)
      {
        considerNotify(paramObserverWrapper);
        localObserverWrapper = null;
      }
      else
      {
        SafeIterableMap.IteratorWithAdditions localIteratorWithAdditions = mObservers.iteratorWithAdditions();
        do
        {
          localObserverWrapper = paramObserverWrapper;
          if (!localIteratorWithAdditions.hasNext()) {
            break;
          }
          considerNotify((ObserverWrapper)((Map.Entry)localIteratorWithAdditions.next()).getValue());
        } while (!mDispatchInvalidated);
        localObserverWrapper = paramObserverWrapper;
      }
      paramObserverWrapper = localObserverWrapper;
    } while (mDispatchInvalidated);
    mDispatchingValue = false;
  }
  
  public Object getValue()
  {
    Object localObject = mData;
    if (localObject != NOT_SET) {
      return localObject;
    }
    return null;
  }
  
  int getVersion()
  {
    return mVersion;
  }
  
  public boolean hasActiveObservers()
  {
    return mActiveCount > 0;
  }
  
  public boolean hasObservers()
  {
    return mObservers.size() > 0;
  }
  
  public void observe(LifecycleOwner paramLifecycleOwner, Observer paramObserver)
  {
    assertMainThread("observe");
    if (paramLifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
      return;
    }
    LifecycleBoundObserver localLifecycleBoundObserver = new LifecycleBoundObserver(paramLifecycleOwner, paramObserver);
    paramObserver = (ObserverWrapper)mObservers.putIfAbsent(paramObserver, localLifecycleBoundObserver);
    if ((paramObserver != null) && (!paramObserver.isAttachedTo(paramLifecycleOwner))) {
      throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
    }
    if (paramObserver != null) {
      return;
    }
    paramLifecycleOwner.getLifecycle().addObserver(localLifecycleBoundObserver);
  }
  
  public void observeForever(Observer paramObserver)
  {
    assertMainThread("observeForever");
    AlwaysActiveObserver localAlwaysActiveObserver = new AlwaysActiveObserver(paramObserver);
    paramObserver = (ObserverWrapper)mObservers.putIfAbsent(paramObserver, localAlwaysActiveObserver);
    if (!(paramObserver instanceof LifecycleBoundObserver))
    {
      if (paramObserver != null) {
        return;
      }
      localAlwaysActiveObserver.activeStateChanged(true);
      return;
    }
    throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
  }
  
  protected void onActive() {}
  
  protected void onInactive() {}
  
  protected void postValue(Object paramObject)
  {
    Object localObject = mDataLock;
    for (;;)
    {
      try
      {
        if (mPendingData == NOT_SET)
        {
          i = 1;
          mPendingData = paramObject;
          if (i == 0) {
            return;
          }
          ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
          return;
        }
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
      int i = 0;
    }
  }
  
  public void removeObserver(Observer paramObserver)
  {
    assertMainThread("removeObserver");
    paramObserver = (ObserverWrapper)mObservers.remove(paramObserver);
    if (paramObserver == null) {
      return;
    }
    paramObserver.detachObserver();
    paramObserver.activeStateChanged(false);
  }
  
  public void removeObservers(LifecycleOwner paramLifecycleOwner)
  {
    assertMainThread("removeObservers");
    Iterator localIterator = mObservers.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (((ObserverWrapper)localEntry.getValue()).isAttachedTo(paramLifecycleOwner)) {
        removeObserver((Observer)localEntry.getKey());
      }
    }
  }
  
  protected void setValue(Object paramObject)
  {
    assertMainThread("setValue");
    mVersion += 1;
    mData = paramObject;
    dispatchingValue(null);
  }
  
  private class AlwaysActiveObserver
    extends LiveData<T>.ObserverWrapper
  {
    AlwaysActiveObserver(Observer paramObserver)
    {
      super(paramObserver);
    }
    
    boolean shouldBeActive()
    {
      return true;
    }
  }
  
  class LifecycleBoundObserver
    extends LiveData<T>.ObserverWrapper
    implements LifecycleEventObserver
  {
    @NonNull
    final LifecycleOwner mOwner;
    
    LifecycleBoundObserver(LifecycleOwner paramLifecycleOwner, Observer paramObserver)
    {
      super(paramObserver);
      mOwner = paramLifecycleOwner;
    }
    
    void detachObserver()
    {
      mOwner.getLifecycle().removeObserver(this);
    }
    
    boolean isAttachedTo(LifecycleOwner paramLifecycleOwner)
    {
      return mOwner == paramLifecycleOwner;
    }
    
    public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent)
    {
      if (mOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED)
      {
        removeObserver(mObserver);
        return;
      }
      activeStateChanged(shouldBeActive());
    }
    
    boolean shouldBeActive()
    {
      return mOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
  }
  
  private abstract class ObserverWrapper
  {
    boolean mActive;
    int mLastVersion = -1;
    final Observer<? super T> mObserver;
    
    ObserverWrapper(Observer paramObserver)
    {
      mObserver = paramObserver;
    }
    
    void activeStateChanged(boolean paramBoolean)
    {
      if (paramBoolean == mActive) {
        return;
      }
      mActive = paramBoolean;
      int i = mActiveCount;
      int j = 1;
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      }
      LiveData localLiveData = LiveData.this;
      int k = mActiveCount;
      if (!mActive) {
        j = -1;
      }
      mActiveCount = (k + j);
      if ((i != 0) && (mActive)) {
        onActive();
      }
      if ((mActiveCount == 0) && (!mActive)) {
        onInactive();
      }
      if (mActive) {
        dispatchingValue(this);
      }
    }
    
    void detachObserver() {}
    
    boolean isAttachedTo(LifecycleOwner paramLifecycleOwner)
    {
      return false;
    }
    
    abstract boolean shouldBeActive();
  }
}
