package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableMap;

public class DecayAnimation
  extends AnimationDriver
{
  private int mCurrentLoop;
  private double mDeceleration;
  private double mFromValue;
  private int mIterations;
  private double mLastValue;
  private long mStartFrameTimeMillis;
  private final double mVelocity;
  
  public DecayAnimation(ReadableMap paramReadableMap)
  {
    mVelocity = paramReadableMap.getDouble("velocity");
    resetConfig(paramReadableMap);
  }
  
  public void resetConfig(ReadableMap paramReadableMap)
  {
    mDeceleration = paramReadableMap.getDouble("deceleration");
    boolean bool2 = paramReadableMap.hasKey("iterations");
    boolean bool1 = true;
    int i;
    if (bool2) {
      i = paramReadableMap.getInt("iterations");
    } else {
      i = 1;
    }
    mIterations = i;
    mCurrentLoop = 1;
    if (mIterations != 0) {
      bool1 = false;
    }
    mHasFinished = bool1;
    mStartFrameTimeMillis = -1L;
    mFromValue = 0.0D;
    mLastValue = 0.0D;
  }
  
  public void runAnimationStep(long paramLong)
  {
    paramLong /= 1000000L;
    if (mStartFrameTimeMillis == -1L)
    {
      mStartFrameTimeMillis = (paramLong - 16L);
      if (mFromValue == mLastValue) {
        mFromValue = mAnimatedValue.mValue;
      } else {
        mAnimatedValue.mValue = mFromValue;
      }
      mLastValue = mAnimatedValue.mValue;
    }
    double d = mFromValue + mVelocity / (1.0D - mDeceleration) * (1.0D - Math.exp(-(1.0D - mDeceleration) * (paramLong - mStartFrameTimeMillis)));
    if (Math.abs(mLastValue - d) < 0.1D)
    {
      if ((mIterations != -1) && (mCurrentLoop >= mIterations))
      {
        mHasFinished = true;
        return;
      }
      mStartFrameTimeMillis = -1L;
      mCurrentLoop += 1;
    }
    mLastValue = d;
    mAnimatedValue.mValue = d;
  }
}
