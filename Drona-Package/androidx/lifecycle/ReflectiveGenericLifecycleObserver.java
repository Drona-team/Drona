package androidx.lifecycle;

class ReflectiveGenericLifecycleObserver
  implements LifecycleEventObserver
{
  private final ClassesInfoCache.CallbackInfo mInfo;
  private final Object mWrapped;
  
  ReflectiveGenericLifecycleObserver(Object paramObject)
  {
    mWrapped = paramObject;
    mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
  }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent)
  {
    mInfo.invokeCallbacks(paramLifecycleOwner, paramEvent, mWrapped);
  }
}
