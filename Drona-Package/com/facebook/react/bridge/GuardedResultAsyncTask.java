package com.facebook.react.bridge;

import android.os.AsyncTask;

public abstract class GuardedResultAsyncTask<Result>
  extends AsyncTask<Void, Void, Result>
{
  private final NativeModuleCallExceptionHandler mExceptionHandler;
  
  protected GuardedResultAsyncTask(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    mExceptionHandler = paramNativeModuleCallExceptionHandler;
  }
  
  protected GuardedResultAsyncTask(ReactContext paramReactContext)
  {
    this(paramReactContext.getExceptionHandler());
  }
  
  protected final Object doInBackground(Void... paramVarArgs)
  {
    try
    {
      paramVarArgs = doInBackgroundGuarded();
      return paramVarArgs;
    }
    catch (RuntimeException paramVarArgs)
    {
      mExceptionHandler.handleException(paramVarArgs);
      throw paramVarArgs;
    }
  }
  
  protected abstract Object doInBackgroundGuarded();
  
  protected final void onPostExecute(Object paramObject)
  {
    try
    {
      onPostExecuteGuarded(paramObject);
      return;
    }
    catch (RuntimeException paramObject)
    {
      mExceptionHandler.handleException(paramObject);
    }
  }
  
  protected abstract void onPostExecuteGuarded(Object paramObject);
}
