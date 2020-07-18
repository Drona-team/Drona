package com.facebook.react.common;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import java.util.concurrent.TimeUnit;

public class ShakeDetector
  implements SensorEventListener
{
  private static final long MIN_TIME_BETWEEN_SAMPLES_NS = TimeUnit.NANOSECONDS.convert(20L, TimeUnit.MILLISECONDS);
  private static final float REQUIRED_FORCE = 13.042845F;
  private static final float SHAKING_WINDOW_NS = (float)TimeUnit.NANOSECONDS.convert(3L, TimeUnit.SECONDS);
  private float mAccelerationX;
  private float mAccelerationY;
  private float mAccelerationZ;
  private long mLastShakeTimestamp;
  private long mLastTimestamp;
  private int mMinNumShakes;
  private int mNumShakes;
  @Nullable
  private SensorManager mSensorManager;
  private final ShakeListener mShakeListener;
  
  public ShakeDetector(ShakeListener paramShakeListener)
  {
    this(paramShakeListener, 1);
  }
  
  public ShakeDetector(ShakeListener paramShakeListener, int paramInt)
  {
    mShakeListener = paramShakeListener;
    mMinNumShakes = paramInt;
  }
  
  private boolean atLeastRequiredForce(float paramFloat)
  {
    return Math.abs(paramFloat) > 13.042845F;
  }
  
  private void maybeDispatchShake(long paramLong)
  {
    if (mNumShakes >= mMinNumShakes * 8)
    {
      reset();
      mShakeListener.onShake();
    }
    if ((float)(paramLong - mLastShakeTimestamp) > SHAKING_WINDOW_NS) {
      reset();
    }
  }
  
  private void recordShake(long paramLong)
  {
    mLastShakeTimestamp = paramLong;
    mNumShakes += 1;
  }
  
  private void reset()
  {
    mNumShakes = 0;
    mAccelerationX = 0.0F;
    mAccelerationY = 0.0F;
    mAccelerationZ = 0.0F;
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (timestamp - mLastTimestamp < MIN_TIME_BETWEEN_SAMPLES_NS) {
      return;
    }
    float f1 = values[0];
    float f2 = values[1];
    float f3 = values[2] - 9.80665F;
    mLastTimestamp = timestamp;
    if ((atLeastRequiredForce(f1)) && (mAccelerationX * f1 <= 0.0F))
    {
      recordShake(timestamp);
      mAccelerationX = f1;
    }
    else if ((atLeastRequiredForce(f2)) && (mAccelerationY * f2 <= 0.0F))
    {
      recordShake(timestamp);
      mAccelerationY = f2;
    }
    else if ((atLeastRequiredForce(f3)) && (mAccelerationZ * f3 <= 0.0F))
    {
      recordShake(timestamp);
      mAccelerationZ = f3;
    }
    maybeDispatchShake(timestamp);
  }
  
  public void start(SensorManager paramSensorManager)
  {
    Assertions.assertNotNull(paramSensorManager);
    Sensor localSensor = paramSensorManager.getDefaultSensor(1);
    if (localSensor != null)
    {
      mSensorManager = paramSensorManager;
      mLastTimestamp = -1L;
      mSensorManager.registerListener(this, localSensor, 2);
      mLastShakeTimestamp = 0L;
      reset();
    }
  }
  
  public void stop()
  {
    if (mSensorManager != null)
    {
      mSensorManager.unregisterListener(this);
      mSensorManager = null;
    }
  }
  
  public static abstract interface ShakeListener
  {
    public abstract void onShake();
  }
}