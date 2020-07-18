package com.facebook.react.jstasks;

public class LinearCountingRetryPolicy
  implements HeadlessJsTaskRetryPolicy
{
  private final int mDelayBetweenAttemptsInMs;
  private final int mRetryAttempts;
  
  public LinearCountingRetryPolicy(int paramInt1, int paramInt2)
  {
    mRetryAttempts = paramInt1;
    mDelayBetweenAttemptsInMs = paramInt2;
  }
  
  public boolean canRetry()
  {
    return mRetryAttempts > 0;
  }
  
  public HeadlessJsTaskRetryPolicy copy()
  {
    return new LinearCountingRetryPolicy(mRetryAttempts, mDelayBetweenAttemptsInMs);
  }
  
  public int getDelay()
  {
    return mDelayBetweenAttemptsInMs;
  }
  
  public HeadlessJsTaskRetryPolicy update()
  {
    int i = mRetryAttempts - 1;
    if (i > 0) {
      return new LinearCountingRetryPolicy(i, mDelayBetweenAttemptsInMs);
    }
    return NoRetryPolicy.INSTANCE;
  }
}
