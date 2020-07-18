package com.facebook.react;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.package_5.Fragment;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

public class ReactFragment
  extends Fragment
  implements PermissionAwareActivity
{
  private static final String ARG_COMPONENT_NAME = "arg_component_name";
  private static final String ARG_LAUNCH_OPTIONS = "arg_launch_options";
  @Nullable
  private PermissionListener mPermissionListener;
  private ReactDelegate mReactDelegate;
  
  public ReactFragment() {}
  
  private static ReactFragment newInstance(String paramString, Bundle paramBundle)
  {
    ReactFragment localReactFragment = new ReactFragment();
    Bundle localBundle = new Bundle();
    localBundle.putString("arg_component_name", paramString);
    localBundle.putBundle("arg_launch_options", paramBundle);
    localReactFragment.setArguments(localBundle);
    return localReactFragment;
  }
  
  public int checkPermission(String paramString, int paramInt1, int paramInt2)
  {
    return getActivity().checkPermission(paramString, paramInt1, paramInt2);
  }
  
  public int checkSelfPermission(String paramString)
  {
    return getActivity().checkSelfPermission(paramString);
  }
  
  protected ReactNativeHost getReactNativeHost()
  {
    return ((ReactApplication)getActivity().getApplication()).getReactNativeHost();
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    mReactDelegate.onActivityResult(paramInt1, paramInt2, paramIntent, false);
  }
  
  public boolean onBackPressed()
  {
    return mReactDelegate.onBackPressed();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Bundle localBundle = getArguments();
    paramBundle = null;
    if (localBundle != null)
    {
      paramBundle = getArguments().getString("arg_component_name");
      localBundle = getArguments().getBundle("arg_launch_options");
    }
    else
    {
      localBundle = null;
    }
    if (paramBundle != null)
    {
      mReactDelegate = new ReactDelegate(getActivity(), getReactNativeHost(), paramBundle, localBundle);
      return;
    }
    throw new IllegalStateException("Cannot loadApp if component name is null");
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    mReactDelegate.loadApp();
    return mReactDelegate.getReactRootView();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    mReactDelegate.onHostDestroy();
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return mReactDelegate.shouldShowDevMenuOrReload(paramInt, paramKeyEvent);
  }
  
  public void onPause()
  {
    super.onPause();
    mReactDelegate.onHostPause();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    if ((mPermissionListener != null) && (mPermissionListener.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt))) {
      mPermissionListener = null;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    mReactDelegate.onHostResume();
  }
  
  public void requestPermissions(String[] paramArrayOfString, int paramInt, PermissionListener paramPermissionListener)
  {
    mPermissionListener = paramPermissionListener;
    requestPermissions(paramArrayOfString, paramInt);
  }
  
  public static class Builder
  {
    String mComponentName = null;
    Bundle mLaunchOptions = null;
    
    public Builder() {}
    
    public ReactFragment build()
    {
      return ReactFragment.newInstance(mComponentName, mLaunchOptions);
    }
    
    public Builder setComponentName(String paramString)
    {
      mComponentName = paramString;
      return this;
    }
    
    public Builder setLaunchOptions(Bundle paramBundle)
    {
      mLaunchOptions = paramBundle;
      return this;
    }
  }
}
