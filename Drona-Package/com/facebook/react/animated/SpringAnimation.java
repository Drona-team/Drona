package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableMap;

class SpringAnimation
  extends AnimationDriver
{
  private static final double MAX_DELTA_TIME_SEC = 0.064D;
  private static final double SOLVER_TIMESTEP_SEC = 0.001D;
  private int mCurrentLoop;
  private final PhysicsState mCurrentState = new PhysicsState(null);
  private double mDisplacementFromRestThreshold;
  private double mEndValue;
  private double mInitialVelocity;
  private int mIterations;
  private long mLastTime;
  private double mOriginalValue;
  private boolean mOvershootClampingEnabled;
  private double mRestSpeedThreshold;
  private double mSpringDamping;
  private double mSpringMass;
  private boolean mSpringStarted;
  private double mSpringStiffness;
  private double mStartValue;
  private double mTimeAccumulator;
  
  SpringAnimation(ReadableMap paramReadableMap)
  {
    mCurrentState.velocity = paramReadableMap.getDouble("initialVelocity");
    resetConfig(paramReadableMap);
  }
  
  private void advance(double paramDouble)
  {
    if (isAtRest()) {
      return;
    }
    double d1 = 0.064D;
    if (paramDouble > 0.064D) {
      paramDouble = d1;
    }
    mTimeAccumulator += paramDouble;
    double d3 = mSpringDamping;
    paramDouble = mSpringMass;
    double d2 = mSpringStiffness;
    d1 = -mInitialVelocity;
    double d6 = d3 / (Math.sqrt(d2 * paramDouble) * 2.0D);
    double d7 = Math.sqrt(d2 / paramDouble);
    double d4 = Math.sqrt(1.0D - d6 * d6) * d7;
    d2 = mEndValue - mStartValue;
    double d5 = mTimeAccumulator;
    if (d6 < 1.0D)
    {
      paramDouble = Math.exp(-d6 * d7 * d5);
      d3 = mEndValue;
      d6 *= d7;
      d1 += d6 * d2;
      d7 = d1 / d4;
      double d9 = d5 * d4;
      d5 = Math.sin(d9);
      double d8 = Math.cos(d9);
      d1 = d6 * paramDouble * (Math.sin(d9) * d1 / d4 + Math.cos(d9) * d2) - (Math.cos(d9) * d1 - d4 * d2 * Math.sin(d9)) * paramDouble;
      paramDouble = d3 - (d7 * d5 + d8 * d2) * paramDouble;
    }
    else
    {
      d3 = Math.exp(-d7 * d5);
      paramDouble = mEndValue - ((d7 * d2 + d1) * d5 + d2) * d3;
      d1 = d3 * (d1 * (d5 * d7 - 1.0D) + d5 * d2 * (d7 * d7));
    }
    mCurrentState.position = paramDouble;
    mCurrentState.velocity = d1;
    if ((isAtRest()) || ((mOvershootClampingEnabled) && (isOvershooting())))
    {
      if (mSpringStiffness > 0.0D)
      {
        mStartValue = mEndValue;
        mCurrentState.position = mEndValue;
      }
      else
      {
        mEndValue = mCurrentState.position;
        mStartValue = mEndValue;
      }
      mCurrentState.velocity = 0.0D;
    }
  }
  
  private double getDisplacementDistanceForState(PhysicsState paramPhysicsState)
  {
    return Math.abs(mEndValue - position);
  }
  
  private boolean isAtRest()
  {
    return (Math.abs(mCurrentState.velocity) <= mRestSpeedThreshold) && ((getDisplacementDistanceForState(mCurrentState) <= mDisplacementFromRestThreshold) || (mSpringStiffness == 0.0D));
  }
  
  private boolean isOvershooting()
  {
    return (mSpringStiffness > 0.0D) && (((mStartValue < mEndValue) && (mCurrentState.position > mEndValue)) || ((mStartValue > mEndValue) && (mCurrentState.position < mEndValue)));
  }
  
  public void resetConfig(ReadableMap paramReadableMap)
  {
    mSpringStiffness = paramReadableMap.getDouble("stiffness");
    mSpringDamping = paramReadableMap.getDouble("damping");
    mSpringMass = paramReadableMap.getDouble("mass");
    mInitialVelocity = mCurrentState.velocity;
    mEndValue = paramReadableMap.getDouble("toValue");
    mRestSpeedThreshold = paramReadableMap.getDouble("restSpeedThreshold");
    mDisplacementFromRestThreshold = paramReadableMap.getDouble("restDisplacementThreshold");
    mOvershootClampingEnabled = paramReadableMap.getBoolean("overshootClamping");
    boolean bool2 = paramReadableMap.hasKey("iterations");
    boolean bool1 = true;
    int i;
    if (bool2) {
      i = paramReadableMap.getInt("iterations");
    } else {
      i = 1;
    }
    mIterations = i;
    if (mIterations != 0) {
      bool1 = false;
    }
    mHasFinished = bool1;
    mCurrentLoop = 0;
    mTimeAccumulator = 0.0D;
    mSpringStarted = false;
  }
  
  public void runAnimationStep(long paramLong)
  {
    paramLong /= 1000000L;
    if (!mSpringStarted)
    {
      if (mCurrentLoop == 0)
      {
        mOriginalValue = mAnimatedValue.mValue;
        mCurrentLoop = 1;
      }
      PhysicsState localPhysicsState = mCurrentState;
      double d = mAnimatedValue.mValue;
      position = d;
      mStartValue = d;
      mLastTime = paramLong;
      mTimeAccumulator = 0.0D;
      mSpringStarted = true;
    }
    advance((paramLong - mLastTime) / 1000.0D);
    mLastTime = paramLong;
    mAnimatedValue.mValue = mCurrentState.position;
    if (isAtRest())
    {
      if ((mIterations != -1) && (mCurrentLoop >= mIterations))
      {
        mHasFinished = true;
        return;
      }
      mSpringStarted = false;
      mAnimatedValue.mValue = mOriginalValue;
      mCurrentLoop += 1;
    }
  }
  
  private static class PhysicsState
  {
    double position;
    double velocity;
    
    private PhysicsState() {}
  }
}