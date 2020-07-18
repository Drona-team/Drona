package com.facebook.react.common;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;

public class SingleThreadAsserter
{
  @Nullable
  private Thread mThread = null;
  
  public SingleThreadAsserter() {}
  
  public void assertNow()
  {
    Thread localThread = Thread.currentThread();
    if (mThread == null) {
      mThread = localThread;
    }
    boolean bool;
    if (mThread == localThread) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool);
  }
}
