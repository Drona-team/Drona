package androidx.fragment.package_5;

import android.content.Context;
import android.util.Log;
import androidx.core.util.LogWriter;
import androidx.lifecycle.Lifecycle.State;
import java.io.PrintWriter;
import java.util.ArrayList;

final class BackStackRecord
  extends FragmentTransaction
  implements FragmentManager.BackStackEntry, FragmentManager.OpGenerator
{
  private static final String TAG = "FragmentManager";
  boolean mCommitted;
  int mIndex = -1;
  final FragmentManager mManager;
  
  BackStackRecord(FragmentManager paramFragmentManager)
  {
    super(localFragmentFactory, localClassLoader);
    mManager = paramFragmentManager;
  }
  
  private static boolean isFragmentPostponed(FragmentTransaction.Op paramOp)
  {
    paramOp = mFragment;
    return (paramOp != null) && (mAdded) && (mView != null) && (!mDetached) && (!mHidden) && (paramOp.isPostponed());
  }
  
  void bumpBackStackNesting(int paramInt)
  {
    if (!mAddToBackStack) {
      return;
    }
    Object localObject1;
    if (FragmentManager.isLoggingEnabled(2))
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Bump nesting in ");
      ((StringBuilder)localObject1).append(this);
      ((StringBuilder)localObject1).append(" by ");
      ((StringBuilder)localObject1).append(paramInt);
      Log.v("FragmentManager", ((StringBuilder)localObject1).toString());
    }
    int j = mOps.size();
    int i = 0;
    while (i < j)
    {
      localObject1 = (FragmentTransaction.Op)mOps.get(i);
      if (mFragment != null)
      {
        Object localObject2 = mFragment;
        mBackStackNesting += paramInt;
        if (FragmentManager.isLoggingEnabled(2))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Bump nesting of ");
          ((StringBuilder)localObject2).append(mFragment);
          ((StringBuilder)localObject2).append(" to ");
          ((StringBuilder)localObject2).append(mFragment.mBackStackNesting);
          Log.v("FragmentManager", ((StringBuilder)localObject2).toString());
        }
      }
      i += 1;
    }
  }
  
  public int commit()
  {
    return commitInternal(false);
  }
  
  public int commitAllowingStateLoss()
  {
    return commitInternal(true);
  }
  
  int commitInternal(boolean paramBoolean)
  {
    if (!mCommitted)
    {
      if (FragmentManager.isLoggingEnabled(2))
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Commit: ");
        ((StringBuilder)localObject).append(this);
        Log.v("FragmentManager", ((StringBuilder)localObject).toString());
        localObject = new PrintWriter(new LogWriter("FragmentManager"));
        dump("  ", (PrintWriter)localObject);
        ((PrintWriter)localObject).close();
      }
      mCommitted = true;
      if (mAddToBackStack) {
        mIndex = mManager.allocBackStackIndex();
      } else {
        mIndex = -1;
      }
      mManager.enqueueAction(this, paramBoolean);
      return mIndex;
    }
    throw new IllegalStateException("commit already called");
  }
  
  public void commitNow()
  {
    disallowAddToBackStack();
    mManager.execSingleAction(this, false);
  }
  
  public void commitNowAllowingStateLoss()
  {
    disallowAddToBackStack();
    mManager.execSingleAction(this, true);
  }
  
  public FragmentTransaction detach(Fragment paramFragment)
  {
    if ((mFragmentManager != null) && (mFragmentManager != mManager))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot detach Fragment attached to a different FragmentManager. Fragment ");
      localStringBuilder.append(paramFragment.toString());
      localStringBuilder.append(" is already attached to a FragmentManager.");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    return super.detach(paramFragment);
  }
  
  void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    super.doAddOp(paramInt1, paramFragment, paramString, paramInt2);
    mFragmentManager = mManager;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    dump(paramString, paramPrintWriter, true);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(mCommitted);
      if (mTransition != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(mTransition));
      }
      if ((mEnterAnim != 0) || (mExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(mExitAnim));
      }
      if ((mPopEnterAnim != 0) || (mPopExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(mPopExitAnim));
      }
      if ((mBreadCrumbTitleRes != 0) || (mBreadCrumbTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(mBreadCrumbTitleText);
      }
      if ((mBreadCrumbShortTitleRes != 0) || (mBreadCrumbShortTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(mBreadCrumbShortTitleText);
      }
    }
    if (!mOps.isEmpty())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      int j = mOps.size();
      int i = 0;
      while (i < j)
      {
        FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(i);
        Object localObject;
        switch (mCmd)
        {
        default: 
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("cmd=");
          ((StringBuilder)localObject).append(mCmd);
          localObject = ((StringBuilder)localObject).toString();
          break;
        case 10: 
          localObject = "OP_SET_MAX_LIFECYCLE";
          break;
        case 9: 
          localObject = "UNSET_PRIMARY_NAV";
          break;
        case 8: 
          localObject = "SET_PRIMARY_NAV";
          break;
        case 7: 
          localObject = "ATTACH";
          break;
        case 6: 
          localObject = "DETACH";
          break;
        case 5: 
          localObject = "SHOW";
          break;
        case 4: 
          localObject = "HIDE";
          break;
        case 3: 
          localObject = "REMOVE";
          break;
        case 2: 
          localObject = "REPLACE";
          break;
        case 1: 
          localObject = "ADD";
          break;
        case 0: 
          localObject = "NULL";
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Op #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(": ");
        paramPrintWriter.print((String)localObject);
        paramPrintWriter.print(" ");
        paramPrintWriter.println(mFragment);
        if (paramBoolean)
        {
          if ((mEnterAnim != 0) || (mExitAnim != 0))
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("enterAnim=#");
            paramPrintWriter.print(Integer.toHexString(mEnterAnim));
            paramPrintWriter.print(" exitAnim=#");
            paramPrintWriter.println(Integer.toHexString(mExitAnim));
          }
          if ((mPopEnterAnim != 0) || (mPopExitAnim != 0))
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("popEnterAnim=#");
            paramPrintWriter.print(Integer.toHexString(mPopEnterAnim));
            paramPrintWriter.print(" popExitAnim=#");
            paramPrintWriter.println(Integer.toHexString(mPopExitAnim));
          }
        }
        i += 1;
      }
    }
  }
  
  void executeOps()
  {
    Object localObject2 = mOps;
    Object localObject1 = this;
    int j = ((ArrayList)localObject2).size();
    int i = 0;
    while (i < j)
    {
      localObject2 = mOps;
      localObject2 = (FragmentTransaction.Op)((ArrayList)localObject2).get(i);
      Fragment localFragment = mFragment;
      if (localFragment != null) {
        localFragment.setNextTransition(mTransition);
      }
      int k = mCmd;
      if (k != 1)
      {
        switch (k)
        {
        default: 
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Unknown cmd: ");
          ((StringBuilder)localObject1).append(mCmd);
          throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
        case 10: 
          mManager.setMaxLifecycle(localFragment, mCurrentMaxState);
          break;
        case 9: 
          mManager.setPrimaryNavigationFragment(null);
          break;
        case 8: 
          mManager.setPrimaryNavigationFragment(localFragment);
          break;
        case 7: 
          localFragment.setNextAnim(mEnterAnim);
          mManager.setExitAnimationOrder(localFragment, false);
          mManager.attachFragment(localFragment);
          break;
        case 6: 
          localFragment.setNextAnim(mExitAnim);
          mManager.detachFragment(localFragment);
          break;
        case 5: 
          localFragment.setNextAnim(mEnterAnim);
          mManager.setExitAnimationOrder(localFragment, false);
          mManager.showFragment(localFragment);
          break;
        case 4: 
          localFragment.setNextAnim(mExitAnim);
          mManager.hideFragment(localFragment);
          break;
        case 3: 
          localFragment.setNextAnim(mExitAnim);
          mManager.removeFragment(localFragment);
          break;
        }
      }
      else
      {
        localFragment.setNextAnim(mEnterAnim);
        mManager.setExitAnimationOrder(localFragment, false);
        mManager.addFragment(localFragment);
      }
      if ((!mReorderingAllowed) && (mCmd != 1) && (localFragment != null)) {
        mManager.moveFragmentToExpectedState(localFragment);
      }
      i += 1;
    }
    if (!mReorderingAllowed) {
      mManager.moveToState(mManager.mCurState, true);
    }
  }
  
  void executePopOps(boolean paramBoolean)
  {
    Object localObject2 = mOps;
    Object localObject1 = this;
    int i = ((ArrayList)localObject2).size() - 1;
    while (i >= 0)
    {
      localObject2 = mOps;
      localObject2 = (FragmentTransaction.Op)((ArrayList)localObject2).get(i);
      Fragment localFragment = mFragment;
      if (localFragment != null) {
        localFragment.setNextTransition(FragmentManager.reverseTransit(mTransition));
      }
      int j = mCmd;
      if (j != 1)
      {
        switch (j)
        {
        default: 
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Unknown cmd: ");
          ((StringBuilder)localObject1).append(mCmd);
          throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
        case 10: 
          mManager.setMaxLifecycle(localFragment, mOldMaxState);
          break;
        case 9: 
          mManager.setPrimaryNavigationFragment(localFragment);
          break;
        case 8: 
          mManager.setPrimaryNavigationFragment(null);
          break;
        case 7: 
          localFragment.setNextAnim(mPopExitAnim);
          mManager.setExitAnimationOrder(localFragment, true);
          mManager.detachFragment(localFragment);
          break;
        case 6: 
          localFragment.setNextAnim(mPopEnterAnim);
          mManager.attachFragment(localFragment);
          break;
        case 5: 
          localFragment.setNextAnim(mPopExitAnim);
          mManager.setExitAnimationOrder(localFragment, true);
          mManager.hideFragment(localFragment);
          break;
        case 4: 
          localFragment.setNextAnim(mPopEnterAnim);
          mManager.showFragment(localFragment);
          break;
        case 3: 
          localFragment.setNextAnim(mPopEnterAnim);
          mManager.addFragment(localFragment);
          break;
        }
      }
      else
      {
        localFragment.setNextAnim(mPopExitAnim);
        mManager.setExitAnimationOrder(localFragment, true);
        mManager.removeFragment(localFragment);
      }
      if ((!mReorderingAllowed) && (mCmd != 3) && (localFragment != null)) {
        mManager.moveFragmentToExpectedState(localFragment);
      }
      i -= 1;
    }
    if ((!mReorderingAllowed) && (paramBoolean)) {
      mManager.moveToState(mManager.mCurState, true);
    }
  }
  
  Fragment expandOps(ArrayList paramArrayList, Fragment paramFragment)
  {
    int i = 0;
    for (Fragment localFragment1 = paramFragment; i < mOps.size(); localFragment1 = paramFragment)
    {
      FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(i);
      switch (mCmd)
      {
      default: 
        break;
      case 4: 
      case 5: 
        j = i;
        paramFragment = localFragment1;
        break;
      case 8: 
        mOps.add(i, new FragmentTransaction.Op(9, localFragment1));
        j = i + 1;
        paramFragment = mFragment;
        break;
      case 3: 
      case 6: 
        paramArrayList.remove(mFragment);
        j = i;
        paramFragment = localFragment1;
        if (mFragment != localFragment1) {
          break label485;
        }
        mOps.add(i, new FragmentTransaction.Op(9, mFragment));
        j = i + 1;
        paramFragment = null;
        break;
      case 2: 
        Fragment localFragment2 = mFragment;
        int i1 = mContainerId;
        j = paramArrayList.size() - 1;
        int k = 0;
        for (paramFragment = localFragment1; j >= 0; paramFragment = localFragment1)
        {
          Fragment localFragment3 = (Fragment)paramArrayList.get(j);
          int m = i;
          int n = k;
          localFragment1 = paramFragment;
          if (mContainerId == i1) {
            if (localFragment3 == localFragment2)
            {
              n = 1;
              m = i;
              localFragment1 = paramFragment;
            }
            else
            {
              m = i;
              localFragment1 = paramFragment;
              if (localFragment3 == paramFragment)
              {
                mOps.add(i, new FragmentTransaction.Op(9, localFragment3));
                m = i + 1;
                localFragment1 = null;
              }
              paramFragment = new FragmentTransaction.Op(3, localFragment3);
              mEnterAnim = mEnterAnim;
              mPopEnterAnim = mPopEnterAnim;
              mExitAnim = mExitAnim;
              mPopExitAnim = mPopExitAnim;
              mOps.add(m, paramFragment);
              paramArrayList.remove(localFragment3);
              m += 1;
              n = k;
            }
          }
          j -= 1;
          i = m;
          k = n;
        }
        if (k != 0)
        {
          mOps.remove(i);
          i -= 1;
        }
        for (;;)
        {
          break;
          mCmd = 1;
          paramArrayList.add(localFragment2);
        }
        j = i;
        break;
      }
      paramArrayList.add(mFragment);
      paramFragment = localFragment1;
      int j = i;
      label485:
      i = j + 1;
    }
    return localFragment1;
  }
  
  public boolean generateOps(ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    if (FragmentManager.isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Run: ");
      localStringBuilder.append(this);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    paramArrayList1.add(this);
    paramArrayList2.add(Boolean.valueOf(false));
    if (mAddToBackStack) {
      mManager.addBackStackState(this);
    }
    return true;
  }
  
  public CharSequence getBreadCrumbShortTitle()
  {
    if (mBreadCrumbShortTitleRes != 0) {
      return mManager.mHost.getContext().getText(mBreadCrumbShortTitleRes);
    }
    return mBreadCrumbShortTitleText;
  }
  
  public int getBreadCrumbShortTitleRes()
  {
    return mBreadCrumbShortTitleRes;
  }
  
  public CharSequence getBreadCrumbTitle()
  {
    if (mBreadCrumbTitleRes != 0) {
      return mManager.mHost.getContext().getText(mBreadCrumbTitleRes);
    }
    return mBreadCrumbTitleText;
  }
  
  public int getBreadCrumbTitleRes()
  {
    return mBreadCrumbTitleRes;
  }
  
  public int getId()
  {
    return mIndex;
  }
  
  public String getName()
  {
    return mName;
  }
  
  public FragmentTransaction hide(Fragment paramFragment)
  {
    if ((mFragmentManager != null) && (mFragmentManager != mManager))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot hide Fragment attached to a different FragmentManager. Fragment ");
      localStringBuilder.append(paramFragment.toString());
      localStringBuilder.append(" is already attached to a FragmentManager.");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    return super.hide(paramFragment);
  }
  
  boolean interactsWith(int paramInt)
  {
    int k = mOps.size();
    int i = 0;
    while (i < k)
    {
      FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(i);
      int j;
      if (mFragment != null) {
        j = mFragment.mContainerId;
      } else {
        j = 0;
      }
      if ((j != 0) && (j == paramInt)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  boolean interactsWith(ArrayList paramArrayList, int paramInt1, int paramInt2)
  {
    if (paramInt2 == paramInt1) {
      return false;
    }
    int i1 = mOps.size();
    int j = 0;
    int m;
    for (int k = -1; j < i1; k = m)
    {
      Object localObject = (FragmentTransaction.Op)mOps.get(j);
      int i;
      if (mFragment != null) {
        i = mFragment.mContainerId;
      } else {
        i = 0;
      }
      m = k;
      if (i != 0)
      {
        m = k;
        if (i != k)
        {
          k = paramInt1;
          while (k < paramInt2)
          {
            localObject = (BackStackRecord)paramArrayList.get(k);
            int i2 = mOps.size();
            m = 0;
            while (m < i2)
            {
              FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(m);
              int n;
              if (mFragment != null) {
                n = mFragment.mContainerId;
              } else {
                n = 0;
              }
              if (n == i) {
                return true;
              }
              m += 1;
            }
            k += 1;
          }
          m = i;
        }
      }
      j += 1;
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return mOps.isEmpty();
  }
  
  boolean isPostponed()
  {
    int i = 0;
    while (i < mOps.size())
    {
      if (isFragmentPostponed((FragmentTransaction.Op)mOps.get(i))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public FragmentTransaction remove(Fragment paramFragment)
  {
    if ((mFragmentManager != null) && (mFragmentManager != mManager))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot remove Fragment attached to a different FragmentManager. Fragment ");
      localStringBuilder.append(paramFragment.toString());
      localStringBuilder.append(" is already attached to a FragmentManager.");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    return super.remove(paramFragment);
  }
  
  public void runOnCommitRunnables()
  {
    if (mCommitRunnables != null)
    {
      int i = 0;
      while (i < mCommitRunnables.size())
      {
        ((Runnable)mCommitRunnables.get(i)).run();
        i += 1;
      }
      mCommitRunnables = null;
    }
  }
  
  public FragmentTransaction setMaxLifecycle(Fragment paramFragment, Lifecycle.State paramState)
  {
    if (mFragmentManager == mManager)
    {
      if (paramState.isAtLeast(Lifecycle.State.CREATED)) {
        return super.setMaxLifecycle(paramFragment, paramState);
      }
      paramFragment = new StringBuilder();
      paramFragment.append("Cannot set maximum Lifecycle below ");
      paramFragment.append(Lifecycle.State.CREATED);
      throw new IllegalArgumentException(paramFragment.toString());
    }
    paramFragment = new StringBuilder();
    paramFragment.append("Cannot setMaxLifecycle for Fragment not attached to FragmentManager ");
    paramFragment.append(mManager);
    throw new IllegalArgumentException(paramFragment.toString());
  }
  
  void setOnStartPostponedListener(Fragment.OnStartEnterTransitionListener paramOnStartEnterTransitionListener)
  {
    int i = 0;
    while (i < mOps.size())
    {
      FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(i);
      if (isFragmentPostponed(localOp)) {
        mFragment.setOnStartEnterTransitionListener(paramOnStartEnterTransitionListener);
      }
      i += 1;
    }
  }
  
  public FragmentTransaction setPrimaryNavigationFragment(Fragment paramFragment)
  {
    if ((paramFragment != null) && (mFragmentManager != null) && (mFragmentManager != mManager))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot setPrimaryNavigation for Fragment attached to a different FragmentManager. Fragment ");
      localStringBuilder.append(paramFragment.toString());
      localStringBuilder.append(" is already attached to a FragmentManager.");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    return super.setPrimaryNavigationFragment(paramFragment);
  }
  
  public FragmentTransaction show(Fragment paramFragment)
  {
    if ((mFragmentManager != null) && (mFragmentManager != mManager))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot show Fragment attached to a different FragmentManager. Fragment ");
      localStringBuilder.append(paramFragment.toString());
      localStringBuilder.append(" is already attached to a FragmentManager.");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    return super.show(paramFragment);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("BackStackEntry{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    if (mIndex >= 0)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(mIndex);
    }
    if (mName != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(mName);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  Fragment trackAddedFragmentsInPop(ArrayList paramArrayList, Fragment paramFragment)
  {
    int i = mOps.size() - 1;
    while (i >= 0)
    {
      FragmentTransaction.Op localOp = (FragmentTransaction.Op)mOps.get(i);
      int j = mCmd;
      if (j != 1)
      {
        if (j != 3) {}
        switch (j)
        {
        default: 
          break;
        case 10: 
          mCurrentMaxState = mOldMaxState;
          break;
        case 9: 
          paramFragment = mFragment;
          break;
        case 8: 
          paramFragment = null;
          break;
        case 6: 
          paramArrayList.add(mFragment);
          break;
        }
      }
      else
      {
        paramArrayList.remove(mFragment);
      }
      i -= 1;
    }
    return paramFragment;
  }
}
