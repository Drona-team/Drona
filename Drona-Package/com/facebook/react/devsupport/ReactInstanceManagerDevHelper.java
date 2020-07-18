package com.facebook.react.devsupport;

import android.app.Activity;
import com.facebook.react.bridge.JavaJSExecutor.Factory;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.NativeDeltaClient;

public abstract interface ReactInstanceManagerDevHelper
{
  public abstract Activity getCurrentActivity();
  
  public abstract JavaScriptExecutorFactory getJavaScriptExecutorFactory();
  
  public abstract void onJSBundleLoadedFromServer(NativeDeltaClient paramNativeDeltaClient);
  
  public abstract void onReloadWithJSDebugger(JavaJSExecutor.Factory paramFactory);
  
  public abstract void toggleElementInspector();
}
