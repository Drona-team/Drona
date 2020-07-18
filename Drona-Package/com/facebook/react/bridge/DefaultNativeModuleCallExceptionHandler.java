package com.facebook.react.bridge;

public class DefaultNativeModuleCallExceptionHandler
  implements NativeModuleCallExceptionHandler
{
  public DefaultNativeModuleCallExceptionHandler() {}
  
  public void handleException(Exception paramException)
  {
    if ((paramException instanceof RuntimeException)) {
      throw ((RuntimeException)paramException);
    }
    throw new RuntimeException(paramException);
  }
}
