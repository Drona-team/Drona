package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

class FrameBasedAnimationDriver
  extends AnimationDriver
{
  private static final double FRAME_TIME_MILLIS = 16.666666666666668D;
  private int mCurrentLoop;
  private double[] mFrames;
  private double mFromValue;
  private int mIterations;
  private long mStartFrameTimeNanos;
  private double mToValue;
  
  FrameBasedAnimationDriver(ReadableMap paramReadableMap)
  {
    resetConfig(paramReadableMap);
  }
  
  public void resetConfig(ReadableMap paramReadableMap)
  {
    ReadableArray localReadableArray = paramReadableMap.getArray("frames");
    int j = localReadableArray.size();
    if ((mFrames == null) || (mFrames.length != j)) {
      mFrames = new double[j];
    }
    int i = 0;
    while (i < j)
    {
      mFrames[i] = localReadableArray.getDouble(i);
      i += 1;
    }
    boolean bool1 = paramReadableMap.hasKey("toValue");
    double d = 0.0D;
    if (bool1)
    {
      if (paramReadableMap.getType("toValue") == ReadableType.Number) {
        d = paramReadableMap.getDouble("toValue");
      }
      mToValue = d;
    }
    else
    {
      mToValue = 0.0D;
    }
    boolean bool2 = paramReadableMap.hasKey("iterations");
    bool1 = true;
    if (bool2)
    {
      if (paramReadableMap.getType("iterations") == ReadableType.Number) {
        i = paramReadableMap.getInt("iterations");
      } else {
        i = 1;
      }
      mIterations = i;
    }
    else
    {
      mIterations = 1;
    }
    mCurrentLoop = 1;
    if (mIterations != 0) {
      bool1 = false;
    }
    mHasFinished = bool1;
    mStartFrameTimeNanos = -1L;
  }
  
  public void runAnimationStep(long paramLong)
  {
    if (mStartFrameTimeNanos < 0L)
    {
      mStartFrameTimeNanos = paramLong;
      if (mCurrentLoop == 1) {
        mFromValue = mAnimatedValue.mValue;
      }
    }
    int i = (int)Math.round((paramLong - mStartFrameTimeNanos) / 1000000L / 16.666666666666668D);
    if (i >= 0)
    {
      if (mHasFinished) {
        return;
      }
      double d;
      if (i >= mFrames.length - 1)
      {
        d = mToValue;
        if ((mIterations != -1) && (mCurrentLoop >= mIterations))
        {
          mHasFinished = true;
        }
        else
        {
          mStartFrameTimeNanos = -1L;
          mCurrentLoop += 1;
        }
      }
      else
      {
        d = mFromValue;
        d = mFrames[i] * (mToValue - mFromValue) + d;
      }
      mAnimatedValue.mValue = d;
      return;
    }
    throw new IllegalStateException("Calculated frame index should never be lower than 0");
  }
}
