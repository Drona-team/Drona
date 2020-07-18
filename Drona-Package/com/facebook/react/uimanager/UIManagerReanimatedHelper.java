package com.facebook.react.uimanager;

public class UIManagerReanimatedHelper
{
  public UIManagerReanimatedHelper() {}
  
  public static boolean isOperationQueueEmpty(UIImplementation paramUIImplementation)
  {
    return paramUIImplementation.getUIViewOperationQueue().isEmpty();
  }
}
