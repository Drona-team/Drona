package androidx.fragment.package_5;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class FragmentStore
{
  private static final String TAG = "FragmentManager";
  private final HashMap<String, androidx.fragment.app.FragmentStateManager> mActive = new HashMap();
  private final ArrayList<androidx.fragment.app.Fragment> mAdded = new ArrayList();
  
  FragmentStore() {}
  
  void addFragment(Fragment paramFragment)
  {
    if (!mAdded.contains(paramFragment))
    {
      localObject = mAdded;
      try
      {
        mAdded.add(paramFragment);
        mAdded = true;
        return;
      }
      catch (Throwable paramFragment)
      {
        throw paramFragment;
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment already added: ");
    ((StringBuilder)localObject).append(paramFragment);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  void burpActive()
  {
    mActive.values().removeAll(Collections.singleton(null));
  }
  
  boolean containsActiveFragment(String paramString)
  {
    return mActive.containsKey(paramString);
  }
  
  void dispatchStateChange(int paramInt)
  {
    Iterator localIterator = mAdded.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Fragment)localIterator.next();
      localObject = (FragmentStateManager)mActive.get(mWho);
      if (localObject != null) {
        ((FragmentStateManager)localObject).setFragmentManagerState(paramInt);
      }
    }
    localIterator = mActive.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (FragmentStateManager)localIterator.next();
      if (localObject != null) {
        ((FragmentStateManager)localObject).setFragmentManagerState(paramInt);
      }
    }
  }
  
  void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append(paramString);
    ((StringBuilder)localObject1).append("    ");
    localObject1 = ((StringBuilder)localObject1).toString();
    if (!mActive.isEmpty())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Active Fragments:");
      Iterator localIterator = mActive.values().iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = (FragmentStateManager)localIterator.next();
        paramPrintWriter.print(paramString);
        if (localObject2 != null)
        {
          localObject2 = ((FragmentStateManager)localObject2).getFragment();
          paramPrintWriter.println(localObject2);
          ((Fragment)localObject2).dump((String)localObject1, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
        else
        {
          paramPrintWriter.println("null");
        }
      }
    }
    int j = mAdded.size();
    if (j > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Added Fragments:");
      int i = 0;
      while (i < j)
      {
        paramFileDescriptor = (Fragment)mAdded.get(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(paramFileDescriptor.toString());
        i += 1;
      }
    }
  }
  
  Fragment findActiveFragment(String paramString)
  {
    paramString = (FragmentStateManager)mActive.get(paramString);
    if (paramString != null) {
      return paramString.getFragment();
    }
    return null;
  }
  
  Fragment findFragmentById(int paramInt)
  {
    int i = mAdded.size() - 1;
    while (i >= 0)
    {
      localObject1 = (Fragment)mAdded.get(i);
      if ((localObject1 != null) && (mFragmentId == paramInt)) {
        return localObject1;
      }
      i -= 1;
    }
    Object localObject1 = mActive.values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (FragmentStateManager)((Iterator)localObject1).next();
      if (localObject2 != null)
      {
        localObject2 = ((FragmentStateManager)localObject2).getFragment();
        if (mFragmentId == paramInt) {
          return localObject2;
        }
      }
    }
    return null;
  }
  
  Fragment findFragmentByTag(String paramString)
  {
    Object localObject1;
    if (paramString != null)
    {
      int i = mAdded.size() - 1;
      while (i >= 0)
      {
        localObject1 = (Fragment)mAdded.get(i);
        if ((localObject1 != null) && (paramString.equals(mTag))) {
          return localObject1;
        }
        i -= 1;
      }
    }
    if (paramString != null)
    {
      localObject1 = mActive.values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (FragmentStateManager)((Iterator)localObject1).next();
        if (localObject2 != null)
        {
          localObject2 = ((FragmentStateManager)localObject2).getFragment();
          if (paramString.equals(mTag)) {
            return localObject2;
          }
        }
      }
    }
    return null;
  }
  
  Fragment findFragmentByWho(String paramString)
  {
    Iterator localIterator = mActive.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (FragmentStateManager)localIterator.next();
      if (localObject != null)
      {
        localObject = ((FragmentStateManager)localObject).getFragment().findFragmentByWho(paramString);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    return null;
  }
  
  Fragment findFragmentUnder(Fragment paramFragment)
  {
    ViewGroup localViewGroup = mContainer;
    View localView = mView;
    if (localViewGroup != null)
    {
      if (localView == null) {
        return null;
      }
      int i = mAdded.indexOf(paramFragment) - 1;
      while (i >= 0)
      {
        paramFragment = (Fragment)mAdded.get(i);
        if ((mContainer == localViewGroup) && (mView != null)) {
          return paramFragment;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  int getActiveFragmentCount()
  {
    return mActive.size();
  }
  
  List getActiveFragments()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = mActive.values().iterator();
    while (localIterator.hasNext())
    {
      FragmentStateManager localFragmentStateManager = (FragmentStateManager)localIterator.next();
      if (localFragmentStateManager != null) {
        localArrayList.add(localFragmentStateManager.getFragment());
      } else {
        localArrayList.add(null);
      }
    }
    return localArrayList;
  }
  
  FragmentStateManager getFragmentStateManager(String paramString)
  {
    return (FragmentStateManager)mActive.get(paramString);
  }
  
  List getFragments()
  {
    if (mAdded.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList localArrayList1 = mAdded;
    try
    {
      ArrayList localArrayList2 = new ArrayList(mAdded);
      return localArrayList2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void makeActive(FragmentStateManager paramFragmentStateManager)
  {
    mActive.put(getFragmentmWho, paramFragmentStateManager);
  }
  
  void makeInactive(FragmentStateManager paramFragmentStateManager)
  {
    paramFragmentStateManager = paramFragmentStateManager.getFragment();
    Iterator localIterator = mActive.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (FragmentStateManager)localIterator.next();
      if (localObject != null)
      {
        localObject = ((FragmentStateManager)localObject).getFragment();
        if (mWho.equals(mTargetWho))
        {
          mTarget = paramFragmentStateManager;
          mTargetWho = null;
        }
      }
    }
    mActive.put(mWho, null);
    if (mTargetWho != null) {
      mTarget = findActiveFragment(mTargetWho);
    }
  }
  
  void removeFragment(Fragment paramFragment)
  {
    ArrayList localArrayList = mAdded;
    try
    {
      mAdded.remove(paramFragment);
      mAdded = false;
      return;
    }
    catch (Throwable paramFragment)
    {
      throw paramFragment;
    }
  }
  
  void resetActiveFragments()
  {
    mActive.clear();
  }
  
  void restoreAddedFragments(List paramList)
  {
    mAdded.clear();
    if (paramList != null)
    {
      Object localObject = paramList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        paramList = (String)((Iterator)localObject).next();
        Fragment localFragment = findActiveFragment(paramList);
        if (localFragment != null)
        {
          if (FragmentManager.isLoggingEnabled(2))
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("restoreSaveState: added (");
            localStringBuilder.append(paramList);
            localStringBuilder.append("): ");
            localStringBuilder.append(localFragment);
            Log.v("FragmentManager", localStringBuilder.toString());
          }
          addFragment(localFragment);
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("No instantiated fragment for (");
          ((StringBuilder)localObject).append(paramList);
          ((StringBuilder)localObject).append(")");
          throw new IllegalStateException(((StringBuilder)localObject).toString());
        }
      }
    }
  }
  
  ArrayList saveActiveFragments()
  {
    ArrayList localArrayList = new ArrayList(mActive.size());
    Iterator localIterator = mActive.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (FragmentStateManager)localIterator.next();
      if (localObject != null)
      {
        Fragment localFragment = ((FragmentStateManager)localObject).getFragment();
        localObject = ((FragmentStateManager)localObject).saveState();
        localArrayList.add(localObject);
        if (FragmentManager.isLoggingEnabled(2))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Saved state of ");
          localStringBuilder.append(localFragment);
          localStringBuilder.append(": ");
          localStringBuilder.append(mSavedFragmentState);
          Log.v("FragmentManager", localStringBuilder.toString());
        }
      }
    }
    return localArrayList;
  }
  
  ArrayList saveAddedFragments()
  {
    ArrayList localArrayList1 = mAdded;
    try
    {
      if (mAdded.isEmpty()) {
        return null;
      }
      ArrayList localArrayList2 = new ArrayList(mAdded.size());
      Iterator localIterator = mAdded.iterator();
      while (localIterator.hasNext())
      {
        Fragment localFragment = (Fragment)localIterator.next();
        localArrayList2.add(mWho);
        if (FragmentManager.isLoggingEnabled(2))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("saveAllState: adding fragment (");
          localStringBuilder.append(mWho);
          localStringBuilder.append("): ");
          localStringBuilder.append(localFragment);
          Log.v("FragmentManager", localStringBuilder.toString());
        }
      }
      return localArrayList2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
