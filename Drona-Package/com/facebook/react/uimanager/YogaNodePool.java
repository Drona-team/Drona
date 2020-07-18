package com.facebook.react.uimanager;

import com.facebook.react.common.ClearableSynchronizedPool;
import com.facebook.yoga.YogaNode;

public class YogaNodePool
{
  private static final Object sInitLock = new Object();
  private static ClearableSynchronizedPool<YogaNode> sPool;
  
  public YogaNodePool() {}
  
  public static ClearableSynchronizedPool getInstance()
  {
    if (sPool != null) {
      return sPool;
    }
    Object localObject = sInitLock;
    try
    {
      if (sPool == null) {
        sPool = new ClearableSynchronizedPool(1024);
      }
      ClearableSynchronizedPool localClearableSynchronizedPool = sPool;
      return localClearableSynchronizedPool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
