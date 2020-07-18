package com.facebook.react.bridge;

public abstract class GuardedRunnable
  implements Runnable
{
  private final NativeModuleCallExceptionHandler mExceptionHandler;
  
  public GuardedRunnable(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    mExceptionHandler = paramNativeModuleCallExceptionHandler;
  }
  
  public GuardedRunnable(ReactContext paramReactContext)
  {
    this(paramReactContext.getExceptionHandler());
  }
  
  public final void run()
  {
    try
    {
      runGuarded();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      mExceptionHandler.handleException(localRuntimeException);
    }
  }
  
  public abstract void runGuarded();
}
