package androidx.fragment.package_5;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.R.id;
import androidx.lifecycle.ViewModelStoreOwner;

class FragmentStateManager
{
  private static final String TAG = "FragmentManager";
  private static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  private static final String TARGET_STATE_TAG = "android:target_state";
  private static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  private static final String VIEW_STATE_TAG = "android:view_state";
  private final FragmentLifecycleCallbacksDispatcher mDispatcher;
  @NonNull
  private final Fragment mFragment;
  private int mFragmentManagerState = -1;
  
  FragmentStateManager(FragmentLifecycleCallbacksDispatcher paramFragmentLifecycleCallbacksDispatcher, Fragment paramFragment)
  {
    mDispatcher = paramFragmentLifecycleCallbacksDispatcher;
    mFragment = paramFragment;
  }
  
  FragmentStateManager(FragmentLifecycleCallbacksDispatcher paramFragmentLifecycleCallbacksDispatcher, Fragment paramFragment, FragmentState paramFragmentState)
  {
    mDispatcher = paramFragmentLifecycleCallbacksDispatcher;
    mFragment = paramFragment;
    mFragment.mSavedViewState = null;
    mFragment.mBackStackNesting = 0;
    mFragment.mInLayout = false;
    mFragment.mAdded = false;
    paramFragment = mFragment;
    if (mFragment.mTarget != null) {
      paramFragmentLifecycleCallbacksDispatcher = mFragment.mTarget.mWho;
    } else {
      paramFragmentLifecycleCallbacksDispatcher = null;
    }
    mTargetWho = paramFragmentLifecycleCallbacksDispatcher;
    mFragment.mTarget = null;
    if (mSavedFragmentState != null)
    {
      mFragment.mSavedFragmentState = mSavedFragmentState;
      return;
    }
    mFragment.mSavedFragmentState = new Bundle();
  }
  
  FragmentStateManager(FragmentLifecycleCallbacksDispatcher paramFragmentLifecycleCallbacksDispatcher, ClassLoader paramClassLoader, FragmentFactory paramFragmentFactory, FragmentState paramFragmentState)
  {
    mDispatcher = paramFragmentLifecycleCallbacksDispatcher;
    mFragment = paramFragmentFactory.instantiate(paramClassLoader, mClassName);
    if (mArguments != null) {
      mArguments.setClassLoader(paramClassLoader);
    }
    mFragment.setArguments(mArguments);
    mFragment.mWho = mWho;
    mFragment.mFromLayout = mFromLayout;
    mFragment.mRestored = true;
    mFragment.mFragmentId = mFragmentId;
    mFragment.mContainerId = mContainerId;
    mFragment.mTag = mTag;
    mFragment.mRetainInstance = mRetainInstance;
    mFragment.mRemoving = mRemoving;
    mFragment.mDetached = mDetached;
    mFragment.mHidden = mHidden;
    mFragment.mMaxState = androidx.lifecycle.Lifecycle.State.values()[mMaxLifecycleState];
    if (mSavedFragmentState != null) {
      mFragment.mSavedFragmentState = mSavedFragmentState;
    } else {
      mFragment.mSavedFragmentState = new Bundle();
    }
    if (FragmentManager.isLoggingEnabled(2))
    {
      paramFragmentLifecycleCallbacksDispatcher = new StringBuilder();
      paramFragmentLifecycleCallbacksDispatcher.append("Instantiated fragment ");
      paramFragmentLifecycleCallbacksDispatcher.append(mFragment);
      Log.v("FragmentManager", paramFragmentLifecycleCallbacksDispatcher.toString());
    }
  }
  
