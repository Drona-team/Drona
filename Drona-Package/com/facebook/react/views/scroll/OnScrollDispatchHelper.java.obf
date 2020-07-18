package com.facebook.react.views.scroll;

import android.os.SystemClock;

public class OnScrollDispatchHelper
{
  private static final int MIN_EVENT_SEPARATION_MS = 10;
  private long mLastScrollEventTimeMs = -11L;
  private int mPrevX = Integer.MIN_VALUE;
  private int mPrevY = Integer.MIN_VALUE;
  private float mXFlingVelocity = 0.0F;
  private float mYFlingVelocity = 0.0F;
  
  public OnScrollDispatchHelper() {}
  
  public float getXFlingVelocity()
  {
    return mXFlingVelocity;
  }
  
  public float getYFlingVelocity()
  {
    return mYFlingVelocity;
  }
  
  public boolean onScrollChanged(int paramInt1, int paramInt2)
  {
    long l = SystemClock.uptimeMillis();
    boolean bool;
    if ((l - mLastScrollEventTimeMs <= 10L) && (mPrevX == paramInt1) && (mPrevY == paramInt2)) {
      bool = false;
    } else {
      bool = true;
    }
    if (l - mLastScrollEventTimeMs != 0L)
    {
      mXFlingVelocity = ((paramInt1 - mPrevX) / (float)(l - mLastScrollEventTimeMs));
      mYFlingVelocity = ((paramInt2 - mPrevY) / (float)(l - mLastScrollEventTimeMs));
    }
    mLastScrollEventTimeMs = l;
    mPrevX = paramInt1;
    mPrevY = paramInt2;
    return bool;
  }
}
