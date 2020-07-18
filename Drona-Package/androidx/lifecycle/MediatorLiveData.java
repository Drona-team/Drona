package androidx.lifecycle;

import androidx.arch.core.internal.SafeIterableMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MediatorLiveData<T>
  extends MutableLiveData<T>
{
  private SafeIterableMap<LiveData<?>, Source<?>> mSources = new SafeIterableMap();
  
  public MediatorLiveData() {}
  
  public void addSource(LiveData paramLiveData, Observer paramObserver)
  {
    Source localSource = new Source(paramLiveData, paramObserver);
    paramLiveData = (Source)mSources.putIfAbsent(paramLiveData, localSource);
    if ((paramLiveData != null) && (mObserver != paramObserver)) {
      throw new IllegalArgumentException("This source was already added with the different observer");
    }
    if (paramLiveData != null) {
      return;
    }
    if (hasActiveObservers()) {
      localSource.plug();
    }
  }
  
  protected void onActive()
  {
    Iterator localIterator = mSources.iterator();
    while (localIterator.hasNext()) {
      ((Source)((Map.Entry)localIterator.next()).getValue()).plug();
    }
  }
  
  protected void onInactive()
  {
    Iterator localIterator = mSources.iterator();
    while (localIterator.hasNext()) {
      ((Source)((Map.Entry)localIterator.next()).getValue()).unplug();
    }
  }
  
  public void removeSource(LiveData paramLiveData)
  {
    paramLiveData = (Source)mSources.remove(paramLiveData);
    if (paramLiveData != null) {
      paramLiveData.unplug();
    }
  }
  
  private static class Source<V>
    implements Observer<V>
  {
    final LiveData<V> mLiveData;
    final Observer<? super V> mObserver;
    int mVersion = -1;
    
    Source(LiveData paramLiveData, Observer paramObserver)
    {
      mLiveData = paramLiveData;
      mObserver = paramObserver;
    }
    
    public void onChanged(Object paramObject)
    {
      if (mVersion != mLiveData.getVersion())
      {
        mVersion = mLiveData.getVersion();
        mObserver.onChanged(paramObject);
      }
    }
    
    void plug()
    {
      mLiveData.observeForever(this);
    }
    
    void unplug()
    {
      mLiveData.removeObserver(this);
    }
  }
}
