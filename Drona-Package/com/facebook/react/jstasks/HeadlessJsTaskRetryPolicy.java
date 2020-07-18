package com.facebook.react.jstasks;

public abstract interface HeadlessJsTaskRetryPolicy
{
  public abstract boolean canRetry();
  
  public abstract HeadlessJsTaskRetryPolicy copy();
  
  public abstract int getDelay();
  
  public abstract HeadlessJsTaskRetryPolicy update();
}
