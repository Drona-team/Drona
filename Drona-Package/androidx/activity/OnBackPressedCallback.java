package androidx.activity;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class OnBackPressedCallback
{
  private CopyOnWriteArrayList<Cancellable> mCancellables = new CopyOnWriteArrayList();
  private boolean mEnabled;
  
  public OnBackPressedCallback(boolean paramBoolean)
  {
    mEnabled = paramBoolean;
  }
  
  void addCancellable(Cancellable paramCancellable)
  {
    mCancellables.add(paramCancellable);
  }
  
  public abstract void handleOnBackPressed();
  
  public final boolean isEnabled()
  {
    return mEnabled;
  }
  
  public final void remove()
  {
    Iterator localIterator = mCancellables.iterator();
    while (localIterator.hasNext()) {
      ((Cancellable)localIterator.next()).cancel();
    }
  }
  
  void removeCancellable(Cancellable paramCancellable)
  {
    mCancellables.remove(paramCancellable);
  }
  
  public final void setEnabled(boolean paramBoolean)
  {
    mEnabled = paramBoolean;
  }
}
