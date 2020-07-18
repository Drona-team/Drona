package com.facebook.react.views.scroll;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import androidx.annotation.Nullable;

public class VelocityHelper
{
  @Nullable
  private VelocityTracker mVelocityTracker;
  private float mXVelocity;
  private float mYVelocity;
  
  public VelocityHelper() {}
  
  public void calculateVelocity(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction() & 0xFF;
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(paramMotionEvent);
    if ((i != 1) && (i != 3)) {
      return;
    }
    mVelocityTracker.computeCurrentVelocity(1);
    mXVelocity = mVelocityTracker.getXVelocity();
    mYVelocity = mVelocityTracker.getYVelocity();
    if (mVelocityTracker != null)
    {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }
  
  public float getXVelocity()
  {
    return mXVelocity;
  }
  
  public float getYVelocity()
  {
    return mYVelocity;
  }
}