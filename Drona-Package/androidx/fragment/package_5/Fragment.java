package androidx.fragment.package_5;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.package_4.SharedElementCallback;
import androidx.core.view.LayoutInflaterCompat;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Fragment
  implements ComponentCallbacks, View.OnCreateContextMenuListener, LifecycleOwner, ViewModelStoreOwner, HasDefaultViewModelProviderFactory, SavedStateRegistryOwner
{
  static final int ACTIVITY_CREATED = 2;
  static final int ATTACHED = 0;
  static final int CREATED = 1;
  static final int INITIALIZING = -1;
  static final int RESUMED = 4;
  static final int STARTED = 3;
  static final Object USE_DEFAULT_TRANSITION = new Object();
  boolean mAdded;
  AnimationInfo mAnimationInfo;
  Bundle mArguments;
  int mBackStackNesting;
  private boolean mCalled;
  @NonNull
  FragmentManager mChildFragmentManager = new FragmentManagerImpl();
  ViewGroup mContainer;
  int mContainerId;
  @LayoutRes
  private int mContentLayoutId;
  private ViewModelProvider.Factory mDefaultFactory;
  boolean mDeferStart;
  boolean mDetached;
  int mFragmentId;
  FragmentManager mFragmentManager;
  boolean mFromLayout;
  boolean mHasMenu;
  boolean mHidden;
  boolean mHiddenChanged;
  androidx.fragment.app.FragmentHostCallback<?> mHost;
  boolean mInLayout;
  boolean mIsCreated;
  boolean mIsNewlyAdded;
  private Boolean mIsPrimaryNavigationFragment = null;
  LayoutInflater mLayoutInflater;
  LifecycleRegistry mLifecycleRegistry;
  Lifecycle.State mMaxState = Lifecycle.State.RESUMED;
  boolean mMenuVisible = true;
  Fragment mParentFragment;
  boolean mPerformedCreateView;
  float mPostponedAlpha;
  Runnable mPostponedDurationRunnable = new Runnable()
  {
    public void run()
    {
      startPostponedEnterTransition();
    }
  };
  boolean mRemoving;
  boolean mRestored;
  boolean mRetainInstance;
  boolean mRetainInstanceChangedWhileDetached;
  Bundle mSavedFragmentState;
  SavedStateRegistryController mSavedStateRegistryController;
  @Nullable
  Boolean mSavedUserVisibleHint;
  SparseArray<Parcelable> mSavedViewState;
  int mState = -1;
  String mTag;
  Fragment mTarget;
  int mTargetRequestCode;
  String mTargetWho = null;
  boolean mUserVisibleHint = true;
  View mView;
  @Nullable
  FragmentViewLifecycleOwner mViewLifecycleOwner;
  MutableLiveData<LifecycleOwner> mViewLifecycleOwnerLiveData = new MutableLiveData();
  @NonNull
  String mWho = UUID.randomUUID().toString();
  
  public Fragment()
  {
    initLifecycle();
  }
  
  public Fragment(int paramInt)
  {
    this();
    mContentLayoutId = paramInt;
  }
  
  private AnimationInfo ensureAnimationInfo()
  {
    if (mAnimationInfo == null) {
      mAnimationInfo = new AnimationInfo();
    }
    return mAnimationInfo;
  }
  
  private void initLifecycle()
  {
    mLifecycleRegistry = new LifecycleRegistry(this);
    mSavedStateRegistryController = SavedStateRegistryController.create(this);
    if (Build.VERSION.SDK_INT >= 19) {
      mLifecycleRegistry.addObserver(new Fragment.2(this));
    }
  }
  
  public static Fragment instantiate(Context paramContext, String paramString)
  {
    return instantiate(paramContext, paramString, null);
  }
  
  public static Fragment instantiate(Context paramContext, String paramString, Bundle paramBundle)
  {
    try
    {
      paramContext = FragmentFactory.loadFragmentClass(paramContext.getClassLoader(), paramString);
      paramContext = paramContext.getConstructor(new Class[0]);
      paramContext = paramContext.newInstance(new Object[0]);
      paramContext = (Fragment)paramContext;
      if (paramBundle != null)
      {
        paramBundle.setClassLoader(paramContext.getClass().getClassLoader());
        paramContext.setArguments(paramBundle);
        return paramContext;
      }
    }
    catch (InvocationTargetException paramContext)
    {
      paramBundle = new StringBuilder();
      paramBundle.append("Unable to instantiate fragment ");
      paramBundle.append(paramString);
      paramBundle.append(": calling Fragment constructor caused an exception");
      throw new InstantiationException(paramBundle.toString(), paramContext);
    }
    catch (NoSuchMethodException paramContext)
    {
      paramBundle = new StringBuilder();
      paramBundle.append("Unable to instantiate fragment ");
      paramBundle.append(paramString);
      paramBundle.append(": could not find Fragment constructor");
      throw new InstantiationException(paramBundle.toString(), paramContext);
    }
    catch (IllegalAccessException paramContext)
    {
      paramBundle = new StringBuilder();
      paramBundle.append("Unable to instantiate fragment ");
      paramBundle.append(paramString);
      paramBundle.append(": make sure class name exists, is public, and has an empty constructor that is public");
      throw new InstantiationException(paramBundle.toString(), paramContext);
    }
    catch (InstantiationException paramContext)
    {
      paramBundle = new StringBuilder();
      paramBundle.append("Unable to instantiate fragment ");
      paramBundle.append(paramString);
      paramBundle.append(": make sure class name exists, is public, and has an empty constructor that is public");
      throw new InstantiationException(paramBundle.toString(), paramContext);
    }
    return paramContext;
  }
  
  void callStartTransitionListener()
  {
    OnStartEnterTransitionListener localOnStartEnterTransitionListener;
    if (mAnimationInfo == null)
    {
      localOnStartEnterTransitionListener = null;
    }
    else
    {
      mAnimationInfo.mEnterTransitionPostponed = false;
      localOnStartEnterTransitionListener = mAnimationInfo.mStartEnterTransitionListener;
      mAnimationInfo.mStartEnterTransitionListener = null;
    }
    if (localOnStartEnterTransitionListener != null) {
      localOnStartEnterTransitionListener.onStartEnterTransition();
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFragmentId=#");
    paramPrintWriter.print(Integer.toHexString(mFragmentId));
    paramPrintWriter.print(" mContainerId=#");
    paramPrintWriter.print(Integer.toHexString(mContainerId));
    paramPrintWriter.print(" mTag=");
    paramPrintWriter.println(mTag);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mState=");
    paramPrintWriter.print(mState);
    paramPrintWriter.print(" mWho=");
    paramPrintWriter.print(mWho);
    paramPrintWriter.print(" mBackStackNesting=");
    paramPrintWriter.println(mBackStackNesting);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAdded=");
    paramPrintWriter.print(mAdded);
    paramPrintWriter.print(" mRemoving=");
    paramPrintWriter.print(mRemoving);
    paramPrintWriter.print(" mFromLayout=");
    paramPrintWriter.print(mFromLayout);
    paramPrintWriter.print(" mInLayout=");
    paramPrintWriter.println(mInLayout);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHidden=");
    paramPrintWriter.print(mHidden);
    paramPrintWriter.print(" mDetached=");
    paramPrintWriter.print(mDetached);
    paramPrintWriter.print(" mMenuVisible=");
    paramPrintWriter.print(mMenuVisible);
    paramPrintWriter.print(" mHasMenu=");
    paramPrintWriter.println(mHasMenu);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRetainInstance=");
    paramPrintWriter.print(mRetainInstance);
    paramPrintWriter.print(" mUserVisibleHint=");
    paramPrintWriter.println(mUserVisibleHint);
    if (mFragmentManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mFragmentManager=");
      paramPrintWriter.println(mFragmentManager);
    }
    if (mHost != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mHost=");
      paramPrintWriter.println(mHost);
    }
    if (mParentFragment != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mParentFragment=");
      paramPrintWriter.println(mParentFragment);
    }
    if (mArguments != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mArguments=");
      paramPrintWriter.println(mArguments);
    }
    if (mSavedFragmentState != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedFragmentState=");
      paramPrintWriter.println(mSavedFragmentState);
    }
    if (mSavedViewState != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedViewState=");
      paramPrintWriter.println(mSavedViewState);
    }
    Object localObject = getTargetFragment();
    if (localObject != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTarget=");
      paramPrintWriter.print(localObject);
      paramPrintWriter.print(" mTargetRequestCode=");
      paramPrintWriter.println(mTargetRequestCode);
    }
    if (getNextAnim() != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAnim=");
      paramPrintWriter.println(getNextAnim());
    }
    if (mContainer != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mContainer=");
      paramPrintWriter.println(mContainer);
    }
    if (mView != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mView=");
      paramPrintWriter.println(mView);
    }
    if (getAnimatingAway() != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimatingAway=");
      paramPrintWriter.println(getAnimatingAway());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStateAfterAnimating=");
      paramPrintWriter.println(getStateAfterAnimating());
    }
    if (getContext() != null) {
      LoaderManager.getInstance(this).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    paramPrintWriter.print(paramString);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Child ");
    ((StringBuilder)localObject).append(mChildFragmentManager);
    ((StringBuilder)localObject).append(":");
    paramPrintWriter.println(((StringBuilder)localObject).toString());
    localObject = mChildFragmentManager;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("  ");
    ((FragmentManager)localObject).dump(localStringBuilder.toString(), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public final boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  Fragment findFragmentByWho(String paramString)
  {
    if (paramString.equals(mWho)) {
      return this;
    }
    return mChildFragmentManager.findFragmentByWho(paramString);
  }
  
  public final FragmentActivity getActivity()
  {
    if (mHost == null) {
      return null;
    }
    return (FragmentActivity)mHost.getActivity();
  }
  
  public boolean getAllowEnterTransitionOverlap()
  {
    if ((mAnimationInfo != null) && (mAnimationInfo.mAllowEnterTransitionOverlap != null)) {
      return mAnimationInfo.mAllowEnterTransitionOverlap.booleanValue();
    }
    return true;
  }
  
  public boolean getAllowReturnTransitionOverlap()
  {
    if ((mAnimationInfo != null) && (mAnimationInfo.mAllowReturnTransitionOverlap != null)) {
      return mAnimationInfo.mAllowReturnTransitionOverlap.booleanValue();
    }
    return true;
  }
  
  View getAnimatingAway()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mAnimatingAway;
  }
  
  Animator getAnimator()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mAnimator;
  }
  
  public final Bundle getArguments()
  {
    return mArguments;
  }
  
  public final FragmentManager getChildFragmentManager()
  {
    if (mHost != null) {
      return mChildFragmentManager;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" has not been attached yet.");
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public Context getContext()
  {
    if (mHost == null) {
      return null;
    }
    return mHost.getContext();
  }
  
  public ViewModelProvider.Factory getDefaultViewModelProviderFactory()
  {
    if (mFragmentManager != null)
    {
      if (mDefaultFactory == null) {
        mDefaultFactory = new SavedStateViewModelFactory(requireActivity().getApplication(), this, getArguments());
      }
      return mDefaultFactory;
    }
    throw new IllegalStateException("Can't access ViewModels from detached fragment");
  }
  
  public Object getEnterTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mEnterTransition;
  }
  
  SharedElementCallback getEnterTransitionCallback()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mEnterTransitionCallback;
  }
  
  public Object getExitTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mExitTransition;
  }
  
  SharedElementCallback getExitTransitionCallback()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mExitTransitionCallback;
  }
  
  public final FragmentManager getFragmentManager()
  {
    return mFragmentManager;
  }
  
  public final Object getHost()
  {
    if (mHost == null) {
      return null;
    }
    return mHost.onGetHost();
  }
  
  public final int getId()
  {
    return mFragmentId;
  }
  
  public final LayoutInflater getLayoutInflater()
  {
    if (mLayoutInflater == null) {
      return performGetLayoutInflater(null);
    }
    return mLayoutInflater;
  }
  
  public LayoutInflater getLayoutInflater(Bundle paramBundle)
  {
    if (mHost != null)
    {
      paramBundle = mHost.onGetLayoutInflater();
      LayoutInflaterCompat.setFactory2(paramBundle, mChildFragmentManager.getLayoutInflaterFactory());
      return paramBundle;
    }
    throw new IllegalStateException("onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager.");
  }
  
  public Lifecycle getLifecycle()
  {
    return mLifecycleRegistry;
  }
  
  public LoaderManager getLoaderManager()
  {
    return LoaderManager.getInstance(this);
  }
  
  int getNextAnim()
  {
    if (mAnimationInfo == null) {
      return 0;
    }
    return mAnimationInfo.mNextAnim;
  }
  
  int getNextTransition()
  {
    if (mAnimationInfo == null) {
      return 0;
    }
    return mAnimationInfo.mNextTransition;
  }
  
  public final Fragment getParentFragment()
  {
    return mParentFragment;
  }
  
  public final FragmentManager getParentFragmentManager()
  {
    Object localObject = mFragmentManager;
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" not associated with a fragment manager.");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public Object getReenterTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    if (mAnimationInfo.mReenterTransition == USE_DEFAULT_TRANSITION) {
      return getExitTransition();
    }
    return mAnimationInfo.mReenterTransition;
  }
  
  public final Resources getResources()
  {
    return requireContext().getResources();
  }
  
  public final boolean getRetainInstance()
  {
    return mRetainInstance;
  }
  
  public Object getReturnTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    if (mAnimationInfo.mReturnTransition == USE_DEFAULT_TRANSITION) {
      return getEnterTransition();
    }
    return mAnimationInfo.mReturnTransition;
  }
  
  public final SavedStateRegistry getSavedStateRegistry()
  {
    return mSavedStateRegistryController.getSavedStateRegistry();
  }
  
  public Object getSharedElementEnterTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    return mAnimationInfo.mSharedElementEnterTransition;
  }
  
  public Object getSharedElementReturnTransition()
  {
    if (mAnimationInfo == null) {
      return null;
    }
    if (mAnimationInfo.mSharedElementReturnTransition == USE_DEFAULT_TRANSITION) {
      return getSharedElementEnterTransition();
    }
    return mAnimationInfo.mSharedElementReturnTransition;
  }
  
  int getStateAfterAnimating()
  {
    if (mAnimationInfo == null) {
      return 0;
    }
    return mAnimationInfo.mStateAfterAnimating;
  }
  
  public final String getString(int paramInt)
  {
    return getResources().getString(paramInt);
  }
  
  public final String getString(int paramInt, Object... paramVarArgs)
  {
    return getResources().getString(paramInt, paramVarArgs);
  }
  
  public final String getTag()
  {
    return mTag;
  }
  
  public final Fragment getTargetFragment()
  {
    if (mTarget != null) {
      return mTarget;
    }
    if ((mFragmentManager != null) && (mTargetWho != null)) {
      return mFragmentManager.findActiveFragment(mTargetWho);
    }
    return null;
  }
  
  public final int getTargetRequestCode()
  {
    return mTargetRequestCode;
  }
  
  public final CharSequence getText(int paramInt)
  {
    return getResources().getText(paramInt);
  }
  
  public boolean getUserVisibleHint()
  {
    return mUserVisibleHint;
  }
  
  public View getView()
  {
    return mView;
  }
  
  public LifecycleOwner getViewLifecycleOwner()
  {
    if (mViewLifecycleOwner != null) {
      return mViewLifecycleOwner;
    }
    throw new IllegalStateException("Can't access the Fragment View's LifecycleOwner when getView() is null i.e., before onCreateView() or after onDestroyView()");
  }
  
  public LiveData getViewLifecycleOwnerLiveData()
  {
    return mViewLifecycleOwnerLiveData;
  }
  
  public ViewModelStore getViewModelStore()
  {
    if (mFragmentManager != null) {
      return mFragmentManager.getViewModelStore(this);
    }
    throw new IllegalStateException("Can't access ViewModels from detached fragment");
  }
  
  public final boolean hasOptionsMenu()
  {
    return mHasMenu;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  void initState()
  {
    initLifecycle();
    mWho = UUID.randomUUID().toString();
    mAdded = false;
    mRemoving = false;
    mFromLayout = false;
    mInLayout = false;
    mRestored = false;
    mBackStackNesting = 0;
    mFragmentManager = null;
    mChildFragmentManager = new FragmentManagerImpl();
    mHost = null;
    mFragmentId = 0;
    mContainerId = 0;
    mTag = null;
    mHidden = false;
    mDetached = false;
  }
  
  public final boolean isAdded()
  {
    return (mHost != null) && (mAdded);
  }
  
  public final boolean isDetached()
  {
    return mDetached;
  }
  
  public final boolean isHidden()
  {
    return mHidden;
  }
  
  boolean isHideReplaced()
  {
    if (mAnimationInfo == null) {
      return false;
    }
    return mAnimationInfo.mIsHideReplaced;
  }
  
  final boolean isInBackStack()
  {
    return mBackStackNesting > 0;
  }
  
  public final boolean isInLayout()
  {
    return mInLayout;
  }
  
  public final boolean isMenuVisible()
  {
    return mMenuVisible;
  }
  
  boolean isPostponed()
  {
    if (mAnimationInfo == null) {
      return false;
    }
    return mAnimationInfo.mEnterTransitionPostponed;
  }
  
  public final boolean isRemoving()
  {
    return mRemoving;
  }
  
  final boolean isRemovingParent()
  {
    Fragment localFragment = getParentFragment();
    return (localFragment != null) && ((localFragment.isRemoving()) || (localFragment.isRemovingParent()));
  }
  
  public final boolean isResumed()
  {
    return mState >= 4;
  }
  
  public final boolean isStateSaved()
  {
    if (mFragmentManager == null) {
      return false;
    }
    return mFragmentManager.isStateSaved();
  }
  
  public final boolean isVisible()
  {
    return (isAdded()) && (!isHidden()) && (mView != null) && (mView.getWindowToken() != null) && (mView.getVisibility() == 0);
  }
  
  void noteStateNotSaved()
  {
    mChildFragmentManager.noteStateNotSaved();
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    mCalled = true;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public void onAttach(Activity paramActivity)
  {
    mCalled = true;
  }
  
  public void onAttach(Context paramContext)
  {
    mCalled = true;
    if (mHost == null) {
      paramContext = null;
    } else {
      paramContext = mHost.getActivity();
    }
    if (paramContext != null)
    {
      mCalled = false;
      onAttach(paramContext);
    }
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    mCalled = true;
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    mCalled = true;
    restoreChildFragmentState(paramBundle);
    if (!mChildFragmentManager.isStateAtLeast(1)) {
      mChildFragmentManager.dispatchCreate();
    }
  }
  
  public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    return null;
  }
  
  public Animator onCreateAnimator(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    return null;
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    requireActivity().onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {}
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (mContentLayoutId != 0) {
      return paramLayoutInflater.inflate(mContentLayoutId, paramViewGroup, false);
    }
    return null;
  }
  
  public void onDestroy()
  {
    mCalled = true;
  }
  
  public void onDestroyOptionsMenu() {}
  
  public void onDestroyView()
  {
    mCalled = true;
  }
  
  public void onDetach()
  {
    mCalled = true;
  }
  
  public LayoutInflater onGetLayoutInflater(Bundle paramBundle)
  {
    return getLayoutInflater(paramBundle);
  }
  
  public void onHiddenChanged(boolean paramBoolean) {}
  
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    mCalled = true;
  }
  
  public void onInflate(Context paramContext, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    mCalled = true;
    if (mHost == null) {
      paramContext = null;
    } else {
      paramContext = mHost.getActivity();
    }
    if (paramContext != null)
    {
      mCalled = false;
      onInflate(paramContext, paramAttributeSet, paramBundle);
    }
  }
  
  public void onLowMemory()
  {
    mCalled = true;
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onOptionsMenuClosed(Menu paramMenu) {}
  
  public void onPause()
  {
    mCalled = true;
  }
  
  public void onPictureInPictureModeChanged(boolean paramBoolean) {}
  
  public void onPrepareOptionsMenu(Menu paramMenu) {}
  
  public void onPrimaryNavigationFragmentChanged(boolean paramBoolean) {}
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {}
  
  public void onResume()
  {
    mCalled = true;
  }
  
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  public void onStart()
  {
    mCalled = true;
  }
  
  public void onStop()
  {
    mCalled = true;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle) {}
  
  public void onViewStateRestored(Bundle paramBundle)
  {
    mCalled = true;
  }
  
  void performActivityCreated(Bundle paramBundle)
  {
    mChildFragmentManager.noteStateNotSaved();
    mState = 2;
    mCalled = false;
    onActivityCreated(paramBundle);
    if (mCalled)
    {
      mChildFragmentManager.dispatchActivityCreated();
      return;
    }
    paramBundle = new StringBuilder();
    paramBundle.append("Fragment ");
    paramBundle.append(this);
    paramBundle.append(" did not call through to super.onActivityCreated()");
    throw new SuperNotCalledException(paramBundle.toString());
  }
  
  void performAttach()
  {
    mChildFragmentManager.attachController(mHost, new Fragment.4(this), this);
    mState = 0;
    mCalled = false;
    onAttach(mHost.getContext());
    if (mCalled) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onAttach()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performConfigurationChanged(Configuration paramConfiguration)
  {
    onConfigurationChanged(paramConfiguration);
    mChildFragmentManager.dispatchConfigurationChanged(paramConfiguration);
  }
  
  boolean performContextItemSelected(MenuItem paramMenuItem)
  {
    if (!mHidden)
    {
      if (onContextItemSelected(paramMenuItem)) {
        return true;
      }
      if (mChildFragmentManager.dispatchContextItemSelected(paramMenuItem)) {
        return true;
      }
    }
    return false;
  }
  
  void performCreate(Bundle paramBundle)
  {
    mChildFragmentManager.noteStateNotSaved();
    mState = 1;
    mCalled = false;
    mSavedStateRegistryController.performRestore(paramBundle);
    onCreate(paramBundle);
    mIsCreated = true;
    if (mCalled)
    {
      mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
      return;
    }
    paramBundle = new StringBuilder();
    paramBundle.append("Fragment ");
    paramBundle.append(this);
    paramBundle.append(" did not call through to super.onCreate()");
    throw new SuperNotCalledException(paramBundle.toString());
  }
  
  boolean performCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool = mHidden;
    int j = 0;
    if (!bool)
    {
      int i = j;
      if (mHasMenu)
      {
        i = j;
        if (mMenuVisible)
        {
          i = 1;
          onCreateOptionsMenu(paramMenu, paramMenuInflater);
        }
      }
      return i | mChildFragmentManager.dispatchCreateOptionsMenu(paramMenu, paramMenuInflater);
    }
    return false;
  }
  
  void performCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    mChildFragmentManager.noteStateNotSaved();
    mPerformedCreateView = true;
    mViewLifecycleOwner = new FragmentViewLifecycleOwner();
    mView = onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    if (mView != null)
    {
      mViewLifecycleOwner.initialize();
      mViewLifecycleOwnerLiveData.setValue(mViewLifecycleOwner);
      return;
    }
    if (!mViewLifecycleOwner.isInitialized())
    {
      mViewLifecycleOwner = null;
      return;
    }
    throw new IllegalStateException("Called getViewLifecycleOwner() but onCreateView() returned null");
  }
  
  void performDestroy()
  {
    mChildFragmentManager.dispatchDestroy();
    mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    mState = 0;
    mCalled = false;
    mIsCreated = false;
    onDestroy();
    if (mCalled) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onDestroy()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performDestroyView()
  {
    mChildFragmentManager.dispatchDestroyView();
    if (mView != null) {
      mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }
    mState = 1;
    mCalled = false;
    onDestroyView();
    if (mCalled)
    {
      LoaderManager.getInstance(this).markForRedelivery();
      mPerformedCreateView = false;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onDestroyView()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performDetach()
  {
    mState = -1;
    mCalled = false;
    onDetach();
    mLayoutInflater = null;
    if (mCalled)
    {
      if (!mChildFragmentManager.isDestroyed())
      {
        mChildFragmentManager.dispatchDestroy();
        mChildFragmentManager = new FragmentManagerImpl();
      }
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Fragment ");
      localStringBuilder.append(this);
      localStringBuilder.append(" did not call through to super.onDetach()");
      throw new SuperNotCalledException(localStringBuilder.toString());
    }
  }
  
  LayoutInflater performGetLayoutInflater(Bundle paramBundle)
  {
    mLayoutInflater = onGetLayoutInflater(paramBundle);
    return mLayoutInflater;
  }
  
  void performLowMemory()
  {
    onLowMemory();
    mChildFragmentManager.dispatchLowMemory();
  }
  
  void performMultiWindowModeChanged(boolean paramBoolean)
  {
    onMultiWindowModeChanged(paramBoolean);
    mChildFragmentManager.dispatchMultiWindowModeChanged(paramBoolean);
  }
  
  boolean performOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (!mHidden)
    {
      if ((mHasMenu) && (mMenuVisible) && (onOptionsItemSelected(paramMenuItem))) {
        return true;
      }
      if (mChildFragmentManager.dispatchOptionsItemSelected(paramMenuItem)) {
        return true;
      }
    }
    return false;
  }
  
  void performOptionsMenuClosed(Menu paramMenu)
  {
    if (!mHidden)
    {
      if ((mHasMenu) && (mMenuVisible)) {
        onOptionsMenuClosed(paramMenu);
      }
      mChildFragmentManager.dispatchOptionsMenuClosed(paramMenu);
    }
  }
  
  void performPause()
  {
    mChildFragmentManager.dispatchPause();
    if (mView != null) {
      mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }
    mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    mState = 3;
    mCalled = false;
    onPause();
    if (mCalled) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onPause()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performPictureInPictureModeChanged(boolean paramBoolean)
  {
    onPictureInPictureModeChanged(paramBoolean);
    mChildFragmentManager.dispatchPictureInPictureModeChanged(paramBoolean);
  }
  
  boolean performPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool = mHidden;
    int j = 0;
    if (!bool)
    {
      int i = j;
      if (mHasMenu)
      {
        i = j;
        if (mMenuVisible)
        {
          i = 1;
          onPrepareOptionsMenu(paramMenu);
        }
      }
      return i | mChildFragmentManager.dispatchPrepareOptionsMenu(paramMenu);
    }
    return false;
  }
  
  void performPrimaryNavigationFragmentChanged()
  {
    boolean bool = mFragmentManager.isPrimaryNavigation(this);
    if ((mIsPrimaryNavigationFragment == null) || (mIsPrimaryNavigationFragment.booleanValue() != bool))
    {
      mIsPrimaryNavigationFragment = Boolean.valueOf(bool);
      onPrimaryNavigationFragmentChanged(bool);
      mChildFragmentManager.dispatchPrimaryNavigationFragmentChanged();
    }
  }
  
  void performResume()
  {
    mChildFragmentManager.noteStateNotSaved();
    mChildFragmentManager.execPendingActions(true);
    mState = 4;
    mCalled = false;
    onResume();
    if (mCalled)
    {
      mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
      if (mView != null) {
        mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
      }
      mChildFragmentManager.dispatchResume();
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onResume()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performSaveInstanceState(Bundle paramBundle)
  {
    onSaveInstanceState(paramBundle);
    mSavedStateRegistryController.performSave(paramBundle);
    Parcelable localParcelable = mChildFragmentManager.saveAllState();
    if (localParcelable != null) {
      paramBundle.putParcelable("android:support:fragments", localParcelable);
    }
  }
  
  void performStart()
  {
    mChildFragmentManager.noteStateNotSaved();
    mChildFragmentManager.execPendingActions(true);
    mState = 3;
    mCalled = false;
    onStart();
    if (mCalled)
    {
      mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
      if (mView != null) {
        mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START);
      }
      mChildFragmentManager.dispatchStart();
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onStart()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  void performStop()
  {
    mChildFragmentManager.dispatchStop();
    if (mView != null) {
      mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }
    mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    mState = 2;
    mCalled = false;
    onStop();
    if (mCalled) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Fragment ");
    localStringBuilder.append(this);
    localStringBuilder.append(" did not call through to super.onStop()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  public void postponeEnterTransition()
  {
    ensureAnimationInfomEnterTransitionPostponed = true;
  }
  
  public final void postponeEnterTransition(long paramLong, TimeUnit paramTimeUnit)
  {
    ensureAnimationInfomEnterTransitionPostponed = true;
    Handler localHandler;
    if (mFragmentManager != null) {
      localHandler = mFragmentManager.mHost.getHandler();
    } else {
      localHandler = new Handler(Looper.getMainLooper());
    }
    localHandler.removeCallbacks(mPostponedDurationRunnable);
    localHandler.postDelayed(mPostponedDurationRunnable, paramTimeUnit.toMillis(paramLong));
  }
  
  public void registerForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(this);
  }
  
  public final void requestPermissions(String[] paramArrayOfString, int paramInt)
  {
    if (mHost != null)
    {
      mHost.onRequestPermissionsFromFragment(this, paramArrayOfString, paramInt);
      return;
    }
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("Fragment ");
    paramArrayOfString.append(this);
    paramArrayOfString.append(" not attached to Activity");
    throw new IllegalStateException(paramArrayOfString.toString());
  }
  
  public final FragmentActivity requireActivity()
  {
    Object localObject = getActivity();
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" not attached to an activity.");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public final Bundle requireArguments()
  {
    Object localObject = getArguments();
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" does not have any arguments.");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public final Context requireContext()
  {
    Object localObject = getContext();
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" not attached to a context.");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public final FragmentManager requireFragmentManager()
  {
    return getParentFragmentManager();
  }
  
  public final Object requireHost()
  {
    Object localObject = getHost();
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" not attached to a host.");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public final Fragment requireParentFragment()
  {
    Object localObject = getParentFragment();
    if (localObject == null)
    {
      if (getContext() == null)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Fragment ");
        ((StringBuilder)localObject).append(this);
        ((StringBuilder)localObject).append(" is not attached to any Fragment or host");
        throw new IllegalStateException(((StringBuilder)localObject).toString());
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Fragment ");
      ((StringBuilder)localObject).append(this);
      ((StringBuilder)localObject).append(" is not a child Fragment, it is directly attached to ");
      ((StringBuilder)localObject).append(getContext());
      throw new IllegalStateException(((StringBuilder)localObject).toString());
    }
    return localObject;
  }
  
  public final View requireView()
  {
    Object localObject = getView();
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Fragment ");
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" did not return a View from onCreateView() or this was called before onCreateView().");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  void restoreChildFragmentState(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      paramBundle = paramBundle.getParcelable("android:support:fragments");
      if (paramBundle != null)
      {
        mChildFragmentManager.restoreSaveState(paramBundle);
        mChildFragmentManager.dispatchCreate();
      }
    }
  }
  
  final void restoreViewState(Bundle paramBundle)
  {
    if (mSavedViewState != null)
    {
      mView.restoreHierarchyState(mSavedViewState);
      mSavedViewState = null;
    }
    mCalled = false;
    onViewStateRestored(paramBundle);
    if (mCalled)
    {
      if (mView != null) {
        mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
      }
    }
    else
    {
      paramBundle = new StringBuilder();
      paramBundle.append("Fragment ");
      paramBundle.append(this);
      paramBundle.append(" did not call through to super.onViewStateRestored()");
      throw new SuperNotCalledException(paramBundle.toString());
    }
  }
  
  public void setAllowEnterTransitionOverlap(boolean paramBoolean)
  {
    ensureAnimationInfomAllowEnterTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  public void setAllowReturnTransitionOverlap(boolean paramBoolean)
  {
    ensureAnimationInfomAllowReturnTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  void setAnimatingAway(View paramView)
  {
    ensureAnimationInfomAnimatingAway = paramView;
  }
  
  void setAnimator(Animator paramAnimator)
  {
    ensureAnimationInfomAnimator = paramAnimator;
  }
  
  public void setArguments(Bundle paramBundle)
  {
    if ((mFragmentManager != null) && (isStateSaved())) {
      throw new IllegalStateException("Fragment already added and state has been saved");
    }
    mArguments = paramBundle;
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ensureAnimationInfomEnterTransitionCallback = paramSharedElementCallback;
  }
  
  public void setEnterTransition(Object paramObject)
  {
    ensureAnimationInfomEnterTransition = paramObject;
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ensureAnimationInfomExitTransitionCallback = paramSharedElementCallback;
  }
  
  public void setExitTransition(Object paramObject)
  {
    ensureAnimationInfomExitTransition = paramObject;
  }
  
  public void setHasOptionsMenu(boolean paramBoolean)
  {
    if (mHasMenu != paramBoolean)
    {
      mHasMenu = paramBoolean;
      if ((isAdded()) && (!isHidden())) {
        mHost.onSupportInvalidateOptionsMenu();
      }
    }
  }
  
  void setHideReplaced(boolean paramBoolean)
  {
    ensureAnimationInfomIsHideReplaced = paramBoolean;
  }
  
  public void setInitialSavedState(SavedState paramSavedState)
  {
    if (mFragmentManager == null)
    {
      if ((paramSavedState != null) && (mState != null)) {
        paramSavedState = mState;
      } else {
        paramSavedState = null;
      }
      mSavedFragmentState = paramSavedState;
      return;
    }
    throw new IllegalStateException("Fragment already added");
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    if (mMenuVisible != paramBoolean)
    {
      mMenuVisible = paramBoolean;
      if ((mHasMenu) && (isAdded()) && (!isHidden())) {
        mHost.onSupportInvalidateOptionsMenu();
      }
    }
  }
  
  void setNextAnim(int paramInt)
  {
    if ((mAnimationInfo == null) && (paramInt == 0)) {
      return;
    }
    ensureAnimationInfomNextAnim = paramInt;
  }
  
  void setNextTransition(int paramInt)
  {
    if ((mAnimationInfo == null) && (paramInt == 0)) {
      return;
    }
    ensureAnimationInfo();
    mAnimationInfo.mNextTransition = paramInt;
  }
  
  void setOnStartEnterTransitionListener(OnStartEnterTransitionListener paramOnStartEnterTransitionListener)
  {
    ensureAnimationInfo();
    if (paramOnStartEnterTransitionListener == mAnimationInfo.mStartEnterTransitionListener) {
      return;
    }
    if ((paramOnStartEnterTransitionListener != null) && (mAnimationInfo.mStartEnterTransitionListener != null))
    {
      paramOnStartEnterTransitionListener = new StringBuilder();
      paramOnStartEnterTransitionListener.append("Trying to set a replacement startPostponedEnterTransition on ");
      paramOnStartEnterTransitionListener.append(this);
      throw new IllegalStateException(paramOnStartEnterTransitionListener.toString());
    }
    if (mAnimationInfo.mEnterTransitionPostponed) {
      mAnimationInfo.mStartEnterTransitionListener = paramOnStartEnterTransitionListener;
    }
    if (paramOnStartEnterTransitionListener != null) {
      paramOnStartEnterTransitionListener.startListening();
    }
  }
  
  public void setReenterTransition(Object paramObject)
  {
    ensureAnimationInfomReenterTransition = paramObject;
  }
  
  public void setRetainInstance(boolean paramBoolean)
  {
    mRetainInstance = paramBoolean;
    if (mFragmentManager != null)
    {
      if (paramBoolean)
      {
        mFragmentManager.addRetainedFragment(this);
        return;
      }
      mFragmentManager.removeRetainedFragment(this);
      return;
    }
    mRetainInstanceChangedWhileDetached = true;
  }
  
  public void setReturnTransition(Object paramObject)
  {
    ensureAnimationInfomReturnTransition = paramObject;
  }
  
  public void setSharedElementEnterTransition(Object paramObject)
  {
    ensureAnimationInfomSharedElementEnterTransition = paramObject;
  }
  
  public void setSharedElementReturnTransition(Object paramObject)
  {
    ensureAnimationInfomSharedElementReturnTransition = paramObject;
  }
  
  void setStateAfterAnimating(int paramInt)
  {
    ensureAnimationInfomStateAfterAnimating = paramInt;
  }
  
  public void setTargetFragment(Fragment paramFragment, int paramInt)
  {
    FragmentManager localFragmentManager = mFragmentManager;
    if (paramFragment != null) {
      localObject = mFragmentManager;
    } else {
      localObject = null;
    }
    if ((localFragmentManager != null) && (localObject != null) && (localFragmentManager != localObject))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Fragment ");
      ((StringBuilder)localObject).append(paramFragment);
      ((StringBuilder)localObject).append(" must share the same FragmentManager to be set as a target fragment");
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    Object localObject = paramFragment;
    while (localObject != null) {
      if (localObject != this)
      {
        localObject = ((Fragment)localObject).getTargetFragment();
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Setting ");
        ((StringBuilder)localObject).append(paramFragment);
        ((StringBuilder)localObject).append(" as the target of ");
        ((StringBuilder)localObject).append(this);
        ((StringBuilder)localObject).append(" would create a target cycle");
        throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      }
    }
    if (paramFragment == null)
    {
      mTargetWho = null;
      mTarget = null;
    }
    else if ((mFragmentManager != null) && (mFragmentManager != null))
    {
      mTargetWho = mWho;
      mTarget = null;
    }
    else
    {
      mTargetWho = null;
      mTarget = paramFragment;
    }
    mTargetRequestCode = paramInt;
  }
  
  public void setUserVisibleHint(boolean paramBoolean)
  {
    if ((!mUserVisibleHint) && (paramBoolean) && (mState < 3) && (mFragmentManager != null) && (isAdded()) && (mIsCreated)) {
      mFragmentManager.performPendingDeferredStart(this);
    }
    mUserVisibleHint = paramBoolean;
    boolean bool;
    if ((mState < 3) && (!paramBoolean)) {
      bool = true;
    } else {
      bool = false;
    }
    mDeferStart = bool;
    if (mSavedFragmentState != null) {
      mSavedUserVisibleHint = Boolean.valueOf(paramBoolean);
    }
  }
  
  public boolean shouldShowRequestPermissionRationale(String paramString)
  {
    if (mHost != null) {
      return mHost.onShouldShowRequestPermissionRationale(paramString);
    }
    return false;
  }
  
  public void startActivity(Intent paramIntent)
  {
    startActivity(paramIntent, null);
  }
  
  public void startActivity(Intent paramIntent, Bundle paramBundle)
  {
    if (mHost != null)
    {
      mHost.onStartActivityFromFragment(this, paramIntent, -1, paramBundle);
      return;
    }
    paramIntent = new StringBuilder();
    paramIntent.append("Fragment ");
    paramIntent.append(this);
    paramIntent.append(" not attached to Activity");
    throw new IllegalStateException(paramIntent.toString());
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    startActivityForResult(paramIntent, paramInt, null);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if (mHost != null)
    {
      mHost.onStartActivityFromFragment(this, paramIntent, paramInt, paramBundle);
      return;
    }
    paramIntent = new StringBuilder();
    paramIntent.append("Fragment ");
    paramIntent.append(this);
    paramIntent.append(" not attached to Activity");
    throw new IllegalStateException(paramIntent.toString());
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (mHost != null)
    {
      mHost.onStartIntentSenderFromFragment(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      return;
    }
    paramIntentSender = new StringBuilder();
    paramIntentSender.append("Fragment ");
    paramIntentSender.append(this);
    paramIntentSender.append(" not attached to Activity");
    throw new IllegalStateException(paramIntentSender.toString());
  }
  
  public void startPostponedEnterTransition()
  {
    if ((mFragmentManager != null) && (mFragmentManager.mHost != null))
    {
      if (Looper.myLooper() != mFragmentManager.mHost.getHandler().getLooper())
      {
        mFragmentManager.mHost.getHandler().postAtFrontOfQueue(new Fragment.3(this));
        return;
      }
      callStartTransitionListener();
      return;
    }
    ensureAnimationInfomEnterTransitionPostponed = false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(getClass().getSimpleName());
    localStringBuilder.append("{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append("}");
    localStringBuilder.append(" (");
    localStringBuilder.append(mWho);
    localStringBuilder.append(")");
    if (mFragmentId != 0)
    {
      localStringBuilder.append(" id=0x");
      localStringBuilder.append(Integer.toHexString(mFragmentId));
    }
    if (mTag != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(mTag);
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void unregisterForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(null);
  }
  
  class AnimationInfo
  {
    Boolean mAllowEnterTransitionOverlap;
    Boolean mAllowReturnTransitionOverlap;
    View mAnimatingAway;
    Animator mAnimator;
    Object mEnterTransition = null;
    SharedElementCallback mEnterTransitionCallback = null;
    boolean mEnterTransitionPostponed;
    Object mExitTransition = null;
    SharedElementCallback mExitTransitionCallback = null;
    boolean mIsHideReplaced;
    int mNextAnim;
    int mNextTransition;
    Object mReenterTransition = Fragment.USE_DEFAULT_TRANSITION;
    Object mReturnTransition = Fragment.USE_DEFAULT_TRANSITION;
    Object mSharedElementEnterTransition = null;
    Object mSharedElementReturnTransition = Fragment.USE_DEFAULT_TRANSITION;
    Fragment.OnStartEnterTransitionListener mStartEnterTransitionListener;
    int mStateAfterAnimating;
    
    AnimationInfo() {}
  }
  
  public class InstantiationException
    extends RuntimeException
  {
    public InstantiationException(Exception paramException)
    {
      super(paramException);
    }
  }
  
  abstract interface OnStartEnterTransitionListener
  {
    public abstract void onStartEnterTransition();
    
    public abstract void startListening();
  }
  
  @SuppressLint({"BanParcelableUsage"})
  public class SavedState
    implements Parcelable
  {
    @NonNull
    public static final Parcelable.Creator<androidx.fragment.app.Fragment.SavedState> CREATOR = new Parcelable.ClassLoaderCreator()
    {
      public Fragment.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Fragment.SavedState(paramAnonymousParcel, null);
      }
      
      public Fragment.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new Fragment.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public Fragment.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Fragment.SavedState[paramAnonymousInt];
      }
    };
    final Bundle mState;
    
    SavedState()
    {
      mState = this$1;
    }
    
    SavedState(ClassLoader paramClassLoader)
    {
      mState = this$1.readBundle();
      if ((paramClassLoader != null) && (mState != null)) {
        mState.setClassLoader(paramClassLoader);
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeBundle(mState);
    }
  }
}
