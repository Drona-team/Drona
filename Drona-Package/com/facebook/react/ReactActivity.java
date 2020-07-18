package com.facebook.react;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.package_5.FragmentActivity;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

public abstract class ReactActivity
  extends AppCompatActivity
  implements DefaultHardwareBackBtnHandler, PermissionAwareActivity
{
  private final ReactActivityDelegate mDelegate = createReactActivityDelegate();
  
  protected ReactActivity() {}
  
  protected ReactActivityDelegate createReactActivityDelegate()
  {
    return new ReactActivityDelegate(this, getMainComponentName());
  }
  
  protected String getMainComponentName()
  {
    return null;
  }
  
  protected final ReactInstanceManager getReactInstanceManager()
  {
    return mDelegate.getReactInstanceManager();
  }
  
  protected final ReactNativeHost getReactNativeHost()
  {
    return mDelegate.getReactNativeHost();
  }
  
  public void invokeDefaultOnBackPressed()
  {
    super.onBackPressed();
  }
  
  protected final void loadApp(String paramString)
  {
    mDelegate.loadApp(paramString);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    mDelegate.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onBackPressed()
  {
    if (!mDelegate.onBackPressed()) {
      super.onBackPressed();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    mDelegate.onCreate(paramBundle);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    mDelegate.onDestroy();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return (mDelegate.onKeyDown(paramInt, paramKeyEvent)) || (super.onKeyDown(paramInt, paramKeyEvent));
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return (mDelegate.onKeyLongPress(paramInt, paramKeyEvent)) || (super.onKeyLongPress(paramInt, paramKeyEvent));
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return (mDelegate.onKeyUp(paramInt, paramKeyEvent)) || (super.onKeyUp(paramInt, paramKeyEvent));
  }
  
  public void onNewIntent(Intent paramIntent)
  {
    if (!mDelegate.onNewIntent(paramIntent)) {
      super.onNewIntent(paramIntent);
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    mDelegate.onPause();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    mDelegate.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
  }
  
  protected void onResume()
  {
    super.onResume();
    mDelegate.onResume();
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    mDelegate.onWindowFocusChanged(paramBoolean);
  }
  
  public void requestPermissions(String[] paramArrayOfString, int paramInt, PermissionListener paramPermissionListener)
  {
    mDelegate.requestPermissions(paramArrayOfString, paramInt, paramPermissionListener);
  }
}
