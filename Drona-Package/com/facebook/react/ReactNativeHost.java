package com.facebook.react;

import android.app.Application;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.uimanager.UIImplementationProvider;
import java.util.Iterator;
import java.util.List;

public abstract class ReactNativeHost
{
  private final Application mApplication;
  @Nullable
  private ReactInstanceManager mReactInstanceManager;
  
  protected ReactNativeHost(Application paramApplication)
  {
    mApplication = paramApplication;
  }
  
  public void clear()
  {
    if (mReactInstanceManager != null)
    {
      mReactInstanceManager.destroy();
      mReactInstanceManager = null;
    }
  }
  
  protected ReactInstanceManager createReactInstanceManager()
  {
    ReactMarker.logMarker(ReactMarkerConstants.BUILD_REACT_INSTANCE_MANAGER_START);
    Object localObject1 = ReactInstanceManager.builder().setApplication(mApplication).setJSMainModulePath(getJSMainModuleName()).setUseDeveloperSupport(getUseDeveloperSupport()).setRedBoxHandler(getRedBoxHandler()).setJavaScriptExecutorFactory(getJavaScriptExecutorFactory()).setUIImplementationProvider(getUIImplementationProvider()).setJSIModulesPackage(getJSIModulePackage()).setInitialLifecycleState(LifecycleState.BEFORE_CREATE);
    Object localObject2 = getPackages().iterator();
    while (((Iterator)localObject2).hasNext()) {
      ((ReactInstanceManagerBuilder)localObject1).addPackage((ReactPackage)((Iterator)localObject2).next());
    }
    localObject2 = getJSBundleFile();
    if (localObject2 != null) {
      ((ReactInstanceManagerBuilder)localObject1).setJSBundleFile((String)localObject2);
    } else {
      ((ReactInstanceManagerBuilder)localObject1).setBundleAssetName((String)Assertions.assertNotNull(getBundleAssetName()));
    }
    localObject1 = ((ReactInstanceManagerBuilder)localObject1).build();
    ReactMarker.logMarker(ReactMarkerConstants.BUILD_REACT_INSTANCE_MANAGER_END);
    return localObject1;
  }
  
  protected final Application getApplication()
  {
    return mApplication;
  }
  
  protected String getBundleAssetName()
  {
    return "index.android.bundle";
  }
  
  protected String getJSBundleFile()
  {
    return null;
  }
  
  protected JSIModulePackage getJSIModulePackage()
  {
    return null;
  }
  
  protected String getJSMainModuleName()
  {
    return "index.android";
  }
  
  protected JavaScriptExecutorFactory getJavaScriptExecutorFactory()
  {
    return null;
  }
  
  protected abstract List getPackages();
  
  public ReactInstanceManager getReactInstanceManager()
  {
    if (mReactInstanceManager == null)
    {
      ReactMarker.logMarker(ReactMarkerConstants.GET_REACT_INSTANCE_MANAGER_START);
      mReactInstanceManager = createReactInstanceManager();
      ReactMarker.logMarker(ReactMarkerConstants.GET_REACT_INSTANCE_MANAGER_END);
    }
    return mReactInstanceManager;
  }
  
  protected RedBoxHandler getRedBoxHandler()
  {
    return null;
  }
  
  protected UIImplementationProvider getUIImplementationProvider()
  {
    return new UIImplementationProvider();
  }
  
  public abstract boolean getUseDeveloperSupport();
  
  public boolean hasInstance()
  {
    return mReactInstanceManager != null;
  }
}
