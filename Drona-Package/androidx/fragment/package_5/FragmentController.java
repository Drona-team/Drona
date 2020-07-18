package androidx.fragment.package_5;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.collection.SimpleArrayMap;
import androidx.core.util.Preconditions;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FragmentController
{
  private final androidx.fragment.app.FragmentHostCallback<?> mHost;
  
  private FragmentController(FragmentHostCallback paramFragmentHostCallback)
  {
    mHost = paramFragmentHostCallback;
  }
  
  public static FragmentController createController(FragmentHostCallback paramFragmentHostCallback)
  {
    return new FragmentController((FragmentHostCallback)Preconditions.checkNotNull(paramFragmentHostCallback, "callbacks == null"));
  }
  
  public void attachHost(Fragment paramFragment)
  {
    mHost.mFragmentManager.attachController(mHost, mHost, paramFragment);
  }
  
  public void dispatchActivityCreated()
  {
    mHost.mFragmentManager.dispatchActivityCreated();
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    mHost.mFragmentManager.dispatchConfigurationChanged(paramConfiguration);
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    return mHost.mFragmentManager.dispatchContextItemSelected(paramMenuItem);
  }
  
  public void dispatchCreate()
  {
    mHost.mFragmentManager.dispatchCreate();
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    return mHost.mFragmentManager.dispatchCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void dispatchDestroy()
  {
    mHost.mFragmentManager.dispatchDestroy();
  }
  
  public void dispatchDestroyView()
  {
    mHost.mFragmentManager.dispatchDestroyView();
  }
  
  public void dispatchLowMemory()
  {
    mHost.mFragmentManager.dispatchLowMemory();
  }
  
  public void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    mHost.mFragmentManager.dispatchMultiWindowModeChanged(paramBoolean);
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    return mHost.mFragmentManager.dispatchOptionsItemSelected(paramMenuItem);
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    mHost.mFragmentManager.dispatchOptionsMenuClosed(paramMenu);
  }
  
  public void dispatchPause()
  {
    mHost.mFragmentManager.dispatchPause();
  }
  
  public void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    mHost.mFragmentManager.dispatchPictureInPictureModeChanged(paramBoolean);
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    return mHost.mFragmentManager.dispatchPrepareOptionsMenu(paramMenu);
  }
  
  public void dispatchReallyStop() {}
  
  public void dispatchResume()
  {
    mHost.mFragmentManager.dispatchResume();
  }
  
  public void dispatchStart()
  {
    mHost.mFragmentManager.dispatchStart();
  }
  
  public void dispatchStop()
  {
    mHost.mFragmentManager.dispatchStop();
  }
  
  public void doLoaderDestroy() {}
  
  public void doLoaderRetain() {}
  
  public void doLoaderStart() {}
  
  public void doLoaderStop(boolean paramBoolean) {}
  
  public void dumpLoaders(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public boolean execPendingActions()
  {
    return mHost.mFragmentManager.execPendingActions(true);
  }
  
  public Fragment findFragmentByWho(String paramString)
  {
    return mHost.mFragmentManager.findFragmentByWho(paramString);
  }
  
  public List getActiveFragments(List paramList)
  {
    return mHost.mFragmentManager.getActiveFragments();
  }
  
  public int getActiveFragmentsCount()
  {
    return mHost.mFragmentManager.getActiveFragmentCount();
  }
  
  public FragmentManager getSupportFragmentManager()
  {
    return mHost.mFragmentManager;
  }
  
  public LoaderManager getSupportLoaderManager()
  {
    throw new UnsupportedOperationException("Loaders are managed separately from FragmentController, use LoaderManager.getInstance() to obtain a LoaderManager.");
  }
  
  public void noteStateNotSaved()
  {
    mHost.mFragmentManager.noteStateNotSaved();
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return mHost.mFragmentManager.getLayoutInflaterFactory().onCreateView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  public void reportLoaderStart() {}
  
  public void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    mHost.mFragmentManager.restoreAllState(paramParcelable, paramFragmentManagerNonConfig);
  }
  
  public void restoreAllState(Parcelable paramParcelable, List paramList)
  {
    mHost.mFragmentManager.restoreAllState(paramParcelable, new FragmentManagerNonConfig(paramList, null, null));
  }
  
  public void restoreLoaderNonConfig(SimpleArrayMap paramSimpleArrayMap) {}
  
  public void restoreSaveState(Parcelable paramParcelable)
  {
    if ((mHost instanceof ViewModelStoreOwner))
    {
      mHost.mFragmentManager.restoreSaveState(paramParcelable);
      return;
    }
    throw new IllegalStateException("Your FragmentHostCallback must implement ViewModelStoreOwner to call restoreSaveState(). Call restoreAllState()  if you're still using retainNestedNonConfig().");
  }
  
  public SimpleArrayMap retainLoaderNonConfig()
  {
    return null;
  }
  
  public FragmentManagerNonConfig retainNestedNonConfig()
  {
    return mHost.mFragmentManager.retainNonConfig();
  }
  
  public List retainNonConfig()
  {
    FragmentManagerNonConfig localFragmentManagerNonConfig = mHost.mFragmentManager.retainNonConfig();
    if ((localFragmentManagerNonConfig != null) && (localFragmentManagerNonConfig.getFragments() != null)) {
      return new ArrayList(localFragmentManagerNonConfig.getFragments());
    }
    return null;
  }
  
  public Parcelable saveAllState()
  {
    return mHost.mFragmentManager.saveAllState();
  }
}
