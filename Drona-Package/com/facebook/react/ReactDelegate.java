package com.facebook.react;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

public class ReactDelegate
{
  private final Activity mActivity;
  @Nullable
  private DoubleTapReloadRecognizer mDoubleTapReloadRecognizer;
  @Nullable
  private Bundle mLaunchOptions;
  @Nullable
  private final String mMainComponentName;
  private ReactNativeHost mReactNativeHost;
  private ReactRootView mReactRootView;
  
  public ReactDelegate(Activity paramActivity, ReactNativeHost paramReactNativeHost, String paramString, Bundle paramBundle)
  {
    mActivity = paramActivity;
    mMainComponentName = paramString;
    mLaunchOptions = paramBundle;
    mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    mReactNativeHost = paramReactNativeHost;
  }
  
  private ReactNativeHost getReactNativeHost()
  {
    return mReactNativeHost;
  }
  
  protected ReactRootView createRootView()
  {
    return new ReactRootView(mActivity);
  }
  
  public ReactInstanceManager getReactInstanceManager()
  {
    return getReactNativeHost().getReactInstanceManager();
  }
  
  public ReactRootView getReactRootView()
  {
    return mReactRootView;
  }
  
  public void loadApp()
  {
    loadApp(mMainComponentName);
  }
  
  public void loadApp(String paramString)
  {
    if (mReactRootView == null)
    {
      mReactRootView = createRootView();
      mReactRootView.startReactApplication(getReactNativeHost().getReactInstanceManager(), paramString, mLaunchOptions);
      return;
    }
    throw new IllegalStateException("Cannot loadApp while app is already running.");
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent, boolean paramBoolean)
  {
    if ((getReactNativeHost().hasInstance()) && (paramBoolean)) {
      getReactNativeHost().getReactInstanceManager().onActivityResult(mActivity, paramInt1, paramInt2, paramIntent);
    }
  }
  
  public boolean onBackPressed()
  {
    if (getReactNativeHost().hasInstance())
    {
      getReactNativeHost().getReactInstanceManager().onBackPressed();
      return true;
    }
    return false;
  }
  
  public void onHostDestroy()
  {
    if (mReactRootView != null)
    {
      mReactRootView.unmountReactApplication();
      mReactRootView = null;
    }
    if (getReactNativeHost().hasInstance()) {
      getReactNativeHost().getReactInstanceManager().onHostDestroy(mActivity);
    }
  }
  
  public void onHostPause()
  {
    if (getReactNativeHost().hasInstance()) {
      getReactNativeHost().getReactInstanceManager().onHostPause(mActivity);
    }
  }
  
  public void onHostResume()
  {
    if (getReactNativeHost().hasInstance())
    {
      if ((mActivity instanceof DefaultHardwareBackBtnHandler))
      {
        getReactNativeHost().getReactInstanceManager().onHostResume(mActivity, (DefaultHardwareBackBtnHandler)mActivity);
        return;
      }
      throw new ClassCastException("Host Activity does not implement DefaultHardwareBackBtnHandler");
    }
  }
  
  public boolean shouldShowDevMenuOrReload(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((getReactNativeHost().hasInstance()) && (getReactNativeHost().getUseDeveloperSupport()))
    {
      if (paramInt == 82)
      {
        getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
        return true;
      }
      if (((DoubleTapReloadRecognizer)Assertions.assertNotNull(mDoubleTapReloadRecognizer)).didDoubleTapR(paramInt, mActivity.getCurrentFocus()))
      {
        getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
        return true;
      }
    }
    return false;
  }
}
