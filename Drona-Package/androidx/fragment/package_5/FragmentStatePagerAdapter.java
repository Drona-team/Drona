package androidx.fragment.package_5;

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Lifecycle.State;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FragmentStatePagerAdapter
  extends PagerAdapter
{
  public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;
  @Deprecated
  public static final int BEHAVIOR_SET_USER_VISIBLE_HINT = 0;
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentStatePagerAdapt";
  private final int mBehavior;
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private boolean mExecutingFinishUpdate;
  private final FragmentManager mFragmentManager;
  private ArrayList<androidx.fragment.app.Fragment> mFragments = new ArrayList();
  private ArrayList<androidx.fragment.app.Fragment.SavedState> mSavedState = new ArrayList();
  
  public FragmentStatePagerAdapter(FragmentManager paramFragmentManager)
  {
    this(paramFragmentManager, 0);
  }
  
  public FragmentStatePagerAdapter(FragmentManager paramFragmentManager, int paramInt)
  {
    mFragmentManager = paramFragmentManager;
    mBehavior = paramInt;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramObject = (Fragment)paramObject;
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    while (mSavedState.size() <= paramInt) {
      mSavedState.add(null);
    }
    ArrayList localArrayList = mSavedState;
    if (paramObject.isAdded()) {
      paramViewGroup = mFragmentManager.saveFragmentInstanceState(paramObject);
    } else {
      paramViewGroup = null;
    }
    localArrayList.set(paramInt, paramViewGroup);
    mFragments.set(paramInt, null);
    mCurTransaction.remove(paramObject);
    if (paramObject.equals(mCurrentPrimaryItem)) {
      mCurrentPrimaryItem = null;
    }
  }
  
  public void finishUpdate(ViewGroup paramViewGroup)
  {
    if (mCurTransaction != null)
    {
      if (!mExecutingFinishUpdate) {
        try
        {
          mExecutingFinishUpdate = true;
          mCurTransaction.commitNowAllowingStateLoss();
          mExecutingFinishUpdate = false;
        }
        catch (Throwable paramViewGroup)
        {
          mExecutingFinishUpdate = false;
          throw paramViewGroup;
        }
      }
      mCurTransaction = null;
    }
  }
  
  public abstract Fragment getItem(int paramInt);
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    if (mFragments.size() > paramInt)
    {
      localFragment = (Fragment)mFragments.get(paramInt);
      if (localFragment != null) {
        return localFragment;
      }
    }
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    Fragment localFragment = getItem(paramInt);
    if (mSavedState.size() > paramInt)
    {
      Fragment.SavedState localSavedState = (Fragment.SavedState)mSavedState.get(paramInt);
      if (localSavedState != null) {
        localFragment.setInitialSavedState(localSavedState);
      }
    }
    while (mFragments.size() <= paramInt) {
      mFragments.add(null);
    }
    localFragment.setMenuVisibility(false);
    if (mBehavior == 0) {
      localFragment.setUserVisibleHint(false);
    }
    mFragments.set(paramInt, localFragment);
    mCurTransaction.add(paramViewGroup.getId(), localFragment);
    if (mBehavior == 1) {
      mCurTransaction.setMaxLifecycle(localFragment, Lifecycle.State.STARTED);
    }
    return localFragment;
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return ((Fragment)paramObject).getView() == paramView;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader)
  {
    if (paramParcelable != null)
    {
      paramParcelable = (Bundle)paramParcelable;
      paramParcelable.setClassLoader(paramClassLoader);
      paramClassLoader = paramParcelable.getParcelableArray("states");
      mSavedState.clear();
      mFragments.clear();
      int i;
      if (paramClassLoader != null)
      {
        i = 0;
        while (i < paramClassLoader.length)
        {
          mSavedState.add((Fragment.SavedState)paramClassLoader[i]);
          i += 1;
        }
      }
      paramClassLoader = paramParcelable.keySet().iterator();
      while (paramClassLoader.hasNext())
      {
        String str = (String)paramClassLoader.next();
        if (str.startsWith("f"))
        {
          i = Integer.parseInt(str.substring(1));
          Object localObject = mFragmentManager.getFragment(paramParcelable, str);
          if (localObject != null)
          {
            while (mFragments.size() <= i) {
              mFragments.add(null);
            }
            ((Fragment)localObject).setMenuVisibility(false);
            mFragments.set(i, localObject);
          }
          else
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Bad fragment at key ");
            ((StringBuilder)localObject).append(str);
            Log.w("FragmentStatePagerAdapt", ((StringBuilder)localObject).toString());
          }
        }
      }
    }
  }
  
  public Parcelable saveState()
  {
    Object localObject2;
    Object localObject1;
    if (mSavedState.size() > 0)
    {
      localObject2 = new Bundle();
      localObject1 = new Fragment.SavedState[mSavedState.size()];
      mSavedState.toArray((Object[])localObject1);
      ((Bundle)localObject2).putParcelableArray("states", (Parcelable[])localObject1);
    }
    else
    {
      localObject2 = null;
    }
    int i = 0;
    while (i < mFragments.size())
    {
      Fragment localFragment = (Fragment)mFragments.get(i);
      localObject1 = localObject2;
      if (localFragment != null)
      {
        localObject1 = localObject2;
        if (localFragment.isAdded())
        {
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new Bundle();
          }
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("f");
          ((StringBuilder)localObject2).append(i);
          localObject2 = ((StringBuilder)localObject2).toString();
          mFragmentManager.putFragment((Bundle)localObject1, (String)localObject2, localFragment);
        }
      }
      i += 1;
      localObject2 = localObject1;
    }
    return localObject2;
  }
  
  public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup = (Fragment)paramObject;
    if (paramViewGroup != mCurrentPrimaryItem)
    {
      if (mCurrentPrimaryItem != null)
      {
        mCurrentPrimaryItem.setMenuVisibility(false);
        if (mBehavior == 1)
        {
          if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
          }
          mCurTransaction.setMaxLifecycle(mCurrentPrimaryItem, Lifecycle.State.STARTED);
        }
        else
        {
          mCurrentPrimaryItem.setUserVisibleHint(false);
        }
      }
      paramViewGroup.setMenuVisibility(true);
      if (mBehavior == 1)
      {
        if (mCurTransaction == null) {
          mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.setMaxLifecycle(paramViewGroup, Lifecycle.State.RESUMED);
      }
      else
      {
        paramViewGroup.setUserVisibleHint(true);
      }
      mCurrentPrimaryItem = paramViewGroup;
    }
  }
  
  public void startUpdate(ViewGroup paramViewGroup)
  {
    if (paramViewGroup.getId() != -1) {
      return;
    }
    paramViewGroup = new StringBuilder();
    paramViewGroup.append("ViewPager with adapter ");
    paramViewGroup.append(this);
    paramViewGroup.append(" requires a view id");
    throw new IllegalStateException(paramViewGroup.toString());
  }
}
