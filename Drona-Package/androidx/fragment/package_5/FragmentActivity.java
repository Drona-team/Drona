package androidx.fragment.package_5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.collection.SparseArrayCompat;
import androidx.core.package_4.ActivityCompat;
import androidx.core.package_4.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.package_4.ActivityCompat.PermissionCompatDelegate;
import androidx.core.package_4.ActivityCompat.RequestPermissionsRequestCodeValidator;
import androidx.core.package_4.SharedElementCallback;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

public class FragmentActivity
  extends ComponentActivity
  implements ActivityCompat.OnRequestPermissionsResultCallback, ActivityCompat.RequestPermissionsRequestCodeValidator
{
  static final String ALLOCATED_REQUEST_INDICIES_TAG = "android:support:request_indicies";
  static final String FRAGMENTS_TAG = "android:support:fragments";
  static final int MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS = 65534;
  static final String NEXT_CANDIDATE_REQUEST_INDEX_TAG = "android:support:next_request_index";
  static final String REQUEST_FRAGMENT_WHO_TAG = "android:support:request_fragment_who";
  private static final String TAG = "FragmentActivity";
  boolean mCreated;
  final LifecycleRegistry mFragmentLifecycleRegistry = new LifecycleRegistry(this);
  final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
  int mNextCandidateRequestIndex;
  SparseArrayCompat<String> mPendingFragmentActivityResults;
  boolean mRequestedPermissionsFromFragment;
  boolean mResumed;
  boolean mStartedActivityFromFragment;
  boolean mStartedIntentSenderFromFragment;
  boolean mStopped = true;
  
  public FragmentActivity() {}
  
  public FragmentActivity(int paramInt)
  {
    super(paramInt);
  }
  
  private int allocateRequestIndex(Fragment paramFragment)
  {
    if (mPendingFragmentActivityResults.size() < 65534)
    {
      while (mPendingFragmentActivityResults.indexOfKey(mNextCandidateRequestIndex) >= 0) {
        mNextCandidateRequestIndex = ((mNextCandidateRequestIndex + 1) % 65534);
      }
      int i = mNextCandidateRequestIndex;
      mPendingFragmentActivityResults.put(i, mWho);
      mNextCandidateRequestIndex = ((mNextCandidateRequestIndex + 1) % 65534);
      return i;
    }
    throw new IllegalStateException("Too many pending Fragment activity results.");
  }
  
  static void checkForValidRequestCode(int paramInt)
  {
    if ((paramInt & 0xFFFF0000) == 0) {
      return;
    }
    throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
  }
  
  private void markFragmentsCreated()
  {
    while (markState(getSupportFragmentManager(), Lifecycle.State.CREATED)) {}
  }
  
  private static boolean markState(FragmentManager paramFragmentManager, Lifecycle.State paramState)
  {
    paramFragmentManager = paramFragmentManager.getFragments().iterator();
    boolean bool1 = false;
    while (paramFragmentManager.hasNext())
    {
      Fragment localFragment = (Fragment)paramFragmentManager.next();
      if (localFragment != null)
      {
        boolean bool2 = bool1;
        if (localFragment.getHost() != null) {
          bool2 = bool1 | markState(localFragment.getChildFragmentManager(), paramState);
        }
        bool1 = bool2;
        if (localFragment.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
        {
          mLifecycleRegistry.setCurrentState(paramState);
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  final View dispatchFragmentsOnCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return mFragments.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Local FragmentActivity ");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(" State:");
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("  ");
    localObject = ((StringBuilder)localObject).toString();
    paramPrintWriter.print((String)localObject);
    paramPrintWriter.print("mCreated=");
    paramPrintWriter.print(mCreated);
    paramPrintWriter.print(" mResumed=");
    paramPrintWriter.print(mResumed);
    paramPrintWriter.print(" mStopped=");
    paramPrintWriter.print(mStopped);
    if (getApplication() != null) {
      LoaderManager.getInstance(this).dump((String)localObject, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    mFragments.getSupportFragmentManager().dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public FragmentManager getSupportFragmentManager()
  {
    return mFragments.getSupportFragmentManager();
  }
  
  public LoaderManager getSupportLoaderManager()
  {
    return LoaderManager.getInstance(this);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    mFragments.noteStateNotSaved();
    int i = paramInt1 >> 16;
    if (i != 0)
    {
      i -= 1;
      localObject = (String)mPendingFragmentActivityResults.get(i);
      mPendingFragmentActivityResults.remove(i);
      if (localObject == null)
      {
        Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
        return;
      }
      Fragment localFragment = mFragments.findFragmentByWho((String)localObject);
      if (localFragment == null)
      {
        paramIntent = new StringBuilder();
        paramIntent.append("Activity result no fragment exists for who: ");
        paramIntent.append((String)localObject);
        Log.w("FragmentActivity", paramIntent.toString());
        return;
      }
      localFragment.onActivityResult(paramInt1 & 0xFFFF, paramInt2, paramIntent);
      return;
    }
    Object localObject = ActivityCompat.getPermissionCompatDelegate();
    if ((localObject != null) && (((ActivityCompat.PermissionCompatDelegate)localObject).onActivityResult(this, paramInt1, paramInt2, paramIntent))) {
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    mFragments.noteStateNotSaved();
    mFragments.dispatchConfigurationChanged(paramConfiguration);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    mFragments.attachHost(null);
    if (paramBundle != null)
    {
      Object localObject = paramBundle.getParcelable("android:support:fragments");
      mFragments.restoreSaveState((Parcelable)localObject);
      if (paramBundle.containsKey("android:support:next_request_index"))
      {
        mNextCandidateRequestIndex = paramBundle.getInt("android:support:next_request_index");
        localObject = paramBundle.getIntArray("android:support:request_indicies");
        String[] arrayOfString = paramBundle.getStringArray("android:support:request_fragment_who");
        int i;
        if ((localObject != null) && (arrayOfString != null) && (localObject.length == arrayOfString.length))
        {
          mPendingFragmentActivityResults = new SparseArrayCompat(localObject.length);
          i = 0;
        }
        while (i < localObject.length)
        {
          mPendingFragmentActivityResults.put(localObject[i], arrayOfString[i]);
          i += 1;
          continue;
          Log.w("FragmentActivity", "Invalid requestCode mapping in savedInstanceState.");
        }
      }
    }
    if (mPendingFragmentActivityResults == null)
    {
      mPendingFragmentActivityResults = new SparseArrayCompat();
      mNextCandidateRequestIndex = 0;
    }
    super.onCreate(paramBundle);
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    mFragments.dispatchCreate();
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0) {
      return super.onCreatePanelMenu(paramInt, paramMenu) | mFragments.dispatchCreateOptionsMenu(paramMenu, getMenuInflater());
    }
    return super.onCreatePanelMenu(paramInt, paramMenu);
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    View localView = dispatchFragmentsOnCreateView(paramView, paramString, paramContext, paramAttributeSet);
    if (localView == null) {
      return super.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
    }
    return localView;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    View localView2 = dispatchFragmentsOnCreateView(null, paramString, paramContext, paramAttributeSet);
    View localView1 = localView2;
    if (localView2 == null) {
      localView1 = super.onCreateView(paramString, paramContext, paramAttributeSet);
    }
    return localView1;
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    mFragments.dispatchDestroy();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    mFragments.dispatchLowMemory();
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    if (super.onMenuItemSelected(paramInt, paramMenuItem)) {
      return true;
    }
    if (paramInt != 0)
    {
      if (paramInt != 6) {
        return false;
      }
      return mFragments.dispatchContextItemSelected(paramMenuItem);
    }
    return mFragments.dispatchOptionsItemSelected(paramMenuItem);
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean)
  {
    mFragments.dispatchMultiWindowModeChanged(paramBoolean);
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    mFragments.noteStateNotSaved();
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0) {
      mFragments.dispatchOptionsMenuClosed(paramMenu);
    }
    super.onPanelClosed(paramInt, paramMenu);
  }
  
  protected void onPause()
  {
    super.onPause();
    mResumed = false;
    mFragments.dispatchPause();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
  }
  
  public void onPictureInPictureModeChanged(boolean paramBoolean)
  {
    mFragments.dispatchPictureInPictureModeChanged(paramBoolean);
  }
  
  protected void onPostResume()
  {
    super.onPostResume();
    onResumeFragments();
  }
  
  protected boolean onPrepareOptionsPanel(View paramView, Menu paramMenu)
  {
    return super.onPreparePanel(0, paramView, paramMenu);
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    if (paramInt == 0) {
      return onPrepareOptionsPanel(paramView, paramMenu) | mFragments.dispatchPrepareOptionsMenu(paramMenu);
    }
    return super.onPreparePanel(paramInt, paramView, paramMenu);
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    mFragments.noteStateNotSaved();
    int i = paramInt >> 16 & 0xFFFF;
    if (i != 0)
    {
      i -= 1;
      String str = (String)mPendingFragmentActivityResults.get(i);
      mPendingFragmentActivityResults.remove(i);
      if (str == null)
      {
        Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
        return;
      }
      Fragment localFragment = mFragments.findFragmentByWho(str);
      if (localFragment == null)
      {
        paramArrayOfString = new StringBuilder();
        paramArrayOfString.append("Activity result no fragment exists for who: ");
        paramArrayOfString.append(str);
        Log.w("FragmentActivity", paramArrayOfString.toString());
        return;
      }
      localFragment.onRequestPermissionsResult(paramInt & 0xFFFF, paramArrayOfString, paramArrayOfInt);
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    mResumed = true;
    mFragments.noteStateNotSaved();
    mFragments.execPendingActions();
  }
  
  protected void onResumeFragments()
  {
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    mFragments.dispatchResume();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    markFragmentsCreated();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    Object localObject = mFragments.saveAllState();
    if (localObject != null) {
      paramBundle.putParcelable("android:support:fragments", (Parcelable)localObject);
    }
    if (mPendingFragmentActivityResults.size() > 0)
    {
      paramBundle.putInt("android:support:next_request_index", mNextCandidateRequestIndex);
      localObject = new int[mPendingFragmentActivityResults.size()];
      String[] arrayOfString = new String[mPendingFragmentActivityResults.size()];
      int i = 0;
      while (i < mPendingFragmentActivityResults.size())
      {
        localObject[i] = mPendingFragmentActivityResults.keyAt(i);
        arrayOfString[i] = ((String)mPendingFragmentActivityResults.valueAt(i));
        i += 1;
      }
      paramBundle.putIntArray("android:support:request_indicies", (int[])localObject);
      paramBundle.putStringArray("android:support:request_fragment_who", arrayOfString);
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    mStopped = false;
    if (!mCreated)
    {
      mCreated = true;
      mFragments.dispatchActivityCreated();
    }
    mFragments.noteStateNotSaved();
    mFragments.execPendingActions();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    mFragments.dispatchStart();
  }
  
  public void onStateNotSaved()
  {
    mFragments.noteStateNotSaved();
  }
  
  protected void onStop()
  {
    super.onStop();
    mStopped = true;
    markFragmentsCreated();
    mFragments.dispatchStop();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
  }
  
  void requestPermissionsFromFragment(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
  {
    if (paramInt == -1)
    {
      ActivityCompat.requestPermissions(this, paramArrayOfString, paramInt);
      return;
    }
    checkForValidRequestCode(paramInt);
    try
    {
      mRequestedPermissionsFromFragment = true;
      int i = allocateRequestIndex(paramFragment);
      ActivityCompat.requestPermissions(this, paramArrayOfString, (i + 1 << 16) + (paramInt & 0xFFFF));
      mRequestedPermissionsFromFragment = false;
      return;
    }
    catch (Throwable paramFragment)
    {
      mRequestedPermissionsFromFragment = false;
      throw paramFragment;
    }
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setEnterSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setExitSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if ((!mStartedActivityFromFragment) && (paramInt != -1)) {
      checkForValidRequestCode(paramInt);
    }
    super.startActivityForResult(paramIntent, paramInt);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if ((!mStartedActivityFromFragment) && (paramInt != -1)) {
      checkForValidRequestCode(paramInt);
    }
    super.startActivityForResult(paramIntent, paramInt, paramBundle);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    startActivityFromFragment(paramFragment, paramIntent, paramInt, null);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    mStartedActivityFromFragment = true;
    if (paramInt == -1)
    {
      try
      {
        ActivityCompat.startActivityForResult(this, paramIntent, -1, paramBundle);
        mStartedActivityFromFragment = false;
        return;
      }
      catch (Throwable paramFragment) {}
    }
    else
    {
      checkForValidRequestCode(paramInt);
      int i = allocateRequestIndex(paramFragment);
      ActivityCompat.startActivityForResult(this, paramIntent, (i + 1 << 16) + (paramInt & 0xFFFF), paramBundle);
      mStartedActivityFromFragment = false;
      return;
    }
    mStartedActivityFromFragment = false;
    throw paramFragment;
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4)
    throws IntentSender.SendIntentException
  {
    if ((!mStartedIntentSenderFromFragment) && (paramInt1 != -1)) {
      checkForValidRequestCode(paramInt1);
    }
    super.startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4);
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if ((!mStartedIntentSenderFromFragment) && (paramInt1 != -1)) {
      checkForValidRequestCode(paramInt1);
    }
    super.startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
  }
  
  public void startIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    mStartedIntentSenderFromFragment = true;
    if (paramInt1 == -1)
    {
      try
      {
        ActivityCompat.startIntentSenderForResult(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
        mStartedIntentSenderFromFragment = false;
        return;
      }
      catch (Throwable paramFragment) {}
    }
    else
    {
      checkForValidRequestCode(paramInt1);
      int i = allocateRequestIndex(paramFragment);
      ActivityCompat.startIntentSenderForResult(this, paramIntentSender, (i + 1 << 16) + (paramInt1 & 0xFFFF), paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      mStartedIntentSenderFromFragment = false;
      return;
    }
    mStartedIntentSenderFromFragment = false;
    throw paramFragment;
  }
  
  public void supportFinishAfterTransition()
  {
    ActivityCompat.finishAfterTransition(this);
  }
  
  public void supportInvalidateOptionsMenu()
  {
    invalidateOptionsMenu();
  }
  
  public void supportPostponeEnterTransition()
  {
    ActivityCompat.postponeEnterTransition(this);
  }
  
  public void supportStartPostponedEnterTransition()
  {
    ActivityCompat.startPostponedEnterTransition(this);
  }
  
  public final void validateRequestPermissionsRequestCode(int paramInt)
  {
    if ((!mRequestedPermissionsFromFragment) && (paramInt != -1)) {
      checkForValidRequestCode(paramInt);
    }
  }
  
  class HostCallbacks
    extends androidx.fragment.app.FragmentHostCallback<androidx.fragment.app.FragmentActivity>
    implements ViewModelStoreOwner, OnBackPressedDispatcherOwner
  {
    public HostCallbacks()
    {
      super();
    }
    
    public Lifecycle getLifecycle()
    {
      return mFragmentLifecycleRegistry;
    }
    
    public OnBackPressedDispatcher getOnBackPressedDispatcher()
    {
      return FragmentActivity.this.getOnBackPressedDispatcher();
    }
    
    public ViewModelStore getViewModelStore()
    {
      return FragmentActivity.this.getViewModelStore();
    }
    
    public void onAttachFragment(Fragment paramFragment)
    {
      FragmentActivity.this.onAttachFragment(paramFragment);
    }
    
    public void onDump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public View onFindViewById(int paramInt)
    {
      return findViewById(paramInt);
    }
    
    public FragmentActivity onGetHost()
    {
      return FragmentActivity.this;
    }
    
    public LayoutInflater onGetLayoutInflater()
    {
      return getLayoutInflater().cloneInContext(FragmentActivity.this);
    }
    
    public int onGetWindowAnimations()
    {
      Window localWindow = getWindow();
      if (localWindow == null) {
        return 0;
      }
      return getAttributeswindowAnimations;
    }
    
    public boolean onHasView()
    {
      Window localWindow = getWindow();
      return (localWindow != null) && (localWindow.peekDecorView() != null);
    }
    
    public boolean onHasWindowAnimations()
    {
      return getWindow() != null;
    }
    
    public void onRequestPermissionsFromFragment(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
    {
      requestPermissionsFromFragment(paramFragment, paramArrayOfString, paramInt);
    }
    
    public boolean onShouldSaveFragmentState(Fragment paramFragment)
    {
      return isFinishing() ^ true;
    }
    
    public boolean onShouldShowRequestPermissionRationale(String paramString)
    {
      return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, paramString);
    }
    
    public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
    {
      startActivityFromFragment(paramFragment, paramIntent, paramInt);
    }
    
    public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
    {
      startActivityFromFragment(paramFragment, paramIntent, paramInt, paramBundle);
    }
    
    public void onStartIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
      throws IntentSender.SendIntentException
    {
      startIntentSenderFromFragment(paramFragment, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
    }
    
    public void onSupportInvalidateOptionsMenu()
    {
      supportInvalidateOptionsMenu();
    }
  }
}
