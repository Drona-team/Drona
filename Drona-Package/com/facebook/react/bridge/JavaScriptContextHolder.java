package com.facebook.react.bridge;

import androidx.annotation.GuardedBy;

public class JavaScriptContextHolder
{
  @GuardedBy("this")
  private long mContext;
  
  public JavaScriptContextHolder(long paramLong)
  {
    mContext = paramLong;
  }
  
  public void clear()
  {
    try
    {
      mContext = 0L;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getIdentity()
  {
    return mContext;
  }
}