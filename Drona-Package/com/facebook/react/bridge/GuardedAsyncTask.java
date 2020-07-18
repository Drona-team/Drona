package com.facebook.react.bridge;

import android.os.AsyncTask;

public abstract class GuardedAsyncTask<Params, Progress>
  extends AsyncTask<Params, Progress, Void>
{
  private final NativeModuleCallExceptionHandler mExceptionHandler;
  
  protected GuardedAsyncTask(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    mExceptionHandler = paramNativeModuleCallExceptionHandler;
  }
  
  protected GuardedAsyncTask(ReactContext paramReactContext)
  {
    this(paramReactContext.getExceptionHandler());
  }
  
  protected final Void doInBackground(Object... paramVarArgs)
  {
    try
    {
      doInBackgroundGuarded(paramVarArgs);
    }
    catch (RuntimeException paramVarArgs)
    {
      mExceptionHandler.handleException(paramVarArgs);
    }
    return null;
  }
  
  protected abstract void doInBackgroundGuarded(Object... paramVarArgs);
}
