package androidx.lifecycle;

public abstract interface LifecycleEventObserver
  extends LifecycleObserver
{
  public abstract void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent);
}
