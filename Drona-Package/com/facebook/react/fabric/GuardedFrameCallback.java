package com.facebook.react.fabric;

import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.ChoreographerCompat.FrameCallback;

public abstract class GuardedFrameCallback
  extends ChoreographerCompat.FrameCallback
{
  private final NativeModuleCallExceptionHandler mExceptionHandler;
  
  protected GuardedFrameCallback(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    mExceptionHandler = paramNativeModuleCallExceptionHandler;
  }
  
  protected GuardedFrameCallback(ReactContext paramReactContext)
  {
    this(paramReactContext.getExceptionHandler());
  }
  
  public final void doFrame(long paramLong)
  {
    try
    {
      doFrameGuarded(paramLong);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      mExceptionHandler.handleException(localRuntimeException);
    }
  }
  
  protected abstract void doFrameGuarded(long paramLong);
}
