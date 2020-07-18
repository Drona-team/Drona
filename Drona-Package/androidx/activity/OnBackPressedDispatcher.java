package androidx.activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import java.util.ArrayDeque;
import java.util.Iterator;

public final class OnBackPressedDispatcher
{
  @Nullable
  private final Runnable mFallbackOnBackPressed;
  final ArrayDeque<OnBackPressedCallback> mOnBackPressedCallbacks = new ArrayDeque();
  
  public OnBackPressedDispatcher()
  {
    this(null);
  }
  
  public OnBackPressedDispatcher(Runnable paramRunnable)
  {
    mFallbackOnBackPressed = paramRunnable;
  }
  
  public void addCallback(OnBackPressedCallback paramOnBackPressedCallback)
  {
    addCancellableCallback(paramOnBackPressedCallback);
  }
  
  public void addCallback(LifecycleOwner paramLifecycleOwner, OnBackPressedCallback paramOnBackPressedCallback)
  {
    paramLifecycleOwner = paramLifecycleOwner.getLifecycle();
    if (paramLifecycleOwner.getCurrentState() == Lifecycle.State.DESTROYED) {
      return;
    }
    paramOnBackPressedCallback.addCancellable(new LifecycleOnBackPressedCancellable(paramLifecycleOwner, paramOnBackPressedCallback));
  }
  
  Cancellable addCancellableCallback(OnBackPressedCallback paramOnBackPressedCallback)
  {
    mOnBackPressedCallbacks.add(paramOnBackPressedCallback);
    OnBackPressedCancellable localOnBackPressedCancellable = new OnBackPressedCancellable(paramOnBackPressedCallback);
    paramOnBackPressedCallback.addCancellable(localOnBackPressedCancellable);
    return localOnBackPressedCancellable;
  }
  
  public boolean hasEnabledCallbacks()
  {
    Iterator localIterator = mOnBackPressedCallbacks.descendingIterator();
    while (localIterator.hasNext()) {
      if (((OnBackPressedCallback)localIterator.next()).isEnabled()) {
        return true;
      }
    }
    return false;
  }
  
  public void onBackPressed()
  {
    Iterator localIterator = mOnBackPressedCallbacks.descendingIterator();
    while (localIterator.hasNext())
    {
      OnBackPressedCallback localOnBackPressedCallback = (OnBackPressedCallback)localIterator.next();
      if (localOnBackPressedCallback.isEnabled())
      {
        localOnBackPressedCallback.handleOnBackPressed();
        return;
      }
    }
    if (mFallbackOnBackPressed != null) {
      mFallbackOnBackPressed.run();
    }
  }
  
  private class LifecycleOnBackPressedCancellable
    implements LifecycleEventObserver, Cancellable
  {
    @Nullable
    private Cancellable mCurrentCancellable;
    private final Lifecycle mLifecycle;
    private final OnBackPressedCallback mOnBackPressedCallback;
    
    LifecycleOnBackPressedCancellable(Lifecycle paramLifecycle, OnBackPressedCallback paramOnBackPressedCallback)
    {
      mLifecycle = paramLifecycle;
      mOnBackPressedCallback = paramOnBackPressedCallback;
      paramLifecycle.addObserver(this);
    }
    
    public void cancel()
    {
      mLifecycle.removeObserver(this);
      mOnBackPressedCallback.removeCancellable(this);
      if (mCurrentCancellable != null)
      {
        mCurrentCancellable.cancel();
        mCurrentCancellable = null;
      }
    }
    
    public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent)
    {
      if (paramEvent == Lifecycle.Event.ON_START)
      {
        mCurrentCancellable = addCancellableCallback(mOnBackPressedCallback);
        return;
      }
      if (paramEvent == Lifecycle.Event.ON_STOP)
      {
        if (mCurrentCancellable != null) {
          mCurrentCancellable.cancel();
        }
      }
      else if (paramEvent == Lifecycle.Event.ON_DESTROY) {
        cancel();
      }
    }
  }
  
  private class OnBackPressedCancellable
    implements Cancellable
  {
    private final OnBackPressedCallback mOnBackPressedCallback;
    
    OnBackPressedCancellable(OnBackPressedCallback paramOnBackPressedCallback)
    {
      mOnBackPressedCallback = paramOnBackPressedCallback;
    }
    
    public void cancel()
    {
      mOnBackPressedCallbacks.remove(mOnBackPressedCallback);
      mOnBackPressedCallback.removeCancellable(this);
    }
  }
}
