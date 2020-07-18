package androidx.lifecycle;

import androidx.arch.core.util.Function;

public class Transformations
{
  private Transformations() {}
  
  public static LiveData addSource(LiveData paramLiveData, final Function paramFunction)
  {
    MediatorLiveData localMediatorLiveData = new MediatorLiveData();
    localMediatorLiveData.addSource(paramLiveData, new Observer()
    {
      public void onChanged(Object paramAnonymousObject)
      {
        val$result.setValue(paramFunction.apply(paramAnonymousObject));
      }
    });
    return localMediatorLiveData;
  }
  
  public static LiveData switchMap(LiveData paramLiveData, Function paramFunction)
  {
    final MediatorLiveData localMediatorLiveData = new MediatorLiveData();
    localMediatorLiveData.addSource(paramLiveData, new Observer()
    {
      LiveData<Y> mSource;
      
      public void onChanged(Object paramAnonymousObject)
      {
        paramAnonymousObject = (LiveData)val$switchMapFunction.apply(paramAnonymousObject);
        if (mSource == paramAnonymousObject) {
          return;
        }
        if (mSource != null) {
          localMediatorLiveData.removeSource(mSource);
        }
        mSource = paramAnonymousObject;
        if (mSource != null) {
          localMediatorLiveData.addSource(mSource, new Observer()
          {
            public void onChanged(Object paramAnonymous2Object)
            {
              val$result.setValue(paramAnonymous2Object);
            }
          });
        }
      }
    });
    return localMediatorLiveData;
  }
}