  private Bundle saveBasicState()
  {
    Object localObject2 = new Bundle();
    mFragment.performSaveInstanceState((Bundle)localObject2);
    mDispatcher.dispatchOnFragmentSaveInstanceState(mFragment, (Bundle)localObject2, false);
    Object localObject1 = localObject2;
    if (((BaseBundle)localObject2).isEmpty()) {
      localObject1 = null;
    }
    if (mFragment.mView != null) {
      saveViewState();
    }
    localObject2 = localObject1;
    if (mFragment.mSavedViewState != null)
    {
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = new Bundle();
      }
      ((Bundle)localObject2).putSparseParcelableArray("android:view_state", mFragment.mSavedViewState);
    }
    localObject1 = localObject2;
    if (!mFragment.mUserVisibleHint)
    {
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Bundle();
      }
      ((Bundle)localObject1).putBoolean("android:user_visible_hint", mFragment.mUserVisibleHint);
    }
    return localObject1;
  }
  
  void activityCreated()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("moveto ACTIVITY_CREATED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    mFragment.performActivityCreated(mFragment.mSavedFragmentState);
    mDispatcher.dispatchOnFragmentActivityCreated(mFragment, mFragment.mSavedFragmentState, false);
  }
  
  void attach(FragmentHostCallback paramFragmentHostCallback, FragmentManager paramFragmentManager, Fragment paramFragment)
  {
    mFragment.mHost = paramFragmentHostCallback;
    mFragment.mParentFragment = paramFragment;
    mFragment.mFragmentManager = paramFragmentManager;
    mDispatcher.dispatchOnFragmentPreAttached(mFragment, paramFragmentHostCallback.getContext(), false);
    mFragment.performAttach();
    if (mFragment.mParentFragment == null) {
      paramFragmentHostCallback.onAttachFragment(mFragment);
    } else {
      mFragment.mParentFragment.onAttachFragment(mFragment);
    }
    mDispatcher.dispatchOnFragmentAttached(mFragment, paramFragmentHostCallback.getContext(), false);
  }
  
  int computeMaxState()
  {
    int j = mFragmentManagerState;
    int i = j;
    if (mFragment.mFromLayout) {
      if (mFragment.mInLayout) {
        i = Math.max(mFragmentManagerState, 1);
      } else if (mFragmentManagerState < 2) {
        i = Math.min(j, mFragment.mState);
      } else {
        i = Math.min(j, 1);
      }
    }
    j = i;
    if (!mFragment.mAdded) {
      j = Math.min(i, 1);
    }
    i = j;
    if (mFragment.mRemoving) {
      if (mFragment.isInBackStack()) {
        i = Math.min(j, 1);
      } else {
        i = Math.min(j, -1);
      }
    }
    j = i;
    if (mFragment.mDeferStart)
    {
      j = i;
      if (mFragment.mState < 3) {
        j = Math.min(i, 2);
      }
    }
    i = j;
    switch (1.$SwitchMap$androidx$lifecycle$Lifecycle$State[mFragment.mMaxState.ordinal()])
    {
    default: 
      return Math.min(j, -1);
    case 3: 
      return Math.min(j, 1);
    case 2: 
      i = Math.min(j, 3);
    }
    return i;
  }
  
  void create()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("moveto CREATED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    if (!mFragment.mIsCreated)
    {
      mDispatcher.dispatchOnFragmentPreCreated(mFragment, mFragment.mSavedFragmentState, false);
      mFragment.performCreate(mFragment.mSavedFragmentState);
      mDispatcher.dispatchOnFragmentCreated(mFragment, mFragment.mSavedFragmentState, false);
      return;
    }
    mFragment.restoreChildFragmentState(mFragment.mSavedFragmentState);
    mFragment.mState = 1;
  }
  
  void createView(FragmentContainer paramFragmentContainer)
  {
    if (mFragment.mFromLayout) {
      return;
    }
    if (FragmentManager.isLoggingEnabled(3))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("moveto CREATE_VIEW: ");
      ((StringBuilder)localObject).append(mFragment);
      Log.d("FragmentManager", ((StringBuilder)localObject).toString());
    }
    Object localObject = null;
    if (mFragment.mContainer != null) {
      localObject = mFragment.mContainer;
    } else if (mFragment.mContainerId != 0) {
      if (mFragment.mContainerId != -1)
      {
        paramFragmentContainer = (ViewGroup)paramFragmentContainer.onFindViewById(mFragment.mContainerId);
        localObject = paramFragmentContainer;
        if (paramFragmentContainer != null) {
          break label299;
        }
        if (mFragment.mRestored)
        {
          localObject = paramFragmentContainer;
          break label299;
        }
        paramFragmentContainer = mFragment;
      }
    }
    try
    {
      paramFragmentContainer = paramFragmentContainer.getResources();
      int i = mFragment.mContainerId;
      paramFragmentContainer = paramFragmentContainer.getResourceName(i);
    }
    catch (Resources.NotFoundException paramFragmentContainer)
    {
      boolean bool2;
      boolean bool1;
      for (;;) {}
    }
    paramFragmentContainer = "unknown";
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("No view found for id 0x");
    ((StringBuilder)localObject).append(Integer.toHexString(mFragment.mContainerId));
    ((StringBuilder)localObject).append(" (");
    ((StringBuilder)localObject).append(paramFragmentContainer);
    ((StringBuilder)localObject).append(") for fragment ");
    ((StringBuilder)localObject).append(mFragment);
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    paramFragmentContainer = new StringBuilder();
    paramFragmentContainer.append("Cannot create fragment ");
    paramFragmentContainer.append(mFragment);
    paramFragmentContainer.append(" for a container view with no id");
    throw new IllegalArgumentException(paramFragmentContainer.toString());
    label299:
    mFragment.mContainer = ((ViewGroup)localObject);
    mFragment.performCreateView(mFragment.performGetLayoutInflater(mFragment.mSavedFragmentState), (ViewGroup)localObject, mFragment.mSavedFragmentState);
    if (mFragment.mView != null)
    {
      paramFragmentContainer = mFragment.mView;
      bool2 = false;
      paramFragmentContainer.setSaveFromParentEnabled(false);
      mFragment.mView.setTag(R.id.fragment_container_view_tag, mFragment);
      if (localObject != null) {
        ((ViewGroup)localObject).addView(mFragment.mView);
      }
      if (mFragment.mHidden) {
        mFragment.mView.setVisibility(8);
      }
      ViewCompat.requestApplyInsets(mFragment.mView);
      mFragment.onViewCreated(mFragment.mView, mFragment.mSavedFragmentState);
      mDispatcher.dispatchOnFragmentViewCreated(mFragment, mFragment.mView, mFragment.mSavedFragmentState, false);
      paramFragmentContainer = mFragment;
      bool1 = bool2;
      if (mFragment.mView.getVisibility() == 0)
      {
        bool1 = bool2;
        if (mFragment.mContainer != null) {
          bool1 = true;
        }
      }
      mIsNewlyAdded = bool1;
      return;
    }
  }
  
  void destroy(FragmentHostCallback paramFragmentHostCallback, FragmentManagerViewModel paramFragmentManagerViewModel)
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("movefrom CREATED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    boolean bool2 = mFragment.mRemoving;
    boolean bool1 = true;
    int i;
    if ((bool2) && (!mFragment.isInBackStack())) {
      i = 1;
    } else {
      i = 0;
    }
    int j;
    if ((i == 0) && (!paramFragmentManagerViewModel.shouldDestroy(mFragment))) {
      j = 0;
    } else {
      j = 1;
    }
    if (j != 0)
    {
      if ((paramFragmentHostCallback instanceof ViewModelStoreOwner)) {
        bool1 = paramFragmentManagerViewModel.isCleared();
      } else if ((paramFragmentHostCallback.getContext() instanceof Activity)) {
        bool1 = true ^ ((Activity)paramFragmentHostCallback.getContext()).isChangingConfigurations();
      }
      if ((i != 0) || (bool1)) {
        paramFragmentManagerViewModel.clearNonConfigState(mFragment);
      }
      mFragment.performDestroy();
      mDispatcher.dispatchOnFragmentDestroyed(mFragment, false);
      return;
    }
    mFragment.mState = 0;
  }
  
  void detach(FragmentManagerViewModel paramFragmentManagerViewModel)
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("movefrom ATTACHED: ");
      ((StringBuilder)localObject).append(mFragment);
      Log.d("FragmentManager", ((StringBuilder)localObject).toString());
    }
    mFragment.performDetach();
    Object localObject = mDispatcher;
    Fragment localFragment = mFragment;
    int j = 0;
    ((FragmentLifecycleCallbacksDispatcher)localObject).dispatchOnFragmentDetached(localFragment, false);
    mFragment.mState = -1;
    mFragment.mHost = null;
    mFragment.mParentFragment = null;
    mFragment.mFragmentManager = null;
    int i = j;
    if (mFragment.mRemoving)
    {
      i = j;
      if (!mFragment.isInBackStack()) {
        i = 1;
      }
    }
    if ((i != 0) || (paramFragmentManagerViewModel.shouldDestroy(mFragment)))
    {
      if (FragmentManager.isLoggingEnabled(3))
      {
        paramFragmentManagerViewModel = new StringBuilder();
        paramFragmentManagerViewModel.append("initState called for fragment: ");
        paramFragmentManagerViewModel.append(mFragment);
        Log.d("FragmentManager", paramFragmentManagerViewModel.toString());
      }
      mFragment.initState();
    }
  }
  
  void ensureInflatedView()
  {
    if ((mFragment.mFromLayout) && (mFragment.mInLayout) && (!mFragment.mPerformedCreateView))
    {
      if (FragmentManager.isLoggingEnabled(3))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("moveto CREATE_VIEW: ");
        localStringBuilder.append(mFragment);
        Log.d("FragmentManager", localStringBuilder.toString());
      }
      mFragment.performCreateView(mFragment.performGetLayoutInflater(mFragment.mSavedFragmentState), null, mFragment.mSavedFragmentState);
      if (mFragment.mView != null)
      {
        mFragment.mView.setSaveFromParentEnabled(false);
        mFragment.mView.setTag(R.id.fragment_container_view_tag, mFragment);
        if (mFragment.mHidden) {
          mFragment.mView.setVisibility(8);
        }
        mFragment.onViewCreated(mFragment.mView, mFragment.mSavedFragmentState);
        mDispatcher.dispatchOnFragmentViewCreated(mFragment, mFragment.mView, mFragment.mSavedFragmentState, false);
      }
    }
  }
  
  Fragment getFragment()
  {
    return mFragment;
  }
  
  void pause()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("movefrom RESUMED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    mFragment.performPause();
    mDispatcher.dispatchOnFragmentPaused(mFragment, false);
  }
  
  void restoreState(ClassLoader paramClassLoader)
  {
    if (mFragment.mSavedFragmentState == null) {
      return;
    }
    mFragment.mSavedFragmentState.setClassLoader(paramClassLoader);
    mFragment.mSavedViewState = mFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
    mFragment.mTargetWho = mFragment.mSavedFragmentState.getString("android:target_state");
    if (mFragment.mTargetWho != null) {
      mFragment.mTargetRequestCode = mFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
    }
    if (mFragment.mSavedUserVisibleHint != null)
    {
      mFragment.mUserVisibleHint = mFragment.mSavedUserVisibleHint.booleanValue();
      mFragment.mSavedUserVisibleHint = null;
    }
    else
    {
      mFragment.mUserVisibleHint = mFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
    }
    if (!mFragment.mUserVisibleHint) {
      mFragment.mDeferStart = true;
    }
  }
  
  void restoreViewState()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("moveto RESTORE_VIEW_STATE: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    if (mFragment.mView != null) {
      mFragment.restoreViewState(mFragment.mSavedFragmentState);
    }
    mFragment.mSavedFragmentState = null;
  }
  
  void resume()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("moveto RESUMED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    mFragment.performResume();
    mDispatcher.dispatchOnFragmentResumed(mFragment, false);
    mFragment.mSavedFragmentState = null;
    mFragment.mSavedViewState = null;
  }
  
  Fragment.SavedState saveInstanceState()
  {
    if (mFragment.mState > -1)
    {
      Bundle localBundle = saveBasicState();
      if (localBundle != null) {
        return new Fragment.SavedState(localBundle);
      }
    }
    return null;
  }
  
  FragmentState saveState()
  {
    FragmentState localFragmentState = new FragmentState(mFragment);
    if ((mFragment.mState > -1) && (mSavedFragmentState == null))
    {
      mSavedFragmentState = saveBasicState();
      if (mFragment.mTargetWho != null)
      {
        if (mSavedFragmentState == null) {
          mSavedFragmentState = new Bundle();
        }
        mSavedFragmentState.putString("android:target_state", mFragment.mTargetWho);
        if (mFragment.mTargetRequestCode != 0)
        {
          mSavedFragmentState.putInt("android:target_req_state", mFragment.mTargetRequestCode);
          return localFragmentState;
        }
      }
    }
    else
    {
      mSavedFragmentState = mFragment.mSavedFragmentState;
    }
    return localFragmentState;
  }
  
  void saveViewState()
  {
    if (mFragment.mView == null) {
      return;
    }
    SparseArray localSparseArray = new SparseArray();
    mFragment.mView.saveHierarchyState(localSparseArray);
    if (localSparseArray.size() > 0) {
      mFragment.mSavedViewState = localSparseArray;
    }
  }
  
  void setFragmentManagerState(int paramInt)
  {
    mFragmentManagerState = paramInt;
  }
  
  void start()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("moveto STARTED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    mFragment.performStart();
    mDispatcher.dispatchOnFragmentStarted(mFragment, false);
  }
  
  void stop()
  {
    if (FragmentManager.isLoggingEnabled(3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("movefrom STARTED: ");
      localStringBuilder.append(mFragment);
      Log.d("FragmentManager", localStringBuilder.toString());
    }
    mFragment.performStop();
    mDispatcher.dispatchOnFragmentStopped(mFragment, false);
  }
}
