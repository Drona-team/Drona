package androidx.fragment.package_5;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle.State;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public abstract class FragmentTransaction
{
  static final int OP_ADD = 1;
  static final int OP_ATTACH = 7;
  static final int OP_DETACH = 6;
  static final int OP_HIDE = 4;
  static final int OP_NULL = 0;
  static final int OP_REMOVE = 3;
  static final int OP_REPLACE = 2;
  static final int OP_SET_MAX_LIFECYCLE = 10;
  static final int OP_SET_PRIMARY_NAV = 8;
  static final int OP_SHOW = 5;
  static final int OP_UNSET_PRIMARY_NAV = 9;
  public static final int TRANSIT_ENTER_MASK = 4096;
  public static final int TRANSIT_EXIT_MASK = 8192;
  public static final int TRANSIT_FRAGMENT_CLOSE = 8194;
  public static final int TRANSIT_FRAGMENT_FADE = 4099;
  public static final int TRANSIT_FRAGMENT_OPEN = 4097;
  public static final int TRANSIT_NONE = 0;
  public static final int TRANSIT_UNSET = -1;
  boolean mAddToBackStack;
  boolean mAllowAddToBackStack = true;
  int mBreadCrumbShortTitleRes;
  CharSequence mBreadCrumbShortTitleText;
  int mBreadCrumbTitleRes;
  CharSequence mBreadCrumbTitleText;
  private final ClassLoader mClassLoader;
  ArrayList<Runnable> mCommitRunnables;
  int mEnterAnim;
  int mExitAnim;
  private final FragmentFactory mFragmentFactory;
  @Nullable
  String mName;
  ArrayList<androidx.fragment.app.FragmentTransaction.Op> mOps = new ArrayList();
  int mPopEnterAnim;
  int mPopExitAnim;
  boolean mReorderingAllowed = false;
  ArrayList<String> mSharedElementSourceNames;
  ArrayList<String> mSharedElementTargetNames;
  int mTransition;
  
  public FragmentTransaction()
  {
    mFragmentFactory = null;
    mClassLoader = null;
  }
  
  FragmentTransaction(FragmentFactory paramFragmentFactory, ClassLoader paramClassLoader)
  {
    mFragmentFactory = paramFragmentFactory;
    mClassLoader = paramClassLoader;
  }
  
  private Fragment createFragment(Class paramClass, Bundle paramBundle)
  {
    if (mFragmentFactory != null)
    {
      if (mClassLoader != null)
      {
        paramClass = mFragmentFactory.instantiate(mClassLoader, paramClass.getName());
        if (paramBundle != null)
        {
          paramClass.setArguments(paramBundle);
          return paramClass;
        }
      }
      else
      {
        throw new IllegalStateException("The FragmentManager must be attached to itshost to create a Fragment");
      }
    }
    else {
      throw new IllegalStateException("Creating a Fragment requires that this FragmentTransaction was built with FragmentManager.beginTransaction()");
    }
    return paramClass;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment)
  {
    doAddOp(paramInt, paramFragment, null, 1);
    return this;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment, String paramString)
  {
    doAddOp(paramInt, paramFragment, paramString, 1);
    return this;
  }
  
  public final FragmentTransaction add(int paramInt, Class paramClass, Bundle paramBundle)
  {
    return add(paramInt, createFragment(paramClass, paramBundle));
  }
  
  public final FragmentTransaction add(int paramInt, Class paramClass, Bundle paramBundle, String paramString)
  {
    return add(paramInt, createFragment(paramClass, paramBundle), paramString);
  }
  
  FragmentTransaction add(ViewGroup paramViewGroup, Fragment paramFragment, String paramString)
  {
    mContainer = paramViewGroup;
    return add(paramViewGroup.getId(), paramFragment, paramString);
  }
  
  public FragmentTransaction add(Fragment paramFragment, String paramString)
  {
    doAddOp(0, paramFragment, paramString, 1);
    return this;
  }
  
  public final FragmentTransaction add(Class paramClass, Bundle paramBundle, String paramString)
  {
    return add(createFragment(paramClass, paramBundle), paramString);
  }
  
  void addOp(Op paramOp)
  {
    mOps.add(paramOp);
    mEnterAnim = mEnterAnim;
    mExitAnim = mExitAnim;
    mPopEnterAnim = mPopEnterAnim;
    mPopExitAnim = mPopExitAnim;
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString)
  {
    if (FragmentTransition.supportsTransition())
    {
      paramView = ViewCompat.getTransitionName(paramView);
      if (paramView != null)
      {
        if (mSharedElementSourceNames == null)
        {
          mSharedElementSourceNames = new ArrayList();
          mSharedElementTargetNames = new ArrayList();
        }
        else
        {
          if (mSharedElementTargetNames.contains(paramString)) {
            break label129;
          }
          if (mSharedElementSourceNames.contains(paramView)) {
            break label89;
          }
        }
        mSharedElementSourceNames.add(paramView);
        mSharedElementTargetNames.add(paramString);
        return this;
        label89:
        paramString = new StringBuilder();
        paramString.append("A shared element with the source name '");
        paramString.append(paramView);
        paramString.append("' has already been added to the transaction.");
        throw new IllegalArgumentException(paramString.toString());
        label129:
        paramView = new StringBuilder();
        paramView.append("A shared element with the target name '");
        paramView.append(paramString);
        paramView.append("' has already been added to the transaction.");
        throw new IllegalArgumentException(paramView.toString());
      }
      throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
    }
    return this;
  }
  
  public FragmentTransaction addToBackStack(String paramString)
  {
    if (mAllowAddToBackStack)
    {
      mAddToBackStack = true;
      mName = paramString;
      return this;
    }
    throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
  }
  
  public FragmentTransaction attach(Fragment paramFragment)
  {
    addOp(new Op(7, paramFragment));
    return this;
  }
  
  public abstract int commit();
  
  public abstract int commitAllowingStateLoss();
  
  public abstract void commitNow();
  
  public abstract void commitNowAllowingStateLoss();
  
  public FragmentTransaction detach(Fragment paramFragment)
  {
    addOp(new Op(6, paramFragment));
    return this;
  }
  
  public FragmentTransaction disallowAddToBackStack()
  {
    if (!mAddToBackStack)
    {
      mAllowAddToBackStack = false;
      return this;
    }
    throw new IllegalStateException("This transaction is already being added to the back stack");
  }
  
  void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    Object localObject = paramFragment.getClass();
    int i = ((Class)localObject).getModifiers();
    if ((!((Class)localObject).isAnonymousClass()) && (Modifier.isPublic(i)) && ((!((Class)localObject).isMemberClass()) || (Modifier.isStatic(i))))
    {
      if (paramString != null)
      {
        if ((mTag != null) && (!paramString.equals(mTag)))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Can't change tag of fragment ");
          ((StringBuilder)localObject).append(paramFragment);
          ((StringBuilder)localObject).append(": was ");
          ((StringBuilder)localObject).append(mTag);
          ((StringBuilder)localObject).append(" now ");
          ((StringBuilder)localObject).append(paramString);
          throw new IllegalStateException(((StringBuilder)localObject).toString());
        }
        mTag = paramString;
      }
      if (paramInt1 != 0) {
        if (paramInt1 != -1)
        {
          if ((mFragmentId != 0) && (mFragmentId != paramInt1))
          {
            paramString = new StringBuilder();
            paramString.append("Can't change container ID of fragment ");
            paramString.append(paramFragment);
            paramString.append(": was ");
            paramString.append(mFragmentId);
            paramString.append(" now ");
            paramString.append(paramInt1);
            throw new IllegalStateException(paramString.toString());
          }
          mFragmentId = paramInt1;
          mContainerId = paramInt1;
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Can't add fragment ");
          ((StringBuilder)localObject).append(paramFragment);
          ((StringBuilder)localObject).append(" with tag ");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(" to container view with no id");
          throw new IllegalArgumentException(((StringBuilder)localObject).toString());
        }
      }
      addOp(new Op(paramInt2, paramFragment));
      return;
    }
    paramFragment = new StringBuilder();
    paramFragment.append("Fragment ");
    paramFragment.append(((Class)localObject).getCanonicalName());
    paramFragment.append(" must be a public static class to be  properly recreated from instance state.");
    throw new IllegalStateException(paramFragment.toString());
  }
  
  public FragmentTransaction hide(Fragment paramFragment)
  {
    addOp(new Op(4, paramFragment));
    return this;
  }
  
  public boolean isAddToBackStackAllowed()
  {
    return mAllowAddToBackStack;
  }
  
  public boolean isEmpty()
  {
    return mOps.isEmpty();
  }
  
  public FragmentTransaction remove(Fragment paramFragment)
  {
    addOp(new Op(3, paramFragment));
    return this;
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment)
  {
    return replace(paramInt, paramFragment, null);
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment, String paramString)
  {
    if (paramInt != 0)
    {
      doAddOp(paramInt, paramFragment, paramString, 2);
      return this;
    }
    throw new IllegalArgumentException("Must use non-zero containerViewId");
  }
  
  public final FragmentTransaction replace(int paramInt, Class paramClass, Bundle paramBundle)
  {
    return replace(paramInt, paramClass, paramBundle, null);
  }
  
  public final FragmentTransaction replace(int paramInt, Class paramClass, Bundle paramBundle, String paramString)
  {
    return replace(paramInt, createFragment(paramClass, paramBundle), paramString);
  }
  
  public FragmentTransaction runOnCommit(Runnable paramRunnable)
  {
    disallowAddToBackStack();
    if (mCommitRunnables == null) {
      mCommitRunnables = new ArrayList();
    }
    mCommitRunnables.add(paramRunnable);
    return this;
  }
  
  public FragmentTransaction setAllowOptimization(boolean paramBoolean)
  {
    return setReorderingAllowed(paramBoolean);
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(int paramInt)
  {
    mBreadCrumbShortTitleRes = paramInt;
    mBreadCrumbShortTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(CharSequence paramCharSequence)
  {
    mBreadCrumbShortTitleRes = 0;
    mBreadCrumbShortTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(int paramInt)
  {
    mBreadCrumbTitleRes = paramInt;
    mBreadCrumbTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(CharSequence paramCharSequence)
  {
    mBreadCrumbTitleRes = 0;
    mBreadCrumbTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2)
  {
    return setCustomAnimations(paramInt1, paramInt2, 0, 0);
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mEnterAnim = paramInt1;
    mExitAnim = paramInt2;
    mPopEnterAnim = paramInt3;
    mPopExitAnim = paramInt4;
    return this;
  }
  
  public FragmentTransaction setMaxLifecycle(Fragment paramFragment, Lifecycle.State paramState)
  {
    addOp(new Op(10, paramFragment, paramState));
    return this;
  }
  
  public FragmentTransaction setPrimaryNavigationFragment(Fragment paramFragment)
  {
    addOp(new Op(8, paramFragment));
    return this;
  }
  
  public FragmentTransaction setReorderingAllowed(boolean paramBoolean)
  {
    mReorderingAllowed = paramBoolean;
    return this;
  }
  
  public FragmentTransaction setTransition(int paramInt)
  {
    mTransition = paramInt;
    return this;
  }
  
  public FragmentTransaction setTransitionStyle(int paramInt)
  {
    return this;
  }
  
  public FragmentTransaction show(Fragment paramFragment)
  {
    addOp(new Op(5, paramFragment));
    return this;
  }
  
  final class Op
  {
    int mCmd;
    Lifecycle.State mCurrentMaxState;
    int mEnterAnim;
    int mExitAnim;
    Fragment mFragment;
    Lifecycle.State mOldMaxState;
    int mPopEnterAnim;
    int mPopExitAnim;
    
    Op() {}
    
    Op(Fragment paramFragment)
    {
      mCmd = this$1;
      mFragment = paramFragment;
      mOldMaxState = Lifecycle.State.RESUMED;
      mCurrentMaxState = Lifecycle.State.RESUMED;
    }
    
    Op(Fragment paramFragment, Lifecycle.State paramState)
    {
      mCmd = this$1;
      mFragment = paramFragment;
      mOldMaxState = mMaxState;
      mCurrentMaxState = paramState;
    }
  }
}
