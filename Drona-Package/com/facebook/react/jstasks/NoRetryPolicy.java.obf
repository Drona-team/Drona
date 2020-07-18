package com.facebook.react.jstasks;

public class NoRetryPolicy
  implements HeadlessJsTaskRetryPolicy
{
  public static final NoRetryPolicy INSTANCE = new NoRetryPolicy();
  
  private NoRetryPolicy() {}
  
  public boolean canRetry()
  {
    return false;
  }
  
  public HeadlessJsTaskRetryPolicy copy()
  {
    return this;
  }
  
  public int getDelay()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Should not retrieve delay as canRetry is: ");
    localStringBuilder.append(canRetry());
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public HeadlessJsTaskRetryPolicy update()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Should not update as canRetry is: ");
    localStringBuilder.append(canRetry());
    throw new IllegalStateException(localStringBuilder.toString());
  }
}
