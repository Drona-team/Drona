package androidx.fragment.package_5;

import android.animation.Animator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.core.util.LogWriter;
import androidx.fragment.R.id;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FragmentManager
{
  private static boolean DEBUG = false;
  public static final int POP_BACK_STACK_INCLUSIVE = 1;
  static final String TAG = "FragmentManager";
  ArrayList<androidx.fragment.app.BackStackRecord> mBackStack;
  private ArrayList<androidx.fragment.app.FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  private final AtomicInteger mBackStackIndex = new AtomicInteger();
  FragmentContainer mContainer;
  private ArrayList<androidx.fragment.app.Fragment> mCreatedMenus;
  int mCurState = -1;
  private boolean mDestroyed;
  private Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      execPendingActions(true);
    }
  };
  private boolean mExecutingActions;
  private ConcurrentHashMap<androidx.fragment.app.Fragment, HashSet<androidx.core.os.CancellationSignal>> mExitAnimationCancellationSignals = new ConcurrentHashMap();
  private FragmentFactory mFragmentFactory = null;
  private final FragmentStore mFragmentStore = new FragmentStore();
  private final FragmentTransition.Callback mFragmentTransitionCallback = new FragmentTransition.Callback()
  {
    public void onComplete(Fragment paramAnonymousFragment, androidx.core.opml.CancellationSignal paramAnonymousCancellationSignal)
    {
      if (!paramAnonymousCancellationSignal.isCanceled()) {
        removeCancellationSignal(paramAnonymousFragment, paramAnonymousCancellationSignal);
      }
    }
    
    public void onStart(Fragment paramAnonymousFragment, androidx.core.opml.CancellationSignal paramAnonymousCancellationSignal)
    {
      addCancellationSignal(paramAnonymousFragment, paramAnonymousCancellationSignal);
    }
  };
  private boolean mHavePendingDeferredStart;
  androidx.fragment.app.FragmentHostCallback<?> mHost;
  private FragmentFactory mHostFragmentFactory = new FragmentFactory()
  {
    public Fragment instantiate(ClassLoader paramAnonymousClassLoader, String paramAnonymousString)
    {
      return mHost.instantiate(mHost.getContext(), paramAnonymousString, null);
    }
  };
  private final FragmentLayoutInflaterFactory mLayoutInflaterFactory = new FragmentLayoutInflaterFactory(this);
  private final FragmentLifecycleCallbacksDispatcher mLifecycleCallbacksDispatcher = new FragmentLifecycleCallbacksDispatcher(this);
  private boolean mNeedMenuInvalidate;
  private FragmentManagerViewModel mNonConfig;
  private final OnBackPressedCallback mOnBackPressedCallback = new OnBackPressedCallback(false)
  {
    public void handleOnBackPressed()
    {
      FragmentManager.this.handleOnBackPressed();
    }
  };
  private OnBackPressedDispatcher mOnBackPressedDispatcher;
  private Fragment mParent;
  private final ArrayList<androidx.fragment.app.FragmentManager.OpGenerator> mPendingActions = new ArrayList();
  private ArrayList<androidx.fragment.app.FragmentManager.StartEnterTransitionListener> mPostponedTransactions;
  @Nullable
  Fragment mPrimaryNav;
  private boolean mStateSaved;
  private boolean mStopped;
  private ArrayList<androidx.fragment.app.Fragment> mTmpAddedFragments;
  private ArrayList<Boolean> mTmpIsPop;
  private ArrayList<androidx.fragment.app.BackStackRecord> mTmpRecords;
  
  public FragmentManager() {}
  
  private void addAddedFragments(ArraySet paramArraySet)
  {
    if (mCurState < 1) {
      return;
    }
    int i = Math.min(mCurState, 3);
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (mState < i)
      {
        moveToState(localFragment, i);
        if ((mView != null) && (!mHidden) && (mIsNewlyAdded)) {
          paramArraySet.add(localFragment);
        }
      }
    }
  }
  
  private void cancelExitAnimation(Fragment paramFragment)
  {
    HashSet localHashSet = (HashSet)mExitAnimationCancellationSignals.get(paramFragment);
    if (localHashSet != null)
    {
      Iterator localIterator = localHashSet.iterator();
      while (localIterator.hasNext()) {
        ((androidx.core.opml.CancellationSignal)localIterator.next()).cancel();
      }
      localHashSet.clear();
      destroyFragmentView(paramFragment);
      mExitAnimationCancellationSignals.remove(paramFragment);
    }
  }
  
  private void checkStateLoss()
  {
    if (!isStateSaved()) {
      return;
    }
    throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
  }
  
  private void cleanupExec()
  {
    mExecutingActions = false;
    mTmpIsPop.clear();
    mTmpRecords.clear();
  }
  
  private void completeShowHideFragment(Fragment paramFragment)
  {
    if (mView != null)
    {
      FragmentAnim.AnimationOrAnimator localAnimationOrAnimator = FragmentAnim.loadAnimation(mHost.getContext(), mContainer, paramFragment, mHidden ^ true);
      if ((localAnimationOrAnimator != null) && (animator != null))
      {
        animator.setTarget(mView);
        if (mHidden)
        {
          if (paramFragment.isHideReplaced())
          {
            paramFragment.setHideReplaced(false);
          }
          else
          {
            ViewGroup localViewGroup = mContainer;
            View localView = mView;
            localViewGroup.startViewTransition(localView);
            animator.addListener(new FragmentManager.5(this, localViewGroup, localView, paramFragment));
          }
        }
        else {
          mView.setVisibility(0);
        }
        animator.start();
      }
      else
      {
        if (localAnimationOrAnimator != null)
        {
          mView.startAnimation(animation);
          animation.start();
        }
        int i;
        if ((mHidden) && (!paramFragment.isHideReplaced())) {
          i = 8;
        } else {
          i = 0;
        }
        mView.setVisibility(i);
        if (paramFragment.isHideReplaced()) {
          paramFragment.setHideReplaced(false);
        }
      }
    }
    if ((mAdded) && (isMenuAvailable(paramFragment))) {
      mNeedMenuInvalidate = true;
    }
    mHiddenChanged = false;
    paramFragment.onHiddenChanged(mHidden);
  }
  
  private void destroyFragmentView(Fragment paramFragment)
  {
    paramFragment.performDestroyView();
    mLifecycleCallbacksDispatcher.dispatchOnFragmentViewDestroyed(paramFragment, false);
    mContainer = null;
    mView = null;
    mViewLifecycleOwner = null;
    mViewLifecycleOwnerLiveData.setValue(null);
    mInLayout = false;
  }
  
  private void dispatchParentPrimaryNavigationFragmentChanged(Fragment paramFragment)
  {
    if ((paramFragment != null) && (paramFragment.equals(findActiveFragment(mWho)))) {
      paramFragment.performPrimaryNavigationFragmentChanged();
    }
  }
  
  private void dispatchStateChange(int paramInt)
  {
    try
    {
      mExecutingActions = true;
      mFragmentStore.dispatchStateChange(paramInt);
      moveToState(paramInt, false);
      mExecutingActions = false;
      execPendingActions(true);
      return;
    }
    catch (Throwable localThrowable)
    {
      mExecutingActions = false;
      throw localThrowable;
    }
  }
  
  private void doPendingDeferredStart()
  {
    if (mHavePendingDeferredStart)
    {
      mHavePendingDeferredStart = false;
      startPendingDeferredFragments();
    }
  }
  
  public static void enableDebugLogging(boolean paramBoolean)
  {
    DEBUG = paramBoolean;
  }
  
  private void endAnimatingAwayFragments()
  {
    if (!mExitAnimationCancellationSignals.isEmpty())
    {
      Iterator localIterator = mExitAnimationCancellationSignals.keySet().iterator();
      while (localIterator.hasNext())
      {
        Fragment localFragment = (Fragment)localIterator.next();
        cancelExitAnimation(localFragment);
        moveToState(localFragment, localFragment.getStateAfterAnimating());
      }
    }
  }
  
  private void ensureExecReady(boolean paramBoolean)
  {
    if (!mExecutingActions)
    {
      if (mHost == null)
      {
        if (mDestroyed) {
          throw new IllegalStateException("FragmentManager has been destroyed");
        }
        throw new IllegalStateException("FragmentManager has not been attached to a host.");
      }
      if (Looper.myLooper() == mHost.getHandler().getLooper())
      {
        if (!paramBoolean) {
          checkStateLoss();
        }
        if (mTmpRecords == null)
        {
          mTmpRecords = new ArrayList();
          mTmpIsPop = new ArrayList();
        }
        mExecutingActions = true;
        try
        {
          executePostponedTransaction(null, null);
          mExecutingActions = false;
          return;
        }
        catch (Throwable localThrowable)
        {
          mExecutingActions = false;
          throw localThrowable;
        }
      }
      throw new IllegalStateException("Must be called from main thread of fragment host");
    }
    throw new IllegalStateException("FragmentManager is already executing transactions");
  }
  
  private static void executeOps(ArrayList paramArrayList1, ArrayList paramArrayList2, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList1.get(paramInt1);
      boolean bool2 = ((Boolean)paramArrayList2.get(paramInt1)).booleanValue();
      boolean bool1 = true;
      if (bool2)
      {
        localBackStackRecord.bumpBackStackNesting(-1);
        if (paramInt1 != paramInt2 - 1) {
          bool1 = false;
        }
        localBackStackRecord.executePopOps(bool1);
      }
      else
      {
        localBackStackRecord.bumpBackStackNesting(1);
        localBackStackRecord.executeOps();
      }
      paramInt1 += 1;
    }
  }
  
  private void executeOpsTogether(ArrayList paramArrayList1, ArrayList paramArrayList2, int paramInt1, int paramInt2)
  {
    int j = paramInt1;
    boolean bool = getmReorderingAllowed;
    if (mTmpAddedFragments == null) {
      mTmpAddedFragments = new ArrayList();
    } else {
      mTmpAddedFragments.clear();
    }
    mTmpAddedFragments.addAll(mFragmentStore.getFragments());
    Object localObject = getPrimaryNavigationFragment();
    int k = paramInt1;
    int i = 0;
    while (k < paramInt2)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList1.get(k);
      if (!((Boolean)paramArrayList2.get(k)).booleanValue()) {
        localObject = localBackStackRecord.expandOps(mTmpAddedFragments, (Fragment)localObject);
      } else {
        localObject = localBackStackRecord.trackAddedFragmentsInPop(mTmpAddedFragments, (Fragment)localObject);
      }
      if ((i == 0) && (!mAddToBackStack)) {
        i = 0;
      } else {
        i = 1;
      }
      k += 1;
    }
    mTmpAddedFragments.clear();
    if (!bool) {
      FragmentTransition.startTransitions(this, paramArrayList1, paramArrayList2, paramInt1, paramInt2, false, mFragmentTransitionCallback);
    }
    executeOps(paramArrayList1, paramArrayList2, paramInt1, paramInt2);
    if (bool)
    {
      localObject = new ArraySet();
      addAddedFragments((ArraySet)localObject);
      k = postponePostponableTransactions(paramArrayList1, paramArrayList2, paramInt1, paramInt2, (ArraySet)localObject);
      makeRemovedFragmentsInvisible((ArraySet)localObject);
    }
    else
    {
      k = paramInt2;
    }
    int m = j;
    if (k != paramInt1)
    {
      m = j;
      if (bool)
      {
        FragmentTransition.startTransitions(this, paramArrayList1, paramArrayList2, paramInt1, k, true, mFragmentTransitionCallback);
        moveToState(mCurState, true);
        m = j;
      }
    }
    while (m < paramInt2)
    {
      localObject = (BackStackRecord)paramArrayList1.get(m);
      if ((((Boolean)paramArrayList2.get(m)).booleanValue()) && (mIndex >= 0)) {
        mIndex = -1;
      }
      ((BackStackRecord)localObject).runOnCommitRunnables();
      m += 1;
    }
    if (i != 0) {
      reportBackStackChanged();
    }
  }
  
  private void executePostponedTransaction(ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    int i;
    if (mPostponedTransactions == null) {
      i = 0;
    } else {
      i = mPostponedTransactions.size();
    }
    int j = 0;
    for (int m = i; j < m; m = i)
    {
      StartEnterTransitionListener localStartEnterTransitionListener = (StartEnterTransitionListener)mPostponedTransactions.get(j);
      int k;
      if ((paramArrayList1 != null) && (!mIsBack))
      {
        i = paramArrayList1.indexOf(mRecord);
        if ((i != -1) && (paramArrayList2 != null) && (((Boolean)paramArrayList2.get(i)).booleanValue()))
        {
          mPostponedTransactions.remove(j);
          k = j - 1;
          i = m - 1;
          localStartEnterTransitionListener.cancelTransaction();
          break label246;
        }
      }
      if (!localStartEnterTransitionListener.isReady())
      {
        i = m;
        k = j;
        if (paramArrayList1 != null)
        {
          i = m;
          k = j;
          if (!mRecord.interactsWith(paramArrayList1, 0, paramArrayList1.size())) {}
        }
      }
      else
      {
        mPostponedTransactions.remove(j);
        k = j - 1;
        i = m - 1;
        if ((paramArrayList1 != null) && (!mIsBack))
        {
          j = paramArrayList1.indexOf(mRecord);
          if ((j != -1) && (paramArrayList2 != null) && (((Boolean)paramArrayList2.get(j)).booleanValue()))
          {
            localStartEnterTransitionListener.cancelTransaction();
            break label246;
          }
        }
        localStartEnterTransitionListener.completeTransaction();
      }
      label246:
      j = k + 1;
    }
  }
  
  public static Fragment findFragment(View paramView)
  {
    Object localObject = findViewFragment(paramView);
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("View ");
    ((StringBuilder)localObject).append(paramView);
    ((StringBuilder)localObject).append(" does not have a Fragment set");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  static FragmentManager findFragmentManager(View paramView)
  {
    Object localObject1 = findViewFragment(paramView);
    if (localObject1 != null) {
      return ((Fragment)localObject1).getChildFragmentManager();
    }
    localObject1 = paramView.getContext();
    Object localObject3 = null;
    Object localObject2;
    for (;;)
    {
      localObject2 = localObject3;
      if (!(localObject1 instanceof ContextWrapper)) {
        break;
      }
      if ((localObject1 instanceof FragmentActivity))
      {
        localObject2 = (FragmentActivity)localObject1;
        break;
      }
      localObject1 = ((ContextWrapper)localObject1).getBaseContext();
    }
    if (localObject2 != null) {
      return ((FragmentActivity)localObject2).getSupportFragmentManager();
    }
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("View ");
    ((StringBuilder)localObject1).append(paramView);
    ((StringBuilder)localObject1).append(" is not within a subclass of FragmentActivity.");
    throw new IllegalStateException(((StringBuilder)localObject1).toString());
  }
  
  private static Fragment findViewFragment(View paramView)
  {
    while (paramView != null)
    {
      Fragment localFragment = getViewFragment(paramView);
      if (localFragment != null) {
        return localFragment;
      }
      paramView = paramView.getParent();
      if ((paramView instanceof View)) {
        paramView = (View)paramView;
      } else {
        paramView = null;
      }
    }
    return null;
  }
  
  private void forcePostponedTransactions()
  {
    if (mPostponedTransactions != null) {
      while (!mPostponedTransactions.isEmpty()) {
        ((StartEnterTransitionListener)mPostponedTransactions.remove(0)).completeTransaction();
      }
    }
  }
  
  private boolean generateOpsForPendingActions(ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    ArrayList localArrayList = mPendingActions;
    try
    {
      boolean bool = mPendingActions.isEmpty();
      int i = 0;
      if (bool) {
        return false;
      }
      int j = mPendingActions.size();
      bool = false;
      while (i < j)
      {
        bool |= ((OpGenerator)mPendingActions.get(i)).generateOps(paramArrayList1, paramArrayList2);
        i += 1;
      }
      mPendingActions.clear();
      mHost.getHandler().removeCallbacks(mExecCommit);
      return bool;
    }
    catch (Throwable paramArrayList1)
    {
      throw paramArrayList1;
    }
  }
  
  private FragmentManagerViewModel getChildNonConfig(Fragment paramFragment)
  {
    return mNonConfig.getChildNonConfig(paramFragment);
  }
  
  private ViewGroup getFragmentContainer(Fragment paramFragment)
  {
    if (mContainerId <= 0) {
      return null;
    }
    if (mContainer.onHasView())
    {
      paramFragment = mContainer.onFindViewById(mContainerId);
      if ((paramFragment instanceof ViewGroup)) {
        return (ViewGroup)paramFragment;
      }
    }
    return null;
  }
  
  static Fragment getViewFragment(View paramView)
  {
    paramView = paramView.getTag(R.id.fragment_container_view_tag);
    if ((paramView instanceof Fragment)) {
      return (Fragment)paramView;
    }
    return null;
  }
  
  static boolean isLoggingEnabled(int paramInt)
  {
    return (DEBUG) || (Log.isLoggable("FragmentManager", paramInt));
  }
  
  private boolean isMenuAvailable(Fragment paramFragment)
  {
    return ((mHasMenu) && (mMenuVisible)) || (mChildFragmentManager.checkForMenus());
  }
  
  private void makeInactive(FragmentStateManager paramFragmentStateManager)
  {
    Fragment localFragment = paramFragmentStateManager.getFragment();
    if (!mFragmentStore.containsActiveFragment(mWho)) {
      return;
    }
    if (isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Removed fragment from active set ");
      localStringBuilder.append(localFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    mFragmentStore.makeInactive(paramFragmentStateManager);
    removeRetainedFragment(localFragment);
  }
  
  private void makeRemovedFragmentsInvisible(ArraySet paramArraySet)
  {
    int j = paramArraySet.size();
    int i = 0;
    while (i < j)
    {
      Fragment localFragment = (Fragment)paramArraySet.valueAt(i);
      if (!mAdded)
      {
        View localView = localFragment.requireView();
        mPostponedAlpha = localView.getAlpha();
        localView.setAlpha(0.0F);
      }
      i += 1;
    }
  }
  
  private boolean popBackStackImmediate(String paramString, int paramInt1, int paramInt2)
  {
    execPendingActions(false);
    ensureExecReady(true);
    if ((mPrimaryNav != null) && (paramInt1 < 0) && (paramString == null) && (mPrimaryNav.getChildFragmentManager().popBackStackImmediate())) {
      return true;
    }
    boolean bool = popBackStackState(mTmpRecords, mTmpIsPop, paramString, paramInt1, paramInt2);
    if (bool)
    {
      mExecutingActions = true;
      try
      {
        removeRedundantOperationsAndExecute(mTmpRecords, mTmpIsPop);
        cleanupExec();
      }
      catch (Throwable paramString)
      {
        cleanupExec();
        throw paramString;
      }
    }
    updateOnBackPressedCallbackEnabled();
    doPendingDeferredStart();
    mFragmentStore.burpActive();
    return bool;
  }
  
  private int postponePostponableTransactions(ArrayList paramArrayList1, ArrayList paramArrayList2, int paramInt1, int paramInt2, ArraySet paramArraySet)
  {
    int i = paramInt2 - 1;
    int k;
    for (int j = paramInt2; i >= paramInt1; j = k)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList1.get(i);
      boolean bool = ((Boolean)paramArrayList2.get(i)).booleanValue();
      int m;
      if ((localBackStackRecord.isPostponed()) && (!localBackStackRecord.interactsWith(paramArrayList1, i + 1, paramInt2))) {
        m = 1;
      } else {
        m = 0;
      }
      k = j;
      if (m != 0)
      {
        if (mPostponedTransactions == null) {
          mPostponedTransactions = new ArrayList();
        }
        StartEnterTransitionListener localStartEnterTransitionListener = new StartEnterTransitionListener(localBackStackRecord, bool);
        mPostponedTransactions.add(localStartEnterTransitionListener);
        localBackStackRecord.setOnStartPostponedListener(localStartEnterTransitionListener);
        if (bool) {
          localBackStackRecord.executeOps();
        } else {
          localBackStackRecord.executePopOps(false);
        }
        k = j - 1;
        if (i != k)
        {
          paramArrayList1.remove(i);
          paramArrayList1.add(k, localBackStackRecord);
        }
        addAddedFragments(paramArraySet);
      }
      i -= 1;
    }
    return j;
  }
  
  private void removeRedundantOperationsAndExecute(ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    if (paramArrayList1.isEmpty()) {
      return;
    }
    if (paramArrayList1.size() == paramArrayList2.size())
    {
      executePostponedTransaction(paramArrayList1, paramArrayList2);
      int n = paramArrayList1.size();
      int i = 0;
      int j;
      for (int k = 0; i < n; k = j)
      {
        int m = i;
        j = k;
        if (!getmReorderingAllowed)
        {
          if (k != i) {
            executeOpsTogether(paramArrayList1, paramArrayList2, k, i);
          }
          k = i + 1;
          j = k;
          if (((Boolean)paramArrayList2.get(i)).booleanValue()) {
            for (;;)
            {
              j = k;
              if (k >= n) {
                break;
              }
              j = k;
              if (!((Boolean)paramArrayList2.get(k)).booleanValue()) {
                break;
              }
              j = k;
              if (getmReorderingAllowed) {
                break;
              }
              k += 1;
            }
          }
          executeOpsTogether(paramArrayList1, paramArrayList2, i, j);
          m = j - 1;
        }
        i = m + 1;
      }
      if (k != n) {
        executeOpsTogether(paramArrayList1, paramArrayList2, k, n);
      }
    }
    else
    {
      throw new IllegalStateException("Internal error with the back stack records");
    }
  }
  
  private void reportBackStackChanged()
  {
    if (mBackStackChangeListeners != null)
    {
      int i = 0;
      while (i < mBackStackChangeListeners.size())
      {
        ((OnBackStackChangedListener)mBackStackChangeListeners.get(i)).onBackStackChanged();
        i += 1;
      }
    }
  }
  
  static int reverseTransit(int paramInt)
  {
    if (paramInt != 4097)
    {
      if (paramInt != 4099)
      {
        if (paramInt != 8194) {
          return 0;
        }
        return 4097;
      }
      return 4099;
    }
    return 8194;
  }
  
  private void setVisibleRemovingFragment(Fragment paramFragment)
  {
    ViewGroup localViewGroup = getFragmentContainer(paramFragment);
    if (localViewGroup != null)
    {
      if (localViewGroup.getTag(R.id.visible_removing_fragment_view_tag) == null) {
        localViewGroup.setTag(R.id.visible_removing_fragment_view_tag, paramFragment);
      }
      ((Fragment)localViewGroup.getTag(R.id.visible_removing_fragment_view_tag)).setNextAnim(paramFragment.getNextAnim());
    }
  }
  
  private void startPendingDeferredFragments()
  {
    Iterator localIterator = mFragmentStore.getActiveFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        performPendingDeferredStart(localFragment);
      }
    }
  }
  
  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (mHost != null)
    {
      FragmentHostCallback localFragmentHostCallback = mHost;
      try
      {
        localFragmentHostCallback.onDump("  ", null, localPrintWriter, new String[0]);
      }
      catch (Exception localException1)
      {
        Log.e("FragmentManager", "Failed dumping state", localException1);
      }
    }
    else
    {
      try
      {
        dump("  ", null, localException1, new String[0]);
      }
      catch (Exception localException2)
      {
        Log.e("FragmentManager", "Failed dumping state", localException2);
      }
    }
    throw paramRuntimeException;
  }
  
  private void updateOnBackPressedCallbackEnabled()
  {
    Object localObject = mPendingActions;
    try
    {
      boolean bool2 = mPendingActions.isEmpty();
      boolean bool1 = true;
      if (!bool2)
      {
        mOnBackPressedCallback.setEnabled(true);
        return;
      }
      localObject = mOnBackPressedCallback;
      if ((getBackStackEntryCount() <= 0) || (!isPrimaryNavigation(mParent))) {
        bool1 = false;
      }
      ((OnBackPressedCallback)localObject).setEnabled(bool1);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (mBackStack == null) {
      mBackStack = new ArrayList();
    }
    mBackStack.add(paramBackStackRecord);
  }
  
  void addCancellationSignal(Fragment paramFragment, androidx.core.opml.CancellationSignal paramCancellationSignal)
  {
    if (mExitAnimationCancellationSignals.get(paramFragment) == null) {
      mExitAnimationCancellationSignals.put(paramFragment, new HashSet());
    }
    ((HashSet)mExitAnimationCancellationSignals.get(paramFragment)).add(paramCancellationSignal);
  }
  
  void addFragment(Fragment paramFragment)
  {
    if (isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("add: ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    makeActive(paramFragment);
    if (!mDetached)
    {
      mFragmentStore.addFragment(paramFragment);
      mRemoving = false;
      if (mView == null) {
        mHiddenChanged = false;
      }
      if (isMenuAvailable(paramFragment)) {
        mNeedMenuInvalidate = true;
      }
    }
  }
  
  public void addOnBackStackChangedListener(OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (mBackStackChangeListeners == null) {
      mBackStackChangeListeners = new ArrayList();
    }
    mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }
  
  void addRetainedFragment(Fragment paramFragment)
  {
    if (isStateSaved())
    {
      if (isLoggingEnabled(2)) {
        Log.v("FragmentManager", "Ignoring addRetainedFragment as the state is already saved");
      }
    }
    else if ((mNonConfig.addRetainedFragment(paramFragment)) && (isLoggingEnabled(2)))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Updating retained Fragments: Added ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
  }
  
  int allocBackStackIndex()
  {
    return mBackStackIndex.getAndIncrement();
  }
  
  void attachController(FragmentHostCallback paramFragmentHostCallback, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (mHost == null)
    {
      mHost = paramFragmentHostCallback;
      mContainer = paramFragmentContainer;
      mParent = paramFragment;
      if (mParent != null) {
        updateOnBackPressedCallbackEnabled();
      }
      if ((paramFragmentHostCallback instanceof OnBackPressedDispatcherOwner))
      {
        paramFragmentContainer = (OnBackPressedDispatcherOwner)paramFragmentHostCallback;
        mOnBackPressedDispatcher = ((OnBackPressedDispatcherOwner)paramFragmentContainer).getOnBackPressedDispatcher();
        if (paramFragment != null) {
          paramFragmentContainer = paramFragment;
        }
        mOnBackPressedDispatcher.addCallback(paramFragmentContainer, mOnBackPressedCallback);
      }
      if (paramFragment != null)
      {
        mNonConfig = mFragmentManager.getChildNonConfig(paramFragment);
        return;
      }
      if ((paramFragmentHostCallback instanceof ViewModelStoreOwner))
      {
        mNonConfig = FragmentManagerViewModel.getInstance(((ViewModelStoreOwner)paramFragmentHostCallback).getViewModelStore());
        return;
      }
      mNonConfig = new FragmentManagerViewModel(false);
      return;
    }
    throw new IllegalStateException("Already attached");
  }
  
  void attachFragment(Fragment paramFragment)
  {
    StringBuilder localStringBuilder;
    if (isLoggingEnabled(2))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("attach: ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    if (mDetached)
    {
      mDetached = false;
      if (!mAdded)
      {
        mFragmentStore.addFragment(paramFragment);
        if (isLoggingEnabled(2))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("add from attach: ");
          localStringBuilder.append(paramFragment);
          Log.v("FragmentManager", localStringBuilder.toString());
        }
        if (isMenuAvailable(paramFragment)) {
          mNeedMenuInvalidate = true;
        }
      }
    }
  }
  
  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }
  
  boolean checkForMenus()
  {
    Iterator localIterator = mFragmentStore.getActiveFragments().iterator();
    boolean bool1 = false;
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      boolean bool2 = bool1;
      if (localFragment != null) {
        bool2 = isMenuAvailable(localFragment);
      }
      bool1 = bool2;
      if (bool2) {
        return true;
      }
    }
    return false;
  }
  
  void completeExecute(BackStackRecord paramBackStackRecord, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramBoolean1) {
      paramBackStackRecord.executePopOps(paramBoolean3);
    } else {
      paramBackStackRecord.executeOps();
    }
    Object localObject1 = new ArrayList(1);
    Object localObject2 = new ArrayList(1);
    ((ArrayList)localObject1).add(paramBackStackRecord);
    ((ArrayList)localObject2).add(Boolean.valueOf(paramBoolean1));
    if (paramBoolean2) {
      FragmentTransition.startTransitions(this, (ArrayList)localObject1, (ArrayList)localObject2, 0, 1, true, mFragmentTransitionCallback);
    }
    if (paramBoolean3) {
      moveToState(mCurState, true);
    }
    localObject1 = mFragmentStore.getActiveFragments().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Fragment)((Iterator)localObject1).next();
      if ((localObject2 != null) && (mView != null) && (mIsNewlyAdded) && (paramBackStackRecord.interactsWith(mContainerId)))
      {
        if (mPostponedAlpha > 0.0F) {
          mView.setAlpha(mPostponedAlpha);
        }
        if (paramBoolean3)
        {
          mPostponedAlpha = 0.0F;
        }
        else
        {
          mPostponedAlpha = -1.0F;
          mIsNewlyAdded = false;
        }
      }
    }
  }
  
  void detachFragment(Fragment paramFragment)
  {
    StringBuilder localStringBuilder;
    if (isLoggingEnabled(2))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("detach: ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    if (!mDetached)
    {
      mDetached = true;
      if (mAdded)
      {
        if (isLoggingEnabled(2))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("remove from detach: ");
          localStringBuilder.append(paramFragment);
          Log.v("FragmentManager", localStringBuilder.toString());
        }
        mFragmentStore.removeFragment(paramFragment);
        if (isMenuAvailable(paramFragment)) {
          mNeedMenuInvalidate = true;
        }
        setVisibleRemovingFragment(paramFragment);
      }
    }
  }
  
  void dispatchActivityCreated()
  {
    mStateSaved = false;
    mStopped = false;
    dispatchStateChange(2);
  }
  
  void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.performConfigurationChanged(paramConfiguration);
      }
    }
  }
  
  boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (mCurState < 1) {
      return false;
    }
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem))) {
        return true;
      }
    }
    return false;
  }
  
  void dispatchCreate()
  {
    mStateSaved = false;
    mStopped = false;
    dispatchStateChange(1);
  }
  
  boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    int j = mCurState;
    int i = 0;
    if (j < 1) {
      return false;
    }
    Object localObject1 = null;
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    boolean bool = false;
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if ((localFragment != null) && (localFragment.performCreateOptionsMenu(paramMenu, paramMenuInflater)))
      {
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(localFragment);
        bool = true;
        localObject1 = localObject2;
      }
    }
    if (mCreatedMenus != null) {
      while (i < mCreatedMenus.size())
      {
        paramMenu = (Fragment)mCreatedMenus.get(i);
        if ((localObject1 == null) || (!localObject1.contains(paramMenu))) {
          paramMenu.onDestroyOptionsMenu();
        }
        i += 1;
      }
    }
    mCreatedMenus = localObject1;
    return bool;
  }
  
  void dispatchDestroy()
  {
    mDestroyed = true;
    execPendingActions(true);
    endAnimatingAwayFragments();
    dispatchStateChange(-1);
    mHost = null;
    mContainer = null;
    mParent = null;
    if (mOnBackPressedDispatcher != null)
    {
      mOnBackPressedCallback.remove();
      mOnBackPressedDispatcher = null;
    }
  }
  
  void dispatchDestroyView()
  {
    dispatchStateChange(1);
  }
  
  void dispatchLowMemory()
  {
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.performLowMemory();
      }
    }
  }
  
  void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.performMultiWindowModeChanged(paramBoolean);
      }
    }
  }
  
  boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (mCurState < 1) {
      return false;
    }
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem))) {
        return true;
      }
    }
    return false;
  }
  
  void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (mCurState < 1) {
      return;
    }
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.performOptionsMenuClosed(paramMenu);
      }
    }
  }
  
  void dispatchPause()
  {
    dispatchStateChange(3);
  }
  
  void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.performPictureInPictureModeChanged(paramBoolean);
      }
    }
  }
  
  boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    int i = mCurState;
    boolean bool = false;
    if (i < 1) {
      return false;
    }
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if ((localFragment != null) && (localFragment.performPrepareOptionsMenu(paramMenu))) {
        bool = true;
      }
    }
    return bool;
  }
  
  void dispatchPrimaryNavigationFragmentChanged()
  {
    updateOnBackPressedCallbackEnabled();
    dispatchParentPrimaryNavigationFragmentChanged(mPrimaryNav);
  }
  
  void dispatchResume()
  {
    mStateSaved = false;
    mStopped = false;
    dispatchStateChange(4);
  }
  
  void dispatchStart()
  {
    mStateSaved = false;
    mStopped = false;
    dispatchStateChange(3);
  }
  
  void dispatchStop()
  {
    mStopped = true;
    dispatchStateChange(2);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("    ");
    localObject = ((StringBuilder)localObject).toString();
    mFragmentStore.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramFileDescriptor = mCreatedMenus;
    int j = 0;
    int k;
    int i;
    if (paramFileDescriptor != null)
    {
      k = mCreatedMenus.size();
      if (k > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        i = 0;
        while (i < k)
        {
          paramFileDescriptor = (Fragment)mCreatedMenus.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(paramFileDescriptor.toString());
          i += 1;
        }
      }
    }
    if (mBackStack != null)
    {
      k = mBackStack.size();
      if (k > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        i = 0;
        while (i < k)
        {
          paramFileDescriptor = (BackStackRecord)mBackStack.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(paramFileDescriptor.toString());
          paramFileDescriptor.dump((String)localObject, paramPrintWriter);
          i += 1;
        }
      }
    }
    paramPrintWriter.print(paramString);
    paramFileDescriptor = new StringBuilder();
    paramFileDescriptor.append("Back Stack Index: ");
    paramFileDescriptor.append(mBackStackIndex.get());
    paramPrintWriter.println(paramFileDescriptor.toString());
    paramFileDescriptor = mPendingActions;
    try
    {
      k = mPendingActions.size();
      if (k > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Pending Actions:");
        i = j;
        while (i < k)
        {
          paramArrayOfString = (OpGenerator)mPendingActions.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(paramArrayOfString);
          i += 1;
        }
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("FragmentManager misc state:");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mHost=");
      paramPrintWriter.println(mHost);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mContainer=");
      paramPrintWriter.println(mContainer);
      if (mParent != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  mParent=");
        paramPrintWriter.println(mParent);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mCurState=");
      paramPrintWriter.print(mCurState);
      paramPrintWriter.print(" mStateSaved=");
      paramPrintWriter.print(mStateSaved);
      paramPrintWriter.print(" mStopped=");
      paramPrintWriter.print(mStopped);
      paramPrintWriter.print(" mDestroyed=");
      paramPrintWriter.println(mDestroyed);
      if (mNeedMenuInvalidate)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  mNeedMenuInvalidate=");
        paramPrintWriter.println(mNeedMenuInvalidate);
        return;
      }
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  void enqueueAction(OpGenerator paramOpGenerator, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      if (mHost == null)
      {
        if (mDestroyed) {
          throw new IllegalStateException("FragmentManager has been destroyed");
        }
        throw new IllegalStateException("FragmentManager has not been attached to a host.");
      }
      checkStateLoss();
    }
    ArrayList localArrayList = mPendingActions;
    try
    {
      if (mHost == null)
      {
        if (paramBoolean) {
          return;
        }
        throw new IllegalStateException("Activity has been destroyed");
      }
      mPendingActions.add(paramOpGenerator);
      scheduleCommit();
      return;
    }
    catch (Throwable paramOpGenerator)
    {
      throw paramOpGenerator;
    }
  }
  
  boolean execPendingActions(boolean paramBoolean)
  {
    ensureExecReady(paramBoolean);
    paramBoolean = false;
    while (generateOpsForPendingActions(mTmpRecords, mTmpIsPop))
    {
      mExecutingActions = true;
      try
      {
        removeRedundantOperationsAndExecute(mTmpRecords, mTmpIsPop);
        cleanupExec();
        paramBoolean = true;
      }
      catch (Throwable localThrowable)
      {
        cleanupExec();
        throw localThrowable;
      }
    }
    updateOnBackPressedCallbackEnabled();
    doPendingDeferredStart();
    mFragmentStore.burpActive();
    return paramBoolean;
  }
  
  void execSingleAction(OpGenerator paramOpGenerator, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (mHost == null) {
        return;
      }
      if (mDestroyed) {
        return;
      }
    }
    ensureExecReady(paramBoolean);
    if (paramOpGenerator.generateOps(mTmpRecords, mTmpIsPop))
    {
      mExecutingActions = true;
      try
      {
        removeRedundantOperationsAndExecute(mTmpRecords, mTmpIsPop);
        cleanupExec();
      }
      catch (Throwable paramOpGenerator)
      {
        cleanupExec();
        throw paramOpGenerator;
      }
    }
    updateOnBackPressedCallbackEnabled();
    doPendingDeferredStart();
    mFragmentStore.burpActive();
  }
  
  public boolean executePendingTransactions()
  {
    boolean bool = execPendingActions(true);
    forcePostponedTransactions();
    return bool;
  }
  
  Fragment findActiveFragment(String paramString)
  {
    return mFragmentStore.findActiveFragment(paramString);
  }
  
  public Fragment findFragmentById(int paramInt)
  {
    return mFragmentStore.findFragmentById(paramInt);
  }
  
  public Fragment findFragmentByTag(String paramString)
  {
    return mFragmentStore.findFragmentByTag(paramString);
  }
  
  Fragment findFragmentByWho(String paramString)
  {
    return mFragmentStore.findFragmentByWho(paramString);
  }
  
  int getActiveFragmentCount()
  {
    return mFragmentStore.getActiveFragmentCount();
  }
  
  List getActiveFragments()
  {
    return mFragmentStore.getActiveFragments();
  }
  
  public BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (BackStackEntry)mBackStack.get(paramInt);
  }
  
  public int getBackStackEntryCount()
  {
    if (mBackStack != null) {
      return mBackStack.size();
    }
    return 0;
  }
  
  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    paramBundle = paramBundle.getString(paramString);
    if (paramBundle == null) {
      return null;
    }
    Fragment localFragment = findActiveFragment(paramBundle);
    if (localFragment == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Fragment no longer exists for key ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": unique id ");
      localStringBuilder.append(paramBundle);
      throwException(new IllegalStateException(localStringBuilder.toString()));
    }
    return localFragment;
  }
  
  public FragmentFactory getFragmentFactory()
  {
    if (mFragmentFactory != null) {
      return mFragmentFactory;
    }
    if (mParent != null) {
      return mParent.mFragmentManager.getFragmentFactory();
    }
    return mHostFragmentFactory;
  }
  
  public List getFragments()
  {
    return mFragmentStore.getFragments();
  }
  
  LayoutInflater.Factory2 getLayoutInflaterFactory()
  {
    return mLayoutInflaterFactory;
  }
  
  FragmentLifecycleCallbacksDispatcher getLifecycleCallbacksDispatcher()
  {
    return mLifecycleCallbacksDispatcher;
  }
  
  Fragment getParent()
  {
    return mParent;
  }
  
  public Fragment getPrimaryNavigationFragment()
  {
    return mPrimaryNav;
  }
  
  ViewModelStore getViewModelStore(Fragment paramFragment)
  {
    return mNonConfig.getViewModelStore(paramFragment);
  }
  
  void handleOnBackPressed()
  {
    execPendingActions(true);
    if (mOnBackPressedCallback.isEnabled())
    {
      popBackStackImmediate();
      return;
    }
    mOnBackPressedDispatcher.onBackPressed();
  }
  
  void hideFragment(Fragment paramFragment)
  {
    if (isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("hide: ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    if (!mHidden)
    {
      mHidden = true;
      mHiddenChanged = (true ^ mHiddenChanged);
      setVisibleRemovingFragment(paramFragment);
    }
  }
  
  public boolean isDestroyed()
  {
    return mDestroyed;
  }
  
  boolean isPrimaryNavigation(Fragment paramFragment)
  {
    if (paramFragment == null) {
      return true;
    }
    FragmentManager localFragmentManager = mFragmentManager;
    return (paramFragment.equals(localFragmentManager.getPrimaryNavigationFragment())) && (isPrimaryNavigation(mParent));
  }
  
  boolean isStateAtLeast(int paramInt)
  {
    return mCurState >= paramInt;
  }
  
  public boolean isStateSaved()
  {
    return (mStateSaved) || (mStopped);
  }
  
  void makeActive(Fragment paramFragment)
  {
    if (mFragmentStore.containsActiveFragment(mWho)) {
      return;
    }
    Object localObject = new FragmentStateManager(mLifecycleCallbacksDispatcher, paramFragment);
    ((FragmentStateManager)localObject).restoreState(mHost.getContext().getClassLoader());
    mFragmentStore.makeActive((FragmentStateManager)localObject);
    if (mRetainInstanceChangedWhileDetached)
    {
      if (mRetainInstance) {
        addRetainedFragment(paramFragment);
      } else {
        removeRetainedFragment(paramFragment);
      }
      mRetainInstanceChangedWhileDetached = false;
    }
    ((FragmentStateManager)localObject).setFragmentManagerState(mCurState);
    if (isLoggingEnabled(2))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Added fragment to active set ");
      ((StringBuilder)localObject).append(paramFragment);
      Log.v("FragmentManager", ((StringBuilder)localObject).toString());
    }
  }
  
  void moveFragmentToExpectedState(Fragment paramFragment)
  {
    Object localObject;
    if (!mFragmentStore.containsActiveFragment(mWho))
    {
      if (isLoggingEnabled(3))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Ignoring moving ");
        ((StringBuilder)localObject).append(paramFragment);
        ((StringBuilder)localObject).append(" to state ");
        ((StringBuilder)localObject).append(mCurState);
        ((StringBuilder)localObject).append("since it is not added to ");
        ((StringBuilder)localObject).append(this);
        Log.d("FragmentManager", ((StringBuilder)localObject).toString());
      }
    }
    else
    {
      moveToState(paramFragment);
      if (mView != null)
      {
        localObject = mFragmentStore.findFragmentUnder(paramFragment);
        if (localObject != null)
        {
          localObject = mView;
          ViewGroup localViewGroup = mContainer;
          int i = localViewGroup.indexOfChild((View)localObject);
          int j = localViewGroup.indexOfChild(mView);
          if (j < i)
          {
            localViewGroup.removeViewAt(j);
            localViewGroup.addView(mView, i);
          }
        }
        if ((mIsNewlyAdded) && (mContainer != null))
        {
          if (mPostponedAlpha > 0.0F) {
            mView.setAlpha(mPostponedAlpha);
          }
          mPostponedAlpha = 0.0F;
          mIsNewlyAdded = false;
          localObject = FragmentAnim.loadAnimation(mHost.getContext(), mContainer, paramFragment, true);
          if (localObject != null) {
            if (animation != null)
            {
              mView.startAnimation(animation);
            }
            else
            {
              animator.setTarget(mView);
              animator.start();
            }
          }
        }
      }
      if (mHiddenChanged) {
        completeShowHideFragment(paramFragment);
      }
    }
  }
  
  void moveToState(int paramInt, boolean paramBoolean)
  {
    if ((mHost == null) && (paramInt != -1)) {
      throw new IllegalStateException("No activity");
    }
    if ((!paramBoolean) && (paramInt == mCurState)) {
      return;
    }
    mCurState = paramInt;
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext()) {
      moveFragmentToExpectedState((Fragment)localIterator.next());
    }
    localIterator = mFragmentStore.getActiveFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if ((localFragment != null) && (!mIsNewlyAdded)) {
        moveFragmentToExpectedState(localFragment);
      }
    }
    startPendingDeferredFragments();
    if ((mNeedMenuInvalidate) && (mHost != null) && (mCurState == 4))
    {
      mHost.onSupportInvalidateOptionsMenu();
      mNeedMenuInvalidate = false;
    }
  }
  
  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, mCurState);
  }
  
  void moveToState(Fragment paramFragment, int paramInt)
  {
    Object localObject2 = mFragmentStore.getFragmentStateManager(mWho);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new FragmentStateManager(mLifecycleCallbacksDispatcher, paramFragment);
      ((FragmentStateManager)localObject1).setFragmentManagerState(1);
    }
    int k = Math.min(paramInt, ((FragmentStateManager)localObject1).computeMaxState());
    paramInt = k;
    int i = mState;
    ViewGroup localViewGroup = null;
    int j;
    if (i <= k)
    {
      if ((mState < k) && (!mExitAnimationCancellationSignals.isEmpty())) {
        cancelExitAnimation(paramFragment);
      }
      switch (mState)
      {
      default: 
        j = paramInt;
        break;
      case -1: 
        if (k > -1)
        {
          if (isLoggingEnabled(3))
          {
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("moveto ATTACHED: ");
            ((StringBuilder)localObject2).append(paramFragment);
            Log.d("FragmentManager", ((StringBuilder)localObject2).toString());
          }
          if (mTarget != null) {
            if (mTarget.equals(findActiveFragment(mTarget.mWho)))
            {
              if (mTarget.mState < 1) {
                moveToState(mTarget, 1);
              }
              mTargetWho = mTarget.mWho;
              mTarget = null;
            }
            else
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("Fragment ");
              ((StringBuilder)localObject1).append(paramFragment);
              ((StringBuilder)localObject1).append(" declared target fragment ");
              ((StringBuilder)localObject1).append(mTarget);
              ((StringBuilder)localObject1).append(" that does not belong to this FragmentManager!");
              throw new IllegalStateException(((StringBuilder)localObject1).toString());
            }
          }
          if (mTargetWho != null)
          {
            localObject2 = findActiveFragment(mTargetWho);
            if (localObject2 != null)
            {
              if (mState < 1) {
                moveToState((Fragment)localObject2, 1);
              }
            }
            else
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("Fragment ");
              ((StringBuilder)localObject1).append(paramFragment);
              ((StringBuilder)localObject1).append(" declared target fragment ");
              ((StringBuilder)localObject1).append(mTargetWho);
              ((StringBuilder)localObject1).append(" that does not belong to this FragmentManager!");
              throw new IllegalStateException(((StringBuilder)localObject1).toString());
            }
          }
          ((FragmentStateManager)localObject1).attach(mHost, this, mParent);
        }
      case 0: 
        if (k > 0) {
          ((FragmentStateManager)localObject1).create();
        }
      case 1: 
        if (k > -1) {
          ((FragmentStateManager)localObject1).ensureInflatedView();
        }
        if (k > 1)
        {
          ((FragmentStateManager)localObject1).createView(mContainer);
          ((FragmentStateManager)localObject1).activityCreated();
          ((FragmentStateManager)localObject1).restoreViewState();
        }
      case 2: 
        if (k > 2) {
          ((FragmentStateManager)localObject1).start();
        }
        break;
      }
      j = paramInt;
      if (k > 3)
      {
        ((FragmentStateManager)localObject1).resume();
        j = paramInt;
      }
    }
    else
    {
      j = paramInt;
      if (mState > k)
      {
        int m = mState;
        j = 0;
        i = paramInt;
        switch (m)
        {
        default: 
          j = paramInt;
          break;
        case 4: 
          if (k < 4) {
            ((FragmentStateManager)localObject1).pause();
          }
        case 3: 
          if (k < 3) {
            ((FragmentStateManager)localObject1).stop();
          }
        case 2: 
          if (k < 2)
          {
            if (isLoggingEnabled(3))
            {
              localObject2 = new StringBuilder();
              ((StringBuilder)localObject2).append("movefrom ACTIVITY_CREATED: ");
              ((StringBuilder)localObject2).append(paramFragment);
              Log.d("FragmentManager", ((StringBuilder)localObject2).toString());
            }
            if ((mView != null) && (mHost.onShouldSaveFragmentState(paramFragment)) && (mSavedViewState == null)) {
              ((FragmentStateManager)localObject1).saveViewState();
            }
            if ((mView != null) && (mContainer != null))
            {
              mContainer.endViewTransition(mView);
              mView.clearAnimation();
              if (!paramFragment.isRemovingParent())
              {
                localObject2 = localViewGroup;
                if (mCurState > -1)
                {
                  localObject2 = localViewGroup;
                  if (!mDestroyed)
                  {
                    localObject2 = localViewGroup;
                    if (mView.getVisibility() == 0)
                    {
                      localObject2 = localViewGroup;
                      if (mPostponedAlpha >= 0.0F) {
                        localObject2 = FragmentAnim.loadAnimation(mHost.getContext(), mContainer, paramFragment, false);
                      }
                    }
                  }
                }
                mPostponedAlpha = 0.0F;
                localViewGroup = mContainer;
                View localView = mView;
                if (localObject2 != null)
                {
                  paramFragment.setStateAfterAnimating(k);
                  FragmentAnim.animateRemoveFragment(paramFragment, (FragmentAnim.AnimationOrAnimator)localObject2, mFragmentTransitionCallback);
                }
                localViewGroup.removeView(localView);
                if (localViewGroup != mContainer) {
                  return;
                }
              }
            }
            if (mExitAnimationCancellationSignals.get(paramFragment) == null) {
              destroyFragmentView(paramFragment);
            } else {
              paramFragment.setStateAfterAnimating(k);
            }
          }
        case 1: 
          i = paramInt;
          if (k < 1)
          {
            i = j;
            if (mRemoving)
            {
              i = j;
              if (!paramFragment.isInBackStack()) {
                i = 1;
              }
            }
            if ((i == 0) && (!mNonConfig.shouldDestroy(paramFragment)))
            {
              if (mTargetWho != null)
              {
                localObject2 = findActiveFragment(mTargetWho);
                if ((localObject2 != null) && (((Fragment)localObject2).getRetainInstance())) {
                  mTarget = ((Fragment)localObject2);
                }
              }
            }
            else {
              makeInactive((FragmentStateManager)localObject1);
            }
            if (mExitAnimationCancellationSignals.get(paramFragment) != null)
            {
              paramFragment.setStateAfterAnimating(k);
              i = 1;
            }
            else
            {
              ((FragmentStateManager)localObject1).destroy(mHost, mNonConfig);
              i = paramInt;
            }
          }
          break;
        }
        j = i;
        if (i < 0)
        {
          ((FragmentStateManager)localObject1).detach(mNonConfig);
          j = i;
        }
      }
    }
    if (mState != j)
    {
      if (isLoggingEnabled(3))
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("moveToState: Fragment state for ");
        ((StringBuilder)localObject1).append(paramFragment);
        ((StringBuilder)localObject1).append(" not updated inline; expected state ");
        ((StringBuilder)localObject1).append(j);
        ((StringBuilder)localObject1).append(" found ");
        ((StringBuilder)localObject1).append(mState);
        Log.d("FragmentManager", ((StringBuilder)localObject1).toString());
      }
      mState = j;
    }
  }
  
  void noteStateNotSaved()
  {
    if (mHost == null) {
      return;
    }
    mStateSaved = false;
    mStopped = false;
    Iterator localIterator = mFragmentStore.getFragments().iterator();
    while (localIterator.hasNext())
    {
      Fragment localFragment = (Fragment)localIterator.next();
      if (localFragment != null) {
        localFragment.noteStateNotSaved();
      }
    }
  }
  
  public FragmentTransaction openTransaction()
  {
    return beginTransaction();
  }
  
  void performPendingDeferredStart(Fragment paramFragment)
  {
    if (mDeferStart)
    {
      if (mExecutingActions)
      {
        mHavePendingDeferredStart = true;
        return;
      }
      mDeferStart = false;
      moveToState(paramFragment, mCurState);
    }
  }
  
  public void popBackStack()
  {
    enqueueAction(new PopBackStackState(null, -1, 0), false);
  }
  
  public void popBackStack(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= 0)
    {
      enqueueAction(new PopBackStackState(null, paramInt1, paramInt2), false);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Bad id: ");
    localStringBuilder.append(paramInt1);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void popBackStack(String paramString, int paramInt)
  {
    enqueueAction(new PopBackStackState(paramString, -1, paramInt), false);
  }
  
  public boolean popBackStackImmediate()
  {
    return popBackStackImmediate(null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= 0) {
      return popBackStackImmediate(null, paramInt1, paramInt2);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Bad id: ");
    localStringBuilder.append(paramInt1);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    return popBackStackImmediate(paramString, -1, paramInt);
  }
  
  boolean popBackStackState(ArrayList paramArrayList1, ArrayList paramArrayList2, String paramString, int paramInt1, int paramInt2)
  {
    if (mBackStack == null) {
      return false;
    }
    if ((paramString == null) && (paramInt1 < 0) && ((paramInt2 & 0x1) == 0))
    {
      paramInt1 = mBackStack.size() - 1;
      if (paramInt1 < 0) {
        return false;
      }
      paramArrayList1.add(mBackStack.remove(paramInt1));
      paramArrayList2.add(Boolean.valueOf(true));
      return true;
    }
    int i;
    if ((paramString == null) && (paramInt1 < 0))
    {
      i = -1;
    }
    else
    {
      int j = mBackStack.size() - 1;
      BackStackRecord localBackStackRecord;
      while (j >= 0)
      {
        localBackStackRecord = (BackStackRecord)mBackStack.get(j);
        if (((paramString != null) && (paramString.equals(localBackStackRecord.getName()))) || ((paramInt1 >= 0) && (paramInt1 == mIndex))) {
          break;
        }
        j -= 1;
      }
      if (j < 0) {
        return false;
      }
      i = j;
      if ((paramInt2 & 0x1) != 0)
      {
        paramInt2 = j - 1;
        for (;;)
        {
          i = paramInt2;
          if (paramInt2 < 0) {
            break;
          }
          localBackStackRecord = (BackStackRecord)mBackStack.get(paramInt2);
          if ((paramString == null) || (!paramString.equals(localBackStackRecord.getName())))
          {
            i = paramInt2;
            if (paramInt1 < 0) {
              break;
            }
            i = paramInt2;
            if (paramInt1 != mIndex) {
              break;
            }
          }
          paramInt2 -= 1;
        }
      }
    }
    if (i == mBackStack.size() - 1) {
      return false;
    }
    paramInt1 = mBackStack.size() - 1;
    while (paramInt1 > i)
    {
      paramArrayList1.add(mBackStack.remove(paramInt1));
      paramArrayList2.add(Boolean.valueOf(true));
      paramInt1 -= 1;
    }
    return true;
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (mFragmentManager != this)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Fragment ");
      localStringBuilder.append(paramFragment);
      localStringBuilder.append(" is not currently in the FragmentManager");
      throwException(new IllegalStateException(localStringBuilder.toString()));
    }
    paramBundle.putString(paramString, mWho);
  }
  
  public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks, boolean paramBoolean)
  {
    mLifecycleCallbacksDispatcher.registerFragmentLifecycleCallbacks(paramFragmentLifecycleCallbacks, paramBoolean);
  }
  
  void removeCancellationSignal(Fragment paramFragment, androidx.core.opml.CancellationSignal paramCancellationSignal)
  {
    HashSet localHashSet = (HashSet)mExitAnimationCancellationSignals.get(paramFragment);
    if ((localHashSet != null) && (localHashSet.remove(paramCancellationSignal)) && (localHashSet.isEmpty()))
    {
      mExitAnimationCancellationSignals.remove(paramFragment);
      if (mState < 3)
      {
        destroyFragmentView(paramFragment);
        moveToState(paramFragment, paramFragment.getStateAfterAnimating());
      }
    }
  }
  
  void removeFragment(Fragment paramFragment)
  {
    if (isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("remove: ");
      localStringBuilder.append(paramFragment);
      localStringBuilder.append(" nesting=");
      localStringBuilder.append(mBackStackNesting);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    boolean bool = paramFragment.isInBackStack();
    if ((!mDetached) || ((bool ^ true)))
    {
      mFragmentStore.removeFragment(paramFragment);
      if (isMenuAvailable(paramFragment)) {
        mNeedMenuInvalidate = true;
      }
      mRemoving = true;
      setVisibleRemovingFragment(paramFragment);
    }
  }
  
  public void removeOnBackStackChangedListener(OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (mBackStackChangeListeners != null) {
      mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
    }
  }
  
  void removeRetainedFragment(Fragment paramFragment)
  {
    if (isStateSaved())
    {
      if (isLoggingEnabled(2)) {
        Log.v("FragmentManager", "Ignoring removeRetainedFragment as the state is already saved");
      }
    }
    else if ((mNonConfig.removeRetainedFragment(paramFragment)) && (isLoggingEnabled(2)))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Updating retained Fragments: Removed ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
  }
  
  void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if ((mHost instanceof ViewModelStoreOwner)) {
      throwException(new IllegalStateException("You must use restoreSaveState when your FragmentHostCallback implements ViewModelStoreOwner"));
    }
    mNonConfig.restoreFromSnapshot(paramFragmentManagerNonConfig);
    restoreSaveState(paramParcelable);
  }
  
  void restoreSaveState(Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      return;
    }
    FragmentManagerState localFragmentManagerState = (FragmentManagerState)paramParcelable;
    if (mActive == null) {
      return;
    }
    mFragmentStore.resetActiveFragments();
    Object localObject1 = mActive.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      paramParcelable = (FragmentState)((Iterator)localObject1).next();
      if (paramParcelable != null)
      {
        localObject2 = mNonConfig.findRetainedFragmentByWho(mWho);
        StringBuilder localStringBuilder;
        if (localObject2 != null)
        {
          if (isLoggingEnabled(2))
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("restoreSaveState: re-attaching retained ");
            localStringBuilder.append(localObject2);
            Log.v("FragmentManager", localStringBuilder.toString());
          }
          paramParcelable = new FragmentStateManager(mLifecycleCallbacksDispatcher, (Fragment)localObject2, paramParcelable);
        }
        else
        {
          paramParcelable = new FragmentStateManager(mLifecycleCallbacksDispatcher, mHost.getContext().getClassLoader(), getFragmentFactory(), paramParcelable);
        }
        localObject2 = paramParcelable.getFragment();
        mFragmentManager = this;
        if (isLoggingEnabled(2))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("restoreSaveState: active (");
          localStringBuilder.append(mWho);
          localStringBuilder.append("): ");
          localStringBuilder.append(localObject2);
          Log.v("FragmentManager", localStringBuilder.toString());
        }
        paramParcelable.restoreState(mHost.getContext().getClassLoader());
        mFragmentStore.makeActive(paramParcelable);
        paramParcelable.setFragmentManagerState(mCurState);
      }
    }
    paramParcelable = mNonConfig.getRetainedFragments().iterator();
    while (paramParcelable.hasNext())
    {
      localObject1 = (Fragment)paramParcelable.next();
      if (!mFragmentStore.containsActiveFragment(mWho))
      {
        if (isLoggingEnabled(2))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Discarding retained Fragment ");
          ((StringBuilder)localObject2).append(localObject1);
          ((StringBuilder)localObject2).append(" that was not found in the set of active Fragments ");
          ((StringBuilder)localObject2).append(mActive);
          Log.v("FragmentManager", ((StringBuilder)localObject2).toString());
        }
        moveToState((Fragment)localObject1, 1);
        mRemoving = true;
        moveToState((Fragment)localObject1, -1);
      }
    }
    mFragmentStore.restoreAddedFragments(mAdded);
    if (mBackStack != null)
    {
      mBackStack = new ArrayList(mBackStack.length);
      int i = 0;
      while (i < mBackStack.length)
      {
        paramParcelable = mBackStack[i].instantiate(this);
        if (isLoggingEnabled(2))
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("restoreAllState: back stack #");
          ((StringBuilder)localObject1).append(i);
          ((StringBuilder)localObject1).append(" (index ");
          ((StringBuilder)localObject1).append(mIndex);
          ((StringBuilder)localObject1).append("): ");
          ((StringBuilder)localObject1).append(paramParcelable);
          Log.v("FragmentManager", ((StringBuilder)localObject1).toString());
          localObject1 = new PrintWriter(new LogWriter("FragmentManager"));
          paramParcelable.dump("  ", (PrintWriter)localObject1, false);
          ((PrintWriter)localObject1).close();
        }
        mBackStack.add(paramParcelable);
        i += 1;
      }
    }
    mBackStack = null;
    mBackStackIndex.set(mBackStackIndex);
    if (mPrimaryNavActiveWho != null)
    {
      mPrimaryNav = findActiveFragment(mPrimaryNavActiveWho);
      dispatchParentPrimaryNavigationFragmentChanged(mPrimaryNav);
    }
  }
  
  FragmentManagerNonConfig retainNonConfig()
  {
    if ((mHost instanceof ViewModelStoreOwner)) {
      throwException(new IllegalStateException("You cannot use retainNonConfig when your FragmentHostCallback implements ViewModelStoreOwner."));
    }
    return mNonConfig.getSnapshot();
  }
  
  Parcelable saveAllState()
  {
    forcePostponedTransactions();
    endAnimatingAwayFragments();
    execPendingActions(true);
    mStateSaved = true;
    ArrayList localArrayList1 = mFragmentStore.saveActiveFragments();
    boolean bool = localArrayList1.isEmpty();
    Object localObject2 = null;
    if (bool)
    {
      if (isLoggingEnabled(2))
      {
        Log.v("FragmentManager", "saveAllState: no fragments!");
        return null;
      }
    }
    else
    {
      ArrayList localArrayList2 = mFragmentStore.saveAddedFragments();
      Object localObject1 = localObject2;
      if (mBackStack != null)
      {
        int j = mBackStack.size();
        localObject1 = localObject2;
        if (j > 0)
        {
          localObject2 = new BackStackState[j];
          int i = 0;
          for (;;)
          {
            localObject1 = localObject2;
            if (i >= j) {
              break;
            }
            localObject2[i] = new BackStackState((BackStackRecord)mBackStack.get(i));
            if (isLoggingEnabled(2))
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("saveAllState: adding back stack #");
              ((StringBuilder)localObject1).append(i);
              ((StringBuilder)localObject1).append(": ");
              ((StringBuilder)localObject1).append(mBackStack.get(i));
              Log.v("FragmentManager", ((StringBuilder)localObject1).toString());
            }
            i += 1;
          }
        }
      }
      localObject2 = new FragmentManagerState();
      mActive = localArrayList1;
      mAdded = localArrayList2;
      mBackStack = ((BackStackState[])localObject1);
      mBackStackIndex = mBackStackIndex.get();
      if (mPrimaryNav == null) {
        break label273;
      }
      mPrimaryNavActiveWho = mPrimaryNav.mWho;
      return localObject2;
    }
    return null;
    label273:
    return localObject2;
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    FragmentStateManager localFragmentStateManager = mFragmentStore.getFragmentStateManager(mWho);
    if ((localFragmentStateManager == null) || (!localFragmentStateManager.getFragment().equals(paramFragment)))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Fragment ");
      localStringBuilder.append(paramFragment);
      localStringBuilder.append(" is not currently in the FragmentManager");
      throwException(new IllegalStateException(localStringBuilder.toString()));
    }
    return localFragmentStateManager.saveInstanceState();
  }
  
  void scheduleCommit()
  {
    ArrayList localArrayList1 = mPendingActions;
    for (;;)
    {
      int j;
      try
      {
        ArrayList localArrayList2 = mPostponedTransactions;
        j = 0;
        if ((localArrayList2 == null) || (mPostponedTransactions.isEmpty())) {
          break label94;
        }
        i = 1;
        if (mPendingActions.size() != 1) {
          break label99;
        }
        j = 1;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      mHost.getHandler().removeCallbacks(mExecCommit);
      mHost.getHandler().post(mExecCommit);
      updateOnBackPressedCallbackEnabled();
      return;
      label94:
      int i = 0;
      continue;
      label99:
      if (i == 0) {
        if (j == 0) {}
      }
    }
  }
  
  void setExitAnimationOrder(Fragment paramFragment, boolean paramBoolean)
  {
    paramFragment = getFragmentContainer(paramFragment);
    if ((paramFragment != null) && ((paramFragment instanceof FragmentContainerView))) {
      ((FragmentContainerView)paramFragment).setDrawDisappearingViewsLast(paramBoolean ^ true);
    }
  }
  
  public void setFragmentFactory(FragmentFactory paramFragmentFactory)
  {
    mFragmentFactory = paramFragmentFactory;
  }
  
  void setMaxLifecycle(Fragment paramFragment, Lifecycle.State paramState)
  {
    if ((paramFragment.equals(findActiveFragment(mWho))) && ((mHost == null) || (mFragmentManager == this)))
    {
      mMaxState = paramState;
      return;
    }
    paramState = new StringBuilder();
    paramState.append("Fragment ");
    paramState.append(paramFragment);
    paramState.append(" is not an active fragment of FragmentManager ");
    paramState.append(this);
    throw new IllegalArgumentException(paramState.toString());
  }
  
  void setPrimaryNavigationFragment(Fragment paramFragment)
  {
    if ((paramFragment != null) && ((!paramFragment.equals(findActiveFragment(mWho))) || ((mHost != null) && (mFragmentManager != this))))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Fragment ");
      ((StringBuilder)localObject).append(paramFragment);
      ((StringBuilder)localObject).append(" is not an active fragment of FragmentManager ");
      ((StringBuilder)localObject).append(this);
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    Object localObject = mPrimaryNav;
    mPrimaryNav = paramFragment;
    dispatchParentPrimaryNavigationFragmentChanged((Fragment)localObject);
    dispatchParentPrimaryNavigationFragmentChanged(mPrimaryNav);
  }
  
  void showFragment(Fragment paramFragment)
  {
    if (isLoggingEnabled(2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("show: ");
      localStringBuilder.append(paramFragment);
      Log.v("FragmentManager", localStringBuilder.toString());
    }
    if (mHidden)
    {
      mHidden = false;
      mHiddenChanged ^= true;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (mParent != null)
    {
      localStringBuilder.append(mParent.getClass().getSimpleName());
      localStringBuilder.append("{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(mParent)));
      localStringBuilder.append("}");
    }
    else if (mHost != null)
    {
      localStringBuilder.append(mHost.getClass().getSimpleName());
      localStringBuilder.append("{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(mHost)));
      localStringBuilder.append("}");
    }
    else
    {
      localStringBuilder.append("null");
    }
    localStringBuilder.append("}}");
    return localStringBuilder.toString();
  }
  
  public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks)
  {
    mLifecycleCallbacksDispatcher.unregisterFragmentLifecycleCallbacks(paramFragmentLifecycleCallbacks);
  }
  
  public abstract interface BackStackEntry
  {
    public abstract CharSequence getBreadCrumbShortTitle();
    
    public abstract int getBreadCrumbShortTitleRes();
    
    public abstract CharSequence getBreadCrumbTitle();
    
    public abstract int getBreadCrumbTitleRes();
    
    public abstract int getId();
    
    public abstract String getName();
  }
  
  public abstract class FragmentLifecycleCallbacks
  {
    public FragmentLifecycleCallbacks() {}
    
    public void onFragmentActivityCreated(FragmentManager paramFragmentManager, Fragment paramFragment, Bundle paramBundle) {}
    
    public void onFragmentAttached(FragmentManager paramFragmentManager, Fragment paramFragment, Context paramContext) {}
    
    public void onFragmentCreated(FragmentManager paramFragmentManager, Fragment paramFragment, Bundle paramBundle) {}
    
    public void onFragmentDestroyed(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentDetached(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentPaused(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentPreAttached(FragmentManager paramFragmentManager, Fragment paramFragment, Context paramContext) {}
    
    public void onFragmentPreCreated(FragmentManager paramFragmentManager, Fragment paramFragment, Bundle paramBundle) {}
    
    public void onFragmentResumed(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentSaveInstanceState(FragmentManager paramFragmentManager, Fragment paramFragment, Bundle paramBundle) {}
    
    public void onFragmentStarted(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentStopped(FragmentManager paramFragmentManager, Fragment paramFragment) {}
    
    public void onFragmentViewCreated(FragmentManager paramFragmentManager, Fragment paramFragment, View paramView, Bundle paramBundle) {}
    
    public void onFragmentViewDestroyed(FragmentManager paramFragmentManager, Fragment paramFragment) {}
  }
  
  public abstract interface OnBackStackChangedListener
  {
    public abstract void onBackStackChanged();
  }
  
  abstract interface OpGenerator
  {
    public abstract boolean generateOps(ArrayList paramArrayList1, ArrayList paramArrayList2);
  }
  
  class PopBackStackState
    implements FragmentManager.OpGenerator
  {
    final int mFlags;
    final int mFolder;
    final String mName;
    
    PopBackStackState(String paramString, int paramInt1, int paramInt2)
    {
      mName = paramString;
      mFolder = paramInt1;
      mFlags = paramInt2;
    }
    
    public boolean generateOps(ArrayList paramArrayList1, ArrayList paramArrayList2)
    {
      if ((mPrimaryNav != null) && (mFolder < 0) && (mName == null) && (mPrimaryNav.getChildFragmentManager().popBackStackImmediate())) {
        return false;
      }
      return popBackStackState(paramArrayList1, paramArrayList2, mName, mFolder, mFlags);
    }
  }
  
  class StartEnterTransitionListener
    implements Fragment.OnStartEnterTransitionListener
  {
    final boolean mIsBack;
    private int mNumPostponed;
    
    StartEnterTransitionListener(boolean paramBoolean)
    {
      mIsBack = paramBoolean;
    }
    
    void cancelTransaction()
    {
      mManager.completeExecute(FragmentManager.this, mIsBack, false, false);
    }
    
    void completeTransaction()
    {
      int i;
      if (mNumPostponed > 0) {
        i = 1;
      } else {
        i = 0;
      }
      Iterator localIterator = mManager.getFragments().iterator();
      while (localIterator.hasNext())
      {
        Fragment localFragment = (Fragment)localIterator.next();
        localFragment.setOnStartEnterTransitionListener(null);
        if ((i != 0) && (localFragment.isPostponed())) {
          localFragment.startPostponedEnterTransition();
        }
      }
      mManager.completeExecute(FragmentManager.this, mIsBack, i ^ 0x1, true);
    }
    
    public boolean isReady()
    {
      return mNumPostponed == 0;
    }
    
    public void onStartEnterTransition()
    {
      mNumPostponed -= 1;
      if (mNumPostponed != 0) {
        return;
      }
      mManager.scheduleCommit();
    }
    
    public void startListening()
    {
      mNumPostponed += 1;
    }
  }
}
