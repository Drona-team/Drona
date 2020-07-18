package androidx.fragment.package_5;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class FragmentLifecycleCallbacksDispatcher
{
  @NonNull
  private final FragmentManager mFragmentManager;
  @NonNull
  private final CopyOnWriteArrayList<androidx.fragment.app.FragmentLifecycleCallbacksDispatcher.FragmentLifecycleCallbacksHolder> mLifecycleCallbacks = new CopyOnWriteArrayList();
  
  FragmentLifecycleCallbacksDispatcher(FragmentManager paramFragmentManager)
  {
    mFragmentManager = paramFragmentManager;
  }
  
  void dispatchOnFragmentActivityCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentActivityCreated(paramFragment, paramBundle, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentActivityCreated(mFragmentManager, paramFragment, paramBundle);
      }
    }
  }
  
  void dispatchOnFragmentAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentAttached(paramFragment, paramContext, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentAttached(mFragmentManager, paramFragment, paramContext);
      }
    }
  }
  
  void dispatchOnFragmentCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentCreated(paramFragment, paramBundle, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentCreated(mFragmentManager, paramFragment, paramBundle);
      }
    }
  }
  
  void dispatchOnFragmentDestroyed(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentDestroyed(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentDestroyed(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentDetached(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentDetached(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentDetached(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentPaused(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPaused(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentPaused(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentPreAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPreAttached(paramFragment, paramContext, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentPreAttached(mFragmentManager, paramFragment, paramContext);
      }
    }
  }
  
  void dispatchOnFragmentPreCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPreCreated(paramFragment, paramBundle, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentPreCreated(mFragmentManager, paramFragment, paramBundle);
      }
    }
  }
  
  void dispatchOnFragmentResumed(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentResumed(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentResumed(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentSaveInstanceState(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentSaveInstanceState(paramFragment, paramBundle, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentSaveInstanceState(mFragmentManager, paramFragment, paramBundle);
      }
    }
  }
  
  void dispatchOnFragmentStarted(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentStarted(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentStarted(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentStopped(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentStopped(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentStopped(mFragmentManager, paramFragment);
      }
    }
  }
  
  void dispatchOnFragmentViewCreated(Fragment paramFragment, View paramView, Bundle paramBundle, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentViewCreated(paramFragment, paramView, paramBundle, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentViewCreated(mFragmentManager, paramFragment, paramView, paramBundle);
      }
    }
  }
  
  void dispatchOnFragmentViewDestroyed(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject = mFragmentManager.getParent();
    if (localObject != null) {
      ((Fragment)localObject).getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentViewDestroyed(paramFragment, true);
    }
    localObject = mLifecycleCallbacks.iterator();
    while (((Iterator)localObject).hasNext())
    {
      FragmentLifecycleCallbacksHolder localFragmentLifecycleCallbacksHolder = (FragmentLifecycleCallbacksHolder)((Iterator)localObject).next();
      if ((!paramBoolean) || (mRecursive)) {
        mCallback.onFragmentViewDestroyed(mFragmentManager, paramFragment);
      }
    }
  }
  
  public void registerFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks, boolean paramBoolean)
  {
    mLifecycleCallbacks.add(new FragmentLifecycleCallbacksHolder(paramFragmentLifecycleCallbacks, paramBoolean));
  }
  
  public void unregisterFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks)
  {
    CopyOnWriteArrayList localCopyOnWriteArrayList = mLifecycleCallbacks;
    int i = 0;
    for (;;)
    {
      try
      {
        int j = mLifecycleCallbacks.size();
        if (i < j)
        {
          if (mLifecycleCallbacks.get(i)).mCallback != paramFragmentLifecycleCallbacks) {
            break label64;
          }
          mLifecycleCallbacks.remove(i);
        }
        return;
      }
      catch (Throwable paramFragmentLifecycleCallbacks)
      {
        throw paramFragmentLifecycleCallbacks;
      }
      label64:
      i += 1;
    }
  }
  
  final class FragmentLifecycleCallbacksHolder
  {
    final boolean mRecursive;
    
    FragmentLifecycleCallbacksHolder(boolean paramBoolean)
    {
      mRecursive = paramBoolean;
    }
  }
}
