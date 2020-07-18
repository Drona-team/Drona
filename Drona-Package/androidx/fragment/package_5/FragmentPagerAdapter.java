package androidx.fragment.package_5;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Lifecycle.State;
import androidx.viewpager.widget.PagerAdapter;

public abstract class FragmentPagerAdapter
  extends PagerAdapter
{
  public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;
  @Deprecated
  public static final int BEHAVIOR_SET_USER_VISIBLE_HINT = 0;
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentPagerAdapter";
  private final int mBehavior;
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private boolean mExecutingFinishUpdate;
  private final FragmentManager mFragmentManager;
  
  public FragmentPagerAdapter(FragmentManager paramFragmentManager)
  {
    this(paramFragmentManager, 0);
  }
  
  public FragmentPagerAdapter(FragmentManager paramFragmentManager, int paramInt)
  {
    mFragmentManager = paramFragmentManager;
    mBehavior = paramInt;
  }
  
  private static String makeFragmentName(int paramInt, long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("android:switcher:");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(":");
    localStringBuilder.append(paramLong);
    return localStringBuilder.toString();
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup = (Fragment)paramObject;
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    mCurTransaction.detach(paramViewGroup);
    if (paramViewGroup.equals(mCurrentPrimaryItem)) {
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
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    long l = getItemId(paramInt);
    Object localObject = makeFragmentName(paramViewGroup.getId(), l);
    Fragment localFragment = mFragmentManager.findFragmentByTag((String)localObject);
    localObject = localFragment;
    if (localFragment != null)
    {
      mCurTransaction.attach(localFragment);
    }
    else
    {
      localFragment = getItem(paramInt);
      localObject = localFragment;
      mCurTransaction.add(paramViewGroup.getId(), localFragment, makeFragmentName(paramViewGroup.getId(), l));
    }
    if (localObject != mCurrentPrimaryItem)
    {
      ((Fragment)localObject).setMenuVisibility(false);
      if (mBehavior == 1)
      {
        mCurTransaction.setMaxLifecycle((Fragment)localObject, Lifecycle.State.STARTED);
        return localObject;
      }
      ((Fragment)localObject).setUserVisibleHint(false);
    }
    return localObject;
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return ((Fragment)paramObject).getView() == paramView;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {}
  
  public Parcelable saveState()
  {
    return null;
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
