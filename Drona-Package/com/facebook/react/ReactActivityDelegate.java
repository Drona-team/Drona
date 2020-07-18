package com.facebook.react;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;

public class ReactActivityDelegate
{
  @Nullable
  private final Activity mActivity;
  @Nullable
  private final String mMainComponentName;
  @Nullable
  private PermissionListener mPermissionListener;
  @Nullable
  private Callback mPermissionsCallback;
  private ReactDelegate mReactDelegate;
  
  public ReactActivityDelegate(Activity paramActivity, String paramString)
  {
    mActivity = paramActivity;
    mMainComponentName = paramString;
  }
  
  public ReactActivityDelegate(ReactActivity paramReactActivity, String paramString)
  {
    mActivity = paramReactActivity;
    mMainComponentName = paramString;
  }
  
  protected ReactRootView createRootView()
  {
    return new ReactRootView(getContext());
  }
  
  protected Context getContext()
  {
    return (Context)Assertions.assertNotNull(mActivity);
  }
  
  protected Bundle getLaunchOptions()
  {
    return null;
  }
  
  public String getMainComponentName()
  {
    return mMainComponentName;
  }
  
  protected Activity getPlainActivity()
  {
    return (Activity)getContext();
  }
  
  public ReactInstanceManager getReactInstanceManager()
  {
    return mReactDelegate.getReactInstanceManager();
  }
  
  protected ReactNativeHost getReactNativeHost()
  {
    return ((ReactApplication)getPlainActivity().getApplication()).getReactNativeHost();
  }
  
  protected void loadApp(String paramString)
  {
    mReactDelegate.loadApp(paramString);
    getPlainActivity().setContentView(mReactDelegate.getReactRootView());
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    mReactDelegate.onActivityResult(paramInt1, paramInt2, paramIntent, true);
  }
  
  public boolean onBackPressed()
  {
    return mReactDelegate.onBackPressed();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    paramBundle = getMainComponentName();
    mReactDelegate = new ReactDelegate(getPlainActivity(), getReactNativeHost(), paramBundle, getLaunchOptions())
    {
      protected ReactRootView createRootView()
      {
        return ReactActivityDelegate.this.createRootView();
      }
    };
    if (mMainComponentName != null) {
      loadApp(paramBundle);
    }
  }
  
  protected void onDestroy()
  {
    mReactDelegate.onHostDestroy();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((getReactNativeHost().hasInstance()) && (getReactNativeHost().getUseDeveloperSupport()) && (paramInt == 90))
    {
      paramKeyEvent.startTracking();
      return true;
    }
    return false;
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((getReactNativeHost().hasInstance()) && (getReactNativeHost().getUseDeveloperSupport()) && (paramInt == 90))
    {
      getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
      return true;
    }
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return mReactDelegate.shouldShowDevMenuOrReload(paramInt, paramKeyEvent);
  }
  
  public boolean onNewIntent(Intent paramIntent)
  {
    if (getReactNativeHost().hasInstance())
    {
      getReactNativeHost().getReactInstanceManager().onNewIntent(paramIntent);
      return true;
    }
    return false;
  }
  
  protected void onPause()
  {
    mReactDelegate.onHostPause();
  }
  
  public void onRequestPermissionsResult(final int paramInt, final String[] paramArrayOfString, final int[] paramArrayOfInt)
  {
    mPermissionsCallback = new Callback()
    {
      public void invoke(Object... paramAnonymousVarArgs)
      {
        if ((mPermissionListener != null) && (mPermissionListener.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt))) {
          ReactActivityDelegate.access$002(ReactActivityDelegate.this, null);
        }
      }
    };
  }
  
  protected void onResume()
  {
    mReactDelegate.onHostResume();
    if (mPermissionsCallback != null)
    {
      mPermissionsCallback.invoke(new Object[0]);
      mPermissionsCallback = null;
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    if (getReactNativeHost().hasInstance()) {
      getReactNativeHost().getReactInstanceManager().onWindowFocusChange(paramBoolean);
    }
  }
  
  public void requestPermissions(String[] paramArrayOfString, int paramInt, PermissionListener paramPermissionListener)
  {
    mPermissionListener = paramPermissionListener;
    getPlainActivity().requestPermissions(paramArrayOfString, paramInt);
  }
}
