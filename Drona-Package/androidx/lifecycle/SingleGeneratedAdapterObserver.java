package androidx.lifecycle;

class SingleGeneratedAdapterObserver
  implements LifecycleEventObserver
{
  private final GeneratedAdapter mGeneratedAdapter;
  
  SingleGeneratedAdapterObserver(GeneratedAdapter paramGeneratedAdapter)
  {
    mGeneratedAdapter = paramGeneratedAdapter;
  }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent)
  {
    mGeneratedAdapter.callMethods(paramLifecycleOwner, paramEvent, false, null);
    mGeneratedAdapter.callMethods(paramLifecycleOwner, paramEvent, true, null);
  }
}
